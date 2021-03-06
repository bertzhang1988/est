package TestCase.LdgScreen;

import java.awt.AWTException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Data.DataForUS200006;
import Function.CommonFunction;
import Function.DataCommon;
import Function.Setup;
import Page.EqpStatusPageS;

public class US200006FoodAndPoisWarning extends Setup {
	private EqpStatusPageS page;
	private WebDriverWait w1;
	private WebDriverWait w2;

	@BeforeClass
	public void SetUp() throws AWTException, InterruptedException {
		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 50);
		w2 = new WebDriverWait(driver, 80);
		  
		driver.manage().window().maximize();
		page.SetStatus("ldg");
	}

	@Test(priority = 1, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class)
	public void addSingleFoodToLDGTrailerWithoutPro(String terminalcd, String SCAC, String TrailerNB, Date MRST)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "food");
		for (int i = 0; i < 1; i++) {
			page.RemoveProButton.click();
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
			page.SubmitButton1.click();
			Date d = CommonFunction.gettime("UTC");
			// (new WebDriverWait(driver,
			// 80)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,"pro(s)
			// loaded."));
			w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
			w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
			w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SAssert.assertEquals(AfterLOADWb.get(0), SCAC,
					"Waybill table Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			SAssert.assertEquals(AfterLOADWb.get(1), TrailerNB,
					"Waybill table Equipment_Unit_NB is wrong " + CurrentPro);
			SAssert.assertEquals(AfterLOADWb.get(3), "LH.LDG", "Waybill table Source_Modify_ID is wrong " + CurrentPro);
			Date f = CommonFunction.SETtime((Date) AfterLOADWb.get(9));
			SAssert.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table system_modify_ts  " + f + "  " + d + "  " + "   " + CurrentPro);
		}
		SAssert.assertAll();
	}

	@Test(priority = 2, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class)
	public void addSinglePoisToLDGTrailerWithoutPro(String terminalcd, String SCAC, String TrailerNB, Date MRST)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "pois");
		for (int i = 0; i < 1; i++) {
			page.RemoveProButton.click();
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
			page.SubmitButton1.click();
			Date d = CommonFunction.gettime("UTC");
			// (new WebDriverWait(driver,
			// 80)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,"pro(s)
			// loaded."));
			w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
			w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
			w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SAssert.assertEquals(AfterLOADWb.get(0), SCAC,
					"Waybill table Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			SAssert.assertEquals(AfterLOADWb.get(1), TrailerNB,
					"Waybill table Equipment_Unit_NB is wrong " + CurrentPro);
			SAssert.assertEquals(AfterLOADWb.get(3), "LH.LDG", "Waybill table Source_Modify_ID is wrong " + CurrentPro);
			Date f = CommonFunction.SETtime((Date) AfterLOADWb.get(9));
			SAssert.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table system_modify_ts  " + f + "  " + d + "  " + "   " + CurrentPro);

		}

		SAssert.assertAll();
	}

	@Test(priority = 3, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class)
	public void addBatchFoodToLDGTrailerWithoutPro(String terminalcd, String SCAC, String TrailerNB, Date MRST)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		page.RemoveProButton.click();
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "food");
		ArrayList<String> Addpro = new ArrayList<String>();
		for (int i = 0; i < 2; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			Addpro.add(CurrentPro);
		}
		// change cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);

		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		// (new WebDriverWait(driver,
		// 80)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,"pro(s)
		// loaded."));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		for (int pro = 0; pro < Addpro.size(); pro++) {
			String CurrentPro = Addpro.get(pro);
			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SAssert.assertEquals(AfterLOADWb.get(0), SCAC,
					"Waybill table Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			SAssert.assertEquals(AfterLOADWb.get(1), TrailerNB,
					"Waybill table Equipment_Unit_NB is wrong " + CurrentPro);
			SAssert.assertEquals(AfterLOADWb.get(3), "LH.LDG", "Waybill table Source_Modify_ID is wrong " + CurrentPro);
			Date f = CommonFunction.SETtime((Date) AfterLOADWb.get(9));
			SAssert.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table system_modify_ts  " + f + "  " + d + "  " + "   " + CurrentPro);

		}
		SAssert.assertAll();
	}

	@Test(priority = 4, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class)
	public void addBatchPoisToLDGTrailerWithoutPro(String terminalcd, String SCAC, String TrailerNB, Date MRST)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		page.RemoveProButton.click();
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "pois");
		ArrayList<String> Addpro = new ArrayList<String>();
		for (int i = 0; i < 2; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			Addpro.add(CurrentPro);
		}
		// change cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);

		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField, "pro(s) loaded."));
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		for (int pro = 0; pro < Addpro.size(); pro++) {
			String CurrentPro = Addpro.get(pro);
			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SAssert.assertEquals(AfterLOADWb.get(0), SCAC,
					"Waybill table Standard_Carrier_Alpha_CD is wrong " + CurrentPro);
			SAssert.assertEquals(AfterLOADWb.get(1), TrailerNB,
					"Waybill table Equipment_Unit_NB is wrong " + CurrentPro);
			SAssert.assertEquals(AfterLOADWb.get(3), "LH.LDG", "Waybill table Source_Modify_ID is wrong " + CurrentPro);
			Date f = CommonFunction.SETtime((Date) AfterLOADWb.get(9));
			SAssert.assertTrue(Math.abs(f.getTime() - d.getTime()) < 120000,
					"waybill table system_modify_ts  " + f + "  " + d + "  " + "   " + CurrentPro);

		}
		SAssert.assertAll();

	}

	@Test(priority = 5, dataProvider = "2000.06", dataProviderClass = DataForUS200006.class)
	public void VerifySinglePoisProForFoodTrailer(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		// List<ArrayList<String>>
		// PROonTrailerBeforeAdd=DataForUS200006.GetProList(SCAC,TrailerNB);
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "pois");
		for (int i = 0; i < 1; i++) {
			page.RemoveProButton.click();
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			// enter cube
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);

			page.SubmitButton1.click();
			w1.until(ExpectedConditions.textToBePresentInElement(
					page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div")),
					"Bill is POISON. Food already on trailer."));
			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SAssert.assertFalse(AfterLOADWb.get(0).equals(SCAC) && AfterLOADWb.get(1).equals(TrailerNB),
					"Waybill table Standard_Carrier_Alpha_CD and Equipment_Unit_NBis wrong  " + CurrentPro);
		}

		SAssert.assertAll();
	}

	@Test(priority = 6, dataProvider = "2000.06", dataProviderClass = DataForUS200006.class)
	public void VerifyBatchProForFoodTrailer(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<String> Addpro = new ArrayList<String>();
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "pois");
		page.RemoveProButton.click();
		for (int i = 0; i < 2; i++) {
			String pro = PRO.get(i);
			page.EnterPro(pro);
			Addpro.add(pro);
		}
		page.SubmitButton1.click();
		w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"There was an error while adding all your pros to the trailer."));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

		for (int pro = 1; pro <= Addpro.size(); pro++) {
			String CurrentPro = Addpro.get(pro - 1);
			String CurrentProH = page.addHyphenToPro(CurrentPro);
			SAssert.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[2]/div")).getText(),
					CurrentProH); // check pro
			SAssert.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[3]/div")).getText(),
					"Bill is POISON. Food already on trailer."); // check error
																	// message
			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SAssert.assertFalse(AfterLOADWb.get(0).equals(SCAC) && AfterLOADWb.get(1).equals(TrailerNB),
					"Waybill table Standard_Carrier_Alpha_CD and Equipment_Unit_NBis wrong  " + CurrentPro);
		}

		SAssert.assertAll();
	}

	@Test(priority = 7, dataProvider = "2000.06", dataProviderClass = DataForUS200006.class)
	public void VerifySingleProForPoisonTrailer(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "food");
		for (int i = 0; i < 1; i++) {
			page.RemoveProButton.click();
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();

			// enter cube
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
			page.SubmitButton1.click();
			(new WebDriverWait(driver, 30)).until(ExpectedConditions.textToBePresentInElement(
					page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div")),
					"Bill is FOOD. Poison already on trailer."));
			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SAssert.assertFalse(AfterLOADWb.get(0).equals(SCAC) && AfterLOADWb.get(1).equals(TrailerNB),
					"Waybill table Standard_Carrier_Alpha_CD and Equipment_Unit_NBis wrong  " + CurrentPro);
		}

		SAssert.assertAll();
	}

	@Test(priority = 8, dataProvider = "2000.06", dataProviderClass = DataForUS200006.class)
	public void VerifyBatchProForPoisonTrailer(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<String> Addpro = new ArrayList<String>();
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "food");
		page.RemoveProButton.click();
		for (int i = 0; i < 2; i++) {
			String pro = PRO.get(i);
			page.EnterPro(pro);
			Addpro.add(pro);
		}
		page.SubmitButton1.click();
		w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"There was an error while adding all your pros to the trailer."));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		for (int pro = 1; pro <= Addpro.size(); pro++) {
			String CurrentPro = Addpro.get(pro - 1);
			String CurrentProH = page.addHyphenToPro(CurrentPro);
			SAssert.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[2]/div")).getText(),
					CurrentProH); // check pro
			SAssert.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[3]/div")).getText(),
					"Bill is FOOD. Poison already on trailer."); // check error
																	// message
			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SAssert.assertFalse(AfterLOADWb.get(0).equals(SCAC) && AfterLOADWb.get(1).equals(TrailerNB),
					"Waybill table Standard_Carrier_Alpha_CD and Equipment_Unit_NBis wrong  " + CurrentPro);
		}
		SAssert.assertAll();
	}

	@Test(priority = 9, dataProvider = "ldg trailer with both food and pois", dataProviderClass = DataForUS200006.class)
	public void VerifySinglePoisProForTrailerWithBothFOODandPOIS(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		// List<ArrayList<String>>
		// PROonTrailerBeforeAdd=DataForUS200006.GetProList(SCAC,TrailerNB);
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "pois");
		for (int i = 0; i < 1; i++) {
			page.RemoveProButton.click();
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			// enter cube
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);

			page.SubmitButton1.click();
			w1.until(ExpectedConditions.textToBePresentInElement(
					page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div")),
					"Bill is POISON. Food already on trailer."));
			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SAssert.assertFalse(AfterLOADWb.get(0).equals(SCAC) && AfterLOADWb.get(1).equals(TrailerNB),
					"Waybill table Standard_Carrier_Alpha_CD and Equipment_Unit_NBis wrong  " + CurrentPro);
		}

		SAssert.assertAll();
	}

	@Test(priority = 10, dataProvider = "ldg trailer with both food and pois", dataProviderClass = DataForUS200006.class)
	public void VerifyBatchPoisProForTrailerWithBothFOODandPOIS(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<String> Addpro = new ArrayList<String>();
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "pois");
		page.RemoveProButton.click();
		for (int i = 0; i < 2; i++) {
			String pro = PRO.get(i);
			page.EnterPro(pro);
			Addpro.add(pro);
		}
		page.SubmitButton1.click();
		w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"There was an error while adding all your pros to the trailer."));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

		for (int pro = 1; pro <= Addpro.size(); pro++) {
			String CurrentPro = Addpro.get(pro - 1);
			String CurrentProH = page.addHyphenToPro(CurrentPro);
			SAssert.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[2]/div")).getText(),
					CurrentProH); // check pro
			SAssert.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[3]/div")).getText(),
					"Bill is POISON. Food already on trailer."); // check error
																	// message
			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SAssert.assertFalse(AfterLOADWb.get(0).equals(SCAC) && AfterLOADWb.get(1).equals(TrailerNB),
					"Waybill table Standard_Carrier_Alpha_CD and Equipment_Unit_NBis wrong  " + CurrentPro);
		}

		SAssert.assertAll();
	}

	@Test(priority = 11, dataProvider = "ldg trailer with both food and pois", dataProviderClass = DataForUS200006.class)
	public void VerifySingleFoodProForTrailerWithBothFOODandPOIS(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "food");
		for (int i = 0; i < 1; i++) {
			page.RemoveProButton.click();
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();

			// enter cube
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
			page.SubmitButton1.click();
			(new WebDriverWait(driver, 30)).until(ExpectedConditions.textToBePresentInElement(
					page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div")),
					"Bill is FOOD. Poison already on trailer."));
			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SAssert.assertFalse(AfterLOADWb.get(0).equals(SCAC) && AfterLOADWb.get(1).equals(TrailerNB),
					"Waybill table Standard_Carrier_Alpha_CD and Equipment_Unit_NBis wrong  " + CurrentPro);
		}

		SAssert.assertAll();
	}

	@Test(priority = 12, dataProvider = "ldg trailer with both food and pois", dataProviderClass = DataForUS200006.class)
	public void VerifyBatchFoodProTrailerWithBothFOODandPOIS(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<String> Addpro = new ArrayList<String>();
		ArrayList<String> PRO = DataCommon.GetProWithType(SCAC, TrailerNB, "food");
		page.RemoveProButton.click();
		for (int i = 0; i < 2; i++) {
			String pro = PRO.get(i);
			page.EnterPro(pro);
			Addpro.add(pro);
		}
		page.SubmitButton1.click();
		w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"There was an error while adding all your pros to the trailer."));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		for (int pro = 1; pro <= Addpro.size(); pro++) {
			String CurrentPro = Addpro.get(pro - 1);
			String CurrentProH = page.addHyphenToPro(CurrentPro);
			SAssert.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[2]/div")).getText(),
					CurrentProH); // check pro
			SAssert.assertEquals(page.AddProForm.findElement(By.xpath("div[" + pro + "]/div/div[3]/div")).getText(),
					"Bill is FOOD. Poison already on trailer."); // check error
																	// message
			// check waybill
			ArrayList<Object> AfterLOADWb = DataCommon.GetWaybillInformationOfPro(CurrentPro);
			SAssert.assertFalse(AfterLOADWb.get(0).equals(SCAC) && AfterLOADWb.get(1).equals(TrailerNB),
					"Waybill table Standard_Carrier_Alpha_CD and Equipment_Unit_NBis wrong  " + CurrentPro);
		}
		SAssert.assertAll();
	}
}