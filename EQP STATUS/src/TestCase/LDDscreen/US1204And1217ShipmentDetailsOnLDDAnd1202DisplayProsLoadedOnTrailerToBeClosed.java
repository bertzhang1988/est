package TestCase.LDDscreen;

import java.awt.AWTException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import Function.CommonFunction;
import Function.DataCommon;
import Function.Setup;
import Page.EqpStatusPageS;

public class US1204And1217ShipmentDetailsOnLDDAnd1202DisplayProsLoadedOnTrailerToBeClosed extends Setup {
	private EqpStatusPageS page;

	@BeforeClass
	public void SetUp() throws AWTException, InterruptedException {
		page = new EqpStatusPageS(driver);
		  
		driver.manage().window().maximize();
		page.SetStatus("ldd");
	}

	@Test(priority = 1, dataProvider = "lddscreen2", dataProviderClass = DataForUSLDDLifeTest.class)
	public void VerifyShipmentDetailsOnLDD(String terminalcd, String SCAC, String TrailerNB, String Desti,
			String AmountPro, String AmountWeight, String Cube, String Seal, String headloadDestination,
			String headloadCube, Date MReqpst, String CurrentStatus, String flag, String serv)
			throws AWTException, InterruptedException, ClassNotFoundException, SQLException {
		SoftAssert SA = new SoftAssert();
		page.SetLocation(terminalcd);
		Date CurrentTime = CommonFunction.gettime("UTC");
		page.EnterTrailer(SCAC, TrailerNB);

		SA.assertEquals(page.ShipmentCount2.getAttribute("value").replaceAll("_", ""), AmountPro,
				"ship count is wrong");
		SA.assertEquals(page.ShipmentWeight2.getAttribute("value").replaceAll("_", ""), AmountWeight,
				"ship weight is wrong");
		SA.assertEquals(page.DestinationField.getAttribute("value").replaceAll("_", ""), Desti, "destination is wrong");
		SA.assertEquals(page.CubeField.getAttribute("value"), Cube, "cube is wrong");
		SA.assertEquals(page.SealField.getAttribute("value").replaceAll("_", ""), Seal, "seal is wrong");
		SA.assertEquals(page.HeadloadDestination.getAttribute("value"), headloadDestination, "ship count is wrong");
		SA.assertEquals(page.HeadloadCube.getAttribute("value"), headloadCube, "ship weight is wrong");
		SA.assertEquals(page.ShipmentFlag.getText(), flag, " flag is wrong");
		SA.assertEquals(page.ServiceFlag.getText(), serv, "serv is wrong");

		// Check date and time prepopulate

		Date picker = page.GetDatePickerTime();
		Date expect;
		if (CurrentStatus.equalsIgnoreCase("LDD")) {
			expect = CommonFunction.getPrepopulateTimeNoStatusChange(terminalcd, MReqpst);
		} else {
			expect = CommonFunction.getPrepopulateTimeStatusChange(terminalcd, CurrentTime, MReqpst);
		}
		SA.assertEquals(picker, expect, "ldd screen prepopulate time is wrong ");

		// check pro grid
		LinkedHashSet<ArrayList<String>> ProInfo = page.GetProList(page.ProListLDDForm);
		SA.assertEquals(ProInfo, DataCommon.GetProListLD(SCAC, TrailerNB), "prolist information is wrong");
		SA.assertAll();
	}

}
