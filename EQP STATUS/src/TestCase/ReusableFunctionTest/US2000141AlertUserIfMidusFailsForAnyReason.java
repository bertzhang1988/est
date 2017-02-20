package TestCase.ReusableFunctionTest;

import java.awt.AWTException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Data.DataForUS200091;
import Function.CommonFunction;
import Function.DataCommon;
import Function.SetupBrowser;
import Page.EqpStatusPageS;

public class US2000141AlertUserIfMidusFailsForAnyReason extends SetupBrowser {

	private EqpStatusPageS page;

	@BeforeClass
	public void SetUp() throws AWTException, InterruptedException {

		page = new EqpStatusPageS(driver);
		driver.get(conf.GetURL());
		driver.manage().window().maximize();
		page.SetStatus("ldg");

	}

	@Test(priority = 1, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class)
	public void TrailerWithoutPROAddPRO(String terminalcd, String SCAC, String TrailerNB, String destination,
			Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<Object> OldEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		String[] dest = { "270", "112", "841", "198", "135" };
		int ran = new Random().nextInt(dest.length);
		String changeDesti = dest[ran];
		page.SetDestination(changeDesti);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		// add pro
		ArrayList<String> PRO = DataCommon.GetProNotInAnyTrailer();
		ArrayList<String> ADDPRO = new ArrayList<String>();
		ArrayList<ArrayList<Object>> PROInfoBeforeAdd = new ArrayList<ArrayList<Object>>();
		for (int i = 0; i < 1; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
			ArrayList<Object> CheckWaybillRecord = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			PROInfoBeforeAdd.add(CheckWaybillRecord);
		}
		// change cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		page.SubmitButton1.click();
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Update failed. Please open a ticket with the trailer, terminal and user ID for the EST app."));
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord, OldEqpStatusRecord, "Equipment_Status got change");
		// check pro
		ArrayList<ArrayList<Object>> PROInfoAfterAdd = new ArrayList<ArrayList<Object>>();
		for (String pro : ADDPRO) {
			ArrayList<Object> CheckWaybillRecord = DataCommon.GetWaybillInformationOfPro(pro);
			PROInfoAfterAdd.add(CheckWaybillRecord);
		}
		SA.assertEquals(PROInfoAfterAdd, PROInfoBeforeAdd, "pro should not add but pro information got change");
		Thread.sleep(3000);
		SA.assertAll();
	}

	@Test(priority = 2, dataProvider = "2000.91", dataProviderClass = DataForUS200091.class)
	public void LdgTrailerWithProAddPro(String terminalcd, String SCAC, String TrailerNB, String destination,
			Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// add pro
		ArrayList<String> PRO = DataCommon.GetProNotInAnyTrailer();
		ArrayList<String> ADDPRO = new ArrayList<String>();
		ArrayList<ArrayList<Object>> PROInfoBeforeAdd = new ArrayList<ArrayList<Object>>();
		for (int i = 0; i < 1; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			ADDPRO.add(CurrentPro);
			ArrayList<Object> CheckWaybillRecord = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			PROInfoBeforeAdd.add(CheckWaybillRecord);
		}

		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		ArrayList<Object> OldEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		// click new submit
		page.SubmitButton1.click();
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Update failed. Please open a ticket with the trailer, terminal and user ID for the EST app."));

		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord, OldEqpStatusRecord, "Equipment_Status got change");
		// check pro
		ArrayList<ArrayList<Object>> PROInfoAfterAdd = new ArrayList<ArrayList<Object>>();
		for (String pro : ADDPRO) {
			ArrayList<Object> CheckWaybillRecord = DataCommon.GetWaybillInformationOfPro(pro);
			PROInfoAfterAdd.add(CheckWaybillRecord);
		}
		SA.assertEquals(PROInfoAfterAdd, PROInfoBeforeAdd, "pro should not add but pro information got change");
		Thread.sleep(3000);
		SA.assertAll();
	}

}
