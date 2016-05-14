package LdgScreen;

import java.awt.AWTException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
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

import Data.DataForUS200075;
import Page.DataCommon;
import Page.EqpStatusPageS;

public class US200075DisplayPROsInOrderWhichTheyEnter {
	private WebDriver driver;
	private EqpStatusPageS page;
  @Test(priority=1,dataProvider = "2000.75",dataProviderClass=DataForUS200075.class, groups = { "ldg uc" })
  public void AddSinglePro(String terminalcd,String SCAC,String TrailerNB) throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
	  SoftAssert SA= new SoftAssert();  
	  page.SetLocation(terminalcd);	 
	  page.EnterTrailer(SCAC,TrailerNB);
	  ArrayList<String> PRO=DataCommon.GetProNotInAnyTrailer();
	  page.RemoveProButton.click();
	  for(int i=0;i<3;i++){
	  String CurrentPro=PRO.get(i);
	  page.EnterPro(CurrentPro);
	  String CurrentProH= page.addHyphenToPro(CurrentPro);
	  //page.AddProCheckBoxList.findElement(By.xpath("div["+NEW1+"]/div/div/div/div")).click();
	  int Ran=(int)(Math.random()*99)+1;
	  page.SetCube(Integer.toString(Ran));
	  page.SubmitLDGButton.click();
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
	  page.EnterTrailer(SCAC,TrailerNB);
	  (new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.ProListForm, CurrentProH));
	  int NEW2=page.ProListForm.findElements(By.xpath("div")).size();  
	  Assert.assertEquals(page.ProListForm.findElement(By.xpath("div["+NEW2+"]/div/div[2]/div")).getText(),CurrentProH);
	  
		 }	 
	LinkedHashSet<ArrayList<String>> ProInfo=page.GetProList(page.ProListForm);
	SA.assertEquals(ProInfo,DataCommon.GetProList(SCAC, TrailerNB)," pro grid is wrong");		 
	SA.assertAll();	  
  
  }

  
  
  @Test(priority=2,dataProvider = "2000.75",dataProviderClass=DataForUS200075.class, groups = { "ldg uc" })
  public void AddMultipleProsInSingleBatch(String terminalcd,String SCAC,String TrailerNB) throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
	  SoftAssert SA= new SoftAssert();  
	  page.SetLocation(terminalcd);	 
	  page.EnterTrailer(SCAC,TrailerNB);
	  ArrayList<String> PRO=DataCommon.GetProNotInAnyTrailer();
	  page.RemoveProButton.click();
	  ArrayList<String> Addpro= new  ArrayList<String>();
	  for(int i=0;i<3;i++){  
	  String CurrentPro=PRO.get(i);
	  page.EnterPro(CurrentPro); 
	  Addpro.add(CurrentPro);
		 } 		 

	  int Ran=(int)(Math.random()*99)+1;
	  page.SetCube(Integer.toString(Ran));
			 
	  page.SubmitLDGButton.click();	 
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
	  page.EnterTrailer(SCAC,TrailerNB);
      LinkedHashSet<ArrayList<String>> ProInfo=page.GetProList(page.ProListForm);
	  SA.assertEquals(ProInfo,DataCommon.GetProList(SCAC, TrailerNB)," pro grid is wrong");		 
	  // check right grid is as same as left grid
	  ArrayList<String> AddproBatch= new  ArrayList<String>();
	  Iterator<ArrayList<String>> pr=ProInfo.iterator();
      while(pr.hasNext()){
      ArrayList<String> pro=pr.next(); 
      AddproBatch.add(pro.get(0).replaceAll("-", ""));
      }  
      SA.assertEquals( AddproBatch, Addpro," pro sequence is wrong");
      // check eqps
	  ArrayList<Object> NewEqpStatusRecord= DataCommon.CheckEQPStatusUpdate(SCAC, TrailerNB);
	  SA.assertEquals(NewEqpStatusRecord.get(16), page.AD_ID,"modify_id is wrong");
	  SA.assertEquals(NewEqpStatusRecord.get(17), page.M_ID,"eqps Mainframe_User_ID is wrong");
	  // check eqp
	  ArrayList<Object> NewEqp= DataCommon.CheckEquipment(SCAC, TrailerNB);
	  SA.assertEquals(NewEqp.get(0), page.M_ID," eqp Mainframe_User_ID is wrong");
      SA.assertAll();
  }
  

  @Test(priority=3,dataProvider = "2000.75",dataProviderClass=DataForUS200075.class, groups = { "ldg uc" })
  public void AddTwoBatchesProsInSameSection(String terminalcd,String SCAC,String TrailerNB) throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
	  SoftAssert SA= new SoftAssert();  
	  page.SetLocation(terminalcd);	 
	  page.EnterTrailer(SCAC,TrailerNB);
	  ArrayList<String> PRO=DataCommon.GetProNotInAnyTrailer();
	  page.RemoveProButton.click();
	  ArrayList<String> Addpro= new  ArrayList<String>();
	  for(int i=0;i<3;i++){
	  String CurrentPro=PRO.get(i);
	  page.EnterPro(CurrentPro);	 
	  Addpro.add(CurrentPro);
		 }		 

	  int Ran=(int)(Math.random()*99)+1;
	  page.SetCube(Integer.toString(Ran));
		
	  page.SubmitLDGButton.click();	
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
	  page.EnterTrailer(SCAC,TrailerNB);
	  page.RemoveProButton.click();	  
	  PRO=DataCommon.GetProNotInAnyTrailer();
	  //
	  for(int i=0;i<2;i++){	  
      String CurrentPro=PRO.get(i);
      page.EnterPro(CurrentPro);
	  Thread.sleep(500);
	  int NEW1= page.AddProForm.findElements(By.xpath("div")).size();
	  Assert.assertEquals(page.AddProForm.findElement(By.xpath("div["+NEW1+"]/div/div[2]/div")).getText(),page.addHyphenToPro(CurrentPro)); 	 
	  Addpro.add(CurrentPro);
			 }
	  page.SubmitLDGButton.click();	
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.visibilityOf(page.TrailerInputField));		
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.textToBePresentInElementValue(page.TrailerInputField,""));
	  (new WebDriverWait(driver, 80)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
	  
	  page.EnterTrailer(SCAC,TrailerNB);
	  LinkedHashSet<ArrayList<String>> ProInfo=page.GetProList(page.ProListForm);
	  SA.assertEquals(ProInfo,DataCommon.GetProList(SCAC, TrailerNB)," pro grid is wrong");		 
	  // check right grid is as same as left grid
	  ArrayList<String> AddproBatch= new  ArrayList<String>();
	  Iterator<ArrayList<String>> pr=ProInfo.iterator();
      while(pr.hasNext()){
      ArrayList<String> pro=pr.next(); 
      AddproBatch.add(pro.get(0).replaceAll("-", ""));
      }  
      SA.assertEquals( AddproBatch, Addpro," pro sequence is wrong");
      SA.assertAll(); 
			 
  }
  
  
  
  
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
  }

  @AfterClass( groups = { "ldg uc" })
  public void Close() {
	  driver.close();
  }

}
