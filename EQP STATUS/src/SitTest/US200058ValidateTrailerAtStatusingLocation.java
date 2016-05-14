package SitTest;

import java.awt.AWTException;
import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

import Data.DataForUS200058;
import Page.EqpStatusPageS;

public class US200058ValidateTrailerAtStatusingLocation {
	private WebDriver driver;
	private EqpStatusPageS page;
	public static String SetToStatus;

	
 @BeforeClass
 @Parameters({"browser","status"})
 public void SetUp(@Optional("chrome")String browser,@Optional("CL") String status) throws AWTException, InterruptedException { 
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
	 // driver.manage().window().maximize();
	 SetToStatus=status;
	  page.SetStatus(SetToStatus);

  
 }
 
 @Test(priority=1,dataProvider = "2000.58",dataProviderClass=DataForUS200058.class)
 public void VerifyInvalidStatusLocation(String terminalcd,String SCAC,String TrailerNB) throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
	 Actions builder = new Actions(driver);
	 page.SetLocation("326");
	 if(page.TrailerField.isDisplayed()){page.TrailerField.click(); }
	 page.TrailerInputField.clear();
	 page.TrailerInputField.sendKeys(TrailerNB);
		builder.sendKeys(Keys.TAB).build().perform();
		String SCACTrailer=page.SCACTrailer( SCAC, TrailerNB);
		try{
		(new WebDriverWait(driver, 3)).until(ExpectedConditions.visibilityOfElementLocated(By.linkText(SCACTrailer)));	
		driver.findElement(By.linkText(SCACTrailer)).click();	
		}catch(Exception e){						
		} 
	 (new WebDriverWait(driver, 10)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,"This trailer is located at #"+terminalcd+""));
 
 }
	 	
 @Test(priority=2,dataProvider = "2000.58",dataProviderClass=DataForUS200058.class)
 public void VerifyValidStatusLocation(String terminalcd,String SCAC,String TrailerNB) throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
	 page.SetLocation(terminalcd);
	 page.EnterTrailer(SCAC,TrailerNB);
	 //Assert.assertFalse(page.CheckErrorAndWarningMessageDisplay("This trailer is located at #"+terminalcd+""));
	 (new WebDriverWait(driver, 10)).until(ExpectedConditions.invisibilityOfElementWithText(By.xpath("html/body/div[1]/div"), "This trailer is located at #"+terminalcd+""));
	 Thread.sleep(500);	 
 }
	 	
 @AfterClass
 public void TearDown() {
 driver.close();
 }


}
