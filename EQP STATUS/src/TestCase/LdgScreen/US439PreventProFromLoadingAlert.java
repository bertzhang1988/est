package TestCase.LdgScreen;

import java.awt.AWTException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import Data.DataForUS439;
import Function.Setup;
import Page.EqpStatusPageS;

public class US439PreventProFromLoadingAlert extends Setup {
	private EqpStatusPageS page;
	private WebDriverWait w1;

	@BeforeClass
	public void SetUp() throws AWTException, InterruptedException {
		page = new EqpStatusPageS(driver);
		w1 = new WebDriverWait(driver, 50);
		  
		driver.manage().window().maximize();
		page.SetStatus("LDG");
	}

	@Test(dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class)
	public void EnterLDGTrailerWithoutPro(String terminalcd, String SCAC, String TrailerNB, Date MRST)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);
	}

	@Test(priority = 2, dataProvider = "Invalid pro", dataProviderClass = DataForUS439.class, dependsOnMethods = {
			"EnterLDGTrailerWithoutPro" })
	public void VerifyMRValidation(String PRO)
			throws InterruptedException, AWTException, ClassNotFoundException, SQLException {

		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
		page.EnterPro(PRO);
		int NEW = page.AddProForm.findElements(By.xpath("div")).size();
		// change cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		page.SubmitButton.click();
		WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
		w1.until(ExpectedConditions.textToBePresentInElement(Message, "Cannot add master bill."));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
	}

	@Test(priority = 3, dataProvider = "Invalid pro", dataProviderClass = DataForUS439.class, dependsOnMethods = {
			"EnterLDGTrailerWithoutPro" })
	public void VerifySUValidation(String PRO)
			throws InterruptedException, AWTException, ClassNotFoundException, SQLException {

		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
		page.EnterPro(PRO);
		int NEW = page.AddProForm.findElements(By.xpath("div")).size();
		// change cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		page.SubmitButton.click();
		WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
		w1.until(ExpectedConditions.textToBePresentInElement(Message, "Cannot add supplemental bill."));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
	}

	@Test(priority = 4, dataProvider = "Invalid pro", dataProviderClass = DataForUS439.class, dependsOnMethods = {
			"EnterLDGTrailerWithoutPro" })
	public void VerifyVOValidation(String PRO)
			throws InterruptedException, AWTException, ClassNotFoundException, SQLException {

		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
		page.EnterPro(PRO);
		int NEW = page.AddProForm.findElements(By.xpath("div")).size();
		// change cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		page.SubmitButton.click();
		WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
		w1.until(ExpectedConditions.textToBePresentInElement(Message, "Cannot add voided PRO."));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
	}

	@Test(priority = 5, dataProvider = "Invalid pro", dataProviderClass = DataForUS439.class, dependsOnMethods = {
			"EnterLDGTrailerWithoutPro" })
	public void VerifyAlreadyDeliveredValidation(String PRO)
			throws InterruptedException, AWTException, ClassNotFoundException, SQLException {

		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
		page.EnterPro(PRO);
		int NEW = page.AddProForm.findElements(By.xpath("div")).size();
		// change cube
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		page.SetCube(NewCube);
		page.SubmitButton.click();
		WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
		w1.until(ExpectedConditions.textToBePresentInElement(Message, "PRO already delivered."));
		w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
	}

	@Test(priority = 6, dataProvider = "ldgscreen", dataProviderClass = DataForUSLDGLifeTest.class)
	public void VerifyMasterRevenueValidationToLDGTrailerWithoutPro(String terminalcd, String SCAC, String TrailerNB,
			Date MRST) throws InterruptedException, AWTException, ClassNotFoundException, SQLException {
		page.SetLocation(terminalcd);
		page.EnterTrailer(SCAC, TrailerNB);

		// ADD MR PRO
		ArrayList<String> MRprolist = DataForUS439.Getpro1("mr", SCAC, TrailerNB);
		for (int i = 0; i < 2; i++) {
			page.RemoveProButton.click();
			String pro = MRprolist.get(i);
			page.EnterPro(pro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			// change cube
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
			page.SubmitButton.click();
			WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
			try {
				w1.until(ExpectedConditions.textToBePresentInElement(Message, "Cannot add master bill."));
			} catch (Exception e) {
				System.out.println("mr pro is not working as expectation" + pro);
			}
			w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			page.CheckAllAddProButton.click();
			page.RemoveProButton.click();
		}

		// ADD SU PRO
		ArrayList<String> SUprolist = DataForUS439.Getpro1("SU", SCAC, TrailerNB);
		for (int i = 0; i < 2; i++) {
			page.RemoveProButton.click();
			String pro = SUprolist.get(i);
			page.EnterPro(pro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			// change cube
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
			page.SubmitButton.click();
			WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
			try {
				w1.until(ExpectedConditions.textToBePresentInElement(Message, "Cannot add supplemental bill."));
			} catch (Exception e) {
				System.out.println("su pro is not working as expectation" + pro);
			}
			w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			page.CheckAllAddProButton.click();
			page.RemoveProButton.click();
		}

		// ADD VO PRO
		ArrayList<String> VOprolist = DataForUS439.Getpro1("VO", SCAC, TrailerNB);
		for (int i = 0; i < 2; i++) {
			page.RemoveProButton.click();
			String pro = VOprolist.get(i);
			page.EnterPro(pro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			// change cube
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
			page.SubmitButton.click();
			WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
			try {
				w1.until(ExpectedConditions.textToBePresentInElement(Message, "Cannot add voided PRO."));
			} catch (Exception e) {
				System.out.println("vo pro is not working as expectation" + pro);
			}
			w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			page.CheckAllAddProButton.click();
			page.RemoveProButton.click();
		}

		// ADD PRO already delivered.
		ArrayList<String> Dprolist = DataForUS439.Getpro1("", SCAC, TrailerNB);
		for (int i = 0; i < 2; i++) {
			page.RemoveProButton.click();
			String pro = Dprolist.get(i);
			page.EnterPro(pro);
			int NEW = page.AddProForm.findElements(By.xpath("div")).size();
			// change cube
			int Ran = (int) (Math.random() * 99) + 1;
			String NewCube = Integer.toString(Ran);
			page.SetCube(NewCube);
			page.SubmitButton.click();
			WebElement Message = page.AddProForm.findElement(By.xpath("div[" + NEW + "]/div/div[3]/div"));
			try {
				w1.until(ExpectedConditions.textToBePresentInElement(Message, "PRO already delivered."));
			} catch (Exception e) {
				System.out.println("already delivered pro is not working as expectation" + pro);
			}
			w1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
			page.CheckAllAddProButton.click();
			page.RemoveProButton.click();
		}
	}

	@AfterClass
	public void TearDown() {
		page.CheckAllAddProButton.click();
		page.RemoveProButton.click();
		page.CheckAllAddProButton.click();
	}
}
