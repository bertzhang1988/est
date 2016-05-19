package trial2;

import java.awt.AWTException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Page.EqpStatusPageS;

public class TestExecute {

	@Test
	public void TestExecution() throws IOException, AWTException, InterruptedException {
		File file = new File("C:\\Users\\uyr27b0\\Desktop\\selenium\\trial2.xlsx");
		FileInputStream inputStream = new FileInputStream(file);
	    XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
	    XSSFSheet sheet1 = workbook.getSheetAt(0);
	    int rownumber=sheet1.getPhysicalNumberOfRows();
	    for (int i=1;i<rownumber;i++)
	    {
	    	Cell ExecutionCell=sheet1.getRow(i).getCell(1);
	    	String ExecutionMark=ExecutionCell.getStringCellValue();
	    	if(ExecutionMark.equalsIgnoreCase("y")){
	    	String TcName=sheet1.getRow(i).getCell(0).getStringCellValue();
	    	if(TcName.equalsIgnoreCase("VerifyValidTerminal")){
	    		TerminalValidation.verifyValidTerminal();	
	    	}else if(TcName.equalsIgnoreCase("VerifyInvalidTerminal")){
	    	TerminalValidation.verifyInvalidTerminal();}
	    		
	    	}
	    	
	    }
	}

}
