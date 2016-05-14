package trial;

import java.awt.AWTException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import Data.DataForUS461AndUS465AndUS457;
import Page.EqpStatusPageS;

public class trial5 {
	private WebDriver driver=null;
	 EqpStatusPageS page;
	 Actions builder;
  @Test
  public void f() throws AWTException, InterruptedException {
	  System.setProperty("webdriver.chrome.driver", "C:\\Users\\uyr27b0\\Desktop\\selenium\\selenium//chromedriver.exe");
	  driver = new ChromeDriver();            
	  //driver=new InternetExplorerDriver();;
	  //driver=new FirefoxDriver();
	   page=new EqpStatusPageS(driver);
	   builder=new Actions(driver);
	   driver.get(page.sit1);
	   driver.manage().window().maximize();
	   page.SetStatus("ldg");
	   page.SetLocation("326");
		 (new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		 page.EnterTrailer("RDWY", "126096");
		 WebElement e=page.ProListForm.findElement(By.xpath("div[31]"));

	 //  builder.moveToElement(page.ProListForm).click().build().perform();
	   
	   JavascriptExecutor jse = (JavascriptExecutor)driver;
	   //jse.executeScript("window.scrollBy(0,350)", "");
	  //  jse.executeScript("scroll(0, 1350);");
	   jse.executeScript("arguments[0].scrollIntoView(true);",e);
	
	  // Thread.sleep(5000);
	  // WebElement f=page.ProListForm.findElement(By.xpath("div[51]"));
	   //jse.executeScript("arguments[0].scrollIntoView(true);",f);
	   //jse.executeScript("window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));");
	  // WebElement element = driver.findElement(By.xpath("Value"));
	   //Coordinates coordinate = ((Locatable)e).getCoordinates(); 
	  // coordinate.onPage(); 
	  // coordinate.inViewPort();
	   WebElement f=page.ProListForm.findElement(By.xpath("div[1]"));
	   jse.executeScript("arguments[0].scrollIntoView(true);",f); 
  }
  //@Test
  public void ef() throws AWTException, InterruptedException {
	  System.setProperty("webdriver.chrome.driver", "C:\\Users\\uyr27b0\\Desktop\\selenium\\selenium//chromedriver.exe");
	  driver = new ChromeDriver();            
	  //driver=new InternetExplorerDriver();;
	  //driver=new FirefoxDriver();
	   page=new EqpStatusPageS(driver);
	   builder=new Actions(driver);
	   driver.get(page.sit1);
	   driver.manage().window().maximize();
	   page.SetStatus("ldd");
	   page.SetLocation("326");
		 (new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		 page.EnterTrailer("RDWY", "126096");
		//WebElement e=page.ProListForm.findElement(By.xpath("div[31]"));

	 //  builder.moveToElement(page.ProListForm).click().build().perform();
	   
	//  JavascriptExecutor jse = (JavascriptExecutor)driver;
	   //jse.executeScript("window.scrollBy(0,350)", "");
	  //  jse.executeScript("scroll(0, 1350);");
	 //  jse.executeScript("arguments[0].scrollIntoView(true);",e);
	
	   //check pro grid
		int line=page.ProListSecondForm.findElements(By.xpath("div")).size();
		//Set<ArrayList<String>> ProInfo= new HashSet<ArrayList<String>>();       // dont sort the pro list
		LinkedHashSet<ArrayList<String>> ProInfo= new LinkedHashSet<ArrayList<String>>();     // sort the prolist
	    for(int i=1;i<=line;i++){
	 	  //String[] Proline1= ArrayUtils.remove(page.ProListForm.findElement(By.xpath("div["+i+"]")).getText().split("\\n"), 0);
	 	 String[] Proline1= page.ProListSecondForm.findElement(By.xpath("div["+i+"]")).getText().split("\\n");
		 ArrayList<String> e1= new ArrayList<String> (Arrays.asList(Proline1));
		 ProInfo.add(e1); }
	    if( line>=31){
	    JavascriptExecutor jse = (JavascriptExecutor)driver;
	    int additional=31;
		do{
			jse.executeScript("arguments[0].scrollIntoView(true);",page.ProListSecondForm.findElement(By.xpath("div["+additional+"]")));
			 additional=page.ProListSecondForm.findElements(By.xpath("div")).size();
			 for(int j=1;j<=additional;j++) {
					String[] Proline1= page.ProListSecondForm.findElement(By.xpath("div["+j+"]")).getText().split("\\n");
					ArrayList<String> e1= new ArrayList<String> (Arrays.asList(Proline1));
					ProInfo.add(e1);}
		}
		
		while(additional>31);
	    int Rest=page.ProListSecondForm.findElements(By.xpath("div")).size();
	    for(int j=1;j<=Rest;j++) {
			String[] Proline1= page.ProListSecondForm.findElement(By.xpath("div["+j+"]")).getText().split("\\n");
			ArrayList<String> e1= new ArrayList<String> (Arrays.asList(Proline1));
			ProInfo.add(e1);} 	
	    }
	    
		 System.out.println(ProInfo.size());
		 //	SA.assertEquals(ProInfo,DataForUS461AndUS465AndUS457.GetProList(SCAC, TrailerNB));	 
		 	
		 //	SA.assertAll();
  
  }


}



