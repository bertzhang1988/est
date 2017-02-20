package TestCase.LDDscreen;

import java.awt.AWTException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Data.DataForUS200091;
import Function.CommonFunction;
import Function.DataCommon;
import Function.SetupBrowser;
import Page.EqpStatusPageS;

public class US200091SmartEnterLDD extends SetupBrowser {
	private EqpStatusPageS page;
	private Actions builer;
	private WebDriverWait w1;
	private WebDriverWait w2;

	@BeforeClass
	public void SetUp() throws AWTException, InterruptedException {
		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 50);
		w2 = new WebDriverWait(driver, 180);
		driver.get(conf.GetURL());
		driver.manage().window().maximize();
		page.SetStatus("ldd");
		builer = new Actions(driver);
	}

	@AfterMethod
	public void goback() throws InterruptedException {
		page.SetStatus("ldd");
	}

	@Test(priority = 1, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class, description = "2000.107 can transit to ldd, not in ldd, not pro")
	public void NLDDTrailerNoPRO(String terminalcd, String SCAC, String TrailerNB, String destination, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// change destination
		destination = page.ChangeDestiantion();

		// enter shipcount
		String ShipCount = page.ChangeShipCount();

		// enter shipweight
		String Shipweight = page.ChangeShipWeight();

		// enter cube
		String NewCube = page.ChangeCube();

		// enter seal
		String NewSeal = page.ChangeSeal();

		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// click enter
		builer.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));

		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDD", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), destination, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDD", "Source_Create_ID is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(9), ShipCount, "Observed_Shipment_QT is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(10), Shipweight, "Observed_Weight_QT is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(11), NewSeal, "Seal_NB is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SAssert.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		SAssert.assertAll();
	}

	@Test(priority = 2, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class, description = "2000.108 ldg, with pro")
	public void NLDDTrailerHasPRO(String terminalcd, String SCAC, String TrailerNB, String destination, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// check shipmentcount is disabled
		SAssert.assertFalse(page.ShipmentCount2.isEnabled(), "shipment count is not disbaled");

		// enter shipweight
		String Shipweight = page.ChangeShipWeight();

		// enter cube
		String NewCube = page.ChangeCube();

		// enter seal
		String NewSeal = page.ChangeSeal();

		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// click enter
		builer.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDD", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), destination, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDD", "Source_Create_ID is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(10), Shipweight, "Observed_Shipment_QT is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(11), NewSeal, "Seal_NB is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SAssert.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		SAssert.assertAll();
	}

	@Test(priority = 3, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class, description = "2000.109 ldd with pro, change desti, headload")
	public void LDDTrailerHasProChangeDestination(String terminalcd, String SCAC, String TrailerNB, String destination,
			Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// enter seal
		if (page.SealField.getAttribute("value").replace("_", "").equalsIgnoreCase("")) {
			page.ChangeSeal();
		}

		// change destination
		String changeDesti = page.ChangeDestiantion();

		w2.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Mark PROs as Headload"));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		ArrayList<ArrayList<Object>> WbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		// System.out.println(driver.switchTo().activeElement().toString());
		// SAssert.assertEquals(page.YesButton,
		// driver.switchTo().activeElement(), "the cursor is not in YES
		// button");
		if (page.SealField.getAttribute("value").equalsIgnoreCase("__________")) {
			page.ChangeSeal();
		}

		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		builer.sendKeys(Keys.ENTER).build().perform();
		// page.YesButton.click();
		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Closed"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));

		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDD", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDD", "Source_Create_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SAssert.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
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
			// if(i>0) SA.assertTrue(TS.after(f2),"waybill
			// Waybill_Transaction_End_TS is not ascending increase :
			// waybill_transaction_end_ts "+TS+" waybill_transaction_end_ts of
			// previous pro is "+f2+" "+" "+Currentwbti.get(0));
			f2 = TS;
			i = i + 1;
		}

		SAssert.assertAll();
	}

	@Test(priority = 4, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class, description = "2000.110 ldd no pro, change desti")
	public void LDDTrailerNoProChangeDestination(String terminalcd, String SCAC, String TrailerNB, String destination,
			Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		// enter seal
		if (page.SealField.getAttribute("value").replace("_", "").equalsIgnoreCase("")) {
			page.ChangeSeal();
		}
		// change destination
		String changeDesti = page.ChangeDestiantion();

		// Check date and time prepopulate
		SAssert.assertTrue(page.DateInput.isEnabled(), "Date$time field is not enabled");
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, MReqpst);
		SAssert.assertEquals(picker, expect, "ldd screen prepopulate time is wrong ");

		// enter seal if blank
		if (page.SealField.getAttribute("value").equalsIgnoreCase("__________")) {
			page.ChangeSeal();
		}
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		builer.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));

		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDD", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDD", "Source_Create_ID is wrong");

		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SAssert.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}
		SAssert.assertAll();
	}

	@Test(priority = 5, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class, description = "2000.111 ldd no pro, change non desti field")
	public void LDDTrailerNoProChangeNonDestinationField(String terminalcd, String SCAC, String TrailerNB,
			String destination, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<Object> OldEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);

		// enter cube
		String NewCube = page.ChangeCube();

		// enter shipcount
		String ShipCount = page.ChangeShipCount();

		// check time
		SAssert.assertTrue(page.DateInput.isEnabled(), "Date$time field is not enabled");

		// Check date and time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, MReqpst);
		SAssert.assertEquals(picker, expect, "ldd screen prepopulate time is wrong ");

		// enter shipweight
		String Shipweight = page.ChangeShipWeight();

		// enter seal
		String NewSeal = page.ChangeSeal();
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// enter key
		builer.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		Thread.sleep(5000);
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDD", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), destination, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDD", "Source_Create_ID is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(11), NewSeal, "Seal_NB is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(9), ShipCount, "Observed_Shipment_QT is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(10), Shipweight, "Observed_Weight_QT is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SAssert.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}
		SAssert.assertAll();
	}

	@Test(priority = 6, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class, description = "2000.114 ldd with pro, change non desti field")
	public void LDDTrailerHasProChangeNonDestinationField(String terminalcd, String SCAC, String TrailerNB,
			String destination, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<Object> OldEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);

		// enter shipweight
		String Shipweight = page.ChangeShipWeight();

		// check time
		SAssert.assertTrue(page.DateInput.isEnabled(), "Date$time field is not enabled");

		// Check date and time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, MReqpst);
		SAssert.assertEquals(picker, expect, "ldd screen prepopulate time is wrong ");

		// enter cube
		String NewCube = page.ChangeCube();

		// enter seal
		String NewSeal = page.ChangeSeal();

		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// enter key
		builer.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));// wait
																										// the
																										// alert
																										// gone
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));

		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDD", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), destination, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDD", "Source_Create_ID is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SAssert.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		SAssert.assertEquals(NewEqpStatusRecord.get(10), Shipweight, "Observed_Weight_QT is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(11), NewSeal, "Seal_NB is wrong");

		SAssert.assertAll();
	}

	@Test(priority = 7, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class, description = "2000.117 ldd with pro no change")
	public void LDDTrailerWithoutProNoChange(String terminalcd, String SCAC, String TrailerNB, String destination,
			Date MReqpst) throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<Object> OldEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);

		// enter key
		builer.sendKeys(Keys.ENTER).build().perform();
		// (new WebDriverWait(driver,
		// 50)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,"No
		// updates entered."));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		// (new WebDriverWait(driver,
		// 80)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord, OldEqpStatusRecord, "eqps change");
		SAssert.assertAll();
	}
}