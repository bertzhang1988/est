package TestCase.ReusableFunctionTest;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Data.DataForUS1215;
import Data.DataForUS200091;
import Function.CommonFunction;
import Function.ConfigRd;
import Function.DataCommon;
import Page.EqpStatusPageS;
import TestCase.CLtesting.DataForCLScreenTesting;

public class AddVariousTypeOfProForTrailer {
	private WebDriver driver;
	private EqpStatusPageS page;

	@Test(priority = 1, dataProvider = "12.15", dataProviderClass = DataForUS1215.class, description = "non ldg without pro, add pro through quick close", groups = {
			"ldg uc" })
	public void NONLDGTrailerWithoutPROAddPRO(String terminalcd, String SCAC, String TrailerNB, String Desti,
			String AmountPro, String AmountWeight, String Cube, String seal, String hldesti, String hlcube,
			Date MReqpst) throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		page.SetStatus("ldg");
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		Date CurrentTime = CommonFunction.gettime("UTC");
		Date LocalTime = null;

		// check date&time field should a. eqpst>current-time use eqpst minute+1
		// b. eqpst<current time use current time
		if (MReqpst.before(CurrentTime)) {
			LocalTime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		} else if (MReqpst.after(CurrentTime)) {
			LocalTime = CommonFunction.getLocalTime(terminalcd, MReqpst);
		}

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
		cal.setTime(LocalTime);
		int hourOfDay = cal.get(Calendar.HOUR_OF_DAY); // 24 hour clock
		String hour = String.format("%02d", hourOfDay);
		int minute = cal.get(Calendar.MINUTE);
		String Minute = String.format("%02d", minute);
		String MinutePlusOne = String.format("%02d", minute + 1);
		String DATE = dateFormat.format(cal.getTime());

