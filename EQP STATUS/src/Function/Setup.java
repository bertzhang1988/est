package Function;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class Setup {

	protected WebDriver driver;
	protected static ConfigRd conf;

	@BeforeClass
	@Parameters({ "browser", "env" })
	public void SetUp(@Optional("chrome") String browser, @Optional("sit") String environment) {
		conf = new ConfigRd(environment);
		if (browser.equalsIgnoreCase("chrome")) {
			System.setProperty("webdriver.chrome.driver", conf.GetChromePath());
			driver = new ChromeDriver();
		} else if (browser.equalsIgnoreCase("ie")) {
			System.setProperty("webdriver.ie.driver", conf.GetIEPath());
			driver = new InternetExplorerDriver();
		} else if (browser.equalsIgnoreCase("hl")) {
			File file = new File(conf.GetPhantomJSDriverPath());
			System.setProperty("phantomjs.binary.path", file.getAbsolutePath());
			driver = new PhantomJSDriver();
		}
		driver.get(conf.GetURL());
	}

	@AfterClass
	public void CloseBrowser() {
		driver.close();
	}

}
