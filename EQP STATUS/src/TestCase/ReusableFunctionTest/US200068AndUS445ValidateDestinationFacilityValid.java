package TestCase.ReusableFunctionTest;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Function.ConfigRd;
import Page.EqpStatusPageS;

public class US200068AndUS445ValidateDestinationFacilityValid {
	private WebDriver driver;
	private EqpStatusPageS page;
	private Actions builder;

	@BeforeClass
	@Parameters({ "browser", "status" })
	public void SetUp(@Optional("chrome") String browser, @Optional("ldd") String status)
			throws AWTException, InterruptedException {
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
		// driver.manage().window().maximize();
		page.SetStatus(status);
		builder = new Actions(driver);

	}

	@Test(priority = 1, dataProvider = "2000.682", dataProviderClass = DataForReusableFunction.class)
	public void EnterTrailerInLdgNoShipments(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
	}

	@Test(priority = 3, dataProvider = "2000.68", dataProviderClass = DataForReusableFunction.class)
	public void VerifyInvalidDestination(String Destination) throws AWTException {
		page.DestinationField.clear();
		page.DestinationField.sendKeys(Destination);
		// page.Title.click();
		builder.sendKeys(Keys.TAB).build().perform();
		// r.keyPress(KeyEvent.VK_TAB);
		// r.keyRelease(KeyEvent.VK_TAB);
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Invalid Destination, please enter valid Destination."));
	}

	@Test(priority = 2, dataProvider = "2000.68", dataProviderClass = DataForReusableFunction.class)
	public void VerifyValidDestination(String Destination) throws AWTException {
		page.DestinationField.clear();
		page.DestinationField.sendKeys(Destination);
		// page.Title.click();
		builder.sendKeys(Keys.TAB).build().perform();
		// r.keyPress(KeyEvent.VK_TAB);
		// r.keyRelease(KeyEvent.VK_TAB);
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.invisibilityOfElementLocated(
				By.xpath("//*[contains(text(), 'Invalid Destination, please enter valid Destination.')]")));
		Assert.assertFalse(
				page.CheckErrorAndWarningMessageDisplay("Invalid Destination, please enter valid Destination."));
	}

	@AfterClass
	public void TearDown() {
		driver.close();

	}

}
