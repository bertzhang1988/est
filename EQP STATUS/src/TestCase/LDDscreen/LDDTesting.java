package TestCase.LDDscreen;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Function.CommonFunction;
import Function.ConfigRd;
import Function.DataCommon;
import Function.Utility;
import Page.EqpStatusPageS;

public class LDDTesting {

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

	@Test(priority = 1, dataProvider = "lddscreen", dataProviderClass = DataForUSLDDLifeTest.class)
	public void NonLDDtrailerNoProSetclosed(String terminalcd, String SCAC, String TrailerNB, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		Date LocalTime = null;
		page.EnterTrailer(SCAC, TrailerNB);

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

		// ENTER DESTINATION
		String[] dest = { "270", "112", "841", "198", "135" };
		int ran = new Random().nextInt(dest.length);
		String destination = dest[ran];
		page.SetDestination(destination);

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
		//
		// add comment
		String Comment = "Seal to " + NewSeal;
		page.AddComment(Comment);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// click enter
		page.SubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
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
		SAssert.assertEquals(NewEqpStatusRecord.get(9), ShipCount, "Observed_Shipment_QT is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(10), Shipweight, "Observed_Weight_QT is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(11), NewSeal, "Seal_NB is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(16), page.AD_ID, "modify_id is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(17), page.M_ID, "eqps Mainframe_User_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SAssert.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		// check eqp
		ArrayList<Object> NewEqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqp.get(0), page.M_ID, " eqp Mainframe_User_ID is wrong");
		SAssert.assertEquals(NewEqp.get(1), Comment, " eqp Equipment_Comment_TX is wrong");
		SAssert.assertAll();

	}

