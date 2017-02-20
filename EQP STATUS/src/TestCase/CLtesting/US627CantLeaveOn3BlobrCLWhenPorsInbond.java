package TestCase.CLtesting;

import java.awt.AWTException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Function.CommonFunction;
import Function.DataCommon;
import Function.SetupBrowser;
import Function.Utility;
import Page.EqpStatusPageS;

public class US627CantLeaveOn3BlobrCLWhenPorsInbond extends SetupBrowser {
	private EqpStatusPageS page;
	private WebDriverWait w1;
	private WebDriverWait w2;

	@BeforeClass()
	public void SetUp() throws AWTException, InterruptedException, IOException {
		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 50);
		w2 = new WebDriverWait(driver, 80);
		driver.get(conf.GetURL());
		driver.manage().window().maximize();
		page.SetStatus("CL");
	}

	@Test(priority = 1, dataProvider = "ClScreen2", dataProviderClass = DataForCLScreenTesting.class, description = "to CL us trailer with inbond pro, lobr, leave on")
	public void ToClWithInbondProToUSLeaveOn(String terminalcd, String SCAC, String TrailerNB, String CountryCode)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// update cityRoute
		String NewCityRoute = page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		Date PlanDate = page.SetPlanDate(Localtime, 2);

		// select city route type
		String SetCityRtype = page.SetCityRouteType("trap");

		// set date&time
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// get inbond pro
		ArrayList<String> InbondPro = DataCommon.GetInbondProOnTrailer(SCAC, TrailerNB, CountryCode);

		// pro list before disposition
		LinkedHashSet<ArrayList<String>> ProInfo1 = DataCommon.GetProListLOBR(SCAC, TrailerNB);

		// handle the left pro
		page.HandleLOBRproAll("LeaveOn");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		w2.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				" is INBOND and cannot be delivered, must DOCK PRO."));
		for (String inbondPRO : InbondPro) {
			SAssert.assertTrue(
					page.ErrorAndWarningField.getText()
							.contains(inbondPRO + " is INBOND and cannot be delivered, must DOCK PRO."),
					"the error message is not showing for pro:" + inbondPRO);

		}
		w2.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));

		// check pro grid prepopulate
		SAssert.assertEquals(DataCommon.GetProListLOBR(SCAC, TrailerNB), ProInfo1, "pro OF THE TRAILER CHANGED");

		SAssert.assertAll();
	}

	@Test(priority = 2, dataProvider = "ClScreen2", dataProviderClass = DataForCLScreenTesting.class, description = " to CL cannada trailer with inbond pro, lobr, leave on")
	public void ToClWithInbondProToCANLeaveOn(String terminalcd, String SCAC, String TrailerNB, String CountryCode)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

		// update cityRoute
		String NewCityRoute = page.UpdateCityRoute();

		// enter plan date
		Date Localtime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		Date PlanDate = page.SetPlanDate(Localtime, 2);

		// select city route type
		String SetCityRtype = page.SetCityRouteType("INTERLINE");

		// set date&time
		page.SetDatePicker(page.GetDatePickerTime(), -1);
		Date AlterTime = CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());

		// get inbond pro
		ArrayList<String> InbondPro = DataCommon.GetInbondProOnTrailer(SCAC, TrailerNB, CountryCode);

		// pro list before disposition
		LinkedHashSet<ArrayList<String>> ProInfo1 = DataCommon.GetProListLOBR(SCAC, TrailerNB);

		// handle the left pro
		page.HandleLOBRproAll("LeaveOn");
		w1.until(ExpectedConditions.visibilityOf(page.AlertMessage));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		w2.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				" is INBOND and cannot be delivered, must DOCK PRO."));
		for (String inbondPRO : InbondPro) {
			SAssert.assertTrue(
					page.ErrorAndWarningField.getText()
							.contains(inbondPRO + " is INBOND and cannot be delivered, must DOCK PRO."),
					"the error message is not showing for pro:" + inbondPRO);

		}
		w2.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));

		// check pro grid prepopulate
		SAssert.assertEquals(DataCommon.GetProListLOBR(SCAC, TrailerNB), ProInfo1, "pro OF THE TRAILER CHANGED");

		SAssert.assertAll();
	}

	@AfterMethod
	public void getbackCL(ITestResult result) throws InterruptedException {
		if (result.getStatus() == ITestResult.FAILURE) {

			String Testparameter = Arrays.toString(Arrays.copyOf(result.getParameters(), 3)).replaceAll("[^\\d.a-zA-Z]",
					"");
			String FailureTestparameter = result.getName() + Testparameter;

			Utility.takescreenshot(driver, FailureTestparameter);
			driver.navigate().refresh();
			page.SetStatus("cl");
		}
	}
}
