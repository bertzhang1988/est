package LdgScreen;

import java.awt.AWTException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Data.DataForUS200091;
import Page.CommonFunction;
import Page.DataCommon;
import Page.EqpStatusPageS;

public class US200091SmartEnterLDG {
private WebDriver driver;
private EqpStatusPageS page;
private Actions builer;	
@BeforeClass( groups = { "ldg uc" })
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
  page.SetStatus("ldg");
  builer= new Actions(driver);
}

@Test(priority=1,dataProvider = "2000.91",dataProviderClass=DataForUS200091.class, groups = { "ldg uc" })
public void TrailerWithoutPROAddPROSetToldg(String terminalcd, String SCAC, String TrailerNB,String destination,Date MReqpst) throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
SoftAssert SAssert= new SoftAssert();
page.SetLocation(terminalcd);	
page.EnterTrailer(SCAC,TrailerNB);	
//change destination
String[] dest={"270","112","841","198","135"};
int ran =new Random().nextInt(dest.length);
String changeDesti=dest[ran];
page.SetDestination(changeDesti);
//alter time
page.SetDatePicker( page.GetDatePickerTime(), -3); 
Date AlterTime=CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
// add pro
ArrayList<String> PRO= DataCommon.GetProNotInAnyTrailer();
ArrayList<String> ADDPRO=new ArrayList<String>();
page.RemoveProButton.click();
for(int i=0;i<1;i++){
String CurrentPro=PRO.get(i);
page.EnterPro(CurrentPro);
ADDPRO.add(CurrentPro); 
}
// ENTER CUBE
int Ran=(int)(Math.random()*99)+1;
String NewCube=Integer.toString(Ran);
page.SetCube(NewCube);
// PRESS ENTER
page.AddProField.click();
builer.sendKeys(page.AddProField, Keys.ENTER).build().perform();
Date d=CommonFunction.gettime("UTC");

(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
(new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
(new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
(new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));
// check eqps
ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG","Equipment_Status_Type_CD is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd,"Statusing_Facility_CD is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(2), changeDesti,"equipment_dest_facility_cd is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG","Source_Create_ID is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube,"Actual_Capacity_Consumed_PC is wrong");
for (int i=5;i<=8;i++){
Date TS=CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));	
if(i==7){
SAssert.assertTrue(Math.abs(TS.getTime()-AlterTime.getTime())<60000,"equipment_status_ts "+"  "+TS+"  "+AlterTime);
}else{
SAssert.assertTrue(Math.abs(TS.getTime()-d.getTime())<120000,i+"  "+TS+"  "+d);}}

// check waybill
for(String pro: ADDPRO){

ArrayList<Object> NewWbAndWbtRecord=DataCommon.GetWaybillInformationOfPro(pro);
SAssert.assertEquals(NewWbAndWbtRecord.get(0), SCAC,""+pro +" wb.Standard_Carrier_Alpha_CD is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(1), TrailerNB,"wb.Equipment_Unit_NB is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(13),terminalcd ,"waybill from terminal is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(17), SCAC,"wb.To_Standard_Carrier_Alpha_CD is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(18), TrailerNB,"wb.To_Equipment_Unit_NB is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(20), "LOADING","wb.Waybill_Transaction_Type_NM is wrong");

Date System_Modify_TS=CommonFunction.SETtime((Date) NewWbAndWbtRecord.get(9));	
SAssert.assertTrue(Math.abs(System_Modify_TS.getTime()-d.getTime())<120000," waybill table System_Modify_TS "+System_Modify_TS+"  "+d);
Date Waybill_Transaction_End_TS=CommonFunction.SETtime((Date) NewWbAndWbtRecord.get(11));	
SAssert.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime()-d.getTime())<120000," waybill table Waybill_Transaction_End_TS "+Waybill_Transaction_End_TS+"  "+d);
}
SAssert.assertAll();
}

