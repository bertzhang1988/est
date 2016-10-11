package TestCase.ReusableFunctionTest;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.openqa.selenium.WebDriver;
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

import Function.CommonFunction;
import Function.ConfigRd;
import Page.EqpStatusPageS;

public class US200051PreventStatusTimeChangesOutsidePolicy {
	private WebDriver driver;
	private EqpStatusPageS page;
	public static String SetToStatus;

	@BeforeClass
	@Parameters({ "browser", "status" })
	public void SetUp(@Optional("chrome") String browser, @Optional("ldg") String status)
			throws AWTException, InterruptedException {
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
		// driver.manage().window().maximize();
		SetToStatus = status;
		page.SetStatus(SetToStatus);

	}

	@Test(priority = 1, dataProvider = "2000.51", dataProviderClass = DataForReusableFunction.class)
	public void VerifyInvalidStatusStatusTimeChanges(String terminalcd, String SCAC, String TrailerNB,
			String CurrentStatus, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();

		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// set date&time
		page.SetDatePicker(page.GetDatePickerTime(), -100);

		// Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd,
		// page.GetDatePickerTime());
		Date CurrentTime = CommonFunction.gettime("UTC");
		ArrayList<Object> TimeRange = CommonFunction.GetValidStatusTimeRange(CurrentTime, Mrst, SetToStatus,
				terminalcd);

		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(page.ErrorAndWarningField));

		SA.assertEquals(page.ErrorAndWarningField.getText(),
				"Status time must be between " + TimeRange.get(3) + " and " + TimeRange.get(2));
		SA.assertAll();
	}

	@AfterClass
	public void TearDown() {
		driver.close();
	}

}
