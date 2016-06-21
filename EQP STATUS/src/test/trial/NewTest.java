package test.trial;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import Data.DataForUS439;
import Page.CommonFunction;

public class NewTest {
	WebDriver driver;
	
 // @Test
  public void f() throws MalformedURLException {
     DesiredCapabilities caps = new DesiredCapabilities();
   caps=DesiredCapabilities.chrome();
     // caps=DesiredCapabilities.internetExplorer();
	  //caps=DesiredCapabilities.firefox();
	 //caps.setBrowserName("internet explorer");
	 //caps.setVersion("10");
	// caps.setPlatform(Platform.VISTA);
	 driver= new RemoteWebDriver(new URL("http://10.0.80.94:5555/wd/hub"), caps);
	//driver= new RemoteWebDriver(new URL("http://10.51.235.212:4444/wd/hub"), caps);
     driver.get("http://javasit11.yrcw.com:3010/");
     
  }

// @Test
 public void e(){

	 int a=30;
	 String b=String.format("%02d",a);
	 System.out.println(b);
 }

// @Test
 public void F(){

	 String a="MATU259674";
	 boolean b=a.matches("[a-zA-Z]+[\\d]+");
	 System.out.println(b);
 }
 //@Test
 public void g() throws InterruptedException{
	 ArrayList<String>  d=new ArrayList<String>();
	 String FirstTWO="88";
		for(int i=0;i<10;i++){
		SimpleDateFormat sdfDate = new SimpleDateFormat("mmssSSS");
		TimeUnit.MILLISECONDS.sleep(200);
		Date now = new Date();
		String strDate = sdfDate.format(now);
	    String firstNine=FirstTWO+strDate;
	    System.out.println(strDate);
	    String CheckDight;
	    int remainder=(Integer.parseInt(firstNine))%11;
	    if(remainder==0) {CheckDight="0";}
	    else if(remainder==1) {CheckDight="X";}
	    else{ CheckDight= Integer.toString(11-remainder);}
	    String pro;
	    pro=firstNine+CheckDight;
	    d.add(pro);}
		System.out.println(d);
	
 }

 @Test(priority=2,dataProvider = "Invalid pro",dataProviderClass=DataForUS439.class)
 public void VerifyProDigitCheck(String pro) throws InterruptedException{
 	String Pronumber=pro.trim().toUpperCase();
    String message=null;
 	int flag=CommonFunction.CheckProPattern(Pronumber);
 	if (flag==1){
 		message="Invalid Pro Number";
 	}else if(flag==3){
 		message="Invalid Check Digit";
 	}else if(flag==2){
 		message="";
 	}
 	if(flag!=2){
 		System.out.println(pro);
 }

}}