@Test(priority=2,dataProvider = "2000.91",dataProviderClass=DataForUS200091.class, groups = { "ldg uc" })
public void TrailerWithoutPROSetToLdg(String terminalcd, String SCAC, String TrailerNB, String destination,Date MReqpst) throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException {
SoftAssert SAssert= new SoftAssert();
page.SetLocation(terminalcd);	
page.EnterTrailer(SCAC,TrailerNB);	

String[] dest={"270","112","841","198","135"};
int ran =new Random().nextInt(dest.length);
String changeDesti=dest[ran];
page.SetDestination(changeDesti);
//alter time
page.SetDatePicker( page.GetDatePickerTime(), -3); 
Date AlterTime=CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
builer.sendKeys(page.AddProField, Keys.ENTER).build().perform();
Date d=CommonFunction.gettime("UTC");
(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
(new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
(new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div/div")));

ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG","Equipment_Status_Type_CD is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd,"Statusing_Facility_CD is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(2), changeDesti,"equipment_dest_facility_cd is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG","Source_Create_ID is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(4), null,"Actual_Capacity_Consumed_PC is wrong");
for (int i=5;i<=8;i++){
Date TS=CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));	
if(i==7){
SAssert.assertTrue(Math.abs(TS.getTime()-AlterTime.getTime())<60000,"equipment_status_ts "+"  "+TS+"  "+AlterTime);
}else{
SAssert.assertTrue(Math.abs(TS.getTime()-d.getTime())<120000,i+"  "+TS+"  "+d);}}
SAssert.assertAll();
}

@Test(priority=3,dataProvider = "2000.91",dataProviderClass=DataForUS200091.class, groups = { "ldg uc" })
public void TrailerWithPROAddPRO(String terminalcd, String SCAC, String TrailerNB,String destination,Date MReqpst) throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException{
SoftAssert SAssert= new SoftAssert();	
page.SetLocation(terminalcd);	
page.EnterTrailer(SCAC,TrailerNB);
(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.Title, "Leftover Bill Review"));
(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
//change destination
String[] dest={"270","112","841","198","135"};
int ran =new Random().nextInt(dest.length);
String changeDesti=dest[ran];
page.SetDestination(changeDesti);

//enter cube
int Ran=(int)(Math.random()*99)+1;
String NewCube=Integer.toString(Ran);
page.SetCube(NewCube);
//alter time
page.SetDatePicker( page.GetDatePickerTime(), -3); 
Date AlterTime=CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
String[] handleLobrPro={"headload","leaveON","allshort","dock"};
int ran1 =new Random().nextInt(handleLobrPro.length);
page.HandleLOBRproAll(handleLobrPro[ran1]);
Date d1=CommonFunction.gettime("UTC");
//page.LobrSubmitButton.click();
(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.Title, "Set Trailer Status Loading"));
(new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
Thread.sleep(5000);

ArrayList<String> PRO= DataCommon.GetProNotInAnyTrailer();
ArrayList<String> ADDPRO=new ArrayList<String>();
for(int i=0;i<1;i++){
String CurrentPro=PRO.get(i);
page.EnterPro(CurrentPro);
ADDPRO.add(CurrentPro);
}
// change cube
int Ran1=(int)(Math.random()*99)+1;
String NewCube1=Integer.toString(Ran1);
page.SetCube(NewCube1);
page.AddProField.click();
builer.sendKeys(page.AddProField, Keys.ENTER).build().perform();
Date d=CommonFunction.gettime("UTC");
(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,"Trailer "+SCAC+TrailerNB+" updated to LDG"));	 
(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
(new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
(new WebDriverWait(driver, 80)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG","Equipment_Status_Type_CD is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd,"Statusing_Facility_CD is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(2), changeDesti,"equipment_dest_facility_cd is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG","Source_Create_ID is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube1,"Actual_Capacity_Consumed_PC is wrong");
for (int i=5;i<=8;i++){
Date TS=CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));	
if(i==7){
SAssert.assertTrue(Math.abs(TS.getTime()-AlterTime.getTime())<60000,"equipment_status_ts "+"  "+TS+"  "+AlterTime);
}else if(i==5){
SAssert.assertTrue(Math.abs(TS.getTime()-d.getTime())<120000,"modify_ts "+"  "+TS+"  "+d);}	
else{
SAssert.assertTrue(Math.abs(TS.getTime()-d1.getTime())<120000,i+"  "+TS+"  "+d);}}
for(String CurrentPro: ADDPRO){
ArrayList<Object> NewWbAndWbtRecord=DataCommon.GetWaybillInformationOfPro(CurrentPro);
SAssert.assertEquals(NewWbAndWbtRecord.get(0), SCAC,""+CurrentPro +" wb.Standard_Carrier_Alpha_CD is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(1), TrailerNB,"wb.Equipment_Unit_NB is wrong");

SAssert.assertEquals(NewWbAndWbtRecord.get(17), SCAC,"wb.To_Standard_Carrier_Alpha_CD is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(18), TrailerNB,"wb.To_Equipment_Unit_NB is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(20), "LOADING","wb.Waybill_Transaction_Type_NM is wrong");
Date System_Modify_TS=CommonFunction.SETtime((Date) NewWbAndWbtRecord.get(9));	
SAssert.assertTrue(Math.abs(System_Modify_TS.getTime()-d.getTime())<120000," waybill table System_Modify_TS "+System_Modify_TS+"  "+d);
Date Waybill_Transaction_End_TS=CommonFunction.SETtime((Date) NewWbAndWbtRecord.get(11));	
SAssert.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime()-d.getTime())<120000," waybill table Waybill_Transaction_End_TS "+Waybill_Transaction_End_TS+"  "+d);
}
SAssert.assertAll();
}


