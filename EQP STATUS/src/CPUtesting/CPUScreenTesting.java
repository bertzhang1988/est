package CPUtesting;

import java.awt.AWTException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.testng.asserts.SoftAssert;


import BORtesting.DataForBORScreenTesting;
import CLtesting.DataForCLScreenTesting;
import Data.DataForUS461AndUS465AndUS457;
import MTYtesting.DataForMTYScreenTesting;
import Page.CommonFunction;
import Page.DataCommon;
import Page.EqpStatusPageS;

public class CPUScreenTesting {
	
	 private WebDriver driver;
	 private EqpStatusPageS page;
	 
 @BeforeClass
 @Parameters({"browser"})
 public void SetUp(@Optional("Chrome")String browser) throws AWTException, InterruptedException { 
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
   page.SetStatus("CPU");
   Actions builder= new Actions(driver);
   
	  }	
  
  @Test(priority=1,dataProvider = "CPUScreen",dataProviderClass=DataForCPUScreenTesting.class)
  public void SetTrailerToMtyfromONH(String terminalcd,String SCAC,String TrailerNB,String Desti,String Cube,String AmountPro,String AmountWeight, Date MRSts) throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
	  SoftAssert SA= new SoftAssert();
	  page.SetLocation(terminalcd);
	  page.EnterTrailer(SCAC,TrailerNB); 
	  Date CurrentTime=CommonFunction.gettime("UTC");
	  Date LocalTime = null;


