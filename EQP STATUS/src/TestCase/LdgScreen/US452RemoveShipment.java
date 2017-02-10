package TestCase.LdgScreen;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Data.DataForUS452;
import Function.CommonFunction;
import Function.ConfigRd;
import Function.DataCommon;
import Page.EqpStatusPageS;

public class US452RemoveShipment {
	private WebDriver driver;
	private EqpStatusPageS page;
	private ConfigRd Conf;

	@BeforeClass
	@Parameters({ "browser" })
	public void SetUp(@Optional("chrome") String browser) throws AWTException, InterruptedException {
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

	@Test(priority = 1, dataProvider = "4.52", dataProviderClass = DataForUS452.class)
	public void VerifyDockPro(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<String> GetProAlreadyOnTrailer = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		for (int j = 0; j < GetProAlreadyOnTrailer.size(); j++) {
			int line = page.ProListForm.findElements(By.xpath("div")).size();
			int ran = (int) (Math.random() * (line - 1)) + 1;
			jse.executeScript("arguments[0].scrollIntoView(true);",
					page.ProListCheckboxListldg.findElement(By.xpath("div[" + ran + "]/div/div/div/div")));
			page.ProListCheckboxListldg.findElement(By.xpath("div[" + ran + "]/div/div/div/div")).click();
			String RemovedPro = page.ProListForm.findElement(By.xpath("div[" + ran + "]/div/div[2]")).getText()
					.replaceAll("-", "");
			ArrayList<Object> BeforeRemoveWb = DataCommon.GetWaybillInformationOfPro(RemovedPro);
			page.DockProButton.click();
			Date d = CommonFunction.gettime("UTC");
			Date today = CommonFunction.getDay(d);
			(new WebDriverWait(driver, 20))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
			Thread.sleep(2000);
			ArrayList<Object> AfterRemoveWb = DataCommon.GetWaybillInformationOfPro(RemovedPro);
			// check waybill
			SAssert.assertTrue(AfterRemoveWb.get(0) == null, "waybill scac is wrong");
			SAssert.assertTrue(AfterRemoveWb.get(1) == null, "waybill equipment_unit_nb is wrong");
			SAssert.assertEquals(AfterRemoveWb.get(3), "LH.LDG", "waybill source_modify_id is wrong");
			SAssert.assertEquals(AfterRemoveWb.get(7), BeforeRemoveWb.get(7), "" + RemovedPro + "  Create_TS is wrong");
			SAssert.assertEquals(AfterRemoveWb.get(8), BeforeRemoveWb.get(8),
					"" + RemovedPro + " System_Insert_TS is wrong");
			Date f = CommonFunction.SETtime((Date) AfterRemoveWb.get(9));
			SAssert.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table waybill system_modify_ts  " + f + "  " + d + "  " + "   " + RemovedPro);
			SAssert.assertEquals(AfterRemoveWb.get(12), BeforeRemoveWb.get(12),
					"waybill table record_key is wrong " + RemovedPro);
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
		if (page.ProListCheckAllProChecked.isDisplayed())
			page.ProListCheckAllProChecked.click();
		// enter cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		page.SubmitButton1.click();
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 50))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
		// CHECK EQPS
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqpStatusRecord.get(16), Conf.GetAD_ID(), "modify_id is wrong");
		SAssert.assertEquals(NewEqpStatusRecord.get(17), Conf.GetM_ID(), "eqps Mainframe_User_ID is wrong");
		// check eqp
		ArrayList<Object> NewEqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		SAssert.assertEquals(NewEqp.get(0), Conf.GetM_ID(), " eqp Mainframe_User_ID is wrong");

