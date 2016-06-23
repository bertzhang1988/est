package TestCase.SitTest;

import java.awt.AWTException;
import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Function.ConfigRd;
import Page.EqpStatusPageS;

public class US2000142DisplayErrorMessageForToolAccessRejection {

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

	}

	@Test(priority = 1)
	public void CheckStatusTrailerButton() {
		(new WebDriverWait(driver, 150)).until(ExpectedConditions.visibilityOf(page.StatusTrailerButton));
		page.StatusTrailerButton.click();
		(new WebDriverWait(driver, 150)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"You do not have authority to update trailers for this terminal"));
	}

	@Test(priority = 2)
	public void CheckLoadToEnrButton() {
		(new WebDriverWait(driver, 150)).until(ExpectedConditions.visibilityOf(page.LOADTOENRbutton));
		page.StatusTrailerButton.click();
		(new WebDriverWait(driver, 150)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"You do not have authority to update trailers for this terminal"));
	}

	@Test(priority = 3)
	public void CheckTrailerByTerminalButton() {
		(new WebDriverWait(driver, 150)).until(ExpectedConditions.visibilityOf(page.TrailerByTerminalButton));
		page.StatusTrailerButton.click();
		(new WebDriverWait(driver, 150)).until(ExpectedConditions.visibilityOf(page.IQTerminalInput));
	}

	@AfterClass
	public void Close() {
		driver.close();
	}
}
