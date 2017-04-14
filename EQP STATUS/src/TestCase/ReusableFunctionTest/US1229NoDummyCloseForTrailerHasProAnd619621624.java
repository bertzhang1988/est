package TestCase.ReusableFunctionTest;

import java.awt.AWTException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Data.DataForUS1229;
import Function.CommonFunction;
import Function.Setup;
import Page.EqpStatusPageS;

public class US1229NoDummyCloseForTrailerHasProAnd619621624 extends Setup {

	private EqpStatusPageS page;
	private WebDriverWait w1;
	private WebDriverWait w2;

	@BeforeClass
	public void SetUp() throws AWTException, InterruptedException {

		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 50);
		w2 = new WebDriverWait(driver, 80);
		  
		driver.manage().window().maximize();
		page.SetStatus("ldd");

	}

	// @Test(priority=1,dataProvider =
	// "12.29",dataProviderClass=DataForUS1229.class)
	public void MTYTrailerNoPRO(String terminalcd, String SCAC, String TrailerNB, String destination)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		int oldnumber = Integer.parseInt(page.UpdatedTrailerNum.getText());
		String newnumber = Integer.toString(oldnumber + 1);
		// ENTER DESTINATION IF IT IS BLANK
		String desti = page.DestinationField.getAttribute("value");
		if (desti.equalsIgnoreCase("___") || desti.equalsIgnoreCase("")) {
			String[] dest = { "326", "112", "841", "851" };
			int ran = new Random().nextInt(dest.length);
			String changeDesti = dest[ran];
			destination = changeDesti;
			page.SetDestination(destination);
		}
		// enter shipcount
		int Ran3 = (int) (Math.random() * 98) + 1;
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
		// click submit
		page.SubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		(new WebDriverWait(driver, 150))
				.until(ExpectedConditions.textToBePresentInElement(page.UpdatedTrailerNum, newnumber));
		Thread.sleep(2000);
		ArrayList<Object> NewEqpStatusRecord = DataForUS1229.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDD", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), destination, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDD", "Source_Create_ID is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
		}
		SAssert.assertEquals(NewEqpStatusRecord.get(9), ShipCount, "Observed_Shipment_QT is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(10), Shipweight, "Observed_Weight_QT is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(11), NewSeal, "Seal_NB is wrong");
		SAssert.assertAll();
	}

	// @Test(priority=2,dataProvider =
	// "12.29",dataProviderClass=DataForUS1229.class)
	public void OFDTrailerNoPRO(String terminalcd, String SCAC, String TrailerNB, String destination)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		int oldnumber = Integer.parseInt(page.UpdatedTrailerNum.getText());
		String newnumber = Integer.toString(oldnumber + 1);
		// ENTER DESTINATION IF IT IS BLANK
		String desti = page.DestinationField.getAttribute("value");
		if (desti.equalsIgnoreCase("___") || desti.equalsIgnoreCase("")) {
			String[] dest = { "326", "112", "841", "851" };
			int ran = new Random().nextInt(dest.length);
			String changeDesti = dest[ran];
			destination = changeDesti;
			page.SetDestination(destination);
		}
		// enter shipcount
		int Ran3 = (int) (Math.random() * 98) + 1;
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
		// click submit
		page.SubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		(new WebDriverWait(driver, 150))
				.until(ExpectedConditions.textToBePresentInElement(page.UpdatedTrailerNum, newnumber));
		Thread.sleep(2000);
		ArrayList<Object> NewEqpStatusRecord = DataForUS1229.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDD", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), destination, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDD", "Source_Create_ID is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
		}
		SAssert.assertEquals(NewEqpStatusRecord.get(9), ShipCount, "Observed_Shipment_QT is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(10), Shipweight, "Observed_Weight_QT is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(11), NewSeal, "Seal_NB is wrong");
		SAssert.assertAll();
	}

	// @Test(priority=3,dataProvider =
	// "12.29",dataProviderClass=DataForUS1229.class)
	public void SPTTrailerNoPRO(String terminalcd, String SCAC, String TrailerNB, String destination)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		int oldnumber = Integer.parseInt(page.UpdatedTrailerNum.getText());
		String newnumber = Integer.toString(oldnumber + 1);
		// ENTER DESTINATION IF IT IS BLANK
		String desti = page.DestinationField.getAttribute("value");
		if (desti.equalsIgnoreCase("___") || desti.equalsIgnoreCase("")) {
			String[] dest = { "326", "112", "841", "851" };
			int ran = new Random().nextInt(dest.length);
			String changeDesti = dest[ran];
			destination = changeDesti;
			page.SetDestination(destination);
		}
		// enter shipcount
		int Ran3 = (int) (Math.random() * 98) + 1;
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
		// click submit
		page.SubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		(new WebDriverWait(driver, 150))
				.until(ExpectedConditions.textToBePresentInElement(page.UpdatedTrailerNum, newnumber));
		Thread.sleep(2000);
		ArrayList<Object> NewEqpStatusRecord = DataForUS1229.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDD", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), destination, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDD", "Source_Create_ID is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube, "Actual_Capacity_Consumed_PC is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
		}
		SAssert.assertEquals(NewEqpStatusRecord.get(9), ShipCount, "Observed_Shipment_QT is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(10), Shipweight, "Observed_Weight_QT is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(11), NewSeal, "Seal_NB is wrong");
		SAssert.assertAll();
	}

	@Test(priority = 4, dataProvider = "12.29", dataProviderClass = DataForUS1229.class)
	public void MTYTrailerHasPRO(String terminalcd, String SCAC, String TrailerNB, String destination)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		SAssert.assertTrue(page.ErrorAndWarningField.getText().contains(
				"Trailer has PROs. Shipments must be processed from trailer or loaded to trailer before it can be closed."));

		// ENTER DESTINATION IF IT IS BLANK
		String desti = page.DestinationField.getAttribute("value");
		if (desti.equalsIgnoreCase("___") || desti.equalsIgnoreCase("")) {
			String[] dest = { "326", "112", "841", "851" };
			int ran = new Random().nextInt(dest.length);
			String changeDesti = dest[ran];
			destination = changeDesti;
			page.SetDestination(destination);
		}
		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		// handle the left pro
		page.LeftoverCheckAllPRO.click();
		String[] handleLobrPro = { "headload", "leaveON" };
		int ran = new Random().nextInt(handleLobrPro.length);
		page.HandleLOBRproAll(handleLobrPro[ran]);

		(new WebDriverWait(driver, 260)).until(ExpectedConditions.invisibilityOfElementLocated(By.name("abandon")));
		page.LobrSubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		// check screen
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), destination);
		SAssert.assertFalse(page.DateInput.isEnabled(), "date&time field is not disabled");
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataForUS1229.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), destination, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG", "Source_Create_ID is wrong");

		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 180000, i + "  " + TS + "  " + d);
		}

		page.ChangeStatusTo("ldd");
		SAssert.assertAll();
	}

	@Test(priority = 5, dataProvider = "12.29", dataProviderClass = DataForUS1229.class)
	public void SPTTrailerHasPRO(String terminalcd, String SCAC, String TrailerNB, String destination)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		SAssert.assertTrue(page.ErrorAndWarningField.getText().contains(
				"Trailer has PROs. Shipments must be processed from trailer or loaded to trailer before it can be closed."));

		// ENTER DESTINATION IF IT IS BLANK
		String desti = page.DestinationField.getAttribute("value");
		if (desti.equalsIgnoreCase("___") || desti.equalsIgnoreCase("")) {
			String[] dest = { "123", "112", "841", "851" };
			int ran = new Random().nextInt(dest.length);
			String changeDesti = dest[ran];
			destination = changeDesti;
			page.SetDestination(destination);
		}
		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		// handle lobr pro
		page.LeftoverCheckAllPRO.click();
		String[] handleLobrPro = { "headload", "leaveON" };
		int ran = new Random().nextInt(handleLobrPro.length);
		page.HandleLOBRproAll(handleLobrPro[ran]);
		(new WebDriverWait(driver, 260)).until(ExpectedConditions.invisibilityOfElementLocated(By.name("abandon")));
		page.LobrSubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		// check screen
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), destination);
		SAssert.assertFalse(page.DateInput.isEnabled(), "date&time field is not disabled");
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataForUS1229.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), destination, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG", "Source_Create_ID is wrong");

		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 180000, i + "  " + TS + "  " + d);
		}
		page.ChangeStatusTo("ldd");
		SAssert.assertAll();
	}

	@Test(priority = 6, dataProvider = "12.29", dataProviderClass = DataForUS1229.class)
	public void OFDTrailerHasPRO(String terminalcd, String SCAC, String TrailerNB, String destination)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		SAssert.assertTrue(page.ErrorAndWarningField.getText().contains(
				"Trailer has PROs. Shipments must be processed from trailer or loaded to trailer before it can be closed."));

		// ENTER DESTINATION IF IT IS BLANK
		String desti = page.DestinationField.getAttribute("value");
		if (desti.equalsIgnoreCase("___") || desti.equalsIgnoreCase("")) {
			String[] dest = { "326", "112", "841", "851" };
			int ran = new Random().nextInt(dest.length);
			String changeDesti = dest[ran];
			destination = changeDesti;
			page.SetDestination(destination);
		}
		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);

		// handle the left pro
		page.LeftoverCheckAllPRO.click();
		String[] handleLobrPro = { "headload", "leaveON" };
		int ran = new Random().nextInt(handleLobrPro.length);
		page.HandleLOBRproAll(handleLobrPro[ran]);

		(new WebDriverWait(driver, 260)).until(ExpectedConditions.invisibilityOfElementLocated(By.name("abandon")));
		page.LobrSubmitButton.click();
		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		// check screen
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), destination);
		SAssert.assertFalse(page.DateInput.isEnabled(), "date&time field is not disabled");
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataForUS1229.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(2), destination, "equipment_dest_facility_cd is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG", "Source_Create_ID is wrong");

		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));
			SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 180000, i + "  " + TS + "  " + d);
		}
		page.ChangeStatusTo("ldd");
		SAssert.assertAll();
	}

	// @Test(priority=7,dataProvider =
	// "12.29",dataProviderClass=DataForUS1229.class)
	public void MTYTrailerHasPROCancel(String terminalcd, String SCAC, String TrailerNB, String destination)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<Object> OldEqpStatusRecord = DataForUS1229.CheckEQPStatusUpdate(SCAC, TrailerNB);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		SAssert.assertTrue(page.ErrorAndWarningField.getText().contains(
				"Trailer has PROs. Shipments must be processed from trailer or loaded to trailer before it can be closed."));

		// ENTER DESTINATION IF IT IS BLANK
		String desti = page.DestinationField.getAttribute("value");
		if (desti.equalsIgnoreCase("___") || desti.equalsIgnoreCase("")) {
			String[] dest = { "326", "112", "841", "851" };
			int ran = new Random().nextInt(dest.length);
			String changeDesti = dest[ran];
			destination = changeDesti;
			page.SetDestination(destination);
		}
		page.LobrCancelButton.click();

		w2.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		// check screen
		SAssert.assertEquals(page.TerminalField.getAttribute("value"), terminalcd, "terminal display wrong");
		SAssert.assertEquals(page.SCACField.getAttribute("value"), null, "scac is not clear");
		SAssert.assertEquals(page.TrailerInputField.getAttribute("value"), "______", "trailer# is not clear");
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), "", "Destination field is not clear");
		ArrayList<Object> NewEqpStatusRecord = DataForUS1229.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord, OldEqpStatusRecord);
		page.ChangeStatusTo("ldd");
		SAssert.assertAll();
	}

	// @Test(priority=8,dataProvider =
	// "12.29",dataProviderClass=DataForUS1229.class)
	public void SPTTrailerHasPROCancel(String terminalcd, String SCAC, String TrailerNB, String destination)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<Object> OldEqpStatusRecord = DataForUS1229.CheckEQPStatusUpdate(SCAC, TrailerNB);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		SAssert.assertTrue(page.ErrorAndWarningField.getText().contains(
				"Trailer has PROs. Shipments must be processed from trailer or loaded to trailer before it can be closed."));

		// ENTER DESTINATION IF IT IS BLANK
		String desti = page.DestinationField.getAttribute("value");
		if (desti.equalsIgnoreCase("___") || desti.equalsIgnoreCase("")) {
			String[] dest = { "326", "112", "841", "851" };
			int ran = new Random().nextInt(dest.length);
			String changeDesti = dest[ran];
			destination = changeDesti;
			page.SetDestination(destination);
		}
		page.LobrCancelButton.click();

		w2.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		// check screen
		SAssert.assertEquals(page.TerminalField.getAttribute("value"), terminalcd, "terminal display wrong");
		SAssert.assertEquals(page.SCACField.getAttribute("value"), null, "scac is not clear");
		SAssert.assertEquals(page.TrailerInputField.getAttribute("value"), "______", "trailer# is not clear");
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), "", "Destination field is not clear");
		ArrayList<Object> NewEqpStatusRecord = DataForUS1229.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord, OldEqpStatusRecord);
		page.ChangeStatusTo("ldd");
		SAssert.assertAll();
	}

	// @Test(priority=9,dataProvider =
	// "12.29",dataProviderClass=DataForUS1229.class)
	public void OFDTrailerHasPROCancel(String terminalcd, String SCAC, String TrailerNB, String destination)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<Object> OldEqpStatusRecord = DataForUS1229.CheckEQPStatusUpdate(SCAC, TrailerNB);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		SAssert.assertTrue(page.ErrorAndWarningField.getText().contains(
				"Trailer has PROs. Shipments must be processed from trailer or loaded to trailer before it can be closed."));

		// ENTER DESTINATION IF IT IS BLANK
		String desti = page.DestinationField.getAttribute("value");
		if (desti.equalsIgnoreCase("___") || desti.equalsIgnoreCase("")) {
			String[] dest = { "396", "112", "841", "851" };
			int ran = new Random().nextInt(dest.length);
			String changeDesti = dest[ran];
			destination = changeDesti;
			page.SetDestination(destination);
		}
		page.LobrCancelButton.click();

		w2.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		// check screen
		SAssert.assertEquals(page.TerminalField.getAttribute("value"), terminalcd, "terminal display wrong");
		SAssert.assertEquals(page.SCACField.getAttribute("value"), null, "scac is not clear");
		SAssert.assertEquals(page.TrailerInputField.getAttribute("value"), "______", "trailer# is not clear");
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), "", "Destination field is not clear");
		ArrayList<Object> NewEqpStatusRecord = DataForUS1229.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord, OldEqpStatusRecord);
		page.ChangeStatusTo("ldd");
		SAssert.assertAll();
	}

}