		SAssert.assertAll();
	}

	@Test(priority = 2, dataProvider = "4.52", dataProviderClass = DataForUS452.class)
	public void VerifyDockThenAddNewPRO(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		// dock all pro
		page.ProListCheckAllProUncheck.click();
		page.DockProButton.click();
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		// add pro
		ArrayList<String> GetPro = DataCommon.GetProNotInAnyTrailer();
		page.RemoveProButton.click();
		for (int j = 0; j < 2; j++) {
			String CurrentPro = GetPro.get(j);
			page.EnterPro(CurrentPro);
			String CurrentProH = page.addHyphenToPro(CurrentPro);
			ArrayList<Object> BeforeAddWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
			page.SubmitButton.click();
			Date d = CommonFunction.gettime("UTC");
			Date today = CommonFunction.getDay(d);
			(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
			(new WebDriverWait(driver, 80))
					.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
			(new WebDriverWait(driver, 80))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			page.EnterTrailer(SCAC, TrailerNB);
			(new WebDriverWait(driver, 150))
					.until(ExpectedConditions.textToBePresentInElement(page.ProListForm, CurrentProH));
			int NEW2 = page.ProListForm.findElements(By.xpath("div")).size();
			Assert.assertEquals(page.ProListForm.findElement(By.xpath("div[" + NEW2 + "]/div/div[2]")).getText(),
					CurrentProH);
			ArrayList<Object> AfterAddWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			// check waybill
			SAssert.assertEquals(AfterAddWb.get(0), SCAC, "waybill scac is wrong");
			SAssert.assertEquals(AfterAddWb.get(1), TrailerNB, "waybill equipment_unit_nb is wrong");
			SAssert.assertEquals(AfterAddWb.get(3), "LH.LDG", "waybill source_modify_id is wrong");
			SAssert.assertEquals(AfterAddWb.get(7), BeforeAddWb.get(7),
					"" + CurrentPro + " waybill Create_TS is wrong");
			SAssert.assertEquals(AfterAddWb.get(8), BeforeAddWb.get(8),
					"" + CurrentPro + " waybill System_Insert_TS is wrong");
			Date f = CommonFunction.SETtime((Date) AfterAddWb.get(9));
			SAssert.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table system_modify_ts  " + f + "  " + d + "  " + "   " + CurrentPro);
			SAssert.assertEquals(AfterAddWb.get(12), BeforeAddWb.get(12),
					"waybill table record_key is wrong " + CurrentPro);
			SAssert.assertEquals(AfterAddWb.get(14), null, "waybill transaction To_Facility_CD is wrong" + CurrentPro);
			SAssert.assertEquals(AfterAddWb.get(13), terminalcd, "waybill equipment_unit_nb is wrong");
			SAssert.assertEquals(AfterAddWb.get(17), SCAC, "waybill To_Standard_Carrier_Alpha_CD is wrong");
			SAssert.assertEquals(AfterAddWb.get(18), TrailerNB, "waybill To_Equipment_Unit_NB is wrong");
			SAssert.assertEquals(AfterAddWb.get(15), null, "waybill From_Standard_Carrier_Alpha_CD is wrong");
			SAssert.assertEquals(AfterAddWb.get(16), null, "waybill From_Equipment_Unit_NB is wrong");
			SAssert.assertEquals(AfterAddWb.get(20), "LOADING", "waybill Waybill_Transaction_Type_NM is wrong");
			Date f1 = CommonFunction.SETtime((Date) AfterAddWb.get(11));
			SAssert.assertTrue(Math.abs(f1.getTime() - d.getTime()) < 120000,
					"waybill table Waybill_Transaction_End_TS  " + f1 + "  " + d + "  " + "   " + CurrentPro);

		}
		SAssert.assertAll();
	}

	@Test(priority = 3, dataProvider = "4.52", dataProviderClass = DataForUS452.class)
	public void DockAndReAdd(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<String> GetPro = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		// DOCK ALL PRO
		page.ProListCheckAllProUncheck.click();
		page.DockProButton.click();
		Thread.sleep(4000);
		(new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		// page.ProListCheckAllPro.click();
		page.RemoveProButton.click();
		for (int j = 0; j < GetPro.size(); j++) {
			String CurrentPro = GetPro.get(j);
			page.EnterPro(CurrentPro);
			String CurrentProH = page.addHyphenToPro(CurrentPro);
			ArrayList<Object> BeforeAddWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);

			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
			page.SubmitButton.click();
			Date d = CommonFunction.gettime("UTC");
			Date today = CommonFunction.getDay(d);
			(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
			(new WebDriverWait(driver, 80))
					.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
			(new WebDriverWait(driver, 80))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			page.EnterTrailer(SCAC, TrailerNB);
			(new WebDriverWait(driver, 150))
					.until(ExpectedConditions.textToBePresentInElement(page.ProListForm, CurrentProH));
			int NEW2 = page.ProListForm.findElements(By.xpath("div")).size();
			Assert.assertEquals(page.ProListForm.findElement(By.xpath("div[" + NEW2 + "]/div/div[2]")).getText(),
					CurrentProH);
			ArrayList<Object> AfterAddWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			// check waybill
			SAssert.assertEquals(AfterAddWb.get(0), SCAC, "waybill scac is wrong");
			SAssert.assertEquals(AfterAddWb.get(1), TrailerNB, "waybill equipment_unit_nb is wrong");
			SAssert.assertEquals(AfterAddWb.get(3), "LH.LDG", "waybill source_modify_id is wrong");
			SAssert.assertEquals(AfterAddWb.get(7), BeforeAddWb.get(7),
					"" + CurrentPro + " waybill Create_TS is wrong");
			SAssert.assertEquals(AfterAddWb.get(8), BeforeAddWb.get(8),
					"" + CurrentPro + " waybill System_Insert_TS is wrong");
			Date f = CommonFunction.SETtime((Date) AfterAddWb.get(9));
			SAssert.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table system_modify_ts  " + f + "  " + d + "  " + "   " + CurrentPro);
			SAssert.assertEquals(AfterAddWb.get(12), BeforeAddWb.get(12),
					"waybill table record_key is wrong " + CurrentPro);
			SAssert.assertEquals(AfterAddWb.get(14), null, "waybill transaction To_Facility_CD is wrong" + CurrentPro);
			SAssert.assertEquals(AfterAddWb.get(13), terminalcd, "waybill equipment_unit_nb is wrong");
			SAssert.assertEquals(AfterAddWb.get(17), SCAC, "waybill To_Standard_Carrier_Alpha_CD is wrong");
			SAssert.assertEquals(AfterAddWb.get(18), TrailerNB, "waybill To_Equipment_Unit_NB is wrong");
			SAssert.assertEquals(AfterAddWb.get(15), null, "waybill From_Standard_Carrier_Alpha_CD is wrong");
			SAssert.assertEquals(AfterAddWb.get(16), null, "waybill From_Equipment_Unit_NB is wrong");
			SAssert.assertEquals(AfterAddWb.get(20), "LOADING", "waybill Waybill_Transaction_Type_NM is wrong");
			Date f1 = CommonFunction.SETtime((Date) AfterAddWb.get(11));
			SAssert.assertTrue(Math.abs(f1.getTime() - d.getTime()) < 120000,
					"waybill table Waybill_Transaction_End_TS  " + f1 + "  " + d + "  " + "   " + CurrentPro);
		}

		SAssert.assertAll();
	}

	@Test(priority = 4, dataProvider = "4.52", dataProviderClass = DataForUS452.class)
	public void EmptyTrailerAddSomeThenDock(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		// ADD PRO
		ArrayList<String> GetProNotOnAnyTrailer = DataCommon.GetProNotInAnyTrailer();
		page.RemoveProButton.click();
		for (int j = 0; j < 2; j++) {
			String CurrentPro = GetProNotOnAnyTrailer.get(j);
			page.EnterPro(CurrentPro);
		}
		if (page.CubeField.getAttribute("value").equalsIgnoreCase("")) {
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
		}
		page.SubmitButton.click();
		(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		(new WebDriverWait(driver, 80))
				.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<String> GetProAlreadyOnTrailer = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		for (int j = 0; j < GetProAlreadyOnTrailer.size(); j++) {
			int line = page.ProListForm.findElements(By.xpath("div")).size();
			int ran = (int) (Math.random() * (line - 1)) + 1;
			page.ProListCheckboxListldg.findElement(By.xpath("div[" + ran + "]/div/div/div/div")).click();
			String RemovedPro = page.ProListForm.findElement(By.xpath("div[" + ran + "]/div/div[2]")).getText()
					.replaceAll("-", "");
			ArrayList<Object> BeforeRemoveWb = DataCommon.GetWaybillInformationOfPro(RemovedPro);
			page.DockProButton.click();
			Date d = CommonFunction.gettime("UTC");
			Date today = CommonFunction.getDay(d);
			(new WebDriverWait(driver, 20))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
			Thread.sleep(8000);
			ArrayList<Object> AfterRemoveWb = DataCommon.GetWaybillInformationOfPro(RemovedPro);
			// check waybill transaction
			SAssert.assertTrue(AfterRemoveWb.get(0) == null, "waybill scac is wrong");
			SAssert.assertTrue(AfterRemoveWb.get(1) == null, "waybill equipment_unit_nb is wrong");
			SAssert.assertEquals(AfterRemoveWb.get(3), "LH.LDG", "waybill source_modify_id is wrong");
			SAssert.assertEquals(AfterRemoveWb.get(7), BeforeRemoveWb.get(7), "" + RemovedPro + "  Create_TS is wrong");
			SAssert.assertEquals(AfterRemoveWb.get(8), BeforeRemoveWb.get(8),
					"" + RemovedPro + " System_Insert_TS is wrong");
			Date f = CommonFunction.SETtime((Date) AfterRemoveWb.get(9));
			SAssert.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table waybill system_modify_ts  " + f + "  " + d + "  " + "   " + RemovedPro);
			SAssert.assertEquals(AfterRemoveWb.get(12), BeforeRemoveWb.get(12),
					"waybill table record_key is wrong " + RemovedPro);
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
		SAssert.assertAll();
	}

	@AfterClass
	public void Close() {
		driver.close();
	}
}
