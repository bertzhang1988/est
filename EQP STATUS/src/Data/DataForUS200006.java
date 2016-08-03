package Data;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.testng.annotations.DataProvider;

import Function.DataCommon;
import Function.DataConnection;

public class DataForUS200006 {

	@DataProvider(name = "ldgtrailerNoPro")
	public static Iterator<String[]> CreateData1(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn1 = DataConnection.getConnection();
		Statement stat = null;
		stat = conn1.createStatement();
		String query1 = DataCommon.query1;
		ArrayList<String[]> b1 = new ArrayList<String[]>();
		ResultSet rs1 = stat.executeQuery(query1);
		while (rs1.next()) {
			String terminalcd = rs1.getString("Statusing_Facility_CD");
			String SCAC = rs1.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs1.getString("Equipment_Unit_NB");
			ArrayList<String> a1 = new ArrayList<String>();
			a1.add(terminalcd);
			a1.add(SCAC);
			a1.add(TrailerNB);
			b1.add(a1.toArray(new String[3]));
		}
		if (rs1 != null)
			rs1.close();
		if (stat != null)
			stat.close();
		if (conn1 != null)
			conn1.close();
		return b1.iterator();
	}

	@DataProvider(name = "2000.06")
	public static Iterator<String[]> CreateData(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		Statement stat = null;
		stat = conn2.createStatement();
		String stuff1 = null;
		String stuff2 = null;

		if (m.getName().equalsIgnoreCase("VerifySinglePoisProForFoodTrailer")
				|| m.getName().equalsIgnoreCase("VerifyBatchProForFoodTrailer")) {
			stuff1 = "food";
			stuff2 = "pois";
		} else if (m.getName().equalsIgnoreCase("VerifySingleProForPoisonTrailer")
				|| m.getName().equalsIgnoreCase("VerifyBatchProForPoisonTrailer")) {
			stuff1 = "pois";
			stuff2 = "food";
		}

		String query2 = "select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],WBS.SERVICE_CD"
				+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst, EQP.Waybill_vw wb, EQP.Waybill_Service_VW WBS,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB"
				+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps"
				+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')"
				+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldg' and neqps.[Equipment_Status_Type_CD] ='ldg'"
				+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and neqps.Equipment_Dest_Facility_CD is not null"
				+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
				+ " and neqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and neqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD and wb.PRO_NB=WBS.PRO_NB AND  WBS.SERVICE_CD ='"
				+ stuff1 + "'"
				+ " and not exists(select 1 from (select wb.Standard_Carrier_Alpha_CD,wb.Equipment_Unit_NB from EQP.Waybill wb,EQP.Waybill_Service WBS where wb.PRO_NB=WBS.PRO_NB AND WBS.SERVICE_CD in ('"
				+ stuff2 + "')) pois "
				+ " where neqps.Standard_Carrier_Alpha_CD = pois.Standard_Carrier_Alpha_CD  and neqps.Equipment_Unit_NB = pois.Equipment_Unit_NB)"
				+ " group by neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],WBS.SERVICE_CD order by newid()";

		ArrayList<String[]> b2 = new ArrayList<String[]>();
		ResultSet rs2 = stat.executeQuery(query2);
		while (rs2.next()) {
			String terminalcd = rs2.getString("Statusing_Facility_CD");
			String SCAC = rs2.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs2.getString("Equipment_Unit_NB");
			ArrayList<String> a2 = new ArrayList<String>();
			a2.add(terminalcd);
			a2.add(SCAC);
			a2.add(TrailerNB);
			b2.add(a2.toArray(new String[3]));
		}
		if (rs2 != null)
			rs2.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();
		return b2.iterator();
	}

	@DataProvider(name = "ldg trailer with both food and pois")
	public static Iterator<String[]> trailerWithFoodANDPois(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		Statement stat = null;
		stat = conn2.createStatement();
		String query2 = "select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],WBS.SERVICE_CD"
				+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst, EQP.Waybill_vw wb, EQP.Waybill_Service_VW WBS,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB"
				+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps"
				+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')"
				+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldg' and neqps.[Equipment_Status_Type_CD] ='ldg'"
				+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and neqps.Equipment_Dest_Facility_CD is not null"
				+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
				+ " and neqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and neqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD and wb.PRO_NB=WBS.PRO_NB AND  WBS.SERVICE_CD ='food'"
				+ " and exists(select 1 from (select wb.Standard_Carrier_Alpha_CD,wb.Equipment_Unit_NB from EQP.Waybill wb,EQP.Waybill_Service WBS where wb.PRO_NB=WBS.PRO_NB AND WBS.SERVICE_CD in ('pois')) pois "
				+ " where neqps.Standard_Carrier_Alpha_CD = pois.Standard_Carrier_Alpha_CD  and neqps.Equipment_Unit_NB = pois.Equipment_Unit_NB)"
				+ " group by neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],WBS.SERVICE_CD order by newid()";

		ArrayList<String[]> b2 = new ArrayList<String[]>();
		ResultSet rs2 = stat.executeQuery(query2);
		while (rs2.next()) {
			String terminalcd = rs2.getString("Statusing_Facility_CD");
			String SCAC = rs2.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs2.getString("Equipment_Unit_NB");
			ArrayList<String> a2 = new ArrayList<String>();
			a2.add(terminalcd);
			a2.add(SCAC);
			a2.add(TrailerNB);
			b2.add(a2.toArray(new String[3]));
		}
		if (rs2 != null)
			rs2.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();
		return b2.iterator();
	}

}
