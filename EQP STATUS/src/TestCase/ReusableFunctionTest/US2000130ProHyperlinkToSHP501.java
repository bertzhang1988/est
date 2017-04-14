package TestCase.ReusableFunctionTest;

import java.awt.AWTException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Function.CommonFunction;
import Function.DataCommon;
import Function.Setup;
import Page.EqpStatusPageS;
import TestCase.CLTGtesting.DataForCLTGScreenTesting;
import TestCase.CLtesting.DataForCLScreenTesting;
import TestCase.LDDscreen.DataForUSLDDLifeTest;
import TestCase.LdgScreen.DataForUSLDGLifeTest;

public class US2000130ProHyperlinkToSHP501 extends Setup {

	private EqpStatusPageS page;

	@BeforeClass()
	public void SetUp() throws AWTException, InterruptedException, IOException {
		page = new EqpStatusPageS(driver);
		  
		driver.manage().window().maximize();

	}

	@Test(priority = 1, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class)
	public void LDGTrailerWithProCheckProHyperlink(String terminalcd, String SCAC, String TrailerNB, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetStatus("ldg");
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListForm);
		SA.assertEquals(ProInfo, DataCommon.GetProListLD(SCAC, TrailerNB), "pro grid is wrong");

		// Check pro hyperlink
		ArrayList<String> Prolist = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		String CurrentWindowHandle = driver.getWindowHandle();
		int times = 0;
		for (String Pro : Prolist) {
			times++;
			if (times == 5)
				break;
			page.ProListForm.findElement(By.linkText(CommonFunction.addHyphenToPro(Pro))).click();
			(new WebDriverWait(driver, 50)).until(ExpectedConditions.numberOfWindowsToBe(2));
			Set<String> WindowHandles = driver.getWindowHandles();
			for (String windowHandle : WindowHandles) {
				if (!windowHandle.equalsIgnoreCase(CurrentWindowHandle)) {
					driver.switchTo().window(windowHandle);
				}
			}
			String GetTitleOfWindow = driver.getTitle();
			String GetShp501Url = driver.getCurrentUrl();
			SA.assertEquals(GetTitleOfWindow, "SHP501 - Shipment Inquiry", Pro + " does not kick off the SHP501");
			SA.assertEquals(GetShp501Url, "http://tmssit1.yrcw.com/webapps/tms/shp501.html?nxtData=" + Pro + "",
					Pro + " SHP501 URL IS WRONG");
			driver.close();
			driver.switchTo().window(CurrentWindowHandle);
		}

