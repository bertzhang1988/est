package TestCase.LDDscreen;

import java.awt.AWTException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Data.DataForUS607;
import Function.CommonFunction;
import Function.DataCommon;
import Function.SetupBrowser;
import Page.EqpStatusPageS;

public class US622SmartEnterHeadloadOnDestinationChangeLDD extends SetupBrowser {
	private EqpStatusPageS page;
	private Actions builder;
	private WebDriverWait w1;
	private WebDriverWait w2;

	@BeforeClass
	public void SetUp() throws AWTException, InterruptedException {
		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 50);
		w2 = new WebDriverWait(driver, 150);
		driver.get(conf.GetURL());
		driver.manage().window().maximize();
		page.SetStatus("ldd");
		builder = new Actions(driver);
	}
	@AfterMethod
	public void setback() throws InterruptedException {
		page.SetStatus("ldd");
	}

	@Test(priority = 1, dataProvider = "6.07", dataProviderClass = DataForUS607.class, description = "2000.109")
	public void lddTrailerWithProsYes(String terminalcd, String SCAC, String TrailerNB, String OrgiDesti, String cube,
			String HLDestination, String HLCube)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// ENTER SEAL
		if (page.SealField.getAttribute("value").replace("_", "").equalsIgnoreCase("")) {
			page.ChangeSeal();
		}

		// change destination
		String changeDesti = page.ChangeDestiantion();

		// check the hl screen
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Mark PROs as Headload"));
		w1.until(ExpectedConditions.visibilityOf(page.HeadloadProForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		SAssert.assertEquals(page.HeadloadDestination.getAttribute("value"), OrgiDesti, "screenHldestination is wrong");
		SAssert.assertEquals(page.HLCubeField.getAttribute("value"), cube, "screenHLcube is wrong");
		SAssert.assertEquals(page.ManifestToField.getText(), OrgiDesti, "screen manifest dest is wrong");
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti, "screen desti is wrong");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), cube, "screen cube is wrong");
		page.hlCancelButton.click();// click cancel
		// navigate back to ldd screen
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Closed"));
		w1.until(ExpectedConditions.visibilityOf(page.ProListLDDForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		Assert.assertEquals(page.DestinationField.getAttribute("value"), OrgiDesti);

		/// input cube if there is no cube value before
		if (page.CubeField.getAttribute("value").equalsIgnoreCase("")) {
			page.ChangeCube();
		}

		// enter seal
		if (page.SealField.getAttribute("value").replace("_", "").equalsIgnoreCase("")) {
			page.ChangeSeal();
		}

		// change destination
		page.SetDestination(changeDesti);
		// chcek hl screen
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Mark PROs as Headload"));
		w1.until(ExpectedConditions.visibilityOf(page.HeadloadProForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		SAssert.assertEquals(page.HeadloadDestination.getAttribute("value"), OrgiDesti, "screenHldestination is wrong");
		SAssert.assertEquals(page.HLCubeField.getAttribute("value"), cube, "screenHLcube is wrong");
		SAssert.assertEquals(page.ManifestToField.getText(), OrgiDesti, "screen manifest dest is wrong");
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti, "screen desti is wrong");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), cube, "screen cube is wrong");
		ArrayList<ArrayList<Object>> WbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		builder.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.visibilityOf(page.ProListLDDForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		Thread.sleep(3000);
		// check eqpststus
		ArrayList<Object> NewEQPStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEQPStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong"); // equipment_dest_facility_cd
		SAssert.assertEquals(NewEQPStatusRecord.get(12), cube, "Headload_Capacity_Consumed_PC is wring"); // Headload_Capacity_Consumed_PC
		SAssert.assertEquals(NewEQPStatusRecord.get(13), OrgiDesti, "headload_dest_facility_cd is wrong"); // headload_dest_facility_cd
		SAssert.assertEquals(NewEQPStatusRecord.get(14), terminalcd, "headload_origin_facility_CD is wrong"); // headload_origin_facility_CD
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEQPStatusRecord.get(i));
			if (i == 7) {
				SAssert.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}
		// check waybill
		ArrayList<ArrayList<Object>> NewWbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		int i = 0;
		Date f2 = null;
		for (ArrayList<Object> Currentwbti : NewWbtRecord) {
			SAssert.assertEquals(Currentwbti.get(1), "Y", Currentwbti.get(0) + "  Headload_IN is wrong"); // Headload_IN
			Date TS = CommonFunction.SETtime((Date) Currentwbti.get(3));
			SAssert.assertEquals(Currentwbti.get(3), WbtRecord.get(i).get(3),
					Currentwbti.get(0) + " Waybill_Transaction_End_TS is wrong");// Waybill_Transaction_End_TS
			SAssert.assertEquals(Currentwbti.get(2), WbtRecord.get(i).get(2),
					Currentwbti.get(0) + " manifest_destination is wrong");
			SAssert.assertEquals(Currentwbti.get(4), "LH.LDD", Currentwbti.get(0) + " source_modify_ID is wrong");
			// CHECK THE WAYBILL TRANSACTION END TS ARE NOT IDENTICAL
			// if(i>0) SAssert.assertTrue(TS.after(f2),"waybill
			// Waybill_Transaction_End_TS is not ascending increase :
			// waybill_transaction_end_ts "+TS+" waybill_transaction_end_ts of
			// previous pro is "+f2+" "+" "+Currentwbti.get(0));
			f2 = TS;
			i = i + 1;
		}
		SAssert.assertAll();

	}

	@Test(priority = 2, dataProvider = "6.07", dataProviderClass = DataForUS607.class)
	public void ldgTrailerWithProsYes(String terminalcd, String SCAC, String TrailerNB, String OrgiDesti, String cube,
			String HLDestination, String HLCube)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// ENTER SEAL
		if (page.SealField.getAttribute("value").replace("_", "").equalsIgnoreCase("")) {
			page.ChangeSeal();
		}

		// change destination
		String changeDesti = page.ChangeDestiantion();

		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Mark PROs as Headload"));
		w1.until(ExpectedConditions.visibilityOf(page.HeadloadProForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		SAssert.assertEquals(page.HeadloadDestination.getAttribute("value"), OrgiDesti, "screenHldestination is wrong");
		SAssert.assertEquals(page.HLCubeField.getAttribute("value"), cube, "screenHLcube is wrong");
		SAssert.assertEquals(page.ManifestToField.getText(), OrgiDesti, "screen manifest dest is wrong");
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti, "screen desti is wrong");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), cube, "screen cube is wrong");
		page.hlCancelButton.click();// click cancel
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Closed"));
		w1.until(ExpectedConditions.visibilityOf(page.ProListLDDForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		Assert.assertEquals(page.DestinationField.getAttribute("value"), OrgiDesti);

		// input cube if there is no cube value before
		if (page.CubeField.getAttribute("value").equalsIgnoreCase("")) {
			page.ChangeCube();
		}

		// enter seal
		if (page.SealField.getAttribute("value").replace("_", "").equalsIgnoreCase("")) {
			page.ChangeSeal();
		}

		// change destination
		page.SetDestination(changeDesti);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Mark PROs as Headload"));
		w1.until(ExpectedConditions.visibilityOf(page.HeadloadProForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		SAssert.assertEquals(page.HeadloadDestination.getAttribute("value"), OrgiDesti, "screenHldestination is wrong");
		SAssert.assertEquals(page.HLCubeField.getAttribute("value"), cube, "screenHLcube is wrong");
		SAssert.assertEquals(page.ManifestToField.getText(), OrgiDesti, "screen manifest dest is wrong");
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti, "screen desti is wrong");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), cube, "screen cube is wrong");
		ArrayList<ArrayList<Object>> WbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		builder.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.visibilityOf(page.ProListLDDForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		// check eqpststus
		ArrayList<Object> NewEQPStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEQPStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong"); // equipment_dest_facility_cd
		SAssert.assertEquals(NewEQPStatusRecord.get(12), cube, "Headload_Capacity_Consumed_PC is wring"); // Headload_Capacity_Consumed_PC
		SAssert.assertEquals(NewEQPStatusRecord.get(13), OrgiDesti, "headload_dest_facility_cd is wrong"); // headload_dest_facility_cd
		SAssert.assertEquals(NewEQPStatusRecord.get(14), terminalcd, "headload_origin_facility_CD is wrong"); // headload_origin_facility_CD
		SAssert.assertEquals(NewEQPStatusRecord.get(0), "LDD", "STATUS IS NOT SET TO LDD");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEQPStatusRecord.get(i));
			if (i == 7) {
				SAssert.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}
		// check waybill
		ArrayList<ArrayList<Object>> NewWbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		int i = 0;
		Date f2 = null;
		for (ArrayList<Object> Currentwbti : NewWbtRecord) {
			SAssert.assertEquals(Currentwbti.get(1), "Y", Currentwbti.get(0) + "  Headload_IN is wrong"); // Headload_IN
			Date TS = CommonFunction.SETtime((Date) Currentwbti.get(3));
			SAssert.assertEquals(Currentwbti.get(3), WbtRecord.get(i).get(3),
					Currentwbti.get(0) + " Waybill_Transaction_End_TS is wrong");// Waybill_Transaction_End_TS
			SAssert.assertEquals(Currentwbti.get(2), WbtRecord.get(i).get(2),
					Currentwbti.get(0) + " manifest_destination is wrong");
			SAssert.assertEquals(Currentwbti.get(4), "LH.LDD", Currentwbti.get(0) + " source_modify_ID is wrong");
			// CHECK THE WAYBILL TRANSACTION END TS ARE NOT IDENTICAL
			// if(i>0) SAssert.assertTrue(TS.after(f2),"waybill
			// Waybill_Transaction_End_TS is not ascending increase :
			// waybill_transaction_end_ts "+TS+" waybill_transaction_end_ts of
			// previous pro is "+f2+" "+" "+Currentwbti.get(0));
			f2 = TS;
			i = i + 1;
		}
		SAssert.assertAll();

	}

	@Test(priority = 5, dataProvider = "6.07", dataProviderClass = DataForUS607.class)
	public void lddTrailerWithHeadloadProsClickYes(String terminalcd, String SCAC, String TrailerNB, String OrgiDesti,
			String cube, String HLDestination, String HLCube)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// enter cube
		String NewCube = page.ChangeCube();

		// ENTER SEAL
		if (page.SealField.getAttribute("value").replace("_", "").equalsIgnoreCase("")) {
			page.ChangeSeal();
		}

		// change destination
		String changeDesti = page.ChangeDestiantion();

		w1.until(ExpectedConditions.visibilityOf(page.HeadloadProForm));
		SAssert.assertEquals(page.HeadloadDestination.getAttribute("value"), OrgiDesti, "screenHldestination is wrong");
		SAssert.assertEquals(page.HLCubeField.getAttribute("value"), NewCube, "screenHLcube is wrong");
		SAssert.assertEquals(page.ManifestToField.getText(), OrgiDesti, "screen manifest dest is wrong");
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti, "screen desti is wrong");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), NewCube, "screen cube is wrong");
		page.hlCancelButton.click();// click cancel
		w2.until(ExpectedConditions.visibilityOf(page.ProListLDDForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar-spinner")));
		// Assert.assertEquals(page.DestinationField.getAttribute("value"),OrgiDesti);

		page.SetCube(NewCube);

		// change seal
		if (page.SealField.getAttribute("value").equalsIgnoreCase("__________")
				|| page.SealField.getAttribute("value").equalsIgnoreCase("")) {
			page.ChangeSeal();
		}
		page.SetDestination(changeDesti);
		w1.until(ExpectedConditions.visibilityOf(page.HeadloadProForm));
		SAssert.assertEquals(page.HeadloadDestination.getAttribute("value"), OrgiDesti, "screenHldestination is wrong");
		SAssert.assertEquals(page.HLCubeField.getAttribute("value"), NewCube, "screenHLcube is wrong");
		SAssert.assertEquals(page.ManifestToField.getText(), OrgiDesti, "screen manifest dest is wrong");
		SAssert.assertEquals(page.DestinationField.getAttribute("value"), changeDesti, "screen desti is wrong");
		SAssert.assertEquals(page.CubeField.getAttribute("value"), NewCube, "screen cube is wrong");
		ArrayList<Object> EQPStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		ArrayList<ArrayList<Object>> WbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		// alter time
		page.SetDatePicker(page.GetDatePickerTime(), -3);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
		builder.sendKeys(Keys.ENTER).build().perform();
		Date d = CommonFunction.gettime("UTC");
		w2.until(ExpectedConditions.visibilityOf(page.ProListLDDForm));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		// check eqpststus
		ArrayList<Object> NewEQPStatusRecord = DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
		SAssert.assertEquals(NewEQPStatusRecord.get(2), changeDesti, "equipment_dest_facility_cd is wrong"); // equipment_dest_facility_cd
		SAssert.assertEquals(NewEQPStatusRecord.get(12), NewCube, "Headload_Capacity_Consumed_PC is wrong"); // Headload_Capacity_Consumed_PC
		SAssert.assertEquals(NewEQPStatusRecord.get(13), OrgiDesti, "headload_dest_facility_cd is wrong"); // headload_dest_facility_cd
		SAssert.assertEquals(NewEQPStatusRecord.get(14), terminalcd, "eqps headload_origin_facility_CD is wrong");
		for (int i = 5; i <= 8; i++) {
			Date TS = CommonFunction.SETtime((Date) NewEQPStatusRecord.get(i));
			if (i == 7) {
				SAssert.assertTrue(Math.abs(TS.getTime() - AlterTime.getTime()) < 60000,
						"equipment_status_ts " + "  " + TS + "  " + AlterTime);
			} else {
				SAssert.assertTrue(Math.abs(TS.getTime() - d.getTime()) < 120000, i + "  " + TS + "  " + d);
			}
		}
		// check waybill
		ArrayList<ArrayList<Object>> NewWbtRecord = DataCommon.CheckWaybillUpdateForHL(SCAC, TrailerNB);
		int i = 0;
		Date f2 = null;
		for (ArrayList<Object> Currentwbti : NewWbtRecord) {
			SAssert.assertEquals(Currentwbti.get(1), "Y", Currentwbti.get(0) + "  Headload_IN is wrong"); // Headload_IN
			Date TS = CommonFunction.SETtime((Date) Currentwbti.get(3));
			SAssert.assertEquals(Currentwbti.get(3), WbtRecord.get(i).get(3),
					Currentwbti.get(0) + " Waybill_Transaction_End_TS is wrong");// Waybill_Transaction_End_TS
			SAssert.assertEquals(Currentwbti.get(2), WbtRecord.get(i).get(2),
					Currentwbti.get(0) + " manifest_destination is wrong");
			SAssert.assertEquals(Currentwbti.get(4), "LH.LDD", Currentwbti.get(0) + " source_modify_ID is wrong");
			// CHECK THE WAYBILL TRANSACTION END TS ARE NOT IDENTICAL
			// if(i>0) SAssert.assertTrue(TS.after(f2),"waybill
			// Waybill_Transaction_End_TS is not ascending increase :
			// waybill_transaction_end_ts "+TS+" waybill_transaction_end_ts of
			// previous pro is "+f2+" "+" "+Currentwbti.get(0));
			f2 = TS;
			i = i + 1;
		}
		SAssert.assertAll();
	}

}
