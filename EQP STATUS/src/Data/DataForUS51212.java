package Data;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.testng.annotations.DataProvider;

import Function.DataCommon;
import Function.DataConnection;

public class DataForUS51212 {
	@DataProvider(name = "512.12DT")
	public static Iterator<String[]> CreateData(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn1 = DataConnection.getConnection();
		Statement stat = conn1.createStatement();
		ArrayList<String[]> b = new ArrayList<String[]>();
		String query = DataCommon.query52;
		ResultSet rs = stat.executeQuery(query);
		while (rs.next()) {
			String terminalcd = rs.getString("Statusing_Facility_CD");
			String SCAC = rs.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs.getString("Equipment_Unit_NB");
			String Destination = rs.getString("Equipment_Dest_Facility_CD");
			ArrayList<String> a = new ArrayList<String>();
			a.add(terminalcd);
			a.add(SCAC);
			a.add(TrailerNB);
			a.add(Destination);
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

	@DataProvider(name = "512.12ST")
	public static Iterator<String[]> CreateData2(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn1 = DataConnection.getConnection();

		String query = " select TOP 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.M204_Equipment_Status_Type_CD"
				+ "  from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.M204_Equipment_Status_Type_CD"
				+ "  from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,m204_equipment_status_type_cd"
				+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.m204_equipment_status_type_cd,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps"
				+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')   "
				+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='cl'"
				+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
				+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
				+ " and exists (select 1 from ( "
				+ " select  ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.M204_Equipment_Status_Type_CD"
				+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.M204_Equipment_Status_Type_CD"
				+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,m204_equipment_status_type_cd"
				+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.m204_equipment_status_type_cd,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps"
				+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')   "
				+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and neqps.[Equipment_Status_Type_CD] in (?) "
				+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
				+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
				+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
				+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.M204_Equipment_Status_Type_CD"
				+ " having COUNT(wb.pro_nb)>0  ) arv where arv.Statusing_Facility_CD=neqps.Statusing_Facility_CD )) esi"
				+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
				+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.M204_Equipment_Status_Type_CD"
				+ " having COUNT(wb.pro_nb)=0 order by newid()";
		PreparedStatement stat = conn1.prepareStatement(query);
		if (m.getName().contains("ARV"))
			stat.setString(1, "ARV");
		else if (m.getName().contains("CPU"))
			stat.setString(1, "CPU");
		ResultSet rs = stat.executeQuery();
		if (!rs.isBeforeFirst())
			System.out.println("there is no data for " + m.getName() + "\n");
		ArrayList<String[]> b = new ArrayList<String[]>();
		while (rs.next()) {
			String terminalcd = rs.getString("Statusing_Facility_CD");
			String SCAC = rs.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs.getString("Equipment_Unit_NB");
			String Destination = rs.getString("Equipment_Dest_Facility_CD");
			ArrayList<String> a = new ArrayList<String>();
			a.add(terminalcd);
			a.add(SCAC);
			a.add(TrailerNB);
			a.add(Destination);
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

	public static ArrayList<String> GetTrailerOnSameTerminal(String terminalcd, String statustype)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		Statement stat = conn2.createStatement();
		String query2 = " select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,nEqps.Equipment_Dest_Facility_CD"
				+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB"
				+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps"
				+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM IN('TRAILER','STRAIGHT TRUCK')"
				+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]='"
				+ statustype + "' and neqps.Statusing_Facility_CD='" + terminalcd + "'"
				+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
				+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
				+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD group by neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,nEqps.Equipment_Dest_Facility_CD order by newid()";
		ArrayList<String> PickedTrailer = new ArrayList<String>();
		ResultSet rs = stat.executeQuery(query2);
		if (!rs.isBeforeFirst())
			System.out.println(
					"their is no data for " + Thread.currentThread().getStackTrace()[2].getMethodName() + "\n");

		while (rs.next()) {
			String SCAC = rs.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs.getString("Equipment_Unit_NB");
			PickedTrailer.add(SCAC);
			PickedTrailer.add(TrailerNB);
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();

		return PickedTrailer;

	}

	public static ArrayList<String> GetTrailerOnDifferentTerminal(String terminalcd, String statustype)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		Statement stat = conn2.createStatement();
		String query2 = " select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,nEqps.Equipment_Dest_Facility_CD"
				+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB"
				+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps"
				+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM IN('TRAILER','STRAIGHT TRUCK')"
				+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]='"
				+ statustype + "' and neqps.Statusing_Facility_CD <>'" + terminalcd + "'"
				+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
				+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
				+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD group by neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,nEqps.Equipment_Dest_Facility_CD order by newid()";
		ArrayList<String> PickedTrailer = new ArrayList<String>();
		ResultSet rs = stat.executeQuery(query2);
		if (!rs.isBeforeFirst())
			System.out.println(
					"their is no data for " + Thread.currentThread().getStackTrace()[2].getMethodName() + "\n");
		while (rs.next()) {
			String SCAC = rs.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs.getString("Equipment_Unit_NB");
			PickedTrailer.add(SCAC);
			PickedTrailer.add(TrailerNB);
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();

		return PickedTrailer;

	}
}
