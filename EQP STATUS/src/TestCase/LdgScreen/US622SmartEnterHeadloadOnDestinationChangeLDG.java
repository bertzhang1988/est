package TestCase.LdgScreen;

import java.awt.AWTException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Data.DataForUS607;
import Function.CommonFunction;
import Function.DataCommon;
import Function.SetupBrowser;
import Function.Utility;
import Page.EqpStatusPageS;

public class US622SmartEnterHeadloadOnDestinationChangeLDG extends SetupBrowser {
	private EqpStatusPageS page;
	private Actions builder;
	private WebDriverWait w1;
	private WebDriverWait w2;

	@BeforeClass(groups = { "ldg uc" })
	public void SetUp() throws AWTException, InterruptedException {
		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 50);
		w2 = new WebDriverWait(driver, 150);
		driver.get(conf.GetURL());
		driver.manage().window().maximize();
		page.SetStatus("ldg");
		builder = new Actions(driver);
	}

	@AfterMethod(groups = { "ldg uc" })
	public void getbackldg(ITestResult result) throws InterruptedException {
		if (result.getStatus() == ITestResult.FAILURE) {

			String Testparameter = Arrays.toString(Arrays.copyOf(result.getParameters(), 3)).replaceAll("[^\\d.a-zA-Z]",
					"");
			String FailureTestparameter = result.getName() + Testparameter;

			Utility.takescreenshot(driver, FailureTestparameter);
			page.SetStatus("ldg");
		}
	}

	@Test(priority = 1, dataProvider = "6.07", dataProviderClass = DataForUS607.class, description = "2000.100", groups = {
			"ldg uc" })
	public void ldgTrailerWithProsYes(String terminalcd, String SCAC, String TrailerNB, String OrgiDesti, String cube,
			String HLDestination, String HLCube)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		// change destination
		String[] dest = { "851", "112", "841", "270", "198", "135" };
		int ran = new Random().nextInt(dest.length);
		String changeDesti = dest[ran];
		while (changeDesti.equalsIgnoreCase(OrgiDesti)) {
			changeDesti = dest[new Random().nextInt(dest.length)];
		}
		page.SetDestination(changeDesti);
		w1.until(ExpectedConditions.visibilityOf(page.HeadloadProForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		SAssert.assertEquals(page.HeadloadDestination.getAttribute("value"), OrgiDesti, "screenHldestination is wrong");
		SAssert.assertEquals(page.HLCubeField.getAttribute("value"), cube, "screenHLcube is wrong");
		SAssert.assertEquals(page.ManifestToField.getText(), OrgiDesti, "screen manifest dest is wrong");
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti, "screen desti is wrong");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), cube, "screen cube is wrong");
		page.hlCancelButton.click();// click cancel
		w1.until(ExpectedConditions.visibilityOf(page.AddProForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), OrgiDesti);
		// input cube if there is no cube value before
		if (page.CubeField.getAttribute("value").equalsIgnoreCase("")) {
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			cube = NewCube;
			page.SetCube(cube);
		}
		// change destination
		page.SetDestination(changeDesti);
		w1.until(ExpectedConditions.visibilityOf(page.HeadloadProForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		SAssert.assertEquals(page.HeadloadDestination.getAttribute("value"), OrgiDesti, "screenHldestination is wrong");
		SAssert.assertEquals(page.HLCubeField.getAttribute("value"), cube, "screenHLcube is wrong");
		SAssert.assertEquals(page.ManifestToField.getText(), OrgiDesti, "screen manifest dest is wrong");
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti, "screen desti is wrong");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), cube, "screen cube is wrong");
		ArrayList<Object> EQPStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		ArrayList<ArrayList<Object>> WbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), 0, 36);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		builder.sendKeys(Keys.ENTER).build().perform();// PRESS ENTER KEY
		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.visibilityOf(page.AddProForm));
		Thread.sleep(8000);
		// check eqpststus
		ArrayList<Object> NewEQPStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEQPStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong"); // equipment_dest_facility_cd
		SAssert.assertEquals(NewEQPStatusRecord.get(12), cube, "Headload_Capacity_Consumed_PC is wring"); // Headload_Capacity_Consumed_PC
		SAssert.assertEquals(NewEQPStatusRecord.get(13), OrgiDesti, "headload_dest_facility_cd is wrong"); // headload_dest_facility_cd
		SAssert.assertEquals(NewEQPStatusRecord.get(14), terminalcd, "headload_origin_facility_CD is wrong"); // headload_origin_facility_CD
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEQPStatusRecord.get(i));
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
			SAssert.assertEquals(Currentwbti.get(3), WbtRecord.get(i).get(3),
					Currentwbti.get(0) + " Waybill_Transaction_End_TS is wrong");
			SAssert.assertEquals(Currentwbti.get(2), WbtRecord.get(i).get(2),
					Currentwbti.get(0) + " manifest_destination is wrong");
			SAssert.assertEquals(Currentwbti.get(4), "LH.LDG", Currentwbti.get(0) + " source_modify_ID is wrong");
			// CHECK THE WAYBILL TRANSACTION END TS ARE NOT IDENTICAL
			// if(i>0) SAssert.assertTrue(TS.after(f2),"waybill
			// Waybill_Transaction_End_TS is not ascending increase :
			// waybill_transaction_end_ts "+TS+" waybill_transaction_end_ts of
			// previous pro is "+f2+" "+" "+Currentwbti.get(0));
			f2 = TS;
			i = i + 1;
		}
		SAssert.assertAll();

	}

	@Test(priority = 2, dataProvider = "6.07", dataProviderClass = DataForUS607.class, groups = { "ldg uc" })
	public void ldgTrailerWithoutProsClickYes(String terminalcd, String SCAC, String TrailerNB, String OrgiDesti,
			String cube, String HLDestination, String HLCube)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// ADD PRO
		ArrayList<String> PRO = DataCommon.GetProNotInAnyTrailer();
		page.RemoveProButton.click();
		ArrayList<String> Addpro = new ArrayList<String>();
		for (int i = 0; i < 2; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			Addpro.add(CurrentPro);
		}
		// change cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);

		page.SubmitButton1.click();

		w2.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Trailer " + page.SCACTrailer(SCAC, TrailerNB) + " updated to LDG"));
		w2.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField, "pro(s) loaded."));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w1.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		// repull this trailer
		page.EnterTrailer(SCAC, TrailerNB);
		// change destination
		String[] dest = { "851", "112", "841", "270", "198", "135" };
		int ran = new Random().nextInt(dest.length);
		String changeDesti = dest[ran];
		while (changeDesti.equalsIgnoreCase(OrgiDesti)) {
			changeDesti = dest[new Random().nextInt(dest.length)];
		}
		page.SetDestination(changeDesti);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Mark PROs as Headload"));
		w1.until(ExpectedConditions.visibilityOf(page.HeadloadProForm));
		SAssert.assertEquals(page.HeadloadDestination.getAttribute("value"), OrgiDesti, "screenHldestination is wrong");
		SAssert.assertEquals(page.HLCubeField.getAttribute("value"), NewCube, "screenHLcube is wrong");
		SAssert.assertEquals(page.ManifestToField.getText(), OrgiDesti, "screen manifest dest is wrong");
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti, "screen desti is wrong");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), NewCube, "screen cube is wrong");
		page.hlCancelButton.click();// click cancel
		w1.until(ExpectedConditions.visibilityOf(page.AddProForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		Assert.assertEquals(page.DestinationField.getAttribute("value"), OrgiDesti);
		// change destination
		page.SetCube(NewCube);
		page.SetDestination(changeDesti);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Mark PROs as Headload"));
		w1.until(ExpectedConditions.visibilityOf(page.HeadloadProForm));
		SAssert.assertEquals(page.HeadloadDestination.getAttribute("value"), OrgiDesti, "screenHldestination is wrong");
		SAssert.assertEquals(page.HLCubeField.getAttribute("value"), NewCube, "screenHLcube is wrong");
		SAssert.assertEquals(page.ManifestToField.getText(), OrgiDesti, "screen manifest dest is wrong");
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti, "screen desti is wrong");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), NewCube, "screen cube is wrong");
		ArrayList<Object> EQPStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		ArrayList<ArrayList<Object>> WbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), 0, 34);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		builder.sendKeys(Keys.ENTER).build().perform();// PRESS ENTER KEY
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AddProForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		Thread.sleep(5000);
		// check eqpststus
		ArrayList<Object> NewEQPStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEQPStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd IS WRONG"); // equipment_dest_facility_cd
		SAssert.assertEquals(NewEQPStatusRecord.get(12), NewCube, "Headload_Capacity_Consumed_PC IS WRONG"); // Headload_Capacity_Consumed_PC
		SAssert.assertEquals(NewEQPStatusRecord.get(13), OrgiDesti, "headload_dest_facility_cd IS WRONG"); // headload_dest_facility_cd
		SAssert.assertEquals(NewEQPStatusRecord.get(14), terminalcd, "eqps headload_origin_facility_CD is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEQPStatusRecord.get(i));
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
			SAssert.assertEquals(Currentwbti.get(3), WbtRecord.get(i).get(3),
					Currentwbti.get(0) + " Waybill_Transaction_End_TS is wrong");
			SAssert.assertEquals(Currentwbti.get(2), WbtRecord.get(i).get(2),
					Currentwbti.get(0) + " manifest_destination is wrong");
			SAssert.assertEquals(Currentwbti.get(4), "LH.LDG", Currentwbti.get(0) + " source_modify_ID is wrong");
			// CHECK THE WAYBILL TRANSACTION END TS ARE NOT IDENTICAL
			// if(i>0) SAssert.assertTrue(TS.after(f2),"waybill
			// Waybill_Transaction_End_TS is not ascending increase :
			// waybill_transaction_end_ts "+TS+" waybill_transaction_end_ts of
			// previous pro is "+f2+" "+" "+Currentwbti.get(0));
			f2 = TS;
			i = i + 1;
		}
		SAssert.assertAll();
	}

	@Test(priority = 3, dataProvider = "6.07", dataProviderClass = DataForUS607.class, groups = { "ldg uc" })
	public void ldgTrailerWithHeadloadProsClickYes(String terminalcd, String SCAC, String TrailerNB, String OrgiDesti,
			String cube, String HLDestination, String HLCube)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		// ADD PRO
		ArrayList<String> PRO = DataCommon.GetProNotInAnyTrailer();
		page.RemoveProButton.click();
		ArrayList<String> Addpro = new ArrayList<String>();
		for (int i = 0; i < 2; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			Addpro.add(CurrentPro);
		}
		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		// click submit
		page.SubmitButton1.click();
		w2.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Trailer " + page.SCACTrailer(SCAC, TrailerNB) + " updated to LDG"));
		w2.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField, "pro(s) loaded."));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w1.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));

		// repull the trailer
		page.EnterTrailer(SCAC, TrailerNB);
		// change destination
		String changeDesti = page.ChangeDestiantion();
		page.SetDestination(changeDesti);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Mark PROs as Headload"));
		w1.until(ExpectedConditions.visibilityOf(page.HeadloadProForm));
		SAssert.assertEquals(page.HeadloadDestination.getAttribute("value"), OrgiDesti, "screenHldestination is wrong");
		SAssert.assertEquals(page.HLCubeField.getAttribute("value"), NewCube, "screenHLcube is wrong");
		SAssert.assertEquals(page.ManifestToField.getText(), OrgiDesti, "screen manifest dest is wrong");
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti, "screen desti is wrong");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), NewCube, "screen cube is wrong");
		page.hlCancelButton.click();// click cancel
		w1.until(ExpectedConditions.visibilityOf(page.AddProForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		Assert.assertEquals(page.DestinationField.getAttribute("value"), OrgiDesti);
		// change destination
		page.SetCube(NewCube);
		page.SetDestination(changeDesti);
		w1.until(ExpectedConditions.visibilityOf(page.HeadloadProForm));
		SAssert.assertEquals(page.HeadloadDestination.getAttribute("value"), OrgiDesti, "screenHldestination is wrong");
		SAssert.assertEquals(page.HLCubeField.getAttribute("value"), NewCube, "screenHLcube is wrong");
		SAssert.assertEquals(page.ManifestToField.getText(), OrgiDesti, "screen manifest dest is wrong");
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti, "screen desti is wrong");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), NewCube, "screen cube is wrong");
		ArrayList<Object> EQPStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		ArrayList<ArrayList<Object>> WbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), 0, 34);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		builder.sendKeys(Keys.ENTER).build().perform();// PRESS ENTER KEY
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AddProForm));
		Thread.sleep(6000);
		// check eqpststus
		ArrayList<Object> NewEQPStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEQPStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong"); // equipment_dest_facility_cd
		SAssert.assertEquals(NewEQPStatusRecord.get(12), NewCube, "Headload_Capacity_Consumed_PC is wrong"); // Headload_Capacity_Consumed_PC
		SAssert.assertEquals(NewEQPStatusRecord.get(13), OrgiDesti, "headload_dest_facility_cd is wrong"); // headload_dest_facility_cd
		SAssert.assertEquals(NewEQPStatusRecord.get(14), terminalcd, "eqps headload_origin_facility_CD is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEQPStatusRecord.get(i));
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
			SAssert.assertEquals(Currentwbti.get(3), WbtRecord.get(i).get(3),
					Currentwbti.get(0) + " Waybill_Transaction_End_TS is wrong");// Waybill_Transaction_End_TS
			SAssert.assertEquals(Currentwbti.get(2), WbtRecord.get(i).get(2),
					Currentwbti.get(0) + " manifest_destination is wrong");
			SAssert.assertEquals(Currentwbti.get(4), "LH.LDG", Currentwbti.get(0) + " source_modify_ID is wrong");
			// CHECK THE WAYBILL TRANSACTION END TS ARE NOT IDENTICAL
			// if(i>0) SAssert.assertTrue(TS.after(f2),"waybill
			// Waybill_Transaction_End_TS is not ascending increase :
			// waybill_transaction_end_ts "+TS+" waybill_transaction_end_ts of
			// previous pro is "+f2+" "+" "+Currentwbti.get(0));
			f2 = TS;
			i = i + 1;
		}
		SAssert.assertAll();
	}

	@Test(priority = 4, dataProvider = "6.07", dataProviderClass = DataForUS607.class, description = "help test 6.07 ldd screen uc 2", groups = {
			"ldg uc" })
	public void ldgTrailerWithoutProsLDDClickYes(String terminalcd, String SCAC, String TrailerNB, String OrgiDesti,
			String cube, String HLDestination, String HLCube)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		// ADD PRO
		ArrayList<String> PRO = DataCommon.GetProNotInAnyTrailer();
		page.RemoveProButton.click();
		ArrayList<String> Addpro = new ArrayList<String>();
		for (int i = 0; i < 2; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			Addpro.add(CurrentPro);
		}
		// change cube
		int Ran1 = (int) (Math.random() * 99) + 1;
		String NewCube1 = Integer.toString(Ran1);
		page.SetCube(NewCube1);

		page.SubmitButton1.click();

		w2.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Trailer " + page.SCACTrailer(SCAC, TrailerNB) + " updated to LDG"));
		w2.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField, "pro(s) loaded."));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w1.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		// go ldd screen
		page.SetStatus("ldd");
		// repull the trailer

		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		// enter seal
		if (page.SealField.getAttribute("value").equalsIgnoreCase("__________")) {
			int Ran2 = (int) (Math.random() * 999998999) + 1000;
			String NewSeal = Integer.toString(Ran2);
			page.SetSealLDD(NewSeal);
		}
		// change destination
		String[] dest = { "851", "112", "841", "270", "198", "135" };
		int ran = new Random().nextInt(dest.length);
		String changeDesti = dest[ran];
		while (changeDesti.equalsIgnoreCase(OrgiDesti)) {
			changeDesti = dest[new Random().nextInt(dest.length)];
		}
		page.SetDestination(changeDesti);

		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Mark PROs as Headload"));
		w1.until(ExpectedConditions.visibilityOf(page.HeadloadProForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		SAssert.assertEquals(page.HeadloadDestination.getAttribute("value"), OrgiDesti, "screenHldestination is wrong");
		SAssert.assertEquals(page.HLCubeField.getAttribute("value"), NewCube1, "screenHLcube is wrong");
		SAssert.assertEquals(page.ManifestToField.getText(), OrgiDesti, "screen manifest dest is wrong");
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti, "screen desti is wrong");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), NewCube1, "screen cube is wrong");
		page.hlCancelButton.click();// click cancel
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Closed"));
		w1.until(ExpectedConditions.visibilityOf(page.ProListLDDForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		Assert.assertEquals(page.DestinationField.getAttribute("value"), OrgiDesti);
		// input cube if there is no cube value before
		if (page.CubeField.getAttribute("value").equalsIgnoreCase("")) {
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			NewCube1 = NewCube;
			page.SetCube(NewCube1);
		}
		if (page.SealField.getAttribute("value").equalsIgnoreCase("__________")) {
			int Ran2 = (int) (Math.random() * 999998999) + 1000;
			String NewSeal = Integer.toString(Ran2);
			page.SetSealLDD(NewSeal);
		}
		// change destination
		page.SetDestination(changeDesti);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Mark PROs as Headload"));
		w1.until(ExpectedConditions.visibilityOf(page.HeadloadProForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		SAssert.assertEquals(page.HeadloadDestination.getAttribute("value"), OrgiDesti, "screenHldestination is wrong");
		SAssert.assertEquals(page.HLCubeField.getAttribute("value"), NewCube1, "screenHLcube is wrong");
		SAssert.assertEquals(page.ManifestToField.getText(), OrgiDesti, "screen manifest dest is wrong");
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti, "screen desti is wrong");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), NewCube1, "screen cube is wrong");
		ArrayList<Object> EQPStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		ArrayList<ArrayList<Object>> WbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), 0, 34);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		builder.sendKeys(Keys.ENTER).build().perform();// PRESS ENTER KEY
		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.visibilityOf(page.ProListLDDForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		Thread.sleep(5000);
		// check eqpststus
		ArrayList<Object> NewEQPStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEQPStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong"); // equipment_dest_facility_cd
		SAssert.assertEquals(NewEQPStatusRecord.get(12), NewCube1, "Headload_Capacity_Consumed_PC is wring"); // Headload_Capacity_Consumed_PC
		SAssert.assertEquals(NewEQPStatusRecord.get(13), OrgiDesti, "headload_dest_facility_cd is wrong"); // headload_dest_facility_cd
		SAssert.assertEquals(NewEQPStatusRecord.get(14), terminalcd, "headload_origin_facility_CD is wrong"); // headload_origin_facility_CD
		SAssert.assertEquals(NewEQPStatusRecord.get(0), "LDD", "STATUS IS NOT SET TO LDD");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEQPStatusRecord.get(i));
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
			SAssert.assertEquals(Currentwbti.get(3), WbtRecord.get(i).get(3),
					Currentwbti.get(0) + " Waybill_Transaction_End_TS is wrong");
			SAssert.assertEquals(Currentwbti.get(2), WbtRecord.get(i).get(2),
					Currentwbti.get(0) + " manifest_destination is wrong");
			SAssert.assertEquals(Currentwbti.get(4), "LH.LDD", Currentwbti.get(0) + " source_modify_ID is wrong");
			// CHECK THE WAYBILL TRANSACTION END TS ARE NOT IDENTICAL
			// if(i>0) SAssert.assertTrue(TS.after(f2),"waybill
			// Waybill_Transaction_End_TS is not ascending increase :
			// waybill_transaction_end_ts "+TS+" waybill_transaction_end_ts of
			// previous pro is "+f2+" "+" "+Currentwbti.get(0));
			f2 = TS;
			i = i + 1;
		}
		SAssert.assertAll();

	}

}