@Test(priority=4,dataProvider = "2000.91",dataProviderClass=DataForUS200091.class,description="lobr, ldg change destination ", groups = { "ldg uc" }, enabled=false)
public void TrailerWithPRONoAdding(String terminalcd, String SCAC, String TrailerNB,String destination,Date MReqpst) throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException{
SoftAssert SAssert= new SoftAssert();
page.SetLocation(terminalcd);	
page.EnterTrailer(SCAC,TrailerNB);
(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.Title, "Leftover Bill Review"));
(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
//change destination
String[] dest={"270","112","841","198","135"};
int ran =new Random().nextInt(dest.length);
String changeDesti=dest[ran];
page.SetDestination(changeDesti);
//enter cube
int Ran=(int)(Math.random()*99)+1;
String NewCube=Integer.toString(Ran);
page.SetCube(NewCube);
//alter time
page.SetDatePicker( page.GetDatePickerTime(), -3); 
Date AlterTime=CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
String[] handleLobrPro={"headload","leaveON","allshort","dock"};
int ran1 =new Random().nextInt(handleLobrPro.length);
page.HandleLOBRproAll(handleLobrPro[ran1]);
Date d=CommonFunction.gettime("UTC");
(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.AlertMessage));
(new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.Title, "Set Trailer Status Loading"));
(new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
builer.sendKeys(Keys.ENTER).build().perform();
//check eqps
ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG","Equipment_Status_Type_CD is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd,"Statusing_Facility_CD is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(2), changeDesti,"equipment_dest_facility_cd is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG","Source_Create_ID is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube,"Actual_Capacity_Consumed_PC is wrong");
for (int i=5;i<=8;i++){
Date TS=CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));	
if(i==7){
SAssert.assertTrue(Math.abs(TS.getTime()-AlterTime.getTime())<60000,"equipment_status_ts "+"  "+TS+"  "+AlterTime);
}else{
SAssert.assertTrue(Math.abs(TS.getTime()-d.getTime())<120000,i+"  "+TS+"  "+d);}}
SAssert.assertAll();
}

