package Page;

import java.awt.AWTException;
import java.awt.Robot;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.TimeZone;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import Function.DataCommon;

public class EqpStatusPageS {

	WebDriver driver;
	Robot r;

	public EqpStatusPageS(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);

	}

	public String AD_ID = "UYR27B0";
	public String M_ID = "UYR27B0";

	/* Common */

	@FindBy(how = How.LINK_TEXT, using = "STATUS TRAILER")
	public WebElement StatusTrailerButton;

	@FindBy(how = How.LINK_TEXT, using = "TRAILERS BY TERMINAL")
	public WebElement TrailerByTerminalButton;

	@FindBy(how = How.LINK_TEXT, using = "LOAD TO ENR")
	public WebElement LOADTOENRbutton;

	@FindBy(how = How.XPATH, using = ".//*[@id='mainBody']/h3")
	public WebElement TitleOfScreen;

	@FindBy(how = How.LINK_TEXT, using = "FAQ")
	public WebElement FAQ;

	@FindBy(how = How.XPATH, using = ".//img[@src='assets/images/yrcf_logo_other.png']")
	public WebElement YrcLogoIcon;

	@FindBy(how = How.XPATH, using = ".//*[@label='Set Status To']/div/div/span")
	public WebElement SetStatusToField;

	@FindBy(how = How.XPATH, using = ".//*[@label='Set Status To']/div/input[1]")
	public WebElement SetStatusToInput;

	@FindBy(how = How.XPATH, using = ".//*[@label='Set Status To']/div/ul")
	public WebElement StatusList;

	@FindBy(how = How.CSS, using = "input[name^='trailerVM.trailer.locationCode_']")
	public WebElement TerminalField;

	@FindBy(how = How.CSS, using = "div[name^='trailerVM.trailer.trailerSCACAndNumber_'] input")
	public WebElement TrailerInputField;

	@FindBy(how = How.CSS, using = "div[name^='trailerVM.trailer.trailerSCACAndNumber_']>div>div>span")
	public WebElement TrailerField;

	@FindBy(how = How.XPATH, using = ".//*[@label='SCAC']/div/div/span")
	public WebElement SCACField;

	@FindBy(how = How.CSS, using = "input[name^='trailerStatusVM.trailer.trailerDestination_']")
	public WebElement DestinationField;

	@FindBy(how = How.XPATH, using = "html/body/div[1]/div")
	public WebElement ErrorAndWarningField;

	@FindBy(how = How.CSS, using = ".growl-item.alert.ng-scope.alert-error.alert-danger.icon[id^='notification_']")
	public WebElement ErrorMessage;

	@FindBy(how = How.CSS, using = ".growl-item.alert.ng-scope.alert-success.icon.alert-dismissable[id^='notification_']")
	public WebElement SuccessMessage;

	@FindBy(how = How.CSS, using = ".growl-item.alert.ng-scope.alert-warning.icon.alert-dismissable[id^='notification_']")
	public WebElement WarningMessage;

	@FindBy(how = How.CSS, using = "button[label='Submit']")
	public WebElement SubmitButton;

	// date picker

	@FindBy(how = How.CSS, using = "input[name^='model_']")
	public WebElement DateInput;

	@FindBy(how = How.CSS, using = "input[name='hour_0']")
	public WebElement HourInput;

	@FindBy(how = How.CSS, using = "input[name='minute_0']")
	public WebElement MinuteInput;

	@FindBy(how = How.CSS, using = "div[class='curtain']")
	public WebElement Curtain;

	/* ldg */

	@FindBy(how = How.CSS, using = "input[name^='addProsVM.addProsInput_']")
	public WebElement AddProField;

	@FindBy(how = How.XPATH, using = ".//div[@config='addProsVM.addProsGrid.config']//div[@class='ui-grid-selection-row-header-buttons ui-grid-icon-ok']")
	public WebElement CheckAllAddProButton;

	@FindBy(how = How.NAME, using = "submitLDG")
	public WebElement SubmitButton1;

	@FindBy(how = How.ID, using = "updatedTrailerNum")
	public WebElement UpdatedTrailerNum;

	@FindBy(how = How.XPATH, using = ".//div[@config='addProsVM.addProsGrid.config']/div/div/div[3]/div[2]/div")
	public WebElement AddProForm;

	@FindBy(how = How.XPATH, using = ".//div[@config='addProsVM.addProsGrid.config']/div/div[2]/div/div[2]/div")
	public WebElement AddProCheckBoxList;

	@FindBy(how = How.CSS, using = ".btn.btn-default.yrc-button[label='Remove']")
	public WebElement RemoveProButton;

	@FindBy(how = How.XPATH, using = ".//div[@config='trailerStatusVM.currentProsConfig.config']/div/div/div[3]/div[2]/div")
	public WebElement ProListForm;

	@FindBy(how = How.XPATH, using = ".//div[@config='trailerStatusVM.currentProsConfig.config']/div/div/div[2]/div/div[2]/div")
	public WebElement ProListCheckboxListldg;

	@FindBy(how = How.XPATH, using = ".//div[@config='trailerStatusVM.currentProsConfig.config']//div[@class='ui-grid-selection-row-header-buttons ui-grid-icon-ok']")
	public WebElement ProListCheckAllProUncheck;

	@FindBy(how = How.XPATH, using = ".//div[@config='trailerStatusVM.currentProsConfig.config']//div[@class='ui-grid-selection-row-header-buttons ui-grid-icon-ok ui-grid-all-selected']")
	public WebElement ProListCheckAllProChecked;

	@FindBy(how = How.XPATH, using = "//span[contains(text(), 'Ship Count')]/following-sibling::div")
	public WebElement ShipCount1;

	@FindBy(how = How.XPATH, using = "//span[contains(text(), 'Ship Weight')]/following-sibling::div")
	public WebElement ShipWeight1;

	@FindBy(how = How.XPATH, using = "//span[contains(text(), 'HL Dest')]/following-sibling::div")
	public WebElement HLDestLdg;

	@FindBy(how = How.XPATH, using = "//span[contains(text(), 'HL Cube')]/following-sibling::div")
	public WebElement HLCubeLdg;

	@FindBy(how = How.NAME, using = "dockPros")
	public WebElement DockProButton;

	@FindBy(how = How.CSS, using = "button[label='Submit & Close Out']")
	public WebElement SubmitAndCloseOutButton;

	/* quickclose */

	@FindBy(how = How.NAME, using = "cancelQuickClose")
	public WebElement qcCancelButton;

	@FindBy(how = How.NAME, using = "quickClose")
	public WebElement qcCloseTrailerButton;

	@FindBy(how = How.CSS, using = "input[name^='trailerVM.trailer.trailerDestination_']")
	public WebElement qcDestination;

	@FindBy(how = How.CSS, using = "input[name^='trailerVM.trailer.trailerShipmentCount_']")
	public WebElement qcShipmentCount;

	@FindBy(how = How.CSS, using = "input[name^='trailerVM.trailer.trailerShipmentWeight_']")
	public WebElement qcShipmentWeight;

	@FindBy(how = How.CSS, using = "input[name^='trailerVM.trailer.trailerCube_']")
	public WebElement qcEnrCubeField;

	@FindBy(how = How.CSS, using = "input[name^='trailerVM.trailer.trailerSealNumber_']")
	public WebElement qcSealField;

	/* ldd */

	@FindBy(how = How.ID, using = "shipmentFlagContainer")
	public WebElement ShipmentFlag;

	@FindBy(how = How.ID, using = "serviceFlagContainer")
	public WebElement ServiceFlag;

	@FindBy(how = How.NAME, using = "shipmentFlagsSummary")
	public WebElement ShipmentFlagExclamation;

	@FindBy(how = How.NAME, using = "serviceFlagsSummary")
	public WebElement ServiceFlagExclamation;

	@FindBy(how = How.XPATH, using = ".//*[@name='shipmentFlagsSummary']/following-sibling::div/div[2]")
	public WebElement ShipmentFlagLegend;

	@FindBy(how = How.XPATH, using = ".//*[@name='serviceFlagsSummary']/following-sibling::div/div[2]")
	public WebElement ServiceFlagLegend;

	@FindBy(how = How.CSS, using = "input[name^='trailerStatusVM.trailer.trailerShipmentCount_']")
	public WebElement ShipmentCount2;

	@FindBy(how = How.CSS, using = "input[name^='trailerStatusVM.trailer.trailerShipmentWeight_']")
	public WebElement ShipmentWeight2;

	@FindBy(how = How.CSS, using = "input[name^='trailerStatusVM.trailer.trailerCube_']")
	public WebElement CubeField;

	@FindBy(how = How.CSS, using = "input[name^='trailerStatusVM.trailer.trailerHeadloadCube_']")
	public WebElement HeadloadCube;

	@FindBy(how = How.CSS, using = "input[name^='trailerStatusVM.trailer.trailerHeadloadDestination_']")
	public WebElement HeadloadDestination;

	@FindBy(how = How.CSS, using = "input[name^='trailerStatusVM.trailer.trailerSealNumber_']")
	public WebElement SealField;

	@FindBy(how = How.XPATH, using = ".//div[@label='Current PROs on trailer']/div/div/div[2]/div[2]/div")
	public WebElement ProListSecondForm;

	@FindBy(how = How.XPATH, using = "//span[contains(text(), 'Comments ')]")
	public WebElement CommentButton;

	@FindBy(how = How.CSS, using = "input[name^='trailerStatusVM.trailer.comments_']")
	public WebElement CommentInput;

	/* Alert */

	@FindBy(how = How.XPATH, using = "html/body/div[4]/div/div")
	// @FindBy(how = How.CSS, using = "div.modal-content")
	public WebElement AlertMessage;

	@FindBy(how = How.XPATH, using = "html/body/div[4]/div/div/div[3]/button[1]")
	public WebElement ChangeWeightButton;

	@FindBy(how = How.XPATH, using = "html/body/div[4]/div/div/div[3]/button[2]")
	public WebElement CorrectWeightButton;

	/* left over bill */

	@FindBy(how = How.XPATH, using = ".//*[@config='trailerStatusVM.lobrProsOnTrailerGrid.config']/div/div/div[3]/div[2]/div")
	public WebElement LeftoverBillForm;

	@FindBy(how = How.XPATH, using = ".//*[@config='trailerStatusVM.lobrProsOnTrailerGrid.config']//div[@class='ui-grid-selection-row-header-buttons ui-grid-icon-ok']")
	public WebElement LeftoverCheckAllPRO;

	@FindBy(how = How.XPATH, using = ".//*[@config='trailerStatusVM.lobrProsOnTrailerGrid.config']//div[@class='ui-grid-selection-row-header-buttons ui-grid-icon-ok ui-grid-all-selected']")
	public WebElement LeftoverUnCheckAllPRO;

	@FindBy(how = How.NAME, using = "headloadPros")
	public WebElement HEADLOADButton;

	@FindBy(how = How.NAME, using = "leavePros")
	public WebElement LEAVEONButton;

	@FindBy(how = How.NAME, using = "dockPros")
	public WebElement DOCKButton;

	@FindBy(how = How.NAME, using = "shortPros")
	public WebElement ALLSHORTButton;

	@FindBy(how = How.NAME, using = "finishReview")
	public WebElement LobrSubmitButton;

	@FindBy(how = How.NAME, using = "abandon")
	public WebElement LobrCancelButton;

	/* headload */

	@FindBy(how = How.XPATH, using = ".//*[@config='trailerStatusVM.headloadProsOnTrailerGrid.config']/div/div/div[3]/div[2]/div")
	public WebElement HeadloadProForm;

	@FindBy(how = How.NAME, using = "setHeadload")
	public WebElement YesButton;

	@FindBy(how = How.NAME, using = "updateTrailerDest")
	public WebElement NoButton;

	@FindBy(how = How.NAME, using = "abandon")
	public WebElement hlCancelButton;

	@FindBy(how = How.XPATH, using = ".//h4[contains(text(), 'Shipments loaded on trailer going to')]/u/mark")
	public WebElement ManifestToField;

	@FindBy(how = How.CSS, using = "input[name^='trailerStatusVM.trailer.trailerHeadloadCube_']")
	public WebElement HLCubeField;

	@FindBy(how = How.CSS, using = "input[name^='trailerStatusVM.trailer.trailerHeadloadDestination_']")
	public WebElement HLDestField;

	// cl & cltg

	@FindBy(how = How.CSS, using = "input[name^='trailerStatusVM.trailer.cityRouteName_']")
	public WebElement CityRoute;

	@FindBy(how = How.CSS, using = "input[name^='trailerStatusVM.trailer.trailerPlanDate_']")
	public WebElement PlanDate;

	@FindBy(how = How.CSS, using = "div[label='City Route Type']>div>div>span")
	public WebElement CityRouteTypeField;

	@FindBy(how = How.CSS, using = "div[label='City Route Type']>div>input")
	public WebElement CityRouteTypeInput;

	@FindBy(how = How.CSS, using = "div[label='City Route Type']>div>ul>li")
	public WebElement CityRouteTypeList;

	/* INQUIRY SCREEN */
	// @FindBy(how = How.XPATH, using =
	// ".//input[starts-with(@name,'trailerInquiryVM.terminalNumber')]")
	// @FindBy(how = How.XPATH, using =
	// ".//input[contains(@name,'trailerInquiryVM.terminalNumber')]")
	@FindBy(how = How.CSS, using = "input[name^='trailerInquiryVM.terminalNumber_']")
	public WebElement IQTerminalInput;

	@FindBy(how = How.XPATH, using = ".//div[starts-with(@class,'trailer-inquiry-content')]//div[@class='panel-group']")
	public WebElement IQStatusList;

	@FindBy(how = How.CSS, using = "button[label='Filters']")
	public WebElement FilterButton;

	@FindBy(how = How.XPATH, using = "//h4[contains(text(), 'Filters')]/parent::div")
	public WebElement FileterField;

	@FindBy(how = How.XPATH, using = ".//span[contains(text(), 'Trailer Status')]")
	public WebElement TrailerStatusFileterButton;

	@FindBy(how = How.XPATH, using = "//div[contains(text(), 'Trailer Status')]/following-sibling::div/div")
	public WebElement TrailerStatusFileterField;

	@FindBy(how = How.XPATH, using = ".//span[contains(text(), 'Sub type')]")
	public WebElement SubTypeFileterButton;

	@FindBy(how = How.XPATH, using = ".//span[contains(text(), 'Sub type')]/ancestor::div[@class='panel-heading']/following-sibling::div/div")
	public WebElement SubTypeFileterField;

	@FindBy(how = How.XPATH, using = ".//span[contains(text(), 'Trailer Length')]")
	public WebElement TrailerLengthFileterButton;

	@FindBy(how = How.XPATH, using = ".//span[contains(text(), 'Trailer Length')]/ancestor::div[@class='panel-heading']/following-sibling::div/div")
	public WebElement TrailerLengthFileterField;

	@FindBy(how = How.CSS, using = "button[label='Apply']")
	public WebElement ApplyButton;

	@FindBy(how = How.CSS, using = "button[label='Search']")
	public WebElement SearchButton;

	// load to enr
	@FindBy(how = How.XPATH, using = ".//div[@config='trailerVM.currentProsConfig.config']/div/div/div[3]/div[2]/div")
	public WebElement LENRProListForm;

	// UAD
	@FindBy(how = How.NAME, using = "submitUAD")
	public WebElement UadSubmit;

	// UI method

	public void LoadNewPRO(int proamount) throws ClassNotFoundException, SQLException, InterruptedException {
		ArrayList<String> PRO = DataCommon.GetProNotInAnyTrailer();
		for (int i = 0; i < proamount; i++) {
			String CurrentPro = PRO.get(i);
			this.EnterPro(CurrentPro);
		}
	}

	public void AddComment(String Comment) {
		if (!this.CommentInput.isDisplayed()) {
			this.CommentButton.click();
		}
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(this.CommentInput));
		this.CommentInput.clear();
		this.CommentInput.sendKeys(Comment);

	}

	public void SetInquiryScreen() {
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(this.TrailerByTerminalButton));
		this.TrailerByTerminalButton.click();
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(this.IQTerminalInput));
	}

	public void SetStatus(String status) throws InterruptedException {
		(new WebDriverWait(driver, 150)).until(ExpectedConditions.visibilityOf(this.StatusTrailerButton));
		this.StatusTrailerButton.click();
		(new WebDriverWait(driver, 20)).until(
				ExpectedConditions.presenceOfElementLocated(By.xpath(".//*[@label='Set Status To']/div/div/span")));
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(this.SetStatusToField));
		ChangeStatusTo(status);
	}

	public void SetToLoadEnrScreen() {
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(this.LOADTOENRbutton));
		this.LOADTOENRbutton.click();
		(new WebDriverWait(driver, 20))
				.until(ExpectedConditions.textToBePresentInElement(this.TitleOfScreen, "Load To ENR"));
	}

	public void ChangeStatusTo(String status) throws InterruptedException {
		Actions builder = new Actions(driver);
		this.SetStatusToField.click();
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(this.StatusList));
		this.SetStatusToInput.click();
		// this.SetStatusToInput.sendKeys(status);
		// Thread.sleep(1000);
		String FullStatusName = null;

		if (status.equalsIgnoreCase("ldd")) {
			FullStatusName = "LDD - Linehaul Closed";
		} else if (status.equalsIgnoreCase("ldg")) {
			FullStatusName = "LDG - Linehaul Loading";
		} else if (status.equalsIgnoreCase("bor")) {
			FullStatusName = "BOR - Bad Order Requested";
		} else if (status.equalsIgnoreCase("cl")) {
			FullStatusName = "CL - City Loading";
		} else if (status.equalsIgnoreCase("cltg")) {
			FullStatusName = "CLTG - City Closed";
		} else if (status.equalsIgnoreCase("cpu")) {
			FullStatusName = "CPU - City Pickup";
		} else if (status.equalsIgnoreCase("mty")) {
			FullStatusName = "MTY - Empty";
		} else if (status.equalsIgnoreCase("ofd")) {
			FullStatusName = "OFD - Out for Delivery";
		} else if (status.equalsIgnoreCase("uad")) {
			FullStatusName = "UAD - Unloading";
		}
		(new WebDriverWait(driver, 2))
				.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(FullStatusName)));
		// (new WebDriverWait(driver,
		// 2)).until(ExpectedConditions.textToBePresentInElement(this.TrailerField,
		// FullStatusName));
		WebElement SelectStatus = this.StatusList.findElement(By.linkText(FullStatusName));
		builder.doubleClick(SelectStatus).build().perform();

	}

	public void SetLocation(String terminalcd) throws AWTException, InterruptedException {
		Actions builder = new Actions(driver);
		r = new Robot();
		this.TerminalField.click();
		this.TerminalField.clear();
		this.TerminalField.sendKeys(terminalcd);
		// r.keyPress(KeyEvent..VK_TAB);
		// r.keyRelease(KeyEvent.VK_TAB);
		builder.sendKeys(Keys.TAB).build().perform();
		// this.Title.click();
		Thread.sleep(1500);
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));

	}

	public String SCACTrailer(String SCAC, String TrailerNB) {
		String SCACTrailer;
		if (SCAC.equalsIgnoreCase("RDWY")) {
			SCACTrailer = TrailerNB;
		} else {
			SCACTrailer = SCAC + " " + TrailerNB;
		}
		return SCACTrailer;

	}

	public void EnterTrailer(String SCAC, String TrailerNB) throws AWTException, InterruptedException {
		Actions builder = new Actions(driver);
		if (TrailerField.isDisplayed()) {
			this.TrailerField.click();
		}
		this.TrailerInputField.click();
		this.TrailerInputField.clear();
		this.TrailerInputField.sendKeys(TrailerNB);
		this.TrailerInputField.click();
		(new WebDriverWait(driver, 2))
				.until(ExpectedConditions.textToBePresentInElementValue(this.TrailerInputField, TrailerNB));
		if (driver instanceof InternetExplorerDriver) {
			builder.sendKeys(this.TrailerInputField, Keys.SPACE, Keys.SPACE, Keys.SPACE, Keys.TAB).build().perform();
		} else {
			builder.sendKeys(this.TrailerInputField, Keys.TAB).build().perform();
		}
		// builder.sendKeys(this.TrailerInputField,Keys.TAB).build().perform();
		String SCACTrailer = SCACTrailer(SCAC, TrailerNB);
		try {
			(new WebDriverWait(driver, 2))
					.until(ExpectedConditions.textToBePresentInElement(this.TrailerField, SCACTrailer));
			// TimeoutException
		} catch (Exception e) {
			(new WebDriverWait(driver, 20))
					.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(SCACTrailer)));
			driver.findElement(By.linkText(SCACTrailer)).click();
		}
		(new WebDriverWait(driver, 5))
				.until(ExpectedConditions.textToBePresentInElement(this.TrailerField, SCACTrailer));
		Thread.sleep(1000);
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
	}

	public void SetDestination(String desti) throws InterruptedException {
		Actions builder = new Actions(driver);
		this.DestinationField.click();
		this.DestinationField.clear();
		this.DestinationField.sendKeys(desti);
		builder.sendKeys(Keys.TAB).build().perform();
		Thread.sleep(1000);
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar")));
	}

	public void SetCityRoute(String CityRoute) throws InterruptedException {
		Actions builder = new Actions(driver);
		this.CityRoute.clear();
		this.CityRoute.click();
		this.CityRoute.sendKeys(CityRoute);
		builder.sendKeys(Keys.TAB).build().perform();
	}

	public String SetCityRouteType(String CityRouteType) {
		WebDriverWait Wait = new WebDriverWait(driver, 20);
		this.CityRouteTypeField.click();
		Wait.until(ExpectedConditions.visibilityOf(this.CityRouteTypeList));
		String CityRouteTypeName = null;

		if (CityRouteType.equalsIgnoreCase("appt")) {
			CityRouteTypeName = "APPT";
		} else if (CityRouteType.equalsIgnoreCase("CARTAGE")) {
			CityRouteTypeName = "CARTAGE";
		} else if (CityRouteType.equalsIgnoreCase("INTERLINE")) {
			CityRouteTypeName = "INTERLINE";
		} else if (CityRouteType.equalsIgnoreCase("PEDDLE")) {
			CityRouteTypeName = "PEDDLE";
		} else if (CityRouteType.equalsIgnoreCase("TRAP")) {
			CityRouteTypeName = "TRAP";
		}

		this.CityRouteTypeList.findElement(By.linkText(CityRouteTypeName)).click();
		Wait.until(ExpectedConditions.visibilityOf(this.CityRouteTypeField));
		Wait.until(ExpectedConditions.textToBePresentInElement(this.CityRouteTypeField, CityRouteTypeName));
		return CityRouteTypeName;
	}

	public void SetCube(String Cube) throws InterruptedException {
		Actions builder = new Actions(driver);
		this.CubeField.clear();
		this.CubeField.click();
		this.CubeField.sendKeys(Cube);
		builder.sendKeys(Keys.TAB).build().perform();
	}

	public void SetSealLDD(String Seal) throws InterruptedException {
		Actions builder = new Actions(driver);
		this.SealField.clear();
		this.SealField.click();
		this.SealField.sendKeys(Seal);
		builder.sendKeys(Keys.TAB).build().perform();

	}

	public void SetSealQC(String Seal) throws InterruptedException {
		Actions builder = new Actions(driver);
		this.qcSealField.clear();
		this.qcSealField.click();
		this.qcSealField.sendKeys(Seal);
		builder.sendKeys(Keys.TAB).build().perform();

	}

	public void SetShipCount(String shipcount) throws InterruptedException {
		Actions builder = new Actions(driver);
		this.ShipmentCount2.clear();
		this.ShipmentCount2.click();
		this.ShipmentCount2.sendKeys(shipcount);
		builder.sendKeys(Keys.TAB).build().perform();

	}

	public void SetShipWeight(String shipweight) throws InterruptedException {
		Actions builder = new Actions(driver);
		this.ShipmentWeight2.clear();
		this.ShipmentWeight2.click();
		this.ShipmentWeight2.sendKeys(shipweight);
		builder.sendKeys(Keys.TAB).build().perform();

	}

	public void SetHLDest(String HLD) throws InterruptedException {
		Actions builder = new Actions(driver);
		this.HeadloadDestination.clear();
		this.HeadloadDestination.click();
		this.HeadloadDestination.sendKeys(HLD);
		builder.sendKeys(Keys.TAB).build().perform();

	}

	public void SetHeadloadCube(String HLC) throws InterruptedException {
		Actions builder = new Actions(driver);
		this.HeadloadCube.clear();
		this.HeadloadCube.click();
		this.HeadloadCube.sendKeys(HLC);
		builder.sendKeys(Keys.TAB).build().perform();

	}

	public String ChangeDestiantion() throws InterruptedException {
		String originDestiantion = this.DestinationField.getAttribute("value");
		String[] dest = { "851", "112", "841", "270", "198", "135" };
		String changeDesti = null;
		do {
			changeDesti = dest[new Random().nextInt(dest.length)];
		} while (changeDesti.equalsIgnoreCase(originDestiantion));
		this.SetDestination(changeDesti);
		return changeDesti;
	}

	public String ChangeCube() throws InterruptedException {
		int Ran = (int) (Math.random() * 99) + 1;
		String NewCube = Integer.toString(Ran);
		this.SetCube(NewCube);
		return NewCube;
	}

	public String UpdateCityRoute() throws InterruptedException {
		int length = (int) ((Math.random() * 9) + 1);
		String CityRoute = RandomStringUtils.randomAlphanumeric(length);
		this.SetCityRoute(CityRoute);
		return CityRoute.toUpperCase();
	}

	public void EnterPro(String Pro) throws InterruptedException {
		Actions builder = new Actions(driver);
		this.AddProField.click();
		this.AddProField.clear();
		String Pronumber = Pro.trim().toUpperCase();
		String CurrentProH = this.addHyphenToPro(Pronumber);
		this.AddProField.sendKeys(Pro);
		try {
			(new WebDriverWait(driver, 3))
					.until(ExpectedConditions.textToBePresentInElement(this.AddProForm, CurrentProH));
		} catch (Exception e) {
			this.AddProField.click();
			builder.sendKeys(this.AddProField, Keys.TAB).build().perform();
		}

		Thread.sleep(500);
		int NEW1 = this.AddProForm.findElements(By.xpath("div")).size();
		if (NEW1 > 25) {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].scrollIntoView(false);",
					this.AddProForm.findElement(By.xpath("div[" + NEW1 + "]/div/div[2]/div")));
		}
		try {
			Assert.assertEquals(this.AddProForm.findElement(By.xpath("div[" + NEW1 + "]/div/div[2]/div")).getText(),
					CurrentProH);
		} catch (Exception e) {
			System.out.println(Pronumber + " is not kick to the grid");
		}

	}

	public WebElement ErrorAndWarning(String ErrorMessage) {
		return driver.findElement(By.xpath("//*[contains(text(), '" + ErrorMessage + "')]"));
	}

	public String addHyphenToPro(String PRONB) {
		String pronb = PRONB.substring(0, 3) + "-" + PRONB.substring(3, 9) + "-" + PRONB.substring(9, PRONB.length());
		return pronb;
	}

	public boolean CheckErrorAndWarningMessageDisplay(String ErrorMessage) {
		try {
			return this.ErrorAndWarning(ErrorMessage).isDisplayed();
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	public boolean CheckDisplay(WebElement element) {
		try {
			return element.isDisplayed();
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	public void HandleLOBRproAll(String handle) throws InterruptedException {
		Actions builder = new Actions(driver);
		try {
			if (this.LeftoverCheckAllPRO.isDisplayed()) {
				this.LeftoverCheckAllPRO.click();
			}
		} catch (Exception e) {
		}

		if (handle.equalsIgnoreCase("headload")) {
			String[] dest = { "270", "112", "841", "198", "135" };

			int ran = new Random().nextInt(dest.length);
			String changeDesti = dest[ran];
			String hldesti = changeDesti;
			this.HeadloadDestination.clear();
			this.HeadloadDestination.sendKeys(hldesti);
			builder.sendKeys(Keys.TAB).build().perform();
			int Ran = (int) (Math.random() * 99) + 1;
			String HeadloadCube = Integer.toString(Ran);
			this.HeadloadCube.clear();
			this.HeadloadCube.sendKeys(HeadloadCube);
			builder.sendKeys(Keys.TAB).build().perform();
			this.HEADLOADButton.click();
			// Thread.sleep(2000);
		} else if (handle.equalsIgnoreCase("leaveon")) {
			this.LEAVEONButton.click();
			// Thread.sleep(2000);
		} else if (handle.equalsIgnoreCase("allshort")) {
			this.ALLSHORTButton.click();
			(new WebDriverWait(driver, 50))
					.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
			(new WebDriverWait(driver, 50))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		} else if (handle.equalsIgnoreCase("dock")) {
			this.DOCKButton.click();
			(new WebDriverWait(driver, 50))
					.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
			(new WebDriverWait(driver, 50))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		}
	}

	public void HandleLOBRpro(String handle) throws InterruptedException {

		if (handle.equalsIgnoreCase("headload")) {
			this.HEADLOADButton.click();
			Thread.sleep(2000);
		} else if (handle.equalsIgnoreCase("leaveon")) {
			this.LEAVEONButton.click();
			Thread.sleep(2000);
		} else if (handle.equalsIgnoreCase("allshort")) {
			this.ALLSHORTButton.click();
			(new WebDriverWait(driver, 50))
					.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
			(new WebDriverWait(driver, 50))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		} else if (handle.equalsIgnoreCase("dock")) {
			this.DOCKButton.click();
			(new WebDriverWait(driver, 50))
					.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
			(new WebDriverWait(driver, 50))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("html/body/div[4]/div/div")));
		}
	}

	public Date GetDatePickerTime() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		String DateAndTime = this.DateInput.getAttribute("value") + " " + this.HourInput.getAttribute("value") + ":"
				+ this.MinuteInput.getAttribute("value");
		SimpleDateFormat PickerDate = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(PickerDate.parse(DateAndTime));
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		return cal.getTime();
	}

	public void SetDate(String Day) {
		this.DateInput.clear();
		this.DateInput.sendKeys(Day);
	}

	public void SetHour(String Hour) {
		this.HourInput.clear();
		this.HourInput.sendKeys("9");
		this.HourInput.click();
		this.HourInput.sendKeys(Hour);
	}

	public void SetHour1(String Hour) {
		Actions builder = new Actions(driver);
		this.HourInput.clear();
		String firstKey = Hour.substring(0, 1);
		String secondKey = Hour.substring(1, 2);
		if (driver instanceof InternetExplorerDriver) {
			this.HourInput.sendKeys(firstKey);
			this.HourInput.sendKeys(secondKey);
		} else
			builder.sendKeys(this.HourInput, firstKey).sendKeys(this.HourInput, secondKey).build().perform();

	}

	public void SetMinute(String minute) {
		this.MinuteInput.clear();
		this.MinuteInput.sendKeys(minute);
	}

	public void SetPlanDay(String Day) {
		this.PlanDate.clear();
		this.PlanDate.sendKeys(Day);
	}

	public void SetDatePicker(Date SetDate, int AlterHour) {
		Actions builder = new Actions(driver);
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		cal.setTime(SetDate);
		cal.add(Calendar.HOUR_OF_DAY, AlterHour);
		int hourOfDay = cal.get(Calendar.HOUR_OF_DAY); // 24 hour clock
		String hour = String.format("%02d", hourOfDay);
		int minute = cal.get(Calendar.MINUTE);
		String Minute = String.format("%02d", minute);
		String DATE = dateFormat.format(cal.getTime());
		this.SetDate(DATE);
		this.SetHour1(hour);
		this.SetMinute(Minute);
		builder.sendKeys(Keys.TAB).build().perform();
	}

	public void SetDatePicker2(Date SetDate, int AlterHour, int AlterMinute) {
		Actions builder = new Actions(driver);
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		cal.setTime(SetDate);
		if (AlterHour != 0)
			cal.add(Calendar.HOUR_OF_DAY, AlterHour);
		if (AlterMinute != 0)
			cal.add(Calendar.MINUTE, AlterMinute);
		int hourOfDay = cal.get(Calendar.HOUR_OF_DAY); // 24 hour clock
		String hour = String.format("%02d", hourOfDay);
		int minute = cal.get(Calendar.MINUTE);
		String Minute = String.format("%02d", minute);
		String DATE = dateFormat.format(cal.getTime());
		this.SetDate(DATE);
		this.SetHour1(hour);
		this.SetMinute(Minute);
		builder.sendKeys(Keys.TAB).build().perform();
	}

	public Date SetPlanDate(Date LocalTime, int AlterDay) {
		Actions builder = new Actions(driver);
		Calendar cal = Calendar.getInstance();
		cal.setTime(LocalTime);
		if (AlterDay != 0)
			cal.add(Calendar.DAY_OF_MONTH, AlterDay);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date PlanDay = cal.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		String DATE = dateFormat.format(PlanDay);
		this.SetPlanDay(DATE);
		builder.sendKeys(Keys.TAB).build().perform();
		return PlanDay;

	}

	public Date GetPlanDatePickerTime() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		String PlanDay = this.PlanDate.getAttribute("value");
		if (PlanDay.equalsIgnoreCase("")) {
			return null;
		} else {
			SimpleDateFormat PickerDate = new SimpleDateFormat("MM/dd/yyyy");
			Calendar cal = Calendar.getInstance();
			try {
				cal.setTime(PickerDate.parse(PlanDay));
			} catch (ParseException pe) {
				pe.printStackTrace();
			}
			return cal.getTime();
		}
	}

	public LinkedHashSet<ArrayList<String>> GetProList(WebElement ProGrid) {
		int line = ProGrid.findElements(By.xpath("div")).size();
		// Set<ArrayList<String>> ProInfo= new HashSet<ArrayList<String>>(); //
		// dont sort the pro list
		LinkedHashSet<ArrayList<String>> ProInfo = new LinkedHashSet<ArrayList<String>>(); // sort
																							// the
																							// prolist
		for (int i = 1; i <= line; i++) {
			String[] Proline1 = ArrayUtils
					.remove(ProGrid.findElement(By.xpath("div[" + i + "]")).getText().split("\\n"), 0);
			// String[] Proline1=
			// page.ProListForm.findElement(By.xpath("div["+i+"]")).getText().split("\\n");
			ArrayList<String> e1 = new ArrayList<String>(Arrays.asList(Proline1));
			ProInfo.add(e1);
		}
		if (line >= 31) {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			int additional = 31;
			do {
				jse.executeScript("arguments[0].scrollIntoView(true);",
						ProGrid.findElement(By.xpath("div[" + additional + "]")));
				additional = ProGrid.findElements(By.xpath("div")).size();
				for (int j = 1; j <= additional; j++) {
					String[] Proline1 = ArrayUtils
							.remove(ProGrid.findElement(By.xpath("div[" + j + "]")).getText().split("\\n"), 0);
					ArrayList<String> e1 = new ArrayList<String>(Arrays.asList(Proline1));
					ProInfo.add(e1);
				}
			} while (additional > 31);
			int Rest = ProGrid.findElements(By.xpath("div")).size();
			for (int j = 1; j <= Rest; j++) {
				String[] Proline1 = ArrayUtils
						.remove(ProGrid.findElement(By.xpath("div[" + j + "]")).getText().split("\\n"), 0);
				ArrayList<String> e1 = new ArrayList<String>(Arrays.asList(Proline1));
				ProInfo.add(e1);
			}
		}
		return ProInfo;
	}

	public LinkedHashSet<ArrayList<String>> GetProListInInquiryScreen(WebElement ProGrid) throws InterruptedException {
		int line = ProGrid.findElements(By.xpath("div")).size();
		LinkedHashSet<ArrayList<String>> ProInfo = new LinkedHashSet<ArrayList<String>>();
		for (int i = 1; i <= line; i++) {
			String[] Proline1 = ProGrid.findElement(By.xpath("div[" + i + "]")).getText().split("\\n");
			ArrayList<String> e1 = new ArrayList<String>(Arrays.asList(Proline1));
			ProInfo.add(e1);
		}
		if (line >= 27) {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			int additional = 27;
			do {
				jse.executeScript("arguments[0].scrollIntoView(true);",
						ProGrid.findElement(By.xpath("div[" + additional + "]")));
				Thread.sleep(500);
				additional = ProGrid.findElements(By.xpath("div")).size();
				for (int j = 1; j <= additional; j++) {
					String[] Proline1 = ProGrid.findElement(By.xpath("div[" + j + "]")).getText().split("\\n");
					ArrayList<String> e1 = new ArrayList<String>(Arrays.asList(Proline1));
					ProInfo.add(e1);
				}
			} while (additional > 27);
		}
		return ProInfo;
	}

}
