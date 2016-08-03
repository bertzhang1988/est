package TestCase.LoadToEnr;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Data.DataForUS200001AndUS200002AndUS200041;
import Data.DataForUS200004;
import Data.DataForUS439;
import Data.DataForUS458;
import Function.CommonFunction;
import Function.ConfigRd;
import Function.DataCommon;
import Page.EqpStatusPageS;

public class LoadToEnrScreenTesting {
	private WebDriver driver;
	private EqpStatusPageS page;

	@BeforeClass
	@Parameters({ "browser" })
	public void SetUp(@Optional("chrome") String browser) throws AWTException, InterruptedException {
		ConfigRd Conf = new ConfigRd();
		if (browser.equalsIgnoreCase("chrome")) {
			System.setProperty("webdriver.chrome.driver", Conf.GetChromePath());
			driver = new ChromeDriver();
		} else if (browser.equalsIgnoreCase("ie")) {
			System.setProperty("webdriver.ie.driver", Conf.GetIEPath());
			driver = new InternetExplorerDriver();
		} else if (browser.equalsIgnoreCase("hl")) {
			File file = new File(Conf.GetPhantomJSDriverPath());
			System.setProperty("phantomjs.binary.path", file.getAbsolutePath());
			driver = new PhantomJSDriver();
		}
		// driver=new FirefoxDriver();
		page = new EqpStatusPageS(driver);
		driver.get(Conf.GetURL());
		driver.manage().window().maximize();
		page.SetToLoadEnrScreen();
	}

	@AfterClass
	public void Close() {
		driver.close();
	}

