package TestCase.SitTest;

import java.awt.AWTException;
import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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

import Data.DataForUS200001AndUS200002AndUS200041;
import Function.ConfigRd;
import Page.EqpStatusPageS;

public class US200001AndUS200002ValidateTrailerAndUS200041ValidateTerminal {
	private WebDriver driver;
	private EqpStatusPageS page;
	private Actions builder;

	@BeforeClass
	@Parameters({ "browser", "status" })
	public void SetUp(@Optional("chrome") String browser, @Optional("ldg") String status)
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
		driver.get(page.sit1);
		driver.manage().window().maximize();
		builder = new Actions(driver);
		page.SetStatus(status);
		builder = new Actions(driver);
	}

	@Test(priority = 2, dataProvider = "2000.41", dataProviderClass = DataForUS200001AndUS200002AndUS200041.class)
	public void VerifyInvalidTerminal(String terminalcd) throws AWTException, InterruptedException {
		page.SetLocation(terminalcd);
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"The terminal number is invalid. Please enter another terminal number."));

	}

	@Test(priority = 1, dataProvider = "2000.41", dataProviderClass = DataForUS200001AndUS200002AndUS200041.class)
	public void VerifyValidTerminal(String terminalcd) throws AWTException, InterruptedException {
		page.SetLocation(terminalcd);
		(new WebDriverWait(driver, 10))
				.until(ExpectedConditions.invisibilityOfElementWithText(By.xpath("html/body/div[1]/div"),
						"The terminal number is invalid. Please enter another terminal number."));
		Thread.sleep(500);
	}

	@Test(priority = 3, dataProvider = "2000.02", dataProviderClass = DataForUS200001AndUS200002AndUS200041.class)
	public void VerifyMaintenanceTrailer(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException {
		page.SetLocation(terminalcd);
		if (page.TrailerField.isDisplayed()) {
			page.TrailerField.click();
		}
		// this.TrailerInputField.click();
		page.TrailerInputField.clear();
		page.TrailerInputField.sendKeys(TrailerNB);
		builder.sendKeys(Keys.TAB).build().perform();
		String SCACTrailer = page.SCACTrailer(SCAC, TrailerNB);
		try {
			(new WebDriverWait(driver, 3))
					.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(SCACTrailer)));
			driver.findElement(By.linkText(SCACTrailer)).click();
		} catch (Exception e) {
		}
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"Trailer " + SCAC + "-" + TrailerNB + " availability is maintenance."));
		// (new WebDriverWait(driver,
		// 5)).until(ExpectedConditions.visibilityOf(page.ErrorAndWarning("Trailer
		// "+SCAC+"-"+TrailerNB+" availability is maintenance.")));
		Thread.sleep(500);
	}

	@Test(priority = 4, dataProvider = "2000.01retired", dataProviderClass = DataForUS200001AndUS200002AndUS200041.class)
	public void VerifyRetiredTrailer(String TrailerNB) throws AWTException, InterruptedException {
		if (page.TrailerField.isDisplayed()) {
			page.TrailerField.click();
		}
		page.TrailerInputField.clear();
		page.TrailerInputField.sendKeys(TrailerNB);
		builder.sendKeys(Keys.TAB).build().perform();
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		(new WebDriverWait(driver, 2)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"The trailer number you have entered is invalid. Please enter another trailer number."));
		// (new WebDriverWait(driver,
		// 5)).until(ExpectedConditions.visibilityOf(page.ErrorAndWarning("The
		// trailer number you have entered is invalid. Please enter another
		// trailer number.")));
		Thread.sleep(500);
	}

	@Test(priority = 5, dataProvider = "2000.01NotDb", dataProviderClass = DataForUS200001AndUS200002AndUS200041.class)
	public void VerifyNotExistTrailer(String TrailerNB) throws AWTException, InterruptedException {
		if (page.TrailerField.isDisplayed()) {
			page.TrailerField.click();
		}
		page.TrailerInputField.clear();
		page.TrailerInputField.sendKeys(TrailerNB);
		builder.sendKeys(Keys.TAB).build().perform();
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		// (new WebDriverWait(driver,
		// 2)).until(ExpectedConditions.textToBePresentInElement(page.ErrorMessage,"The
		// trailer number you have entered is not in the database. Please enter
		// another trailer number."));
		// (new WebDriverWait(driver,
		// 2)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),
		// 'The trailer number you have entered is not in the database. Please
		// enter another trailer number.')]")));
		// (new WebDriverWait(driver,
		// 5)).until(ExpectedConditions.visibilityOf(page.ErrorAndWarning("The
		// trailer number you have entered is invalid. Please enter another
		// trailer number.")));
		(new WebDriverWait(driver, 5)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"The trailer number you have entered is invalid. Please enter another trailer number."));
		Thread.sleep(500);
	}

	@AfterClass
	public void TearDown() {
		driver.close();
	}

}
