package TestCase.LdgScreen;

import java.awt.AWTException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Data.DataForUS200091;
import Function.CommonFunction;
import Function.DataCommon;
import Function.Setup;
import Page.EqpStatusPageS;

public class US2000119NewSubmitButtonLDG extends Setup {
	private EqpStatusPageS page;
	private WebDriverWait w1;
	private WebDriverWait w2;

	@BeforeTest(groups = { "ldg uc" })
	public void SetUp() throws AWTException, InterruptedException {
		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 50);
		w2 = new WebDriverWait(driver, 150);
		  
		driver.manage().window().maximize();
		page.SetStatus("ldg");

	}

	@Test(priority = 1, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class, groups = { "ldg uc" })
	public void TrailerWithoutPROAddPRO(String terminalcd, String SCAC, String TrailerNB, String destination,
			Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		String[] dest = { "270", "112", "841", "198", "135" };
		int ran = new Random().nextInt(dest.length);
		String changeDesti = dest[ran];
		page.SetDestination(changeDesti);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// add pro not in any trailer
		ArrayList<String> GetProNotOnAnyTrailer = DataCommon.GetProNotInAnyTrailer();
		ArrayList<String> ADDPRO = new ArrayList<String>();
		page.RemoveProButton.click();
		for (int j = 0; j < 16; j++) {
			String CurrentPro = GetProNotOnAnyTrailer.get(j);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// add pro from other trailer
		ArrayList<String> PRO2 = DataCommon.GetProFromTrailerOnDifferentTerminal(terminalcd, SCAC, TrailerNB);
		for (int i = 0; i < 12; i++) {
			String CurrentPro = PRO2.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// add no waybill record pro
		ArrayList<String> PRO3 = DataCommon.GenerateProNotInDB();
		for (int i = 0; i < 12; i++) {
			String CurrentPro = PRO3.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}
		// change cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);

		page.SubmitButton1.click();

		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField, "pro(s) loaded"));
		Date d2 = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		// check eqps
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
		// check pro
		for (String pro : ADDPRO) {
			ArrayList<Object> CheckWaybillRecord = DataCommon.GetWaybillInformationOfPro(pro);
			SA.assertEquals(CheckWaybillRecord.get(0), SCAC, "waybill SCAC is wrong");
			SA.assertEquals(CheckWaybillRecord.get(1), TrailerNB, "waybill trailernb is wrong");
			SA.assertEquals(CheckWaybillRecord.get(17), SCAC, "waybill  toSCAC is wrong");
			SA.assertEquals(CheckWaybillRecord.get(13), terminalcd, "waybill from terminal is wrong");
			SA.assertEquals(CheckWaybillRecord.get(18), TrailerNB, "waybill totrailernb is wrong");
			SA.assertEquals(CheckWaybillRecord.get(20), "LOADING", "waybill TRANSACTION TYPE is wrong");
			Date System_Modify_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(9));
			SA.assertTrue(Math.abs(System_Modify_TS.getTime() - d.getTime()) < 120000,
					" waybill table System_Modify_TS " + System_Modify_TS + "  " + d);
			Date Waybill_Transaction_End_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(11));
			SA.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime() - d.getTime()) < 120000,
					" waybill table Waybill_Transaction_End_TS " + System_Modify_TS + "  " + d);
		}
		System.out.println((d2.getTime() - d.getTime()) / 1000.0);
		SA.assertAll();
	}

	@Test(priority = 2, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class, groups = { "ldg uc" })
	public void TrailerWithoutPRONoAdding(String terminalcd, String SCAC, String TrailerNB, String destination,
			Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		String[] dest = { "270", "112", "841", "198", "135" };
		int ran = new Random().nextInt(dest.length);
		String changeDesti = dest[ran];
		page.SetDestination(changeDesti);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
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

	@Test(priority = 3, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class, groups = { "ldg uc" })
	public void TrailerWithPROAddPRO(String terminalcd, String SCAC, String TrailerNB, String destination, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		// change destination
		String[] dest = { "270", "112", "841", "198", "135" };
		int ran = new Random().nextInt(dest.length);
		String changeDesti = dest[ran];
		page.SetDestination(changeDesti);
		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);

		try {
			if (page.LeftoverCheckAllPRO.isDisplayed()) {
				page.LeftoverCheckAllPRO.click();
			}
		} catch (Exception e) {

		}

		String[] handleLobrPro = { "headload", "leaveON", "allshort", "dock" };
		int ran1 = new Random().nextInt(handleLobrPro.length);
		page.HandleLOBRproAll(handleLobrPro[ran1]);

		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		Thread.sleep(3000);
		ArrayList<String> PRO = DataCommon.GetProNotInAnyTrailer();
		ArrayList<String> ADDPRO = new ArrayList<String>();
		for (int i = 0; i < 1; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}
		int Ran1 = (int) (Math.random() * 99) + 1;
		String NewCube1 = Integer.toString(Ran1);
		page.SetCube(NewCube1);

		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		// check eqps
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
		// check pro
		for (String pro : ADDPRO) {
			ArrayList<Object> CheckWaybillRecord = DataCommon.GetWaybillInformationOfPro(pro);
			SA.assertEquals(CheckWaybillRecord.get(0), SCAC, "waybill SCAC is wrong");
			SA.assertEquals(CheckWaybillRecord.get(1), TrailerNB, "waybill trailernb is wrong");
			SA.assertEquals(CheckWaybillRecord.get(17), SCAC, "waybill  toSCAC is wrong");
			SA.assertEquals(CheckWaybillRecord.get(13), terminalcd, "waybill from terminal is wrong");
			SA.assertEquals(CheckWaybillRecord.get(18), TrailerNB, "waybill totrailernb is wrong");
			SA.assertEquals(CheckWaybillRecord.get(20), "LOADING", "waybill TRANSACTION TYPE is wrong");
			Date System_Modify_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(9));
			SA.assertTrue(Math.abs(System_Modify_TS.getTime() - d.getTime()) < 120000,
					" waybill table System_Modify_TS " + System_Modify_TS + "  " + d);
			Date Waybill_Transaction_End_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(11));
			SA.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime() - d.getTime()) < 120000,
					" waybill table Waybill_Transaction_End_TS " + Waybill_Transaction_End_TS + "  " + d);
		}
		SA.assertAll();
	}

	@Test(priority = 4, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class, enabled = false)
	public void TrailerWithPRONoAdding(String terminalcd, String SCAC, String TrailerNB, String destination,
			Date MReqpst) throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		// change destination
		String[] dest = { "270", "112", "841", "198", "135" };
		int ran = new Random().nextInt(dest.length);
		String changeDesti = dest[ran];
		page.SetDestination(changeDesti);
		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		String[] handleLobrPro = { "headload", "leaveON", "allshort", "dock" };
		int ran1 = new Random().nextInt(handleLobrPro.length);
		page.HandleLOBRproAll(handleLobrPro[ran1]);
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG", "Source_Create_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
		}
		SA.assertAll();
	}

	@Test(priority = 5, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class, groups = { "ldg uc" })
	public void LdgTrailerWithProAddPro(String terminalcd, String SCAC, String TrailerNB, String destination,
			Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// add pro not in any trailer
		ArrayList<String> GetProNotOnAnyTrailer = DataCommon.GetProNotInAnyTrailer();
		ArrayList<String> ADDPRO = new ArrayList<String>();
		page.RemoveProButton.click();
		for (int j = 0; j < 16; j++) {
			String CurrentPro = GetProNotOnAnyTrailer.get(j);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// add pro from other trailer
		ArrayList<String> PRO2 = DataCommon.GetProFromTrailerOnDifferentTerminal(terminalcd, SCAC, TrailerNB);
		for (int i = 0; i < 12; i++) {
			String CurrentPro = PRO2.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// add no waybill record pro
		ArrayList<String> PRO3 = DataCommon.GenerateProNotInDB();
		for (int i = 0; i < 12; i++) {
			String CurrentPro = PRO3.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		ArrayList<Object> OldEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		// click new submit
		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField, "pro(s) loaded"));
		Date d2 = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), destination, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(19), "LH.LDG", "Source_Modify_ID is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 5) {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, "modify_ts " + "  " + TS + "  " + d);
			} else {
				SAssert.assertEquals(NewEqpStatusRecord.get(i), (Date) OldEqpStatusRecord.get(i),
						i + "  " + NewEqpStatusRecord.get(i) + "  " + OldEqpStatusRecord.get(i));
			}
		}

		// check pro
		for (String CurrentPro : ADDPRO) {
			ArrayList<Object> NewWbAndWbtRecord = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SAssert.assertEquals(NewWbAndWbtRecord.get(0), SCAC,
					"" + CurrentPro + " wb.Standard_Carrier_Alpha_CD is wrong");
			SAssert.assertEquals(NewWbAndWbtRecord.get(1), TrailerNB, "wb.Equipment_Unit_NB is wrong");
			SAssert.assertEquals(NewWbAndWbtRecord.get(17), SCAC, "wb.To_Standard_Carrier_Alpha_CD is wrong");
			SAssert.assertEquals(NewWbAndWbtRecord.get(18), TrailerNB, "wb.To_Equipment_Unit_NB is wrong");
			SAssert.assertEquals(NewWbAndWbtRecord.get(20), "LOADING", "wb.Waybill_Transaction_Type_NM is wrong");
			Date System_Modify_TS = CommonFunction.SETtime((Date) NewWbAndWbtRecord.get(9));
			SAssert.assertTrue(Math.abs(System_Modify_TS.getTime() - d.getTime()) < 120000,
					" waybill table System_Modify_TS " + System_Modify_TS + "  " + d);
			Date Waybill_Transaction_End_TS = CommonFunction.SETtime((Date) NewWbAndWbtRecord.get(11));
			SAssert.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime() - d.getTime()) < 120000,
					" waybill table Waybill_Transaction_End_TS " + Waybill_Transaction_End_TS + "  " + d);
		}
		System.out.println((d2.getTime() - d.getTime()) / 1000.0);
		SAssert.assertAll();
	}

	@Test(priority = 6, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class, groups = { "ldg uc" })
	public void LdgTrailerWithoutProNoChange(String terminalcd, String SCAC, String TrailerNB, String destination,
			Date MReqpst) throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		SA.assertEquals(page.DestinationField.getAttribute("value"), destination, "destiantion display IS WRONG");
		ArrayList<Object> OldEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);

		// click submit
		page.SubmitButton1.click();
		// (new WebDriverWait(driver,
		// 50)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,"No
		// updates entered."));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		// check eqps update only modify_ts update
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord, OldEqpStatusRecord, "eqps change");
		SA.assertAll();
	}

	@Test(priority = 7, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class, groups = { "ldg uc" })
	public void LdgTrailerWithoutProChangeDestinationAddPRO(String terminalcd, String SCAC, String TrailerNB,
			String destination, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// change DESTINATION
		String[] dest = { "270", "112", "841", "198", "135" };
		int ran = new Random().nextInt(dest.length);
		String changeDesti = dest[ran];
		while (changeDesti.equalsIgnoreCase(destination)) {
			changeDesti = dest[new Random().nextInt(dest.length)];
		}
		page.SetDestination(changeDesti);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// add pro
		ArrayList<String> PRO = DataCommon.GetProNotInAnyTrailer();
		ArrayList<String> ADDPRO = new ArrayList<String>();
		for (int i = 0; i < 1; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}
		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		// enter key
		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		Thread.sleep(3000);
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG", "Source_Create_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}
		// check pro
		for (String pro : ADDPRO) {
			ArrayList<Object> CheckWaybillRecord = DataCommon.GetWaybillInformationOfPro(pro);
			SA.assertEquals(CheckWaybillRecord.get(0), SCAC, "waybill SCAC is wrong");
			SA.assertEquals(CheckWaybillRecord.get(1), TrailerNB, "waybill trailernb is wrong");
			SA.assertEquals(CheckWaybillRecord.get(17), SCAC, "waybill  toSCAC is wrong");
			SA.assertEquals(CheckWaybillRecord.get(13), terminalcd, "waybill from terminal is wrong");
			SA.assertEquals(CheckWaybillRecord.get(18), TrailerNB, "waybill totrailernb is wrong");
			SA.assertEquals(CheckWaybillRecord.get(20), "LOADING", "waybill TRANSACTION TYPE is wrong");
			Date System_Modify_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(9));
			SA.assertTrue(Math.abs(System_Modify_TS.getTime() - d.getTime()) < 120000,
					" waybill table System_Modify_TS " + System_Modify_TS + "  " + d);
			Date Waybill_Transaction_End_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(11));
			SA.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime() - d.getTime()) < 120000,
					" waybill table Waybill_Transaction_End_TS " + Waybill_Transaction_End_TS + "  " + d);
		}
		SA.assertAll();
	}

	@Test(priority = 8, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class, groups = { "ldg uc" })
	public void LdgTrailerWithProChangeDestinationAddPRO(String terminalcd, String SCAC, String TrailerNB,
			String destination, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// change destination
		String[] dest = { "270", "112", "841", "198", "135" };
		int ran = new Random().nextInt(dest.length);
		String changeDesti = dest[ran];
		while (changeDesti.equalsIgnoreCase(destination)) {
			changeDesti = dest[new Random().nextInt(dest.length)];
		}
		destination = changeDesti;
		page.SetDestination(destination);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Mark PROs as Headload"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// headload yes
		page.YesButton.click();
		Date d1 = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		(new WebDriverWait(driver, 20))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		// add pro
		ArrayList<String> PRO = DataCommon.GetProNotInAnyTrailer();
		ArrayList<String> ADDPRO = new ArrayList<String>();
		for (int i = 0; i < 1; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}
		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		// enter key
		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));// wait
																										// the
																										// alert
																										// gone
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		Thread.sleep(5000);
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), destination, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SAssert.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else if (i == 5) {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, "modify_ts " + "  " + TS + "  " + d);
			} else {
				SAssert.assertTrue(Math.abs(TS.getTime() - d1.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}
		// check waybill information
		for (String pro : ADDPRO) {
			ArrayList<Object> CheckWaybillRecord = DataCommon.GetWaybillInformationOfPro(pro);
			SAssert.assertEquals(CheckWaybillRecord.get(0), SCAC, "waybill SCAC is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(1), TrailerNB, "waybill trailernb is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(17), SCAC, "waybill  toSCAC is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(13), terminalcd, "waybill from terminal is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(18), TrailerNB, "waybill totrailernb is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(20), "LOADING", "waybill TRANSACTION TYPE is wrong");
			Date System_Modify_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(9));
			SAssert.assertTrue(Math.abs(System_Modify_TS.getTime() - d1.getTime()) < 120000,
					" waybill table System_Modify_TS " + System_Modify_TS + "  " + d);
			Date Waybill_Transaction_End_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(11));
			SAssert.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime() - d1.getTime()) < 120000,
					" waybill table Waybill_Transaction_End_TS " + Waybill_Transaction_End_TS + "  " + d);
		}
		SAssert.assertAll();
	}
}