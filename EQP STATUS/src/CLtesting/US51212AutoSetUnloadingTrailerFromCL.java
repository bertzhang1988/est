package CLtesting;

import java.awt.AWTException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Data.DataForUS51212;
import Page.CommonFunction;
import Page.DataCommon;
import Page.EqpStatusPageS;

public class US51212AutoSetUnloadingTrailerFromCL {
	private WebDriver driver;
	private EqpStatusPageS page;
	 
 @Test(priority=1,dataProvider = "512.12",dataProviderClass=DataForUS51212.class)
  public void PullProFromARVTrailerInSameTerminal(String terminalcd,String SCAC,String TrailerNB,String Desti) throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
   SoftAssert Sassert= new SoftAssert();  
   page.SetLocation(terminalcd);	 
   page.EnterTrailer(SCAC,TrailerNB);	
   //pick an arv trailer in same terminal
   ArrayList<String> PickedTrailer=DataForUS51212.GetTrailerOnSameTerminal(terminalcd,"arv");
   String fromSCAC=PickedTrailer.get(0);
   String fromTrailerNb=PickedTrailer.get(1);
   //get eqps information of the picked trailer
   ArrayList<Object> OldEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(fromSCAC, fromTrailerNb);
   // get pro from the picked trailer
   ArrayList<String> PROlist=DataCommon.GetProOnTrailer(fromSCAC, fromTrailerNb);
   
   page.RemoveProButton.click();
   //add pro
   ArrayList<String> ADDEDPRO= new  ArrayList<String>();
   for(int j=0;j<1;j++){	
	String currentPro=PROlist.get(j);
    page.EnterPro(currentPro);
    ADDEDPRO.add(currentPro);
   }
    Date AlterTime=CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
    
   // change cityroute
	page.SetCityRoute("Downtown");
	
