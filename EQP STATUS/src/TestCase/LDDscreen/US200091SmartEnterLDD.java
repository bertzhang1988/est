package TestCase.LDDscreen;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Data.DataForUS200091;
import Function.CommonFunction;
import Function.ConfigRd;
import Function.DataCommon;
import Page.EqpStatusPageS;

public class US200091SmartEnterLDD {
	private WebDriver driver;
	private EqpStatusPageS page;
	private Actions builer;

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
		// ENTER DESTINATION IF IT IS BLANK
		if (page.DestinationField.getAttribute("value").equalsIgnoreCase("___")) {
			String[] dest = { "270", "112", "841", "198", "135" };
			int ran = new Random().nextInt(dest.length);
			String changeDesti = dest[ran];
			destination = changeDesti;
			page.SetDestination(destination);
		}

		// enter shipcount
		int Ran3 = (int) (Math.random() * 998) + 1;
		String ShipCount = Integer.toString(Ran3);
		page.SetShipCount(ShipCount);
		// enter shipweight
		int Ran4 = (int) (Math.random() * 27000) + 1000;
		String Shipweight = Integer.toString(Ran4);
		page.SetShipWeight(Shipweight);
		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		// enter seal
		int Ran2 = (int) (Math.random() * 999998999) + 1000;
		String NewSeal = Integer.toString(Ran2);
		page.SetSealLDD(NewSeal);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// click enter
		builer.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));

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
		int Ran4 = (int) (Math.random() * 27000) + 1000;
		String Shipweight = Integer.toString(Ran4);
		page.SetShipWeight(Shipweight);
		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		// enter seal
		int Ran2 = (int) (Math.random() * 999998999) + 1000;
		String NewSeal = Integer.toString(Ran2);
		page.SetSealLDD(NewSeal);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// click enter
		builer.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
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
			int Ran2 = (int) (Math.random() * 999998999) + 1000;
			String NewSeal = Integer.toString(Ran2);
			page.SetSealLDD(NewSeal);
		}
		// change destination
		String[] dest = { "270", "112", "841", "198", "135" };
		int ran = new Random().nextInt(dest.length);
		String changeDesti = dest[ran];
		while (destination.equalsIgnoreCase(changeDesti)) {
			changeDesti = dest[new Random().nextInt(dest.length)];
		}
		page.SetDestination(changeDesti);
		(new WebDriverWait(driver, 150))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Mark PROs as Headload"));
		(new WebDriverWait(driver, 150)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		ArrayList<ArrayList<Object>> WbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		// System.out.println(driver.switchTo().activeElement().toString());
		// SAssert.assertEquals(page.YesButton,
		// driver.switchTo().activeElement(), "the cursor is not in YES
		// button");
		if (page.SealField.getAttribute("value").equalsIgnoreCase("__________")) {
			int Ran2 = (int) (Math.random() * 999998999) + 1000;
			String NewSeal = Integer.toString(Ran2);
			page.SetSealLDD(NewSeal);
		}
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		builer.sendKeys(Keys.ENTER).build().perform();
		// page.YesButton.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 150))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Closed"));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));

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
			int Ran2 = (int) (Math.random() * 999998999) + 1000;
			String NewSeal = Integer.toString(Ran2);
			page.SetSealLDD(NewSeal);
		}
		// change destination
		String[] dest = { "270", "112", "841", "198", "135" };
		int ran = new Random().nextInt(dest.length);
		String changeDesti = dest[ran];
		while (destination.equalsIgnoreCase(changeDesti)) {
			changeDesti = dest[new Random().nextInt(dest.length)];
		}
		page.SetDestination(changeDesti);
		// check time
		Date CurrentTime = CommonFunction.gettime("UTC");
		Date LocalTime = null;
		SAssert.assertTrue(page.DateInput.isEnabled(), "Date$time field is not enabled");
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
		SAssert.assertEquals(page.DateInput.getAttribute("value"), DATE, "TIME DARE IS WRONG");
		SAssert.assertEquals(page.HourInput.getAttribute("value"), hour, "TIME HOR IS WRONG");
		if (MReqpst.after(CurrentTime)) {
			SAssert.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne, "TIME MINUTE IS WRONG");
		} else {
			SAssert.assertEquals(page.MinuteInput.getAttribute("value"), Minute, "TIME MINUTE IS WRONG");
		}

		// enter seal if blank
		if (page.SealField.getAttribute("value").equalsIgnoreCase("__________")) {
			int Ran2 = (int) (Math.random() * 999998999) + 1000;
			String NewSeal = Integer.toString(Ran2);
			page.SetSealLDD(NewSeal);
		}
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		builer.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));

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
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		// enter shipcount
		int Ran3 = (int) (Math.random() * 98) + 1;
		String ShipCount = Integer.toString(Ran3);
		page.SetShipCount(ShipCount);
		// check time
		Date CurrentTime = CommonFunction.gettime("UTC");
		Date LocalTime = null;
		SAssert.assertTrue(page.DateInput.isEnabled(), "Date$time field is not enabled");
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
		SAssert.assertEquals(page.DateInput.getAttribute("value"), DATE, "TIME DARE IS WRONG");
		SAssert.assertEquals(page.HourInput.getAttribute("value"), hour, "TIME HOR IS WRONG");
		if (MReqpst.after(CurrentTime)) {
			SAssert.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne, "TIME MINUTE IS WRONG");
		} else {
			SAssert.assertEquals(page.MinuteInput.getAttribute("value"), Minute, "TIME MINUTE IS WRONG");
		}

		// enter shipweight
		int Ran4 = (int) (Math.random() * 27000) + 1000;
		String Shipweight = Integer.toString(Ran4);
		page.SetShipWeight(Shipweight);
		// enter seal
		int Ran2 = (int) (Math.random() * 999998999) + 1000;
		String NewSeal = Integer.toString(Ran2);
		page.SetSealLDD(NewSeal);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// enter key
		builer.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
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
		int Ran4 = (int) (Math.random() * 27000) + 1000;
		String Shipweight = Integer.toString(Ran4);
		page.SetShipWeight(Shipweight);

		// check time
		Date CurrentTime = CommonFunction.gettime("UTC");
		Date LocalTime = null;
		SAssert.assertTrue(page.DateInput.isEnabled(), "Date$time field is not enabled");
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
		SAssert.assertEquals(page.DateInput.getAttribute("value"), DATE, "TIME DARE IS WRONG");
		SAssert.assertEquals(page.HourInput.getAttribute("value"), hour, "TIME HOR IS WRONG");
		if (MReqpst.after(CurrentTime)) {
			SAssert.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne, "TIME MINUTE IS WRONG");
		} else {
			SAssert.assertEquals(page.MinuteInput.getAttribute("value"), Minute, "TIME MINUTE IS WRONG");
		}
		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		// enter seal
		int Ran2 = (int) (Math.random() * 999998999) + 1000;
		String NewSeal = Integer.toString(Ran2);
		page.SetSealLDD(NewSeal);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// enter key
		builer.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 150))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));// wait
																												// the
																												// alert
																												// gone
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));

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
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		// (new WebDriverWait(driver,
		// 80)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord, OldEqpStatusRecord, "eqps change");
		SAssert.assertAll();
	}

	@AfterTest
	public void Close() {
		driver.close();
	}
}