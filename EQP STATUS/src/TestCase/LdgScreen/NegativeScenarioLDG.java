package TestCase.LdgScreen;

import java.awt.AWTException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Function.CommonFunction;
import Function.DataCommon;
import Function.Setup;
import Page.EqpStatusPageS;
import TestCase.ReusableFunctionTest.DataForReusableFunction;

public class NegativeScenarioLDG extends Setup {
	private EqpStatusPageS page;
	private Actions builder;
	private WebDriverWait w1;

	@BeforeClass
	public void SetUp() throws AWTException, InterruptedException, IOException {
		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 50);
		  
		driver.manage().window().maximize();
		page.SetStatus("ldg");
		builder = new Actions(driver);
	}

	@Test(priority = 1, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class, description = "change headload destination")
	public void SetTrailerToLDGWithPro(String terminalcd, String SCAC, String TrailerNB, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
	}

	@Test(priority = 2, dataProvider = "2000.68", dataProviderClass = DataForReusableFunction.class)
	public void VerifyInvalidDestination(String Destination) throws AWTException {
		page.HeadloadDestination.clear();
		page.HeadloadDestination.sendKeys(Destination);
		builder.sendKeys(Keys.TAB).build().perform();
		w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Invalid Headload Destination, please enter valid Destination."));
	}

	@Test(priority = 3, dataProvider = "2000.68", dataProviderClass = DataForReusableFunction.class)
	public void VerifyValidDestination(String Destination) throws AWTException {
		page.HeadloadDestination.clear();
		page.HeadloadDestination.sendKeys(Destination);
		builder.sendKeys(Keys.TAB).build().perform();
		w1.until(ExpectedConditions.invisibilityOfElementLocated(
				By.xpath("//*[contains(text(), 'Invalid Headload Destination, please enter valid Destination.')]")));
	}

	@Test(priority = 4, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class, description = "change headload destination")
	public void SetTrailerToLDGWithProCheckHeadloadCube(String terminalcd, String SCAC, String TrailerNB, Date MReqpst)
			throws InterruptedException, AWTException {
		page.SetStatus("ldg");
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		String[] Cube = { "00011", "1 1", "00", "   1", "33", "88", "100", "1030", "7000", "abc" };
		for (int i = 0; i < Cube.length; i++) {
			String cube = Cube[i];
			page.SetHeadloadCube(cube);
			Thread.sleep(500);
			int result = CommonFunction.CheckCubePattern(cube);
			if (result == 2) {
				w1.until(ExpectedConditions.visibilityOfElementLocated(
						By.xpath("//*[contains(text(), 'Invalid Headload Cube. Value must be between 1 and 100.')]")));
			} else {
				w1.until(ExpectedConditions.invisibilityOfElementLocated(
						By.xpath("//*[contains(text(), 'Invalid Headload Cube. Value must be between 1 and 100.')]")));
			}
			page.HeadloadCube.clear();
		}
	}

	@Test(priority = 5, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class, description = "6.19")
	public void SetTrailerToLDGWithProLeaveHeadLoadCubeAndDestination(String terminalcd, String SCAC, String TrailerNB,
			Date MReqpst) throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		page.SetStatus("ldg");
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		Date CurrentTime = CommonFunction.gettime("UTC");
		Date LocalTime = null;
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
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
		ArrayList<ArrayList<Object>> WbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		ArrayList<Object> EqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		// ENTER DESTINATION IF IT IS BLANK
		String changeDesti = null;
		String desti = page.DestinationField.getAttribute("value");
		if (desti.equalsIgnoreCase("___") || desti.equalsIgnoreCase("")) {
			String[] dest = { "270", "112", "841", "198", "135" };
			int ran = new Random().nextInt(dest.length);
			changeDesti = dest[ran];
			page.SetDestination(changeDesti);
		}
		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);

		// select all pro and click headload
		try {
			if (page.LeftoverCheckAllPRO.isDisplayed()) {
				page.LeftoverCheckAllPRO.click();
			}
		} catch (Exception e) {
		}
		page.HEADLOADButton.click();
		w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Required field(s) missing or has incorrect values. Please enter required data and re-submit"));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord, EqpStatusRecord, "Equipment_Status got changed");
		// check waybill
		ArrayList<ArrayList<Object>> NewWbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		SAssert.assertEquals(NewWbtRecord, WbtRecord, "PRO change");
		SAssert.assertAll();
	}

}
