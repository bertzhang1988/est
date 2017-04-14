package TestCase.ReusableFunctionTest;

import java.awt.AWTException;
import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Function.ConfigRd;
import Function.Setup;
import Page.EqpStatusPageS;

public class US2000142DisplayErrorMessageForToolAccessRejection extends Setup {
	private EqpStatusPageS page;
	private WebDriverWait w1;

	@BeforeClass
	public void SetUp() throws AWTException, InterruptedException {

		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 150);
		  
		driver.manage().window().maximize();

	}

	@Test(priority = 1)
	public void CheckStatusTrailerButton() {
		w1.until(ExpectedConditions.visibilityOf(page.StatusTrailerButton));
		page.StatusTrailerButton.click();
		w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"You do not have authority to update trailers for this terminal"));
	}

	@Test(priority = 2)
	public void CheckLoadToEnrButton() {
		w1.until(ExpectedConditions.visibilityOf(page.LOADTOENRbutton));
		page.StatusTrailerButton.click();
		w1.until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"You do not have authority to update trailers for this terminal"));
	}

	@Test(priority = 3)
	public void CheckTrailerByTerminalButton() {
		w1.until(ExpectedConditions.visibilityOf(page.TrailerByTerminalButton));
		page.StatusTrailerButton.click();
		w1.until(ExpectedConditions.visibilityOf(page.IQTerminalInput));
	}
}
