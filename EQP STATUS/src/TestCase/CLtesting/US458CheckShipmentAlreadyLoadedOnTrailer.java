package TestCase.CLtesting;

import java.awt.AWTException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import Function.CommonFunction;
import Function.DataCommon;
import Function.SetupBrowser;
import Page.EqpStatusPageS;

public class US458CheckShipmentAlreadyLoadedOnTrailer extends SetupBrowser {
	private EqpStatusPageS page;
	private WebDriverWait w1;

	@BeforeClass
	public void SetUp() throws AWTException, InterruptedException {
		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 20);
		driver.get(conf.GetURL());
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
			w1.until(ExpectedConditions.textToBePresentInElement(
					page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div")),
					"This PRO is already on trailer."));
			w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			w1.until(ExpectedConditions.elementToBeClickable(page.RemoveProButton));
			page.CheckAllAddProButton.click();
			page.RemoveProButton.click();
		}
	}
}
