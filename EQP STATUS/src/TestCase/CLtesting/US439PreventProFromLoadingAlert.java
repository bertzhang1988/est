package TestCase.CLtesting;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

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

import Data.DataForUS439;
import Function.CommonFunction;
import Function.ConfigRd;
import Page.EqpStatusPageS;

public class US439PreventProFromLoadingAlert {

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
		page = new EqpStatusPageS(driver);
		driver.get(Conf.GetURL());
		driver.manage().window().maximize();
		page.SetStatus("CL");
	}

	@Test(priority = 50, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class)
	public void EnterTrailerInCLWithoutPro(String terminalcd, String SCAC, String TrailerNB, String CityR,
			String CityRT, String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);
		// update cityRoute
		page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		page.SetPlanDate(Localtime, 2);

		// select city route type
		page.SetCityRouteType("trap");

		// set date&time
		page.SetDatePicker(page.GetDatePickerTime(), -1);

	}

	@Test(priority = 60, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class, groups = {
			"Canada" })
	public void EnterTrailerInCLStatusNoProAtCan(String terminalcd, String SCAC, String TrailerNB, String CityR,
			String CityRT, String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);
		// update cityRoute
		page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		page.SetPlanDate(Localtime, 2);

		// select city route type
		page.SetCityRouteType("trap");

		// set date&time
		page.SetDatePicker(page.GetDatePickerTime(), -1);

	}

	@Test(priority = 2, dataProvider = "Invalid pro", dataProviderClass = DataForUS439.class, dependsOnMethods = {
			"EnterTrailerInCLWithoutPro" })
	public void VerifyMRValidation(String PRO)
			throws InterruptedException, AWTException, ClassNotFoundException, SQLException {

		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
		page.EnterPro(PRO);
		int NEW = page.AddProForm.findElements(By.xpath("div")).size();

		page.SubmitButton.click();
		WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
		(new WebDriverWait(driver, 20))
				.until(ExpectedConditions.textToBePresentInElement(Message, "Cannot add master bill."));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
	}

	@Test(priority = 3, dataProvider = "Invalid pro", dataProviderClass = DataForUS439.class, dependsOnMethods = "EnterTrailerInCLWithoutPro")
	public void VerifySUValidation(String PRO)
			throws InterruptedException, AWTException, ClassNotFoundException, SQLException {
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
		page.EnterPro(PRO);
		int NEW = page.AddProForm.findElements(By.xpath("div")).size();

		page.SubmitButton.click();
		WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
		(new WebDriverWait(driver, 20))
				.until(ExpectedConditions.textToBePresentInElement(Message, "Cannot add supplemental bill."));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
	}

	@Test(priority = 4, dataProvider = "Invalid pro", dataProviderClass = DataForUS439.class, dependsOnMethods = {
			"EnterTrailerInCLWithoutPro" })
	public void VerifyVOValidation(String PRO)
			throws InterruptedException, AWTException, ClassNotFoundException, SQLException {
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
		page.EnterPro(PRO);
		int NEW = page.AddProForm.findElements(By.xpath("div")).size();

		page.SubmitButton.click();
		WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
		(new WebDriverWait(driver, 20))
				.until(ExpectedConditions.textToBePresentInElement(Message, "Cannot add voided PRO."));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
	}

	@Test(priority = 5, dataProvider = "Invalid pro", dataProviderClass = DataForUS439.class, dependsOnMethods = {
			"EnterTrailerInCLWithoutPro" })
	public void VerifyAlreadyDeliveredValidation(String PRO)
			throws InterruptedException, AWTException, ClassNotFoundException, SQLException {
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
		page.EnterPro(PRO);
		int NEW = page.AddProForm.findElements(By.xpath("div")).size();

		page.SubmitButton.click();
		WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
		(new WebDriverWait(driver, 20))
				.until(ExpectedConditions.textToBePresentInElement(Message, "PRO already delivered."));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
	}

	@Test(priority = 7, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class)
	public void VerifyMasterRevenueValidationCLWithoutPro(String terminalcd, String SCAC, String TrailerNB,
			String CityR, String CityRT, String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws InterruptedException, AWTException, ClassNotFoundException, SQLException {
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);
		// update cityRoute
		page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		page.SetPlanDate(Localtime, 2);

		// select city route type
		page.SetCityRouteType("trap");

		// set date&time
		page.SetDatePicker(page.GetDatePickerTime(), -1);
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
			page.CheckAllAddProButton.click();
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
			page.CheckAllAddProButton.click();
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
			page.CheckAllAddProButton.click();
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
			page.CheckAllAddProButton.click();
			page.RemoveProButton.click();
		}
	}

	@Test(priority = 6, dataProvider = "Inbond pro", dataProviderClass = DataForUS439.class, dependsOnMethods = {
			"EnterTrailerInCLWithoutPro" })
	public void VerifyInbondProToUS(String PRO)
			throws InterruptedException, AWTException, ClassNotFoundException, SQLException {
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
		page.EnterPro(PRO);
		int NEW = page.AddProForm.findElements(By.xpath("div")).size();

		page.SubmitButton.click();
		WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
		(new WebDriverWait(driver, 20))
				.until(ExpectedConditions.textToBePresentInElement(Message, "Shipment is INBOND, can not be loaded."));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
	}

	@Test(priority = 8, dataProvider = "Inbond pro", dataProviderClass = DataForUS439.class, dependsOnMethods = "EnterTrailerInCLStatusNoProAtCan", groups = {
			"Canada" })
	public void VerifyInbondProToCAN(String PRO)
			throws InterruptedException, AWTException, ClassNotFoundException, SQLException {
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
		page.EnterPro(PRO);
		int NEW = page.AddProForm.findElements(By.xpath("div")).size();

		page.SubmitButton.click();
		WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
		(new WebDriverWait(driver, 20))
				.until(ExpectedConditions.textToBePresentInElement(Message, "Shipment is INBOND, can not be loaded."));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
	}

	@AfterClass
	public void TearDown() {
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
		page.CheckAllAddProButton.click();
		driver.quit();
	}
}
