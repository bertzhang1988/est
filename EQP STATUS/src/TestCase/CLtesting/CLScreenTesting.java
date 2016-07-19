package TestCase.CLtesting;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Function.CommonFunction;
import Function.ConfigRd;
import Function.DataCommon;
import Page.EqpStatusPageS;

public class CLScreenTesting {

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
		page.SetStatus("cl");
	}

	// @Test(priority = 1, dataProvider = "cl with pro", dataProviderClass =
	// DataForCLScreenTesting.class, description = "not in cl with pro set to
	// cl,leave on")
	public void ThreeBlOBRLeaveOn(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		(new WebDriverWait(driver, 10)).until(
				ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review - 3 Button"));

	}

	@Test(priority = 1, dataProvider = "cl with pro", dataProviderClass = DataForCLScreenTesting.class, description = "not in cl no pro set to cl")
	public void ToCLNoPro(String terminalcd, String SCAC, String TrailerNB, String Desti, String Cube, String AmountPro,
			String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);
		// Check date and time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MRSts);
		SA.assertEquals(picker, expect, "CL screen prepopulate time is wrong ");
		// enter plan date

		// (new WebDriverWait(driver,
		// 10)).until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen,
		// "Leftover Bill Review - 3 Button"));

	}

	// @Test(priority = 1, dataProvider = "cl with pro", dataProviderClass =
	// DataForCLScreenTesting.class)
	public void CLScreen(String terminalcd, String SCAC, String TrailerNB, String Desti, String Cube, String AmountPro,
			String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// check date&time field should be equipment_status_ts at statusing
		// location time zone
		Date LocalTime = CommonFunction.getLocalTime(terminalcd, MRSts);
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
		cal.setTime(LocalTime);
		int hourOfDay = cal.get(Calendar.HOUR_OF_DAY); // 24 hour clock
		String hour = String.format("%02d", hourOfDay);
		int minute = cal.get(Calendar.MINUTE);
		String Minute = String.format("%02d", minute);
		String DATE = dateFormat.format(cal.getTime());
		SA.assertEquals(page.DateInput.getAttribute("value"), DATE);
		SA.assertEquals(page.HourInput.getAttribute("value"), hour);
		SA.assertEquals(page.MinuteInput.getAttribute("value"), Minute);

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListForm);
		SA.assertEquals(ProInfo, DataCommon.GetProList(SCAC, TrailerNB), "pro grid is wrong");

		SA.assertAll();
	}

	// @Test(priority = 2, dataProvider = "cl with pro", dataProviderClass =
	// DataForCLScreenTesting.class)
	public void threeBlOBR(String terminalcd, String SCAC, String TrailerNB, String Desti, String Cube,
			String AmountPro, String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		(new WebDriverWait(driver, 10)).until(
				ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review - 3 Button"));
		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SA.assertEquals(ProInfo, DataCommon.GetProListLOBR(SCAC, TrailerNB), " LOBR pro grid is wrong");

		page.LobrCancelButton.click();
		(new WebDriverWait(driver, 10)).until(
				ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status City Loading"));
		SA.assertAll();
	}

	// @AfterClass
	public void TearDown() {
		driver.quit();
	}
}
