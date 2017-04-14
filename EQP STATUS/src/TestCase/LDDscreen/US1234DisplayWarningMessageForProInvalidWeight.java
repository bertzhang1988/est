package TestCase.LDDscreen;

import java.awt.AWTException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Function.CommonFunction;
import Function.DataCommon;
import Function.Setup;
import Page.EqpStatusPageS;

public class US1234DisplayWarningMessageForProInvalidWeight extends Setup {
	private EqpStatusPageS page;
	private WebDriverWait w1;

	@BeforeClass
	public void SetUp() throws AWTException, InterruptedException {
		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 20);
		  
		driver.manage().window().maximize();

	}

	@Test(priority = 1, dataProvider = "lddscreen", dataProviderClass = DataForUSLDDLifeTest.class, description = "LDD and ldg Trailer With pro has invalid weight set in ldd screen", dependsOnMethods = {
			"SetToLDD" })
	public void ToLDDtrailerHasProWithInvalidWeight(String terminalcd, String SCAC, String TrailerNB, Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		ArrayList<String> GetProHasInvalidWeight = DataCommon.GetProHasInvalidWeightOnTrailer(SCAC, TrailerNB);
		ArrayList<String> GetProOnTrailer = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		int numOfInvalidPro = GetProHasInvalidWeight.size();
		String warningMessage;
		if (numOfInvalidPro == 1) {
			warningMessage = "Warning:\n" + numOfInvalidPro
					+ " Shipment loaded on the trailer is missing valid weight. Please adjust trailer weight as necessary.";
		} else {
			warningMessage = "Warning:\n" + numOfInvalidPro
					+ " Shipments loaded on the trailer are missing valid weight. Please adjust trailer weight as necessary.";
		}
		w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField, warningMessage));

		// chekc grid
		for (String pro : GetProOnTrailer) {
			WebElement ProLine = page.ProListLDDForm
					.findElement(By.xpath("//div[contains(text(), '" + CommonFunction.addHyphenToPro(pro) + "')]"));
			jse.executeScript("arguments[0].scrollIntoView(true);", ProLine);
			String invalidcolor = page.ProListLDDForm
					.findElement(By
							.xpath("//div[contains(text(), '" + CommonFunction.addHyphenToPro(pro) + "')]/parent::div"))
					.getAttribute("class");
			if (GetProHasInvalidWeight.contains(pro)) {
				SA.assertTrue(invalidcolor.contains("invalidDataRowColor"),
						"invalid pro not with invalid color background  " + pro);
			} else {
				SA.assertFalse(invalidcolor.contains("invalidDataRowColor"),
						"valid pro  with invalid color background   " + pro);

			}
		}

		SA.assertAll();
	}

	@Test(priority = 50)
	public void SetToLDG() throws InterruptedException {
		page.SetStatus("LDG");
	}

	@Test(priority = 40)
	public void SetToLDD() throws InterruptedException {
		page.SetStatus("LDD");
	}

	@Test(priority = 2, dataProvider = "lddscreen", dataProviderClass = DataForUSLDDLifeTest.class, description = "ldd and ldg trailer With pro has invalid weight set in ldg screen, quick close", dependsOnMethods = {
			"SetToLDG" })
	public void ToLDDtrailerHasProWithInvalidWeightQuickClose(String terminalcd, String SCAC, String TrailerNB,
			Date MReqpst)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
		SoftAssert SA = new SoftAssert();
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		page.ChangeCube();
		page.SubmitAndCloseOutButton.click();
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status to Closed"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		ArrayList<String> GetProHasInvalidWeight = DataCommon.GetProHasInvalidWeightOnTrailer(SCAC, TrailerNB);
		ArrayList<String> GetProOnTrailer = DataCommon.GetProOnTrailer(SCAC, TrailerNB);
		int numOfInvalidPro = GetProHasInvalidWeight.size();
		String warningMessage;
		if (numOfInvalidPro == 1) {
			warningMessage = "Warning:\n" + numOfInvalidPro
					+ " Shipment loaded on the trailer is missing valid weight. Please adjust trailer weight as necessary.";
		} else {
			warningMessage = "Warning:\n" + numOfInvalidPro
					+ " Shipments loaded on the trailer are missing valid weight. Please adjust trailer weight as necessary.";
		}
		w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField, warningMessage));

		// chekc grid
		for (String pro : GetProOnTrailer) {
			WebElement ProLine = page.ProListForm
					.findElement(By.xpath("//div[contains(text(), '" + CommonFunction.addHyphenToPro(pro) + "')]"));
			jse.executeScript("arguments[0].scrollIntoView(true);", ProLine);
			String invalidcolor = page.ProListForm
					.findElement(By
							.xpath("//div[contains(text(), '" + CommonFunction.addHyphenToPro(pro) + "')]/parent::div"))
					.getAttribute("class");
			if (GetProHasInvalidWeight.contains(pro)) {
				SA.assertTrue(invalidcolor.contains("invalidDataRowColor"),
						"invalid pro not with invalid color background  " + pro);
			} else {
				SA.assertFalse(invalidcolor.contains("invalidDataRowColor"),
						"valid pro  with invalid color background   " + pro);

			}
		}
		page.qcCancelButton.click();
		w1.until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Set Trailer Status Loading"));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		SA.assertAll();

	}
}
