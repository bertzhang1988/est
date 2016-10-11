package TestCase.TrailerInquiryTesting;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
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
import TestCase.TerminalInquiryTesting.DataForInQuiryScreen;

public class TrailerInquiryScreen {
	private WebDriver driver;
	private EqpStatusPageS page;
	private Actions builder;

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
		page.SetTrailerInquiryScreen();
		builder = new Actions(driver);
	}

	@Test(priority = 1, dataProvider = "TrailerInquiry", dataProviderClass = DataForTrailerInquiryScreen.class)
	public void ldgTrailerWithPro(String SCAC, String TrailerNB, ArrayList<String> TrailerInformation, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.EnterTrailer(SCAC, TrailerNB);
		Date d = CommonFunction.gettime("UTC");

		// get hours
		long diff = d.getTime() - Mrst.getTime();
		long diffHours = diff / (60 * 60 * 1000);
		String Hrs = Long.toString(diffHours);
		TrailerInformation.add(Hrs);

		// check trailer grid
		String[] Trailerline1 = page.TIQtrailerGrid.getText().split("\\n");
		ArrayList<String> TrailerGrid = new ArrayList<String>(Arrays.asList(Trailerline1));
		SA.assertEquals(TrailerGrid, TrailerInformation,
				"trailer grid is wrong\nACTUAL:" + TrailerGrid + "\nEXPECT:" + TrailerInformation + "\n");

		// check pro grid
		LinkedHashSet<ArrayList<String>> GetProGrid = page.GetProListInInquiryScreen(page.TIQProGrid);
		LinkedHashSet<ArrayList<String>> ExpectedProGrid = DataForInQuiryScreen.GetProListInQuiry(SCAC, TrailerNB);
		SA.assertEquals(GetProGrid, ExpectedProGrid,
				"Pro grid is wrong \nActual :" + GetProGrid + "\nExpected :" + ExpectedProGrid + "\n");

		// Check pro hyperlink
		ArrayList<String> Prolist = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		String CurrentWindowHandle = driver.getWindowHandle();
		for (String Pro : Prolist) {
			page.TIQProGrid.findElement(By.linkText(CommonFunction.addHyphenToPro(Pro))).click();
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

	@Test(priority = 2, dataProvider = "TrailerInquiry", dataProviderClass = DataForTrailerInquiryScreen.class)
	public void lddTrailerWithPro(String SCAC, String TrailerNB, ArrayList<String> TrailerInformation, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.EnterTrailer(SCAC, TrailerNB);
		Date d = CommonFunction.gettime("UTC");

		// get hours
		long diff = d.getTime() - Mrst.getTime();
		long diffHours = diff / (60 * 60 * 1000);
		String Hrs = Long.toString(diffHours);
		TrailerInformation.add(Hrs);

		// check trailer grid
		String[] Trailerline1 = page.TIQtrailerGrid.getText().split("\\n");
		ArrayList<String> TrailerGrid = new ArrayList<String>(Arrays.asList(Trailerline1));
		SA.assertEquals(TrailerGrid, TrailerInformation,
				"trailer grid is wrong\nACTUAL:" + TrailerGrid + "\nEXPECT:" + TrailerInformation + "\n");

		// check pro grid
		LinkedHashSet<ArrayList<String>> GetProGrid = page.GetProListInInquiryScreen(page.TIQProGrid);
		LinkedHashSet<ArrayList<String>> ExpectedProGrid = DataForInQuiryScreen.GetProListInQuiry(SCAC, TrailerNB);
		SA.assertEquals(GetProGrid, ExpectedProGrid,
				"Pro grid is wrong \nActual :" + GetProGrid + "\nExpected :" + ExpectedProGrid + "\n");
		SA.assertAll();
	}

	@Test(priority = 3, dataProvider = "TrailerInquiry", dataProviderClass = DataForTrailerInquiryScreen.class)
	public void clTrailerWithPro(String SCAC, String TrailerNB, ArrayList<String> TrailerInformation, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.EnterTrailer(SCAC, TrailerNB);
		Date d = CommonFunction.gettime("UTC");

		// get hours
		long diff = d.getTime() - Mrst.getTime();
		long diffHours = diff / (60 * 60 * 1000);
		String Hrs = Long.toString(diffHours);
		TrailerInformation.add(Hrs);

		// check trailer grid
		String[] Trailerline1 = page.TIQtrailerGrid.getText().split("\\n");
		ArrayList<String> TrailerGrid = new ArrayList<String>(Arrays.asList(Trailerline1));
		SA.assertEquals(TrailerGrid, TrailerInformation,
				"trailer grid is wrong\nACTUAL:" + TrailerGrid + "\nEXPECT:" + TrailerInformation + "\n");

		// check pro grid
		LinkedHashSet<ArrayList<String>> GetProGrid = page.GetProListInInquiryScreen(page.TIQProGrid);
		LinkedHashSet<ArrayList<String>> ExpectedProGrid = DataForInQuiryScreen.GetProListInQuiry(SCAC, TrailerNB);
		SA.assertEquals(GetProGrid, ExpectedProGrid,
				"Pro grid is wrong \nActual :" + GetProGrid + "\nExpected :" + ExpectedProGrid + "\n");
		SA.assertAll();
	}

	@Test(priority = 4, dataProvider = "TrailerInquiry", dataProviderClass = DataForTrailerInquiryScreen.class)
	public void cltgTrailerWithPro(String SCAC, String TrailerNB, ArrayList<String> TrailerInformation, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.EnterTrailer(SCAC, TrailerNB);
		Date d = CommonFunction.gettime("UTC");

		// get hours
		long diff = d.getTime() - Mrst.getTime();
		long diffHours = diff / (60 * 60 * 1000);
		String Hrs = Long.toString(diffHours);
		TrailerInformation.add(Hrs);

		// check trailer grid
		String[] Trailerline1 = page.TIQtrailerGrid.getText().split("\\n");
		ArrayList<String> TrailerGrid = new ArrayList<String>(Arrays.asList(Trailerline1));
		SA.assertEquals(TrailerGrid, TrailerInformation,
				"trailer grid is wrong\nACTUAL:" + TrailerGrid + "\nEXPECT:" + TrailerInformation + "\n");

		// check pro grid
		LinkedHashSet<ArrayList<String>> GetProGrid = page.GetProListInInquiryScreen(page.TIQProGrid);
		LinkedHashSet<ArrayList<String>> ExpectedProGrid = DataForInQuiryScreen.GetProListInQuiry(SCAC, TrailerNB);
		SA.assertEquals(GetProGrid, ExpectedProGrid,
				"Pro grid is wrong \nActual :" + GetProGrid + "\nExpected :" + ExpectedProGrid + "\n");
		SA.assertAll();
	}

	@Test(priority = 5, dataProvider = "TrailerInquiry", dataProviderClass = DataForTrailerInquiryScreen.class)
	public void borTrailerWithPro(String SCAC, String TrailerNB, ArrayList<String> TrailerInformation, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.EnterTrailer(SCAC, TrailerNB);
		Date d = CommonFunction.gettime("UTC");

		// get hours
		long diff = d.getTime() - Mrst.getTime();
		long diffHours = diff / (60 * 60 * 1000);
		String Hrs = Long.toString(diffHours);
		TrailerInformation.add(Hrs);

		// check trailer grid
		String[] Trailerline1 = page.TIQtrailerGrid.getText().split("\\n");
		ArrayList<String> TrailerGrid = new ArrayList<String>(Arrays.asList(Trailerline1));
		SA.assertEquals(TrailerGrid, TrailerInformation,
				"trailer grid is wrong\nACTUAL:" + TrailerGrid + "\nEXPECT:" + TrailerInformation + "\n");

		// check pro grid
		LinkedHashSet<ArrayList<String>> GetProGrid = page.GetProListInInquiryScreen(page.TIQProGrid);
		LinkedHashSet<ArrayList<String>> ExpectedProGrid = DataForInQuiryScreen.GetProListInQuiry(SCAC, TrailerNB);
		SA.assertEquals(GetProGrid, ExpectedProGrid,
				"Pro grid is wrong \nActual :" + GetProGrid + "\nExpected :" + ExpectedProGrid + "\n");
		// Check pro hyperlink
		ArrayList<String> Prolist = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		String CurrentWindowHandle = driver.getWindowHandle();
		for (String Pro : Prolist) {
			page.TIQProGrid.findElement(By.linkText(CommonFunction.addHyphenToPro(Pro))).click();
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

	@Test(priority = 6, dataProvider = "TrailerInquiry", dataProviderClass = DataForTrailerInquiryScreen.class)
	public void mtyTrailerWithPro(String SCAC, String TrailerNB, ArrayList<String> TrailerInformation, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.EnterTrailer(SCAC, TrailerNB);
		Date d = CommonFunction.gettime("UTC");

		// get hours
		long diff = d.getTime() - Mrst.getTime();
		long diffHours = diff / (60 * 60 * 1000);
		String Hrs = Long.toString(diffHours);
		TrailerInformation.add(Hrs);

		// check trailer grid
		String[] Trailerline1 = page.TIQtrailerGrid.getText().split("\\n");
		ArrayList<String> TrailerGrid = new ArrayList<String>(Arrays.asList(Trailerline1));
		SA.assertEquals(TrailerGrid, TrailerInformation,
				"trailer grid is wrong\nACTUAL:" + TrailerGrid + "\nEXPECT:" + TrailerInformation + "\n");

		// check pro grid
		LinkedHashSet<ArrayList<String>> GetProGrid = page.GetProListInInquiryScreen(page.TIQProGrid);
		LinkedHashSet<ArrayList<String>> ExpectedProGrid = DataForInQuiryScreen.GetProListInQuiry(SCAC, TrailerNB);
		SA.assertEquals(GetProGrid, ExpectedProGrid,
				"Pro grid is wrong \nActual :" + GetProGrid + "\nExpected :" + ExpectedProGrid + "\n");

		// Check pro hyperlink
		ArrayList<String> Prolist = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		String CurrentWindowHandle = driver.getWindowHandle();
		for (String Pro : Prolist) {
			page.TIQProGrid.findElement(By.linkText(CommonFunction.addHyphenToPro(Pro))).click();
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

	@Test(priority = 7, dataProvider = "TrailerInquiry", dataProviderClass = DataForTrailerInquiryScreen.class)
	public void cpuTrailerWithPro(String SCAC, String TrailerNB, ArrayList<String> TrailerInformation, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.EnterTrailer(SCAC, TrailerNB);
		Date d = CommonFunction.gettime("UTC");

		// get hours
		long diff = d.getTime() - Mrst.getTime();
		long diffHours = diff / (60 * 60 * 1000);
		String Hrs = Long.toString(diffHours);
		TrailerInformation.add(Hrs);

		// check trailer grid
		String[] Trailerline1 = page.TIQtrailerGrid.getText().split("\\n");
		ArrayList<String> TrailerGrid = new ArrayList<String>(Arrays.asList(Trailerline1));
		SA.assertEquals(TrailerGrid, TrailerInformation,
				"trailer grid is wrong\nACTUAL:" + TrailerGrid + "\nEXPECT:" + TrailerInformation + "\n");

		// check pro grid
		LinkedHashSet<ArrayList<String>> GetProGrid = page.GetProListInInquiryScreen(page.TIQProGrid);
		LinkedHashSet<ArrayList<String>> ExpectedProGrid = DataForInQuiryScreen.GetProListInQuiry(SCAC, TrailerNB);
		SA.assertEquals(GetProGrid, ExpectedProGrid,
				"Pro grid is wrong \nActual :" + GetProGrid + "\nExpected :" + ExpectedProGrid + "\n");
		SA.assertAll();
	}

	@Test(priority = 8, dataProvider = "TrailerInquiry", dataProviderClass = DataForTrailerInquiryScreen.class)
	public void ofdTrailerWithPro(String SCAC, String TrailerNB, ArrayList<String> TrailerInformation, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.EnterTrailer(SCAC, TrailerNB);
		Date d = CommonFunction.gettime("UTC");

		// get hours
		long diff = d.getTime() - Mrst.getTime();
		long diffHours = diff / (60 * 60 * 1000);
		String Hrs = Long.toString(diffHours);
		TrailerInformation.add(Hrs);

		// check trailer grid
		String[] Trailerline1 = page.TIQtrailerGrid.getText().split("\\n");
		ArrayList<String> TrailerGrid = new ArrayList<String>(Arrays.asList(Trailerline1));
		SA.assertEquals(TrailerGrid, TrailerInformation,
				"trailer grid is wrong\nACTUAL:" + TrailerGrid + "\nEXPECT:" + TrailerInformation + "\n");

		// check pro grid
		LinkedHashSet<ArrayList<String>> GetProGrid = page.GetProListInInquiryScreen(page.TIQProGrid);
		LinkedHashSet<ArrayList<String>> ExpectedProGrid = DataForInQuiryScreen.GetProListInQuiry(SCAC, TrailerNB);
		SA.assertEquals(GetProGrid, ExpectedProGrid,
				"Pro grid is wrong \nActual :" + GetProGrid + "\nExpected :" + ExpectedProGrid + "\n");
		SA.assertAll();
	}

	@Test(priority = 9, dataProvider = "TrailerInquiry", dataProviderClass = DataForTrailerInquiryScreen.class)
	public void uadTrailerWithPro(String SCAC, String TrailerNB, ArrayList<String> TrailerInformation, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.EnterTrailer(SCAC, TrailerNB);
		Date d = CommonFunction.gettime("UTC");

		// get hours
		long diff = d.getTime() - Mrst.getTime();
		long diffHours = diff / (60 * 60 * 1000);
		String Hrs = Long.toString(diffHours);
		TrailerInformation.add(Hrs);

		// check trailer grid
		String[] Trailerline1 = page.TIQtrailerGrid.getText().split("\\n");
		ArrayList<String> TrailerGrid = new ArrayList<String>(Arrays.asList(Trailerline1));
		SA.assertEquals(TrailerGrid, TrailerInformation,
				"trailer grid is wrong\nACTUAL:" + TrailerGrid + "\nEXPECT:" + TrailerInformation + "\n");

		// check pro grid
		LinkedHashSet<ArrayList<String>> GetProGrid = page.GetProListInInquiryScreen(page.TIQProGrid);
		LinkedHashSet<ArrayList<String>> ExpectedProGrid = DataForInQuiryScreen.GetProListInQuiry(SCAC, TrailerNB);
		SA.assertEquals(GetProGrid, ExpectedProGrid,
				"Pro grid is wrong \nActual :" + GetProGrid + "\nExpected :" + ExpectedProGrid + "\n");
		SA.assertAll();
	}

	@Test(priority = 10, dataProvider = "TrailerInquiry", dataProviderClass = DataForTrailerInquiryScreen.class)
	public void arrTrailerWithPro(String SCAC, String TrailerNB, ArrayList<String> TrailerInformation, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.EnterTrailer(SCAC, TrailerNB);
		Date d = CommonFunction.gettime("UTC");

		// get hours
		long diff = d.getTime() - Mrst.getTime();
		long diffHours = diff / (60 * 60 * 1000);
		String Hrs = Long.toString(diffHours);
		TrailerInformation.add(Hrs);

		// check trailer grid
		String[] Trailerline1 = page.TIQtrailerGrid.getText().split("\\n");
		ArrayList<String> TrailerGrid = new ArrayList<String>(Arrays.asList(Trailerline1));
		SA.assertEquals(TrailerGrid, TrailerInformation,
				"trailer grid is wrong\nACTUAL:" + TrailerGrid + "\nEXPECT:" + TrailerInformation + "\n");

		// check pro grid
		LinkedHashSet<ArrayList<String>> GetProGrid = page.GetProListInInquiryScreen(page.TIQProGrid);
		LinkedHashSet<ArrayList<String>> ExpectedProGrid = DataForInQuiryScreen.GetProListInQuiry(SCAC, TrailerNB);
		SA.assertEquals(GetProGrid, ExpectedProGrid,
				"Pro grid is wrong \nActual :" + GetProGrid + "\nExpected :" + ExpectedProGrid + "\n");
		SA.assertAll();
	}

	@Test(priority = 11, dataProvider = "TrailerInquiry", dataProviderClass = DataForTrailerInquiryScreen.class)
	public void arvTrailerWithPro(String SCAC, String TrailerNB, ArrayList<String> TrailerInformation, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.EnterTrailer(SCAC, TrailerNB);
		Date d = CommonFunction.gettime("UTC");

		// get hours
		long diff = d.getTime() - Mrst.getTime();
		long diffHours = diff / (60 * 60 * 1000);
		String Hrs = Long.toString(diffHours);
		TrailerInformation.add(Hrs);

		// check trailer grid
		String[] Trailerline1 = page.TIQtrailerGrid.getText().split("\\n");
		ArrayList<String> TrailerGrid = new ArrayList<String>(Arrays.asList(Trailerline1));
		SA.assertEquals(TrailerGrid, TrailerInformation,
				"trailer grid is wrong\nACTUAL:" + TrailerGrid + "\nEXPECT:" + TrailerInformation + "\n");

		// check pro grid
		LinkedHashSet<ArrayList<String>> GetProGrid = page.GetProListInInquiryScreen(page.TIQProGrid);
		LinkedHashSet<ArrayList<String>> ExpectedProGrid = DataForInQuiryScreen.GetProListInQuiry(SCAC, TrailerNB);
		SA.assertEquals(GetProGrid, ExpectedProGrid,
				"Pro grid is wrong \nActual :" + GetProGrid + "\nExpected :" + ExpectedProGrid + "\n");
		SA.assertAll();
	}

	@Test(priority = 12, dataProvider = "TrailerInquiry", dataProviderClass = DataForTrailerInquiryScreen.class)
	public void sptTrailerWithPro(String SCAC, String TrailerNB, ArrayList<String> TrailerInformation, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.EnterTrailer(SCAC, TrailerNB);
		Date d = CommonFunction.gettime("UTC");

		// get hours
		long diff = d.getTime() - Mrst.getTime();
		long diffHours = diff / (60 * 60 * 1000);
		String Hrs = Long.toString(diffHours);
		TrailerInformation.add(Hrs);

		// check trailer grid
		String[] Trailerline1 = page.TIQtrailerGrid.getText().split("\\n");
		ArrayList<String> TrailerGrid = new ArrayList<String>(Arrays.asList(Trailerline1));
		SA.assertEquals(TrailerGrid, TrailerInformation,
				"trailer grid is wrong\nACTUAL:" + TrailerGrid + "\nEXPECT:" + TrailerInformation + "\n");

		// check pro grid
		LinkedHashSet<ArrayList<String>> GetProGrid = page.GetProListInInquiryScreen(page.TIQProGrid);
		LinkedHashSet<ArrayList<String>> ExpectedProGrid = DataForInQuiryScreen.GetProListInQuiry(SCAC, TrailerNB);
		SA.assertEquals(GetProGrid, ExpectedProGrid,
				"Pro grid is wrong \nActual :" + GetProGrid + "\nExpected :" + ExpectedProGrid + "\n");
		SA.assertAll();
	}

	@Test(priority = 13, dataProvider = "TrailerInquiry", dataProviderClass = DataForTrailerInquiryScreen.class)
	public void enrTrailerWithPro(String SCAC, String TrailerNB, ArrayList<String> TrailerInformation, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.EnterTrailer(SCAC, TrailerNB);
		Date d = CommonFunction.gettime("UTC");

		// get hours HRS for enr just show blank

		if (!TrailerInformation.get(2).equalsIgnoreCase("enr")) {
			long diff = d.getTime() - Mrst.getTime();
			long diffHours = diff / (60 * 60 * 1000);
			String Hrs = Long.toString(diffHours);
			TrailerInformation.add(Hrs);
		}
		// check trailer grid (FOR ENR STATUS TIME STILL USE STATUSING LOCATION,
		// TERMINAL USE DISPATCH DESTINATION)
		String[] Trailerline1 = page.TIQtrailerGrid.getText().split("\\n");
		ArrayList<String> TrailerGrid = new ArrayList<String>(Arrays.asList(Trailerline1));
		SA.assertEquals(TrailerGrid, TrailerInformation,
				"trailer grid is wrong\nACTUAL:" + TrailerGrid + "\nEXPECT:" + TrailerInformation + "\n");

		// check pro grid
		LinkedHashSet<ArrayList<String>> GetProGrid = page.GetProListInInquiryScreen(page.TIQProGrid);
		LinkedHashSet<ArrayList<String>> ExpectedProGrid = DataForInQuiryScreen.GetProListInQuiry(SCAC, TrailerNB);
		SA.assertEquals(GetProGrid, ExpectedProGrid,
				"Pro grid is wrong \nActual :" + GetProGrid + "\nExpected :" + ExpectedProGrid + "\n");

		// Check pro hyperlink
		ArrayList<String> Prolist = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		String CurrentWindowHandle = driver.getWindowHandle();
		for (String Pro : Prolist) {
			page.TIQProGrid.findElement(By.linkText(CommonFunction.addHyphenToPro(Pro))).click();
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

	@Test(priority = 14, dataProvider = "TrailerInquiry", dataProviderClass = DataForTrailerInquiryScreen.class)
	public void storTrailerWithoutPro(String SCAC, String TrailerNB, ArrayList<String> TrailerInformation, Date Mrst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.EnterTrailer(SCAC, TrailerNB);
		Date d = CommonFunction.gettime("UTC");

		// get hours
		long diff = d.getTime() - Mrst.getTime();
		long diffHours = diff / (60 * 60 * 1000);
		String Hrs = Long.toString(diffHours);
		TrailerInformation.add(Hrs);

		// check trailer grid
		String[] Trailerline1 = page.TIQtrailerGrid.getText().split("\\n");
		ArrayList<String> TrailerGrid = new ArrayList<String>(Arrays.asList(Trailerline1));
		SA.assertEquals(TrailerGrid, TrailerInformation,
				"trailer grid is wrong\nACTUAL:" + TrailerGrid + "\nEXPECT:" + TrailerInformation + "\n");

		// check pro grid
		LinkedHashSet<ArrayList<String>> GetProGrid = page.GetProListInInquiryScreen(page.TIQProGrid);
		LinkedHashSet<ArrayList<String>> ExpectedProGrid = DataForInQuiryScreen.GetProListInQuiry(SCAC, TrailerNB);
		SA.assertEquals(GetProGrid, ExpectedProGrid,
				"Pro grid is wrong \nActual :" + GetProGrid + "\nExpected :" + ExpectedProGrid + "\n");
		SA.assertAll();
	}

	@AfterClass
	public void Close() {
		driver.close();
	}
}
