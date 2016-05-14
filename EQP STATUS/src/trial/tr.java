package trial;

import java.awt.AWTException;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Page.EqpStatusPageS;

public class tr {
	 private WebDriver driver;
	 private EqpStatusPageS page;
	 @Test
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
	   Actions builder = new Actions(driver); 
	   //page.SetStatus("ldg");
	   (new WebDriverWait(driver, 150)).until(ExpectedConditions.visibilityOf(page.StatusTrailerButton));
	   page.StatusTrailerButton.click();
	   (new WebDriverWait(driver, 20)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//*[@label='Set Status To']/div/div/span")));
	   (new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(page.SetStatusToField));
	   page.SetStatusToField.click();
	   (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(page.StatusList));
	  // page.SetStatusToInput.click();
	  // page.SetStatusToInput.clear();
	  // Thread.sleep(10000);
	  // page.SetStatusToInput.sendKeys("L");		
	   //builder.sendKeys(Keys.SPACE).build().perform();
	  // Thread.sleep(10000);
	   (new WebDriverWait(driver, 2)).until(ExpectedConditions.visibilityOfElementLocated(By.linkText("LDG - Linehaul Loading")));
	 //  WebElement SelectStatus=page.StatusList.findElement(By.linkText("LDG - Linehaul Loading"));
	   WebElement SelectStatus=page.StatusList.findElement(By.xpath(".//div[contains(text(), 'LDG - Linehaul Loading')]"));
	   SelectStatus.click();
	   //builder.doubleClick(SelectStatus).build().perform();
	   page.SetLocation("621");
	  page.EnterTrailer("RDWY","116822");
		  }

	// @Test
	 @Parameters({"browser"})
	 public void SetUp2(@Optional("ie")String browser) throws AWTException, InterruptedException { 
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
	   Actions builder = new Actions(driver); 
	   (new WebDriverWait(driver, 150)).until(ExpectedConditions.visibilityOf(page.StatusTrailerButton));
	   page.StatusTrailerButton.click();
	   
	   
	 }

}
