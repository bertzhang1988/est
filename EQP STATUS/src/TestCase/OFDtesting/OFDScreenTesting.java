package TestCase.OFDtesting;

import java.awt.AWTException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Function.CommonFunction;
import Function.DataCommon;
import Function.Setup;
import Function.Utility;
import Page.EqpStatusPageS;

public class OFDScreenTesting extends Setup {

	private EqpStatusPageS page;
	private Actions builder;
	private WebDriverWait w1;
	private WebDriverWait w2;

	@BeforeClass
	public void SetUp() throws AWTException, InterruptedException {

		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 50);
		w2 = new WebDriverWait(driver, 80);
		  
		driver.manage().window().maximize();
		page.SetStatus("OFD");
		builder = new Actions(driver);

	}

	@Test(priority = 1, dataProvider = "OFDScreen", dataProviderClass = DataForOFDScreenTesting.class)
	public void SmartEnterSetTrailerNoProToOFD(String terminalcd, String SCAC, String TrailerNB, String CurrentStatus,
			String CRName, String CRType, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		// check time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MRSts);
		SA.assertEquals(picker, expect, "OFD Screen prepopulate time is wrong ");

		// Check Plan Day
		Date expectPlanDay = CommonFunction.getPrepopulatePlanDay(terminalcd, CurrentTime, PlanD);
		SA.assertEquals(page.GetPlanDatePickerTime(), expectPlanDay, "Plan date prepopulate time is wrong ");

		// Check other fields prepopulate
		SA.assertEquals(page.CityRoute.getAttribute("value").replaceAll("_", ""), CRName,
				"City Route prepopulate is wrong ");
		SA.assertEquals(page.CityRouteTypeField.getText(), CRType, "City Route Type prepopulate is wrong ");

		// update cityRoute
		String NewCityRoute = page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		Date PlanDate = page.SetPlanDate(Localtime, 2);

		// select city route type
		String SetCityRtype = page.SetCityRouteType("trap");

		// alter time
		page.SetDatePicker(Localtime, 0, 10);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// press enter key
		builder.sendKeys(page.DateInput, Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));

		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "OFD", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "OFD", "Source_Create_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(21), SetCityRtype, "City_Route_Type_NM is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(22), NewCityRoute, "City_Route_NM is wrong");

		int[] TimeElement = { 5, 6, 7, 8, 20 };
		for (int i : TimeElement) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else if (i == 20) {
				SA.assertEquals(TS, PlanDate, "Planned_Delivery_DT is wrong");
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}
		SA.assertAll();
	}

	@Test(priority = 2, dataProvider = "OFDScreen", dataProviderClass = DataForOFDScreenTesting.class, description = "uc1 505.03")
	public void SmartEnterSetTrailerWithProToOFD(String terminalcd, String SCAC, String TrailerNB, String CurrentStatus,
			String CRName, String CRType, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		// check time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MRSts);
		SA.assertEquals(picker, expect, "OFD Screen prepopulate time is wrong ");

		// Check Plan Day
		Date expectPlanDay = CommonFunction.getPrepopulatePlanDay(terminalcd, CurrentTime, PlanD);
		SA.assertEquals(page.GetPlanDatePickerTime(), expectPlanDay, "Plan date prepopulate time is wrong ");

		// Check other fields prepopulate
		SA.assertEquals(page.CityRoute.getAttribute("value").replaceAll("_", ""), CRName,
				"City Route prepopulate is wrong ");
		SA.assertEquals(page.CityRouteTypeField.getText(), CRType, "City Route Type prepopulate is wrong ");

		// update cityRoute
		String NewCityRoute = page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		Date PlanDate = page.SetPlanDate(Localtime, 2);

		// select city route type
		String SetCityRtype = page.SetCityRouteType("trap");

		// alter time
		page.SetDatePicker(Localtime, 0, 5);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// press enter key
		builder.sendKeys(page.TrailerField, Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));

		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "OFD", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "OFD", "Source_Create_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(21), SetCityRtype, "City_Route_Type_NM is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(22), NewCityRoute, "City_Route_NM is wrong");

		int[] TimeElement = { 5, 6, 7, 8, 20 };
		for (int i : TimeElement) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else if (i == 20) {
				SA.assertEquals(TS, PlanDate, "Planned_Delivery_DT is wrong");
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}
		SA.assertAll();
	}

	@Test(priority = 3, dataProvider = "OFDScreen2", dataProviderClass = DataForOFDScreenTesting.class, description = "in cltg with pro")
	public void SmartEnterSetCLTGTrailerNoProToOFD(String terminalcd, String SCAC, String TrailerNB,
			String CurrentStatus, String CRName, String CRType, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		// check time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MRSts);
		SA.assertEquals(picker, expect, "ofd screen prepopulate time is wrong ");

		// Check Plan Day
		Date expectPlanDay = CommonFunction.getPrepopulatePlanDay(terminalcd, CurrentTime, PlanD);
		SA.assertEquals(page.GetPlanDatePickerTime(), expectPlanDay, "Plan date prepopulate time is wrong ");

		// Check other fields prepopulate
		SA.assertEquals(page.CityRoute.getAttribute("value").replaceAll("_", ""), CRName,
				"City Route prepopulate is wrong ");
		SA.assertEquals(page.CityRouteTypeField.getText(), CRType, "City Route Type prepopulate is wrong ");

		// check disabled
		if (CurrentStatus.equalsIgnoreCase("cltg")) {
			SA.assertEquals(page.CityRouteTypeField.getAttribute("disabled"), "true",
					"cltg trailer  city route type field is not disabled");
			SA.assertFalse(page.CityRoute.isEnabled(), "cltg trailer  city route field is not disabled");
			SA.assertFalse(page.PlanDate.isEnabled(), "cltg trailer plan date field is not disabled");
		}

		int TimeGap = (int) ((CurrentTime.getTime() - MRSts.getTime()) / (1000 * 60 * 60));
		if (TimeGap > 24) {
			page.SetDatePicker(CurrentTime, -23, -59);
		} else if (TimeGap < 24 && TimeGap > 0) {
			page.SetDatePicker(MRSts, 0, 1);
		} else if (TimeGap > 0) {
			page.SetDatePicker(CurrentTime, 0, 59);
		}

		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		builder.sendKeys(page.DateInput, Keys.ENTER).build().perform();

		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "OFD", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "OFD", "Source_Create_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}
		SA.assertAll();
	}

	@Test(priority = 4, dataProvider = "OFDScreen2", dataProviderClass = DataForOFDScreenTesting.class)
	public void SmartEnterSetCLTGTrailerWithProToOFD(String terminalcd, String SCAC, String TrailerNB,
			String CurrentStatus, String CRName, String CRType, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		// check time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MRSts);
		SA.assertEquals(picker, expect, "ofd screen prepopulate time is wrong ");

		// Check Plan Day
		Date expectPlanDay = CommonFunction.getPrepopulatePlanDay(terminalcd, CurrentTime, PlanD);
		SA.assertEquals(page.GetPlanDatePickerTime(), expectPlanDay, "Plan date prepopulate time is wrong ");

		// Check other fields prepopulate
		SA.assertEquals(page.CityRoute.getAttribute("value").replaceAll("_", ""), CRName,
				"City Route prepopulate is wrong ");
		SA.assertEquals(page.CityRouteTypeField.getText(), CRType, "City Route Type prepopulate is wrong ");

		// check disabled
		if (CurrentStatus.equalsIgnoreCase("cltg")) {
			SA.assertEquals(page.CityRouteTypeField.getAttribute("disabled"), "true",
					"cltg trailer city route type field is not disabled");
			SA.assertFalse(page.CityRoute.isEnabled(), "cltg trailer  city route field is not disabled");
			SA.assertFalse(page.PlanDate.isEnabled(), "cltg trailer plan date field is not disabled");
		}

		int TimeGap = (int) ((CurrentTime.getTime() - MRSts.getTime()) / (1000 * 60 * 60));
		if (TimeGap > 24) {
			page.SetDatePicker(CurrentTime, -23, -59);
		} else if (TimeGap < 24 && TimeGap > 0) {
			page.SetDatePicker(MRSts, 0, 1);
		} else if (TimeGap > 0) {
			page.SetDatePicker(CurrentTime, 0, 59);
		}

		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		builder.sendKeys(page.DateInput, Keys.ENTER).build().perform();

		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "OFD", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "OFD", "Source_Create_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}
		SA.assertAll();
	}

	@AfterMethod
	public void getbackldg(ITestResult result) throws InterruptedException {
		if (result.getStatus() == ITestResult.FAILURE) {

			String Testparameter = Arrays.toString(Arrays.copyOf(result.getParameters(), 3)).replaceAll("[^\\d.a-zA-Z]",
					"");
			String FailureTestparameter = result.getName() + Testparameter;

			Utility.takescreenshot(driver, FailureTestparameter);
			page.SetStatus("OFD");
		}
	}
}
