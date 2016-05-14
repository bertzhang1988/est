package LdgScreen;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.testng.asserts.SoftAssert;

import Page.CommonFunction;
import Page.EqpStatusPageS;
import Data.DataForUS200004;
import Data.DataForUS200068AndUS445;
import Data.DataForUS439;

public class US200004ProNumberDigitCheck {
	private WebDriver driver;
	private EqpStatusPageS page;
  
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
	 page.SetStatus("ldg");
	}
	 
@Test(priority=1,dataProvider = "2000.682",dataProviderClass=DataForUS200068AndUS445.class)
public void EnterTrailerInLdgNoShipments(String terminalcd,String SCAC,String TrailerNB) throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
	page.SetLocation(terminalcd);
	page.EnterTrailer(SCAC,TrailerNB);}

@Test(priority=2,dataProvider = "2000.04",dataProviderClass=DataForUS200004.class)
public void VerifyProDigitCheck(String pro) throws InterruptedException{
	Actions builder = new Actions(driver); 
	page.AddProField.clear();
	page.AddProField.sendKeys(pro);
	String Pronumber=pro.trim().toUpperCase();
	String PronumberH=page.addHyphenToPro(Pronumber);
	if(driver instanceof InternetExplorerDriver )builder.sendKeys(page.AddProField,Keys.TAB).build().perform();
	(new WebDriverWait(driver, 5)).until(ExpectedConditions.textToBePresentInElement(page.AddProForm, PronumberH));
	String message = null;
	int flag=CommonFunction.CheckProPattern(Pronumber);
	if (flag==1){
		message="Invalid Pro Number";
	}else if(flag==3){
		message="Invalid Check Digit";
	}else if(flag==2){
		message="";
	}

	int NEW= page.AddProForm.findElements(By.xpath("div")).size();
	//Assert.assertEquals(page.AddProForm.findElement(By.xpath("div["+NEW+"]/div/div[2]/div")).getText(),Pronumber); 
	(new WebDriverWait(driver, 5)).until(ExpectedConditions.textToBePresentInElement(page.AddProForm.findElement(By.xpath("div["+NEW+"]/div/div[3]/div")),message));
	if(flag!=2){
		System.out.println(pro);
	}
	if (NEW==5){
	page.RemoveProButton.click();
	}  
}

//@Test(priority=3,dataProvider = "ldgtrailerNoPro",dataProviderClass=DataForUS200004.class)
public void VerifyProDigitCheck(String terminalcd,String SCAC,String TrailerNB) throws InterruptedException, AWTException{
	SoftAssert SAssert= new SoftAssert();
	page.SetLocation(terminalcd);
	page.EnterTrailer(SCAC,TrailerNB);
	List<String> prolist= Arrays.asList(DataForUS200004.prolist);
	for(int i=0; i<prolist.size();i++){
	String	pro=prolist.get(i);
	page.EnterPro(pro);
	String Pronumber=pro.trim().toUpperCase();
	String PronumberH=page.addHyphenToPro(Pronumber);
	try{(new WebDriverWait(driver, 5)).until(ExpectedConditions.textToBePresentInElement(page.AddProForm, PronumberH));}
	catch(Exception e){
	System.out.println(Pronumber+" is not kick to the grid");	
	}
	String message = null;
	int flag=CommonFunction.CheckProPattern(Pronumber);
	if (flag==1){
		message="Invalid Pro Number";
	}else if(flag==3){
		message="Invalid Check Digit";
	}else if(flag==2){
		message="";
	}

	int NEW= page.AddProForm.findElements(By.xpath("div")).size();
	if(NEW!=0){
	//(new WebDriverWait(driver, 5)).until(ExpectedConditions.textToBePresentInElement(page.AddProForm.findElement(By.xpath("div["+NEW+"]/div/div[2]/div")), Pronumber));
	SAssert.assertEquals(page.AddProForm.findElement(By.xpath("div["+NEW+"]/div/div[2]/div")).getText(),PronumberH); 
	SAssert.assertEquals(page.AddProForm.findElement(By.xpath("div["+NEW+"]/div/div[3]/div")).getText(),message,"   "+Pronumber); 
	}//(new WebDriverWait(driver, 5)).until(ExpectedConditions.textToBePresentInElement(page.AddProForm.findElement(By.xpath("div["+NEW+"]/div/div[3]/div")),message));
	if (NEW==5){page.RemoveProButton.click();}  
    }
	SAssert.assertAll();	
}

@AfterClass
public void TearDown() {
	page.RemoveProButton.click();
	driver.quit();
}




}
