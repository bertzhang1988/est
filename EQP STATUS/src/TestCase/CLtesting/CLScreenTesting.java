package TestCase.CLtesting;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
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

	@Test(priority = 1, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class, description = "not in cl no pro set to cl", enabled = true)
	public void ToCLNoProSetToCl(String terminalcd, String SCAC, String TrailerNB, String CityR, String CityRT,
			String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, SQLException, ClassNotFoundException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		// Check date and time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MRSts);
		SA.assertEquals(picker, expect, "CL screen prepopulate time is wrong ");

		// Check Plan Day and other fields prepopulate
		SA.assertEquals(page.GetPlanDatePickerTime(), CommonFunction.SETtime(PlanD),
				"Plan date prepopulate time is wrong ");
		SA.assertEquals(page.CityRoute.getAttribute("value").replaceAll("_", ""), CityR,
				"City Route prepopulate is wrong ");
		SA.assertEquals(page.CityRouteTypeField.getText(), CityRT, "City Route Type prepopulate is wrong ");
		SA.assertEquals(page.ShipCount1.getText(), AmountPro, "Ship Count prepopulate is wrong ");
		SA.assertEquals(page.ShipWeight1.getText(), AmountWeight, "Ship Weight prepopulate time is wrong ");

		// check pro grid prepopulate
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListForm);
		SA.assertEquals(DataCommon.GetProListCL(SCAC, TrailerNB), ProInfo, "pro grid is wrong");

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

		// click submit
		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Trailer " + page.SCACTrailer(SCAC, TrailerNB) + " updated to CL"));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
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

		SA.assertAll();

	}

	@Test(priority = 2, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class, description = "trailer not in cl status no pro add pro", enabled = true)
	public void ToCLNoProAddPro(String terminalcd, String SCAC, String TrailerNB, String CityR, String CityRT,
			String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		Actions builder = new Actions(driver);
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		// Check date and time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MRSts);
		SA.assertEquals(picker, expect, "CL screen prepopulate time is wrong ");

		// Check Plan Day and other fields prepopulate
		SA.assertEquals(page.GetPlanDatePickerTime(), CommonFunction.SETtime(PlanD),
				"Plan date prepopulate time is wrong ");
		SA.assertEquals(page.CityRoute.getAttribute("value").replaceAll("_", ""), CityR,
				"City Route prepopulate is wrong ");
		SA.assertEquals(page.CityRouteTypeField.getText(), CityRT, "City Route Type prepopulate is wrong ");
		SA.assertEquals(page.ShipCount1.getText(), AmountPro, "Ship Count prepopulate is wrong ");
		SA.assertEquals(page.ShipWeight1.getText(), AmountWeight, "Ship Weight prepopulate time is wrong ");

		// check pro grid prepopulate
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListForm);
		SA.assertEquals(DataCommon.GetProListCL(SCAC, TrailerNB), ProInfo, "pro grid is wrong");

		// update cityRoute
		String NewCityRoute = page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		Date PlanDate = page.SetPlanDate(Localtime, 2);

		// select city route type
		String SetCityRtype = page.SetCityRouteType("interline");

		// set date&time
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// add pro
		ArrayList<String> PRO = DataCommon.GetProNotInAnyTrailer();
		page.RemoveProButton.click();
		ArrayList<String> Addpro = new ArrayList<String>();
		for (int i = 0; i < 3; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			Addpro.add(CurrentPro);
		}

		// enter key
		builder.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Trailer " + page.SCACTrailer(SCAC, TrailerNB) + " updated to CL"));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

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

		// CHECK WAYBILL TABLE (load new pro)
		for (String pro : Addpro) {
			ArrayList<Object> CheckWaybillRecord = DataCommon.GetWaybillInformationOfPro(pro);
			SA.assertEquals(CheckWaybillRecord.get(0), SCAC, "" + pro + " waybill SCAC is wrong");
			SA.assertEquals(CheckWaybillRecord.get(1), TrailerNB, "" + pro + " waybill trailernb is wrong");
			SA.assertEquals(CheckWaybillRecord.get(17), SCAC, "" + pro + " waybill  toSCAC is wrong");
			SA.assertEquals(CheckWaybillRecord.get(13), terminalcd, "" + pro + " waybill from terminal is wrong");
			SA.assertEquals(CheckWaybillRecord.get(18), TrailerNB, "" + pro + " waybill totrailernb is wrong");
			SA.assertEquals(CheckWaybillRecord.get(20), "LOADING", "" + pro + " waybill TRANSACTION TYPE is wrong");
			Date System_Modify_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(9));
			SA.assertTrue(Math.abs(System_Modify_TS.getTime() - d.getTime()) < 120000,
					"" + pro + " waybill table System_Modify_TS " + System_Modify_TS + "  " + d);
			Date Waybill_Transaction_End_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(11));
			SA.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime() - d.getTime()) < 120000,
					"" + pro + " waybill table Waybill_Transaction_End_TS " + System_Modify_TS + "  " + d);
		}

		SA.assertAll();

	}

	@Test(priority = 3, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class, description = "trailer not in cl status no pro add food or pois pro", enabled = true)
	public void ToCLNoProAddProWithType(String terminalcd, String SCAC, String TrailerNB, String CityR, String CityRT,
			String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		Actions builder = new Actions(driver);
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		// Check date and time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MRSts);
		SA.assertEquals(picker, expect, "CL screen prepopulate time is wrong ");

		// Check Plan Day and other fields prepopulate
		SA.assertEquals(page.GetPlanDatePickerTime(), CommonFunction.SETtime(PlanD),
				"Plan date prepopulate time is wrong ");
		SA.assertEquals(page.CityRoute.getAttribute("value").replaceAll("_", ""), CityR,
				"City Route prepopulate is wrong ");
		SA.assertEquals(page.CityRouteTypeField.getText(), CityRT, "City Route Type prepopulate is wrong ");
		SA.assertEquals(page.ShipCount1.getText(), AmountPro, "Ship Count prepopulate is wrong ");
		SA.assertEquals(page.ShipWeight1.getText(), AmountWeight, "Ship Weight prepopulate time is wrong ");

		// check pro grid prepopulate
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListForm);
		SA.assertEquals(DataCommon.GetProListCL(SCAC, TrailerNB), ProInfo, "pro grid is wrong");

		// update cityRoute
		String NewCityRoute = page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		Date PlanDate = page.SetPlanDate(Localtime, 2);

		// select city route type
		String SetCityRtype = page.SetCityRouteType("appt");

		// set date&time
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// add pro
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "pois");
		page.RemoveProButton.click();
		ArrayList<String> Addpro = new ArrayList<String>();
		for (int i = 0; i < 3; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			Addpro.add(CurrentPro);
		}

		// enter key
		builder.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Trailer " + page.SCACTrailer(SCAC, TrailerNB) + " updated to CL"));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

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

		// CHECK WAYBILL TABLE (load new pro)
		for (String pro : Addpro) {
			ArrayList<Object> CheckWaybillRecord = DataCommon.GetWaybillInformationOfPro(pro);
			SA.assertEquals(CheckWaybillRecord.get(0), SCAC, "" + pro + " waybill SCAC is wrong");
			SA.assertEquals(CheckWaybillRecord.get(1), TrailerNB, "" + pro + " waybill trailernb is wrong");
			SA.assertEquals(CheckWaybillRecord.get(17), SCAC, "" + pro + " waybill  toSCAC is wrong");
			SA.assertEquals(CheckWaybillRecord.get(13), terminalcd, "" + pro + " waybill from terminal is wrong");
			SA.assertEquals(CheckWaybillRecord.get(18), TrailerNB, "" + pro + " waybill totrailernb is wrong");
			SA.assertEquals(CheckWaybillRecord.get(20), "LOADING", "" + pro + " waybill TRANSACTION TYPE is wrong");
			Date System_Modify_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(9));
			SA.assertTrue(Math.abs(System_Modify_TS.getTime() - d.getTime()) < 120000,
					"" + pro + " waybill table System_Modify_TS " + System_Modify_TS + "  " + d);
			Date Waybill_Transaction_End_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(11));
			SA.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime() - d.getTime()) < 120000,
					"" + pro + " waybill table Waybill_Transaction_End_TS " + System_Modify_TS + "  " + d);
		}

		SA.assertAll();

	}

	@Test(priority = 4, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class, description = "trailer in cl status With pro set to cl", enabled = true)
	public void CLHasProSetToCL(String terminalcd, String SCAC, String TrailerNB, String CityR, String CityRT,
			String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		Actions builder = new Actions(driver);
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		// Check date and time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, MRSts);
		SA.assertEquals(picker, expect, "CL screen prepopulate time is wrong ");

		// Check Plan Day and other fields prepopulate
		SA.assertEquals(page.GetPlanDatePickerTime(), CommonFunction.SETtime(PlanD),
				"Plan date prepopulate time is wrong ");
		SA.assertEquals(page.CityRoute.getAttribute("value").replaceAll("_", ""), CityR,
				"City Route prepopulate is wrong ");
		SA.assertEquals(page.CityRouteTypeField.getText(), CityRT, "City Route Type prepopulate is wrong ");
		SA.assertEquals(page.ShipCount1.getText(), AmountPro, "Ship Count prepopulate is wrong ");
		SA.assertEquals(page.ShipWeight1.getText(), AmountWeight, "Ship Weight prepopulate time is wrong ");

		// check pro grid prepopulate
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListForm);
		SA.assertEquals(DataCommon.GetProListCL(SCAC, TrailerNB), ProInfo, "pro grid is wrong");

		// update cityRoute
		String NewCityRoute = page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		Date PlanDate = page.SetPlanDate(Localtime, 2);

		// select city route type
		String SetCityRtype = page.SetCityRouteType("appt");

		// set date&time
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// add pro
		ArrayList<String> PRO = DataCommon.GetProNotInAnyTrailer();
		page.RemoveProButton.click();
		ArrayList<String> Addpro = new ArrayList<String>();
		for (int i = 0; i < 3; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			Addpro.add(CurrentPro);
		}

		// enter key
		builder.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Trailer " + page.SCACTrailer(SCAC, TrailerNB) + " updated to CL"));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

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

		// CHECK WAYBILL TABLE (load new pro)
		for (String pro : Addpro) {
			ArrayList<Object> CheckWaybillRecord = DataCommon.GetWaybillInformationOfPro(pro);
			SA.assertEquals(CheckWaybillRecord.get(0), SCAC, "" + pro + " waybill SCAC is wrong");
			SA.assertEquals(CheckWaybillRecord.get(1), TrailerNB, "" + pro + " waybill trailernb is wrong");
			SA.assertEquals(CheckWaybillRecord.get(17), SCAC, "" + pro + " waybill  toSCAC is wrong");
			SA.assertEquals(CheckWaybillRecord.get(13), terminalcd, "" + pro + " waybill from terminal is wrong");
			SA.assertEquals(CheckWaybillRecord.get(18), TrailerNB, "" + pro + " waybill totrailernb is wrong");
			SA.assertEquals(CheckWaybillRecord.get(20), "LOADING", "" + pro + " waybill TRANSACTION TYPE is wrong");
			Date System_Modify_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(9));
			SA.assertTrue(Math.abs(System_Modify_TS.getTime() - d.getTime()) < 120000,
					"" + pro + " waybill table System_Modify_TS " + System_Modify_TS + "  " + d);
			Date Waybill_Transaction_End_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(11));
			SA.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime() - d.getTime()) < 120000,
					"" + pro + " waybill table Waybill_Transaction_End_TS " + System_Modify_TS + "  " + d);
		}

		SA.assertAll();

	}

	@Test(priority = 5, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class, description = "in cltg has pro set to cl", enabled = true)
	public void CLTGHasProSetToCl(String terminalcd, String SCAC, String TrailerNB, String CityR, String CityRT,
			String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, SQLException, ClassNotFoundException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		// Check date and time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MRSts);
		SA.assertEquals(picker, expect, "CL screen prepopulate time is wrong ");

		// Check Plan Day and other fields prepopulate
		SA.assertEquals(page.GetPlanDatePickerTime(), CommonFunction.SETtime(PlanD),
				"Plan date prepopulate time is wrong ");
		SA.assertEquals(page.CityRoute.getAttribute("value").replaceAll("_", ""), CityR,
				"City Route prepopulate is wrong ");
		SA.assertEquals(page.CityRouteTypeField.getText(), CityRT, "City Route Type prepopulate is wrong ");
		SA.assertEquals(page.ShipCount1.getText(), AmountPro, "Ship Count prepopulate is wrong ");
		SA.assertEquals(page.ShipWeight1.getText(), AmountWeight, "Ship Weight prepopulate time is wrong ");

		// check pro grid prepopulate
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListForm);
		SA.assertEquals(DataCommon.GetProListCL(SCAC, TrailerNB), ProInfo, "pro grid is wrong");

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

		// click submit
		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Trailer " + page.SCACTrailer(SCAC, TrailerNB) + " updated to CL"));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
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

		SA.assertAll();

	}

	@Test(priority = 6, dataProvider = "cl with pro", dataProviderClass = DataForCLScreenTesting.class, description = "trailer in status to cl has pro, 3b lobr, leave on ", enabled = true)
	public void ToCLWithProLeaveOn(String terminalcd, String SCAC, String TrailerNB, String Desti, String Cube,
			String AmountPro, String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		WebDriverWait wait = new WebDriverWait(driver, 30);
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);
		wait.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));

		// check date&time prepopulate
		Date Picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MRSts);
		SA.assertEquals(Picker, expect, "3 Button lobr screen date&time is not showing correct");

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SA.assertEquals(ProInfo, DataCommon.GetProListLOBR(SCAC, TrailerNB), "3 button lobr screen pro grid is wrong");

		// get pro list before handle lobr
		ArrayList<String> prolistbeforelobr = DataCommon.GetProOnTrailer(SCAC, TrailerNB);

		// update cityRoute
		String NewCityRoute = page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		Date PlanDate = page.SetPlanDate(Localtime, 2);

		// select city route type
		String SetCityRtype = page.SetCityRouteType("appt");

		// set date&time
		page.SetDatePicker(page.GetDatePickerTime(), -1);
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
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else if (i == 20) {
				SA.assertEquals(TS, PlanDate, "Planned_Delivery_DT is wrong");
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		Thread.sleep(5000);
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
		SA.assertEquals(DataCommon.GetProList(SCAC, TrailerNB), ProInfo2, "cl screen pro grid is wrong");
		SA.assertAll();
	}

	@Test(priority = 7, dataProvider = "cl with pro", dataProviderClass = DataForCLScreenTesting.class, description = "trailer in status to cl has pro, 3b lobr, dock ", enabled = true)
	public void ToCLWithProDock(String terminalcd, String SCAC, String TrailerNB, String Desti, String Cube,
			String AmountPro, String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		WebDriverWait wait = new WebDriverWait(driver, 30);
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);
		wait.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));

		// check date&time prepopulate
		Date Picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MRSts);
		SA.assertEquals(Picker, expect, "3 Button lobr screen date&time is not showing correct");

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SA.assertEquals(ProInfo, DataCommon.GetProListLOBR(SCAC, TrailerNB), "3 button lobr screen pro grid is wrong");

		// get pro list before handle lobr
		ArrayList<String> prolistbeforelobr = DataCommon.GetProOnTrailer(SCAC, TrailerNB);

		// update cityRoute
		String NewCityRoute = page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		Date PlanDate = page.SetPlanDate(Localtime, 2);

		// select city route type
		String SetCityRtype = page.SetCityRouteType("cartage");

		// set date&time
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// leave on
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
		SA.assertEquals(DataCommon.GetProList(SCAC, TrailerNB), ProInfo2, "cl screen pro grid is wrong");

		SA.assertAll();
	}

	@Test(priority = 8, dataProvider = "cl with pro", dataProviderClass = DataForCLScreenTesting.class, description = "trailer in status to cl has pro, 3b lobr, all short ", enabled = true)
	public void ToCLWithProAllShort(String terminalcd, String SCAC, String TrailerNB, String Desti, String Cube,
			String AmountPro, String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		WebDriverWait wait = new WebDriverWait(driver, 30);
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);
		wait.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));

		// check date&time prepopulate
		Date Picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MRSts);
		SA.assertEquals(Picker, expect, "3 Button lobr screen date&time is not showing correct");

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SA.assertEquals(ProInfo, DataCommon.GetProListLOBR(SCAC, TrailerNB), "3 button lobr screen pro grid is wrong");

		// get pro list before handle lobr
		ArrayList<String> prolistbeforelobr = DataCommon.GetProOnTrailer(SCAC, TrailerNB);

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
		SA.assertEquals(DataCommon.GetProList(SCAC, TrailerNB), ProInfo2, "pro grid is wrong");

		SA.assertAll();

	}

	@Test(priority = 9, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class, description = "not in cl no pro quick close", enabled = true)
	public void ToCLNoProQuickClose(String terminalcd, String SCAC, String TrailerNB, String CityR, String CityRT,
			String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, SQLException, ClassNotFoundException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		// Check date and time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MRSts);
		SA.assertEquals(picker, expect, "CL screen prepopulate time is wrong ");

		// Check Plan Day and other fields prepopulate
		SA.assertEquals(page.GetPlanDatePickerTime(), CommonFunction.SETtime(PlanD),
				"Plan date prepopulate time is wrong ");
		SA.assertEquals(page.CityRoute.getAttribute("value").replaceAll("_", ""), CityR,
				"City Route prepopulate is wrong ");
		SA.assertEquals(page.CityRouteTypeField.getText(), CityRT, "City Route Type prepopulate is wrong ");
		SA.assertEquals(page.ShipCount1.getText(), AmountPro, "Ship Count prepopulate is wrong ");
		SA.assertEquals(page.ShipWeight1.getText(), AmountWeight, "Ship Weight prepopulate time is wrong ");

		// check pro grid prepopulate
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListForm);
		SA.assertEquals(DataCommon.GetProListCL(SCAC, TrailerNB), ProInfo, "pro grid is wrong");

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

		// click submit
		page.qcCloseTrailerButton.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Trailer " + page.SCACTrailer(SCAC, TrailerNB) + " updated to CL"));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
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

		SA.assertAll();

	}

	@AfterClass
	public void TearDown() {
		driver.quit();
	}
}
