package TestCase.LDDscreen;

import java.awt.AWTException;
import java.sql.SQLException;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import Data.DataForUS1212AND1211;
import Function.CommonFunction;
import Function.SetupBrowser;
import Page.EqpStatusPageS;

public class US1212and1211andUS1222SummarizeSeriviceAndFlagOnTrailer extends SetupBrowser {
	private EqpStatusPageS page;
	private WebDriverWait w1;

	@Test(priority = 2, dataProvider = "12.12", dataProviderClass = DataForUS1212AND1211.class)
	public void VerifySmmarizeServiceOnTrailer(String terminalcd, String SCAC, String TrailerNB)
			throws AWTException, ClassNotFoundException, SQLException, InterruptedException {
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		String Shipflag = CommonFunction.GetFlag(SCAC, TrailerNB, "flag");
		String Serviceflag = CommonFunction.GetFlag(SCAC, TrailerNB, "SERV");
		String getShipflag = page.ShipmentFlag.getText();
		String getServiceflag = page.ServiceFlag.getText();
		w1.until(ExpectedConditions.textToBePresentInElement(page.ShipmentFlag, Shipflag));
		w1.until(ExpectedConditions.textToBePresentInElement(page.ServiceFlag, Serviceflag));
		// String getShipflag=page.ShipmentFlag.getText();
		// String getServiceflag=page.ServiceFlag.getText();
		System.out.println(SCAC + " " + TrailerNB + ": " + getShipflag + " " + getServiceflag);
		System.out.println(SCAC + " " + TrailerNB + ": " + Shipflag + " " + Serviceflag);
	}

	@BeforeClass
	public void SetUp() throws AWTException, InterruptedException {
		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 20);
		driver.get(conf.GetURL());
		driver.manage().window().maximize();
		page.SetStatus("ldd");

	}

	@Test(priority = 1)
	public void VerifyTheLegendDisplay() throws AWTException, InterruptedException {
		page.SetLocation("326");
		w1.until(ExpectedConditions.elementToBeClickable(page.ServiceFlagExclamation));
		page.ServiceFlagExclamation.click();
		w1.until(ExpectedConditions.visibilityOf(page.ServiceFlagLegend));
		w1.until(ExpectedConditions.textToBePresentInElement(page.ServiceFlagLegend,
				"E = ETME\nS = STME\nH = Corridor Hub\nV = Velocity\nX = Exclusive Use"));
		System.out.println(page.ServiceFlagLegend.getText());

		w1.until(ExpectedConditions.elementToBeClickable(page.ShipmentFlagExclamation));
		page.ShipmentFlagExclamation.click();
		w1.until(ExpectedConditions.visibilityOf(page.ShipmentFlagLegend));
		w1.until(ExpectedConditions.textToBePresentInElement(page.ShipmentFlagLegend,
				"! = Poison\n@ = Food\n% = Hazardous\nF = Freezable"));
		System.out.println(page.ShipmentFlagLegend.getText());
	}
}
