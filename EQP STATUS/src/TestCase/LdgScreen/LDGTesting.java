package TestCase.LdgScreen;

import java.awt.AWTException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Random;

import org.openqa.selenium.By;
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

public class LDGTesting extends Setup {
	private EqpStatusPageS page;
	private WebDriverWait w1;
	private WebDriverWait w2;

	@BeforeClass(groups = { "ldg uc" })
	public void SetUp() throws AWTException, InterruptedException, IOException {
		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 50);
		w2 = new WebDriverWait(driver, 180);
		  
		driver.manage().window().maximize();
		page.SetStatus("ldg");
	}

	@Test(priority = 1, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class, description = "trailer can transit to ldg but not in ldg without pro", groups = {
			"ldg uc" })
	public void SetTrailerToLoadingNoShipments(String terminalcd, String SCAC, String TrailerNB, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		// check date&time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MReqpst);
		SA.assertEquals(picker, expect, "ldg screen prepopulate time is wrong ");

		// change destination
		String changeDesti = page.ChangeDestiantion();

		// set date&time
		page.SetDatePicker(page.GetDatePickerTime(), -23);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// CLICK SUBMIT
		page.SubmitButton1.click();

		Date d = CommonFunction.gettime("UTC");

		w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Trailer " + page.SCACTrailer(SCAC, TrailerNB) + " updated to LDG"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

		Thread.sleep(5000);
		// check new record of eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG", "Source_Create_ID is wrong");
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

	@Test(priority = 2, dataProvider = "ldgscreen2", dataProviderClass = DataForUSLDGLifeTest.class, description = "trailer in ldg with pro, change cube", groups = {
			"ldg uc" })
	public void LDGWithProChangeCube(String terminalcd, String SCAC, String TrailerNB, String Desti, String AmountPro,
			String AmountWeight, String Cube, String hldesti, String hlcube, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		ArrayList<Object> OldEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		// check trailer value prepopulate
		SA.assertEquals(page.DestinationField.getAttribute("value").replaceAll("_", ""), Desti, "destination is wrong");
		SA.assertEquals(page.CubeField.getAttribute("value"), Cube, "cube is wrong");
		SA.assertEquals(page.ShipCount1.getText(), AmountPro, "shipcount is wrong");
		SA.assertEquals(page.ShipWeight1.getText(), AmountWeight, "ship weight is wrong");
		SA.assertEquals(page.HLDestLdg.getText(), hldesti, "headload dest is wrong");
		SA.assertEquals(page.HLCubeLdg.getText(), hlcube, "headload cube is wrong");

		// check date&time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, MRSts);
		SA.assertEquals(picker, expect, "ldg screen prepopulate time is wrong ");

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListForm);
		SA.assertEquals(ProInfo, DataCommon.GetProListLD(SCAC, TrailerNB), "pro grid is wrong");

		// enter cube
		String NewCube = page.ChangeCube();

		// click submit
		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");

		w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Trailer " + page.SCACTrailer(SCAC, TrailerNB) + " updated to LDG"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

		Thread.sleep(2000);
		// check eqps update only modify_ts update
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), OldEqpStatusRecord.get(3), "Source_Create_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(19), "LH.LDG", "Source_Modify_ID is wrong");
		Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(5));
		SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, "modify_ts is wrong  " + TS + "  " + d);
		for (int i = 6; i <= 8; i++) {
			SA.assertEquals(NewEqpStatusRecord.get(i), OldEqpStatusRecord.get(i),
					i + "  " + NewEqpStatusRecord.get(i) + "  " + OldEqpStatusRecord.get(i));
		}

		SA.assertAll();
	}

	@Test(priority = 3, dataProvider = "ldgscreen2", dataProviderClass = DataForUSLDGLifeTest.class, description = "trailer in ldg without pro, change destination", groups = {
			"ldg uc" })
	public void LDGWithoutProChangeDestination(String terminalcd, String SCAC, String TrailerNB, String Desti,
			String AmountPro, String AmountWeight, String Cube, String hldesti, String hlcube, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// check trailer value prepopulate
		SA.assertEquals(page.DestinationField.getAttribute("value").replaceAll("_", ""), Desti, "destination is wrong");
		SA.assertEquals(page.CubeField.getAttribute("value"), Cube, "cube is wrong");
		SA.assertEquals(page.ShipCount1.getText(), AmountPro, "shipcount is wrong");
		SA.assertEquals(page.ShipWeight1.getText(), AmountWeight, "ship weight is wrong");
		SA.assertEquals(page.HLDestLdg.getText(), hldesti, "headload dest is wrong");
		SA.assertEquals(page.HLCubeLdg.getText(), hlcube, "headload cube is wrong");

		// check date&time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, MRSts);
		SA.assertEquals(picker, expect, "ldg screen prepopulate time is wrong ");

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListForm);
		SA.assertEquals(ProInfo, DataCommon.GetProListLD(SCAC, TrailerNB), "pro grid is wrong");

		// change destination
		String changeDesti = page.ChangeDestiantion();

		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// click submit
		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Trailer " + page.SCACTrailer(SCAC, TrailerNB) + " updated to LDG"));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

		// check eqps new record create
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG", "Source_Create_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(16), conf.GetAD_ID(), "modify_id is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(17), conf.GetM_ID(), "eqps Mainframe_User_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts is wrong  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + " " + TS + "  " + d);
			}
		}
		// check eqp
		ArrayList<Object> NewEqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		SA.assertEquals(NewEqp.get(0), conf.GetM_ID(), " eqp Mainframe_User_ID is wrong");
		SA.assertAll();
	}

	@Test(priority = 4, dataProvider = "ldgscreen2", dataProviderClass = DataForUSLDGLifeTest.class, description = "ldg trailer with pro change destination, headload pro, click yes", groups = {
			"ldg uc" })
	public void LDGTrailerWithProChangeDestination(String terminalcd, String SCAC, String TrailerNB, String OrgiDesti,
			String AmountPro, String AmountWeight, String cube, String hldesti, String hlcube, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// check trailer value prepopulate
		SA.assertEquals(page.DestinationField.getAttribute("value").replaceAll("_", ""), OrgiDesti,
				"destination is wrong");
		SA.assertEquals(page.CubeField.getAttribute("value"), cube, "cube is wrong");
		SA.assertEquals(page.ShipCount1.getText(), AmountPro, "shipcount is wrong");
		SA.assertEquals(page.ShipWeight1.getText(), AmountWeight, "ship weight is wrong");
		SA.assertEquals(page.HLDestLdg.getText(), hldesti, "headload dest is wrong");
		SA.assertEquals(page.HLCubeLdg.getText(), hlcube, "headload cube is wrong");

		// check date&time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, MRSts);
		SA.assertEquals(picker, expect, "ldg screen prepopulate time is wrong ");

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListForm);
		SA.assertEquals(ProInfo, DataCommon.GetProListLD(SCAC, TrailerNB), "pro grid is wrong");

		// input cube if there is no cube value before
		if (page.CubeField.getAttribute("value").equalsIgnoreCase("")) {
			page.ChangeCube();
		}
		// change destination
		String changeDesti = page.ChangeDestiantion();

		// screen navigate to headload
		w2.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Mark PROs as Headload"));
		w2.until(ExpectedConditions.visibilityOf(page.HeadloadProForm));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// check headload screen
		SA.assertEquals(page.HeadloadDestination.getAttribute("value").replaceAll("_", ""), OrgiDesti,
				"screenHldestination is wrong");
		SA.assertEquals(page.HLCubeField.getAttribute("value"), cube, "screenHLcube is wrong");
		SA.assertEquals(page.ManifestToField.getText(), OrgiDesti, "screen manifest dest is wrong");
		SA.assertEquals(page.DestinationField.getAttribute("value"), changeDesti, "screen desti is wrong");
		SA.assertEquals(page.CubeField.getAttribute("value"), cube, "screen cube is wrong");

		// CHECK headload pro grid
		LinkedHashSet<ArrayList<String>> HLProInfo = page.GetProList(page.HeadloadProForm);
		SA.assertEquals(HLProInfo, DataCommon.GetHLProList(SCAC, TrailerNB), "HL pro grid is wrong");
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		ArrayList<ArrayList<Object>> WbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		page.YesButton.click();// click yes
		Date d = CommonFunction.gettime("UTC");
		// (new WebDriverWait(driver,
		// 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		// (new WebDriverWait(driver,
		// 50)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,"Trailer
		// "+SCAC+TrailerNB+" updated to LDG"));
		w2.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// (new WebDriverWait(driver,
		// 150)).until(ExpectedConditions.visibilityOf(page.AddProForm));
		Thread.sleep(3000);
		// check eqpststus new record
		ArrayList<Object> NewEQPStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEQPStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong"); // equipment_dest_facility_cd
		SA.assertEquals(NewEQPStatusRecord.get(12), cube, "Headload_Capacity_Consumed_PC is wring"); // Headload_Capacity_Consumed_PC
		SA.assertEquals(NewEQPStatusRecord.get(13), OrgiDesti, "headload_dest_facility_cd is wrong"); // headload_dest_facility_cd
		SA.assertEquals(NewEQPStatusRecord.get(14), terminalcd, "headload_origin_facility_CD is wrong"); // headload_origin_facility_CD

		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEQPStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts is wrong  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + " " + TS + "  " + d);
			}
		}
		// check waybill
		ArrayList<ArrayList<Object>> NewWbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		int i = 0;
		Date f2 = null;
		for (ArrayList<Object> Currentwbti : NewWbtRecord) {
			SA.assertEquals(Currentwbti.get(1), "Y", Currentwbti.get(0) + "  Headload_IN is wrong"); // Headload_IN
			Date TS = CommonFunction.SETtime((Date) Currentwbti.get(3));
			SA.assertEquals(Currentwbti.get(3), WbtRecord.get(i).get(3),
					Currentwbti.get(0) + " Waybill_Transaction_End_TS is wrong");
			SA.assertEquals(Currentwbti.get(2), WbtRecord.get(i).get(2),
					Currentwbti.get(0) + " manifest_destination is wrong");
			SA.assertEquals(Currentwbti.get(4), "LH.LDG", Currentwbti.get(0) + " source_modify_ID is wrong");
			// CHECK THE WAYBILL TRANSACTION END TS ARE NOT IDENTICAL
			// if(i>0) SA.assertTrue(TS.after(f2),"waybill
			// Waybill_Transaction_End_TS is not ascending increase :
			// waybill_transaction_end_ts "+TS+" waybill_transaction_end_ts of
			// previous pro is "+f2+" "+" "+Currentwbti.get(0));
			f2 = TS;
			i = i + 1;

		}

		// check trailer value in ldg screen again
		SA.assertEquals(page.DestinationField.getAttribute("value").replaceAll("_", ""), changeDesti,
				"destination is wrong");
		SA.assertEquals(page.CubeField.getAttribute("value"), cube, "cube is wrong");
		SA.assertEquals(page.ShipCount1.getText(), AmountPro, "shipcount is wrong");
		SA.assertEquals(page.ShipWeight1.getText(), AmountWeight, "ship weight is wrong");
		SA.assertEquals(page.HLDestLdg.getText(), OrgiDesti, "headload dest is wrong");
		SA.assertEquals(page.HLCubeLdg.getText(), cube, "headload cube is wrong");

		// check pro grid in ldg screen again

		LinkedHashSet<ArrayList<String>> ProInfo1 = page.GetProList(page.ProListForm);
		SA.assertEquals(ProInfo1, DataCommon.GetProListLD(SCAC, TrailerNB), "pro grid is wrong");

		SA.assertAll();

	}

	@Test(priority = 5, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class, description = "can transit to ldg not in ldg and ldd with pro, lobr, Dock", groups = {
			"ldg uc" })
	public void SetTrailerToLDGWithProAndDock(String terminalcd, String SCAC, String TrailerNB, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// check date&time pre populate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MReqpst);
		SAssert.assertEquals(picker, expect, "lobr screen prepopulate time is wrong ");

		// check lobr pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SAssert.assertEquals(ProInfo, DataCommon.GetProListLOBR(SCAC, TrailerNB), " lobr pro grid is wrong");
		ArrayList<String> prolistbeforelobr = DataCommon.GetProOnTrailer(SCAC, TrailerNB);

		// change destination
		String changeDesti = page.ChangeDestiantion();

		// enter cube
		String NewCube = page.ChangeCube();

		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// handle the left pro
		page.HandleLOBRproAll("Dock");
		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w2.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG", "Source_Create_ID is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SAssert.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts is wrong  " + TS + "  " + AlterTime);
			} else {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + " " + TS + "  " + d);
			}
		}

		// check waybill
		for (String RemovedPro : prolistbeforelobr) {
			ArrayList<Object> AfterRemoveWb = DataCommon.GetWaybillInformationOfPro(RemovedPro);
			SAssert.assertTrue(AfterRemoveWb.get(0) == null, "waybill scac is wrong");
			SAssert.assertTrue(AfterRemoveWb.get(1) == null, "waybill equipment_unit_nb is wrong");
			SAssert.assertEquals(AfterRemoveWb.get(3), "LOBR", "waybill source_modify_id is wrong");
			// SAssert.assertEquals(AfterRemoveWb.get(7),BeforeRemoveWb.get(7),""+RemovedPro+"
			// Create_TS is wrong");
			// SAssert.assertEquals(AfterRemoveWb.get(8),BeforeRemoveWb.get(8),""+RemovedPro+"
			// System_Insert_TS is wrong");
			Date f = CommonFunction.SETtime((Date) AfterRemoveWb.get(9));
			SAssert.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table waybill system_modify_ts  " + f + "  " + d + "  " + "   " + RemovedPro);
			// SAssert.assertEquals(AfterRemoveWb.get(12),BeforeRemoveWb.get(12),"waybill
			// table record_key is wrong "+RemovedPro);
			SAssert.assertTrue(AfterRemoveWb.get(13) == null, "waybill  From_Facility_CD is wrong" + RemovedPro);
			SAssert.assertEquals(AfterRemoveWb.get(14), terminalcd, "waybill To_Facility_CD is wrong" + RemovedPro);
			SAssert.assertEquals(AfterRemoveWb.get(15), SCAC,
					"waybill  From_Standard_Carrier_Alpha_CD is wrong" + RemovedPro);
			SAssert.assertEquals(AfterRemoveWb.get(16), TrailerNB,
					"waybill  From_Equipment_Unit_NB is wrong" + RemovedPro);
			SAssert.assertTrue(AfterRemoveWb.get(17) == null,
					"waybill  To_Standard_Carrier_Alpha_CD is wrong" + RemovedPro);
			SAssert.assertTrue(AfterRemoveWb.get(18) == null, "waybill  To_Equipment_Unit_NB is wrong" + RemovedPro);
			SAssert.assertEquals(AfterRemoveWb.get(20), "UNLOADING",
					"waybill  Waybill_Transaction_Type_NM is wrong" + RemovedPro);
			Date f1 = CommonFunction.SETtime((Date) AfterRemoveWb.get(11));
			SAssert.assertTrue(Math.abs(f1.getTime() - d.getTime()) < 120000,
					"waybill table Waybill_Transaction_End_TS  " + f1 + "  " + d + "  " + "   " + RemovedPro);

		}

		// check ldg screen
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti,
				"loading screen destination is wrong");
		SAssert.assertFalse(page.DateInput.isEnabled(), "date&time field is not disabled");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), NewCube, "loading screen Cube field is wrong");

		// check date&time prepopulate
		Date picker2 = page.GetDatePickerTime();
		Date expect2 = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, (Date) NewEqpStatusRecord.get(7));
		SAssert.assertEquals(picker2, expect2, "ldg screen prepopulate time is wrong ");

		// check pro grid in ldg screen again
		LinkedHashSet<ArrayList<String>> ProInfo1 = page.GetProList(page.ProListForm);
		SAssert.assertEquals(DataCommon.GetProListLD(SCAC, TrailerNB), ProInfo1, "pro grid is wrong");
		SAssert.assertAll();
	}

	@Test(priority = 6, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class, description = "can transit to ldg not in ldg and ldd with pro, lobr, headload", groups = {
			"ldg uc" })
	public void SetTrailerToLDGWithProAndHeadLoad(String terminalcd, String SCAC, String TrailerNB, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// check lobr date&time pre populate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MReqpst);
		SAssert.assertEquals(picker, expect, "lobr screen prepopulate time is wrong ");

		// check lobr pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SAssert.assertEquals(ProInfo, DataCommon.GetProListLOBR(SCAC, TrailerNB), " lobr pro grid is wrong");
		ArrayList<String> prolistbeforelobr = DataCommon.GetProOnTrailer(SCAC, TrailerNB);

		// change destination
		String changeDesti = page.ChangeDestiantion();

		// enter cube
		String NewCube = page.ChangeCube();

		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), 1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// handle the left pro
		ArrayList<ArrayList<Object>> WbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		page.HandleLOBRproAll("headload");
		String headload_dest = page.HeadloadDestination.getAttribute("value");
		String headloadCube = page.HeadloadCube.getAttribute("value");
		Date d = CommonFunction.gettime("UTC");

		w2.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		// (new WebDriverWait(driver,
		// 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		Thread.sleep(3000);
		// check eqps new record
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG", "Source_Create_ID is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(12), headloadCube, "headload_cube is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(13), headload_dest, "headload_dest_facility_cd is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SAssert.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts is wrong  " + TS + "  " + AlterTime);
			} else {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + " " + TS + "  " + d);
			}
		}

		// check waybill
		ArrayList<ArrayList<Object>> NewWbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		int i = 0;
		Date f2 = null;
		for (ArrayList<Object> Currentwbti : NewWbtRecord) {
			SAssert.assertEquals(Currentwbti.get(1), "Y", Currentwbti.get(0) + "  Headload_IN is wrong"); // Headload_IN
			Date TS = CommonFunction.SETtime((Date) Currentwbti.get(3));
			SAssert.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
					Currentwbti.get(0) + "Waybill_Transaction_End_TS is : " + TS + " set date is " + AlterTime
							+ " Waybill_Transaction_End_TS is wrong");// Waybill_Transaction_End_TS
			SAssert.assertEquals(Currentwbti.get(2), headload_dest,
					Currentwbti.get(0) + " manifest_destination is wrong");// only
																			// work
																			// this
																			// way
																			// in
																			// four
																			// button
																			// lobr
			SAssert.assertEquals(Currentwbti.get(4), "LOBR", Currentwbti.get(0) + " source_modify_ID is wrong");
			// CHECK THE WAYBILL TRANSACTION END TS ARE NOT IDENTICAL
			if (i > 0)
				SAssert.assertTrue(TS.after(f2),
						"waybill Waybill_Transaction_End_TS is not ascending increase : waybill_transaction_end_ts "
								+ TS + "  waybill_transaction_end_ts of previous pro is  " + f2 + "  " + "   "
								+ Currentwbti.get(0));
			f2 = TS;
			i = i + 1;

		}

		// check screen
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti, "");
		SAssert.assertFalse(page.DateInput.isEnabled(), "date&time field is not disabled");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), NewCube, "Cube field is not disabled");

		// check date&time prepopulate
		Date picker2 = page.GetDatePickerTime();
		Date expect2 = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, (Date) NewEqpStatusRecord.get(7));
		SAssert.assertEquals(picker2, expect2, "ldg screen prepopulate time is wrong ");

		// check ldg pro grid
		LinkedHashSet<ArrayList<String>> ProInfo2 = page.GetProList(page.ProListForm);
		SAssert.assertEquals(ProInfo2, DataCommon.GetProListLD(SCAC, TrailerNB), " ldg pro grid is wrong");

		// CHECK THE PRO IS IN SAME ORDER BEFORE LOBR
		ArrayList<String> prolistafterlobr = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		SAssert.assertEquals(prolistafterlobr, prolistbeforelobr, " PRO IS not IN SAME ORDER as BEFORE LOBR");
		SAssert.assertAll();
	}

	@Test(priority = 7, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class, description = "can transit to ldg not in ldg and ldd with pro, lobr, leave on", groups = {
			"ldg uc" })
	public void SetTrailerToLDGWithProAndLeaveOn(String terminalcd, String SCAC, String TrailerNB, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// check lobr date&time pre populate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MReqpst);
		SAssert.assertEquals(picker, expect, "lobr screen prepopulate time is wrong ");

		// check lobr pro gtrid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SAssert.assertEquals(ProInfo, DataCommon.GetProListLOBR(SCAC, TrailerNB), " lobr pro grid is wrong");
		ArrayList<String> prolistbeforelobr = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		// change destination
		String changeDesti = page.ChangeDestiantion();

		// enter cube
		String NewCube = page.ChangeCube();

		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), 1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// handle the left pro
		page.HandleLOBRproAll("leaveON");
		Date d = CommonFunction.gettime("UTC");
		// (new WebDriverWait(driver,
		// 50)).until(ExpectedConditions.elementToBeClickable(
		// page.LobrSubmitButton));
		// page.LobrSubmitButton.click();

		w2.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		// (new WebDriverWait(driver,
		// 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG", "Source_Create_ID is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SAssert.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts is wrong  " + TS + "  " + AlterTime);
			} else {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + " " + TS + "  " + d);
			}
		}

		// check waybill
		Date f2 = null;
		for (int i = 0; i < prolistbeforelobr.size(); i++) {
			String CurrentPro = prolistbeforelobr.get(i);
			ArrayList<Object> AfterADDWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SAssert.assertEquals(AfterADDWb.get(0), SCAC,
					"Waybill table Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			SAssert.assertEquals(AfterADDWb.get(1), TrailerNB,
					"Waybill table Equipment_Unit_NB is wrong " + CurrentPro);
			SAssert.assertEquals(AfterADDWb.get(3), "LOBR", "Waybill table Source_Modify_ID is wrong " + CurrentPro);
			Date f = CommonFunction.SETtime((Date) AfterADDWb.get(9));
			SAssert.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table system_modify_ts  " + f + "  " + d + "  " + "   " + CurrentPro);

			// loading record
			SAssert.assertEquals(AfterADDWb.get(14), null,
					"waybill table loading record To_Facility_CD is wrong " + CurrentPro);
			SAssert.assertEquals(AfterADDWb.get(13), terminalcd,
					"waybill table loading record From_Facility_CD is wrong " + CurrentPro);
			SAssert.assertEquals(AfterADDWb.get(17), SCAC,
					"waybill table loading record To_Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			SAssert.assertEquals(AfterADDWb.get(18), TrailerNB,
					"waybill table loading record To_Equipment_Unit_NB is wrong " + CurrentPro);
			SAssert.assertEquals(AfterADDWb.get(15), null,
					"waybill table loading record From_Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			SAssert.assertEquals(AfterADDWb.get(16), null,
					"waybill table loading record From_Equipment_Unit_NB is wrong " + CurrentPro);
			SAssert.assertEquals(AfterADDWb.get(20), "LOADING",
					"waybill table loading Waybill_Transaction_Type_NM is wrong " + CurrentPro);

			Date f1 = CommonFunction.SETtime((Date) AfterADDWb.get(11));
			SAssert.assertTrue(Math.abs(f1.getTime() - AlterTime.getTime()) < 60000,
					"waybill Waybill_Transaction_End_TS is  " + f1 + " set date is : " + AlterTime + "  " + "   "
							+ CurrentPro);
			// CHECK THE WAYBILL TRANSACTION END TS ARE NOT IDENTICAL
			if (i > 0)
				SAssert.assertTrue(f1.after(f2),
						"waybill Waybill_Transaction_End_TS is not ascending increase : waybill_transaction_end_ts "
								+ f1 + "  waybill_transaction_end_ts of previous pro is  " + f2 + "  " + "   "
								+ CurrentPro);
			f2 = f1;
		}

		// check screen
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti, "");
		SAssert.assertFalse(page.DateInput.isEnabled(), "date&time field is not disabled");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), NewCube, "Cube field is not disabled");

		// check date&time prepopulate
		Date picker2 = page.GetDatePickerTime();
		Date expect2 = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, (Date) NewEqpStatusRecord.get(7));
		SAssert.assertEquals(picker2, expect2, "ldg screen prepopulate time is wrong ");

		// check pro grid in ldg screen again
		LinkedHashSet<ArrayList<String>> ProInfo1 = page.GetProList(page.ProListForm);
		SAssert.assertEquals(ProInfo1, DataCommon.GetProListLD(SCAC, TrailerNB), "pro grid is wrong");

		// CHECK THE PRO IS IN SAME ORDER BEFORE LOBR
		ArrayList<String> prolistafterlobr = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		SAssert.assertEquals(prolistafterlobr, prolistbeforelobr, " PRO IS not IN SAME ORDER as BEFORE LOBR");
		SAssert.assertAll();

		SAssert.assertAll();
	}

	@Test(priority = 8, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class, description = "can transit to ldg not in ldg and ldd with pro, lobr, all short", groups = {
			"ldg uc" })
	public void SetTrailerToLDGWithProAndALLSHORT(String terminalcd, String SCAC, String TrailerNB, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// check lobr date&time pre populate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MReqpst);
		SAssert.assertEquals(picker, expect, "lobr screen prepopulate time is wrong ");

		// check lobr pro gtrid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SAssert.assertEquals(ProInfo, DataCommon.GetProListLOBR(SCAC, TrailerNB), " lobr pro grid is wrong");
		ArrayList<String> prolistbeforelobr = DataCommon.GetProOnTrailer(SCAC, TrailerNB);

		// change destination
		String changeDesti = page.ChangeDestiantion();

		// enter cube
		String NewCube = page.ChangeCube();

		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), 1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// handle the left pro
		page.HandleLOBRproAll("allshort");
		Date d = CommonFunction.gettime("UTC");

		// (new WebDriverWait(driver,
		// 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG", "Source_Create_ID is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SAssert.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts is wrong  " + TS + "  " + AlterTime);
			} else {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + " " + TS + "  " + d);
			}
		}

		// check waybill
		for (String RemovedPro : prolistbeforelobr) {
			ArrayList<Object> AfterRemoveWb = DataCommon.GetWaybillInformationOfPro(RemovedPro);
			SAssert.assertTrue(AfterRemoveWb.get(0) == null, "waybill scac is wrong");
			SAssert.assertTrue(AfterRemoveWb.get(1) == null, "waybill equipment_unit_nb is wrong");
			SAssert.assertEquals(AfterRemoveWb.get(3), "LOBR", "waybill source_modify_id is wrong");
			// SAssert.assertEquals(AfterRemoveWb.get(7),BeforeRemoveWb.get(7),""+RemovedPro+"
			// Create_TS is wrong");
			// SAssert.assertEquals(AfterRemoveWb.get(8),BeforeRemoveWb.get(8),""+RemovedPro+"
			// System_Insert_TS is wrong");
			Date f = CommonFunction.SETtime((Date) AfterRemoveWb.get(9));
			SAssert.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table waybill system_modify_ts  " + f + "  " + d + "  " + "   " + RemovedPro);
			// SAssert.assertEquals(AfterRemoveWb.get(12),BeforeRemoveWb.get(12),"waybill
			// table record_key is wrong "+RemovedPro);
			SAssert.assertTrue(AfterRemoveWb.get(13) == null, "waybill  From_Facility_CD is wrong" + RemovedPro);
			SAssert.assertEquals(AfterRemoveWb.get(14), terminalcd, "waybill To_Facility_CD is wrong" + RemovedPro);
			SAssert.assertEquals(AfterRemoveWb.get(15), SCAC,
					"waybill  From_Standard_Carrier_Alpha_CD is wrong" + RemovedPro);
			SAssert.assertEquals(AfterRemoveWb.get(16), TrailerNB,
					"waybill  From_Equipment_Unit_NB is wrong" + RemovedPro);
			SAssert.assertTrue(AfterRemoveWb.get(17) == null,
					"waybill  To_Standard_Carrier_Alpha_CD is wrong" + RemovedPro);
			SAssert.assertTrue(AfterRemoveWb.get(18) == null, "waybill  To_Equipment_Unit_NB is wrong" + RemovedPro);
			SAssert.assertEquals(AfterRemoveWb.get(20), "UNLOADING",
					"waybill  Waybill_Transaction_Type_NM is wrong" + RemovedPro);
			Date f1 = CommonFunction.SETtime((Date) AfterRemoveWb.get(11));
			SAssert.assertTrue(Math.abs(f1.getTime() - d.getTime()) < 120000,
					"waybill table Waybill_Transaction_End_TS  " + f1 + "  " + d + "  " + "   " + RemovedPro);

		}

		// check screen
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti,
				"loading screen destination is wrong");
		SAssert.assertFalse(page.DateInput.isEnabled(), "date&time field is not disabled");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), NewCube, "loading screen Cube field is wrong");

		// check date&time prepopulate
		Date picker2 = page.GetDatePickerTime();
		Date expect2 = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, (Date) NewEqpStatusRecord.get(7));
		SAssert.assertEquals(picker2, expect2, "ldg screen prepopulate time is wrong ");

		// check pro grid in ldg screen again
		LinkedHashSet<ArrayList<String>> ProInfo1 = page.GetProList(page.ProListForm);
		SAssert.assertEquals(ProInfo1, DataCommon.GetProListLD(SCAC, TrailerNB), "pro grid is wrong");
		SAssert.assertAll();
	}

	@Test(priority = 11, dataProvider = "3000.05", dataProviderClass = DataForUSLDGLifeTest.class, groups = {
			"ldg uc" })
	public void NoLdgEmptyTrailerAddNonExistingPro(String terminalcd, String SCAC, String TrailerNB, String destination,
			String cube, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		// check destination and cube prepopulate
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), destination, "destiantion display IS WRONG");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), cube, "cube display IS WRONG");

		// check date&time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MReqpst);
		SAssert.assertEquals(picker, expect, "ldg screen prepopulate time is wrong ");

		// change destination
		String changeDesti = page.ChangeDestiantion();

		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// add pro
		ArrayList<String> GetProNotInDB = DataCommon.GenerateProNotInDB();
		ArrayList<String> ADDPRO = new ArrayList<String>();
		page.RemoveProButton.click();
		for (int j = 0; j < 2; j++) {
			String CurrentPro = GetProNotInDB.get(j);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// enter cube
		page.ChangeCube();

		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		Date today = CommonFunction.getDay(d);
		w2.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		// Thread.sleep(3000);
		// System.out.println(page.ErrorAndWarningField.getText());
		// (new WebDriverWait(driver,
		// 80)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,"Trailer
		// "+SCAC+TrailerNB+" updated to LDG"));
		w2.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField, "pro(s) loaded."));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));

		// check eqps

		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG", "Source_Create_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SAssert.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts is wrong  " + TS + "  " + AlterTime);
			} else {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + " " + TS + "  " + d);
			}
		}

		SAssert.assertEquals(NewEqpStatusRecord.get(16), conf.GetAD_ID(), "modify_id is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(17), conf.GetM_ID(), "eqps Mainframe_User_ID is wrong");

		// check added pro
		Thread.sleep(5000);
		for (String pro : ADDPRO) {
			ArrayList<Object> CheckWaybillRecord = DataCommon.GetWaybillInformationOfPro(pro);
			SAssert.assertEquals(CheckWaybillRecord.get(0), SCAC, "waybill SCAC is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(1), TrailerNB, "waybill trailernb is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(17), SCAC, "waybill  toSCAC is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(13), terminalcd, "waybill from terminal is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(18), TrailerNB, "waybill totrailernb is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(20), "LOADING", "waybill TRANSACTION TYPE is wrong");
			Date System_Modify_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(9));
			SAssert.assertTrue(Math.abs(System_Modify_TS.getTime() - d.getTime()) < 120000,
					" waybill table System_Modify_TS " + System_Modify_TS + "  " + d);
			Date Waybill_Transaction_End_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(11));
			SAssert.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime() - d.getTime()) < 120000,
					" waybill table Waybill_Transaction_End_TS " + Waybill_Transaction_End_TS + "  " + d);
		}
		// check eqp
		ArrayList<Object> NewEqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqp.get(0), conf.GetM_ID(), " eqp Mainframe_User_ID is wrong");
		SAssert.assertAll();
	}

	@Test(priority = 12, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class, description = "can transit to ldg not in ldg and ldd with pro, lobr, all short", groups = {
			"ldg uc" })
	public void SetTrailerToLDGWithProAndCombineHandle(String terminalcd, String SCAC, String TrailerNB, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// check time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MReqpst);
		SAssert.assertEquals(picker, expect, "lobr screen prepopulate time is wrong ");

		// check lobr pro gtrid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SAssert.assertEquals(ProInfo, DataCommon.GetProListLOBR(SCAC, TrailerNB), " lobr pro grid is wrong");

		// ENTER DESTINATION IF IT IS BLANK
		String changeDesti = null;
		String desti = page.DestinationField.getAttribute("value");
		if (desti.equalsIgnoreCase("___") || desti.equalsIgnoreCase("")) {
			String[] dest = { "270", "112", "841", "198", "135" };
			int ran = new Random().nextInt(dest.length);
			changeDesti = dest[ran];
			page.SetDestination(changeDesti);
		}
		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);

		// enter hld
		String[] dest = { "153", "881", "615", "422", "211" };
		int ran = new Random().nextInt(dest.length);
		String hldesti = dest[ran];
		page.SetHLDest(hldesti);

		// enter hlc
		int cube = (int) (Math.random() * 99) + 1;
		String HeadloadCube = Integer.toString(cube);
		page.SetHeadloadCube(HeadloadCube);

		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// HANDLE PRO
		for (int i = 0; i < ProInfo.size(); i++) {
			String[] handleLobrPro = { "headload", "leaveON", "allshort", "dock" };
			int ran1 = new Random().nextInt(handleLobrPro.length);
			page.HandleLOBRpro(handleLobrPro[ran1]);
			Thread.sleep(1000);
		}
		Date d = CommonFunction.gettime("UTC");
		// navigate to ldg

		// (new WebDriverWait(driver,
		// 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check screen
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti,
				"loading screen destination is wrong");
		SAssert.assertFalse(page.DateInput.isEnabled(), "date&time field is not disabled");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), NewCube, "loading screen Cube field is wrong");

		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG", "Source_Create_ID is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");

		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SAssert.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts is wrong  " + TS + "  " + AlterTime);
			} else {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + " " + TS + "  " + d);
			}
		}
		// check time back in ldg screen
		Date expect2 = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, (Date) NewEqpStatusRecord.get(7));
		SAssert.assertEquals(page.GetDatePickerTime(), expect2, "time is wrong, back in ldg screen");

		// check pro grid in ldg screen again
		LinkedHashSet<ArrayList<String>> ProInfo1 = page.GetProList(page.ProListForm);
		SAssert.assertEquals(DataCommon.GetProListLD(SCAC, TrailerNB), ProInfo1, "pro grid is wrong");
		SAssert.assertAll();
	}

	@Test(priority = 14, dataProvider = "3000.05", dataProviderClass = DataForUSLDGLifeTest.class, enabled = false)
	public void NoLdgEmptyTrailerAddSomePro(String terminalcd, String SCAC, String TrailerNB, String destination,
			String cube, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		Date CurrentTime = CommonFunction.gettime("UTC");

		// check destination and cube prepopulate
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), destination, "destiantion display IS WRONG");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), cube, "cube display IS WRONG");

		// check time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MReqpst);
		SAssert.assertEquals(picker, expect, "lobr screen prepopulate time is wrong ");

		// change destination
		String changeDesti = page.ChangeDestiantion();

		// add pro
		ArrayList<String> GetProNotOnAnyTrailer = DataCommon.GetProNotInAnyTrailer();
		ArrayList<String> ADDPRO = new ArrayList<String>();
		page.RemoveProButton.click();
		for (int j = 0; j < 2; j++) {
			String CurrentPro = GetProNotOnAnyTrailer.get(j);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// enter cube
		page.ChangeCube();

		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		Date today = CommonFunction.getDay(d);
		w2.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		// Thread.sleep(3000);
		// System.out.println(page.ErrorAndWarningField.getText());
		// (new WebDriverWait(driver,
		// 80)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,"Trailer
		// "+SCAC+TrailerNB+" updated to LDG"));
		w2.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField, "pro(s) loaded."));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));

		// check eqps

		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG", "Source_Create_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000,
					"equipment status table" + i + "  " + TS + "  " + d);
		}

		// check added pro
		Thread.sleep(5000);
		for (String pro : ADDPRO) {
			ArrayList<Object> CheckWaybillRecord = DataCommon.GetWaybillInformationOfPro(pro);
			SAssert.assertEquals(CheckWaybillRecord.get(0), SCAC, "waybill SCAC is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(1), TrailerNB, "waybill trailernb is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(17), SCAC, "waybill  toSCAC is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(13), terminalcd, "waybill from terminal is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(18), TrailerNB, "waybill totrailernb is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(20), "LOADING", "waybill TRANSACTION TYPE is wrong");
			Date System_Modify_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(9));
			SAssert.assertTrue(Math.abs(System_Modify_TS.getTime() - d.getTime()) < 120000,
					" waybill table System_Modify_TS " + System_Modify_TS + "  " + d);
			Date Waybill_Transaction_End_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(11));
			SAssert.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime() - d.getTime()) < 120000,
					" waybill table Waybill_Transaction_End_TS " + Waybill_Transaction_End_TS + "  " + d);
		}

		SAssert.assertAll();
	}

	@Test(priority = 15, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class, description = "can transit to ldg not in ldg and ldd with pro, lobr", enabled = false)
	public void SetTrailerToLDGWithPro(String terminalcd, String SCAC, String TrailerNB, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		Date CurrentTime = CommonFunction.gettime("UTC");

		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// check time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MReqpst);
		SAssert.assertEquals(picker, expect, "lobr screen prepopulate time is wrong ");

		// change destination
		String changeDesti = page.ChangeDestiantion();

		// enter cube
		String NewCube = page.ChangeCube();

		// handle the left pro
		String[] handleLobrPro = { "headload", "leaveON", "allshort", "dock" };
		int ran = new Random().nextInt(handleLobrPro.length);
		page.HandleLOBRproAll(handleLobrPro[ran]);
		Date d = CommonFunction.gettime("UTC");
		// (new WebDriverWait(driver,
		// 50)).until(ExpectedConditions.elementToBeClickable(
		// page.LobrSubmitButton));
		// page.LobrSubmitButton.click();

		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		// (new WebDriverWait(driver,
		// 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG", "Source_Create_ID is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");

		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 180000, i + "  " + TS + "  " + d);
		}

		// check screen
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti, "");
		SAssert.assertFalse(page.DateInput.isEnabled(), "date&time field is not disabled");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), NewCube, "Cube field is not disabled");

		// check date&time prepopulate
		Date picker2 = page.GetDatePickerTime();
		Date expect2 = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, (Date) NewEqpStatusRecord.get(7));
		SAssert.assertEquals(picker2, expect2, "ldg screen prepopulate time is wrong ");

		// check pro grid in ldg screen again
		LinkedHashSet<ArrayList<String>> ProInfo1 = page.GetProList(page.ProListForm);
		SAssert.assertEquals(ProInfo1, DataCommon.GetProListLD(SCAC, TrailerNB), "pro grid is wrong");
		SAssert.assertAll();
	}

	@Test(priority = 16, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class, description = "can transit to ldg not in ldg and ldd with pro, lobr, enter a hl destination as same as destination ", groups = {
			"ldg uc" })
	public void SetTrailerToLDGWithProEnterHLDestinationSameAsDestionationIn4Blobr(String terminalcd, String SCAC,
			String TrailerNB, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// check date&time pre populate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MReqpst);
		SAssert.assertEquals(picker, expect, "lobr screen prepopulate time is wrong ");

		// check lobr pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SAssert.assertEquals(ProInfo, DataCommon.GetProListLOBR(SCAC, TrailerNB), " lobr pro grid is wrong");
		ArrayList<String> prolistbeforelobr = DataCommon.GetProOnTrailer(SCAC, TrailerNB);

		// change destination
		String changeDesti = page.ChangeDestiantion();

		// enter cube
		String NewCube = page.ChangeCube();

		// enter hldestination same as destination
		page.SetHLDest(page.DestinationField.getAttribute("value"));

		w1.until(ExpectedConditions.visibilityOf(page.ErrorAndWarningField));
		SAssert.assertEquals(page.ErrorAndWarningField.getText(),
				"The trailer destination and headload destination must be different.");
		page.hlCancelButton.click();
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));

		SAssert.assertAll();
	}

	@AfterMethod(groups = { "ldg uc" })
	public void getbackldg(ITestResult result) throws InterruptedException {
		if (result.getStatus() == ITestResult.FAILURE) {

			String Testparameter = Arrays.toString(Arrays.copyOf(result.getParameters(), 3)).replaceAll("[^\\d.a-zA-Z]",
					"");
			String FailureTestparameter = result.getName() + Testparameter;

			Utility.takescreenshot(driver, FailureTestparameter);
			driver.navigate().refresh();
			page.SetStatus("ldg");
		}
	}
}
