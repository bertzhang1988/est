package TestCase.CLtesting;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Function.CommonFunction;
import Function.ConfigRd;
import Function.DataCommon;
import Function.Utility;
import Page.EqpStatusPageS;

public class US627CantLeaveOn3BlobrCLWhenPorsInbond {

	private WebDriver driver;
	private EqpStatusPageS page;
	private ConfigRd Conf;

	@BeforeClass()
	@Parameters({ "browser" })
	public void SetUp(@Optional("chrome") String browser) throws AWTException, InterruptedException, IOException {
		Conf = new ConfigRd();
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

	@Test(priority = 1, dataProvider = "ClScreen2", dataProviderClass = DataForCLScreenTesting.class, description = "to CL us trailer with inbond pro, lobr, leave on")
	public void ToClWithInbondProToUSLeaveOn(String terminalcd, String SCAC, String TrailerNB, String CountryCode)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// update cityRoute
		String NewCityRoute = page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		Date PlanDate = page.SetPlanDate(Localtime, 2);

		// select city route type
		String SetCityRtype = page.SetCityRouteType("trap");

		// set date&time
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// get inbond pro
		ArrayList<String> InbondPro = DataCommon.GetInbondProOnTrailer(SCAC, TrailerNB, CountryCode);

		// pro list before disposition
		LinkedHashSet<ArrayList<String>> ProInfo1 = DataCommon.GetProListLOBR(SCAC, TrailerNB);

		// handle the left pro
		page.HandleLOBRproAll("LeaveOn");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				" is INBOND and cannot be delivered, must DOCK PRO."));
		for (String inbondPRO : InbondPro) {
			SAssert.assertTrue(page.ErrorAndWarningField.getText()
					.contains(inbondPRO + " is INBOND and cannot be delivered, must DOCK PRO."),"the error message is not showing for pro:" +inbondPRO);

		}
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));

		// check pro grid prepopulate
		SAssert.assertEquals(DataCommon.GetProListLOBR(SCAC, TrailerNB), ProInfo1, "pro OF THE TRAILER CHANGED");

		SAssert.assertAll();
	}

	@Test(priority = 2, dataProvider = "ClScreen2", dataProviderClass = DataForCLScreenTesting.class, description = " to CL cannada trailer with inbond pro, lobr, leave on")
	public void ToClWithInbondProToCANLeaveOn(String terminalcd, String SCAC, String TrailerNB, String CountryCode)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// update cityRoute
		String NewCityRoute = page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		Date PlanDate = page.SetPlanDate(Localtime, 2);

		// select city route type
		String SetCityRtype = page.SetCityRouteType("INTERLINE");

		// set date&time
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// get inbond pro
		ArrayList<String> InbondPro = DataCommon.GetInbondProOnTrailer(SCAC, TrailerNB, CountryCode);

		// pro list before disposition
		LinkedHashSet<ArrayList<String>> ProInfo1 = DataCommon.GetProListLOBR(SCAC, TrailerNB);

		// handle the left pro
		page.HandleLOBRproAll("LeaveOn");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				" is INBOND and cannot be delivered, must DOCK PRO."));
		for (String inbondPRO : InbondPro) {
			SAssert.assertTrue(page.ErrorAndWarningField.getText()
					.contains(inbondPRO + " is INBOND and cannot be delivered, must DOCK PRO."),"the error message is not showing for pro:" +inbondPRO);

		}
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));

		// check pro grid prepopulate
		SAssert.assertEquals(DataCommon.GetProListLOBR(SCAC, TrailerNB), ProInfo1, "pro OF THE TRAILER CHANGED");

		SAssert.assertAll();
	}

	@AfterMethod
	public void getbackCL(ITestResult result) throws InterruptedException {
		if (result.getStatus() == ITestResult.FAILURE) {

			String Testparameter = Arrays.toString(Arrays.copyOf(result.getParameters(), 3)).replaceAll("[^\\d.a-zA-Z]",
					"");
			String FailureTestparameter = result.getName() + Testparameter;

			Utility.takescreenshot(driver, FailureTestparameter);
			driver.navigate().refresh();
			page.SetStatus("cl");
		}
	}

	@AfterClass
	public void TearDown() {
		driver.quit();
	}

}
