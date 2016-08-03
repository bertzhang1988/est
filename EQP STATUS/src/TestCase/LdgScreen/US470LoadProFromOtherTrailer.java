package TestCase.LdgScreen;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

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

import Data.DataForUS470;
import Function.CommonFunction;
import Function.ConfigRd;
import Function.DataCommon;
import Page.EqpStatusPageS;

public class US470LoadProFromOtherTrailer {
	private WebDriver driver;
	private EqpStatusPageS page;

	@Test(priority = 1, dataProvider = "4.70", dataProviderClass = DataForUS470.class)
	public void NoProTrailerPullProFromTrailerInOtherTerminal(String terminalcd, String SCAC, String TrailerNB,
			String Desti)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert Sassert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		String Cube = page.CubeField.getAttribute("value");
		if (Cube.equalsIgnoreCase("") || Cube.equalsIgnoreCase("0")) {
			int Ran = (int) (Math.random() * 99) + 1;
			page.SetCube(Integer.toString(Ran));
		}
		ArrayList<ArrayList<String>> PROInformation = DataForUS470.GetProFromTrailerOnDifferentTerminal(terminalcd,
				SCAC, TrailerNB);
		page.RemoveProButton.click();
		for (int j = 0; j < 1; j++) {
			String CurrentPro = PROInformation.get(j).get(0);
			String FromSCAC = PROInformation.get(j).get(1);
			String FromTrailerNB = PROInformation.get(j).get(2);
			page.EnterPro(CurrentPro);
			String CurrentProH = page.addHyphenToPro(CurrentPro);
			// page.AddProCheckBoxList.findElement(By.xpath("div["+NEW1+"]/div/div/div/div")).click();
			ArrayList<Object> BeforeADDWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			int Ran = (int) (Math.random() * 99) + 1;
			page.SetCube(Integer.toString(Ran));
			page.SubmitButton.click();
			Date d = CommonFunction.gettime("UTC");
			Date today = CommonFunction.getDay(d);
			(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
			(new WebDriverWait(driver, 80))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			(new WebDriverWait(driver, 50))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
			(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
			(new WebDriverWait(driver, 80))
					.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
			(new WebDriverWait(driver, 80))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			page.EnterTrailer(SCAC, TrailerNB);
			(new WebDriverWait(driver, 150))
					.until(ExpectedConditions.textToBePresentInElement(page.ProListForm, CurrentProH));
			int NEW2 = page.ProListForm.findElements(By.xpath("div")).size();
			Sassert.assertEquals(page.ProListForm.findElement(By.xpath("div[" + NEW2 + "]/div/div[2]")).getText(),
					CurrentProH);

			// check waybill
			ArrayList<Object> AfterADDWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(0), SCAC,
					"Waybill table Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(1), TrailerNB,
					"Waybill table Equipment_Unit_NB is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(2), BeforeADDWb.get(2),
					"Waybill table Destination_facility_cd is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(3), "LH.LDG", "Waybill table Source_Modify_ID is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(4), BeforeADDWb.get(4),
					"Waybill table M204_SHP_TAG_ID is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(5), BeforeADDWb.get(5),
					"Waybill table M204_WGP_TAG_ID is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(6), BeforeADDWb.get(6),
					"Waybill table Record_key is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(7), BeforeADDWb.get(7),
					"Waybill table Create_TS is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(8), BeforeADDWb.get(8),
					"Waybill table System_Insert_TS is wrong " + CurrentPro);
			Date f = CommonFunction.SETtime((Date) AfterADDWb.get(9));
			Sassert.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table system_modify_ts  " + f + "  " + d + "  " + "   " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(12), BeforeADDWb.get(12),
					"Waybill table Record_KEY is wrong " + CurrentPro);
			// loading record
			Sassert.assertEquals(AfterADDWb.get(14), null,
					"waybill table loading record To_Facility_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(13), terminalcd,
					"waybill table loading record From_Facility_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(17), SCAC,
					"waybill table loading record To_Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(18), TrailerNB,
					"waybill table loading record To_Equipment_Unit_NB is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(15), null,
					"waybill table loading record From_Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(16), null,
					"waybill table loading record From_Equipment_Unit_NB is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(20), "LOADING",
					"waybill table loading Waybill_Transaction_Type_NM is wrong " + CurrentPro);
			Date f1 = CommonFunction.SETtime((Date) AfterADDWb.get(11));
			Sassert.assertTrue(Math.abs(f1.getTime() - d.getTime()) < 120000,
					"waybill Waybill_Transaction_End_TS   " + f1 + "  " + d + "  " + "   " + CurrentPro);
		}
		Sassert.assertAll();

	}

	@Test(priority = 2, dataProvider = "4.70", dataProviderClass = DataForUS470.class)
	public void WithProTrailerPullProFromTrailerInOtherTerminal(String terminalcd, String SCAC, String TrailerNB,
			String Desti)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert Sassert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		String Cube = page.CubeField.getAttribute("value");
		if (Cube.equalsIgnoreCase("") || Cube.equalsIgnoreCase("0")) {
			int Ran = (int) (Math.random() * 99) + 1;
			page.SetCube(Integer.toString(Ran));
		}
		ArrayList<ArrayList<String>> PROInformation = DataForUS470.GetProFromTrailerOnDifferentTerminal(terminalcd,
				SCAC, TrailerNB);
		page.RemoveProButton.click();
		for (int j = 0; j < 1; j++) {
			String CurrentPro = PROInformation.get(j).get(0);
			String FromSCAC = PROInformation.get(j).get(1);
			String FromTrailerNB = PROInformation.get(j).get(2);
			page.EnterPro(CurrentPro);
			String CurrentProH = page.addHyphenToPro(CurrentPro);
			// page.AddProCheckBoxList.findElement(By.xpath("div["+NEW1+"]/div/div/div/div")).click();
			ArrayList<Object> BeforeADDWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			// change cube
			int Ran = (int) (Math.random() * 99) + 1;
			page.SetCube(Integer.toString(Ran));
			// click submit
			page.SubmitButton.click();
			Date d = CommonFunction.gettime("UTC");
			Date today = CommonFunction.getDay(d);
			(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
			(new WebDriverWait(driver, 80))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			(new WebDriverWait(driver, 50))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
			(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
			(new WebDriverWait(driver, 80))
					.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
			(new WebDriverWait(driver, 80))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			page.EnterTrailer(SCAC, TrailerNB);
			(new WebDriverWait(driver, 150))
					.until(ExpectedConditions.textToBePresentInElement(page.ProListForm, CurrentProH));
			int NEW2 = page.ProListForm.findElements(By.xpath("div")).size();
			Sassert.assertEquals(page.ProListForm.findElement(By.xpath("div[" + NEW2 + "]/div/div[2]")).getText(),
					CurrentProH);
			// check waybill
			ArrayList<Object> AfterADDWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(0), SCAC,
					"Waybill table Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(1), TrailerNB,
					"Waybill table Equipment_Unit_NB is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(2), BeforeADDWb.get(2),
					"Waybill table Destination_facility_cd is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(3), "LH.LDG", "Waybill table Source_Modify_ID is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(4), BeforeADDWb.get(4),
					"Waybill table M204_SHP_TAG_ID is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(5), BeforeADDWb.get(5),
					"Waybill table M204_WGP_TAG_ID is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(6), BeforeADDWb.get(6),
					"Waybill table Record_key is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(7), BeforeADDWb.get(7),
					"Waybill table Create_TS is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(8), BeforeADDWb.get(8),
					"Waybill table System_Insert_TS is wrong " + CurrentPro);
			Date f = CommonFunction.SETtime((Date) AfterADDWb.get(9));
			Sassert.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table system_modify_ts  " + f + "  " + d + "  " + "   " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(12), BeforeADDWb.get(12),
					"Waybill table Record_KEY is wrong " + CurrentPro);
			// loading record
			Sassert.assertEquals(AfterADDWb.get(14), null,
					"waybill table loading record To_Facility_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(13), terminalcd,
					"waybill table loading record From_Facility_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(17), SCAC,
					"waybill table loading record To_Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(18), TrailerNB,
					"waybill table loading record To_Equipment_Unit_NB is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(15), null,
					"waybill table loading record From_Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(16), null,
					"waybill table loading record From_Equipment_Unit_NB is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(20), "LOADING",
					"waybill table loading Waybill_Transaction_Type_NM is wrong " + CurrentPro);
			Date f1 = CommonFunction.SETtime((Date) AfterADDWb.get(11));
			Sassert.assertTrue(Math.abs(f1.getTime() - d.getTime()) < 120000,
					"waybill Waybill_Transaction_End_TS   " + f1 + "  " + d + "  " + "   " + CurrentPro);
		}
		Sassert.assertAll();
	}

	@Test(priority = 3, dataProvider = "4.70", dataProviderClass = DataForUS470.class)
	public void NoProTrailerPullProFromTrailerInSameTerminal(String terminalcd, String SCAC, String TrailerNB,
			String Desti)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert Sassert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		String Cube = page.CubeField.getAttribute("value");

		if (Cube.equalsIgnoreCase("") || Cube.equalsIgnoreCase("0")) {
			int Ran = (int) (Math.random() * 99) + 1;
			page.SetCube(Integer.toString(Ran));
		}

		ArrayList<ArrayList<String>> PROInformation = DataForUS470.GetProFromTrailerOnSameTerminal(terminalcd, SCAC,
				TrailerNB);
		page.RemoveProButton.click();
		for (int j = 0; j < 1; j++) {
			String CurrentPro = PROInformation.get(j).get(0);
			String FromSCAC = PROInformation.get(j).get(1);
			String FromTrailerNB = PROInformation.get(j).get(2);
			page.EnterPro(CurrentPro);
			String CurrentProH = page.addHyphenToPro(CurrentPro);
			// page.AddProCheckBoxList.findElement(By.xpath("div["+NEW1+"]/div/div/div/div")).click();
			ArrayList<Object> BeforeADDWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			int Ran = (int) (Math.random() * 99) + 1;
			page.SetCube(Integer.toString(Ran));
			page.SubmitButton1.click();
			Date d = CommonFunction.gettime("UTC");
			Date today = CommonFunction.getDay(d);
			(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
			(new WebDriverWait(driver, 80))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			(new WebDriverWait(driver, 50))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
			(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
			(new WebDriverWait(driver, 80))
					.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
			(new WebDriverWait(driver, 80))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			page.EnterTrailer(SCAC, TrailerNB);
			(new WebDriverWait(driver, 150))
					.until(ExpectedConditions.textToBePresentInElement(page.ProListForm, CurrentProH));
			int NEW2 = page.ProListForm.findElements(By.xpath("div")).size();
			Sassert.assertEquals(page.ProListForm.findElement(By.xpath("div[" + NEW2 + "]/div/div[2]")).getText(),
					CurrentProH);

			// check waybill
			ArrayList<Object> AfterADDWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(0), SCAC,
					"Waybill table Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(1), TrailerNB,
					"Waybill table Equipment_Unit_NB is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(2), BeforeADDWb.get(2),
					"Waybill table Destination_facility_cd is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(3), "LH.LDG", "Waybill table Source_Modify_ID is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(4), BeforeADDWb.get(4),
					"Waybill table M204_SHP_TAG_ID is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(5), BeforeADDWb.get(5),
					"Waybill table M204_WGP_TAG_ID is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(6), BeforeADDWb.get(6),
					"Waybill table Record_key is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(7), BeforeADDWb.get(7),
					"Waybill table Create_TS is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(8), BeforeADDWb.get(8),
					"Waybill table System_Insert_TS is wrong " + CurrentPro);
			Date f = CommonFunction.SETtime((Date) AfterADDWb.get(9));
			Sassert.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table system_modify_ts  " + f + "  " + d + "  " + "   " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(12), BeforeADDWb.get(12),
					"Waybill table Record_KEY is wrong " + CurrentPro);
			// loading record
			Sassert.assertEquals(AfterADDWb.get(14), null,
					"waybill table loading record To_Facility_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(13), terminalcd,
					"waybill table loading record From_Facility_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(17), SCAC,
					"waybill table loading record To_Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(18), TrailerNB,
					"waybill table loading record To_Equipment_Unit_NB is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(15), null,
					"waybill table loading record From_Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(16), null,
					"waybill table loading record From_Equipment_Unit_NB is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(20), "LOADING",
					"waybill table loading Waybill_Transaction_Type_NM is wrong " + CurrentPro);
			Date f1 = CommonFunction.SETtime((Date) AfterADDWb.get(11));
			Sassert.assertTrue(Math.abs(f1.getTime() - d.getTime()) < 120000,
					"waybill Waybill_Transaction_End_TS   " + f1 + "  " + d + "  " + "   " + CurrentPro);
		}

		Sassert.assertAll();
	}

	@Test(priority = 4, dataProvider = "4.70", dataProviderClass = DataForUS470.class)
	public void WithProTrailerPullProFromTrailerInSameTerminal(String terminalcd, String SCAC, String TrailerNB,
			String Desti)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert Sassert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		String Cube = page.CubeField.getAttribute("value");
		if (Cube.equalsIgnoreCase("") || Cube.equalsIgnoreCase("0")) {
			int Ran = (int) (Math.random() * 99) + 1;
			page.SetCube(Integer.toString(Ran));
		}
		ArrayList<ArrayList<String>> PROInformation = DataForUS470.GetProFromTrailerOnSameTerminal(terminalcd, SCAC,
				TrailerNB);
		page.RemoveProButton.click();
		for (int j = 0; j < 1; j++) {
			String CurrentPro = PROInformation.get(j).get(0);
			String FromSCAC = PROInformation.get(j).get(1);
			String FromTrailerNB = PROInformation.get(j).get(2);
			page.EnterPro(CurrentPro);
			String CurrentProH = page.addHyphenToPro(CurrentPro);
			// page.AddProCheckBoxList.findElement(By.xpath("div["+NEW1+"]/div/div/div/div")).click();
			ArrayList<Object> BeforeADDWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			int Ran = (int) (Math.random() * 99) + 1;
			page.SetCube(Integer.toString(Ran));
			page.SubmitButton1.click();
			Date d = CommonFunction.gettime("UTC");
			Date today = CommonFunction.getDay(d);
			(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
			(new WebDriverWait(driver, 80))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			(new WebDriverWait(driver, 50))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
			(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));
			(new WebDriverWait(driver, 80))
					.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
			(new WebDriverWait(driver, 80))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			page.EnterTrailer(SCAC, TrailerNB);
			(new WebDriverWait(driver, 150))
					.until(ExpectedConditions.textToBePresentInElement(page.ProListForm, CurrentProH));
			int NEW2 = page.ProListForm.findElements(By.xpath("div")).size();
			Sassert.assertEquals(page.ProListForm.findElement(By.xpath("div[" + NEW2 + "]/div/div[2]")).getText(),
					CurrentProH);

			// check waybill
			ArrayList<Object> AfterADDWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(0), SCAC,
					"Waybill table Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(1), TrailerNB,
					"Waybill table Equipment_Unit_NB is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(2), BeforeADDWb.get(2),
					"Waybill table Destination_facility_cd is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(3), "LH.LDG", "Waybill table Source_Modify_ID is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(4), BeforeADDWb.get(4),
					"Waybill table M204_SHP_TAG_ID is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(5), BeforeADDWb.get(5),
					"Waybill table M204_WGP_TAG_ID is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(6), BeforeADDWb.get(6),
					"Waybill table Record_key is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(7), BeforeADDWb.get(7),
					"Waybill table Create_TS is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(8), BeforeADDWb.get(8),
					"Waybill table System_Insert_TS is wrong " + CurrentPro);
			Date f = CommonFunction.SETtime((Date) AfterADDWb.get(9));
			Sassert.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table system_modify_ts  " + f + "  " + d + "  " + "   " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(12), BeforeADDWb.get(12),
					"Waybill table Record_KEY is wrong " + CurrentPro);
			// loading record
			Sassert.assertEquals(AfterADDWb.get(14), null,
					"waybill table loading record To_Facility_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(13), terminalcd,
					"waybill table loading record From_Facility_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(17), SCAC,
					"waybill table loading record To_Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(18), TrailerNB,
					"waybill table loading record To_Equipment_Unit_NB is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(15), null,
					"waybill table loading record From_Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(16), null,
					"waybill table loading record From_Equipment_Unit_NB is wrong " + CurrentPro);
			Sassert.assertEquals(AfterADDWb.get(20), "LOADING",
					"waybill table loading Waybill_Transaction_Type_NM is wrong " + CurrentPro);
			Date f1 = CommonFunction.SETtime((Date) AfterADDWb.get(11));
			Sassert.assertTrue(Math.abs(f1.getTime() - d.getTime()) < 120000,
					"waybill Waybill_Transaction_End_TS   " + f1 + "  " + d + "  " + "   " + CurrentPro);
		}
		Sassert.assertAll();
	}

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
		page.SetStatus("ldg");
	}

	@AfterClass
	public void Close() {
		driver.close();
	}

}
