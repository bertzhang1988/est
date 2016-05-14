package SitTest;

import java.awt.AWTException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

import Data.DataForUS200006;
import Page.EqpStatusPageS;

public class US200070DisplayUpdatablesStatusInDropDownBox {
	 private WebDriver driver=null;
	 EqpStatusPageS page;

  @Test(dataProvider = "status")
  public void VerifyStatusClickable(String status) throws InterruptedException {
	  page.SetStatus(status);
	  
  }
  
  @BeforeTest
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
	   driver.manage().window().maximize();}

  @AfterTest
  public void TearDown() {
		driver.quit();
		  }
  @AfterMethod
  public void waitwhile() throws InterruptedException{
	  Thread.sleep(2000);  
  }
  @DataProvider(name="status")
  public Object[][] StatusData(){
	  return new Object[][] {
	    		{"ldd"},{"ldg"},{"uad"},{"bor"},{"cl"},{"mty"},{"cpu"},{"cltg"},{"ofd"}
	    };
  }
  
}