	@Test(priority = 8, dataProvider = "2000.41", dataProviderClass = DataForUS200001AndUS200002AndUS200041.class)
	public void VerifyInvalidTerminal(String terminalcd) throws AWTException, InterruptedException {
		page.SetLocation(terminalcd);
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"The terminal number is invalid. Please enter another terminal number."));

	}

	@Test(priority = 7, dataProvider = "2000.41", dataProviderClass = DataForUS200001AndUS200002AndUS200041.class)
	public void VerifyValidTerminal(String terminalcd) throws AWTException, InterruptedException {
		page.SetLocation(terminalcd);
		(new WebDriverWait(driver, 10))
				.until(ExpectedConditions.invisibilityOfElementWithText(By.xpath("html/body/div[1]/div"),
						"The terminal number is invalid. Please enter another terminal number."));
		Thread.sleep(500);
	}

	@Test(priority = 1, dataProvider = "loadToEnrscreen", dataProviderClass = DataForUSLoadToEnrTest.class)
	public void ProDigitCheck(String terminal, String SCAC, String TrailerNb, String destination, String Cube,
			String StatusType, Date Mrst) throws AWTException, InterruptedException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminal);
		page.EnterTrailer(SCAC, TrailerNb);
		List<String> prolist = Arrays.asList(DataForUS200004.prolist);
		for (int i = 0; i < prolist.size(); i++) {
			String pro = prolist.get(i);
			page.EnterPro(pro);
			String Pronumber = pro.trim().toUpperCase();
			String PronumberH = page.addHyphenToPro(Pronumber);
			String message = null;
			int flag = CommonFunction.CheckProPattern(Pronumber);
			if (flag == 1) {
				message = "Invalid Pro Number";
			} else if (flag == 3) {
				message = "Invalid Check Digit";
			} else if (flag == 2) {
				message = "";
			}

			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			if (NEW != 0) {
				// (new WebDriverWait(driver,
				// 5)).until(ExpectedConditions.textToBePresentInElement(page.AddProForm.findElement(By.xpath("div["+NEW+"]/div/div[2]/div")),
				// Pronumber));
				SAssert.assertEquals(page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[2]/div")).getText(),
						PronumberH);
				SAssert.assertEquals(page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div")).getText(),
						message, "   " + Pronumber);
			} // (new WebDriverWait(driver,
				// 5)).until(ExpectedConditions.textToBePresentInElement(page.AddProForm.findElement(By.xpath("div["+NEW+"]/div/div[3]/div")),message));
			if (NEW == 5) {
				page.RemoveProButton.click();
			}
		}
		SAssert.assertAll();
	}

	@Test(priority = 2, dataProvider = "loadToEnrscreen", dataProviderClass = DataForUSLoadToEnrTest.class)
	public void CheckShipmentAlreadyLoadedOnTrailer(String terminal, String SCAC, String TrailerNb, String destination,
			String Cube, String StatusType, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		page.SetLocation(terminal);
		page.EnterTrailer(SCAC, TrailerNb);
		Iterator<String> data = DataForUS458.GetPro(SCAC, TrailerNb).iterator();
		while (data.hasNext()) {
			page.RemoveProButton.click();
			String pro = data.next();
			page.EnterPro(pro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			// page.AddProCheckBoxList.findElement(By.xpath("div["+NEW+"]/div/div/div/div")).click();
			page.SubmitButton.click();
			(new WebDriverWait(driver, 20)).until(ExpectedConditions.textToBePresentInElement(
					page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div")),
					"This PRO is already on trailer."));
			(new WebDriverWait(driver, 20))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			(new WebDriverWait(driver, 20)).until(ExpectedConditions.elementToBeClickable(page.RemoveProButton));
			page.RemoveProButton.click();
		}
	}

	@Test(priority = 3, dataProvider = "loadToEnrscreen", dataProviderClass = DataForUSLoadToEnrTest.class)
	public void VerifyMasterRevenueVlidation(String terminalcd, String SCAC, String TrailerNB, String destination,
			String Cube, String StatusType, Date Mrst)
			throws InterruptedException, AWTException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// ADD MR PRO
		ArrayList<String> MRprolist = DataForUS439.Getpro1("mr", SCAC, TrailerNB);
		for (int i = 0; i < 2; i++) {
			page.RemoveProButton.click();
			String pro = MRprolist.get(i);
			page.EnterPro(pro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			page.SubmitButton.click();
			WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
			try {
				(new WebDriverWait(driver, 20))
						.until(ExpectedConditions.textToBePresentInElement(Message, "Cannot add master bill."));
			} catch (Exception e) {
				System.out.println("mr pro is not working as expectation" + pro);
			}
			(new WebDriverWait(driver, 50))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			page.RemoveProButton.click();
		}

		// ADD SU PRO
		ArrayList<String> SUprolist = DataForUS439.Getpro1("SU", SCAC, TrailerNB);
		for (int i = 0; i < 2; i++) {
			page.RemoveProButton.click();
			String pro = SUprolist.get(i);
			page.EnterPro(pro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			page.SubmitButton.click();
			WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
			try {
				(new WebDriverWait(driver, 20))
						.until(ExpectedConditions.textToBePresentInElement(Message, "Cannot add supplemental bill."));
			} catch (Exception e) {
				System.out.println("su pro is not working as expectation" + pro);
			}
			(new WebDriverWait(driver, 50))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			page.RemoveProButton.click();
		}

		// ADD VO PRO
		ArrayList<String> VOprolist = DataForUS439.Getpro1("VO", SCAC, TrailerNB);
		for (int i = 0; i < 2; i++) {
			page.RemoveProButton.click();
			String pro = VOprolist.get(i);
			page.EnterPro(pro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			page.SubmitButton.click();
			WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
			try {
				(new WebDriverWait(driver, 20))
						.until(ExpectedConditions.textToBePresentInElement(Message, "Cannot add voided PRO."));
			} catch (Exception e) {
				System.out.println("vo pro is not working as expectation" + pro);
			}
			(new WebDriverWait(driver, 50))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			page.RemoveProButton.click();
		}

		// ADD PRO already delivered.
		ArrayList<String> Dprolist = DataForUS439.Getpro1("", SCAC, TrailerNB);
		for (int i = 0; i < 2; i++) {
			page.RemoveProButton.click();
			String pro = Dprolist.get(i);
			page.EnterPro(pro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			page.SubmitButton.click();
			WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
			try {
				(new WebDriverWait(driver, 20))
						.until(ExpectedConditions.textToBePresentInElement(Message, "PRO already delivered."));
			} catch (Exception e) {
				System.out.println("already delivered pro is not working as expectation" + pro);
			}
			(new WebDriverWait(driver, 50))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			page.RemoveProButton.click();
		}
	}

	@Test(priority = 4, dataProvider = "loadToEnrscreen2", dataProviderClass = DataForUSLoadToEnrTest.class)
	public void VerifyBatchProForFoodTrailer(String terminalcd, String SCAC, String TrailerNB, String destination,
			String Cube, String StatusType, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<String> Addpro = new ArrayList<String>();
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "pois");
		page.RemoveProButton.click();
		for (int i = 0; i < 2; i++) {
			String pro = PRO.get(i);
			page.EnterPro(pro);
			Addpro.add(pro);
		}
		page.SubmitButton.click();
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"There are no valid Pro(s) in the list."));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

		for (int pro = 1; pro <= Addpro.size(); pro++) {
			String CurrentPro = Addpro.get(pro - 1);
			String CurrentProH = page.addHyphenToPro(CurrentPro);
			SAssert.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[2]/div")).getText(),
					CurrentProH); // check pro
			SAssert.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[3]/div")).getText(),
					"Bill is POISON. Food already on trailer."); // check error
																	// message
			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SAssert.assertFalse(AfterLOADWb.get(0).equals(SCAC) && AfterLOADWb.get(1).equals(TrailerNB),
					"Waybill table Standard_Carrier_Alpha_CD and Equipment_Unit_NBis wrong  " + CurrentPro);
		}

		SAssert.assertAll();
	}

	@Test(priority = 5, dataProvider = "loadToEnrscreen2", dataProviderClass = DataForUSLoadToEnrTest.class)
	public void VerifyBatchProForPoisonTrailer(String terminalcd, String SCAC, String TrailerNB, String destination,
			String Cube, String StatusType, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<String> Addpro = new ArrayList<String>();
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "food");
		page.RemoveProButton.click();
		for (int i = 0; i < 2; i++) {
			String pro = PRO.get(i);
			page.EnterPro(pro);
			Addpro.add(pro);
		}
		page.SubmitButton.click();
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"There are no valid Pro(s) in the list."));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		for (int pro = 1; pro <= Addpro.size(); pro++) {
			String CurrentPro = Addpro.get(pro - 1);
			String CurrentProH = page.addHyphenToPro(CurrentPro);
			SAssert.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[2]/div")).getText(),
					CurrentProH); // check pro
			SAssert.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[3]/div")).getText(),
					"Bill is FOOD. Poison already on trailer."); // check error
																	// message
			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SAssert.assertFalse(AfterLOADWb.get(0).equals(SCAC) && AfterLOADWb.get(1).equals(TrailerNB),
					"Waybill table Standard_Carrier_Alpha_CD and Equipment_Unit_NBis wrong  " + CurrentPro);
		}
		SAssert.assertAll();
	}

	@Test(priority = 6, dataProvider = "loadToEnrscreen", dataProviderClass = DataForUSLoadToEnrTest.class)
	public void AddMultipleProsInSingleBatch(String terminalcd, String SCAC, String TrailerNB, String destination,
			String Cube, String StatusType, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		// check date time
		Date LocalTime = CommonFunction.getLocalTime(terminalcd, Mrst);
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
		cal.setTime(LocalTime);
		int hourOfDay = cal.get(Calendar.HOUR_OF_DAY); // 24 hour clock
		String hour = String.format("%02d", hourOfDay);
		int minute = cal.get(Calendar.MINUTE);
		String Minute = String.format("%02d", minute);
		String MinutePlusOne = String.format("%02d", minute + 1);
		String DATE = dateFormat.format(cal.getTime());

		SA.assertEquals(page.DateInput.getAttribute("value"), DATE, "TIME DARE IS WRONG");
		SA.assertEquals(page.HourInput.getAttribute("value"), hour, "TIME HOR IS WRONG");
		SA.assertEquals(page.MinuteInput.getAttribute("value"), Minute, "TIME MINUTE IS WRONG");
		// add pro
		ArrayList<String> PRO = DataCommon.GetProNotInAnyTrailer();
		page.RemoveProButton.click();
		ArrayList<String> Addpro = new ArrayList<String>();
		for (int i = 0; i < 80; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			Addpro.add(CurrentPro);
		}

		page.SubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField, "pro(s) loaded"));
		Date d2 = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		System.out.println((d2.getTime() - d.getTime()) / 1000);
		page.EnterTrailer(SCAC, TrailerNB);
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LENRProListForm);
		SA.assertEquals(ProInfo, DataCommon.GetProList(SCAC, TrailerNB), " pro grid is wrong");
		// check right grid is as same as left grid
		ArrayList<String> AddproBatch = new ArrayList<String>();
		Iterator<ArrayList<String>> pr = ProInfo.iterator();
		while (pr.hasNext()) {
			ArrayList<String> pro = pr.next();
			AddproBatch.add(pro.get(0).replaceAll("-", ""));
		}
		SA.assertEquals(AddproBatch, Addpro, " pro sequence is wrong");

		SA.assertAll();
	}

}
