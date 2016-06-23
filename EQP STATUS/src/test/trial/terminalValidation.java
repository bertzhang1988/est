package test.trial;

import java.awt.AWTException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Function.ConfigRd;
import Page.EqpStatusPageS;

public class terminalValidation {
	private WebDriver driver;
	private EqpStatusPageS page;
	private Actions builder;

	@BeforeClass
	@Parameters({ "browser", "status" })
	public void SetUp(@Optional("chrome") String browser, @Optional("ldg") String status)
			throws AWTException, InterruptedException {
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
		builder = new Actions(driver);
		page.SetStatus(status);
		builder = new Actions(driver);
	}

	// @Test
	public void verifyInvalidTerminal() throws IOException, AWTException, InterruptedException {
		File file = new File("C:\\Users\\uyr27b0\\Desktop\\selenium\\trial1.xlsx");
		FileInputStream inputStream = new FileInputStream(file);
		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		XSSFSheet sheet1 = workbook.getSheetAt(0);
		int rownumber = sheet1.getLastRowNum();
		for (int i = 1; i < rownumber; i++) {
			Iterator<Cell> cellIterator = sheet1.getRow(i).cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				String terminal = cell.getStringCellValue();
				page.SetLocation(terminal);
			}
		}
		workbook.close();

	}

	@Test
	public void verifyInvalidTerminal2() throws IOException, AWTException, InterruptedException {
		File file = new File("C:\\Users\\uyr27b0\\Desktop\\selenium\\trial1.xlsx");
		FileInputStream inputStream = new FileInputStream(file);
		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		XSSFSheet sheet1 = workbook.getSheetAt(0);
		int rownumber = sheet1.getLastRowNum();
		for (int i = 1; i < rownumber; i++) {
			Iterator<Cell> cellIterator = sheet1.getRow(i).cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				String terminal = cell.getStringCellValue();
				page.SetLocation(terminal);
			}
		}
		workbook.close();

	}

	// @Test(priority=2,dataProvider="2000.41",dataProviderClass=DataForUS200001AndUS200002AndUS200041.class)
	public void VerifyInvalidTerminal(String terminalcd) throws AWTException, InterruptedException {
		page.SetLocation(terminalcd);
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,
				"The terminal number is invalid. Please enter another terminal number."));

	}
}
