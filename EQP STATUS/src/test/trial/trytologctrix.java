package test.trial;

import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.testng.annotations.Test;

public class trytologctrix {
	 private WebDriver driver=null;
 @Test
  public void f() throws InterruptedException {
	System.setProperty("webdriver.ie.driver", "C:\\Users\\uyr27b0\\Desktop\\selenium\\selenium\\ie32\\IEDriverServer.exe");  
	driver=new InternetExplorerDriver();  
	driver.navigate().to("https://xen.yrcw.com/Citrix/XenApp/site/default.aspx");
	driver.findElement(By.id("user")).sendKeys("uyr27b0");
	driver.findElement(By.id("password")).sendKeys("Zzt@12345678");
	driver.findElement(By.id("btnLogin")).click();
	//driver.findElement(By.name("reset1")).click();
	driver.findElement(By.linkText("Citrix - Internet Explorer 11")).click();
	Thread.sleep(5000);
	String NORMALIEHandle= driver.getWindowHandle();
	Thread.sleep(5000);
	Set<String> handles=driver.getWindowHandles();
	System.out.println(handles.size());
	for(String hd:handles){
		if(!hd.equals(NORMALIEHandle)){
			driver.switchTo().window(hd);
			driver.manage().window().maximize();
			driver.navigate().to("http://javasit11.yrcw.com:3010/");
		           }
	            }
	
  }
	
	//@Test
	public void e() throws InterruptedException {
		System.setProperty("webdriver.ie.driver", "C:\\Users\\uyr27b0\\Desktop\\selenium\\selenium\\ie32\\IEDriverServer.exe");  
		driver=new InternetExplorerDriver();  
		driver.navigate().to("https://xen.yrcw.com/Citrix/XenApp/site/default.aspx");
		String NORMALIEHandle= driver.getWindowHandle();
		((JavascriptExecutor)driver).executeScript("window.open();");
		
		Set<String> handles=driver.getWindowHandles();
		System.out.println(handles.size());
		for(String hd:handles){
			if(!hd.equals(NORMALIEHandle)){
				driver.switchTo().window(hd);
				//driver.manage().window().maximize();
				driver.navigate().to("http://javasit11.yrcw.com:3010/");
			           }
		            }
		driver.switchTo().window(NORMALIEHandle);
		driver.navigate().to("http://javadev1.yrcw.com:3010/");
  }
}
