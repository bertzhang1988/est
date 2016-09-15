package TestCase.CLtesting;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.openqa.selenium.By;
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

import Data.DataForUS51212;
import Function.CommonFunction;
import Function.ConfigRd;
import Function.DataCommon;
import Page.EqpStatusPageS;

public class US50708AutoIncludeOrigAndRouteInUadRecord {
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

	// @AfterClass
	public void Close() {
		driver.close();
	}

	@Test(priority = 1, dataProvider = "512.12DT", dataProviderClass = DataForUS51212.class)
	public void ToCLNoProThenUAD(String terminalcd, String SCAC, String TrailerNB, String Desti)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);
		// update cityRoute
		String NewCityRoute = page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		Date PlanDate = page.SetPlanDate(Localtime, 2);

		// select city route type
		String SetCityRtype = page.SetCityRouteType("trap");

		// set date&time
		page.SetDatePicker2(page.GetDatePickerTime(), 0, 5);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// click submit
		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Trailer " + page.SCACTrailer(SCAC, TrailerNB) + " updated to CL"));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		// check eqps
		ArrayList<Object> CLNewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(CLNewEqpStatusRecord.get(0), "CL", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(CLNewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(CLNewEqpStatusRecord.get(3), "CL", "Source_Create_ID is wrong");
		SA.assertEquals(CLNewEqpStatusRecord.get(21), SetCityRtype, "City_Route_Type_NM is wrong");
		SA.assertEquals(CLNewEqpStatusRecord.get(22), NewCityRoute, "City_Route_NM is wrong");
		int[] TimeElement = { 5, 6, 7, 8, 20 };
		for (int i : TimeElement) {
			Date TS = CommonFunction.SETtime((Date) CLNewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else if (i == 20) {
				SA.assertEquals(TS, PlanDate, "Planned_Delivery_DT is wrong");
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		// SET TO UAD
		page.SetStatus("uad");
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// alter time
		page.SetDatePicker2(page.GetDatePickerTime(), 0, 10);
		Date AlterTime2 = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		page.SubmitButton.click();
		Date d2 = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> UADNewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(UADNewEqpStatusRecord.get(0), "UAD", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(UADNewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(UADNewEqpStatusRecord.get(3), "UAD", "Source_Create_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) UADNewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime2.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime2);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d2.getTime()) < 120000, i + "  " + TS + "  " + d2);
			}
		}
		if (CLNewEqpStatusRecord.get(2) != null)
			SA.assertEquals(UADNewEqpStatusRecord.get(2), CLNewEqpStatusRecord.get(2),
					"Equipment_Dest_Facility_CD not retain");
		if (CLNewEqpStatusRecord.get(15) != null)
			SA.assertEquals(UADNewEqpStatusRecord.get(15), CLNewEqpStatusRecord.get(15),
					"Equipment_Origin_Facility_CD not retain");
		if (CLNewEqpStatusRecord.get(22) != null)
			SA.assertEquals(UADNewEqpStatusRecord.get(22), CLNewEqpStatusRecord.get(22), "SCity_Route_NM not retain");

		SA.assertAll();

	}

}
