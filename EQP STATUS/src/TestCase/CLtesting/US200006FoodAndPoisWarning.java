package TestCase.CLtesting;

import java.awt.AWTException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Data.DataForUS200006;
import Function.CommonFunction;
import Function.DataCommon;
import Function.SetupBrowser;
import Page.EqpStatusPageS;

public class US200006FoodAndPoisWarning extends SetupBrowser {
	private EqpStatusPageS page;
	private WebDriverWait w1;
	private WebDriverWait w2;

	@BeforeClass
	public void SetUp() throws AWTException, InterruptedException {

		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 50);
		w2 = new WebDriverWait(driver, 80);
		driver.get(conf.GetURL());
		driver.manage().window().maximize();
		page.SetStatus("CL");
	}

	@Test(priority = 1, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class)
	public void addSingleFoodToCLWithoutPro(String terminalcd, String SCAC, String TrailerNB, String CityR,
			String CityRT, String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
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
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "food");
		for (int i = 0; i < 1; i++) {
			page.RemoveProButton.click();
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			page.SubmitButton1.click();
			Date d = CommonFunction.gettime("UTC");
			// (new WebDriverWait(driver,
			// 80)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,"pro(s)
			// loaded."));
			w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
			w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
			w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			// check eqps
			ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
			SA.assertEquals(NewEqpStatusRecord.get(0), "CL", "Equipment_Status_Type_CD is wrong");
			SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
			SA.assertEquals(NewEqpStatusRecord.get(3), "CL", "Source_Create_ID is wrong");
			SA.assertEquals(NewEqpStatusRecord.get(21), SetCityRtype, "City_Route_Type_NM is wrong");
			SA.assertEquals(NewEqpStatusRecord.get(22), NewCityRoute, "City_Route_NM is wrong");
			int[] TimeElement = { 5, 6, 7, 8, 20 };
			for (int i1 : TimeElement) {
				Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i1));
				if (i1 == 7) {
					SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
							"equipment_status_ts " + "  " + TS + "  " + AlterTime);
				} else if (i1 == 20) {
					SA.assertEquals(TS, PlanDate, "Planned_Delivery_DT is wrong");
				} else {
					SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i1 + "  " + TS + "  " + d);
				}
			}

			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SA.assertEquals(AfterLOADWb.get(0), SCAC, "Waybill table Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			SA.assertEquals(AfterLOADWb.get(1), TrailerNB, "Waybill table Equipment_Unit_NB is wrong " + CurrentPro);
			SA.assertEquals(AfterLOADWb.get(3), "LH.LDG", "Waybill table Source_Modify_ID is wrong " + CurrentPro);
			Date f = CommonFunction.SETtime((Date) AfterLOADWb.get(9));
			SA.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table system_modify_ts  " + f + "  " + d + "  " + "   " + CurrentPro);
		}
		SA.assertAll();
	}

	@Test(priority = 2, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class)
	public void addSinglePoisToCLWithoutPro(String terminalcd, String SCAC, String TrailerNB, String CityR,
			String CityRT, String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
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
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "pois");
		for (int i = 0; i < 1; i++) {
			page.RemoveProButton.click();
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			page.SubmitButton1.click();
			Date d = CommonFunction.gettime("UTC");
			// (new WebDriverWait(driver,
			// 80)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,"pro(s)
			// loaded."));
			w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
			w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
			w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			// check eqps
			ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
			SA.assertEquals(NewEqpStatusRecord.get(0), "CL", "Equipment_Status_Type_CD is wrong");
			SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
			SA.assertEquals(NewEqpStatusRecord.get(3), "CL", "Source_Create_ID is wrong");
			SA.assertEquals(NewEqpStatusRecord.get(21), SetCityRtype, "City_Route_Type_NM is wrong");
			SA.assertEquals(NewEqpStatusRecord.get(22), NewCityRoute, "City_Route_NM is wrong");
			int[] TimeElement = { 5, 6, 7, 8, 20 };
			for (int i1 : TimeElement) {
				Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i1));
				if (i1 == 7) {
					SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
							"equipment_status_ts " + "  " + TS + "  " + AlterTime);
				} else if (i1 == 20) {
					SA.assertEquals(TS, PlanDate, "Planned_Delivery_DT is wrong");
				} else {
					SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i1 + "  " + TS + "  " + d);
				}
			}

			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SA.assertEquals(AfterLOADWb.get(0), SCAC, "Waybill table Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			SA.assertEquals(AfterLOADWb.get(1), TrailerNB, "Waybill table Equipment_Unit_NB is wrong " + CurrentPro);
			SA.assertEquals(AfterLOADWb.get(3), "LH.LDG", "Waybill table Source_Modify_ID is wrong " + CurrentPro);
			Date f = CommonFunction.SETtime((Date) AfterLOADWb.get(9));
			SA.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table system_modify_ts  " + f + "  " + d + "  " + "   " + CurrentPro);

		}

		SA.assertAll();
	}

	@Test(priority = 3, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class)
	public void addBatchFoodToCLWithoutPro(String terminalcd, String SCAC, String TrailerNB, String CityR,
			String CityRT, String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
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
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		page.RemoveProButton.click();
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "food");
		ArrayList<String> Addpro = new ArrayList<String>();
		for (int i = 0; i < 2; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			Addpro.add(CurrentPro);
		}
		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		// (new WebDriverWait(driver,
		// 80)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,"pro(s)
		// loaded."));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
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

		for (int pro = 0; pro < Addpro.size(); pro++) {
			String CurrentPro = Addpro.get(pro);
			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SA.assertEquals(AfterLOADWb.get(0), SCAC, "Waybill table Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			SA.assertEquals(AfterLOADWb.get(1), TrailerNB, "Waybill table Equipment_Unit_NB is wrong " + CurrentPro);
			SA.assertEquals(AfterLOADWb.get(3), "LH.LDG", "Waybill table Source_Modify_ID is wrong " + CurrentPro);
			Date f = CommonFunction.SETtime((Date) AfterLOADWb.get(9));
			SA.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table system_modify_ts  " + f + "  " + d + "  " + "   " + CurrentPro);

		}
		SA.assertAll();
	}

	@Test(priority = 4, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class)
	public void addBatchPoisToCLWithoutPro(String terminalcd, String SCAC, String TrailerNB, String CityR,
			String CityRT, String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
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
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		page.RemoveProButton.click();
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "pois");
		ArrayList<String> Addpro = new ArrayList<String>();
		for (int i = 0; i < 2; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			Addpro.add(CurrentPro);
		}
		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField, "pro(s) loaded."));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
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

		for (int pro = 0; pro < Addpro.size(); pro++) {
			String CurrentPro = Addpro.get(pro);
			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SA.assertEquals(AfterLOADWb.get(0), SCAC, "Waybill table Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			SA.assertEquals(AfterLOADWb.get(1), TrailerNB, "Waybill table Equipment_Unit_NB is wrong " + CurrentPro);
			SA.assertEquals(AfterLOADWb.get(3), "LH.LDG", "Waybill table Source_Modify_ID is wrong " + CurrentPro);
			Date f = CommonFunction.SETtime((Date) AfterLOADWb.get(9));
			SA.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table system_modify_ts  " + f + "  " + d + "  " + "   " + CurrentPro);

		}
		SA.assertAll();

	}

	@Test(priority = 5, dataProvider = "2000.06 FOR CL", dataProviderClass = DataForUS200006.class)
	public void VerifySinglePoisProForFoodTrailer(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
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

		// List<ArrayList<String>>
		// PROonTrailerBeforeAdd=DataForUS200006.GetProList(SCAC,TrailerNB);
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "pois");
		for (int i = 0; i < 1; i++) {
			page.RemoveProButton.click();
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			page.SubmitButton1.click();
			w1.until(ExpectedConditions.textToBePresentInElement(
					page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div")),
					"Bill is POISON. Food already on trailer."));

			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SA.assertFalse(AfterLOADWb.get(0).equals(SCAC) && AfterLOADWb.get(1).equals(TrailerNB),
					"Waybill table Standard_Carrier_Alpha_CD and Equipment_Unit_NBis wrong  " + CurrentPro);
		}

		SA.assertAll();
	}

	@Test(priority = 6, dataProvider = "2000.06 FOR CL", dataProviderClass = DataForUS200006.class)
	public void VerifyBatchProForFoodTrailer(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
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
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		ArrayList<String> Addpro = new ArrayList<String>();
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "pois");
		page.RemoveProButton.click();
		for (int i = 0; i < 2; i++) {
			String pro = PRO.get(i);
			page.EnterPro(pro);
			Addpro.add(pro);
		}
		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"There was an error while adding all your pros to the trailer."));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

		for (int pro = 1; pro <= Addpro.size(); pro++) {
			String CurrentPro = Addpro.get(pro - 1);
			String CurrentProH = page.addHyphenToPro(CurrentPro);
			SA.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[2]/div")).getText(),
					CurrentProH); // check pro
			SA.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[3]/div")).getText(),
					"Bill is POISON. Food already on trailer."); // check error

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
			// message
			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SA.assertFalse(AfterLOADWb.get(0).equals(SCAC) && AfterLOADWb.get(1).equals(TrailerNB),
					"Waybill table Standard_Carrier_Alpha_CD and Equipment_Unit_NBis wrong  " + CurrentPro);
		}

		SA.assertAll();
	}

	@Test(priority = 7, dataProvider = "2000.06 FOR CL", dataProviderClass = DataForUS200006.class)
	public void VerifySingleProForPoisonTrailer(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
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

		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "food");
		for (int i = 0; i < 1; i++) {
			page.RemoveProButton.click();
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			page.SubmitButton1.click();
			(new WebDriverWait(driver, 30)).until(ExpectedConditions.textToBePresentInElement(
					page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div")),
					"Bill is FOOD. Poison already on trailer."));

			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SA.assertFalse(AfterLOADWb.get(0).equals(SCAC) && AfterLOADWb.get(1).equals(TrailerNB),
					"Waybill table Standard_Carrier_Alpha_CD and Equipment_Unit_NBis wrong  " + CurrentPro);
		}

		SA.assertAll();
	}

	@Test(priority = 8, dataProvider = "2000.06 FOR CL", dataProviderClass = DataForUS200006.class)
	public void VerifyBatchProForPoisonTrailer(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
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
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		ArrayList<String> Addpro = new ArrayList<String>();
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "food");
		page.RemoveProButton.click();
		for (int i = 0; i < 2; i++) {
			String pro = PRO.get(i);
			page.EnterPro(pro);
			Addpro.add(pro);
		}
		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"There was an error while adding all your pros to the trailer."));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		for (int pro = 1; pro <= Addpro.size(); pro++) {
			String CurrentPro = Addpro.get(pro - 1);
			String CurrentProH = page.addHyphenToPro(CurrentPro);
			SA.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[2]/div")).getText(),
					CurrentProH); // check pro
			SA.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[3]/div")).getText(),
					"Bill is FOOD. Poison already on trailer."); // check error
																	// message

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

			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SA.assertFalse(AfterLOADWb.get(0).equals(SCAC) && AfterLOADWb.get(1).equals(TrailerNB),
					"Waybill table Standard_Carrier_Alpha_CD and Equipment_Unit_NBis wrong  " + CurrentPro);
		}
		SA.assertAll();
	}

	@Test(priority = 9, dataProvider = "CL trailer with both food and pois", dataProviderClass = DataForUS200006.class)
	public void VerifySinglePoisProForTrailerWithBothFOODandPOIS(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
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

		// List<ArrayList<String>>
		// PROonTrailerBeforeAdd=DataForUS200006.GetProList(SCAC,TrailerNB);
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "pois");
		for (int i = 0; i < 1; i++) {
			page.RemoveProButton.click();
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			page.SubmitButton1.click();
			w1.until(ExpectedConditions.textToBePresentInElement(
					page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div")),
					"Bill is POISON. Food already on trailer."));

			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SA.assertFalse(AfterLOADWb.get(0).equals(SCAC) && AfterLOADWb.get(1).equals(TrailerNB),
					"Waybill table Standard_Carrier_Alpha_CD and Equipment_Unit_NBis wrong  " + CurrentPro);
		}

		SA.assertAll();
	}

	@Test(priority = 10, dataProvider = "CL trailer with both food and pois", dataProviderClass = DataForUS200006.class)
	public void VerifyBatchPoisProForTrailerWithBothFOODandPOIS(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
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
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		ArrayList<String> Addpro = new ArrayList<String>();
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "pois");
		page.RemoveProButton.click();
		for (int i = 0; i < 2; i++) {
			String pro = PRO.get(i);
			page.EnterPro(pro);
			Addpro.add(pro);
		}
		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"There was an error while adding all your pros to the trailer."));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

		for (int pro = 1; pro <= Addpro.size(); pro++) {
			String CurrentPro = Addpro.get(pro - 1);
			String CurrentProH = page.addHyphenToPro(CurrentPro);
			SA.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[2]/div")).getText(),
					CurrentProH); // check pro
			SA.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[3]/div")).getText(),
					"Bill is POISON. Food already on trailer."); // check error
																	// message

			// check eqps
			ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
			SA.assertEquals(NewEqpStatusRecord.get(0), "CL", "Equipment_Status_Type_CD is wrong");
			SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
			SA.assertEquals(NewEqpStatusRecord.get(3), "CL", "Source_Create_ID is wrong");
			SA.assertEquals(NewEqpStatusRecord.get(21), SetCityRtype, "City_Route_Type_NM is wrong");
			SA.assertEquals(NewEqpStatusRecord.get(22), NewCityRoute, "City_Route_NM is wrong");
			int[] TimeElement = { 5, 6, 7, 8, 20 };
			for (int i1 : TimeElement) {
				Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i1));
				if (i1 == 7) {
					SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
							"equipment_status_ts " + "  " + TS + "  " + AlterTime);
				} else if (i1 == 20) {
					SA.assertEquals(TS, PlanDate, "Planned_Delivery_DT is wrong");
				} else {
					SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i1 + "  " + TS + "  " + d);
				}
			}
			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SA.assertFalse(AfterLOADWb.get(0).equals(SCAC) && AfterLOADWb.get(1).equals(TrailerNB),
					"Waybill table Standard_Carrier_Alpha_CD and Equipment_Unit_NBis wrong  " + CurrentPro);
		}

		SA.assertAll();
	}

	@Test(priority = 11, dataProvider = "CL trailer with both food and pois", dataProviderClass = DataForUS200006.class)
	public void VerifySingleFoodProForTrailerWithBothFOODandPOIS(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
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

		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "food");
		for (int i = 0; i < 1; i++) {
			page.RemoveProButton.click();
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			page.SubmitButton1.click();
			(new WebDriverWait(driver, 30)).until(ExpectedConditions.textToBePresentInElement(
					page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div")),
					"Bill is FOOD. Poison already on trailer."));

			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SA.assertFalse(AfterLOADWb.get(0).equals(SCAC) && AfterLOADWb.get(1).equals(TrailerNB),
					"Waybill table Standard_Carrier_Alpha_CD and Equipment_Unit_NBis wrong  " + CurrentPro);
		}

		SA.assertAll();
	}

	@Test(priority = 12, dataProvider = "CL trailer with both food and pois", dataProviderClass = DataForUS200006.class)
	public void VerifyBatchFoodProTrailerWithBothFOODandPOIS(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
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
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		ArrayList<String> Addpro = new ArrayList<String>();
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "food");
		page.RemoveProButton.click();
		for (int i = 0; i < 2; i++) {
			String pro = PRO.get(i);
			page.EnterPro(pro);
			Addpro.add(pro);
		}
		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"There was an error while adding all your pros to the trailer."));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		for (int pro = 1; pro <= Addpro.size(); pro++) {
			String CurrentPro = Addpro.get(pro - 1);
			String CurrentProH = page.addHyphenToPro(CurrentPro);
			SA.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[2]/div")).getText(),
					CurrentProH); // check pro
			SA.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[3]/div")).getText(),
					"Bill is FOOD. Poison already on trailer."); // check error
																	// message

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

			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SA.assertFalse(AfterLOADWb.get(0).equals(SCAC) && AfterLOADWb.get(1).equals(TrailerNB),
					"Waybill table Standard_Carrier_Alpha_CD and Equipment_Unit_NBis wrong  " + CurrentPro);
		}
		SA.assertAll();
	}
}