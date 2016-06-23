package TestCase.SitTest;

import java.awt.AWTException;
import java.io.File;
import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Function.ConfigRd;
import Page.EqpStatusPageS;

public class US2000135DisplayFrequentAskQuestion {
	private WebDriver driver;
	private EqpStatusPageS page;

	@BeforeClass
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
	public void VerifyTitleOfAPP() {
		SoftAssert SA = new SoftAssert();
		String Title = driver.getTitle();
		SA.assertEquals(Title, "EST - Equipment Status & Tracking", "THE TITLE OF THE TAB IS WRONG, FIND " + Title);
		SA.assertAll();
	}

	@Test(priority = 3)
	public void VerifyFQALink() throws InterruptedException {
		SoftAssert SA = new SoftAssert();
		String CurrentWindowHandle = driver.getWindowHandle();
		Thread.sleep(2000);
		page.FAQ.click();
		Set<String> WindowHandles = driver.getWindowHandles();
		for (String windowHandle : WindowHandles) {
			if (!windowHandle.equalsIgnoreCase(CurrentWindowHandle)) {
				driver.switchTo().window(windowHandle);
				String LINKofFAQ = driver.getCurrentUrl();
				SA.assertEquals(LINKofFAQ, "http://yrcops/Docs/Operations/FIE-347.pdf",
						"THE FAQ LINK IS WRONG, FIND " + LINKofFAQ);
				driver.close();
			}
		}

		driver.switchTo().window(CurrentWindowHandle);
		SA.assertAll();
	}

	@Test(priority = 2)
	public void VerifyClickLogo() throws InterruptedException {
		SoftAssert SA = new SoftAssert();
		page.YrcLogoIcon.click();
		Thread.sleep(2000);
		String LINKAfterClickIcon = driver.getCurrentUrl();
		SA.assertEquals(LINKAfterClickIcon, "http://intranet.yrcweb.com/",
				"THE FAQ LINK IS WRONG, FIND " + LINKAfterClickIcon);
		driver.navigate().back();
		SA.assertAll();
	}

}
