package TestCase.LDDscreen;

import java.awt.AWTException;
import java.sql.SQLException;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import Data.DataForUS1203;
import Function.SetupBrowser;
import Page.EqpStatusPageS;

public class US1203AndUS200074ChangeWeightOnClosedScreen extends SetupBrowser {

	private EqpStatusPageS page;
	private WebDriverWait w1;

	@BeforeClass
	public void SetUp() throws AWTException, InterruptedException {
		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 10);
		driver.get(conf.GetURL());
		driver.manage().window().maximize();
		page.SetStatus("ldd");

	}

	@Test(priority = 1, dataProvider = "12.03", dataProviderClass = DataForUS1203.class)
	public void VerifyTrailerShorter35(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		String WeightPattern = "([\\d]+)";
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		String[] Weight = { "0", "100", "28000", "30000", "50000", "70000", "80000" };
		for (int i = 0; i < Weight.length; i++) {
			String Weight1 = Weight[i];
			page.SetShipWeight(Weight1);
			Thread.sleep(500);
			String pureWeight = Weight1.replace(" ", "").trim();
			if (!pureWeight.matches(WeightPattern) || Integer.parseInt(pureWeight) > 50000
					|| Integer.parseInt(pureWeight) == 0) {
				w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
						"Shipment Weight Error:\nYou must enter a value between 1 and 50000 pounds."));
			} else {
				if (Integer.parseInt(pureWeight) < 1000 && Integer.parseInt(pureWeight) > 0) {
					w1.until(ExpectedConditions.textToBePresentInElement(page.AlertMessage,
							"You've entered a weight less than 1,000 lbs. Is this the correct weight?"));
				} else if (Integer.parseInt(pureWeight) > 28000 && Integer.parseInt(pureWeight) <= 50000) {
					w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
							"Shipment Weight Warning:\nPlease verify that this weight is allowed for a trailer less than 35 ft."));
				} else {
					w1.until(ExpectedConditions.invisibilityOfElementLocated(
							By.xpath("//*[contains(text(), You must enter a value between 1 and 50000 lbs.)]")));
					w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(
							"//*[contains(text(), Please verify that this weight is allowed for a trailer less than 35 ft.)]")));
				}
			}
			if (page.CheckDisplay(page.AlertMessage)) {
				page.CorrectWeightButton.click();
			}

			page.ShipmentWeight2.clear();
			Thread.sleep(1000);
		}

	}

	@Test(priority = 2, dataProvider = "12.03", dataProviderClass = DataForUS1203.class)
	public void VerifyTrailerLonger35(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {

		String WeightPattern = "([\\d]+)";
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		String[] Weight = { "0", "300", "900", "30000", "50000", "60000", "70000", "80000" };
		for (int i = 0; i < Weight.length; i++) {
			String Weight1 = Weight[i];
			page.SetShipWeight(Weight1);
			Thread.sleep(500);
			String pureWeight = Weight1.replace(" ", "").trim();
			if (!pureWeight.matches(WeightPattern) || Integer.parseInt(pureWeight) > 70000
					|| Integer.parseInt(pureWeight) == 0) {
				w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
						"Shipment Weight Error:\nYou must enter a value between 1 and 70000 pounds."));
			} else {
				if (Integer.parseInt(pureWeight) < 1000 && Integer.parseInt(pureWeight) > 0) {
					w1.until(ExpectedConditions.textToBePresentInElement(page.AlertMessage,
							"You've entered a weight less than 1,000 lbs. Is this the correct weight?"));
				} else if (Integer.parseInt(pureWeight) > 50000 && Integer.parseInt(pureWeight) <= 70000) {
					w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
							"Shipment Weight Warning:\nPlease verify that this weight is allowed for a trailer 35 ft or longer."));
				} else {
					(new WebDriverWait(driver, 1)).until(ExpectedConditions.invisibilityOfElementLocated(
							By.xpath("//*[contains(text(), You must enter a value between 1 and 70000 pounds.)]")));
					(new WebDriverWait(driver, 1)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(
							"//*[contains(text(), Please verify that this weight is allowed for a trailer 35 ft or longer.)]")));
				}
			}
			if (page.CheckDisplay(page.AlertMessage)) {
				page.CorrectWeightButton.click();
			}

			page.ShipmentWeight2.clear();
			Thread.sleep(1000);
		}
	}
}
