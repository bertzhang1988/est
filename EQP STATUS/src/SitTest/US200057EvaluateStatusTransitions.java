package SitTest;

import java.awt.AWTException;
import java.sql.SQLException;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
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

import Data.DataForUS200057;
import Page.EqpStatusPageS;

public class US200057EvaluateStatusTransitions {
	private WebDriver driver;
	private EqpStatusPageS page;
	public static String SetToStatus;

	@BeforeClass
	@Parameters({"browser","status"})
	public void SetUp(@Optional("chrome")String browser,@Optional("ldd")String status) throws AWTException, InterruptedException { 
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
 
 @Test(priority=1,dataProvider = "2000.57",dataProviderClass=DataForUS200057.class)
 public void VerifyInvalidStatusTransition(String terminalcd,String SCAC,String TrailerNB,String CurrentStatus) throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
	 Actions builder = new Actions(driver);
	 page.SetLocation(terminalcd);
	 if(page.TrailerField.isDisplayed()){page.TrailerField.click(); }
	    //this.TrailerInputField.click();
		page.TrailerInputField.clear();
		page.TrailerInputField.sendKeys(TrailerNB);
		builder.sendKeys(Keys.TAB).build().perform();
		String SCACTrailer=page.SCACTrailer( SCAC, TrailerNB);
		try{
		(new WebDriverWait(driver, 3)).until(ExpectedConditions.visibilityOfElementLocated(By.linkText(SCACTrailer)));	
		driver.findElement(By.linkText(SCACTrailer)).click();	
		}catch(Exception e){						
		} 
     (new WebDriverWait(driver, 10)).until(ExpectedConditions.textToBePresentInElement(page.ErrorAndWarningField,"This trailer is currently in "+CurrentStatus+" status. Changing to "+SetToStatus.toUpperCase()+" status is not allowed."));
	//( new WebDriverWait(driver, 5)).until(ExpectedConditions.visibilityOf(page.ErrorAndWarning("This trailer is currently in "+CurrentStatus+" status. Changing to "+SetToStatus.toUpperCase()+" status is not allowed.")));
}
 
 
 @AfterClass
 public void TearDown() {
	  driver.close();
 }

}
