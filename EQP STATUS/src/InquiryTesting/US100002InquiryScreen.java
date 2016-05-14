package InquiryTesting;

import java.awt.AWTException;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.asserts.SoftAssert;

import Page.CommonFunction;
import Page.EqpStatusPageS;

public class US100002InquiryScreen {
	private WebDriver driver;
	private EqpStatusPageS page;
	private Actions builder;
   
	 @BeforeMethod
	 @Parameters({"browser"})
public void SetUp(@Optional("chrome")String browser) throws AWTException, InterruptedException { 
	 if (browser.equalsIgnoreCase("chrome")){
	 System.setProperty("webdriver.chrome.driver", "C:\\Users\\uyr27b0\\Desktop\\selenium\\selenium//chromedriver.exe");
	 driver = new ChromeDriver();            
	 }else if(browser.equalsIgnoreCase("ie")){
	  System.setProperty("webdriver.ie.driver", "C:\\Users\\uyr27b0\\Desktop\\selenium\\selenium\\ie32\\IEDriverServer.exe");
	 driver=new InternetExplorerDriver();
	  }else if(browser.equalsIgnoreCase("hl")){
	 File file = new File("C:\\Users\\uyr27b0\\Desktop\\selenium\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");             
	 System.setProperty("phantomjs.binary.path", file.getAbsolutePath());        
	 driver = new PhantomJSDriver();   
	 	  }
	  page=new EqpStatusPageS(driver);
	  driver.get(page.sit1);
	  driver.manage().window().maximize();  
	  page.SetInquiryScreen();
	  builder=new Actions(driver);
      
	}
	 
 @Test(priority=1,dataProvider = "1000.02",dataProviderClass=DataForInQuiryScreen.class)
  public void DisplayListOfStatuses(String terminal) throws ClassNotFoundException, SQLException, InterruptedException {
	ArrayList<String> ExpectedStatusList=DataForInQuiryScreen.GetStatusList(terminal);
	page.EQTerminalInput.clear();
	page.EQTerminalInput.sendKeys(terminal);
	//builder.sendKeys(page.EQTerminalInput, Keys.ENTER).build().perform();
	page.SearchButton.click();
	if(ExpectedStatusList.size()!=0){
    Thread.sleep(500);
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));	
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(page.EQStatusList));	
	int CountStatus=page.EQStatusList.findElements(By.xpath("div")).size();
	ArrayList<String> StatusList= new ArrayList<String>();
	for (int i=1;i<=CountStatus;i++){
	String StatusName=page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='trailer-inquiry-status']")).getText();
	StatusList.add(StatusName);
	}
    Assert.assertEquals(StatusList,ExpectedStatusList ,"  "+ExpectedStatusList+" "+StatusList);
	}else {
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']")));
	}
  }
  
 @Test(priority=2,dataProvider = "1000.02",dataProviderClass=DataForInQuiryScreen.class)
  public void DisplayListOfStatusesAndPup(String terminal) throws ClassNotFoundException, SQLException, InterruptedException {
	ArrayList<ArrayList<String>> ExpectedStatusList=DataForInQuiryScreen.GetStatusListAndPup(terminal);
	page.EQTerminalInput.clear();
	page.EQTerminalInput.sendKeys(terminal);
	page.SearchButton.click();
	if(ExpectedStatusList.size()!=0){
    Thread.sleep(500);
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));		
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(page.EQStatusList));	
	int CountStatus=page.EQStatusList.findElements(By.xpath("div")).size();
	ArrayList<ArrayList<String>> StatusList= new ArrayList<ArrayList<String>>();
	for (int i=1;i<=CountStatus;i++){
	String StatusName=page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='trailer-inquiry-status']")).getText();
	String Schedules=page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='trailer-inquiry-status-stat']/div[1]/span")).getText();
	String PUPS=page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='trailer-inquiry-status-stat']/div[2]/span")).getText();
	String VANS=page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='trailer-inquiry-status-stat']/div[3]/span")).getText();
	ArrayList<String> A= new ArrayList<String>();
	A.add(StatusName);
	A.add(Schedules);
	A.add(PUPS);
	A.add(VANS);
	StatusList.add(A);	
	}
    Assert.assertEquals(ExpectedStatusList, StatusList,"  "+ExpectedStatusList+" "+StatusList);
	}else {
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']")));
		}
  }
  
  @Test(priority=3,dataProvider = "1000.02",dataProviderClass=DataForInQuiryScreen.class)
  public void DisplayTrailerGrid(String terminal) throws ClassNotFoundException, SQLException, InterruptedException {
	SoftAssert Sassert= new SoftAssert();   
	ArrayList<String> ExpectedStatusList=DataForInQuiryScreen.GetStatusList(terminal);
	page.EQTerminalInput.clear();
	page.EQTerminalInput.sendKeys(terminal);
	page.SearchButton.click();
	Date d=CommonFunction.gettime("UTC");
	if(ExpectedStatusList.size()!=0){
    Thread.sleep(500);
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));		
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(page.EQStatusList));	
	int CountStatus=page.EQStatusList.findElements(By.xpath("div")).size();
	for(int i=1;i<=CountStatus;i++){
		String StatusName=page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='trailer-inquiry-status']")).getText();
		page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='panel-heading']")).click();
		WebElement TrailerGrid=page.EQStatusList.findElement(By.xpath("//div[@class='ui-grid-contents-wrapper']/div[3]//div[@class='ui-grid-canvas']"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(TrailerGrid));	
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		//(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']//div[@class='ui-grid-canvas']/div")));
		
	int TrailerLine=TrailerGrid.findElements(By.xpath("div[@class='ui-grid-row']")).size();
	List<ArrayList<String>> TrailerInformation= new ArrayList<ArrayList<String>>();
	for(int j=1;j<=TrailerLine;j++){
		 String[] OneTrailerInoformation= TrailerGrid.findElements(By.xpath("div[@class='ui-grid-row']")).get(j-1).getText().split("\\n");
		 ArrayList<String> e= new ArrayList<String> (Arrays.asList(OneTrailerInoformation));
		 TrailerInformation.add(e);	
	}
	ArrayList<ArrayList<String>> ExpectedTrailerInformation=DataForInQuiryScreen.GetTrailerInformation(terminal, StatusName, d);
	Sassert.assertEquals(TrailerInformation,ExpectedTrailerInformation,"  "+StatusName);
	page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='panel-heading']")).click();	
	}}else{
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']")));
	}
	Sassert.assertAll();
  }
  
