package TestCase.CLTGtesting;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;

import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Function.CommonFunction;
import Function.ConfigRd;
import Page.EqpStatusPageS;

public class CLTGScreenTesting {

	private WebDriver driver;
	private EqpStatusPageS page;

	@BeforeTest
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
		page.SetStatus("cltg");
	}

	@Test(priority = 1, dataProvider = "cltg with pro", dataProviderClass = DataForCLTGScreenTesting.class)
	public void PrepopValuesLoadingTrailerAndSummarizeBillsWeight(String terminalcd, String SCAC, String TrailerNB,
			String Desti, String Cube, String AmountPro, String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// check date&time field should be equipment_status_ts at statusing
		// location time zone
		Date LocalTime = CommonFunction.getLocalTime(terminalcd, MRSts);
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
		cal.setTime(LocalTime);
		int hourOfDay = cal.get(Calendar.HOUR_OF_DAY); // 24 hour clock
		String hour = String.format("%02d", hourOfDay);
		int minute = cal.get(Calendar.MINUTE);
		String Minute = String.format("%02d", minute);
		String DATE = dateFormat.format(cal.getTime());
		SA.assertEquals(page.DateInput.getAttribute("value"), DATE);
		SA.assertEquals(page.HourInput.getAttribute("value"), hour);
		SA.assertEquals(page.MinuteInput.getAttribute("value"), Minute);

		// check pro grid
		int line = page.ProListSecondForm.findElements(By.xpath("div")).size();
		// Set<ArrayList<String>> ProInfo= new HashSet<ArrayList<String>>(); //
		// dont sort the pro list
		LinkedHashSet<ArrayList<String>> ProInfo = new LinkedHashSet<ArrayList<String>>(); // sort
																							// the
																							// prolist
		for (int j = 1; j <= line; j++) {
			String[] Proline1 = ArrayUtils
					.remove(page.ProListSecondForm.findElement(By.xpath("div[" + j + "]")).getText().split("\\n"), 0);
			// String[] Proline1=
			// page.ProListSecondForm.findElement(By.xpath("div["+i+"]")).getText().split("\\n");
			ArrayList<String> e1 = new ArrayList<String>(Arrays.asList(Proline1));
			ProInfo.add(e1);
		}
		if (line >= 31) {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			int additional = 31;
			do {
				jse.executeScript("arguments[0].scrollIntoView(true);",
						page.ProListSecondForm.findElement(By.xpath("div[" + additional + "]")));
				additional = page.ProListSecondForm.findElements(By.xpath("div")).size();
				for (int j = 1; j <= additional; j++) {
					String[] Proline1 = ArrayUtils.remove(
							page.ProListSecondForm.findElement(By.xpath("div[" + j + "]")).getText().split("\\n"), 0);
					ArrayList<String> e1 = new ArrayList<String>(Arrays.asList(Proline1));
					ProInfo.add(e1);
				}
			}

			while (additional > 31);
			int Rest = page.ProListSecondForm.findElements(By.xpath("div")).size();
			for (int j = 1; j <= Rest; j++) {
				String[] Proline1 = ArrayUtils.remove(
						page.ProListSecondForm.findElement(By.xpath("div[" + j + "]")).getText().split("\\n"), 0);
				ArrayList<String> e1 = new ArrayList<String>(Arrays.asList(Proline1));
				ProInfo.add(e1);
			}
		}
		SA.assertEquals(ProInfo, DataForCLTGScreenTesting.GetProList(SCAC, TrailerNB));

		SA.assertAll();
	}

	// @Test(priority=2,dataProvider =
	// "4.61And4.65And4.57",dataProviderClass=DataForUS461AndUS465AndUS457.class)
	public void PrepopValuesLoadingTrailerAndSummarizeBillsWeight2(String terminalcd, String SCAC, String TrailerNB,
			String Desti, String Cube, String AmountPro, String AmountWeight, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		// (new WebDriverWait(driver,
		// 10)).until(ExpectedConditions.invisibilityOfElementLocated(page.ErrorAndWarningField));
	}

	@AfterTest
	public void TearDown() {
		driver.quit();
	}
}
