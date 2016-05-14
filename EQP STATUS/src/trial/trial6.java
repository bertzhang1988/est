package trial;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;

public class trial6 {
	
	private WebDriver driver=null;
	Actions builder;
  @Test
  public void f() {
	  System.setProperty("webdriver.chrome.driver", "C:\\Users\\uyr27b0\\Desktop\\selenium\\selenium//chromedriver.exe");
	  driver = new ChromeDriver();   
	  builder=new Actions(driver);
	  driver.get("https://www.hotelplanner.com/Hotels/2156-in-Kansas-City-MO.html#dir-bar");
	   driver.manage().window().maximize();
 //builder.moveToElement(page.ProListForm).click().build().perform();
	   
	   JavascriptExecutor jse = (JavascriptExecutor)driver;
	   //jse.executeScript("window.scrollBy(0,350)", "");
	   
	    jse.executeScript("scroll(0, 1350);");
  }
}