@Test(priority=4,dataProvider = "1000.02",dataProviderClass=DataForInQuiryScreen.class)
public void DisplayProGrid(String terminal) throws ClassNotFoundException, SQLException, InterruptedException {
	SoftAssert Sassert= new SoftAssert();   
	Wait<WebDriver> wait=new FluentWait<WebDriver>(driver).withTimeout(10, TimeUnit.SECONDS).pollingEvery(2, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
	ArrayList<String> ExpectedStatusList=DataForInQuiryScreen.GetStatusList(terminal);
	page.EQTerminalInput.clear();
	page.EQTerminalInput.sendKeys(terminal);
	page.SearchButton.click();
	Date d=CommonFunction.gettime("UTC");
	if(ExpectedStatusList.size()!=0){
    Thread.sleep(500);
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));		
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(page.EQStatusList));	
	int CountStatus=page.EQStatusList.findElements(By.xpath("div")).size();
	for(int i=1;i<=CountStatus;i++){
		String StatusName=page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='trailer-inquiry-status']")).getText();
		page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='panel-heading']")).click();
		WebElement TrailerGrid=page.EQStatusList.findElement(By.xpath("//div[@class='ui-grid-contents-wrapper']/div[3]//div[@class='ui-grid-canvas']"));
		WebElement PlusSignColumn=page.EQStatusList.findElement(By.xpath("//div[@class='ui-grid-contents-wrapper']/div[2]//div[@class='ui-grid-canvas']"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(TrailerGrid));	
		Thread.sleep(1000);
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		//(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']//div[@class='ui-grid-canvas']/div")));
		
	int TrailerLine=TrailerGrid.findElements(By.xpath("div[@class='ui-grid-row']")).size();
	if(TrailerLine>20){
		TrailerLine=20;
	}
	List<ArrayList<String>> TrailerInformation= new ArrayList<ArrayList<String>>();
	for(int j=1;j<=TrailerLine;j++){
		
		List<WebElement> PlusSign=PlusSignColumn.findElements(By.tagName("i"));
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("arguments[0].scrollIntoView(false);",PlusSign.get(j-1));
		//Actions actions = new Actions(driver);
		//actions.moveToElement(PlusSign.get(j-1));
		//actions.perform();
		PlusSign.get(j-1).click();
		String SCAC;
		String trailerNb;
		String SCACTrailer=TrailerGrid.findElement(By.xpath("div["+j+"]/div/div[1]")).getText();
		if(SCACTrailer.matches("[a-zA-Z]+[\\d]+")){
		SCAC=SCACTrailer.substring(0, 4);
		trailerNb=SCACTrailer.substring(4);
		}else{
		SCAC="RDWY";
		trailerNb=SCACTrailer;
		}
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']//div[@class='ui-grid-contents-wrapper']/div[3]//div[@class='ui-grid-canvas']/child::div["+(j)+"]/div[@class='expandableRow']/div[@config='row.entity.subGridConfig']//div[@class='ui-grid-canvas']")));
		WebElement ProGridForEachTrailer=TrailerGrid.findElements(By.xpath("div[@class='ui-grid-row']")).get(j-1).findElement(By.xpath("div[@class='expandableRow']/div[@config='row.entity.subGridConfig']//div[@class='ui-grid-canvas']"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));		
		LinkedHashSet<ArrayList<String>> ProInformation= page.GetProListInInquiryScreen(ProGridForEachTrailer);
		LinkedHashSet<ArrayList<String>> ExpectedProInformation=DataForInQuiryScreen.GetProInformation(SCAC, trailerNb);
		Sassert.assertEquals(ProInformation, ExpectedProInformation,StatusName+" "+SCACTrailer );
		jse.executeScript("arguments[0].scrollIntoView(false);",PlusSign.get(j-1));
		PlusSign.get(j-1).click(); 
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.stalenessOf(ProGridForEachTrailer));
	}
	//ArrayList<ArrayList<String>> ExpectedTrailerInformation=DataForInQuiryScreen.GetTrailerInformation(terminal, StatusName, d);
	//Sassert.assertEquals(TrailerInformation,ExpectedTrailerInformation,"  "+StatusName);
	page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='panel-heading']")).click();	
	}}else{
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']")));		
	}
	Sassert.assertAll();
}  

