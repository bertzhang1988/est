package TestCase.ReusableFunctionTest;

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

public class DataForReusableFunction {
	@DataProvider(name = "2000.51")
	public static Iterator<Object[]> CreateData1(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn1 = DataConnection.getConnection();
		Statement stat = null;
		stat = conn1.createStatement();

		String status = US200051PreventStatusTimeChangesOutsidePolicy.SetToStatus;
		String query1 = "select top 10 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
				+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT"
				+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT"
				+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
				+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
				+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='"
				+ status + "' "
				+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
				+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
				+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
				+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
				+ " having COUNT(wb.pro_nb)=0 order by newid()";

		ArrayList<Object[]> b = new ArrayList<Object[]>();
		ResultSet rs = stat.executeQuery(query1);
		while (rs.next()) {
			String terminalcd = rs.getString("Statusing_Facility_CD");
			String SCAC = rs.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs.getString("Equipment_Unit_NB");
			String CurrentStatus = rs.getString("Equipment_Status_Type_CD");
			Timestamp Equipment_Status_TS = rs.getTimestamp("Equipment_Status_TS");
			ArrayList<Object> a = new ArrayList<Object>();
			a.add(terminalcd);
			a.add(SCAC);
			a.add(TrailerNB);
			a.add(CurrentStatus);
			a.add(Equipment_Status_TS);
			b.add(a.toArray(new Object[5]));
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn1 != null)
			conn1.close();
		return b.iterator();
	}

	@DataProvider(name = "2000.58")
	public static Iterator<String[]> CreateData2(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn1 = DataConnection.getConnection();
		Statement stat = null;
		stat = conn1.createStatement();
		String status = US200058ValidateTrailerAtStatusingLocation.SetToStatus;
		String query1 = "select top 10 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
				+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT"
				+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT"
				+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
				+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
				+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='"
				+ status + "' "
				+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
				+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
				+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
				+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
				+ " having COUNT(wb.pro_nb)=0 order by newid()";
		ArrayList<String[]> b = new ArrayList<String[]>();
		ResultSet rs = stat.executeQuery(query1);
		while (rs.next()) {
			String terminalcd = rs.getString("Statusing_Facility_CD");
			String SCAC = rs.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs.getString("Equipment_Unit_NB");
			ArrayList<String> a = new ArrayList<String>();
			a.add(terminalcd);
			a.add(SCAC);
			a.add(TrailerNB);
			b.add(a.toArray(new String[3]));
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn1 != null)
			conn1.close();
		return b.iterator();

	}

	@DataProvider(name = "2000.57")
	public static Iterator<String[]> CreateData3(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn1 = DataConnection.getConnection();
		Statement stat = null;
		stat = conn1.createStatement();

		String status = US200057EvaluateStatusTransitions.SetToStatus;
		String query1 = "select top 10 eqp.[Standard_Carrier_Alpha_CD],eqp.[Equipment_Unit_NB],Neqps.[Equipment_Status_Type_CD],Neqps.[Statusing_Facility_CD]"
				+ " from [EQP].[Equipment_vw] eqp, [EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC"
				+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
				+ " where eqp.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and eqp.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' AND eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')"
				+ " and [Equipment_Avbl_Status_NM]='available'  and  Neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and Neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
				+ " and Neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[From_Equipment_Status_Type_CD] not in( select From_Equipment_Status_Type_CD from EQP.Equipment_Status_Type_Transition_vw where To_Equipment_Status_Type_CD='"
				+ status + "')"
				+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1 where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB) "
				+ " and Neqps.statusing_Facility_CD is not null GROUP BY eqp.[Standard_Carrier_Alpha_CD],eqp.[Equipment_Unit_NB],Neqps.[Equipment_Status_Type_CD],Neqps.[Statusing_Facility_CD] order by NEWID()";

