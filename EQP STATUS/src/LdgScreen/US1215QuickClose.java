package LdgScreen;

import java.awt.AWTException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Random;



import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Data.DataForUS1215;
import Data.DataForUS200091;
import Page.CommonFunction;
import Page.DataCommon;
import Page.EqpStatusPageS;

public class US1215QuickClose {
	private WebDriver driver;
	private EqpStatusPageS page;
	private Actions builder;

@BeforeClass( groups = { "ldg uc" })
@Parameters({"browser"})
public void SetUp(@Optional("ie")String browser) throws AWTException, InterruptedException { 
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
     page.SetStatus("ldg");
     builder=new Actions(driver);
	
	  }

	  @AfterClass( groups = { "ldg uc" })
	  public void Close() {
		  driver.close();
	  }
	 
	  
@Test(priority=1,dataProvider = "12.15",dataProviderClass=DataForUS1215.class,description="ldg trailer with pro quick close", groups = { "ldg uc" })
public void LDGTrailerWithPROQuickClose(String terminal,String SCAC,String TrailerNB,String Desti,String AmountPro,String AmountWeight,String Cube, String seal,String hldesti, String hlcube, Date MReqpst) throws AWTException, InterruptedException, ClassNotFoundException, SQLException{	
   SoftAssert SA= new SoftAssert();
   page.SetLocation(terminal);	 
   page.EnterTrailer(SCAC,TrailerNB);	   
   
   //enter cube if it is empty
   if(page.CubeField.getAttribute("value").equalsIgnoreCase("")){
   int Ran=(int)(Math.random()*99)+1;
   String NewCube=Integer.toString(Ran);
   String cube=NewCube;
   page.SetCube(cube);}
   
   //click submit and close out button
   page.SubmitAndCloseOutButton.click();

   // navigate to quick close screen
   (new WebDriverWait(driver, 20)).until(ExpectedConditions.textToBePresentInElement(page.Title, "Set Trailer Status to Closed"));
   (new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
   Date CurrentTime=CommonFunction.gettime("UTC");
   Date LocalTime = null;
  
   
   // check quick close screen
   SA.assertEquals(page.qcDestination.getAttribute("value"), Desti,"quick close screen destination is wrong");
   SA.assertEquals(page.qcShipmentCount.getAttribute("value"), AmountPro,"quick close screen shipment count is wrong");
   SA.assertEquals(page.qcShipmentWeight.getAttribute("value").replaceAll("_", ""), AmountWeight,"quick close screen shipment weight is wtong");
   SA.assertEquals(page.qcEnrCubeField.getAttribute("value"), Cube,"quick close screen cube is wrong");



    //check date&time field should 
   if(MReqpst.before(CurrentTime)){
		 LocalTime= CommonFunction.getLocalTime(terminal, CurrentTime);
	   }else if(MReqpst.after(CurrentTime)){
		 LocalTime=CommonFunction.getLocalTime(terminal, MReqpst);
	   }

	 Calendar cal1 = Calendar.getInstance();
	 SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/YYYY");
	 cal1.setTime(LocalTime); 
	 int hourOfDay1 = cal1.get(Calendar.HOUR_OF_DAY); // 24 hour clock
	 String hour1=String.format("%02d",hourOfDay1);
	 int minute1 = cal1.get(Calendar.MINUTE);
	 String Minute1=String.format("%02d",minute1);
	 String MinutePlusOne1=String.format("%02d",minute1+1);
	 String DATE1 = dateFormat1.format(cal1.getTime());
	 SA.assertEquals(page.DateInput.getAttribute("value"), DATE1, " qc screen date time is wrong");
	 SA.assertEquals(page.HourInput.getAttribute("value"),hour1,"qc screen hour is wrong");
	 if(MReqpst.after(CurrentTime)){
	 SA.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne1,"qc screen minute is wrong"); 
	 }else{
	 SA.assertEquals(page.MinuteInput.getAttribute("value"), Minute1,"qc screen minute is wrong");}
 	 // check pro grid
 	LinkedHashSet<ArrayList<String>> ProInfo=page.GetProList(page.ProListForm);
 	SA.assertEquals(ProInfo,DataCommon.GetProList(SCAC, TrailerNB)," quick close screen prolist information is wrong");		 
 
 	 //enter seal if it is empty
   int Ran2=(int)(Math.random()*999998999)+1000;
   String NewSeal=Integer.toString(Ran2);
   if(page.qcSealField.getAttribute("value").equalsIgnoreCase("__________")){
   page.SetSealQC(NewSeal);}
   page.qcCloseTrailerButton.click();
   Date d=CommonFunction.gettime("UTC");
   (new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.Title, "Set Trailer Status Loading"));
   (new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
   (new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
   (new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
   (new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
   (new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
	// check eqps 
   ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
   SA.assertEquals(NewEqpStatusRecord.get(0), "LDD","Equipment_Status_Type_CD is wrong");
   SA.assertEquals(NewEqpStatusRecord.get(1), terminal,"Statusing_Facility_CD is wrong");
   SA.assertEquals(NewEqpStatusRecord.get(11), NewSeal,"SEAL_NB is wrong");
   SA.assertEquals(NewEqpStatusRecord.get(3), "LH.LDD","Source_Create_ID is wrong");
   SA.assertEquals(NewEqpStatusRecord.get(4), Cube,"Actual_Capacity_Consumed_PC is wrong");
   
   for (int i=5;i<=8;i++){
   Date TS=CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));	
   SA.assertTrue(Math.abs(TS.getTime()-d.getTime())<120000,i+"  "+TS+"  "+d);}
   SA.assertAll();
}

@Test(priority=2,dataProvider = "12.15",dataProviderClass=DataForUS1215.class,description="non ldg without pro, add pro through quick close", groups = { "ldg uc" })
public void NONLDGTrailerWithoutPROAddPRO(String terminalcd,String SCAC,String TrailerNB,String Desti,String AmountPro,String AmountWeight,String Cube, String seal,String hldesti, String hlcube, Date MReqpst) throws AWTException, InterruptedException, ClassNotFoundException, SQLException{	
	SoftAssert  SAssert= new SoftAssert(); 	
    page.SetLocation(terminalcd);
 	page.EnterTrailer(SCAC,TrailerNB);
 	Date CurrentTime=CommonFunction.gettime("UTC");
 	Date LocalTime = null;
 	
 	
	//check date&time field should a. eqpst>current-time use eqpst  minute+1 b. eqpst<current time use current time
	if(MReqpst.before(CurrentTime)){
		 LocalTime= CommonFunction.getLocalTime(terminalcd, CurrentTime);
	 }else if(MReqpst.after(CurrentTime)){
		 LocalTime=CommonFunction.getLocalTime(terminalcd, MReqpst);
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
	 
	SAssert.assertEquals(page.DateInput.getAttribute("value"), DATE," ldg screen TIME DARE IS WRONG");
	SAssert.assertEquals(page.HourInput.getAttribute("value"),hour,"ldg screen TIME HOR IS WRONG");
	if(MReqpst.after(CurrentTime)){
	SAssert.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne,"ldg scren TIME MINUTE IS WRONG"); 
	}else{
	SAssert.assertEquals(page.MinuteInput.getAttribute("value"), Minute," ldg screen TIME MINUTE IS WRONG");}
	 //ENTER DESTINATION
	String[] dest={"270","112","841","198","135"};
	int ran =new Random().nextInt(dest.length);
	String changeDesti=dest[ran];
	page.SetDestination(changeDesti);
	 
	 // add pro
 	ArrayList<String> GetProNotOnAnyTrailer= DataCommon.GetProNotInAnyTrailer();
 	ArrayList<String> ADDPRO=new ArrayList<String>();
 	page.RemoveProButton.click();
 	 for(int j=0;j<1;j++){
 	String CurrentPro=GetProNotOnAnyTrailer.get(j);
 	page.EnterPro(CurrentPro);
 	ADDPRO.add(CurrentPro);
 	 }
 	
 	// change cube	
	 int Ran=(int)(Math.random()*99)+1;
	 String NewCube=Integer.toString(Ran);
	 page.SetCube(NewCube);
 	 
   //click submit and close out button
   page.SubmitAndCloseOutButton.click();
   Date d=CommonFunction.gettime("UTC");
   // navigate to quick close screen
   (new WebDriverWait(driver, 20)).until(ExpectedConditions.textToBePresentInElement(page.Title, "Set Trailer Status to Closed"));
   (new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

   
   Date CurrentTime2=CommonFunction.gettime("UTC");
   Date LocalTime1 = d;
   // check eqps 
   ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
   SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG","Equipment_Status_Type_CD is wrong");
   SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd,"Statusing_Facility_CD is wrong");
   SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG","Source_Create_ID is wrong");
   SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube,"Actual_Capacity_Consumed_PC is wrong");
   
   for (int i=5;i<=8;i++){
   Date TS=CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));	
   SAssert.assertTrue(Math.abs(TS.getTime()-d.getTime())<120000,i+"  "+TS+"  "+d);} 
   