@Test(priority=5,dataProvider = "1000.02",dataProviderClass=DataForInQuiryScreen.class)
 public void Filter(String terminal) throws ClassNotFoundException, SQLException, InterruptedException {
	SoftAssert Sassert= new SoftAssert(); 
	Wait<WebDriver> wait=new FluentWait<WebDriver>(driver).withTimeout(10, TimeUnit.SECONDS).pollingEvery(1, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
	JavascriptExecutor jse = (JavascriptExecutor)driver;
	ArrayList<String> ExpectedStatusList=DataForInQuiryScreen.GetStatusList(terminal);
	page.EQTerminalInput.clear();
	page.EQTerminalInput.sendKeys(terminal);
	page.SearchButton.click();
	if(ExpectedStatusList.size()!=0){
    Thread.sleep(500);
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));		
	(new WebDriverWait(driver, 50)).until(ExpectedConditions.visibilityOf(page.EQStatusList));	
	if(!page.FileterField.isDisplayed()){
	page.FilterButton.click();
	wait.until(ExpectedConditions.visibilityOf(page.FileterField));
	}
	if(!page.TrailerLengthFileterField.isDisplayed()){
	jse.executeScript("arguments[0].scrollIntoView(true);",page.TrailerLengthFileterButton);
	page.TrailerLengthFileterButton.click();}
	if(!page.SubTypeFileterField.isDisplayed()){
	jse.executeScript("arguments[0].scrollIntoView(true);",page.SubTypeFileterButton);
	page.SubTypeFileterButton.click();}
	ArrayList<String> leng=new ArrayList<String>();
	ArrayList<String> ty=new ArrayList<String>();
	
	//check length filter

	for(int c=0;c<3;c++){
	if(!page.FileterField.isDisplayed()){
		page.FilterButton.click();
		wait.until(ExpectedConditions.visibilityOf(page.FileterField));}
	jse.executeScript("arguments[0].scrollIntoView(true);",page.TrailerLengthFileterButton);
	page.TrailerLengthFileterField.findElement(By.cssSelector("button[label='Unselect All']")).click();// unselect all
	List<WebElement> AllLengthItem= page.TrailerLengthFileterField.findElements(By.tagName("yrc-checkbox"));
	int Ran=(int) (Math.random()* (AllLengthItem.size()-1))+1;
	WebElement SelectItem=page.TrailerLengthFileterField.findElement(By.xpath(".//yrc-checkbox["+Ran+"]//label"));
	SelectItem.click();
	String length=SelectItem.getText();
	System.out.println(length);
	leng.add(length);
	page.ApplyButton.click();
	Date d=CommonFunction.gettime("UTC");
	Thread.sleep(500);
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
	int CountStatus=page.EQStatusList.findElements(By.xpath("div")).size();
	for(int i=1;i<=CountStatus;i++){
		String StatusName=page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='trailer-inquiry-status']")).getText();
		jse.executeScript("arguments[0].scrollIntoView(false);",page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='panel-heading']")));
		page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='panel-heading']")).click();
		Thread.sleep(500);
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		WebElement TrailerGrid=page.EQStatusList.findElement(By.xpath("//div[@class='ui-grid-contents-wrapper']/div[3]//div[@class='ui-grid-canvas']"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(TrailerGrid));	
		//(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']//div[@class='ui-grid-canvas']/div")));
		
	int TrailerLine=TrailerGrid.findElements(By.xpath("div[@class='ui-grid-row']")).size();
	List<ArrayList<String>> TrailerInformation= new ArrayList<ArrayList<String>>();
	for(int j=1;j<=TrailerLine;j++){
		 String[] OneTrailerInoformation= TrailerGrid.findElements(By.xpath("div[@class='ui-grid-row']")).get(j-1).getText().split("\\n");
		 ArrayList<String> e= new ArrayList<String> (Arrays.asList(OneTrailerInoformation));
		 TrailerInformation.add(e);	
	}
	LinkedHashSet<ArrayList<String>> ExpectedTrailerInformation=DataForInQuiryScreen.GetTrailerInformationByFilter(terminal, StatusName, d,leng,ty);
	Sassert.assertEquals(TrailerInformation,ExpectedTrailerInformation,"  "+StatusName+"   "+length);
	page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='panel-heading']")).click();	
	
	}
	leng.clear();
	ty.clear();
	}  
	if(!page.FileterField.isDisplayed()){
	    page.FilterButton.click();
		wait.until(ExpectedConditions.visibilityOf(page.FileterField));}	
	jse.executeScript("arguments[0].scrollIntoView(true);",page.TrailerLengthFileterButton);
	page.TrailerLengthFileterField.findElement(By.cssSelector("button[label='Unselect All']")).click();
	
	// check sub type filter
	
	for(int c=0;c<3;c++){
	if(!page.FileterField.isDisplayed()){
	    page.FilterButton.click();
		wait.until(ExpectedConditions.visibilityOf(page.FileterField));}	
	page.SubTypeFileterField.findElement(By.cssSelector("button[label='Unselect All']")).click();
	List<WebElement> AllLengthItem= page.SubTypeFileterField.findElements(By.tagName("yrc-checkbox"));
	int Ran=(int) (Math.random()* (AllLengthItem.size()-1))+1;
	WebElement SelectItem=page.SubTypeFileterField.findElement(By.xpath(".//yrc-checkbox["+Ran+"]//label"));
	jse.executeScript("arguments[0].scrollIntoView(true);",SelectItem);
	SelectItem.click();
	String Subtype=SelectItem.getText();
	System.out.println(Subtype);
	ty.add(Subtype);
	page.ApplyButton.click();
	Date d=CommonFunction.gettime("UTC");
	Thread.sleep(500);
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
	int CountStatus=page.EQStatusList.findElements(By.xpath("div")).size();
	for(int i=1;i<=CountStatus;i++){
		String StatusName=page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='trailer-inquiry-status']")).getText();
		jse.executeScript("arguments[0].scrollIntoView(false);",page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='panel-heading']")));
		page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='panel-heading']")).click();
		Thread.sleep(500);
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
		WebElement TrailerGrid=page.EQStatusList.findElement(By.xpath("//div[@class='ui-grid-contents-wrapper']/div[3]//div[@class='ui-grid-canvas']"));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(TrailerGrid));	
	
		//(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']//div[@class='ui-grid-canvas']/div")));
		
	int TrailerLine=TrailerGrid.findElements(By.xpath("div[@class='ui-grid-row']")).size();
	List<ArrayList<String>> TrailerInformation= new ArrayList<ArrayList<String>>();
	for(int j=1;j<=TrailerLine;j++){
		 String[] OneTrailerInoformation= TrailerGrid.findElements(By.xpath("div[@class='ui-grid-row']")).get(j-1).getText().split("\\n");
		 ArrayList<String> e= new ArrayList<String> (Arrays.asList(OneTrailerInoformation));
		 TrailerInformation.add(e);	
	}
	LinkedHashSet<ArrayList<String>> ExpectedTrailerInformation=DataForInQuiryScreen.GetTrailerInformationByFilter(terminal, StatusName, d,leng,ty);
	Sassert.assertEquals(TrailerInformation,ExpectedTrailerInformation,"  "+StatusName+"   "+Subtype);
	page.EQStatusList.findElement(By.xpath("div["+i+"]//div[@class='panel-heading']")).click();	
	
	}
	leng.clear();
	ty.clear();
	}
	if(!page.FileterField.isDisplayed()){
	    page.FilterButton.click();
		wait.until(ExpectedConditions.visibilityOf(page.FileterField));}	
	page.SubTypeFileterField.findElement(By.cssSelector("button[label='Unselect All']")).click();
	}else{
	(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(".//div[@class='trailer-inquiry-content']//div[@class='panel-group']")));		
	}
	Sassert.assertAll();		
}
 
 
 

  @AfterMethod
  public void Close() {
	  driver.close();
  }

}
