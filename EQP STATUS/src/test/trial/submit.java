package test.trial;

import java.awt.AWTException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Page.CommonFunction;
import Page.DataCommon;
import Page.EqpStatusPageS;
import UADtesting.DataForUADTesting;

public class submit {

	 private WebDriver driver;
	 private EqpStatusPageS page;
	 private Actions builder;
@BeforeClass
@Parameters({"browser"})
public void SetUp(@Optional("chrome")String browser) throws AWTException, InterruptedException { 
	  if (browser.equalsIgnoreCase("chrome")){
	  System.setProperty("webdriver.chrome.driver", "C:\\Users\\uyr27b0\\Desktop\\selenium\\selenium//chromedriver.exe");
	  driver = new ChromeDriver();            
	  }else if(browser.equalsIgnoreCase("ie")){
	  System.setProperty("webdriver.ie.driver", "C:\\Users\\uyr27b0\\Desktop\\selenium\\selenium\\ie32\\IEDriverServer.exe");
	  driver=new InternetExplorerDriver();
	  }
 //driver=new FirefoxDriver();
  page=new EqpStatusPageS(driver);
  driver.get(page.sit1);
  driver.manage().window().maximize();
  page.SetStatus("UAD");
  builder= new Actions(driver);
	  }
	@Test(priority=1,dataProvider = "UADscreen",dataProviderClass=DataForUADTesting.class)
	   public void SetONHToUAD(String terminalcd,String SCAC,String TrailerNB, Date MRSts) throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
	  	SoftAssert SA= new SoftAssert();
	  	Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
				//Wait for the condition
			 .withTimeout(30, TimeUnit.SECONDS) 
			         // which to check for the condition with interval of 5 seconds. 
			       .pollingEvery(5, TimeUnit.SECONDS) 
			     //Which will ignore the NoSuchElementException
			       .ignoring(NoSuchElementException.class);
	 //wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(""));
	  	page.SetLocation(terminalcd);
	  	page.EnterTrailer(SCAC,TrailerNB);
	  	Date CurrentTime=CommonFunction.gettime("UTC");
		Date LocalTime = null;
		page.SetDatePicker2( page.GetDatePickerTime(),-10,0); 
		Date AlterTime=CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());	 	
		ArrayList<Object> Eqp= DataCommon.CheckEquipment(SCAC, TrailerNB);	
		System.out.println(page.SubmitButton.getText());
		System.out.println(page.SubmitButton.getAttribute("name"));
	
	    
	  	page.SubmitButton.click();
	  	Date d=CommonFunction.gettime("UTC");
	  	//(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
	  	(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
	 	(new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
	 	(new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
	 	// check eqps
	     ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
	 	SA.assertEquals(NewEqpStatusRecord.get(0), "UAD","Equipment_Status_Type_CD is wrong");
	 	SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd,"Statusing_Facility_CD is wrong");
	 	SA.assertEquals(NewEqpStatusRecord.get(3), "UAD","Source_Create_ID is wrong");
	 	SA.assertEquals(NewEqpStatusRecord.get(16), page.AD_ID,"modify_id is wrong");
	     SA.assertEquals(NewEqpStatusRecord.get(17), page.M_ID,"eqps Mainframe_User_ID is wrong");
	 	for (int i=5;i<=8;i++){
			  Date TS=CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));	
			  if(i==7){
			  SA.assertTrue(Math.abs(TS.getTime()-AlterTime.getTime())<60000,"equipment_status_ts "+"  "+TS+"  "+AlterTime);
			  }else{
			  SA.assertTrue(Math.abs(TS.getTime()-d.getTime())<120000,i+"  "+TS+"  "+d);}}	 
	 	
	 	ArrayList<Object> NewEqp= DataCommon.CheckEquipment(SCAC, TrailerNB);
	 	SA.assertEquals(NewEqp.get(0), Eqp.get(0)," eqp Mainframe_User_ID is wrong");
	  	SA.assertAll();
	    }		   

	


}