@Test(priority=5,dataProvider = "2000.91",dataProviderClass=DataForUS200091.class, groups = { "ldg uc" })
public void LdgTrailerWithProAddPro(String terminalcd, String SCAC, String TrailerNB,String destination,Date MReqpst) throws AWTException, InterruptedException, ClassNotFoundException, SQLException{
SoftAssert SAssert= new SoftAssert();	
page.SetLocation(terminalcd);	
page.EnterTrailer(SCAC,TrailerNB);

//add pro
ArrayList<String> PRO= DataCommon.GetProNotInAnyTrailer();
ArrayList<String> ADDPRO=new ArrayList<String>();
for(int i=0;i<1;i++){
String CurrentPro=PRO.get(i);
page.EnterPro(CurrentPro);
ADDPRO.add(CurrentPro);
}

//enter cube
int Ran=(int)(Math.random()*99)+1;
String NewCube=Integer.toString(Ran);
page.SetCube(NewCube);
ArrayList<Object> OldEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
//enter key
builer.sendKeys(page.AddProField, Keys.ENTER).build().perform();
Date d=CommonFunction.gettime("UTC");
(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
(new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
(new WebDriverWait(driver, 80)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));

//check eqps
ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG","Equipment_Status_Type_CD is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd,"Statusing_Facility_CD is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(2), destination,"equipment_dest_facility_cd is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(19), "LH.LDG","Source_Modify_ID is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube,"Actual_Capacity_Consumed_PC is wrong");

for (int i=5;i<=8;i++){
Date TS=CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));	
if(i==5){
SAssert.assertTrue(Math.abs(TS.getTime()-d.getTime())<120000,"modify_ts "+"  "+TS+"  "+d);
}else{
SAssert.assertEquals(NewEqpStatusRecord.get(i),(Date) OldEqpStatusRecord.get(i),i+"  "+NewEqpStatusRecord.get(i)+"  "+OldEqpStatusRecord.get(i));}}

for(String CurrentPro: ADDPRO){
ArrayList<Object> NewWbAndWbtRecord=DataCommon.GetWaybillInformationOfPro(CurrentPro);
SAssert.assertEquals(NewWbAndWbtRecord.get(0), SCAC,""+CurrentPro +" wb.Standard_Carrier_Alpha_CD is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(1), TrailerNB,"wb.Equipment_Unit_NB is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(17), SCAC,"wb.To_Standard_Carrier_Alpha_CD is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(18), TrailerNB,"wb.To_Equipment_Unit_NB is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(20), "LOADING","wb.Waybill_Transaction_Type_NM is wrong");
Date System_Modify_TS=CommonFunction.SETtime((Date) NewWbAndWbtRecord.get(9));	
SAssert.assertTrue(Math.abs(System_Modify_TS.getTime()-d.getTime())<120000," waybill table System_Modify_TS "+System_Modify_TS+"  "+d);
Date Waybill_Transaction_End_TS=CommonFunction.SETtime((Date) NewWbAndWbtRecord.get(11));	
SAssert.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime()-d.getTime())<120000," waybill table Waybill_Transaction_End_TS "+Waybill_Transaction_End_TS+"  "+d);}

SAssert.assertAll();
}

@Test(priority=6,dataProvider = "2000.91",dataProviderClass=DataForUS200091.class, groups = { "ldg uc" })
public void LdgTrailerWithoutProNoChange(String terminalcd, String SCAC, String TrailerNB,String destination,Date MReqpst) throws AWTException, InterruptedException, ClassNotFoundException, SQLException{
SoftAssert SAssert= new SoftAssert();	
page.SetLocation(terminalcd);	
page.EnterTrailer(SCAC,TrailerNB);
SAssert.assertEquals(page.DestinationField.getAttribute("value"), destination,"destiantion display IS WRONG");
ArrayList<Object> OldEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);