	  //check date&time field should a. eqpst>current-time use eqpst  minute+1 b. eqpst<current time use current time
	if(MRSts.before(CurrentTime)){
		LocalTime= CommonFunction.getLocalTime(terminalcd, CurrentTime);
	}else if(MRSts.after(CurrentTime)){
		LocalTime=CommonFunction.getLocalTime(terminalcd, MRSts);
		}
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
	cal.setTime(LocalTime); 
	int hourOfDay = cal.get(Calendar.HOUR_OF_DAY); // 24 hour clock
	String hour=String.format("%02d",hourOfDay);
	int minute = cal.get(Calendar.MINUTE);
	String Minute=String.format("%02d",minute);
	String MinutePlusOne=String.format("%02d",minute+1);
	String DATE = dateFormat.format(cal.getTime());
    SA.assertEquals(page.DateInput.getAttribute("value"), DATE,"TIME DARE IS WRONG");
	SA.assertEquals(page.HourInput.getAttribute("value"),hour,"TIME HOR IS WRONG");
	if(MRSts.after(CurrentTime)){
	SA.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne,"TIME MINUTE IS WRONG"); 
	}else{
	SA.assertEquals(page.MinuteInput.getAttribute("value"), Minute,"TIME MINUTE IS WRONG");}
	//alter time
	page.SetDatePicker2( page.GetDatePickerTime(),0,59); 
	Date AlterTime=CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());		 
	ArrayList<Object> Eqp= DataCommon.CheckEquipment(SCAC, TrailerNB);	 
	  page.SubmitButton.click();
	  Date d=CommonFunction.gettime("UTC");
	  (new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
	  (new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
	  (new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
	  // check eqps
	  ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
      SA.assertEquals(NewEqpStatusRecord.get(0), "CPU","Equipment_Status_Type_CD is wrong");
	  SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd,"Statusing_Facility_CD is wrong");
      SA.assertEquals(NewEqpStatusRecord.get(3), "CPU","Source_Create_ID is wrong");
      SA.assertEquals(NewEqpStatusRecord.get(16), page.AD_ID,"modify_id is wrong");
      SA.assertEquals(NewEqpStatusRecord.get(17), page.M_ID,"eqps Mainframe_User_ID is wrong");
      SA.assertEquals(NewEqpStatusRecord.get(18), "ONH","eqps M204 equipment_status_type_cd is wrong");
		for (int i=5;i<=8;i++){
			  Date TS=CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));	
			  if(i==7){
			  SA.assertTrue(Math.abs(TS.getTime()-AlterTime.getTime())<60000,"equipment_status_ts "+"  "+TS+"  "+AlterTime);
			  }else{
			  SA.assertTrue(Math.abs(TS.getTime()-d.getTime())<120000,i+"  "+TS+"  "+d);}}	 
	  // check eqp
	  ArrayList<Object> NewEqp= DataCommon.CheckEquipment(SCAC, TrailerNB);
	  SA.assertEquals(NewEqp.get(0), Eqp.get(0)," eqp Mainframe_User_ID is wrong");
	  SA.assertAll();
  }
 
  @Test(priority=2,dataProvider = "CPUScreen",dataProviderClass=DataForCPUScreenTesting.class)
  public void SmartEneterSetTrailerToMtyfromONH(String terminalcd,String SCAC,String TrailerNB,String Desti,String Cube,String AmountPro,String AmountWeight, Date MRSts) throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
	  SoftAssert SA= new SoftAssert();
	  Actions builder= new Actions(driver);
	  page.SetLocation(terminalcd);
	  page.EnterTrailer(SCAC,TrailerNB); 
	  //alter time
	  page.SetDatePicker2( page.GetDatePickerTime(),0,-59); 
	  Date AlterTime=CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());		  
	  builder.sendKeys( Keys.ENTER).build().perform();
	  
	  Date d=CommonFunction.gettime("UTC");
	  (new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
	  (new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
	  (new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
	  // check eqps
	  ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
      SA.assertEquals(NewEqpStatusRecord.get(0), "CPU","Equipment_Status_Type_CD is wrong");
	  SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd,"Statusing_Facility_CD is wrong");
      SA.assertEquals(NewEqpStatusRecord.get(3), "CPU","Source_Create_ID is wrong");
		for (int i=5;i<=8;i++){
			  Date TS=CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));	
			  if(i==7){
			  SA.assertTrue(Math.abs(TS.getTime()-AlterTime.getTime())<60000,"equipment_status_ts "+"  "+TS+"  "+AlterTime);
			  }else{
			  SA.assertTrue(Math.abs(TS.getTime()-d.getTime())<120000,i+"  "+TS+"  "+d);}}	 
	  SA.assertAll();
  }
	
  @Test(priority=3,dataProvider = "CPUScreen",dataProviderClass=DataForCPUScreenTesting.class)
  public void SetTrailerToMtyNOTfromONH(String terminalcd,String SCAC,String TrailerNB,String Desti,String Cube,String AmountPro,String AmountWeight, Date MRSts) throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
	  SoftAssert SA= new SoftAssert();
	  page.SetLocation(terminalcd);
	  page.EnterTrailer(SCAC,TrailerNB); 
	  Date CurrentTime=CommonFunction.gettime("UTC");
	  Date LocalTime = null;


	  //check date&time field should a. eqpst>current-time use eqpst  minute+1 b. eqpst<current time use current time
	if(MRSts.before(CurrentTime)){
		LocalTime= CommonFunction.getLocalTime(terminalcd, CurrentTime);
	}else if(MRSts.after(CurrentTime)){
		LocalTime=CommonFunction.getLocalTime(terminalcd, MRSts);
		}
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
	cal.setTime(LocalTime); 
	int hourOfDay = cal.get(Calendar.HOUR_OF_DAY); // 24 hour clock
	String hour=String.format("%02d",hourOfDay);
	int minute = cal.get(Calendar.MINUTE);
	String Minute=String.format("%02d",minute);
	String MinutePlusOne=String.format("%02d",minute+1);
	String DATE = dateFormat.format(cal.getTime());
    SA.assertEquals(page.DateInput.getAttribute("value"), DATE,"TIME DARE IS WRONG");
	SA.assertEquals(page.HourInput.getAttribute("value"),hour,"TIME HOR IS WRONG");
	if(MRSts.after(CurrentTime)){
	SA.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne,"TIME MINUTE IS WRONG"); 
	}else{
	SA.assertEquals(page.MinuteInput.getAttribute("value"), Minute,"TIME MINUTE IS WRONG");}
	//alter time
	page.SetDatePicker2( page.GetDatePickerTime(),0,59); 
	Date AlterTime=CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());	
	  ArrayList<Object> Eqp= DataCommon.CheckEquipment(SCAC, TrailerNB);	 
	  page.SubmitButton.click();
	  Date d=CommonFunction.gettime("UTC");
	  (new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
	  (new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
	  (new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
	  // check eqps
	  ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
      SA.assertEquals(NewEqpStatusRecord.get(0), "CPU","Equipment_Status_Type_CD is wrong");
	  SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd,"Statusing_Facility_CD is wrong");
      SA.assertEquals(NewEqpStatusRecord.get(3), "CPU","Source_Create_ID is wrong");
      SA.assertEquals(NewEqpStatusRecord.get(16), page.AD_ID,"modify_id is wrong");
      SA.assertEquals(NewEqpStatusRecord.get(17), page.M_ID,"eqps Mainframe_User_ID is wrong");
      SA.assertEquals(NewEqpStatusRecord.get(18), "ONH","eqps M204 equipment_status_type_cd is wrong");
		for (int i=5;i<=8;i++){
			  Date TS=CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));	
			  if(i==7){
			  SA.assertTrue(Math.abs(TS.getTime()-AlterTime.getTime())<60000,"equipment_status_ts "+"  "+TS+"  "+AlterTime);
			  }else{
			  SA.assertTrue(Math.abs(TS.getTime()-d.getTime())<120000,i+"  "+TS+"  "+d);}}	 
	  // check eqp
	  ArrayList<Object> NewEqp= DataCommon.CheckEquipment(SCAC, TrailerNB);
	  SA.assertEquals(NewEqp.get(0), page.M_ID," eqp Mainframe_User_ID is wrong");
	  SA.assertAll();
  }
 
  @Test(priority=4,dataProvider = "CPUScreen",dataProviderClass=DataForCPUScreenTesting.class)
  public void SmartEneterSetTrailerToMtyNOTfromONH(String terminalcd,String SCAC,String TrailerNB,String Desti,String Cube,String AmountPro,String AmountWeight, Date MRSts) throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
	  SoftAssert SA= new SoftAssert();
	  Actions builder= new Actions(driver);
	  page.SetLocation(terminalcd);
	  page.EnterTrailer(SCAC,TrailerNB); 
	  //alter time
	  page.SetDatePicker2( page.GetDatePickerTime(),0,-59); 
      Date AlterTime=CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());	
	  builder.sendKeys( Keys.ENTER).build().perform();
	  
	  Date d=CommonFunction.gettime("UTC");
	  (new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
	  (new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
	  (new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
	  // check eqps
	  ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
      SA.assertEquals(NewEqpStatusRecord.get(0), "CPU","Equipment_Status_Type_CD is wrong");
	  SA.assertEquals(NewEqpStatusRecord.get(1), terminalcd,"Statusing_Facility_CD is wrong");
      SA.assertEquals(NewEqpStatusRecord.get(3), "CPU","Source_Create_ID is wrong");
		for (int i=5;i<=8;i++){
			  Date TS=CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));	
			  if(i==7){
			  SA.assertTrue(Math.abs(TS.getTime()-AlterTime.getTime())<60000,"equipment_status_ts "+"  "+TS+"  "+AlterTime);
			  }else{
			  SA.assertTrue(Math.abs(TS.getTime()-d.getTime())<120000,i+"  "+TS+"  "+d);}}	 
	  SA.assertAll();
  }
  
  
  @AfterClass
	  public void TearDown() {
		  driver.quit();
	  }
}
