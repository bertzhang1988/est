package TestCase.MTYtesting;

import java.awt.AWTException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;

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
import Function.SetupBrowser;
import Function.Utility;
import Page.EqpStatusPageS;

public class MTYScreenTesting extends SetupBrowser {

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
		page.SetStatus("MTY");
	}

	@Test(priority = 3, dataProvider = "MTYScreen", dataProviderClass = DataForMTYScreenTesting.class)
	public void twoBlOBRDockFromONH(String terminalcd, String SCAC, String TrailerNB, String Desti, String Cube,
			String AmountPro, String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		// check pre populate
		SA.assertEquals(page.TerminalField.getAttribute("value"), terminalcd, " mty two button lobr terminal is wrong");
		SA.assertEquals(page.TrailerField.getText(), page.SCACTrailer(SCAC, TrailerNB),
				"MTY two button lobr trailer input is wrong");
		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SA.assertEquals(ProInfo, DataCommon.GetProListLOBR(SCAC, TrailerNB), "pro grid is wrong");
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), 0, 59);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// page.LobrCancelButton.click();
		ArrayList<String> ProOnTrailer = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		page.HandleLOBRproAll("Dock");
		Date d = CommonFunction.gettime("UTC");

		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Empty"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "MTY", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "MTY", "Source_Create_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		// CHECK WAYBILL
		for (String RemovedPro : ProOnTrailer) {
			ArrayList<Object> AfterRemoveWb = DataCommon.GetWaybillInformationOfPro(RemovedPro);
			SA.assertTrue(AfterRemoveWb.get(0) == null, "waybill scac is wrong  " + RemovedPro);
			SA.assertTrue(AfterRemoveWb.get(1) == null, "waybill equipment_unit_nb is wrong  " + RemovedPro);
			SA.assertEquals(AfterRemoveWb.get(3), "LOBR", "waybill source_modify_id is wrong  " + RemovedPro);
			// SA.assertEquals(AfterRemoveWb.get(7),BeforeRemoveWb.get(7),""+RemovedPro+"
			// Create_TS is wrong");
			// SA.assertEquals(AfterRemoveWb.get(8),BeforeRemoveWb.get(8),""+RemovedPro+"
			// System_Insert_TS is wrong");
			Date f = CommonFunction.SETtime((Date) AfterRemoveWb.get(9));
			SA.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table waybill system_modify_ts  " + f + "  " + d + "  " + "   " + RemovedPro);
			// SA.assertEquals(AfterRemoveWb.get(12),BeforeRemoveWb.get(12),"waybill
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
		SA.assertAll();
	}

	@Test(priority = 2, dataProvider = "MTYScreen", dataProviderClass = DataForMTYScreenTesting.class)
	public void twoBlOBRAllShortFromONH(String terminalcd, String SCAC, String TrailerNB, String Desti, String Cube,
			String AmountPro, String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		// check pre populate
		SA.assertEquals(page.TerminalField.getAttribute("value"), terminalcd, " mty two button lobr terminal is wrong");
		SA.assertEquals(page.TrailerField.getText(), page.SCACTrailer(SCAC, TrailerNB),
				"MTY two button lobr trailer input is wrong");
		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SA.assertEquals(ProInfo, DataCommon.GetProListLOBR(SCAC, TrailerNB), "pro grid is wrong");
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -1, 0);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// page.LobrCancelButton.click();
		// page.LobrCancelButton.click();
		ArrayList<String> ProOnTrailer = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		page.HandleLOBRproAll("allshort");
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Empty"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "MTY", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "MTY", "Source_Create_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		// CHECK WAYBILL
		for (String RemovedPro : ProOnTrailer) {
			ArrayList<Object> AfterRemoveWb = DataCommon.GetWaybillInformationOfPro(RemovedPro);
			SA.assertTrue(AfterRemoveWb.get(0) == null, "waybill scac is wrong  " + RemovedPro);
			SA.assertTrue(AfterRemoveWb.get(1) == null, "waybill equipment_unit_nb is wrong  " + RemovedPro);
			SA.assertEquals(AfterRemoveWb.get(3), "LOBR", "waybill source_modify_id is wrong  " + RemovedPro);
			// SA.assertEquals(AfterRemoveWb.get(7),BeforeRemoveWb.get(7),""+RemovedPro+"
			// Create_TS is wrong");
			// SA.assertEquals(AfterRemoveWb.get(8),BeforeRemoveWb.get(8),""+RemovedPro+"
			// System_Insert_TS is wrong");
			Date f = CommonFunction.SETtime((Date) AfterRemoveWb.get(9));
			SA.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table waybill system_modify_ts  " + f + "  " + d + "  " + "   " + RemovedPro);
			// SA.assertEquals(AfterRemoveWb.get(12),BeforeRemoveWb.get(12),"waybill
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
		SA.assertAll();
	}

	@Test(priority = 1, dataProvider = "MTYScreen", dataProviderClass = DataForMTYScreenTesting.class)
	public void SetTrailerWithoutProToMtyFromONH(String terminalcd, String SCAC, String TrailerNB, String Desti,
			String Cube, String AmountPro, String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		// Check date and time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MRSts);
		SA.assertEquals(picker, expect, "MTY screen prepopulate time is wrong ");

		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -1, -34);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		page.SubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "MTY", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "MTY", "Source_Create_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(16), conf.GetAD_ID(), "modify_id is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(17), conf.GetM_ID(), "eqps Mainframe_User_ID is wrong");
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

	@Test(priority = 4, dataProvider = "MTYScreen", dataProviderClass = DataForMTYScreenTesting.class)
	public void SmartEneterSetTrailerWithoutProToMtyFromONH(String terminalcd, String SCAC, String TrailerNB,
			String Desti, String Cube, String AmountPro, String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		Actions builder = new Actions(driver);
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -1, -59);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		builder.sendKeys(Keys.ENTER).build().perform();

		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "MTY", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "MTY", "Source_Create_ID is wrong");
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

	@Test(priority = 5, dataProvider = "MTYScreen", dataProviderClass = DataForMTYScreenTesting.class)
	public void SetTrailerWithoutProNotFromONHToMty(String terminalcd, String SCAC, String TrailerNB, String Desti,
			String Cube, String AmountPro, String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		Date CurrentTime = CommonFunction.gettime("UTC");

		// Check date and time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MRSts);
		SA.assertEquals(picker, expect, "MTY screen prepopulate time is wrong ");

		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -1, -9);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		page.SubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "MTY", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "MTY", "Source_Create_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(16), conf.GetAD_ID(), "modify_id is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(17), conf.GetM_ID(), "eqps Mainframe_User_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}
		// check eqp
		ArrayList<Object> NewEqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		SA.assertEquals(NewEqp.get(0), conf.GetM_ID(), " eqp Mainframe_User_ID is wrong");
		SA.assertAll();
	}

	@Test(priority = 6, dataProvider = "MTYScreen", dataProviderClass = DataForMTYScreenTesting.class)
	public void twoBlOBRDockNOTFromONH(String terminalcd, String SCAC, String TrailerNB, String Desti, String Cube,
			String AmountPro, String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		// check pre populate
		SA.assertEquals(page.TerminalField.getAttribute("value"), terminalcd, " mty two button lobr terminal is wrong");
		SA.assertEquals(page.TrailerField.getText(), page.SCACTrailer(SCAC, TrailerNB),
				"MTY two button lobr trailer input is wrong");
		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SA.assertEquals(ProInfo, DataCommon.GetProListLOBR(SCAC, TrailerNB), "pro grid is wrong");
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -1, -6);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// page.LobrCancelButton.click();
		ArrayList<String> ProOnTrailer = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		page.HandleLOBRproAll("Dock");
		Date d = CommonFunction.gettime("UTC");

		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Empty"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "MTY", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "MTY", "Source_Create_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		// CHECK WAYBILL
		for (String RemovedPro : ProOnTrailer) {
			ArrayList<Object> AfterRemoveWb = DataCommon.GetWaybillInformationOfPro(RemovedPro);
			SA.assertTrue(AfterRemoveWb.get(0) == null, "waybill scac is wrong  " + RemovedPro);
			SA.assertTrue(AfterRemoveWb.get(1) == null, "waybill equipment_unit_nb is wrong  " + RemovedPro);
			SA.assertEquals(AfterRemoveWb.get(3), "LOBR", "waybill source_modify_id is wrong  " + RemovedPro);
			// SA.assertEquals(AfterRemoveWb.get(7),BeforeRemoveWb.get(7),""+RemovedPro+"
			// Create_TS is wrong");
			// SA.assertEquals(AfterRemoveWb.get(8),BeforeRemoveWb.get(8),""+RemovedPro+"
			// System_Insert_TS is wrong");
			Date f = CommonFunction.SETtime((Date) AfterRemoveWb.get(9));
			SA.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table waybill system_modify_ts  " + f + "  " + d + "  " + "   " + RemovedPro);
			// SA.assertEquals(AfterRemoveWb.get(12),BeforeRemoveWb.get(12),"waybill
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
		SA.assertAll();
	}

	@Test(priority = 7, dataProvider = "MTYScreen", dataProviderClass = DataForMTYScreenTesting.class)
	public void twoBlOBRAllShortNOTFromONH(String terminalcd, String SCAC, String TrailerNB, String Desti, String Cube,
			String AmountPro, String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		// check pre populate
		SA.assertEquals(page.TerminalField.getAttribute("value"), terminalcd, " mty two button lobr terminal is wrong");
		SA.assertEquals(page.TrailerField.getText(), page.SCACTrailer(SCAC, TrailerNB),
				"MTY two button lobr trailer input is wrong");
		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SA.assertEquals(ProInfo, DataCommon.GetProListLOBR(SCAC, TrailerNB), "pro grid is wrong");
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -1, -45);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// page.LobrCancelButton.click();
		// page.LobrCancelButton.click();
		ArrayList<String> ProOnTrailer = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		page.HandleLOBRproAll("allshort");
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Empty"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "MTY", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "MTY", "Source_Create_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		// CHECK WAYBILL
		for (String RemovedPro : ProOnTrailer) {
			ArrayList<Object> AfterRemoveWb = DataCommon.GetWaybillInformationOfPro(RemovedPro);
			SA.assertTrue(AfterRemoveWb.get(0) == null, "waybill scac is wrong  " + RemovedPro);
			SA.assertTrue(AfterRemoveWb.get(1) == null, "waybill equipment_unit_nb is wrong  " + RemovedPro);
			SA.assertEquals(AfterRemoveWb.get(3), "LOBR", "waybill source_modify_id is wrong  " + RemovedPro);
			// SA.assertEquals(AfterRemoveWb.get(7),BeforeRemoveWb.get(7),""+RemovedPro+"
			// Create_TS is wrong");
			// SA.assertEquals(AfterRemoveWb.get(8),BeforeRemoveWb.get(8),""+RemovedPro+"
			// System_Insert_TS is wrong");
			Date f = CommonFunction.SETtime((Date) AfterRemoveWb.get(9));
			SA.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table waybill system_modify_ts  " + f + "  " + d + "  " + "   " + RemovedPro);
			// SA.assertEquals(AfterRemoveWb.get(12),BeforeRemoveWb.get(12),"waybill
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
		SA.assertAll();
	}

	@Test(priority = 8, dataProvider = "MTYScreen", dataProviderClass = DataForMTYScreenTesting.class)
	public void SmartEneterSetTrailerWithoutProToMtyNOTFromONH(String terminalcd, String SCAC, String TrailerNB,
			String Desti, String Cube, String AmountPro, String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		Actions builder = new Actions(driver);
		page.SetLocation(terminalcd);
		Date d1 = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		// check time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, d1, MRSts);
		SA.assertEquals(picker, expect, "prepopulate time is wrong, get from picker ");

		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), 0, 59);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		builder.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "MTY", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "MTY", "Source_Create_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		// check eqp
		ArrayList<Object> NewEqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		SA.assertEquals(NewEqp.get(0), conf.GetM_ID(), " eqp Mainframe_User_ID is wrong");
		SA.assertAll();
	}

	@AfterMethod()
	public void getbackldg(ITestResult result) throws InterruptedException {
		if (result.getStatus() == ITestResult.FAILURE) {

			String Testparameter = Arrays.toString(Arrays.copyOf(result.getParameters(), 3)).replaceAll("[^\\d.a-zA-Z]",
					"");
			String FailureTestparameter = result.getName() + Testparameter;

			Utility.takescreenshot(driver, FailureTestparameter);
			page.SetStatus("MTY");
		}
	}
}
