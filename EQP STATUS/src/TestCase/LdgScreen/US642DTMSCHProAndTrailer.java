package TestCase.LdgScreen;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
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

public class US642DTMSCHProAndTrailer {
	private WebDriver driver;
	private EqpStatusPageS page;

	@BeforeClass(groups = { "ldg uc" })
	@Parameters({ "browser" })
	public void SetUp(@Optional("chrome") String browser) throws AWTException, InterruptedException, IOException {
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
		page.SetStatus("ldg");
	}

	@Test(priority = 1, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class, description = "can transit to ldg not in ldg and ldd with pro, lobr, headload", groups = {
			"ldg uc" })
	public void SetTrailerToLDGWithProAndHeadLoad(String terminalcd, String SCAC, String TrailerNB, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// set trailer eqp mismatch
		CommonFunction.SetTrailerToEQPdttmsh(SCAC, TrailerNB);

		// check lobr date&time pre populate
		Date picker = page.GetDatePickerTime();
		Date expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MReqpst);
		SAssert.assertEquals(picker, expect, "lobr screen prepopulate time is wrong ");

		// check lobr pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.LeftoverBillForm);
		SAssert.assertEquals(ProInfo, DataCommon.GetProListLOBR(SCAC, TrailerNB), " lobr pro grid is wrong");
		ArrayList<String> prolistbeforelobr = DataCommon.GetProOnTrailer(SCAC, TrailerNB);

		// set pro to WGP
		CommonFunction.SetProToWGPdttmsh(prolistbeforelobr);

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

		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), 1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// handle the left pro
		ArrayList<ArrayList<Object>> WbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		page.HandleLOBRproAll("headload");
		String headload_dest = page.HeadloadDestination.getAttribute("value");
		String headloadCube = page.HeadloadCube.getAttribute("value");
		Date d = CommonFunction.gettime("UTC");

		(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
		// (new WebDriverWait(driver,
		// 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		(new WebDriverWait(driver, 280))
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
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 380000, i + " " + TS + "  " + d);
			}
		}

		// check waybill
		ArrayList<ArrayList<Object>> NewWbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
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

		// CHECK THE PRO IS IN SAME ORDER BEFORE LOBR
		ArrayList<String> prolistafterlobr = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		SAssert.assertEquals(prolistafterlobr, prolistbeforelobr, " PRO IS not IN SAME ORDER as BEFORE LOBR");
		SAssert.assertAll();
	}

	@AfterClass(groups = { "ldg uc" })
	public void TearDown() {
		driver.quit();
	}

}