	// set plan date
	page.PlanDate.sendKeys(CommonFunction.getTommorrow());
	page.SubmitButton.click();
	Date d=CommonFunction.gettime("UTC");
	(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
	(new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
	(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
	(new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
	(new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
	
    // eqps table
	ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(PickedTrailer.get(0), PickedTrailer.get(1));
	Sassert.assertEquals(NewEqpStatusRecord.get(0), "UAD","Equipment_Status_Type_CD is wrong");
	Sassert.assertEquals(NewEqpStatusRecord.get(1), OldEqpStatusRecord.get(1),"Statusing_Facility_CD is wrong");
	Sassert.assertEquals(NewEqpStatusRecord.get(3), "AUTUAD","Source_Create_ID is wrong");
	// orig and desti is keep
	Sassert.assertEquals(NewEqpStatusRecord.get(2), OldEqpStatusRecord.get(2),"equipment_dest_facility_cd is wrong");
	Sassert.assertEquals(NewEqpStatusRecord.get(15), OldEqpStatusRecord.get(15),"equipment_origin_facility_CD is wrong");
	 for (int i=5;i<=8;i++){
	 Date TS=CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));	
	 if(i==7){
	Sassert.assertTrue(Math.abs(TS.getTime()-AlterTime.getTime())<60000,"equipment_status_ts "+"  "+TS+"  "+AlterTime);
	 }else{
	Sassert.assertTrue(Math.abs(TS.getTime()-d.getTime())<120000,i+"  "+TS+"  "+d);}}
	
	for(String CurrentPro: ADDEDPRO){
	
	//check waybill
	ArrayList<Object> AfterADDWb=DataCommon.GetWaybillInformationOfPro(CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(0),SCAC,"Waybill table Standard_Carrier_Alpha_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(1),TrailerNB,"Waybill table Equipment_Unit_NB is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(3),"LH.LDG","Waybill table Source_Modify_ID is wrong "+CurrentPro);
	for (int i=9;i<=9;i++){
	Date f=CommonFunction.SETtime((Date) AfterADDWb.get(i));	
	Sassert.assertTrue(Math.abs(f.getTime()-d.getTime())<120000,"waybill table "+i+"  "+f+"  "+d+"  "+"   "+CurrentPro);		
	//loading record
    Sassert.assertEquals(AfterADDWb.get(14),null,"waybill table loading record To_Facility_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(13),terminalcd,"waybill table loading record From_Facility_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(17),SCAC,"waybill table loading record To_Standard_Carrier_Alpha_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(18),TrailerNB,"waybill table loading record To_Equipment_Unit_NB is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(15),null,"waybill table loading record From_Standard_Carrier_Alpha_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(16),null,"waybill table loading record From_Equipment_Unit_NB is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(20),"LOADING","waybill table loading Waybill_Transaction_Type_NM is wrong "+CurrentPro);
	Date f1=CommonFunction.SETtime((Date) AfterADDWb.get(11));	
	Sassert.assertTrue(Math.abs(f1.getTime()-d.getTime())<120000,"waybill Waybill_Transaction_End_TS   "+f1+"  "+d+"  "+"   "+CurrentPro); 
	}}
	Sassert.assertAll();  
  
  }

  
  
  @Test(priority=2,dataProvider = "512.12",dataProviderClass=DataForUS51212.class)
  public void PullProFromCPUTrailerInSameTerminal(String terminalcd,String SCAC,String TrailerNB,String Desti) throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
   SoftAssert Sassert= new SoftAssert();  
   page.SetLocation(terminalcd);	 
   page.EnterTrailer(SCAC,TrailerNB);	
   //pick an arv trailer in same terminal
   ArrayList<String> PickedTrailer=DataForUS51212.GetTrailerOnSameTerminal(terminalcd,"CPU");
   String fromSCAC=PickedTrailer.get(0);
   String fromTrailerNb=PickedTrailer.get(1);
   //get eqps information of the picked trailer
   ArrayList<Object> OldEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(fromSCAC, fromTrailerNb);
   // get pro from the picked trailer
   ArrayList<String> PROlist=DataCommon.GetProOnTrailer(fromSCAC, fromTrailerNb);
   
   page.RemoveProButton.click();
   //add pro
   ArrayList<String> ADDEDPRO= new  ArrayList<String>();
   for(int j=0;j<1;j++){	
	String currentPro=PROlist.get(j);
    page.EnterPro(currentPro);
    ADDEDPRO.add(currentPro); }
	
   Date AlterTime=CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
   
    // change cityroute
    page.SetCityRoute("Downtown");
	// set plan date
	page.PlanDate.sendKeys(CommonFunction.getTommorrow());
	page.SubmitButton.click();
	Date d=CommonFunction.gettime("UTC");
	Date today=CommonFunction.getDay(CommonFunction.gettime(""));
	// wait the pro is added on right grid
	(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
	(new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
	(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
	(new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
	(new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
	
    // eqps table
	ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(PickedTrailer.get(0), PickedTrailer.get(1));
	Sassert.assertEquals(NewEqpStatusRecord.get(0), "UAD","Equipment_Status_Type_CD is wrong");
	Sassert.assertEquals(NewEqpStatusRecord.get(1), OldEqpStatusRecord.get(1),"Statusing_Facility_CD is wrong");
	Sassert.assertEquals(NewEqpStatusRecord.get(3), "AUTUAD","Source_Create_ID is wrong");
	// orig and desti keep
	Sassert.assertEquals(NewEqpStatusRecord.get(2), OldEqpStatusRecord.get(2),"equipment_dest_facility_cd is wrong");
	Sassert.assertEquals(NewEqpStatusRecord.get(15), OldEqpStatusRecord.get(15),"equipment_origin_facility_CD is wrong");
	 for (int i=5;i<=8;i++){
	 Date TS=CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));	
	 if(i==7){
	Sassert.assertTrue(Math.abs(TS.getTime()-AlterTime.getTime())<60000,"equipment_status_ts "+"  "+TS+"  "+AlterTime);
	 }else{
	Sassert.assertTrue(Math.abs(TS.getTime()-d.getTime())<120000,i+"  "+TS+"  "+d);}}
	
	for(String CurrentPro: ADDEDPRO){
	//check waybill
	ArrayList<Object> AfterADDWb=DataCommon.GetWaybillInformationOfPro(CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(0),SCAC,"Waybill table Standard_Carrier_Alpha_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(1),TrailerNB,"Waybill table Equipment_Unit_NB is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(3),"LH.LDG","Waybill table Source_Modify_ID is wrong "+CurrentPro);
	for (int i=9;i<=9;i++){
	Date f=CommonFunction.SETtime((Date) AfterADDWb.get(i));	
	Sassert.assertTrue(Math.abs(f.getTime()-d.getTime())<120000,"waybill table "+i+"  "+f+"  "+d+"  "+"   "+CurrentPro);}	
	//loading record
    Sassert.assertEquals(AfterADDWb.get(14),null,"waybill table loading record To_Facility_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(13),terminalcd,"waybill table loading record From_Facility_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(17),SCAC,"waybill table loading record To_Standard_Carrier_Alpha_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(18),TrailerNB,"waybill table loading record To_Equipment_Unit_NB is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(15),null,"waybill table loading record From_Standard_Carrier_Alpha_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(16),null,"waybill table loading record From_Equipment_Unit_NB is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(20),"LOADING","waybill table loading Waybill_Transaction_Type_NM is wrong "+CurrentPro);
	Date f1=CommonFunction.SETtime((Date) AfterADDWb.get(11));	
	Sassert.assertTrue(Math.abs(f1.getTime()-d.getTime())<120000,"waybill Waybill_Transaction_End_TS   "+f1+"  "+d+"  "+"   "+CurrentPro); 
	}		    
	Sassert.assertAll();  
  
  }
  

  @Test(priority=3,dataProvider = "512.12",dataProviderClass=DataForUS51212.class)
  public void PullProFromARVTrailerInDifferentTerminal(String terminalcd,String SCAC,String TrailerNB,String Desti) throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
   SoftAssert Sassert= new SoftAssert();  
   page.SetLocation(terminalcd);	 
   page.EnterTrailer(SCAC,TrailerNB);	
   //pick an arv trailer in same terminal
   ArrayList<String> PickedTrailer=DataForUS51212.GetTrailerOnDifferentTerminal(terminalcd,"arv");
   String fromSCAC=PickedTrailer.get(0);
   String fromTrailerNb=PickedTrailer.get(1);
   //get eqps information of the picked trailer
   ArrayList<Object> OldEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(fromSCAC, fromTrailerNb);
   // get pro from the picked trailer
   ArrayList<String> PROlist=DataCommon.GetProOnTrailer(fromSCAC, fromTrailerNb);
   
   page.RemoveProButton.click();
   //add pro
   ArrayList<String> ADDEDPRO= new  ArrayList<String>();
   for(int j=0;j<1;j++){	
	String currentPro=PROlist.get(j);
    page.EnterPro(currentPro);
    ADDEDPRO.add(currentPro);}
	 
   Date AlterTime=CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
   
   // change cityroute
   page.SetCityRoute("Downtown");
	// set plan date
	page.PlanDate.sendKeys(CommonFunction.getTommorrow());
	page.SubmitButton.click();
	Date d=CommonFunction.gettime("UTC");
	Date today=CommonFunction.getDay(CommonFunction.gettime(""));
	// wait the pro is added on right grid
	(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
	(new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
	(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
	(new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
	(new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
    // eqps table
	ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(fromSCAC, fromTrailerNb);
	Sassert.assertEquals(NewEqpStatusRecord, OldEqpStatusRecord,"eqps got changed");

	
	for(String CurrentPro: ADDEDPRO){
	//check waybill
	ArrayList<Object> AfterADDWb=DataCommon.GetWaybillInformationOfPro(CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(0),SCAC,"Waybill table Standard_Carrier_Alpha_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(1),TrailerNB,"Waybill table Equipment_Unit_NB is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(3),"LH.LDG","Waybill table Source_Modify_ID is wrong "+CurrentPro);
	Date f=CommonFunction.SETtime((Date) AfterADDWb.get(9));	
	Sassert.assertTrue(Math.abs(f.getTime()-d.getTime())<120000,"waybill table system_modify_ts "+f+"  "+d+"  "+"   "+CurrentPro);	
	 //loading record
    Sassert.assertEquals(AfterADDWb.get(14),null,"waybill table loading record To_Facility_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(13),terminalcd,"waybill table loading record From_Facility_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(17),SCAC,"waybill table loading record To_Standard_Carrier_Alpha_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(18),TrailerNB,"waybill table loading record To_Equipment_Unit_NB is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(15),null,"waybill table loading record From_Standard_Carrier_Alpha_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(16),null,"waybill table loading record From_Equipment_Unit_NB is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(20),"LOADING","waybill table loading Waybill_Transaction_Type_NM is wrong "+CurrentPro);
	Date f1=CommonFunction.SETtime((Date) AfterADDWb.get(11));	
	Sassert.assertTrue(Math.abs(f1.getTime()-d.getTime())<120000,"waybill Waybill_Transaction_End_TS   "+f1+"  "+d+"  "+"   "+CurrentPro); 
	}
	Sassert.assertAll();  
  
  }
		  
  @Test(priority=4,dataProvider = "512.12",dataProviderClass=DataForUS51212.class)
  public void PullProFromCPUTrailerInDifferentTerminal(String terminalcd,String SCAC,String TrailerNB,String Desti) throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
   SoftAssert Sassert= new SoftAssert();  
   page.SetLocation(terminalcd);	 
   page.EnterTrailer(SCAC,TrailerNB);	
   //pick an arv trailer in same terminal
   ArrayList<String> PickedTrailer=DataForUS51212.GetTrailerOnDifferentTerminal(terminalcd,"cpu");
   String fromSCAC=PickedTrailer.get(0);
   String fromTrailerNb=PickedTrailer.get(1);
   //get eqps information of the picked trailer
   ArrayList<Object> OldEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(fromSCAC, fromTrailerNb);
   // get pro from the picked trailer
   ArrayList<String> PROlist=DataCommon.GetProOnTrailer(fromSCAC, fromTrailerNb);
   
   page.RemoveProButton.click();
   //add pro
   ArrayList<String> ADDEDPRO= new  ArrayList<String>();
   for(int j=0;j<1;j++){	
	String currentPro=PROlist.get(j);
    page.EnterPro(currentPro);
    ADDEDPRO.add(currentPro); }
	 
   Date AlterTime=CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
   
   // change cityroute
   page.SetCityRoute("Downtown");
	// set plan date
	page.PlanDate.sendKeys(CommonFunction.getTommorrow());
	page.SubmitButton.click();
	Date d=CommonFunction.gettime("UTC");
	Date today=CommonFunction.getDay(CommonFunction.gettime(""));
	(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
	(new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
	(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
	(new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
	(new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));

    // eqps table
	ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(fromSCAC, fromTrailerNb);
	Sassert.assertEquals(NewEqpStatusRecord, OldEqpStatusRecord,"eqps got changed");
	
	for(String CurrentPro: ADDEDPRO){
	//check waybill
	ArrayList<Object> AfterADDWb=DataCommon.GetWaybillInformationOfPro(CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(0),SCAC,"Waybill table Standard_Carrier_Alpha_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(1),TrailerNB,"Waybill table Equipment_Unit_NB is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(3),"LH.LDG","Waybill table Source_Modify_ID is wrong "+CurrentPro);
	Date f=CommonFunction.SETtime((Date) AfterADDWb.get(9));	
	Sassert.assertTrue(Math.abs(f.getTime()-d.getTime())<120000,"waybill table system_modify_ts "+f+"  "+d+"  "+"   "+CurrentPro);	
	//loading record
    Sassert.assertEquals(AfterADDWb.get(14),null,"waybill table loading record To_Facility_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(13),terminalcd,"waybill table loading record From_Facility_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(17),SCAC,"waybill table loading record To_Standard_Carrier_Alpha_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(18),TrailerNB,"waybill table loading record To_Equipment_Unit_NB is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(15),null,"waybill table loading record From_Standard_Carrier_Alpha_CD is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(16),null,"waybill table loading record From_Equipment_Unit_NB is wrong "+CurrentPro);
	Sassert.assertEquals(AfterADDWb.get(20),"LOADING","waybill table loading Waybill_Transaction_Type_NM is wrong "+CurrentPro);
	Date f1=CommonFunction.SETtime((Date) AfterADDWb.get(11));	
	Sassert.assertTrue(Math.abs(f1.getTime()-d.getTime())<120000,"waybill Waybill_Transaction_End_TS   "+f1+"  "+d+"  "+"   "+CurrentPro); 
	}
	Sassert.assertAll();  
  
  }
  
  
  
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
	   page.SetStatus("cl");
  }

  @AfterClass
  public void Close() {
	  driver.close();
  }

}
