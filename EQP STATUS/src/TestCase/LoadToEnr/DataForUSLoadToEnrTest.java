package TestCase.LoadToEnr;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimeZone;

import org.testng.annotations.DataProvider;

import Function.DataCommon;
import Function.DataConnection;

public class DataForUSLoadToEnrTest {

	@DataProvider(name = "loadToEnrscreen")
	public static Iterator<Object[]> CreateData1(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn1 = DataConnection.getConnection();
		Statement stat = conn1.createStatement();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		String Query = null;
		if (m.getName().equalsIgnoreCase("ValidTrailerToEnrWithoutProAddPro")) {
			Query = DataCommon.query10;
		} else if (m.getName().equalsIgnoreCase("ProDigitCheck")) {
			Query = DataCommon.query10;
		} else if (m.getName().equalsIgnoreCase("CheckShipmentAlreadyLoadedOnTrailer")) {
			Query = DataCommon.query11;
		} else if (m.getName().equalsIgnoreCase("VerifyMasterRevenueVlidation")) {
			Query = DataCommon.query10;
		} else if (m.getName().equalsIgnoreCase("AddMultipleProsInSingleBatch")) {
			Query = DataCommon.query10;
		}

		ArrayList<Object[]> b1 = new ArrayList<Object[]>();
		ResultSet rs1 = stat.executeQuery(Query);
		while (rs1.next()) {
			String statustype = rs1.getString("Equipment_Status_Type_CD");
			String terminalcd = rs1.getString("Statusing_Facility_CD");
			String SCAC = rs1.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs1.getString("Equipment_Unit_NB");

			Timestamp Equipment_Status_TS = rs1.getTimestamp("Equipment_Status_TS");
			String Desti = rs1.getString("Equipment_Dest_Facility_CD");
			String Cube = rs1.getString("Actual_Capacity_Consumed_PC");
			String StatusType = rs1.getString("equipment_status_type_cd");
			ArrayList<Object> a1 = new ArrayList<Object>();
			a1.add(terminalcd);
			a1.add(SCAC);
			a1.add(TrailerNB);
			a1.add(Desti);
			a1.add(Cube);
			a1.add(StatusType);
			a1.add(Equipment_Status_TS);
			b1.add(a1.toArray(new Object[7]));
		}
		if (rs1 != null)
			rs1.close();
		if (stat != null)
			stat.close();
		if (conn1 != null)
			conn1.close();
		return b1.iterator();
	}

	@DataProvider(name = "loadToEnrscreen2")
	public static Iterator<Object[]> CreateData(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn1 = DataConnection.getConnection();
		Statement stat = null;
		stat = conn1.createStatement();
		String stuff1 = null;
		String stuff2 = null;

		if (m.getName().equalsIgnoreCase("VerifyBatchProForFoodTrailer")
				|| m.getName().equalsIgnoreCase("VerifyBatchProForFoodTrailer")) {
			stuff1 = "food";
			stuff2 = "pois";
		} else if (m.getName().equalsIgnoreCase("VerifyBatchProForPoisonTrailer")
				|| m.getName().equalsIgnoreCase("VerifyBatchProForPoisonTrailer")) {
			stuff1 = "pois";
			stuff2 = "food";
		}

		String query2 = "select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Dispatch_Dest_Facility_CD"
				+ "  from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,EQP.Waybill_Service_VW WBS,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,Dispatch_Dest_Facility_CD"
				+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.Dispatch_Dest_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps"
				+ "  where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
				+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD] in ('arr', 'enr')"
				+ "  and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
				+ "  and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
				+ " and neqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and neqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD and wb.PRO_NB=WBS.PRO_NB AND  WBS.SERVICE_CD ='"
				+ stuff1 + "'"
				+ " and not exists(select 1 from (select wb.Standard_Carrier_Alpha_CD,wb.Equipment_Unit_NB from EQP.Waybill wb,EQP.Waybill_Service WBS where wb.PRO_NB=WBS.PRO_NB AND WBS.SERVICE_CD in('"
				+ stuff2 + "')) pois "
				+ " where neqps.Standard_Carrier_Alpha_CD = pois.Standard_Carrier_Alpha_CD  and neqps.Equipment_Unit_NB = pois.Equipment_Unit_NB)"
				+ "  group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Dispatch_Dest_Facility_CD order by newid()";

		ArrayList<Object[]> b1 = new ArrayList<Object[]>();
		ResultSet rs1 = stat.executeQuery(query2);
		while (rs1.next()) {
			String statustype = rs1.getString("Equipment_Status_Type_CD");
			String terminalcd = rs1.getString("Statusing_Facility_CD");
			String SCAC = rs1.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs1.getString("Equipment_Unit_NB");

			Timestamp Equipment_Status_TS = rs1.getTimestamp("Equipment_Status_TS");
			String Desti = rs1.getString("Equipment_Dest_Facility_CD");
			String Cube = rs1.getString("Actual_Capacity_Consumed_PC");
			String StatusType = rs1.getString("equipment_status_type_cd");
			ArrayList<Object> a1 = new ArrayList<Object>();
			a1.add(terminalcd);
			a1.add(SCAC);
			a1.add(TrailerNB);
			a1.add(Desti);
			a1.add(Cube);
			a1.add(StatusType);
			a1.add(Equipment_Status_TS);
			b1.add(a1.toArray(new Object[7]));
		}
		if (rs1 != null)
			rs1.close();
		if (stat != null)
			stat.close();
		if (conn1 != null)
			conn1.close();
		return b1.iterator();
	}

}
