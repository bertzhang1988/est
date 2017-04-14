package TestCase.LdgScreen;

import java.awt.AWTException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Data.DataForUS200075;
import Function.CommonFunction;
import Function.DataCommon;
import Function.Setup;
import Page.EqpStatusPageS;

public class US200075DisplayPROsInOrderWhichTheyEnter extends Setup {
	private EqpStatusPageS page;
	private WebDriverWait w1;
	private WebDriverWait w2;

	@Test(priority = 1, dataProvider = "2000.75", dataProviderClass = DataForUS200075.class, groups = { "ldg uc" })
	public void AddSinglePro(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<String> PRO = DataCommon.GetProNotInAnyTrailer();
		page.RemoveProButton.click();
		for (int i = 0; i < 3; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			String CurrentProH = page.addHyphenToPro(CurrentPro);
			// page.AddProCheckBoxList.findElement(By.xpath("div["+NEW1+"]/div/div/div/div")).click();
			int Ran = (int) (Math.random() * 99) + 1;
			page.SetCube(Integer.toString(Ran));
			page.SubmitButton1.click();
			w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
			w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
			w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			page.EnterTrailer(SCAC, TrailerNB);
			w1.until(ExpectedConditions.textToBePresentInElement(page.ProListForm, CurrentProH));
			int NEW2 = page.ProListForm.findElements(By.xpath("div")).size();
			Assert.assertEquals(page.ProListForm.findElement(By.xpath("div[" + NEW2 + "]/div/div[2]/div")).getText(),
					CurrentProH);

		}
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListForm);
		SA.assertEquals(ProInfo, DataCommon.GetProListLD(SCAC, TrailerNB), " pro grid is wrong");
		SA.assertAll();

	}

	@Test(priority = 2, dataProvider = "2000.75", dataProviderClass = DataForUS200075.class, groups = { "ldg uc" })
	public void AddMultipleProsInSingleBatch(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<String> PRO = DataCommon.GetProNotInAnyTrailer();
		page.RemoveProButton.click();
		ArrayList<String> Addpro = new ArrayList<String>();
		for (int i = 0; i < 3; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			Addpro.add(CurrentPro);
		}

		int Ran = (int) (Math.random() * 99) + 1;
		page.SetCube(Integer.toString(Ran));
		ArrayList<Object> OldEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		page.SubmitButton1.click();
		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		page.EnterTrailer(SCAC, TrailerNB);
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListForm);
		SA.assertEquals(ProInfo, DataCommon.GetProListLD(SCAC, TrailerNB), " pro grid is wrong");
		// check right grid is as same as left grid
		ArrayList<String> AddproBatch = new ArrayList<String>();
		Iterator<ArrayList<String>> pr = ProInfo.iterator();
		while (pr.hasNext()) {
			ArrayList<String> pro = pr.next();
			AddproBatch.add(pro.get(0).replaceAll("-", ""));
		}
		SA.assertEquals(AddproBatch, Addpro, " pro sequence is wrong");
		// check eqps
		ArrayList<Object> NewEqpStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SA.assertEquals(NewEqpStatusRecord.get(0), "LDG", "Equipment_Status_Type_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd, "Statusing_Facility_CD is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(19), "LH.LDG", "Source_Modify_ID is wrong");
		Date TS = CommonFunction.SETtime((Date) NewEqpStatusRecord.get(5));
		SA.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, "modify_ts is wrong  " + TS + "  " + d);
		for (int i = 6; i <= 8; i++) {
			SA.assertEquals(NewEqpStatusRecord.get(i), OldEqpStatusRecord.get(i),
					i + "  " + NewEqpStatusRecord.get(i) + "  " + OldEqpStatusRecord.get(i));
		}
		SA.assertEquals(NewEqpStatusRecord.get(16), conf.GetAD_ID(), "modify_id is wrong");
		SA.assertEquals(NewEqpStatusRecord.get(17), conf.GetM_ID(), "eqps Mainframe_User_ID is wrong");
		// check eqp
		ArrayList<Object> NewEqp = DataCommon.CheckEquipment(SCAC, TrailerNB);
		SA.assertEquals(NewEqp.get(0), conf.GetM_ID(), " eqp Mainframe_User_ID is wrong");
		SA.assertAll();
	}

	@Test(priority = 3, dataProvider = "2000.75", dataProviderClass = DataForUS200075.class, groups = { "ldg uc" })
	public void AddTwoBatchesProsInSameSection(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<String> PRO = DataCommon.GetProNotInAnyTrailer();
		page.RemoveProButton.click();
		ArrayList<String> Addpro = new ArrayList<String>();
		for (int i = 0; i < 3; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			Addpro.add(CurrentPro);
		}

		int Ran = (int) (Math.random() * 99) + 1;
		page.SetCube(Integer.toString(Ran));

		page.SubmitButton1.click();
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		page.EnterTrailer(SCAC, TrailerNB);
		page.RemoveProButton.click();
		PRO = DataCommon.GetProNotInAnyTrailer();
		//
		for (int i = 0; i < 2; i++) {
			String CurrentPro = PRO.get(i);
			page.EnterPro(CurrentPro);
			Thread.sleep(500);
			int NEW1 = page.AddProForm.findElements(By.xpath("div")).size();
			Assert.assertEquals(page.AddProForm.findElement(By.xpath("div[" + NEW1 + "]/div/div[2]/div")).getText(),
					page.addHyphenToPro(CurrentPro));
			Addpro.add(CurrentPro);
		}
		page.SubmitButton1.click();
		w2.until(ExpectedConditions.visibilityOf(page.TrailerInputField));
		w2.until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField, ""));
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

		page.EnterTrailer(SCAC, TrailerNB);
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListForm);
		SA.assertEquals(ProInfo, DataCommon.GetProListLD(SCAC, TrailerNB), " pro grid is wrong");
		// check right grid is as same as left grid
		ArrayList<String> AddproBatch = new ArrayList<String>();
		Iterator<ArrayList<String>> pr = ProInfo.iterator();
		while (pr.hasNext()) {
			ArrayList<String> pro = pr.next();
			AddproBatch.add(pro.get(0).replaceAll("-", ""));
		}
		SA.assertEquals(AddproBatch, Addpro, " pro sequence is wrong");
		SA.assertAll();

	}

	@BeforeClass(groups = { "ldg uc" })
	public void SetUp() throws AWTException, InterruptedException {
		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 50);
		w2 = new WebDriverWait(driver, 80);
		  
		driver.manage().window().maximize();
		page.SetStatus("ldg");
	}
}
