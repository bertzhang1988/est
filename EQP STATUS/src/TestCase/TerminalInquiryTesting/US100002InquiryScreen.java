package TestCase.TerminalInquiryTesting;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Function.CommonFunction;
import Function.ConfigRd;
import Page.EqpStatusPageS;

public class US100002InquiryScreen {
	private WebDriver driver;
	private EqpStatusPageS page;
	private Actions builder;

	@BeforeClass
	@Parameters({ "browser" })
	public void SetUp(@Optional("chrome") String browser) throws AWTException, InterruptedException {
		ConfigRd Conf = new ConfigRd();
		if (browser.equalsIgnoreCase("chrome")) {
			System.setProperty("webdriver.chrome.driver", Conf.GetChromePath());
			driver = new ChromeDriver();
		} else if (browser.equalsIgnoreCase("ie")) {
			System.setProperty("webdriver.ie.driver", Conf.GetIEPath());
			driver = new InternetExplorerDriver();
		} else if (browser.equalsIgnoreCase("hl")) {
			File file = new File(Conf.GetPhantomJSDriverPath());
			System.setProperty("phantomjs.binary.path", file.getAbsolutePath());
			driver = new PhantomJSDriver();
		}
		page = new EqpStatusPageS(driver);
		driver.get(Conf.GetURL());
		driver.manage().window().maximize();
		page.SetTerminalInquiryScreen();
		builder = new Actions(driver);

	}