//enter key
builer.sendKeys(page.AddProField, Keys.ENTER).build().perform();
//(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,"No updates entered."));	 
(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
(new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
(new WebDriverWait(driver, 80)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
//check eqps
ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
SAssert.assertEquals(NewEqpStatusRecord,OldEqpStatusRecord,"eqps change");
SAssert.assertAll();
}

@Test(priority=7,dataProvider = "2000.91",dataProviderClass=DataForUS200091.class, groups = { "ldg uc" })
public void LdgTrailerWithoutProChangeDestinationAddPRO(String terminalcd, String SCAC, String TrailerNB,String destination,Date MReqpst) throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException{
SoftAssert SAssert= new SoftAssert();	
page.SetLocation(terminalcd);	
page.EnterTrailer(SCAC,TrailerNB);
ArrayList<Object> OldEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);

//change DESTINATION
String[] dest={"270","112","841","198","135"};
int ran =new Random().nextInt(dest.length);
String changeDesti=dest[ran];
while (changeDesti.equalsIgnoreCase(destination)){
changeDesti=dest[new Random().nextInt(dest.length)];
 	}
page.SetDestination(changeDesti);
//alter time
page.SetDatePicker( page.GetDatePickerTime(), -3); 
Date AlterTime=CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
//add pro
ArrayList<String> PRO= DataCommon.GetProNotInAnyTrailer();
ArrayList<String> ADDPRO=new ArrayList<String>();
for(int i=0;i<1;i++){
String CurrentPro=PRO.get(i);
page.EnterPro(CurrentPro);
ADDPRO.add(CurrentPro);
}
// enter cube
int Ran=(int)(Math.random()*99)+1;
String NewCube=Integer.toString(Ran);
page.SetCube(NewCube);
//enter key
builer.sendKeys(page.AddProField, Keys.ENTER).build().perform();
Date d=CommonFunction.gettime("UTC");
(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,"Trailer "+SCAC+TrailerNB+" updated to LDG"));	 
(new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
(new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
(new WebDriverWait(driver, 80)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
//check eqps
ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG","Equipment_Status_Type_CD is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd,"Statusing_Facility_CD is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(2), changeDesti,"equipment_dest_facility_cd is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG","Source_Create_ID is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube,"Actual_Capacity_Consumed_PC is wrong");
for (int i=5;i<=8;i++){
Date TS=CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));	
if(i==7){
SAssert.assertTrue(Math.abs(TS.getTime()-AlterTime.getTime())<60000,"equipment_status_ts "+"  "+TS+"  "+AlterTime);
}else{
SAssert.assertTrue(Math.abs(TS.getTime()-d.getTime())<120000,i+"  "+TS+"  "+d);}}

//check waybill information
for(String pro: ADDPRO){
ArrayList<Object> NewWbAndWbtRecord= DataCommon.GetWaybillInformationOfPro(pro);
SAssert.assertEquals(NewWbAndWbtRecord.get(0), SCAC,""+pro +" wb.Standard_Carrier_Alpha_CD is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(1), TrailerNB,"wb.Equipment_Unit_NB is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(13),terminalcd ,"waybill from terminal is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(17), SCAC,"wb.To_Standard_Carrier_Alpha_CD is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(18), TrailerNB,"wb.To_Equipment_Unit_NB is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(20), "LOADING","wb.Waybill_Transaction_Type_NM is wrong");
Date System_Modify_TS=CommonFunction.SETtime((Date) NewWbAndWbtRecord.get(9));	
SAssert.assertTrue(Math.abs(System_Modify_TS.getTime()-d.getTime())<120000," waybill table System_Modify_TS "+System_Modify_TS+"  "+d);
Date Waybill_Transaction_End_TS=CommonFunction.SETtime((Date) NewWbAndWbtRecord.get(11));	
SAssert.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime()-d.getTime())<120000," waybill table Waybill_Transaction_End_TS "+Waybill_Transaction_End_TS+"  "+d);
}
SAssert.assertAll();
}

