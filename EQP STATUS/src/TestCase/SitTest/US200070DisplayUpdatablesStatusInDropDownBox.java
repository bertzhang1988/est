package TestCase.SitTest;

import java.awt.AWTException;
import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Function.ConfigRd;
import Page.EqpStatusPageS;

public class US200070DisplayUpdatablesStatusInDropDownBox {
	private WebDriver driver = null;
	EqpStatusPageS page;

	@Test(dataProvider = "status")
	public void VerifyStatusClickable(String status) throws InterruptedException {
		page.SetStatus(status);

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
	}

	@AfterTest
	public void TearDown() {
		driver.quit();
	}

	@AfterMethod
	public void waitwhile() throws InterruptedException {
		Thread.sleep(2000);
	}

	@DataProvider(name = "status")
	public Object[][] StatusData() {
		return new Object[][] { { "ldd" }, { "ldg" }, { "uad" }, { "bor" }, { "cl" }, { "mty" }, { "cpu" }, { "cltg" },
				{ "ofd" } };
	}

}
