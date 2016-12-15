package TestCase.CLTGtesting;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Function.CommonFunction;
import Function.ConfigRd;
import Function.DataCommon;
import Function.Utility;
import Page.EqpStatusPageS;
import TestCase.CLtesting.DataForCLScreenTesting;

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

	@Test(priority = 1, dataProvider = "cltg screen 1", dataProviderClass = DataForCLTGScreenTesting.class, description = "cltg trailer with pro, no change in cltg screnn")
	public void CLTGWithProSetToCLTG(String terminalcd, String SCAC, String TrailerNB, String CityR, String CityRT,
			String AmountPro, String AmountWeight, String flag, String serv, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("utc");
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<Object> OldEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		// check date time prepopulate
		Date expect = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, MRSts);
		Date pick = page.GetDatePickerTime();
		SA.assertEquals(pick, expect, "cltg screen date and time prepopulate is wrong");

		// Check Plan Day
		Date expectPlanDay = CommonFunction.getPrepopulatePlanDay(terminalcd, CurrentTime, PlanD);
		SA.assertEquals(page.GetPlanDatePickerTime(), expectPlanDay, "Plan date prepopulate time is wrong ");

		// Check other fields prepopulate
		SA.assertEquals(page.CityRoute.getAttribute("value").replaceAll("_", ""), CityR,
				"City Route prepopulate is wrong ");
		SA.assertEquals(page.CityRouteTypeField.getText(), CityRT, "City Route Type prepopulate is wrong ");
		SA.assertEquals(page.ShipmentCount2.getAttribute("value"), AmountPro, "Ship Count prepopulate is wrong ");
		SA.assertEquals(page.ShipmentWeight2.getAttribute("value").replaceAll("_", ""), AmountWeight,
				"Ship Weight prepopulate time is wrong ");
		SA.assertEquals(page.ShipmentFlag.getText(), flag, "shipments flag is wrong ");
		SA.assertEquals(page.ServiceFlag.getText(), serv, "serv is wrong");

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListLDDForm);
		LinkedHashSet<ArrayList<String>> ExpectedProInformation = DataCommon.GetProListCLTG(SCAC, TrailerNB);
		SA.assertEquals(ProInfo, ExpectedProInformation,
				"cltg screen pro grid is wrong\n" + ExpectedProInformation + "\n" + ProInfo + "\n");

		// click submit
		page.SubmitButton.click();
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField, "No updates entered."));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord, OldEqpStatusRecord, "eqps change");

		SA.assertAll();
	}

	@Test(priority = 2, dataProvider = "cltg screen 1", dataProviderClass = DataForCLTGScreenTesting.class)
	public void CLWithProSetToCLTG(String terminalcd, String SCAC, String TrailerNB, String CityR, String CityRT,
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

		// Check Plan Day
		Date expectPlanDay = CommonFunction.getPrepopulatePlanDay(terminalcd, CurrentTime, PlanD);
		SA.assertEquals(page.GetPlanDatePickerTime(), expectPlanDay, "Plan date prepopulate time is wrong ");

		// Check other fields prepopulate
		SA.assertEquals(page.CityRoute.getAttribute("value").replaceAll("_", ""), CityR,
				"City Route prepopulate is wrong ");
		SA.assertEquals(page.CityRouteTypeField.getText(), CityRT, "City Route Type prepopulate is wrong ");
		SA.assertEquals(page.ShipmentCount2.getAttribute("value"), AmountPro, "Ship Count prepopulate is wrong ");
		SA.assertEquals(page.ShipmentWeight2.getAttribute("value").replaceAll("_", ""), AmountWeight,
				"Ship Weight prepopulate time is wrong ");
		SA.assertEquals(page.ShipmentFlag.getText(), flag, "shipments flag is wrong ");
		SA.assertEquals(page.ServiceFlag.getText(), serv, "serv is wrong");

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListLDDForm);
		LinkedHashSet<ArrayList<String>> ExpectedProInformation = DataCommon.GetProListCLTG(SCAC, TrailerNB);
		SA.assertEquals(ProInfo, ExpectedProInformation,
				"cltg screen pro grid is wrong\n" + ExpectedProInformation + "\n" + ProInfo + "\n");

		// set date&time
		page.SetDatePicker2(CommonFunction.getLocalTime(terminalcd, CurrentTime), 0, 10);
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
		SA.assertEquals(NewEqpStatusRecord.get(21), CityRT, "City_Route_Type_NM is wrong");
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

	@Test(priority = 3, dataProvider = "cltg screen 1", dataProviderClass = DataForCLTGScreenTesting.class)
	public void ToCLTGHasProSetToCLTG(String terminalcd, String SCAC, String TrailerNB, String CityR, String CityRT,
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

		// Check Plan Day for cltg screen, since the plan date field is
		// disabled, so if plan date is null, the screen show blank
		Date expectPlanDay;
		if (PlanD != null) {
			expectPlanDay = CommonFunction.getPrepopulatePlanDay(terminalcd, CurrentTime, PlanD);
		} else {
			expectPlanDay = PlanD;
		}
		SA.assertEquals(page.GetPlanDatePickerTime(), expectPlanDay, "Plan date prepopulate time is wrong ");

		// Check other fields prepopulate
		SA.assertEquals(page.CityRoute.getAttribute("value").replaceAll("_", ""), CityR,
				"City Route prepopulate is wrong ");
		SA.assertEquals(page.CityRouteTypeField.getText(), CityRT, "City Route Type prepopulate is wrong ");
		SA.assertEquals(page.ShipmentCount2.getAttribute("value"), AmountPro, "Ship Count prepopulate is wrong ");
		SA.assertEquals(page.ShipmentWeight2.getAttribute("value").replaceAll("_", ""), AmountWeight,
				"Ship Weight prepopulate time is wrong ");
		SA.assertEquals(page.ShipmentFlag.getText(), flag, "shipments flag is wrong ");
		SA.assertEquals(page.ServiceFlag.getText(), serv, "serv is wrong");

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListLDDForm);
		LinkedHashSet<ArrayList<String>> ExpectedProInformation = DataCommon.GetProListCLTG(SCAC, TrailerNB);
		SA.assertEquals(ProInfo, ExpectedProInformation,
				"cltg screen pro grid is wrong\n" + ExpectedProInformation + "\n" + ProInfo + "\n");

		// set date&time
		page.SetDatePicker2(CommonFunction.getLocalTime(terminalcd, CurrentTime), 0, 10);
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
		SA.assertEquals(NewEqpStatusRecord.get(21), CityRT, "City_Route_Type_NM is wrong");
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

	@Test(priority = 4, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class)
	public void CLTrailerWithProNotInLoading3BlobrLeaveOn(String terminalcd, String SCAC, String TrailerNB,
			String CityR, String CityRT, String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		WebDriverWait wait = new WebDriverWait(driver, 230);
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("utc");
		page.EnterTrailer(SCAC, TrailerNB);
		wait.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// check lobr date&time pre populate

		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeMayNewRecordCreate(terminalcd, CurrentTime, MRSts, PlanD);
		SA.assertEquals(picker, expect, "lobr screen prepopulate time is wrong ");

		// Check Plan Day
		Date expectPlanDay = CommonFunction.getPrepopulatePlanDay(terminalcd, CurrentTime, PlanD);
		SA.assertEquals(page.GetPlanDatePickerTime(), expectPlanDay, "Plan date prepopulate time is wrong ");

		// Check other fields prepopulate
		SA.assertEquals(page.CityRoute.getAttribute("value").replaceAll("_", ""), CityR,
				"City Route prepopulate is wrong ");
		SA.assertEquals(page.CityRouteTypeField.getText(), CityRT, "City Route Type prepopulate is wrong ");

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		LinkedHashSet<ArrayList<String>> ExpectedProInformation = DataCommon.GetProNotInLoadingListLOBR(SCAC,
				TrailerNB);
		SA.assertEquals(ProInfo, ExpectedProInformation,
				"3 button lobr screen pro grid is wrong\n" + ExpectedProInformation + "\n" + ProInfo + "\n");

		// get pro list before handle lobr
		ArrayList<String> prolistbeforelobr = DataCommon.GetProNotInLoadingOnTrailer(SCAC, TrailerNB);

		// update cityRoute
		String NewCityRoute = page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		Date PlanDate = page.SetPlanDate(Localtime, 2);

		// select city route type
		String SetCityRtype = page.SetCityRouteType("trap");

		// set date&time
		page.SetDatePicker2(CommonFunction.getLocalTime(terminalcd, CurrentTime), 0, 10);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// leave on
		page.HandleLOBRproAll("leaveON");
		Date d = CommonFunction.gettime("UTC");
		wait.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		wait.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status City Loading"));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));

		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "CL", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "CL", "Source_Create_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(21), SetCityRtype, "City_Route_Type_NM is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(22), NewCityRoute, "City_Route_NM is wrong");

		int[] TimeElement = { 5, 6, 7, 8, 20 };
		for (int i : TimeElement) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + " " + TS + " " + AlterTime);
			} else if (i == 20) {
				SA.assertEquals(TS, PlanDate, "Planned_Delivery_DT is wrong");
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + " " + TS + " " + d);
			}
		}

		// check waybill
		Date f2 = null;
		for (int i = 0; i < prolistbeforelobr.size(); i++) {
			String CurrentPro = prolistbeforelobr.get(i);
			ArrayList<Object> AfterADDWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SA.assertEquals(AfterADDWb.get(0), SCAC, "Waybill table Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			SA.assertEquals(AfterADDWb.get(1), TrailerNB, "Waybill table Equipment_Unit_NB is wrong " + CurrentPro);
			SA.assertEquals(AfterADDWb.get(3), "LOBR", "Waybill table Source_Modify_ID is wrong " + CurrentPro);
			Date f = CommonFunction.SETtime((Date) AfterADDWb.get(9));
			SA.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table system_modify_ts  " + f + "  " + d + "  " + "   " + CurrentPro);

			// loading record
			SA.assertEquals(AfterADDWb.get(14), null,
					"waybill table loading record To_Facility_CD is wrong " + CurrentPro);
			SA.assertEquals(AfterADDWb.get(13), terminalcd,
					"waybill table loading record From_Facility_CD is wrong " + CurrentPro);
			SA.assertEquals(AfterADDWb.get(17), SCAC,
					"waybill table loading record To_Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			SA.assertEquals(AfterADDWb.get(18), TrailerNB,
					"waybill table loading record To_Equipment_Unit_NB is wrong " + CurrentPro);
			SA.assertEquals(AfterADDWb.get(15), null,
					"waybill table loading record From_Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			SA.assertEquals(AfterADDWb.get(16), null,
					"waybill table loading record From_Equipment_Unit_NB is wrong " + CurrentPro);
			SA.assertEquals(AfterADDWb.get(20), "LOADING",
					"waybill table loading Waybill_Transaction_Type_NM is wrong " + CurrentPro);

			Date f1 = CommonFunction.SETtime((Date) AfterADDWb.get(11));
			SA.assertTrue(Math.abs(f1.getTime() - AlterTime.getTime()) < 60000,
					"waybill Waybill_Transaction_End_TS is  " + f1 + " set date is : " + AlterTime + "  " + "   "
							+ CurrentPro);
			// CHECK THE WAYBILL TRANSACTION END TS ARE NOT IDENTICAL
			if (i > 0)
				SA.assertTrue(f1.after(f2),
						"waybill Waybill_Transaction_End_TS is not ascending increase : waybill_transaction_end_ts "
								+ f1 + "  waybill_transaction_end_ts of previous pro is  " + f2 + "  " + "   "
								+ CurrentPro);
			f2 = f1;
		}
		// check pro grid prepopulate
		LinkedHashSet<ArrayList<String>> ProInfo2 = page.GetProList(page.ProListForm);
		SA.assertEquals(ProInfo2, DataCommon.GetProListCL(SCAC, TrailerNB), "cl screen pro grid is wrong");
		SA.assertAll();
	}

	@Test(priority = 5, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class)
	public void CLTrailerWithProNotInLoading3BlobrDock(String terminalcd, String SCAC, String TrailerNB, String CityR,
			String CityRT, String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		WebDriverWait wait = new WebDriverWait(driver, 230);
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);
		wait.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// check lobr date&time pre populate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeMayNewRecordCreate(terminalcd, CurrentTime, MRSts, PlanD);
		SA.assertEquals(picker, expect, "lobr screen prepopulate time is wrong ");

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SA.assertEquals(ProInfo, DataCommon.GetProNotInLoadingListLOBR(SCAC, TrailerNB),
				"3 button lobr screen pro grid is wrong");

		// Check Plan Day
		Date expectPlanDay = CommonFunction.getPrepopulatePlanDay(terminalcd, CurrentTime, PlanD);
		SA.assertEquals(page.GetPlanDatePickerTime(), expectPlanDay, "Plan date prepopulate time is wrong ");

		// Check other fields prepopulate
		SA.assertEquals(page.CityRoute.getAttribute("value").replaceAll("_", ""), CityR,
				"City Route prepopulate is wrong ");
		SA.assertEquals(page.CityRouteTypeField.getText(), CityRT, "City Route Type prepopulate is wrong ");

		// get pro list before handle lobr
		ArrayList<String> prolistbeforelobr = DataCommon.GetProNotInLoadingOnTrailer(SCAC, TrailerNB);

		// update cityRoute
		String NewCityRoute = page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		Date PlanDate = page.SetPlanDate(Localtime, 2);

		// select city route type
		String SetCityRtype = page.SetCityRouteType("cartage");

		// set date&time
		page.SetDatePicker2(CommonFunction.getLocalTime(terminalcd, CurrentTime), 0, 10);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		SA.assertAll();
		// Dock
		page.HandleLOBRproAll("Dock");
		Date d = CommonFunction.gettime("UTC");
		wait.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		wait.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status City Loading"));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));

		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "CL", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "CL", "Source_Create_ID is wrong");
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

		Thread.sleep(5000);
		// check waybill
		for (String RemovedPro : prolistbeforelobr) {
			ArrayList<Object> AfterRemoveWb = DataCommon.GetWaybillInformationOfPro(RemovedPro);
			SA.assertTrue(AfterRemoveWb.get(0) == null, "waybill scac is wrong");
			SA.assertTrue(AfterRemoveWb.get(1) == null, "waybill equipment_unit_nb is wrong");
			SA.assertEquals(AfterRemoveWb.get(3), "LOBR", "waybill source_modify_id is wrong");
			// SAssert.assertEquals(AfterRemoveWb.get(7),BeforeRemoveWb.get(7),""+RemovedPro+"
			// Create_TS is wrong");
			// SAssert.assertEquals(AfterRemoveWb.get(8),BeforeRemoveWb.get(8),""+RemovedPro+"
			// System_Insert_TS is wrong");
			Date f = CommonFunction.SETtime((Date) AfterRemoveWb.get(9));
			SA.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table waybill system_modify_ts  " + f + "  " + d + "  " + "   " + RemovedPro);
			// SAssert.assertEquals(AfterRemoveWb.get(12),BeforeRemoveWb.get(12),"waybill
			// table record_key is wrong "+RemovedPro);
			SA.assertTrue(AfterRemoveWb.get(13) == null, "waybill  From_Facility_CD is wrong " + RemovedPro);
			SA.assertEquals(AfterRemoveWb.get(14), terminalcd, "waybill To_Facility_CD is wrong " + RemovedPro);
			SA.assertEquals(AfterRemoveWb.get(15), SCAC,
					"waybill  From_Standard_Carrier_Alpha_CD is wrong " + RemovedPro);
			SA.assertEquals(AfterRemoveWb.get(16), TrailerNB, "waybill  From_Equipment_Unit_NB is wrong " + RemovedPro);
			SA.assertTrue(AfterRemoveWb.get(17) == null,
					"waybill  To_Standard_Carrier_Alpha_CD is wrong " + RemovedPro);
			SA.assertTrue(AfterRemoveWb.get(18) == null, "waybill  To_Equipment_Unit_NB is wrong " + RemovedPro);
			SA.assertEquals(AfterRemoveWb.get(20), "UNLOADING",
					"waybill  Waybill_Transaction_Type_NM is wrong " + RemovedPro);
			Date f1 = CommonFunction.SETtime((Date) AfterRemoveWb.get(11));
			SA.assertTrue(Math.abs(f1.getTime() - d.getTime()) < 120000,
					"waybill table Waybill_Transaction_End_TS  " + f1 + "  " + d + "  " + "   " + RemovedPro);
		}

		// check pro grid prepopulate
		LinkedHashSet<ArrayList<String>> ProInfo2 = page.GetProList(page.ProListForm);
		SA.assertEquals(ProInfo2, DataCommon.GetProListCL(SCAC, TrailerNB), "cl screen pro grid is wrong");

		SA.assertAll();
	}

	@Test(priority = 6, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class)
	public void CLTrailerWithProNotInLoading3BlobrAllShort(String terminalcd, String SCAC, String TrailerNB,
			String CityR, String CityRT, String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		WebDriverWait wait = new WebDriverWait(driver, 230);
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);
		wait.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// check date&time prepopulate
		Date Picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeMayNewRecordCreate(terminalcd, CurrentTime, MRSts, PlanD);
		SA.assertEquals(Picker, expect, "3 Button lobr screen date&time is not showing correct");

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SA.assertEquals(ProInfo, DataCommon.GetProNotInLoadingListLOBR(SCAC, TrailerNB),
				"3 button lobr screen pro grid is wrong");

		// Check Plan Day
		Date expectPlanDay = CommonFunction.getPrepopulatePlanDay(terminalcd, CurrentTime, PlanD);
		SA.assertEquals(page.GetPlanDatePickerTime(), expectPlanDay, "Plan date prepopulate time is wrong ");

		// Check other fields prepopulate
		SA.assertEquals(page.CityRoute.getAttribute("value").replaceAll("_", ""), CityR,
				"City Route prepopulate is wrong ");
		SA.assertEquals(page.CityRouteTypeField.getText(), CityRT, "City Route Type prepopulate is wrong ");

		// get pro list before handle lobr
		ArrayList<String> prolistbeforelobr = DataCommon.GetProNotInLoadingOnTrailer(SCAC, TrailerNB);

		// update cityRoute
		String NewCityRoute = page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		Date PlanDate = page.SetPlanDate(Localtime, 2);

		// select city route type
		String SetCityRtype = page.SetCityRouteType("INTERLINE");

		// set date&time
		page.SetDatePicker2(CommonFunction.getLocalTime(terminalcd, CurrentTime), 0, 10);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// all short
		page.HandleLOBRproAll("ALLSHORT");
		Date d = CommonFunction.gettime("UTC");
		wait.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		wait.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status City Loading"));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));

		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "CL", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "CL", "Source_Create_ID is wrong");
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

		Thread.sleep(5000);
		// check waybill
		for (String RemovedPro : prolistbeforelobr) {
			ArrayList<Object> AfterRemoveWb = DataCommon.GetWaybillInformationOfPro(RemovedPro);
			SA.assertTrue(AfterRemoveWb.get(0) == null, "waybill scac is wrong");
			SA.assertTrue(AfterRemoveWb.get(1) == null, "waybill equipment_unit_nb is wrong");
			SA.assertEquals(AfterRemoveWb.get(3), "LOBR", "waybill source_modify_id is wrong");
			// SAssert.assertEquals(AfterRemoveWb.get(7),BeforeRemoveWb.get(7),""+RemovedPro+"
			// Create_TS is wrong");
			// SAssert.assertEquals(AfterRemoveWb.get(8),BeforeRemoveWb.get(8),""+RemovedPro+"
			// System_Insert_TS is wrong");
			Date f = CommonFunction.SETtime((Date) AfterRemoveWb.get(9));
			SA.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table waybill system_modify_ts  " + f + "  " + d + "  " + "   " + RemovedPro);
			// SAssert.assertEquals(AfterRemoveWb.get(12),BeforeRemoveWb.get(12),"waybill
			// table record_key is wrong "+RemovedPro);
			SA.assertTrue(AfterRemoveWb.get(13) == null, "waybill  From_Facility_CD is wrong" + RemovedPro);
			SA.assertEquals(AfterRemoveWb.get(14), terminalcd, "waybill To_Facility_CD is wrong" + RemovedPro);
			SA.assertEquals(AfterRemoveWb.get(15), SCAC,
					"waybill  From_Standard_Carrier_Alpha_CD is wrong" + RemovedPro);
			SA.assertEquals(AfterRemoveWb.get(16), TrailerNB, "waybill  From_Equipment_Unit_NB is wrong" + RemovedPro);
			SA.assertTrue(AfterRemoveWb.get(17) == null, "waybill  To_Standard_Carrier_Alpha_CD is wrong" + RemovedPro);
			SA.assertTrue(AfterRemoveWb.get(18) == null, "waybill  To_Equipment_Unit_NB is wrong" + RemovedPro);
			SA.assertEquals(AfterRemoveWb.get(20), "UNLOADING",
					"waybill  Waybill_Transaction_Type_NM is wrong" + RemovedPro);
			Date f1 = CommonFunction.SETtime((Date) AfterRemoveWb.get(11));
			SA.assertTrue(Math.abs(f1.getTime() - d.getTime()) < 120000,
					"waybill table Waybill_Transaction_End_TS  " + f1 + "  " + d + "  " + "   " + RemovedPro);

		}

		// check pro grid prepopulate
		LinkedHashSet<ArrayList<String>> ProInfo2 = page.GetProList(page.ProListForm);
		SA.assertEquals(ProInfo2, DataCommon.GetProListCL(SCAC, TrailerNB), "cl screen pro grid is wrong");

		SA.assertAll();

	}

	@Test(priority = 7, dataProvider = "cltg screen 2", dataProviderClass = DataForCLTGScreenTesting.class)
	public void ToCLTGNoProSetToCLTG(String terminalcd, String SCAC, String TrailerNB, String CityR, String CityRT,
			String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<Object> OldEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"No PROs Loaded, Cannot Close."));

		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord, OldEqpStatusRecord, "eqps change");
		SA.assertAll();
	}

	@AfterMethod
	public void getback(ITestResult result) throws InterruptedException {
		if (result.getStatus() == ITestResult.FAILURE) {

			String Testparameter = Arrays.toString(Arrays.copyOf(result.getParameters(), 3)).replaceAll("[^\\d.a-zA-Z]",
					"");
			String FailureTestparameter = result.getName() + Testparameter;

			Utility.takescreenshot(driver, FailureTestparameter);
			driver.navigate().refresh();
			page.SetStatus("cltg");
		}
	}

	@BeforeMethod
	public void SetBackCLTG() throws InterruptedException {
		if (!page.SetStatusToField.getText().contains("CLTG"))
			page.ChangeStatusTo("cltg");
	}

	@AfterTest
	public void TearDown() {
		driver.quit();
	}
}
