package TestCase.LdgScreen;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Function.CommonFunction;
import Function.ConfigRd;
import Function.DataCommon;
import Page.EqpStatusPageS;

public class US494Open4BlobrLdgWhenPorsNotInLoading {

	private WebDriver driver;
	private EqpStatusPageS page;
	private ConfigRd Conf;

	@BeforeClass(groups = { "ldg uc" })
	@Parameters({ "browser" })
	public void SetUp(@Optional("chrome") String browser) throws AWTException, InterruptedException, IOException {
		Conf = new ConfigRd();
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
		page.SetStatus("ldg");
	}

	@Test(priority = 1, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class, description = "ldg trailer with pro not in loading type, 4b lobr, Dock", groups = {
			"ldg uc" })
	public void LDGTrailerWithProNotInLoadingAndDock(String terminalcd, String SCAC, String TrailerNB, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// check date&time pre populate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, MReqpst);
		SAssert.assertEquals(picker, expect, "lobr screen prepopulate time is wrong ");

		// check lobr pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SAssert.assertEquals(ProInfo, DataCommon.GetProNotInLoadingListLOBR(SCAC, TrailerNB),
				" lobr pro grid is wrong");
		ArrayList<String> prolistbeforeDisposition = DataCommon.GetProNotInLoadingOnTrailer(SCAC, TrailerNB);

		// change destination
		String changeDesti = page.ChangeDestiantion();

		// enter cube
		String NewCube = page.ChangeCube();

		// SetTime
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// handle the left pro
		page.HandleLOBRproAll("Dock");
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));

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
		for (String RemovedPro : prolistbeforeDisposition) {
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
		SAssert.assertEquals(DataCommon.GetProList(SCAC, TrailerNB), ProInfo1, "pro grid is wrong");
		SAssert.assertAll();
	}

	@Test(priority = 2, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class, description = " ldg trailer with pro not in loading, lobr, all short", groups = {
			"ldg uc" })
	public void LDGTrailerWithProNotInLoadingAndALLSHORT(String terminalcd, String SCAC, String TrailerNB, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// check lobr date&time pre populate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, MReqpst);
		SAssert.assertEquals(picker, expect, "lobr screen prepopulate time is wrong ");

		// check lobr pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SAssert.assertEquals(ProInfo, DataCommon.GetProNotInLoadingListLOBR(SCAC, TrailerNB),
				" lobr pro grid is wrong");
		ArrayList<String> prolistbeforeDisposition = DataCommon.GetProNotInLoadingOnTrailer(SCAC, TrailerNB);

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

		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		// (new WebDriverWait(driver,
		// 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
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
		for (String RemovedPro : prolistbeforeDisposition) {
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
		SAssert.assertEquals(DataCommon.GetProList(SCAC, TrailerNB), ProInfo1, "pro grid is wrong");
		SAssert.assertAll();
	}

	@Test(priority = 3, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class, description = "ldg trailer with pro not in loading, lobr, headload", groups = {
			"ldg uc" })
	public void LDGTrailerWithProNotInLoadingAndHeadLoad(String terminalcd, String SCAC, String TrailerNB, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// check lobr date&time pre populate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, MReqpst);
		SAssert.assertEquals(picker, expect, "lobr screen prepopulate time is wrong ");

		// check lobr pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SAssert.assertEquals(ProInfo, DataCommon.GetProNotInLoadingListLOBR(SCAC, TrailerNB),
				" lobr pro grid is wrong");
		ArrayList<String> prolistbeforeDisposition = DataCommon.GetProNotInLoadingOnTrailer(SCAC, TrailerNB);

		// change destination
		String changeDesti = page.ChangeDestiantion();

		// enter cube
		String NewCube = page.ChangeCube();

		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), 1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		page.HandleLOBRproAll("headload");
		String headload_dest = page.HeadloadDestination.getAttribute("value");
		String headloadCube = page.HeadloadCube.getAttribute("value");
		Date d = CommonFunction.gettime("UTC");

		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		// (new WebDriverWait(driver,
		// 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
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
		ArrayList<ArrayList<Object>> NewWbtRecord = DataCommon.CheckProNotInLoadingUpdateForHL(SCAC, TrailerNB);
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
		SAssert.assertEquals(ProInfo2, DataCommon.GetProList(SCAC, TrailerNB), " ldg pro grid is wrong");

		SAssert.assertAll();
	}

	@Test(priority = 4, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class, description = "ldg trailer with pro not in loading, lobr, leave on", groups = {
			"ldg uc" })
	public void LDGTrailerWithProNotInLoadingAndLeaveOn(String terminalcd, String SCAC, String TrailerNB, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// check lobr date&time pre populate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, MReqpst);
		SAssert.assertEquals(picker, expect, "lobr screen prepopulate time is wrong ");

		// check lobr pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SAssert.assertEquals(ProInfo, DataCommon.GetProNotInLoadingListLOBR(SCAC, TrailerNB),
				" lobr pro grid is wrong");
		ArrayList<String> prolistbeforeDisposition = DataCommon.GetProNotInLoadingOnTrailer(SCAC, TrailerNB);

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

		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		// (new WebDriverWait(driver,
		// 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
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
		for (int i = 0; i < prolistbeforeDisposition.size(); i++) {
			String CurrentPro = prolistbeforeDisposition.get(i);
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
		SAssert.assertEquals(ProInfo1, DataCommon.GetProList(SCAC, TrailerNB), "pro grid is wrong");

		SAssert.assertAll();
	}

	@AfterMethod
	public void SetBackToLDG() throws InterruptedException {
		page.SetStatus("ldg");
	}
}
