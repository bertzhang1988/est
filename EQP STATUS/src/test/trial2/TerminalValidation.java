package test.trial2;

import java.awt.AWTException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;

import Function.ConfigRd;
import Page.EqpStatusPageS;

public class TerminalValidation {
	private static WebDriver driver;
	private static EqpStatusPageS page;
	private static Actions builder;

	public static void verifyInvalidTerminal() throws IOException, AWTException, InterruptedException {
		ConfigRd Conf = new ConfigRd();
		System.setProperty("webdriver.chrome.driver",
				"C:\\Users\\uyr27b0\\Desktop\\selenium\\selenium//chromedriver.exe");
		driver = new ChromeDriver();
		page = new EqpStatusPageS(driver);
		driver.get(Conf.GetURL());
		driver.manage().window().maximize();
		builder = new Actions(driver);
		page.SetStatus("ldg");
		builder = new Actions(driver);
		File file = new File("C:\\Users\\uyr27b0\\Desktop\\selenium\\trial1.xlsx");
		FileInputStream inputStream = new FileInputStream(file);
		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		XSSFSheet sheet1 = workbook.getSheetAt(0);
		int rownumber = sheet1.getPhysicalNumberOfRows();
		for (int i = 1; i < rownumber; i++) {
			Cell TerminalNo = sheet1.getRow(i).getCell(1);
			if (TerminalNo == null)
				break;
			String terminal = TerminalNo.getStringCellValue();
			page.SetLocation(terminal);
		}
		workbook.close();
		driver.close();
	}

	public static void verifyValidTerminal() throws IOException, AWTException, InterruptedException {
		ConfigRd Conf = new ConfigRd();
		System.setProperty("webdriver.chrome.driver",
				"C:\\Users\\uyr27b0\\Desktop\\selenium\\selenium//chromedriver.exe");
		driver = new ChromeDriver();
		page = new EqpStatusPageS(driver);
		driver.get(Conf.GetURL());
		driver.manage().window().maximize();
		builder = new Actions(driver);
		page.SetStatus("ldg");
		builder = new Actions(driver);
		File file = new File("C:\\Users\\uyr27b0\\Desktop\\selenium\\trial1.xlsx");
		FileInputStream inputStream = new FileInputStream(file);
		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		XSSFSheet sheet1 = workbook.getSheetAt(0);
		int rownumber = sheet1.getPhysicalNumberOfRows();
		for (int i = 1; i < rownumber; i++) {
			Cell TerminalNo = sheet1.getRow(i).getCell(0);
			if (TerminalNo == null)
				break;
			String terminal = TerminalNo.getStringCellValue();
			page.SetLocation(terminal);
		}

		workbook.close();
		driver.close();
	}

	@Test
	public void f() {
	}
}
