package TestCase.LDDscreen;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;

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
import Function.DataCommon;
import Page.EqpStatusPageS;

public class US1204And1217ShipmentDetailsOnLDDAnd1202DisplayProsLoadedOnTrailerToBeClosed {
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
		page.SetStatus("ldd");
	}

	@Test(priority = 1, dataProvider = "lddscreen2", dataProviderClass = DataForUSLDDLifeTest.class)
	public void VerifyShipmentDetailsOnLDD(String terminalcd, String SCAC, String TrailerNB, String Desti,
			String AmountPro, String AmountWeight, String Cube, String Seal, String headloadDestination,
			String headloadCube, Date MReqpst, String CurrentStatus, String flag, String serv)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		Date LocalTime = null;
		page.EnterTrailer(SCAC, TrailerNB);

		SA.assertEquals(page.ShipmentCountLdd.getAttribute("value").replaceAll("_", ""), AmountPro,
				"ship count is wrong");
		SA.assertEquals(page.ShipmentWeightLdd.getAttribute("value").replaceAll("_", ""), AmountWeight,
				"ship weight is wrong");
		SA.assertEquals(page.DestinationField.getAttribute("value").replaceAll("_", ""), Desti, "destination is wrong");
		SA.assertEquals(page.CubeField.getAttribute("value"), Cube, "cube is wrong");
		SA.assertEquals(page.SealField.getAttribute("value").replaceAll("_", ""), Seal, "seal is wrong");
		SA.assertEquals(page.HeadloadDestination.getAttribute("value"), headloadDestination, "ship count is wrong");
		SA.assertEquals(page.HeadloadCube.getAttribute("value"), headloadCube, "ship weight is wrong");
		SA.assertEquals(page.ShipmentFlag.getText(), flag, " flag is wrong");
		SA.assertEquals(page.ServiceFlag.getText(), serv, "serv is wrong");

		// check date&time field 1. ldd use equipment_status_ts 2.not ldd, a.
		// eqpst>current-time use eqpst minute+1 b. eqpst<current time use
		// current time
		if (CurrentStatus.equalsIgnoreCase("LDD")) {
			LocalTime = CommonFunction.getLocalTime(terminalcd, MReqpst);
		} else {
			if (MReqpst.before(CurrentTime)) {
				LocalTime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
			} else if (MReqpst.after(CurrentTime)) {
				LocalTime = CommonFunction.getLocalTime(terminalcd, MReqpst);
			}
		}

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
		cal.setTime(LocalTime);
		int hourOfDay = cal.get(Calendar.HOUR_OF_DAY); // 24 hour clock
		String hour = String.format("%02d", hourOfDay);
		int minute = cal.get(Calendar.MINUTE);
		String Minute = String.format("%02d", minute);
		String MinutePlusOne = String.format("%02d", minute + 1);
		String DATE = dateFormat.format(cal.getTime());

		SA.assertEquals(page.DateInput.getAttribute("value"), DATE);
		SA.assertEquals(page.HourInput.getAttribute("value"), hour);
		if (!CurrentStatus.equalsIgnoreCase("LDD") && MReqpst.after(CurrentTime)) {
			SA.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne);
		} else {
			SA.assertEquals(page.MinuteInput.getAttribute("value"), Minute);
		}

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListSecondForm);
		SA.assertEquals(ProInfo, DataCommon.GetProList(SCAC, TrailerNB), "prolist information is wrong");
		SA.assertAll();
	}

	@AfterTest
	public void TearDown() {
		driver.quit();
	}

}