   // CHECK WAYBILL TABLE (load new pro)
   for(String pro: ADDPRO){
   ArrayList<Object> CheckWaybillRecord= DataCommon.GetWaybillInformationOfPro(pro);
   SAssert.assertEquals(CheckWaybillRecord.get(0), SCAC,"waybill SCAC is wrong");
   SAssert.assertEquals(CheckWaybillRecord.get(1),TrailerNB ,"waybill trailernb is wrong");
   SAssert.assertEquals(CheckWaybillRecord.get(17), SCAC,"waybill  toSCAC is wrong");
   SAssert.assertEquals(CheckWaybillRecord.get(13),terminalcd ,"waybill from terminal is wrong");
   SAssert.assertEquals(CheckWaybillRecord.get(18),TrailerNB ,"waybill totrailernb is wrong");
   SAssert.assertEquals(CheckWaybillRecord.get(20),"LOADING" ,"waybill TRANSACTION TYPE is wrong");
   Date System_Modify_TS=CommonFunction.SETtime((Date) CheckWaybillRecord.get(9));	
   SAssert.assertTrue(Math.abs(System_Modify_TS.getTime()-d.getTime())<120000," waybill table System_Modify_TS "+System_Modify_TS+"  "+d);
   Date Waybill_Transaction_End_TS=CommonFunction.SETtime((Date) CheckWaybillRecord.get(11));	
   SAssert.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime()-d.getTime())<120000," waybill table Waybill_Transaction_End_TS "+System_Modify_TS+"  "+d);
   }
   
  

   // check prepopulate quick close screen
   SAssert.assertEquals(page.TerminalField.getAttribute("value"),terminalcd ,"quick close screen terminal is wrong");
   SAssert.assertEquals(page.TrailerField.getText(), page.SCACTrailer(SCAC, TrailerNB),"quick close screen trailer input is wrong");
   SAssert.assertEquals(page.qcDestination.getAttribute("value"), changeDesti,"quick close screen destination is wrong");
  //SAssert.assertEquals(page.qcShipmentCount.getAttribute("value"), AmountPro,"quick close screen shipment count is wrong");
  // SAssert.assertEquals(page.qcShipmentWeight.getAttribute("value").replaceAll("_", ""), AmountWeight,"quick close screen shipment weight is wtong");
   SAssert.assertEquals(page.qcEnrCubeField.getAttribute("value"), NewCube,"quick close screen cube is wrong");
   
   
    //check date&time field should a. eqpst>current-time use eqpst  minute+1 b. eqpst<current time use current time,  set ldg to ldd
   MReqpst=(Date) NewEqpStatusRecord.get(7);
 	 if(MReqpst.before(CurrentTime2)){
 		 LocalTime1= CommonFunction.getLocalTime(terminalcd, CurrentTime2);
 	   }else if(MReqpst.after(CurrentTime2)){
 		 LocalTime1=CommonFunction.getLocalTime(terminalcd, MReqpst);
 	   }

 	 Calendar cal1 = Calendar.getInstance();
 	 SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/YYYY");
 	 cal1.setTime(LocalTime1); 
 	 int hourOfDay1 = cal1.get(Calendar.HOUR_OF_DAY); // 24 hour clock
 	 String hour1=String.format("%02d",hourOfDay1);
 	 int minute1 = cal1.get(Calendar.MINUTE);
 	 String Minute1=String.format("%02d",minute1);
 	 String MinutePlusOne1=String.format("%02d",minute1+1);
 	 String DATE1 = dateFormat1.format(cal1.getTime());
 	 SAssert.assertEquals(page.DateInput.getAttribute("value"), DATE1, " qc screen date time is wrong");
 	 SAssert.assertEquals(page.HourInput.getAttribute("value"),hour1,"qc screen hour is wrong");
 	 if(MReqpst.after(CurrentTime)){
 	 SAssert.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne1,"qc screen minute is wrong"); 
 	 }else{
 	 SAssert.assertEquals(page.MinuteInput.getAttribute("value"), Minute1,"qc screen minute is wrong");}
   
 	 // CHECK PRO GRID IN QC SCREEN
 	LinkedHashSet<ArrayList<String>> ProInfo=page.GetProList(page.ProListForm);
 	SAssert.assertEquals(ProInfo,DataCommon.GetProList(SCAC, TrailerNB)," quick close screen prolist information is wrong");		 
 	 
 	 //enter seal if it is empty
   int Ran2=(int)(Math.random()*999998999)+1000;
   String NewSeal=Integer.toString(Ran2);
   if(page.qcSealField.getAttribute("value").replaceAll("_", "").equalsIgnoreCase("")){
   page.SetSealQC(NewSeal);}
   page.qcCloseTrailerButton.click();
   Date d1=CommonFunction.gettime("UTC");
   (new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.Title, "Set Trailer Status Loading"));
   (new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
   (new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
   (new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
   (new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
   (new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
   ArrayList<Object> NewEqpStatusRecord1= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
   SAssert.assertEquals(NewEqpStatusRecord1.get(0), "LDD","Equipment_Status_Type_CD is wrong");
   SAssert.assertEquals(NewEqpStatusRecord1.get(1), terminalcd,"Statusing_Facility_CD is wrong");
   SAssert.assertEquals(NewEqpStatusRecord1.get(11), NewSeal,"SEAL_NB is wrong");
   SAssert.assertEquals(NewEqpStatusRecord1.get(3), "LH.LDD","Source_Create_ID is wrong");
   SAssert.assertEquals(NewEqpStatusRecord1.get(4), NewCube,"Actual_Capacity_Consumed_PC is wrong");
   
   for (int i=5;i<=8;i++){
   Date TS=CommonFunction.SETtime((Date) NewEqpStatusRecord1.get(i));	
   SAssert.assertTrue(Math.abs(TS.getTime()-d1.getTime())<120000,i+"  "+TS+"  "+d1);}
   SAssert.assertAll();
}	

}
