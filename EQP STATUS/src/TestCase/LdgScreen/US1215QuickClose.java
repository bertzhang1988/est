package TestCase.LdgScreen;

import java.awt.AWTException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Data.DataForUS1215;
import Data.DataForUS439;
import Function.CommonFunction;
import Function.DataCommon;
import Function.SetupBrowser;
import Page.EqpStatusPageS;

public class US1215QuickClose extends SetupBrowser {
	private EqpStatusPageS page;
	private WebDriverWait w1;

	@BeforeClass(groups = { "ldg uc" })
	public void SetUp() throws AWTException, InterruptedException {
		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 50);
		driver.get(conf.GetURL());
		driver.manage().window().maximize();
		page.SetStatus("ldg");

	}

	@Test(priority = 1, dataProvider = "12.15", dataProviderClass = DataForUS1215.class, description = "ldg trailer with pro quick close", groups = {
			"ldg uc" })
	public void LDGTrailerWithPROQuickClose(String terminal, String SCAC, String TrailerNB, String Desti,
			String AmountPro, String AmountWeight, String Cube, String seal, String hldesti, String hlcube,
			Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminal);
		page.EnterTrailer(SCAC, TrailerNB);

		// enter cube if it is empty
		if (page.CubeField.getAttribute("value").equalsIgnoreCase("")) {
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			String cube = NewCube;
			page.SetCube(cube);
		}

		// click submit and close out button
		page.SubmitAndCloseOutButton.click();

		// navigate to quick close screen
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status to Closed"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		Date CurrentTime = CommonFunction.gettime("UTC");

		// check quick close screen
		SA.assertEquals(page.qcDestination.getAttribute("value"), Desti, "quick close screen destination is wrong");
		SA.assertEquals(page.qcShipmentCount.getAttribute("value"), AmountPro,
				"quick close screen shipment count is wrong");
		SA.assertEquals(page.qcShipmentWeight.getAttribute("value").replaceAll("_", ""), AmountWeight,
				"quick close screen shipment weight is wtong");
		SA.assertEquals(page.qcEnrCubeField.getAttribute("value"), Cube, "quick close screen cube is wrong");

		// check date&time pre populate quick close
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminal, CurrentTime, MReqpst);
		SA.assertEquals(picker, expect, "quick close screen prepopulate time is wrong ");

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListForm);
		SA.assertEquals(ProInfo, DataCommon.GetProListLD(SCAC, TrailerNB),
				" quick close screen prolist information is wrong");

		// enter seal if it is empty
		int Ran2 = (int) (Math.random() * 999998999) + 1000;
		String NewSeal = Integer.toString(Ran2);
		if (page.qcSealField.getAttribute("value").equalsIgnoreCase("__________")) {
			page.SetSealQC(NewSeal);
		}
		page.qcCloseTrailerButton.click();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "LDD", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminal, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(11), NewSeal, "SEAL_NB is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "LH.LDD", "Source_Create_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(4), Cube, "Actual_Capacity_Consumed_PC is wrong");

		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
		}
		SA.assertAll();
	}

	@Test(priority = 2, dataProvider = "12.15", dataProviderClass = DataForUS1215.class, description = "non ldg without pro, add pro through quick close", groups = {
			"ldg uc" })
	public void NONLDGTrailerWithoutPROAddPRO(String terminalcd, String SCAC, String TrailerNB, String Desti,
			String AmountPro, String AmountWeight, String Cube, String seal, String hldesti, String hlcube,
			Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		// check date&time pre populate ldg screen
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MReqpst);
		SAssert.assertEquals(picker, expect, "ldg screen prepopulate time is wrong ");

		// ENTER DESTINATION
		String[] dest = { "270", "112", "841", "198", "135" };
		int ran = new Random().nextInt(dest.length);
		String changeDesti = dest[ran];
		page.SetDestination(changeDesti);

		// add pro
		ArrayList<String> GetProNotOnAnyTrailer = DataCommon.GetProNotInAnyTrailer();
		ArrayList<String> ADDPRO = new ArrayList<String>();
		page.RemoveProButton.click();
		for (int j = 0; j < 2; j++) {
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
		for (int i = 0; i < 0; i++) {
			String CurrentPro = PRO3.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
		}

		// change cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);

		// click submit and close out button
		page.SubmitAndCloseOutButton.click();
		Date d = CommonFunction.gettime("UTC");

		// navigate to quick close screen
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status to Closed"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		Date CurrentTime2 = CommonFunction.gettime("UTC");

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
					" waybill table Waybill_Transaction_End_TS " + System_Modify_TS + "  " + d);
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

		// check date&time pre populate quick close screen
		MReqpst = (Date) NewEqpStatusRecord.get(7);
		Date picker2 = page.GetDatePickerTime();
		Date expect2 = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime2, MReqpst);
		SAssert.assertEquals(picker2, expect2, "quick close screen prepopulate time is wrong ");

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
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
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
		SAssert.assertAll();
	}

	@Test(priority = 3, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class)
	public void VerifyMasterRevenueValidationToLDGTrailerWithoutPro(String terminalcd, String SCAC, String TrailerNB,
			Date MRST) throws InterruptedException, AWTException, ClassNotFoundException, SQLException {
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// ADD MR PRO
		ArrayList<String> MRprolist = DataForUS439.Getpro1("mr", SCAC, TrailerNB);
		for (int i = 0; i < 2; i++) {
			page.RemoveProButton.click();
			String pro = MRprolist.get(i);
			page.EnterPro(pro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			// change cube
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
			page.SubmitAndCloseOutButton.click();
			WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
			try {
				w1.until(ExpectedConditions.textToBePresentInElement(Message, "Cannot add master bill."));
			} catch (Exception e) {
				System.out.println("mr pro is not working as expectation" + pro);
			}
			w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
					"Remove invalid pros and choose Submit & Close Out again"));
			// wait other error message gone
			w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div[2]")));
			page.CheckAllAddProButton.click();
			page.RemoveProButton.click();
		}

		// ADD SU PRO
		ArrayList<String> SUprolist = DataForUS439.Getpro1("SU", SCAC, TrailerNB);
		for (int i = 0; i < 2; i++) {
			page.RemoveProButton.click();
			String pro = SUprolist.get(i);
			page.EnterPro(pro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			// change cube
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
			page.SubmitAndCloseOutButton.click();
			WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
			try {
				w1.until(ExpectedConditions.textToBePresentInElement(Message, "Cannot add supplemental bill."));
			} catch (Exception e) {
				System.out.println("su pro is not working as expectation" + pro);
			}
			w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
					"Remove invalid pros and choose Submit & Close Out again"));
			// wait other error message gone
			w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div[2]")));
			page.CheckAllAddProButton.click();
			page.RemoveProButton.click();
		}

		// ADD VO PRO
		ArrayList<String> VOprolist = DataForUS439.Getpro1("VO", SCAC, TrailerNB);
		for (int i = 0; i < 2; i++) {
			page.RemoveProButton.click();
			String pro = VOprolist.get(i);
			page.EnterPro(pro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			// change cube
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
			page.SubmitAndCloseOutButton.click();
			WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
			try {
				w1.until(ExpectedConditions.textToBePresentInElement(Message, "Cannot add voided PRO."));
			} catch (Exception e) {
				System.out.println("vo pro is not working as expectation" + pro);
			}
			w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
					"Remove invalid pros and choose Submit & Close Out again"));
			// wait other error message gone
			w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div[2]")));
			page.CheckAllAddProButton.click();
			page.RemoveProButton.click();
		}

		// ADD PRO already delivered.
		ArrayList<String> Dprolist = DataForUS439.Getpro1("", SCAC, TrailerNB);
		for (int i = 0; i < 2; i++) {
			page.RemoveProButton.click();
			String pro = Dprolist.get(i);
			page.EnterPro(pro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			// change cube
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
			page.SubmitAndCloseOutButton.click();
			WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
			try {
				w1.until(ExpectedConditions.textToBePresentInElement(Message, "PRO already delivered."));
			} catch (Exception e) {
				System.out.println("already delivered pro is not working as expectation" + pro);
			}
			w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
					"Remove invalid pros and choose Submit & Close Out again"));
			// wait other error message gone
			w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div[2]")));
			page.CheckAllAddProButton.click();
			page.RemoveProButton.click();
		}
	}
}
