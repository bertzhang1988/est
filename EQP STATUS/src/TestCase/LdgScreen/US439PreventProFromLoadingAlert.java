package TestCase.LdgScreen;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

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

import Data.DataForUS200068AndUS445;
import Data.DataForUS439;
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
		page.SetStatus("LDG");
	}

	@Test(priority = 1, dataProvider = "2000.682", dataProviderClass = DataForUS200068AndUS445.class)
	public void EnterTrailerInLdgNoShipments(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
	}

	@Test(priority = 2, dataProvider = "Invalid pro", dataProviderClass = DataForUS439.class)
	public void VerifyMRValidation(String PRO)
			throws InterruptedException, AWTException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
		page.EnterPro(PRO);
		int NEW = page.AddProForm.findElements(By.xpath("div")).size();
		// change cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		page.SubmitButton.click();
		WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
		(new WebDriverWait(driver, 20))
				.until(ExpectedConditions.textToBePresentInElement(Message, "Cannot add master bill."));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
	}

	@Test(priority = 3, dataProvider = "Invalid pro", dataProviderClass = DataForUS439.class)
	public void VerifySUValidation(String PRO)
			throws InterruptedException, AWTException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
		page.EnterPro(PRO);
		int NEW = page.AddProForm.findElements(By.xpath("div")).size();
		// change cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		page.SubmitButton.click();
		WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
		(new WebDriverWait(driver, 20))
				.until(ExpectedConditions.textToBePresentInElement(Message, "Cannot add supplemental bill."));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
	}

	@Test(priority = 4, dataProvider = "Invalid pro", dataProviderClass = DataForUS439.class)
	public void VerifyVOValidation(String PRO)
			throws InterruptedException, AWTException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
		page.EnterPro(PRO);
		int NEW = page.AddProForm.findElements(By.xpath("div")).size();
		// change cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		page.SubmitButton.click();
		WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
		(new WebDriverWait(driver, 20))
				.until(ExpectedConditions.textToBePresentInElement(Message, "Cannot add voided PRO."));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
	}

	@Test(priority = 5, dataProvider = "Invalid pro", dataProviderClass = DataForUS439.class)
	public void VerifyAlreadyDeliveredValidation(String PRO)
			throws InterruptedException, AWTException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
		page.EnterPro(PRO);
		int NEW = page.AddProForm.findElements(By.xpath("div")).size();
		// change cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		page.SubmitButton.click();
		WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
		(new WebDriverWait(driver, 20))
				.until(ExpectedConditions.textToBePresentInElement(Message, "PRO already delivered."));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
	}

	@Test(priority = 6, dataProvider = "ldgtrailerNoPro", dataProviderClass = DataForUS439.class)
	public void VerifyMasterRevenueVlidation(String terminalcd, String SCAC, String TrailerNB)
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
			// change cube
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
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
			// change cube
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
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
			// change cube
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
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
			// change cube
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
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

	@AfterClass
	public void TearDown() {
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
		page.CheckAllAddProButton.click();
		driver.quit();
	}
}
