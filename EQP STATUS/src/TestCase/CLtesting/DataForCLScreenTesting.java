package TestCase.CLtesting;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.TimeZone;

import org.testng.annotations.DataProvider;

import Function.DataCommon;
import Function.DataConnection;

public class DataForCLScreenTesting {

	@DataProvider(name = "cl with pro")
	public static Iterator<Object[]> CreateData(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DataConnection.getConnection();
		Statement stat = conn.createStatement();

		String query1 = "select top 5 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC"
				+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB, EQP.Waybill_Transaction_vw wbt,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC"
				+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps"
				+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') "
				+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='cl' and neqps.[Equipment_Status_Type_CD] ='cl'"
				+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
				+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
				+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD and  wb.Pro_NB=wbt.Pro_NB and wbt.Waybill_Transaction_Index_NB=(SELECT MAX(wbt2.Waybill_Transaction_Index_NB) FROM EQP.Waybill_Transaction wbt2 where wb.Pro_NB=wbt2.Pro_NB )"
				+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC order by newid()";

		String query11 =

				"select top 5 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC"
						+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB, EQP.Waybill_Transaction_vw wbt,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC"
						+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps"
						+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') "
						+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='cl' and neqps.[Equipment_Status_Type_CD] not in ('cl')"
						+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
						+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
						+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD and  wb.Pro_NB=wbt.Pro_NB and wbt.Waybill_Transaction_Index_NB=(SELECT MAX(wbt2.Waybill_Transaction_Index_NB) FROM EQP.Waybill_Transaction wbt2 where wb.Pro_NB=wbt2.Pro_NB )"
						+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC order by newid()";

		String query = null;

		if (m.getName().contains("ThreeBlOBR")) {
			query = DataCommon.query43;
		} else if (m.getName().contains("clScreen")) {
			query = query1;
		} else if (m.getName().contains("ToCLNoPro")) {
			query = DataCommon.query42;
		}

		ArrayList<Object[]> b = new ArrayList<Object[]>();
		ResultSet rs = stat.executeQuery(query);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		while (rs.next()) {
			String terminalcd = rs.getString("Statusing_Facility_CD");
			String SCAC = rs.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs.getString("Equipment_Unit_NB");
			String Desti = rs.getString("Equipment_Dest_Facility_CD");
			if (Desti == null) {
				Desti = "";
			}
			String Cube = rs.getString("Actual_Capacity_Consumed_PC");
			if (Cube == null) {
				Cube = "";
			}
			String AmountPRO = rs.getString("AmountShip");
			if (AmountPRO.equalsIgnoreCase("0")) {
				AmountPRO = "";
			}
			String AmountWeight = rs.getString("AmountWeight");
			if (AmountWeight == null) {
				AmountWeight = "";
			}
			Timestamp Equipment_Status_TS = rs.getTimestamp("Equipment_Status_TS");
			ArrayList<Object> a = new ArrayList<Object>();
			a.add(terminalcd);
			a.add(SCAC);
			a.add(TrailerNB);
			a.add(Desti);
			a.add(Cube);
			a.add(AmountPRO);
			a.add(AmountWeight);
			a.add(Equipment_Status_TS);
			b.add(a.toArray(new Object[7]));
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn != null)
			conn.close();
		return b.iterator();
	}

	public static LinkedHashSet<ArrayList<String>> GetProList(String SCAC, String TrailerNB)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		Statement stat = conn2.createStatement();
		String query2 = " select wb.pro_nb, wb.Origin_Facility_CD,wb.Planned_Destination_Facility_CD,wb.Total_Actual_Weight_QT,wbt.Manifest_Destination_Fclty_CD,wbt.Headload_IN from eqp.waybill_vw wb,EQP.Waybill_Transaction_vw wbt"
				+ " where wb.Pro_NB=wbt.Pro_NB  and wbt.Waybill_Transaction_Index_NB=(SELECT MAX(wbt2.Waybill_Transaction_Index_NB) FROM EQP.Waybill_Transaction wbt2 where wb.Pro_NB=wbt2.Pro_NB )"
				+ " and wb.Standard_Carrier_Alpha_CD='" + SCAC + "' and wb.Equipment_Unit_NB='" + TrailerNB
				+ "' order by wbt.Headload_IN desc,wbt.Waybill_Transaction_End_TS,wb.pro_nb";

