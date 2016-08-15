package test.trial;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Function.ConfigRd;
import Page.EqpStatusPageS;

public class TRIALAUTOIT {
	private WebDriver driver;
	private EqpStatusPageS page;

	@BeforeMethod
	@Parameters({ "browser" })
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
		driver.get(Conf.GetURL());
		driver.manage().window().maximize();
		page.SetTrailerInquiryScreen();

	}

	@Test
	public void f() throws InterruptedException, IOException {
		page.IQTerminalInput.clear();
		page.IQTerminalInput.sendKeys("326");
		page.SearchButton.click();
		Thread.sleep(500);
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(page.IQStatusList));
		driver.findElement(By.xpath(".//button[@label='Print']")).click();
		Thread.sleep(2500);
		Runtime.getRuntime().exec("C:\\Users\\uyr27b0\\Desktop\\selenium\\AUTOIT CODE\\CLICK PRINT IE.exe");

	}
}
