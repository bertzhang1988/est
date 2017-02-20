package TestCase.LdgScreen;

import java.awt.AWTException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
		page.SetStatus("ldg");
	}

	@Test(priority = 1, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class)
	public void CheckShipmentAlreadyLoadedOnLDGTrailerWithPro(String terminalcd, String SCAC, String TrailerNB,
			Date MRST) throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		String Cube = page.CubeField.getAttribute("value");
		if (Cube.equalsIgnoreCase("") || Cube.equalsIgnoreCase("0")) {
			int Ran = (int) (Math.random() * 99) + 1;
			page.SetCube(Integer.toString(Ran));
		}
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