		String query6 = "select wb.pro_nb, wb.Origin_Facility_CD,wb.Planned_Destination_Facility_CD,wb.Total_Handling_Units_QT,wb.Total_Actual_Weight_QT,wb.Shipment_Due_DT,msrwbt.Manifest_Destination_Fclty_CD,msrwbt.Headload_IN "
				+ " from eqp.waybill_vw wb left join (select wbt.pro_nb,wbt.Manifest_Destination_Fclty_CD,wbt.Headload_IN,wbt.Waybill_Transaction_End_TS from EQP.Waybill_Transaction_vw wbt,(select wbt.pro_nb, MAX(wbt.Waybill_Transaction_Index_NB) mxidex from EQP.Waybill_Transaction_vw wbt group by wbt.pro_nb) wbt2 where wbt.Pro_NB=wbt2.Pro_NB and wbt.Waybill_Transaction_Index_NB=mxidex) msrwbt"
				+ " on  wb.Pro_NB=msrwbt.Pro_NB where wb.Standard_Carrier_Alpha_CD= '" + SCAC
				+ "'  and wb.Equipment_Unit_NB= '" + TrailerNB
				+ "' order by msrwbt.Headload_IN desc,msrwbt.Waybill_Transaction_End_TS,wb.pro_nb";

		String query10 = " select wb.pro_nb,ssst.[Shipment_Service_Sub_Type_NM], ss.[Shipment_Service_Type_NM] from [EQP].[Waybill_vw] wb left join [EQP].[Waybill_Service_vw] wbs on wb.[Pro_NB]=wbs.pro_nb"
				+ " left join [EQP].[Shipment_Service_vw] ss on wbs.[Service_CD]=ss.[Service_CD] left join [EQP].[Shipment_Service_Sub_Type_vw] ssst on ss.[Shipment_Service_Sub_Type_NM]=ssst.[Shipment_Service_Sub_Type_NM]"
				+ " where wb.pro_nb= ? and ss.[Shipment_Service_Type_NM]= ?  order by ssst.[Display_Sequence_NB]";

		// Set<ArrayList<String>> d=new HashSet<ArrayList<String>>(); // without
		// sort
		LinkedHashSet<ArrayList<String>> d = new LinkedHashSet<ArrayList<String>>(); // sort
		ResultSet rs = stat.executeQuery(query6);
		PreparedStatement stat2 = conn2.prepareStatement(query10);
		while (rs.next()) {
			String PRONB = rs.getString("pro_nb");
			String ORI = rs.getString("Origin_Facility_CD");
			String DEST = rs.getString("Planned_Destination_Facility_CD");
			String ACTUALW = rs.getString("Total_Actual_Weight_QT");
			String MANID = rs.getString("Manifest_Destination_Fclty_CD");
			// String HLI=rs.getString("Headload_IN");
			stat2.setString(1, PRONB);
			stat2.setString(2, "flag");
			ResultSet rs5 = stat2.executeQuery();
			ArrayList<String> f = new ArrayList<String>();
			while (rs5.next()) {
				String ServiceName = rs5.getString("Shipment_Service_Sub_Type_NM");
				f.add(ServiceName);
				f.removeAll(Collections.singleton(null));
			}
			String Flag;
			if (f.size() != 0) {
				Flag = f.toString().replaceAll("[\\[\\] ]", "");
			} else {
				Flag = null;
			}
			ArrayList<String> c = new ArrayList<String>();
			c.add(PRONB);
			c.add(Flag);
			c.add(ORI);
			c.add(DEST);
			c.add(ACTUALW);
			c.add(MANID);
			// c.add(HLI);
			c.removeAll(Collections.singleton(null));
			d.add(c);

		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();

		return d;
	}