	@Test(priority = 1, dataProvider = "1000.02", dataProviderClass = DataForInQuiryScreen.class)
	public void DisplayListOfStatuses(String terminal)
			throws ClassNotFoundException, SQLException, InterruptedException {
		ArrayList<String> ExpectedStatusList = DataForInQuiryScreen.GetStatusList(terminal);
		page.IQTerminalInput.clear();
		page.IQTerminalInput.sendKeys(terminal);
		// builder.sendKeys(page.EQTerminalInput, Keys.ENTER).build().perform();
		page.SearchButton.click();
		Date d = CommonFunction.gettime("UTC");
		if (ExpectedStatusList.size() != 0) {
			Thread.sleep(500);
			(new WebDriverWait(driver, 20))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
			(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(page.IQStatusList));
			Date d2 = CommonFunction.gettime("UTC");
			int CountStatus = page.IQStatusList.findElements(By.xpath("div")).size();
			ArrayList<String> StatusList = new ArrayList<String>();
			for (int i = 1; i <= CountStatus; i++) {
				String StatusName = page.IQStatusList
						.findElement(By.xpath("div[" + i + "]//div[@class='trailer-inquiry-status']")).getText();
				StatusList.add(StatusName);
			}
			System.out.println("time of initial inquiry at facility level: " + terminal + "   "
					+ (d2.getTime() - d.getTime() - 500) / 1000.0);
			Assert.assertEquals(StatusList, ExpectedStatusList,
					"\nexpected: " + ExpectedStatusList + " \nactual: " + StatusList + "\n");
		} else {
			(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(
					By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']")));
		}
	}

	@Test(priority = 2, dataProvider = "1000.02", dataProviderClass = DataForInQuiryScreen.class)
	public void DisplayListOfStatusesAndPup(String terminal)
			throws ClassNotFoundException, SQLException, InterruptedException {
		ArrayList<ArrayList<String>> ExpectedStatusList = DataForInQuiryScreen.GetStatusListAndPup(terminal);
		page.IQTerminalInput.clear();
		page.IQTerminalInput.sendKeys(terminal);
		page.SearchButton.click();
		Date d = CommonFunction.gettime("UTC");
		if (ExpectedStatusList.size() != 0) {
			Thread.sleep(500);
			(new WebDriverWait(driver, 20))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
			(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(page.IQStatusList));
			Date d2 = CommonFunction.gettime("UTC");
			int CountStatus = page.IQStatusList.findElements(By.xpath("div")).size();
			ArrayList<ArrayList<String>> StatusList = new ArrayList<ArrayList<String>>();
			for (int i = 1; i <= CountStatus; i++) {
				String StatusName = page.IQStatusList
						.findElement(By.xpath("div[" + i + "]//div[@class='trailer-inquiry-status']")).getText();
				String Bills = page.IQStatusList
						.findElement(By.xpath("div[" + i + "]//div[@class='trailer-inquiry-status-stat']/div[1]/span"))
						.getText();
				String Schedules = page.IQStatusList
						.findElement(By.xpath("div[" + i + "]//div[@class='trailer-inquiry-status-stat']/div[2]/span"))
						.getText();
				String PUPS = page.IQStatusList
						.findElement(By.xpath("div[" + i + "]//div[@class='trailer-inquiry-status-stat']/div[3]/span"))
						.getText();
				String VANS = page.IQStatusList
						.findElement(By.xpath("div[" + i + "]//div[@class='trailer-inquiry-status-stat']/div[4]/span"))
						.getText();
				ArrayList<String> A = new ArrayList<String>();
				A.add(StatusName);
				A.add(Bills);
				A.add(Schedules);
				A.add(PUPS);
				A.add(VANS);
				StatusList.add(A);
			}
			System.out.println("time of initial inquiry at facility level: " + terminal + "   "
					+ (d2.getTime() - d.getTime() - 500) / 1000.0);
			Assert.assertEquals(ExpectedStatusList, StatusList,
					"\nexpected: " + ExpectedStatusList + "\nactual: " + StatusList + "\n");
		} else {
			(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(
					By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']")));
		}
	}

	@Test(priority = 3, dataProvider = "1000.02", dataProviderClass = DataForInQuiryScreen.class)
	public void DisplayTrailerGrid(String terminal) throws ClassNotFoundException, SQLException, InterruptedException {
		SoftAssert Sassert = new SoftAssert();
		ArrayList<String> ExpectedStatusList = DataForInQuiryScreen.GetStatusList(terminal);
		page.IQTerminalInput.clear();
		page.IQTerminalInput.sendKeys(terminal);
		page.SearchButton.click();
		Date d = CommonFunction.gettime("UTC");
		if (ExpectedStatusList.size() != 0) {
			Thread.sleep(500);
			(new WebDriverWait(driver, 20))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
			(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(page.IQStatusList));
			int CountStatus = page.IQStatusList.findElements(By.xpath("div")).size();
			for (int i = 1; i <= CountStatus; i++) {
				String StatusName = page.IQStatusList
						.findElement(By.xpath("div[" + i + "]//div[@class='trailer-inquiry-status']")).getText();
				StatusName = CommonFunction.GetAbbreNameOfStatus(StatusName);
				page.IQStatusList.findElement(By.xpath("div[" + i + "]//div[@class='panel-heading']")).click();
				Date d1 = CommonFunction.gettime("UTC");
				WebElement TrailerGrid = page.IQStatusList.findElement(
						By.xpath("//div[@class='ui-grid-contents-wrapper']/div[3]//div[@class='ui-grid-canvas']"));
				(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(TrailerGrid));
				(new WebDriverWait(driver, 20))
						.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
				Date d2 = CommonFunction.gettime("UTC");
				// (new WebDriverWait(driver,
				// 20)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']//div[@class='ui-grid-canvas']/div")));

				int TrailerLine = TrailerGrid.findElements(By.xpath("div[@class='ui-grid-row']")).size();
				List<ArrayList<String>> TrailerInformation = new ArrayList<ArrayList<String>>();
				for (int j = 1; j <= TrailerLine; j++) {
					String[] OneTrailerInoformation = TrailerGrid.findElements(By.xpath("div[@class='ui-grid-row']"))
							.get(j - 1).getText().split("\\n");
					ArrayList<String> e = new ArrayList<String>(Arrays.asList(OneTrailerInoformation));
					TrailerInformation.add(e);
				}

				ArrayList<ArrayList<String>> ExpectedTrailerInformation = DataForInQuiryScreen
						.GetTrailerInformation(terminal, StatusName, d);
				Sassert.assertEquals(TrailerInformation, ExpectedTrailerInformation, "  " + StatusName);
				page.IQStatusList.findElement(By.xpath("div[" + i + "]//div[@class='panel-heading']")).click();
				System.out.println("time of trailer list for specific status: " + terminal + " " + StatusName + "  "
						+ (d2.getTime() - d1.getTime()) / 1000.0);
			}
		} else {
			(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(
					By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']")));
		}
		Sassert.assertAll();
	}

	@Test(priority = 4, dataProvider = "1000.02", dataProviderClass = DataForInQuiryScreen.class)
	public void DisplayProGrid(String terminal) throws ClassNotFoundException, SQLException, InterruptedException {
		SoftAssert Sassert = new SoftAssert();
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(10, TimeUnit.SECONDS)
				.pollingEvery(500, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class);
		ArrayList<String> ExpectedStatusList = DataForInQuiryScreen.GetStatusList(terminal);
		page.IQTerminalInput.clear();
		page.IQTerminalInput.sendKeys(terminal);
		page.SearchButton.click();
		Date d = CommonFunction.gettime("UTC");
		if (ExpectedStatusList.size() != 0) {
			Thread.sleep(500);
			(new WebDriverWait(driver, 20))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
			(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(page.IQStatusList));
			int CountStatus = page.IQStatusList.findElements(By.xpath("div")).size();
			for (int i = 1; i <= CountStatus; i++) {
				String StatusName = page.IQStatusList
						.findElement(By.xpath("div[" + i + "]//div[@class='trailer-inquiry-status']")).getText();
				page.IQStatusList.findElement(By.xpath("div[" + i + "]//div[@class='panel-heading']")).click();
				WebElement TrailerGrid = page.IQStatusList.findElement(
						By.xpath("//div[@class='ui-grid-contents-wrapper']/div[3]//div[@class='ui-grid-canvas']"));
				WebElement PlusSignColumn = page.IQStatusList.findElement(
						By.xpath("//div[@class='ui-grid-contents-wrapper']/div[2]//div[@class='ui-grid-canvas']"));
				(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(TrailerGrid));
				Thread.sleep(1000);
				(new WebDriverWait(driver, 20))
						.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
				// (new WebDriverWait(driver,
				// 20)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']//div[@class='ui-grid-canvas']/div")));

				int TrailerLine = TrailerGrid.findElements(By.xpath("div[@class='ui-grid-row']")).size();
				if (TrailerLine > 20) {
					TrailerLine = 20;
				}
				List<ArrayList<String>> TrailerInformation = new ArrayList<ArrayList<String>>();
				for (int j = 1; j <= TrailerLine; j++) {

					List<WebElement> PlusSign = PlusSignColumn.findElements(By.tagName("i"));
					JavascriptExecutor jse = (JavascriptExecutor) driver;
					jse.executeScript("arguments[0].scrollIntoView(false);", PlusSign.get(j - 1));
					// Actions actions = new Actions(driver);
					// actions.moveToElement(PlusSign.get(j-1));
					// actions.perform();

					String SCAC;
					String trailerNb;
					String SCACTrailer = TrailerGrid.findElement(By.xpath("div[" + j + "]/div/div[1]")).getText();
					if (SCACTrailer.matches("[a-zA-Z]+[\\d]+")) {
						SCAC = SCACTrailer.substring(0, 4);
						trailerNb = SCACTrailer.substring(4);
					} else {
						SCAC = "RDWY";
						trailerNb = SCACTrailer;
					}
					PlusSign.get(j - 1).click();
					Date d1 = CommonFunction.gettime("UTC");
					wait.until(ExpectedConditions.presenceOfElementLocated(By
							.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']//div[@class='ui-grid-contents-wrapper']/div[3]//div[@class='ui-grid-canvas']/child::div["
									+ (j)
									+ "]/div[@class='expandableRow']/div[@config='row.entity.subGridConfig']//div[@class='ui-grid-canvas']")));
					(new WebDriverWait(driver, 20))
							.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
					Date d2 = CommonFunction.gettime("UTC");
					WebElement ProGridForEachTrailer = TrailerGrid.findElements(By.xpath("div[@class='ui-grid-row']"))
							.get(j - 1).findElement(By.xpath(
									"div[@class='expandableRow']/div[@config='row.entity.subGridConfig']//div[@class='ui-grid-canvas']"));
					LinkedHashSet<ArrayList<String>> ProInformation = page
							.GetProListInInquiryScreen(ProGridForEachTrailer);
					LinkedHashSet<ArrayList<String>> ExpectedProInformation = DataForInQuiryScreen
							.GetProListInQuiry(SCAC, trailerNb);
					Sassert.assertEquals(ProInformation, ExpectedProInformation, StatusName + " " + SCACTrailer);
					jse.executeScript("arguments[0].scrollIntoView(false);", PlusSign.get(j - 1));
					PlusSign.get(j - 1).click();
					(new WebDriverWait(driver, 20)).until(ExpectedConditions.stalenessOf(ProGridForEachTrailer));
					System.out.println("time of pro detail under specific trailer: " + terminal + " " + StatusName
							+ "  " + SCACTrailer + "  " + (d2.getTime() - d1.getTime()) / 1000.0);
				}
				// ArrayList<ArrayList<String>>
				// ExpectedTrailerInformation=DataForInQuiryScreen.GetTrailerInformation(terminal,
				// StatusName, d);
				// Sassert.assertEquals(TrailerInformation,ExpectedTrailerInformation,"
				// "+StatusName);
				page.IQStatusList.findElement(By.xpath("div[" + i + "]//div[@class='panel-heading']")).click();
			}
		} else {
			(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(
					By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']")));
		}
		Sassert.assertAll();
	}

	@Test(priority = 5, dataProvider = "1000.02", dataProviderClass = DataForInQuiryScreen.class)
	public void Filter(String terminal) throws ClassNotFoundException, SQLException, InterruptedException {
		SoftAssert Sassert = new SoftAssert();
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(10, TimeUnit.SECONDS)
				.pollingEvery(1, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		ArrayList<String> ExpectedStatusList = DataForInQuiryScreen.GetStatusList(terminal);
		page.IQTerminalInput.clear();
		page.IQTerminalInput.sendKeys(terminal);
		page.SearchButton.click();
		if (ExpectedStatusList.size() != 0) {
			Thread.sleep(500);
			(new WebDriverWait(driver, 20))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
			(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.IQStatusList));
			if (!page.FileterField.isDisplayed()) {
				page.FilterButton.click();
				wait.until(ExpectedConditions.visibilityOf(page.FileterField));
			}
			if (!page.TrailerLengthFileterField.isDisplayed()) {
				jse.executeScript("arguments[0].scrollIntoView(true);", page.TrailerLengthFileterButton);
				page.TrailerLengthFileterButton.click();
			}
			if (!page.SubTypeFileterField.isDisplayed()) {
				jse.executeScript("arguments[0].scrollIntoView(true);", page.SubTypeFileterButton);
				page.SubTypeFileterButton.click();
			}
			ArrayList<String> leng = new ArrayList<String>();
			ArrayList<String> ty = new ArrayList<String>();

			// check length filter

			for (int c = 0; c < 3; c++) {
				if (!page.FileterField.isDisplayed()) {
					page.FilterButton.click();
					wait.until(ExpectedConditions.visibilityOf(page.FileterField));
				}
				jse.executeScript("arguments[0].scrollIntoView(true);", page.TrailerLengthFileterButton);
				page.TrailerLengthFileterField.findElement(By.cssSelector("button[label='Unselect All']")).click();// unselect
																													// all
				List<WebElement> AllLengthItem = page.TrailerLengthFileterField
						.findElements(By.tagName("yrc-checkbox"));
				int Ran = (int) (Math.random() * (AllLengthItem.size() - 1)) + 1;
				WebElement SelectItem = page.TrailerLengthFileterField
						.findElement(By.xpath(".//yrc-checkbox[" + Ran + "]//label"));
				SelectItem.click();
				String length = SelectItem.getText();
				System.out.println(length);
				leng.add(length);
				page.ApplyButton.click();
				Date d = CommonFunction.gettime("UTC");
				Thread.sleep(500);
				(new WebDriverWait(driver, 20))
						.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
				int CountStatus = page.IQStatusList.findElements(By.xpath("div")).size();
				for (int i = 1; i <= CountStatus; i++) {
					String StatusName = page.IQStatusList
							.findElement(By.xpath("div[" + i + "]//div[@class='trailer-inquiry-status']")).getText();
					jse.executeScript("arguments[0].scrollIntoView(false);",
							page.IQStatusList.findElement(By.xpath("div[" + i + "]//div[@class='panel-heading']")));
					page.IQStatusList.findElement(By.xpath("div[" + i + "]//div[@class='panel-heading']")).click();
					Thread.sleep(500);
					(new WebDriverWait(driver, 20))
							.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
					WebElement TrailerGrid = page.IQStatusList.findElement(
							By.xpath("//div[@class='ui-grid-contents-wrapper']/div[3]//div[@class='ui-grid-canvas']"));
					(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(TrailerGrid));
					// (new WebDriverWait(driver,
					// 20)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']//div[@class='ui-grid-canvas']/div")));

					int TrailerLine = TrailerGrid.findElements(By.xpath("div[@class='ui-grid-row']")).size();
					List<ArrayList<String>> TrailerInformation = new ArrayList<ArrayList<String>>();
					for (int j = 1; j <= TrailerLine; j++) {
						String[] OneTrailerInoformation = TrailerGrid
								.findElements(By.xpath("div[@class='ui-grid-row']")).get(j - 1).getText().split("\\n");
						ArrayList<String> e = new ArrayList<String>(Arrays.asList(OneTrailerInoformation));
						TrailerInformation.add(e);
					}
					LinkedHashSet<ArrayList<String>> ExpectedTrailerInformation = DataForInQuiryScreen
							.GetTrailerInformationByFilter(terminal, StatusName, d, leng, ty);
					Sassert.assertEquals(TrailerInformation, ExpectedTrailerInformation,
							"  " + StatusName + "   " + length);
					page.IQStatusList.findElement(By.xpath("div[" + i + "]//div[@class='panel-heading']")).click();

				}
				leng.clear();
				ty.clear();
			}
			if (!page.FileterField.isDisplayed()) {
				page.FilterButton.click();
				wait.until(ExpectedConditions.visibilityOf(page.FileterField));
			}
			jse.executeScript("arguments[0].scrollIntoView(true);", page.TrailerLengthFileterButton);
			page.TrailerLengthFileterField.findElement(By.cssSelector("button[label='Unselect All']")).click();

			// check sub type filter

			for (int c = 0; c < 3; c++) {
				if (!page.FileterField.isDisplayed()) {
					page.FilterButton.click();
					wait.until(ExpectedConditions.visibilityOf(page.FileterField));
				}
				page.SubTypeFileterField.findElement(By.cssSelector("button[label='Unselect All']")).click();
				List<WebElement> AllLengthItem = page.SubTypeFileterField.findElements(By.tagName("yrc-checkbox"));
				int Ran = (int) (Math.random() * (AllLengthItem.size() - 1)) + 1;
				WebElement SelectItem = page.SubTypeFileterField
						.findElement(By.xpath(".//yrc-checkbox[" + Ran + "]//label"));
				jse.executeScript("arguments[0].scrollIntoView(true);", SelectItem);
				SelectItem.click();
				String Subtype = SelectItem.getText();
				System.out.println(Subtype);
				ty.add(Subtype);
				page.ApplyButton.click();
				Date d = CommonFunction.gettime("UTC");
				Thread.sleep(500);
				(new WebDriverWait(driver, 20))
						.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
				int CountStatus = page.IQStatusList.findElements(By.xpath("div")).size();
				for (int i = 1; i <= CountStatus; i++) {
					String StatusName = page.IQStatusList
							.findElement(By.xpath("div[" + i + "]//div[@class='trailer-inquiry-status']")).getText();
					jse.executeScript("arguments[0].scrollIntoView(false);",
							page.IQStatusList.findElement(By.xpath("div[" + i + "]//div[@class='panel-heading']")));
					page.IQStatusList.findElement(By.xpath("div[" + i + "]//div[@class='panel-heading']")).click();
					Thread.sleep(500);
					(new WebDriverWait(driver, 20))
							.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
					WebElement TrailerGrid = page.IQStatusList.findElement(
							By.xpath("//div[@class='ui-grid-contents-wrapper']/div[3]//div[@class='ui-grid-canvas']"));
					(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(TrailerGrid));

					// (new WebDriverWait(driver,
					// 20)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']//div[@class='ui-grid-canvas']/div")));

					int TrailerLine = TrailerGrid.findElements(By.xpath("div[@class='ui-grid-row']")).size();
					List<ArrayList<String>> TrailerInformation = new ArrayList<ArrayList<String>>();
					for (int j = 1; j <= TrailerLine; j++) {
						String[] OneTrailerInoformation = TrailerGrid
								.findElements(By.xpath("div[@class='ui-grid-row']")).get(j - 1).getText().split("\\n");
						ArrayList<String> e = new ArrayList<String>(Arrays.asList(OneTrailerInoformation));
						TrailerInformation.add(e);
					}
					LinkedHashSet<ArrayList<String>> ExpectedTrailerInformation = DataForInQuiryScreen
							.GetTrailerInformationByFilter(terminal, StatusName, d, leng, ty);
					Sassert.assertEquals(TrailerInformation, ExpectedTrailerInformation,
							"  " + StatusName + "   " + Subtype);
					page.IQStatusList.findElement(By.xpath("div[" + i + "]//div[@class='panel-heading']")).click();

				}
				leng.clear();
				ty.clear();
			}
			if (!page.FileterField.isDisplayed()) {
				page.FilterButton.click();
				wait.until(ExpectedConditions.visibilityOf(page.FileterField));
			}
			page.SubTypeFileterField.findElement(By.cssSelector("button[label='Unselect All']")).click();
		} else {
			(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(
					By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']")));
		}
		Sassert.assertAll();
	}

	@AfterMethod()
	public void RefreshScreen(ITestResult result) throws InterruptedException {
		if (result.getStatus() == ITestResult.FAILURE)
			driver.navigate().refresh();
	}

	@AfterClass
	public void Close() {
		driver.close();
	}

}
