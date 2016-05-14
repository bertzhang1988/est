package LdgScreen;

import java.awt.AWTException;
import java.sql.SQLException;
import java.util.Iterator;

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


import Data.DataForUS458;
import Page.EqpStatusPageS;

public class US458CheckShipmentAlreadyLoadedOnTrailer {
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

  @Test(priority=1,dataProvider = "458",dataProviderClass=DataForUS458.class)
  public void CheckShipmentAlreadyLoadedOnTrailer(String terminalcd,String SCAC,String TrailerNB) throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
	page.SetLocation(terminalcd);
	page.EnterTrailer(SCAC,TrailerNB);
	String Cube=page.CubeField.getAttribute("value");
	if (Cube.equalsIgnoreCase("")||Cube.equalsIgnoreCase("0")){
	int Ran=(int)(Math.random()*99)+1;
	page.SetCube(Integer.toString(Ran));}
	Iterator<String> data= DataForUS458.GetPro(SCAC, TrailerNB).iterator();
	while(data.hasNext()){
    page.RemoveProButton.click();
	String pro=data.next();  
	page.EnterPro(pro);
	int NEW= page.AddProForm.findElements(By.xpath("div")).size();
	//page.AddProCheckBoxList.findElement(By.xpath("div["+NEW+"]/div/div/div/div")).click();
	page.SubmitLDGButton.click();
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.textToBePresentInElement(page.AddProForm.findElement(By.xpath("div["+NEW+"]/div/div[3]/div")),"This PRO is already on trailer."));
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[1]/div")));
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.elementToBeClickable(page.RemoveProButton));
	page.CheckAllAddProButton.click();
	page.RemoveProButton.click();
	}
	  }
  
  
  
  
  //@AfterClass
  public void TearDown() {
	  driver.quit();
  }
}
