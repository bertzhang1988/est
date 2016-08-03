package TestCase.LDDscreen;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Data.DataForUS1212AND1211;
import Function.CommonFunction;
import Function.ConfigRd;
import Page.EqpStatusPageS;

public class US1212and1211andUS1222SummarizeSeriviceAndFlagOnTrailer {
	private WebDriver driver;
	EqpStatusPageS page;

	@Test(priority = 2, dataProvider = "12.12", dataProviderClass = DataForUS1212AND1211.class)
	public void VerifySmmarizeServiceOnTrailer(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, ClassNotFoundException, SQLException, InterruptedException {
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		String Shipflag = CommonFunction.GetFlag(SCAC, TrailerNB, "flag");
		String Serviceflag = CommonFunction.GetFlag(SCAC, TrailerNB, "SERV");
		String getShipflag = page.ShipmentFlag.getText();
		String getServiceflag = page.ServiceFlag.getText();
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.textToBePresentInElement(page.ShipmentFlag, Shipflag));
		(new WebDriverWait(driver, 10))
				.until(ExpectedConditions.textToBePresentInElement(page.ServiceFlag, Serviceflag));
		// String getShipflag=page.ShipmentFlag.getText();
		// String getServiceflag=page.ServiceFlag.getText();
		System.out.println(SCAC + " " + TrailerNB + ": " + getShipflag + " " + getServiceflag);
		System.out.println(SCAC + " " + TrailerNB + ": " + Shipflag + " " + Serviceflag);
	}

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

	@Test(priority = 1)
	public void VerifyTheLegendDisplay() throws AWTException, InterruptedException {
		page.SetLocation("326");
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.elementToBeClickable(page.ServiceFlagExclamation));
		page.ServiceFlagExclamation.click();
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(page.ServiceFlagLegend));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.textToBePresentInElement(page.ServiceFlagLegend,
				"E = ETME\nS = STME\nH = Corridor Hub\nV = Velocity\nX = Exclusive Use"));
		System.out.println(page.ServiceFlagLegend.getText());

		(new WebDriverWait(driver, 20)).until(ExpectedConditions.elementToBeClickable(page.ShipmentFlagExclamation));
		page.ShipmentFlagExclamation.click();
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(page.ShipmentFlagLegend));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.textToBePresentInElement(page.ShipmentFlagLegend,
				"! = Poison\n@ = Food\n% = Hazardous\nF = Freezable"));
		System.out.println(page.ShipmentFlagLegend.getText());
	}

	@AfterTest
	public void TearDown() {
		driver.quit();
	}

}
