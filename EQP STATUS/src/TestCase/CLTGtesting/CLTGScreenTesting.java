package TestCase.CLTGtesting;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Function.CommonFunction;
import Function.ConfigRd;
import Function.DataCommon;
import Page.EqpStatusPageS;

public class CLTGScreenTesting {

	private WebDriver driver;
	private EqpStatusPageS page;

	@BeforeTest
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
		page.SetStatus("cltg");
	}

	@Test(priority = 1, dataProvider = "cltg screen 1", dataProviderClass = DataForCLTGScreenTesting.class)
	public void ToCLTGWithProSetToCLTG(String terminalcd, String SCAC, String TrailerNB, String CityR, String CityRT,
			String AmountPro, String AmountWeight, String flag, String serv, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("utc");
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<Object> OldEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		// check date time prepopulate
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MRSts);
		Date pick = page.GetDatePickerTime();
		SA.assertEquals(pick, expect, "cltg screen date and time prepopulate is wrong");

		// Check Plan Day and other fields prepopulate
		SA.assertEquals(page.GetPlanDatePickerTime(), PlanD, "Plan date prepopulate time is wrong ");
		SA.assertEquals(page.CityRoute.getAttribute("value").replaceAll("_", ""), CityR,
				"City Route prepopulate is wrong ");
		SA.assertEquals(page.CityRouteTypeField.getText(), CityRT, "City Route Type prepopulate is wrong ");
		SA.assertEquals(page.ShipmentCount2.getAttribute("value"), AmountPro, "Ship Count prepopulate is wrong ");
		SA.assertEquals(page.ShipmentWeight2.getAttribute("value").replaceAll("_", ""), AmountWeight,
				"Ship Weight prepopulate time is wrong ");
		SA.assertEquals(page.ShipmentFlag.getText(), flag, "shipments flag is wrong ");
		SA.assertEquals(page.ServiceFlag.getText(), serv, "serv is wrong");

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListSecondForm);
		SA.assertEquals(DataCommon.GetProListCLTG(SCAC, TrailerNB), ProInfo, "cltg screen pro grid is wrong");

		// set date&time
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// click submit
		page.SubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Trailer " + page.SCACTrailer(SCAC, TrailerNB) + " updated to CLTG"));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "CLTG", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "CLTG", "Source_Create_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(21), "PEDDLE", "City_Route_Type_NM is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(22), OldEqpStatusRecord.get(22), "City_Route_NM is wrong");
		int[] TimeElement = { 5, 6, 7, 8, 20 };
		for (int i : TimeElement) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else if (i == 20) {
				SA.assertEquals(TS, PlanD, "Planned_Delivery_DT is wrong");
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}
		SA.assertAll();
	}

	@AfterTest
	public void TearDown() {
		driver.quit();
	}
}