@Test(priority=8,dataProvider = "2000.91",dataProviderClass=DataForUS200091.class, groups = { "ldg uc" })
public void LdgTrailerWithProChangeDestinationAddPRO(String terminalcd, String SCAC, String TrailerNB,String destination,Date MReqpst) throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException{
SoftAssert SAssert= new SoftAssert();	
page.SetLocation(terminalcd);	
page.EnterTrailer(SCAC,TrailerNB);

//change destination
String[] dest={"270","112","841","198","135"};
int ran =new Random().nextInt(dest.length);
String changeDesti=dest[ran];
while (changeDesti.equalsIgnoreCase(destination)){
changeDesti=dest[new Random().nextInt(dest.length)];}
destination =changeDesti;
page.SetDestination(destination);
(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.Title,"Mark PROs as Headload"));
(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
//alter time
page.SetDatePicker( page.GetDatePickerTime(), -3); 
Date AlterTime=CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
//headload yes
//page.YesButton.click();
builer.sendKeys( Keys.ENTER).build().perform();
Date d1=CommonFunction.gettime("UTC");
(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.Title,"Set Trailer Status Loading"));
(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
// add pro
ArrayList<String> PRO= DataCommon.GetProNotInAnyTrailer();
ArrayList<String> ADDPRO=new ArrayList<String>();
for(int i=0;i<1;i++){
String CurrentPro=PRO.get(i);
page.EnterPro(CurrentPro);
ADDPRO.add(CurrentPro);
}
// enter cube
int Ran=(int)(Math.random()*99)+1;
String NewCube=Integer.toString(Ran);
page.SetCube(NewCube);
//enter key
builer.sendKeys(page.AddProField, Keys.ENTER).build().perform();
Date d=CommonFunction.gettime("UTC");
(new WebDriverWait(driver, 150)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));// wait the alert gone
(new WebDriverWait(driver, 50)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
Thread.sleep(10000);
//check eqps
ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
SAssert.assertEquals(NewEqpStatusRecord.get(0), "LDG","Equipment_Status_Type_CD is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(1), terminalcd,"Statusing_Facility_CD is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(2), destination,"equipment_dest_facility_cd is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(3), "LH.LDG","Source_Create_ID is wrong");
SAssert.assertEquals(NewEqpStatusRecord.get(4), NewCube,"Actual_Capacity_Consumed_PC is wrong");
for (int i=5;i<=8;i++){
Date TS=CommonFunction.SETtime((Date) NewEqpStatusRecord.get(i));	
if(i==7){
SAssert.assertTrue(Math.abs(TS.getTime()-AlterTime.getTime())<60000,"equipment_status_ts "+"  "+TS+"  "+AlterTime);
}else if(i==5){
SAssert.assertTrue(Math.abs(TS.getTime()-d.getTime())<120000,"modify_ts "+"  "+TS+"  "+d);}	
else{
SAssert.assertTrue(Math.abs(TS.getTime()-d1.getTime())<120000,i+"  "+TS+"  "+d);}}
//check waybill information
for(String pro: ADDPRO){
ArrayList<Object> NewWbAndWbtRecord= DataCommon.GetWaybillInformationOfPro(pro);
SAssert.assertEquals(NewWbAndWbtRecord.get(0), SCAC,""+pro +" wb.Standard_Carrier_Alpha_CD is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(1), TrailerNB,"wb.Equipment_Unit_NB is wrong");

SAssert.assertEquals(NewWbAndWbtRecord.get(17), SCAC,"wbt.To_Standard_Carrier_Alpha_CD is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(18), TrailerNB,"wbt.To_Equipment_Unit_NB is wrong");
SAssert.assertEquals(NewWbAndWbtRecord.get(20), "LOADING","wbt.Waybill_Transaction_Type_NM is wrong");
Date System_Modify_TS=CommonFunction.SETtime((Date) NewWbAndWbtRecord.get(9));	
SAssert.assertTrue(Math.abs(System_Modify_TS.getTime()-d.getTime())<120000," waybill table System_Modify_TS "+System_Modify_TS+"  "+d);
Date Waybill_Transaction_End_TS=CommonFunction.SETtime((Date) NewWbAndWbtRecord.get(11));	
SAssert.assertTrue(Math.abs(Waybill_Transaction_End_TS.getTime()-d.getTime())<120000," waybill table Waybill_Transaction_End_TS "+Waybill_Transaction_End_TS+"  "+d);
}
SAssert.assertAll();
}

@AfterClass( groups = { "ldg uc" })
public void Close() {
 driver.close();
}}