		SAssert.assertEquals(page.DateInput.getAttribute("value"), DATE, " ldg screen TIME DARE IS WRONG");
		SAssert.assertEquals(page.HourInput.getAttribute("value"), hour, "ldg screen TIME HOR IS WRONG");
		if (MReqpst.after(CurrentTime)) {
			SAssert.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne,
					"ldg scren TIME MINUTE IS WRONG");
		} else {
			SAssert.assertEquals(page.MinuteInput.getAttribute("value"), Minute, " ldg screen TIME MINUTE IS WRONG");
		}
		// ENTER DESTINATION
		String[] dest = { "270", "112", "841", "198", "135" };
		int ran = new Random().nextInt(dest.length);
		String changeDesti = dest[ran];
		page.SetDestination(changeDesti);

		// add pro not in any trailer
		ArrayList<String> GetProNotOnAnyTrailer = DataCommon.GetProNotInAnyTrailer();
		ArrayList<String> ADDPRO = new ArrayList<String>();
		page.RemoveProButton.click();
		for (int j = 0; j < 3; j++) {
			String CurrentPro = GetProNotOnAnyTrailer.get(j);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// add pro from other trailer
		ArrayList<String> PRO2 = DataCommon.GetProFromTrailerOnDifferentTerminal(terminalcd, SCAC, TrailerNB);
		for (int i = 0; i < 3; i++) {
			String CurrentPro = PRO2.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// add no waybill record pro
		ArrayList<String> PRO3 = DataCommon.GenerateProNotInDB();
		for (int i = 0; i < 3; i++) {
			String CurrentPro = PRO3.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// add dt mismatch pro record pro
		ArrayList<String> PRO4 = DataCommon.ProWithDttmsp();
		for (int i = 0; i < 3; i++) {
			String CurrentPro = PRO4.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// change cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);

		// page.SubmitLDGButton.click();
		// click submit and close out button
		page.SubmitAndCloseOutButton.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField, "pro(s) loaded"));
		Date d2 = CommonFunction.gettime("UTC");
		// navigate to quick close screen
		// (new WebDriverWait(driver,
		// 20)).until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen,
		// "Set Trailer Status to Closed"));
		// (new WebDriverWait(driver,
		// 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		Date CurrentTime2 = CommonFunction.gettime("UTC");
		Date LocalTime1 = d;
		Thread.sleep(20000);
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG", "Source_Create_ID is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");

		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
		}

		// CHECK WAYBILL TABLE (load new pro)
		for (String pro : ADDPRO) {
			ArrayList<Object> CheckWaybillRecord = DataCommon.GetWaybillInformationOfPro(pro);
			SAssert.assertEquals(CheckWaybillRecord.get(0), SCAC, "" + pro + "waybill SCAC is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(1), TrailerNB, "" + pro + "waybill trailernb is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(17), SCAC, "" + pro + "waybill  toSCAC is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(13), terminalcd, "" + pro + "waybill from terminal is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(18), TrailerNB, "" + pro + "waybill totrailernb is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(20), "LOADING", "" + pro + "waybill TRANSACTION TYPE is wrong");
			Date System_Modify_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(9));
			SAssert.assertTrue(Math.abs(System_Modify_TS.getTime() - d.getTime()) < 120000,
					"" + pro + " waybill table System_Modify_TS " + System_Modify_TS + "  " + d);
			Date Waybill_Transaction_End_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(11));
			SAssert.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime() - d.getTime()) < 120000,
					"" + pro + " waybill table Waybill_Transaction_End_TS " + System_Modify_TS + "  " + d);
		}

		// check prepopulate quick close screen
		SAssert.assertEquals(page.TerminalField.getAttribute("value"), terminalcd,
				"quick close screen terminal is wrong");
		SAssert.assertEquals(page.TrailerField.getText(), page.SCACTrailer(SCAC, TrailerNB),
				"quick close screen trailer input is wrong");
		SAssert.assertEquals(page.qcDestination.getAttribute("value"), changeDesti,
				"quick close screen destination is wrong");
		// SAssert.assertEquals(page.qcShipmentCount.getAttribute("value"),
		// AmountPro,"quick close screen shipment count is wrong");
		// SAssert.assertEquals(page.qcShipmentWeight.getAttribute("value").replaceAll("_",
		// ""), AmountWeight,"quick close screen shipment weight is wtong");
		SAssert.assertEquals(page.qcEnrCubeField.getAttribute("value"), NewCube, "quick close screen cube is wrong");

		// check date&time field should a. eqpst>current-time use eqpst minute+1
		// b. eqpst<current time use current time, set ldg to ldd
		MReqpst = (Date) NewEqpStatusRecord.get(7);
		if (MReqpst.before(CurrentTime2)) {
			LocalTime1 = CommonFunction.getLocalTime(terminalcd, CurrentTime2);
		} else if (MReqpst.after(CurrentTime2)) {
			LocalTime1 = CommonFunction.getLocalTime(terminalcd, MReqpst);
		}

		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/YYYY");
		cal1.setTime(LocalTime1);
		int hourOfDay1 = cal1.get(Calendar.HOUR_OF_DAY); // 24 hour clock
		String hour1 = String.format("%02d", hourOfDay1);
		int minute1 = cal1.get(Calendar.MINUTE);
		String Minute1 = String.format("%02d", minute1);
		String MinutePlusOne1 = String.format("%02d", minute1 + 1);
		String DATE1 = dateFormat1.format(cal1.getTime());
		SAssert.assertEquals(page.DateInput.getAttribute("value"), DATE1, " qc screen date time is wrong");
		SAssert.assertEquals(page.HourInput.getAttribute("value"), hour1, "qc screen hour is wrong");
		if (MReqpst.after(CurrentTime)) {
			SAssert.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne1, "qc screen minute is wrong");
		} else {
			SAssert.assertEquals(page.MinuteInput.getAttribute("value"), Minute1, "qc screen minute is wrong");
		}

		// CHECK PRO GRID IN QC SCREEN
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListForm);
		SAssert.assertEquals(ProInfo, DataCommon.GetProListLD(SCAC, TrailerNB),
				" quick close screen prolist information is wrong");

		// enter seal if it is empty
		int Ran2 = (int) (Math.random() * 999998999) + 1000;
		String NewSeal = Integer.toString(Ran2);
		if (page.qcSealField.getAttribute("value").replaceAll("_", "").equalsIgnoreCase("")) {
			page.SetSealQC(NewSeal);
		}
		page.qcCloseTrailerButton.click();
		Date d1 = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		(new WebDriverWait(driver, 20))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 20))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		ArrayList<Object> NewEqpStatusRecord1 = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord1.get(0), "LDD", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord1.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord1.get(11), NewSeal, "SEAL_NB is wrong");
		SAssert.assertEquals(NewEqpStatusRecord1.get(3), "LH.LDD", "Source_Create_ID is wrong");
		SAssert.assertEquals(NewEqpStatusRecord1.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");

		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord1.get(i));
			SAssert.assertTrue(Math.abs(TS.getTime() - d1.getTime()) < 120000, i + "  " + TS + "  " + d1);
		}
		System.out.println((d2.getTime() - d.getTime()) / 1000.0);
		SAssert.assertAll();
	}

	@Test(priority = 2, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class, groups = { "ldg uc" })
	public void LdgTrailerWithProAddPro(String terminalcd, String SCAC, String TrailerNB, String destination,
			Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		page.SetStatus("ldg");
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// add pro not in any trailer
		ArrayList<String> PRO = DataCommon.GetProNotInAnyTrailer();
		ArrayList<String> ADDPRO = new ArrayList<String>();
		for (int i = 0; i < 0; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// add pro from other trailer
		ArrayList<String> PRO2 = DataCommon.GetProFromTrailerOnDifferentTerminal(terminalcd, SCAC, TrailerNB);
		for (int i = 0; i < 0; i++) {
			String CurrentPro = PRO2.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// add no waybill record pro
		ArrayList<String> PRO3 = DataCommon.GenerateProNotInDB();
		for (int i = 0; i < 15; i++) {
			String CurrentPro = PRO3.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// add dt mismatch pro record pro
		ArrayList<String> PRO4 = DataCommon.ProWithDttmsp();
		for (int i = 0; i < 0; i++) {
			String CurrentPro = PRO4.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		for (int i = 0; i < ADDPRO.size(); i++) {
			String value = ADDPRO.get(i);
			System.out.println("Element: " + value);
		}
		ArrayList<Object> OldEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);

		// click new submit
		page.SubmitButton.click();
		// page.SubmitAndCloseOutButton.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField, "pro(s) loaded"));
		Thread.sleep(20000);
		Date d2 = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

		page.EnterTrailer(SCAC, TrailerNB);
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListForm);
		SAssert.assertEquals(ProInfo, DataCommon.GetProListLD(SCAC, TrailerNB), "pro grid is wrong");
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

		for (String CurrentPro : ADDPRO) {
			ArrayList<Object> NewWbAndWbtRecord = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SAssert.assertEquals(NewWbAndWbtRecord.get(0), SCAC,
					"" + CurrentPro + " wb.Standard_Carrier_Alpha_CD is wrong");
			SAssert.assertEquals(NewWbAndWbtRecord.get(1), TrailerNB,
					"" + CurrentPro + "wb.Equipment_Unit_NB is wrong");
			SAssert.assertEquals(NewWbAndWbtRecord.get(17), SCAC,
					"" + CurrentPro + "wb.To_Standard_Carrier_Alpha_CD is wrong");
			SAssert.assertEquals(NewWbAndWbtRecord.get(18), TrailerNB,
					"" + CurrentPro + "wb.To_Equipment_Unit_NB is wrong");
			SAssert.assertEquals(NewWbAndWbtRecord.get(20), "LOADING",
					"" + CurrentPro + "wb.Waybill_Transaction_Type_NM is wrong");
			Date System_Modify_TS = CommonFunction.SETtime((Date) NewWbAndWbtRecord.get(9));
			SAssert.assertTrue(Math.abs(System_Modify_TS.getTime() - d.getTime()) < 120000,
					"" + CurrentPro + " waybill table System_Modify_TS " + System_Modify_TS + "  " + d);
			Date Waybill_Transaction_End_TS = CommonFunction.SETtime((Date) NewWbAndWbtRecord.get(11));
			SAssert.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime() - d.getTime()) < 120000, "" + CurrentPro
					+ " waybill table Waybill_Transaction_End_TS " + Waybill_Transaction_End_TS + "  " + d);
		}
		// System.out.println((d2.getTime()-d.getTime())/1000.0);
		SAssert.assertAll();
	}

	@Test(priority = 3, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class, groups = { "ldg uc" })
	public void LdgTrailerWithProAddPro2(String terminalcd, String SCAC, String TrailerNB, String destination,
			Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetStatus("ldg");
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// // add pro not in any trailer
		ArrayList<String> GetProNotOnAnyTrailer = DataCommon.GetProNotInAnyTrailer();
		ArrayList<String> ADDPRO = new ArrayList<String>();
		page.RemoveProButton.click();
		for (int j = 0; j < 10; j++) {
			String CurrentPro = GetProNotOnAnyTrailer.get(j);
			page.EnterPro(CurrentPro);
			System.out.println(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// // add pro from other trailer
		ArrayList<String> PRO2 = DataCommon.GetProFromTrailerOnDifferentTerminal(terminalcd, SCAC, TrailerNB);
		for (int i = 0; i < 10; i++) {
			String CurrentPro = PRO2.get(i);
			page.EnterPro(CurrentPro);
			System.out.println(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// add no waybill record pro
		ArrayList<String> PRO3 = DataCommon.GenerateProNotInDB();
		for (int i = 0; i < 30; i++) {
			String CurrentPro = PRO3.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// // add dttmsp pro
		ArrayList<String> PRO4 = DataCommon.ProWithDttmsp();
		for (int i = 0; i < 8; i++) {
			String CurrentPro = PRO4.get(i);
			page.EnterPro(CurrentPro);
			System.out.println(CurrentPro);
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
		(new WebDriverWait(driver, 180))
				.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField, "pro(s) loaded"));
		Date d2 = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

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
					" waybill table Waybill_Transaction_End_TS is wrong " + CurrentPro + "  "
							+ Waybill_Transaction_End_TS + "  " + d);
		}
		System.out.println((d2.getTime() - d.getTime()) / 1000.0);
		SAssert.assertAll();
	}

	@Test(priority = 4, dataProvider = "12.15", dataProviderClass = DataForUS1215.class, description = "non ldg without pro, add pro", groups = {
			"ldg uc" })
	public void NONLDGTrailerWithoutPROAddPRO2(String terminalcd, String SCAC, String TrailerNB, String Desti,
			String AmountPro, String AmountWeight, String Cube, String seal, String hldesti, String hlcube,
			Date MReqpst) throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		page.SetStatus("ldg");
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// ENTER DESTINATION
		String[] dest = { "270", "112", "841", "198", "135" };
		int ran = new Random().nextInt(dest.length);
		String changeDesti = dest[ran];
		page.SetDestination(changeDesti);

		// add pro not in any trailer
		ArrayList<String> GetProNotOnAnyTrailer = DataCommon.GetProNotInAnyTrailer();
		ArrayList<String> ADDPRO = new ArrayList<String>();
		page.RemoveProButton.click();
		for (int j = 0; j < 10; j++) {
			String CurrentPro = GetProNotOnAnyTrailer.get(j);
			page.EnterPro(CurrentPro);
			System.out.println(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// add pro from other trailer
		ArrayList<String> PRO2 = DataCommon.GetProFromTrailerOnDifferentTerminal(terminalcd, SCAC, TrailerNB);
		for (int i = 0; i < 10; i++) {
			String CurrentPro = PRO2.get(i);
			page.EnterPro(CurrentPro);
			System.out.println(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// add no waybill record pro
		ArrayList<String> PRO3 = DataCommon.GenerateProNotInDB();
		for (int i = 0; i < 10; i++) {
			String CurrentPro = PRO3.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// add dt mismatch pro record pro
		ArrayList<String> PRO4 = DataCommon.ProWithDttmsp();
		for (int i = 0; i < 10; i++) {
			String CurrentPro = PRO4.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// change cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		// click submit
		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField, "pro(s) loaded"));
		Date d2 = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG", "Source_Create_ID is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");

		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 180000, i + "  " + TS + "  " + d);
		}

		// CHECK WAYBILL TABLE (load new pro)
		for (String pro : ADDPRO) {

			ArrayList<Object> CheckWaybillRecord = DataCommon.GetWaybillInformationOfPro(pro);
			SAssert.assertEquals(CheckWaybillRecord.get(0), SCAC, "" + pro + "waybill SCAC is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(1), TrailerNB, "" + pro + "waybill trailernb is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(17), SCAC, "" + pro + "waybill  toSCAC is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(13), terminalcd, "" + pro + "waybill from terminal is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(18), TrailerNB, "" + pro + "waybill totrailernb is wrong");
			SAssert.assertEquals(CheckWaybillRecord.get(20), "LOADING", "" + pro + "waybill TRANSACTION TYPE is wrong");
			Date System_Modify_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(9));
			SAssert.assertTrue(Math.abs(System_Modify_TS.getTime() - d.getTime()) < 120000,
					"" + pro + " waybill table System_Modify_TS " + System_Modify_TS + "  " + d);
			Date Waybill_Transaction_End_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(11));
			SAssert.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime() - d.getTime()) < 120000,
					"" + pro + " waybill table Waybill_Transaction_End_TS " + Waybill_Transaction_End_TS + "  " + d);

		}
		System.out.println((d2.getTime() - d.getTime()) / 1000.0);
		SAssert.assertAll();
	}

	@Test(priority = 5, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class, description = "trailer in cl status With pro set to cl", enabled = true)
	public void CLHasProSetToCL(String terminalcd, String SCAC, String TrailerNB, String CityR, String CityRT,
			String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {

		SoftAssert SA = new SoftAssert();
		Actions builder = new Actions(driver);
		// page.SetStatus("CL");
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		// Check date and time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, MRSts);
		SA.assertEquals(picker, expect, "CL screen prepopulate time is wrong ");

		// Check Plan Day
		Date expectPlanDay = CommonFunction.getPrepopulatePlanDay(terminalcd, CurrentTime, PlanD);
		SA.assertEquals(page.GetPlanDatePickerTime(), expectPlanDay, "Plan date prepopulate time is wrong ");

		// Check other fields prepopulate
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
		ArrayList<String> Addpro = new ArrayList<String>();
		ArrayList<String> PRO = DataCommon.GetProNotInAnyTrailer();
		page.RemoveProButton.click();
		for (int i = 0; i < 40; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			System.out.println(CurrentPro);
			Addpro.add(CurrentPro);
		}

		// add pro from other trailer
		ArrayList<String> PRO2 = DataCommon.GetProFromTrailerOnDifferentTerminal(terminalcd, SCAC, TrailerNB);
		for (int i = 0; i < 0; i++) {
			String CurrentPro = PRO2.get(i);
			page.EnterPro(CurrentPro);
			System.out.println(CurrentPro);
			Addpro.add(CurrentPro);
		}

		// add no waybill record pro
		ArrayList<String> PRO3 = DataCommon.GenerateProNotInDB();
		for (int i = 0; i < 0; i++) {
			String CurrentPro = PRO3.get(i);
			page.EnterPro(CurrentPro);
			Addpro.add(CurrentPro);
		}

		// add dt mismatch pro record pro
		ArrayList<String> PRO4 = DataCommon.ProWithDttmsp();
		for (int i = 0; i < 0; i++) {
			String CurrentPro = PRO4.get(i);
			page.EnterPro(CurrentPro);
			System.out.println(CurrentPro);
			Addpro.add(CurrentPro);
		}

		// add volume pro
		ArrayList<String> VolumePRO = DataCommon.GetVolumeProNotInAnyTrailer();
		ArrayList<String> VolumePROList = new ArrayList<String>();
		for (int i = 0; i < 0; i++) {
			String CurrentPro = VolumePRO.get(i);
			page.EnterPro(CurrentPro);
			VolumePROList.add(CurrentPro);

		}
		Addpro.addAll(VolumePROList);

		// enter key
		builder.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");

		(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Trailer " + page.SCACTrailer(SCAC, TrailerNB) + " updated to CL"));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		Date d2 = CommonFunction.gettime("UTC");

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
			SA.assertEquals(CheckWaybillRecord.get(3), "CL", "" + pro + " waybill Source_Modify_ID is wrong");
			SA.assertEquals(CheckWaybillRecord.get(17), SCAC, "" + pro + " waybill  toSCAC is wrong");
			SA.assertEquals(CheckWaybillRecord.get(13), terminalcd, "" + pro + " waybill from terminal is wrong");
			SA.assertEquals(CheckWaybillRecord.get(18), TrailerNB, "" + pro + " waybill totrailernb is wrong");
			SA.assertEquals(CheckWaybillRecord.get(19), terminalcd,
					"" + pro + " Manifest_Destination_Fclty_CD is wrong");
			SA.assertEquals(CheckWaybillRecord.get(20), "LOADING", "" + pro + " waybill TRANSACTION TYPE is wrong");
			Date System_Modify_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(9));
			SA.assertTrue(Math.abs(System_Modify_TS.getTime() - d.getTime()) < 120000,
					"" + pro + " waybill table System_Modify_TS " + System_Modify_TS + "  " + d);
			Date Waybill_Transaction_End_TS = CommonFunction.SETtime((Date) CheckWaybillRecord.get(11));
			SA.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime() - d.getTime()) < 120000,
					"" + pro + " waybill table Waybill_Transaction_End_TS " + System_Modify_TS + "  " + d);

		}
		System.out.println((d2.getTime() - d.getTime()) / 1000.0);
		SA.assertAll();

	}

	@BeforeClass(groups = { "ldg uc" })
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

	// @AfterClass( groups = { "ldg uc" })
	public void Close() {
		driver.close();
	}

}
