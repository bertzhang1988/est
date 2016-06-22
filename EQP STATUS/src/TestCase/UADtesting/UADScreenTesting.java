package TestCase.UADtesting;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Function.CommonFunction;
import Function.ConfigRd;
import Function.DataCommon;
import Page.EqpStatusPageS;

public class UADScreenTesting {

	private WebDriver driver;
	private EqpStatusPageS page;

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
		driver.get(page.sit1);
		driver.manage().window().maximize();
		page.SetStatus("UAD");
	}

	@Test(priority = 1, dataProvider = "UADscreen", dataProviderClass = DataForUADTesting.class, enabled = false)
	public void SetONHWithProToUAD(String terminalcd, String SCAC, String TrailerNB, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		page.SubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "UAD", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "UAD", "Source_Create_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000,
					"equipment status table" + i + "  " + TS + "  " + d);
		}
		SA.assertAll();
	}

	// @Test(priority=2,dataProvider =
	// "UADscreen",dataProviderClass=DataForUADTesting.class, enabled=false)
	public void SetONHWithoutProToUAD(String terminalcd, String SCAC, String TrailerNB, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<Object> Eqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		page.SubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "UAD", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "UAD", "Source_Create_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(16), page.AD_ID, "modify_id is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(17), page.M_ID, "eqps Mainframe_User_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000,
					"equipment status table" + i + "  " + TS + "  " + d);
		}

		ArrayList<Object> NewEqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		SA.assertEquals(NewEqp.get(0), Eqp.get(0), " eqp Mainframe_User_ID is wrong");
		SA.assertAll();
	}

	// @Test(priority=3,dataProvider =
	// "UADscreen",dataProviderClass=DataForUADTesting.class, enabled=false)
	public void SetLdgNotONHWithProToUAD(String terminalcd, String SCAC, String TrailerNB, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		page.SubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "UAD", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "UAD", "Source_Create_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000,
					"equipment status table" + i + "  " + TS + "  " + d);
		}
		SA.assertAll();
	}

	// @Test(priority=4,dataProvider =
	// "UADscreen",dataProviderClass=DataForUADTesting.class, enabled=false)
	public void SetLdgNotONHWithoutProToUAD(String terminalcd, String SCAC, String TrailerNB, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		page.SubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "UAD", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "UAD", "Source_Create_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000,
					"equipment status table" + i + "  " + TS + "  " + d);
		}
		SA.assertAll();
	}

	@Test(priority = 1, dataProvider = "UADscreen", dataProviderClass = DataForUADTesting.class)
	public void SetONHToUAD(String terminalcd, String SCAC, String TrailerNB, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		Date CurrentTime = CommonFunction.gettime("UTC");
		Date LocalTime = null;

		// check date&time field should a. eqpst>current-time use eqpst minute+1
		// b. eqpst<current time use current time
		if (MRSts.before(CurrentTime)) {
			LocalTime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		} else if (MRSts.after(CurrentTime)) {
			LocalTime = CommonFunction.getLocalTime(terminalcd, MRSts);
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

		SA.assertEquals(page.DateInput.getAttribute("value"), DATE, "TIME DARE IS WRONG");
		SA.assertEquals(page.HourInput.getAttribute("value"), hour, "TIME HOR IS WRONG");
		if (MRSts.after(CurrentTime)) {
			SA.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne, "TIME MINUTE IS WRONG");
		} else {
			SA.assertEquals(page.MinuteInput.getAttribute("value"), Minute, "TIME MINUTE IS WRONG");
		}
		// alter time
		page.SetDatePicker2(page.GetDatePickerTime(), -1, 0);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		ArrayList<Object> Eqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		page.SubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		// (new WebDriverWait(driver,
		// 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "UAD", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "UAD", "Source_Create_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(16), page.AD_ID, "modify_id is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(17), page.M_ID, "eqps Mainframe_User_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		ArrayList<Object> NewEqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		SA.assertEquals(NewEqp.get(0), Eqp.get(0), " eqp Mainframe_User_ID is wrong");
		SA.assertAll();
	}

	@Test(priority = 2, dataProvider = "UADscreen", dataProviderClass = DataForUADTesting.class)
	public void SetNOTONHToUAD(String terminalcd, String SCAC, String TrailerNB, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		Date CurrentTime = CommonFunction.gettime("UTC");
		Date LocalTime = null;
		// check date&time field should a. eqpst>current-time use eqpst minute+1
		// b. eqpst<current time use current time
		if (MRSts.before(CurrentTime)) {
			LocalTime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		} else if (MRSts.after(CurrentTime)) {
			LocalTime = CommonFunction.getLocalTime(terminalcd, MRSts);
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

		SA.assertEquals(page.DateInput.getAttribute("value"), DATE, "TIME DARE IS WRONG");
		SA.assertEquals(page.HourInput.getAttribute("value"), hour, "TIME HOR IS WRONG");
		if (MRSts.after(CurrentTime)) {
			SA.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne, "TIME MINUTE IS WRONG");
		} else {
			SA.assertEquals(page.MinuteInput.getAttribute("value"), Minute, "TIME MINUTE IS WRONG");
		}
		ArrayList<Object> Eqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		// alter time
		page.SetDatePicker2(page.GetDatePickerTime(), -3, -59);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		page.SubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "UAD", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "UAD", "Source_Create_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(16), page.AD_ID, "modify_id is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(17), page.M_ID, "eqps Mainframe_User_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		ArrayList<Object> NewEqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		SA.assertEquals(NewEqp.get(0), page.M_ID, " eqp Mainframe_User_ID is wrong");
		SA.assertAll();
	}

	@Test(priority = 3, dataProvider = "UADscreen", dataProviderClass = DataForUADTesting.class)
	public void SmartEnterSetONHToUAD(String terminalcd, String SCAC, String TrailerNB, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		Actions builder = new Actions(driver);
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		Date CurrentTime = CommonFunction.gettime("UTC");
		Date LocalTime = null;

		// check date&time field should a. eqpst>current-time use eqpst minute+1
		// b. eqpst<current time use current time
		if (MRSts.before(CurrentTime)) {
			LocalTime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		} else if (MRSts.after(CurrentTime)) {
			LocalTime = CommonFunction.getLocalTime(terminalcd, MRSts);
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

		SA.assertEquals(page.DateInput.getAttribute("value"), DATE, "TIME DARE IS WRONG");
		SA.assertEquals(page.HourInput.getAttribute("value"), hour, "TIME HOR IS WRONG");
		if (MRSts.after(CurrentTime)) {
			SA.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne, "TIME MINUTE IS WRONG");
		} else {
			SA.assertEquals(page.MinuteInput.getAttribute("value"), Minute, "TIME MINUTE IS WRONG");
		}
		// alter time
		page.SetDatePicker2(page.GetDatePickerTime(), -4, -59);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		ArrayList<Object> Eqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		builder.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "UAD", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "UAD", "Source_Create_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(16), page.AD_ID, "modify_id is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(17), page.M_ID, "eqps Mainframe_User_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		ArrayList<Object> NewEqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		SA.assertEquals(NewEqp.get(0), Eqp.get(0), " eqp Mainframe_User_ID is wrong");
		SA.assertAll();
	}

	@Test(priority = 4, dataProvider = "UADscreen", dataProviderClass = DataForUADTesting.class)
	public void SmartEnterSetNOTONHToUAD(String terminalcd, String SCAC, String TrailerNB, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		Actions builder = new Actions(driver);
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		Date CurrentTime = CommonFunction.gettime("UTC");
		Date LocalTime = null;
		// check date&time field should a. eqpst>current-time use eqpst minute+1
		// b. eqpst<current time use current time
		if (MRSts.before(CurrentTime)) {
			LocalTime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		} else if (MRSts.after(CurrentTime)) {
			LocalTime = CommonFunction.getLocalTime(terminalcd, MRSts);
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

		SA.assertEquals(page.DateInput.getAttribute("value"), DATE, "TIME DARE IS WRONG");
		SA.assertEquals(page.HourInput.getAttribute("value"), hour, "TIME HOR IS WRONG");
		if (MRSts.after(CurrentTime)) {
			SA.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne, "TIME MINUTE IS WRONG");
		} else {
			SA.assertEquals(page.MinuteInput.getAttribute("value"), Minute, "TIME MINUTE IS WRONG");
		}
		// alter time
		page.SetDatePicker2(page.GetDatePickerTime(), -4, -59);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		ArrayList<Object> Eqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		builder.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "UAD", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "UAD", "Source_Create_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(16), page.AD_ID, "modify_id is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(17), page.M_ID, "eqps Mainframe_User_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		ArrayList<Object> NewEqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		SA.assertEquals(NewEqp.get(0), page.M_ID, " eqp Mainframe_User_ID is wrong");
		SA.assertAll();
	}

	@AfterClass
	public void TearDown() {
		driver.quit();
	}
}
