package TestCase.CLtesting;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Function.CommonFunction;
import Function.ConfigRd;
import Function.DataCommon;
import Page.EqpStatusPageS;

public class US458CheckShipmentAlreadyLoadedOnTrailer {
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
		page.SetStatus("CL");
	}

	@Test(priority = 1, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class)
	public void CheckShipmentAlreadyLoadedOnCLHasPro(String terminalcd, String SCAC, String TrailerNB, String CityR,
			String CityRT, String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);
		// update cityRoute
		page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		page.SetPlanDate(Localtime, 2);

		// select city route type
		page.SetCityRouteType("trap");

		// set date&time
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Iterator<String> data = DataCommon.GetProOnTrailer(SCAC, TrailerNB).iterator();
		while (data.hasNext()) {
			page.RemoveProButton.click();
			String pro = data.next();
			page.EnterPro(pro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			// page.AddProCheckBoxList.findElement(By.xpath("div["+NEW+"]/div/div/div/div")).click();
			page.SubmitButton1.click();
			(new WebDriverWait(driver, 20)).until(ExpectedConditions.textToBePresentInElement(
					page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div")),
					"This PRO is already on trailer."));
			(new WebDriverWait(driver, 20))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			(new WebDriverWait(driver, 20)).until(ExpectedConditions.elementToBeClickable(page.RemoveProButton));
			page.CheckAllAddProButton.click();
			page.RemoveProButton.click();
		}
	}

	@AfterClass
	public void TearDown() {
		driver.quit();
	}
}