	public static LinkedHashSet<ArrayList<String>> GetProList3ButtonLOBR(String SCAC, String TrailerNB)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		Statement stat = conn2.createStatement();
		String query2 = " select wb.pro_nb, wb.Origin_Facility_CD,wb.Planned_Destination_Facility_CD,wb.Total_Actual_Weight_QT,wbt.Manifest_Destination_Fclty_CD,wbt.Headload_IN from eqp.waybill_vw wb,EQP.Waybill_Transaction_vw wbt"
				+ " where wb.Pro_NB=wbt.Pro_NB  and wbt.Waybill_Transaction_Index_NB=(SELECT MAX(wbt2.Waybill_Transaction_Index_NB) FROM EQP.Waybill_Transaction wbt2 where wb.Pro_NB=wbt2.Pro_NB )"
				+ " and wb.Standard_Carrier_Alpha_CD='" + SCAC + "' and wb.Equipment_Unit_NB='" + TrailerNB
				+ "' order by wbt.Headload_IN desc,wbt.Waybill_Transaction_End_TS,wb.pro_nb";

		String query6 = "select wb.pro_nb, wb.Origin_Facility_CD,wb.Planned_Destination_Facility_CD,wb.Total_Handling_Units_QT,wb.Total_Actual_Weight_QT,wb.Shipment_Due_DT,msrwbt.Manifest_Destination_Fclty_CD,msrwbt.Headload_IN "
				+ " from eqp.waybill_vw wb left join (select wbt.pro_nb,wbt.Manifest_Destination_Fclty_CD,wbt.Headload_IN,wbt.Waybill_Transaction_End_TS from EQP.Waybill_Transaction_vw wbt,(select wbt.pro_nb, MAX(wbt.Waybill_Transaction_Index_NB) mxidex from EQP.Waybill_Transaction_vw wbt group by wbt.pro_nb) wbt2 where wbt.Pro_NB=wbt2.Pro_NB and wbt.Waybill_Transaction_Index_NB=mxidex) msrwbt"
				+ " on  wb.Pro_NB=msrwbt.Pro_NB where wb.Standard_Carrier_Alpha_CD= '" + SCAC
				+ "'  and wb.Equipment_Unit_NB= '" + TrailerNB
				+ "' order by msrwbt.Headload_IN desc,msrwbt.Waybill_Transaction_End_TS,wb.pro_nb";

		String query10 = " select wb.pro_nb,ssst.[Shipment_Service_Sub_Type_NM], ss.[Shipment_Service_Type_NM] from [EQP].[Waybill_vw] wb left join [EQP].[Waybill_Service_vw] wbs on wb.[Pro_NB]=wbs.pro_nb"
				+ " left join [EQP].[Shipment_Service_vw] ss on wbs.[Service_CD]=ss.[Service_CD] left join [EQP].[Shipment_Service_Sub_Type_vw] ssst on ss.[Shipment_Service_Sub_Type_NM]=ssst.[Shipment_Service_Sub_Type_NM]"
				+ " where wb.pro_nb= ? and ss.[Shipment_Service_Type_NM]= ?  order by ssst.[Display_Sequence_NB]";

		// Set<ArrayList<String>> d=new HashSet<ArrayList<String>>(); // without
		// sort
		LinkedHashSet<ArrayList<String>> d = new LinkedHashSet<ArrayList<String>>(); // sort
		ResultSet rs = stat.executeQuery(query6);
		PreparedStatement stat2 = conn2.prepareStatement(query10);
		while (rs.next()) {
			String PRONB = rs.getString("pro_nb");
			String ORI = rs.getString("Origin_Facility_CD");
			String DEST = rs.getString("Planned_Destination_Facility_CD");
			String ACTUALW = rs.getString("Total_Actual_Weight_QT");
			// String MANID=rs.getString("Manifest_Destination_Fclty_CD");
			// String HLI=rs.getString("Headload_IN");
			stat2.setString(1, PRONB);
			stat2.setString(2, "flag");
			ResultSet rs5 = stat2.executeQuery();
			ArrayList<String> f = new ArrayList<String>();
			while (rs5.next()) {
				String ServiceName = rs5.getString("Shipment_Service_Sub_Type_NM");
				f.add(ServiceName);
				f.removeAll(Collections.singleton(null));
			}
			String Flag;
			if (f.size() != 0) {
				Flag = f.toString().replaceAll("[\\[\\] ]", "");
			} else {
				Flag = null;
			}
			ArrayList<String> c = new ArrayList<String>();
			c.add(PRONB);
			c.add(Flag);
			c.add(ORI);
			c.add(DEST);
			c.add(ACTUALW);
			// c.add(MANID);
			// c.add(HLI);
			c.removeAll(Collections.singleton(null));
			d.add(c);

		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();

		return d;
	}

}
