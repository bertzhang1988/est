package trial;

import java.awt.AWTException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import LdgScreen.DataForUSLDGLifeTest;
import Page.CommonFunction;
import Page.EqpStatusPageS;

public class DatePickerTrial {
	 private WebDriver driver;
	 private EqpStatusPageS page;
	 private Actions builder;
@BeforeClass
@Parameters({"browser"})
public void SetUp(@Optional("Chrome")String browser) throws AWTException, InterruptedException { 
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
  builder = new Actions(driver);
	  }

@Test(priority=5,dataProvider = "ldgscreen",dataProviderClass=DataForUSLDGLifeTest.class,description="6.19")
public void SetTrailerToLDGWithProLeaveHeadLoadCubeAndDestination(String terminalcd, String SCAC, String TrailerNB,Date MReqpst) throws AWTException, InterruptedException, ClassNotFoundException, SQLException, ParseException { 
	SoftAssert SAssert= new SoftAssert();
	page.SetLocation(terminalcd);	
	page.EnterTrailer(SCAC,TrailerNB);
	Date CurrentTime=CommonFunction.gettime("UTC");
	Date LocalTime = null;
	(new WebDriverWait(driver, 50)).until(ExpectedConditions.textToBePresentInElement(page.TitleOfScreen, "Leftover Bill Review"));
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
	//check date&time field should a. eqpst>current-time use eqpst  minute+1 b. eqpst<current time use current time
		 if(MReqpst.before(CurrentTime)){
			 LocalTime= CommonFunction.getLocalTime(terminalcd, CurrentTime);
		   }else if(MReqpst.after(CurrentTime)){
			 LocalTime=CommonFunction.getLocalTime(terminalcd, MReqpst);
		   }

		 Calendar cal = Calendar.getInstance();
		 SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
		 cal.setTime(LocalTime); 
		 int hourOfDay = cal.get(Calendar.HOUR_OF_DAY); // 24 hour clock
		 String hour=String.format("%02d",hourOfDay);
		 int minute = cal.get(Calendar.MINUTE);
		 String Minute=String.format("%02d",minute);
		 String MinutePlusOne=String.format("%02d",minute+1);
		 String DATE = dateFormat.format(cal.getTime());
		 
		 SAssert.assertEquals(page.DateInput.getAttribute("value"), DATE,"TIME DARE IS WRONG");
		 SAssert.assertEquals(page.HourInput.getAttribute("value"),hour,"TIME HOR IS WRONG");
		 if(MReqpst.after(CurrentTime)){
		 SAssert.assertEquals(page.MinuteInput.getAttribute("value"), MinutePlusOne,"TIME MINUTE IS WRONG"); 
		 }else{
		 SAssert.assertEquals(page.MinuteInput.getAttribute("value"), Minute,"TIME MINUTE IS WRONG");}
  
        System.out.println(CurrentTime);
        Date localPickerTime=CommonFunction.ConvertUtcTime(terminalcd, page.GetDatePickerTime());
        System.out.println(localPickerTime); 
        System.out.println(MReqpst); 
        page.SetDatePicker(MReqpst,-23);
}


}
