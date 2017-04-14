package TestCase.CPUtesting;

import java.awt.AWTException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Function.CommonFunction;
import Function.DataCommon;
import Function.Setup;
import Function.Utility;
import Page.EqpStatusPageS;

public class CPUScreenTesting extends Setup {

	private EqpStatusPageS page;
	private WebDriverWait w1;
	private WebDriverWait w2;

	@BeforeClass
	public void SetUp() throws AWTException, InterruptedException {

		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 50);
		w2 = new WebDriverWait(driver, 80);
		  
		driver.manage().window().maximize();
		page.SetStatus("CPU");
	}

	@Test(priority = 1, dataProvider = "CPUScreen", dataProviderClass = DataForCPUScreenTesting.class)
	public void SetTrailerToCPUfromONH(String terminalcd, String SCAC, String TrailerNB, String Desti, String Cube,
			String AmountPro, String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		Date CurrentTime = CommonFunction.gettime("UTC");

		// Check date and time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MRSts);
		SA.assertEquals(picker, expect, "cpu screen prepopulate time is wrong ");
		ArrayList<Object> OldEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), 0, 59);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		ArrayList<Object> Eqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		page.SubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "CPU", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "CPU", "Source_Create_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(16), conf.GetAD_ID(), "modify_id is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(17), conf.GetM_ID(), "eqps Mainframe_User_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(18), "ONH", "eqps M204 equipment_status_type_cd is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		// 507.08
		if (OldEqpStatusRecord.get(2) != null)
			SA.assertEquals(NewEqpStatusRecord.get(2), OldEqpStatusRecord.get(2),
					"Equipment_Dest_Facility_CD not retain");
		if (OldEqpStatusRecord.get(15) != null)
			SA.assertEquals(NewEqpStatusRecord.get(15), OldEqpStatusRecord.get(15),
					"Equipment_Origin_Facility_CD not retain");
		if (OldEqpStatusRecord.get(22) != null)
			SA.assertEquals(NewEqpStatusRecord.get(22), OldEqpStatusRecord.get(22), "SCity_Route_NM not retain");

		// check eqp
		ArrayList<Object> NewEqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		SA.assertEquals(NewEqp.get(0), Eqp.get(0), " eqp Mainframe_User_ID is wrong");
		SA.assertAll();
	}

	@Test(priority = 2, dataProvider = "CPUScreen", dataProviderClass = DataForCPUScreenTesting.class)
	public void SmartEneterSetTrailerToCPUfromONH(String terminalcd, String SCAC, String TrailerNB, String Desti,
			String Cube, String AmountPro, String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		Actions builder = new Actions(driver);
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), 0, 5);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		builder.sendKeys(Keys.ENTER).build().perform();
		ArrayList<Object> OldEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "CPU", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "CPU", "Source_Create_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		// 507.08
		if (OldEqpStatusRecord.get(2) != null)
			SA.assertEquals(NewEqpStatusRecord.get(2), OldEqpStatusRecord.get(2),
					"Equipment_Dest_Facility_CD not retain");
		if (OldEqpStatusRecord.get(15) != null)
			SA.assertEquals(NewEqpStatusRecord.get(15), OldEqpStatusRecord.get(15),
					"Equipment_Origin_Facility_CD not retain");
		if (OldEqpStatusRecord.get(22) != null)
			SA.assertEquals(NewEqpStatusRecord.get(22), OldEqpStatusRecord.get(22), "SCity_Route_NM not retain");

		SA.assertAll();
	}

	@Test(priority = 3, dataProvider = "CPUScreen", dataProviderClass = DataForCPUScreenTesting.class)
	public void SetTrailerToCPUNOTfromONH(String terminalcd, String SCAC, String TrailerNB, String Desti, String Cube,
			String AmountPro, String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		Date CurrentTime = CommonFunction.gettime("UTC");

		// Check date and time prepopulate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MRSts);
		SA.assertEquals(picker, expect, "cpu screen prepopulate time is wrong ");
		ArrayList<Object> OldEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), 0, 59);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		ArrayList<Object> Eqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		page.SubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "CPU", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "CPU", "Source_Create_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(16), conf.GetAD_ID(), "modify_id is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(17), conf.GetM_ID(), "eqps Mainframe_User_ID is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(18), "ONH", "eqps M204 equipment_status_type_cd is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		// 507.08
		if (OldEqpStatusRecord.get(2) != null)
			SA.assertEquals(NewEqpStatusRecord.get(2), OldEqpStatusRecord.get(2),
					"Equipment_Dest_Facility_CD not retain");
		if (OldEqpStatusRecord.get(15) != null)
			SA.assertEquals(NewEqpStatusRecord.get(15), OldEqpStatusRecord.get(15),
					"Equipment_Origin_Facility_CD not retain");
		if (OldEqpStatusRecord.get(22) != null)
			SA.assertEquals(NewEqpStatusRecord.get(22), OldEqpStatusRecord.get(22), "SCity_Route_NM not retain");

		// check eqp
		ArrayList<Object> NewEqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		SA.assertEquals(NewEqp.get(0), conf.GetM_ID(), " eqp Mainframe_User_ID is wrong");
		SA.assertAll();
	}

	@Test(priority = 4, dataProvider = "CPUScreen", dataProviderClass = DataForCPUScreenTesting.class)
	public void SmartEneterSetTrailerToCPUNOTfromONH(String terminalcd, String SCAC, String TrailerNB, String Desti,
			String Cube, String AmountPro, String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		Actions builder = new Actions(driver);
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), 0, 6);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		builder.sendKeys(Keys.ENTER).build().perform();
		ArrayList<Object> OldEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "CPU", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(3), "CPU", "Source_Create_ID is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			if (i == 7) {
				SA.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}

		// 507.08
		if (OldEqpStatusRecord.get(2) != null)
			SA.assertEquals(NewEqpStatusRecord.get(2), OldEqpStatusRecord.get(2),
					"Equipment_Dest_Facility_CD not retain");
		if (OldEqpStatusRecord.get(15) != null)
			SA.assertEquals(NewEqpStatusRecord.get(15), OldEqpStatusRecord.get(15),
					"Equipment_Origin_Facility_CD not retain");
		if (OldEqpStatusRecord.get(22) != null)
			SA.assertEquals(NewEqpStatusRecord.get(22), OldEqpStatusRecord.get(22), "SCity_Route_NM not retain");

		SA.assertAll();
	}

	@AfterMethod()
	public void getbackldg(ITestResult result) throws InterruptedException {
		if (result.getStatus() == ITestResult.FAILURE) {

			String Testparameter = Arrays.toString(Arrays.copyOf(result.getParameters(), 3)).replaceAll("[^\\d.a-zA-Z]",
					"");
			String FailureTestparameter = result.getName() + Testparameter;

			Utility.takescreenshot(driver, FailureTestparameter);
			page.SetStatus("CPU");
		}
	}
}