		ArrayList<String[]> b = new ArrayList<String[]>();
		ResultSet rs = stat.executeQuery(query1);
		while (rs.next()) {
			String terminalcd = rs.getString("Statusing_Facility_CD");
			String SCAC = rs.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs.getString("Equipment_Unit_NB");
			String CurrentStatus = rs.getString("Equipment_Status_Type_CD");
			ArrayList<String> a = new ArrayList<String>();
			a.add(terminalcd);
			a.add(SCAC);
			a.add(TrailerNB);
			a.add(CurrentStatus);
			b.add(a.toArray(new String[4]));
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn1 != null)
			conn1.close();
		return b.iterator();
	}

	@DataProvider(name = "2000.68")
	public static Iterator<String[]> CreateData4(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DataConnection.getConnection();
		Statement stat = conn.createStatement();
		ArrayList<String[]> b = new ArrayList<String[]>();
		String query = null;
		if (m.getName().equalsIgnoreCase("VerifyValidDestination")) {
			query = DataCommon.query29;
		} else if (m.getName().equalsIgnoreCase("VerifyInvalidDestination")) {
			query = DataCommon.query30;
		}
		ResultSet rs = stat.executeQuery(query);
		while (rs.next()) {
			String terminalcd = rs.getString("Facility_CD");
			ArrayList<String> a = new ArrayList<String>();
			a.add(terminalcd);
			b.add(a.toArray(new String[1]));
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn != null)
			conn.close();
		return b.iterator();
	}

	@DataProvider(name = "2000.682")
	public static Iterator<Object[]> CreateData12(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn1 = DataConnection.getConnection();
		Statement stat = conn1.createStatement();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		String Query = DataCommon.query1;
		ArrayList<Object[]> b1 = new ArrayList<Object[]>();
		ResultSet rs1 = stat.executeQuery(Query);
		while (rs1.next()) {
			String terminalcd = rs1.getString("Statusing_Facility_CD");
			String SCAC = rs1.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs1.getString("Equipment_Unit_NB");
			ArrayList<Object> a1 = new ArrayList<Object>();
			a1.add(terminalcd);
			a1.add(SCAC);
			a1.add(TrailerNB);
			b1.add(a1.toArray(new Object[3]));
		}
		if (rs1 != null)
			rs1.close();
		if (stat != null)
			stat.close();
		if (conn1 != null)
			conn1.close();

		return b1.iterator();

	}

	@DataProvider(name = "2000.41")
	public static Iterator<String[]> CreateData5(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn1 = DataConnection.getConnection();
		Statement stat = null;
		stat = conn1.createStatement();

		ArrayList<String[]> b1 = new ArrayList<String[]>();
		String query = null;
		if (m.getName().equalsIgnoreCase("VerifyValidTerminal")) {
			query = DataCommon.query27;
		} else if (m.getName().equalsIgnoreCase("VerifyInvalidTerminal")) {
			query = DataCommon.query28;
		}
		ResultSet rs1 = stat.executeQuery(query);
		while (rs1.next()) {
			String terminalcd = rs1.getString("Facility_CD");
			ArrayList<String> a1 = new ArrayList<String>();
			a1.add(terminalcd);
			b1.add(a1.toArray(new String[1]));
		}
		if (rs1 != null)
			rs1.close();
		if (stat != null)
			stat.close();
		if (conn1 != null)
			conn1.close();
		return b1.iterator();
	}

	@DataProvider(name = "2000.02")
	public static Iterator<String[]> CreateData6(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		Statement stat = null;
		stat = conn2.createStatement();
		String query2 = DataCommon.query31;
		ArrayList<String[]> b2 = new ArrayList<String[]>();
		ResultSet rs2 = stat.executeQuery(query2);
		while (rs2.next()) {
			String terminalcd = rs2.getString("Statusing_Facility_CD");
			String SCAC = rs2.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs2.getString("equipment_unit_nb");
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

	@DataProvider(name = "2000.01retired")
	public static Iterator<String[]> CreateData7(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn3 = DataConnection.getConnection();
		Statement stat = null;
		stat = conn3.createStatement();
		String query3 = DataCommon.query32;

		ArrayList<String[]> b3 = new ArrayList<String[]>();
		ResultSet rs3 = stat.executeQuery(query3);
		while (rs3.next()) {
			String TrailerNB = rs3.getString("equipment_unit_nb");
			ArrayList<String> a3 = new ArrayList<String>();
			a3.add(TrailerNB);
			b3.add(a3.toArray(new String[1]));
		}
		if (rs3 != null)
			rs3.close();
		if (stat != null)
			stat.close();
		if (conn3 != null)
			conn3.close();
		return b3.iterator();
	}

	@DataProvider(name = "2000.01NotDb")
	public static Object[][] createData8() {
		return new Object[][] { { "19880802" }, { "333333333" }, { "000000000" }, { "878787" }, { "4444444" },
				{ "888888888" } };

	}

}
