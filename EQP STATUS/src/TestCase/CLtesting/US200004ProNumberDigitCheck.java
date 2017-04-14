package TestCase.CLtesting;

import java.awt.AWTException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Data.DataForUS200004;
import Function.CommonFunction;
import Function.Setup;
import Page.EqpStatusPageS;

public class US200004ProNumberDigitCheck extends Setup {
	private EqpStatusPageS page;

	@BeforeClass
	public void SetUp() throws AWTException, InterruptedException {
		page = new EqpStatusPageS(driver); 
		driver.manage().window().maximize();
		page.SetStatus("cl");
	}

	@Test(priority = 1, dataProvider = "ClScreen1", dataProviderClass = DataForCLScreenTesting.class)
	public void EnterTrailerInCLWithoutPro(String terminalcd, String SCAC, String TrailerNB, String CityR,
			String CityRT, String AmountPro, String AmountWeight, Date PlanD, Date MRSts)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
	}

	@Test(priority = 2, dataProvider = "2000.04", dataProviderClass = DataForUS200004.class, dependsOnMethods = {
			"EnterTrailerInCLWithoutPro" })
	public void VerifyProDigitCheck(String pro) throws InterruptedException {
		page.EnterPro(pro);
		String Pronumber = pro.trim().toUpperCase();
		String message = null;
		int flag = CommonFunction.CheckProPattern(Pronumber);
		if (flag == 1) {
			message = "Invalid Pro Number";
		} else if (flag == 3) {
			message = "Invalid Check Digit";
		} else if (flag == 2) {
			message = "";
		}

		int NEW = page.AddProForm.findElements(By.xpath("div")).size();
		// Assert.assertEquals(page.AddProForm.findElement(By.xpath("div["+NEW+"]/div/div[2]/div")).getText(),Pronumber);
		(new WebDriverWait(driver, 5)).until(ExpectedConditions.textToBePresentInElement(
				page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div")), message));
		if (flag != 2) {
			System.out.println(pro);
		}
		if (NEW == 5) {
			page.RemoveProButton.click();
		}
	}

	// @Test(priority=3,dataProvider =
	// "ldgtrailerNoPro",dataProviderClass=DataForUS200004.class)
	public void VerifyProDigitCheck(String terminalcd, String SCAC, String TrailerNB)
			throws InterruptedException, AWTException {
		SoftAssert SAssert = new SoftAssert();
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
		List<String> prolist = Arrays.asList(DataForUS200004.prolist);
		for (int i = 0; i < prolist.size(); i++) {
			String pro = prolist.get(i);
			page.EnterPro(pro);
			String Pronumber = pro.trim().toUpperCase();
			String PronumberH = page.addHyphenToPro(Pronumber);
			try {
				(new WebDriverWait(driver, 5))
						.until(ExpectedConditions.textToBePresentInElement(page.AddProForm, PronumberH));
			} catch (Exception e) {
				System.out.println(Pronumber + " is not kick to the grid");
			}
			String message = null;
			int flag = CommonFunction.CheckProPattern(Pronumber);
			if (flag == 1) {
				message = "Invalid Pro Number";
			} else if (flag == 3) {
				message = "Invalid Check Digit";
			} else if (flag == 2) {
				message = "";
			}

			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			if (NEW != 0) {
				// (new WebDriverWait(driver,
				// 5)).until(ExpectedConditions.textToBePresentInElement(page.AddProForm.findElement(By.xpath("div["+NEW+"]/div/div[2]/div")),
				// Pronumber));
				SAssert.assertEquals(page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[2]/div")).getText(),
						PronumberH);
				SAssert.assertEquals(page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div")).getText(),
						message, "   " + Pronumber);
			} // (new WebDriverWait(driver,
				// 5)).until(ExpectedConditions.textToBePresentInElement(page.AddProForm.findElement(By.xpath("div["+NEW+"]/div/div[3]/div")),message));
			if (NEW == 5) {
				page.RemoveProButton.click();
			}
		}
		SAssert.assertAll();
	}

	@AfterClass
	public void TearDown() {
		page.RemoveProButton.click();
	}

}
