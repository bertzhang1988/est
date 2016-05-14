package LdgScreen;

import java.awt.AWTException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.testng.asserts.SoftAssert;


import Data.DataForUS461AndUS465AndUS457;
import Page.CommonFunction;
import Page.DataCommon;
import Page.EqpStatusPageS;

public class US461PrepopValuesLoadingTrailerAndUS465SummarizeBillsWeightUS457ShowLoadedShipmentOnGrid {
	
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

  @Test(priority=1,dataProvider = "4.61And4.65And4.57",dataProviderClass=DataForUS461AndUS465AndUS457.class)
 public void PrepopValuesLoadingTrailerAndSummarizeBillsWeight(String terminalcd,String SCAC,String TrailerNB,String Desti,String AmountPro,String AmountWeight,String Cube,String hldesti, String hlcube, Date MRSts) throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
	 SoftAssert SA= new SoftAssert();
	 page.SetLocation(terminalcd);
	 page.EnterTrailer(SCAC,TrailerNB);
	 SA.assertEquals(page.DestinationField.getAttribute("value").replaceAll("_", ""), Desti,"destination is wrong");
	 SA.assertEquals(page.CubeField.getAttribute("value"), Cube,"cube is wrong");
	 //SA.assertEquals(page.ShipCountLdg.getText(), AmountPro,"shipcount is wrong");
	 //SA.assertEquals(page.ShipWeightLdg.getText(), AmountWeight,"ship weight is wrong");
	 SA.assertEquals(page.HLDestLdg.getText(), hldesti,"headload dest is wrong");
	 SA.assertEquals(page.HLCubeLdg.getText(), hlcube,"headload cube is wrong");
	 //check date&time field should be equipment_status_ts at statusing location time zone
	 Date LocalTime=CommonFunction.getLocalTime(terminalcd, MRSts);
	 Calendar cal = Calendar.getInstance();
	 SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
	 cal.setTime(LocalTime); 
	 int hourOfDay = cal.get(Calendar.HOUR_OF_DAY); // 24 hour clock
	 String hour=String.format("%02d",hourOfDay);
	 int minute = cal.get(Calendar.MINUTE);
	 String Minute=String.format("%02d",minute);
	 String DATE = dateFormat.format(cal.getTime());
	 SA.assertEquals(page.DateInput.getAttribute("value"), DATE);
	 SA.assertEquals(page.HourInput.getAttribute("value"),hour);
	 SA.assertEquals(page.MinuteInput.getAttribute("value"), Minute);

	//check pro grid
	 LinkedHashSet<ArrayList<String>> ProInfo=page.GetProList(page.ProListForm);
	 SA.assertEquals(ProInfo,DataCommon.GetProList(SCAC, TrailerNB),"  prolist information is wrong");		 
	 SA.assertAll();
  }		
	

  
// @Test(priority=2,dataProvider = "4.61And4.65And4.57",dataProviderClass=DataForUS461AndUS465AndUS457.class)
  public void PrepopValuesLoadingTrailerAndSummarizeBillsWeight2(String terminalcd,String SCAC,String TrailerNB,String Desti,String AmountPro,String AmountWeight,String Cube,String hldesti, String hlcube, Date MRSts) throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
 	 SoftAssert SA= new SoftAssert();
 	 page.SetLocation(terminalcd);
 	 page.EnterTrailer(SCAC,TrailerNB);
 	 //(new WebDriverWait(driver, 10)).until(ExpectedConditions.invisibilityOfElementLocated(page.ErrorAndWarningField));
  }
	//@AfterClass
	  public void TearDown() {
		  driver.quit();
	  }
}