	@Test(priority = 2, dataProvider = "lddscreen", dataProviderClass = DataForUSLDDLifeTest.class, description = "non ldd and non ldg")
	public void NonLDDtrailerHasProSetldg(String terminalcd, String SCAC, String TrailerNB, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		Date CurrentTime = CommonFunction.gettime("UTC");
		Date LocalTime = null;
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

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

		// check lobr pro gtrid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SAssert.assertEquals(ProInfo, DataCommon.GetProListLOBR(SCAC, TrailerNB), " lobr pro grid is wrong");

		// change destination
		String[] dest = { "270", "112", "841", "198", "135" };
		int ran = new Random().nextInt(dest.length);
		String changeDesti = dest[ran];
		page.SetDestination(changeDesti);
		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		try {
			if (page.LeftoverCheckAllPRO.isDisplayed()) {
				page.LeftoverCheckAllPRO.click();
			}
		} catch (Exception e) {

		}
		String[] handleLobrPro = { "headload", "leaveON", "allshort", "dock" };
		int ran1 = new Random().nextInt(handleLobrPro.length);
		page.HandleLOBRproAll(handleLobrPro[ran1]);
		Date d = CommonFunction.gettime("UTC");

		// Assert.assertEquals(page.LeftoverBillForm.findElements(By.xpath("div")).size(),
		// 0);
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
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
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}
		SAssert.assertAll();
	}

	@Test(priority = 3, dataProvider = "lddscreen2", dataProviderClass = DataForUSLDDLifeTest.class)
	public void LDDTrailerHasProChangeDestination(String terminalcd, String SCAC, String TrailerNB, String Desti,
			String AmountPro, String AmountWeight, String Cube, String Seal, String headloadDestination,
			String headloadCube, Date MReqpst, String CurrentStatus, String flag, String serv)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		Date LocalTime = null;
		page.EnterTrailer(SCAC, TrailerNB);

		SA.assertEquals(page.ShipmentCount2.getAttribute("value").replaceAll("_", ""), AmountPro,
				"ship count is wrong");
		SA.assertEquals(page.ShipmentWeight2.getAttribute("value").replaceAll("_", ""), AmountWeight,
				"ship weight is wrong");
		SA.assertEquals(page.DestinationField.getAttribute("value").replaceAll("_", ""), Desti, "destination is wrong");
		SA.assertEquals(page.CubeField.getAttribute("value"), Cube, "cube is wrong");
		SA.assertEquals(page.SealField.getAttribute("value").replaceAll("_", ""), Seal, "seal is wrong");
		SA.assertEquals(page.HeadloadDestination.getAttribute("value"), headloadDestination, "ship count is wrong");
		SA.assertEquals(page.HeadloadCube.getAttribute("value"), headloadCube, "ship weight is wrong");
		SA.assertEquals(page.ShipmentFlag.getText(), flag, " flag is wrong");
		SA.assertEquals(page.ServiceFlag.getText(), serv, "serv is wrong");

		// check date&time field 1. ldd use equipment_status_ts
		LocalTime = CommonFunction.getLocalTime(terminalcd, MReqpst);
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
		if (MReqpst.after(CurrentTime)) {
			SA.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne, "TIME MINUTE IS WRONG");
		} else {
			SA.assertEquals(page.MinuteInput.getAttribute("value"), Minute, "TIME MINUTE IS WRONG");
		}

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListSecondForm);
		SA.assertEquals(ProInfo, DataCommon.GetProList(SCAC, TrailerNB), "prolist information is wrong");

		ArrayList<ArrayList<Object>> WbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);

		// change destination
		String[] dest = { "270", "112", "841", "198", "135" };
		int ran = new Random().nextInt(dest.length);
		String changeDesti = dest[ran];
		while (Desti.equalsIgnoreCase(changeDesti)) {
			changeDesti = dest[new Random().nextInt(dest.length)];
		}
		page.SetDestination(changeDesti);

		// navigate to headload screen
		(new WebDriverWait(driver, 30))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Mark PROs as Headload"));
		(new WebDriverWait(driver, 30)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		// System.out.println(driver.switchTo().activeElement().toString());
		// SAssert.assertEquals(page.YesButton,
		// driver.switchTo().activeElement(), "the cursor is not in YES
		// button");

		if (page.SealField.getAttribute("value").equalsIgnoreCase("__________")) {
			int Ran2 = (int) (Math.random() * 999998999) + 1000;
			String NewSeal = Integer.toString(Ran2);
			page.SetSealLDD(NewSeal);
			Seal = NewSeal;
		}
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		page.YesButton.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 30))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Closed"));
		(new WebDriverWait(driver, 30)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		// CHECK EQPS
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "LDD", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "LH.LDD", "Source_Create_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(11), Seal, "Seal_NB is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
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
			SA.assertEquals(Currentwbti.get(4), "LH.LDD", Currentwbti.get(0) + " source_modify_ID is wrong");
			// CHECK THE WAYBILL TRANSACTION END TS ARE NOT IDENTICAL
			// if(i>0) SA.assertTrue(TS.after(f2),"waybill
			// Waybill_Transaction_End_TS is not ascending increase :
			// waybill_transaction_end_ts "+TS+" waybill_transaction_end_ts of
			// previous pro is "+f2+" "+" "+Currentwbti.get(0));
			f2 = TS;
			i = i + 1;
		}

		SA.assertAll();
	}

	@Test(priority = 4, dataProvider = "lddscreen2", dataProviderClass = DataForUSLDDLifeTest.class)
	public void LDDTrailerWithoutProAlterCubeAndSeal(String terminalcd, String SCAC, String TrailerNB, String Desti,
			String AmountPro, String AmountWeight, String Cube, String Seal, String headloadDestination,
			String headloadCube, Date MReqpst, String CurrentStatus, String flag, String serv)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		Date LocalTime = null;
		page.EnterTrailer(SCAC, TrailerNB);

		SA.assertEquals(page.ShipmentCount2.getAttribute("value").replaceAll("_", ""), AmountPro,
				"ship count is wrong");
		SA.assertEquals(page.ShipmentWeight2.getAttribute("value").replaceAll("_", ""), AmountWeight,
				"ship weight is wrong");
		SA.assertEquals(page.DestinationField.getAttribute("value").replaceAll("_", ""), Desti, "destination is wrong");
		SA.assertEquals(page.CubeField.getAttribute("value"), Cube, "cube is wrong");
		SA.assertEquals(page.SealField.getAttribute("value").replaceAll("_", ""), Seal, "seal is wrong");
		SA.assertEquals(page.HeadloadDestination.getAttribute("value"), headloadDestination, "ship count is wrong");
		SA.assertEquals(page.HeadloadCube.getAttribute("value"), headloadCube, "ship weight is wrong");
		SA.assertEquals(page.ShipmentFlag.getText(), flag, " flag is wrong");
		SA.assertEquals(page.ServiceFlag.getText(), serv, "serv is wrong");
		// check date&time field 1. ldd use equipment_status_ts

		LocalTime = CommonFunction.getLocalTime(terminalcd, MReqpst);
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
		if (MReqpst.after(CurrentTime)) {
			SA.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne, "TIME MINUTE IS WRONG");
		} else {
			SA.assertEquals(page.MinuteInput.getAttribute("value"), Minute, "TIME MINUTE IS WRONG");
		}

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListSecondForm);
		SA.assertEquals(ProInfo, DataCommon.GetProList(SCAC, TrailerNB), "prolist information is wrong");

		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		// enter seal
		int Ran2 = (int) (Math.random() * 999998999) + 1000;
		String NewSeal = Integer.toString(Ran2);
		page.SetSealLDD(NewSeal);

		// add comment
		String Comment = "Seal to " + NewSeal;
		page.AddComment(Comment);
		ArrayList<Object> OldEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		// click submit
		page.SubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// CHECK EQPS
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "LDD", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(19), "LH.LDD", "Source_Modify_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(11), NewSeal, "Seal_NB is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(16), page.AD_ID, "modify_id is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(17), page.M_ID, "eqps Mainframe_User_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			if (i == 5) {
				Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000,
						"EQPS MODIFY_TS IS WRONG  " + TS + "  " + d);
			} else {
				SA.assertEquals(NewEqpStatusRecord.get(i), OldEqpStatusRecord.get(i),
						"eqps time stamp " + i + " is wrong");
			}
		}
		// check eqp
		ArrayList<Object> NewEqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		SA.assertEquals(NewEqp.get(0), page.M_ID, " eqp Mainframe_User_ID is wrong");
		SA.assertEquals(NewEqp.get(1), Comment, " eqp Equipment_Comment_TX is wrong");
		SA.assertAll();
	}

	@Test(priority = 5, dataProvider = "lddscreen2", dataProviderClass = DataForUSLDDLifeTest.class)
	public void LDGtrailerWithProSetToclosed(String terminalcd, String SCAC, String TrailerNB, String Desti,
			String AmountPro, String AmountWeight, String Cube, String Seal, String headloadDestination,
			String headloadCube, Date MReqpst, String CurrentStatus, String flag, String serv)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		Date LocalTime = null;
		page.EnterTrailer(SCAC, TrailerNB);

		SA.assertEquals(page.ShipmentCount2.getAttribute("value").replaceAll("_", ""), AmountPro,
				"ship count is wrong");
		SA.assertEquals(page.ShipmentWeight2.getAttribute("value").replaceAll("_", ""), AmountWeight,
				"ship weight is wrong");
		SA.assertEquals(page.DestinationField.getAttribute("value").replaceAll("_", ""), Desti, "destination is wrong");
		SA.assertEquals(page.CubeField.getAttribute("value"), Cube, "cube is wrong");
		SA.assertEquals(page.SealField.getAttribute("value").replaceAll("_", ""), Seal, "seal is wrong");
		SA.assertEquals(page.HeadloadDestination.getAttribute("value"), headloadDestination, "ship count is wrong");
		SA.assertEquals(page.HeadloadCube.getAttribute("value"), headloadCube, "ship weight is wrong");
		SA.assertEquals(page.ShipmentFlag.getText(), flag, " flag is wrong");
		SA.assertEquals(page.ServiceFlag.getText(), serv, "serv is wrong");
		// check date&time field 1. ldd use equipment_status_ts 2.not ldd, a.
		// eqpst>current-time use eqpst minute+1 b. eqpst<current time use
		// current time

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

		SA.assertEquals(page.DateInput.getAttribute("value"), DATE, "TIME DARE IS WRONG");
		SA.assertEquals(page.HourInput.getAttribute("value"), hour, "TIME HOR IS WRONG");
		if (MReqpst.after(CurrentTime)) {
			SA.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne, "TIME MINUTE IS WRONG");
		} else {
			SA.assertEquals(page.MinuteInput.getAttribute("value"), Minute, "TIME MINUTE IS WRONG");
		}

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListSecondForm);
		SA.assertEquals(ProInfo, DataCommon.GetProList(SCAC, TrailerNB), "prolist information is wrong");

		// enter seal
		int Ran2 = (int) (Math.random() * 999998999) + 1000;
		String NewSeal = Integer.toString(Ran2);
		page.SetSealLDD(NewSeal);

		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// add comment
		String Comment = "Seal to " + NewSeal;
		page.AddComment(Comment);

		// click submit
		page.SubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "LDD", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(19), "LH.LDD", "Source_Modify_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(11), NewSeal, "Seal_NB is wrong");
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
		// check eqp
		ArrayList<Object> NewEqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		SA.assertEquals(NewEqp.get(0), page.M_ID, " eqp Mainframe_User_ID is wrong");
		SA.assertEquals(NewEqp.get(1), Comment, " eqp Equipment_Comment_TX is wrong");
		SA.assertAll();
	}

	@AfterMethod()
	public void getbackldg(ITestResult result) throws InterruptedException {
		if (result.getStatus() == ITestResult.FAILURE) {

			String Testparameter = Arrays.toString(Arrays.copyOf(result.getParameters(), 3)).replaceAll("[^\\d.a-zA-Z]",
					"");
			String FailureTestparameter = result.getName() + Testparameter;

			Utility.takescreenshot(driver, FailureTestparameter);
			page.ChangeStatusTo("LDD");
		}
	}

	@AfterClass
	public void Close() {
		driver.close();
	}

}
