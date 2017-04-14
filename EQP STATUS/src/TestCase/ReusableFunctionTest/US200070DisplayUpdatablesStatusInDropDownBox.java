package TestCase.ReusableFunctionTest;

import java.awt.AWTException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import Function.Setup;
import Page.EqpStatusPageS;

public class US200070DisplayUpdatablesStatusInDropDownBox extends Setup {

	private EqpStatusPageS page;

	@Test(dataProvider = "status")
	public void VerifyStatusClickable(String status) throws InterruptedException {
		page.SetStatus(status);

	}

	@BeforeClass
	public void SetUp() throws AWTException, InterruptedException {

		page = new EqpStatusPageS(driver);
		  
		driver.manage().window().maximize();
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
