package TestCase.LDDscreen;

import java.awt.AWTException;
import java.io.File;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Data.DataForUS1205;
import Function.CommonFunction;
import Function.ConfigRd;
import Page.EqpStatusPageS;

public class US1205CheckCubeAndSeal {
	private WebDriver driver;
	private EqpStatusPageS page;
	private Actions builder;

	@Parameters({ "browser" })
	@BeforeClass
	public void SetUp(@Optional("ie") String browser) throws AWTException, InterruptedException {
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
		builder = new Actions(driver);
		driver.get(Conf.GetURL());
		driver.manage().window().maximize();
		page.SetStatus("ldd");

	}

	@Test(priority = 1, dataProvider = "12.05", dataProviderClass = DataForUS1205.class)
	public void VerifyCubePatternCheck(String terminal, String scac, String trailernb)
			throws InterruptedException, AWTException {
		page.SetLocation(terminal);
		page.EnterTrailer(scac, trailernb);
		String[] Cube = { "00011", "1 1", "00", "   1", "33", "88", "100", "1030", "7000", "abc" };
		for (int i = 0; i < Cube.length; i++) {
			String cube = Cube[i];
			page.SetCube(cube);
			Thread.sleep(500);
			int result = CommonFunction.CheckCubePattern(cube);
			if (result == 2) {
				(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(
						By.xpath("//*[contains(text(), 'Invalid Cube. Cube must be between 1 and 100.')]")));
			} else {
				(new WebDriverWait(driver, 10)).until(ExpectedConditions.invisibilityOfElementLocated(
						By.xpath("//*[contains(text(), 'Invalid Cube. Cube must be between 1 and 100.')]")));
			}
			page.CubeField.clear();
		}

	}

	// @Test(priority=3,dataProvider =
	// "12.05",dataProviderClass=DataForUS1205.class)
	public void VerifyMannuallyInputShipmentCount(String terminal, String scac, String trailernb)
			throws InterruptedException, AWTException {
		page.SetLocation(terminal);
		page.EnterTrailer(scac, trailernb);
		String[] Count = { "0", "155", "abc", " 3 4 5", "3 3 3 3", "1030", "7000", "abcdef" };
		for (int i = 0; i < Count.length; i++) {
			String countnum = Count[i].replace(" ", "").trim();
			page.ShipmentCount2.click();
			page.ShipmentCount2.clear();
			page.ShipmentCount2.sendKeys(Count[i]);
			builder.sendKeys(Keys.TAB).build().perform();
			Thread.sleep(500);
			String countpattern = "[\\d]+";
			if (!countnum.matches(countpattern) || countnum.equalsIgnoreCase("0")) {
				(new WebDriverWait(driver, 10))
						.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField, ""));
			} else {
				Assert.assertEquals("disabled", page.ShipmentCount2.getAttribute("disabled"));
				break;
			}

		}
	}

	@Test(priority = 2, dataProvider = "12.05", dataProviderClass = DataForUS1205.class)
	public void VerifySealPatternCheck(String terminal, String scac, String trailernb)
			throws InterruptedException, AWTException {
		page.SetLocation(terminal);
		page.EnterTrailer(scac, trailernb);
		String[] Seal = { "0", "155", "abc", " 3 4 5", "3 3 3 3", "1030", "7000", "abcdef" };
		for (int i = 0; i < Seal.length; i++) {
			String seal = Seal[i];
			String sealnum = seal.replace(" ", "").trim();
			page.SetSealLDD(seal);
			Thread.sleep(500);
			int result = sealnum.length();
			if (result < 4) {
				(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
						"//*[contains(text(), 'Invalid Seal Number. The Seal Number must be at least 4 characters.')]")));
			} else {
				Assert.assertFalse(page.CheckErrorAndWarningMessageDisplay(
						"Invalid Seal Number. The Seal Number must be at least 4 characters."));
			}
		}
	}

	@AfterTest
	public void TearDown() {
		driver.quit();
	}

}