		SA.assertAll();
	}

	@Test(priority = 2, dataProvider = "lddscreen2", dataProviderClass = DataForUSLDDLifeTest.class)
	public void LDDTrailerHasProCheckProHyperlink(String terminalcd, String SCAC, String TrailerNB, String Desti,
			String AmountPro, String AmountWeight, String Cube, String Seal, String headloadDestination,
			String headloadCube, Date MReqpst, String CurrentStatus, String flag, String serv)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		page.SetStatus("ldd");
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListLDDForm);
		SA.assertEquals(ProInfo, DataCommon.GetProListLD(SCAC, TrailerNB), "prolist information is wrong");
		// Check pro hyperlink
		ArrayList<String> Prolist = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		String CurrentWindowHandle = driver.getWindowHandle();
		int times = 0;
		for (String Pro : Prolist) {
			times++;
			if (times == 5)
				break;
			page.ProListLDDForm.findElement(By.linkText(CommonFunction.addHyphenToPro(Pro))).click();
			(new WebDriverWait(driver, 50)).until(ExpectedConditions.numberOfWindowsToBe(2));
			Set<String> WindowHandles = driver.getWindowHandles();
			for (String windowHandle : WindowHandles) {
				if (!windowHandle.equalsIgnoreCase(CurrentWindowHandle)) {
					driver.switchTo().window(windowHandle);
				}
			}
			String GetTitleOfWindow = driver.getTitle();
			String GetShp501Url = driver.getCurrentUrl();
			SA.assertEquals(GetTitleOfWindow, "SHP501 - Shipment Inquiry", Pro + " does not kick off the SHP501");
			SA.assertEquals(GetShp501Url, "http://tmssit1.yrcw.com/webapps/tms/shp501.html?nxtData=" + Pro + "",
					Pro + " SHP501 URL IS WRONG");
			driver.close();
			driver.switchTo().window(CurrentWindowHandle);
		}

		SA.assertAll();

	}

	@Test(priority = 3, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class)
	public void CLHasProCheckProHyperlink(String terminalcd, String SCAC, String TrailerNB, String CityR, String CityRT,
			String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetStatus("cl");
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// check pro grid prepopulate
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListForm);
		SA.assertEquals(DataCommon.GetProListCL(SCAC, TrailerNB), ProInfo, "pro grid is wrong");

		// Check pro hyperlink
		ArrayList<String> Prolist = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		String CurrentWindowHandle = driver.getWindowHandle();
		int times = 0;
		for (String Pro : Prolist) {
			times++;
			if (times == 5)
				break;
			page.ProListForm.findElement(By.linkText(CommonFunction.addHyphenToPro(Pro))).click();
			(new WebDriverWait(driver, 50)).until(ExpectedConditions.numberOfWindowsToBe(2));
			Set<String> WindowHandles = driver.getWindowHandles();
			for (String windowHandle : WindowHandles) {
				if (!windowHandle.equalsIgnoreCase(CurrentWindowHandle)) {
					driver.switchTo().window(windowHandle);
				}
			}
			String GetTitleOfWindow = driver.getTitle();
			String GetShp501Url = driver.getCurrentUrl();
			SA.assertEquals(GetTitleOfWindow, "SHP501 - Shipment Inquiry", Pro + " does not kick off the SHP501");
			SA.assertEquals(GetShp501Url, "http://tmssit1.yrcw.com/webapps/tms/shp501.html?nxtData=" + Pro + "",
					Pro + " SHP501 URL IS WRONG");
			driver.close();
			driver.switchTo().window(CurrentWindowHandle);
		}

		SA.assertAll();
	}

	@Test(priority = 4, dataProvider = "cltg screen 1", dataProviderClass = DataForCLTGScreenTesting.class)
	public void CLTGWithProSetToCLTG(String terminalcd, String SCAC, String TrailerNB, String CityR, String CityRT,
			String AmountPro, String AmountWeight, String flag, String serv, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetStatus("CLTG");
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListLDDForm);
		LinkedHashSet<ArrayList<String>> ExpectedProInformation = DataCommon.GetProListCLTG(SCAC, TrailerNB);
		SA.assertEquals(ProInfo, ExpectedProInformation,
				"cltg screen pro grid is wrong\n" + ExpectedProInformation + "\n" + ProInfo + "\n");

		// Check pro hyperlink
		ArrayList<String> Prolist = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		String CurrentWindowHandle = driver.getWindowHandle();
		int times = 0;
		for (String Pro : Prolist) {
			times++;
			if (times == 5)
				break;
			page.ProListLDDForm.findElement(By.linkText(CommonFunction.addHyphenToPro(Pro))).click();
			(new WebDriverWait(driver, 50)).until(ExpectedConditions.numberOfWindowsToBe(2));
			Set<String> WindowHandles = driver.getWindowHandles();
			for (String windowHandle : WindowHandles) {
				if (!windowHandle.equalsIgnoreCase(CurrentWindowHandle)) {
					driver.switchTo().window(windowHandle);
				}
			}
			String GetTitleOfWindow = driver.getTitle();
			String GetShp501Url = driver.getCurrentUrl();
			SA.assertEquals(GetTitleOfWindow, "SHP501 - Shipment Inquiry", Pro + " does not kick off the SHP501");
			SA.assertEquals(GetShp501Url, "http://tmssit1.yrcw.com/webapps/tms/shp501.html?nxtData=" + Pro + "",
					Pro + " SHP501 URL IS WRONG");
			driver.close();
			driver.switchTo().window(CurrentWindowHandle);
		}

		SA.assertAll();

	}

}
