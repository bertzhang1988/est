package Data;

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

import Function.DataConnection;

public class DataForUS1229 {
	@DataProvider(name = "12.29")
	public static Iterator<String[]> CreateData1(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn1 = DataConnection.getConnection();
		Statement stat = conn1.createStatement();

		String Status = null;
		String Query = null;
		if (m.getName().contains("MTY")) {
			Status = "MTY";
		} else if (m.getName().contains("OFD")) {
			Status = "OFD";
		} else if (m.getName().contains("SPT")) {
			Status = "SPT";
		}
		String query1 = " select top 2 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight"
				+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB"
				+ " from  [EQP].[Equipment] eqp,[EQP].[Equipment_Availability] eqpa,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB"
				+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status] eqps) as eqq where eqq.num1=1) Neqps"
				+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM='trailer'"
				+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]='"
				+ Status + "'"
				+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB]"
				+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
				+ " LEFT JOIN EQP.Waybill WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
				+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB"
				+ " having COUNT(wb.pro_nb)=0 ORDER BY NEWID()";

		String query2 = "select TOP 2 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,nEqps.Equipment_Dest_Facility_CD"
				+ " from  [EQP].[Equipment] eqp,[EQP].[Equipment_Availability] eqpa,EQP.Waybill WB, EQP.Waybill_Transaction wbt,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB"
				+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status] eqps) as eqq where eqq.num1=1) Neqps	"
				+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM='trailer'"
				+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]='"
				+ Status + "'"
				+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB]"
				+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
				+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD and  wb.Pro_NB=wbt.Pro_NB and wbt.Waybill_Transaction_Index_NB=(SELECT MAX(wbt2.Waybill_Transaction_Index_NB) FROM EQP.Waybill_Transaction wbt2 where wb.Pro_NB=wbt2.Pro_NB )  ORDER BY NEWID()";

		if (m.getName().contains("NoPRO")) {
			Query = query1;
		} else if (m.getName().contains("HasPRO")) {
			Query = query2;
		}

		ArrayList<String[]> b1 = new ArrayList<String[]>();
		ResultSet rs1 = stat.executeQuery(Query);
		while (rs1.next()) {
			String terminalcd = rs1.getString("Statusing_Facility_CD");
			String SCAC = rs1.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs1.getString("Equipment_Unit_NB");
			String desti = rs1.getString("Equipment_Dest_Facility_CD");
			ArrayList<String> a1 = new ArrayList<String>();
			a1.add(terminalcd);
			a1.add(SCAC);
			a1.add(TrailerNB);
			a1.add(desti);
			b1.add(a1.toArray(new String[4]));
		}
		if (rs1 != null)
			rs1.close();
		if (stat != null)
			stat.close();
		if (conn1 != null)
			conn1.close();
		return b1.iterator();
	}

	public static ArrayList<String> GetProNotInAnyTrailer() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		Statement stat = conn2.createStatement();
		String query2 = " SELECT wb.Pro_NB FROM EQP.Waybill as wb WHERE wb.Standard_Carrier_Alpha_CD Is Null AND wb.Equipment_Unit_NB Is Null AND wb.Shipment_Correction_Type_CD<>'VO' AND wb.Shipment_Purpose_CD<>'MR'"
				+ " And wb.Shipment_Purpose_CD<>'SU' AND wb.Delivery_DT Is Null and wb.Create_TS >'2015-09-30' and wb.Delivery_TS is NULL order by wb.Pro_NB";
		ArrayList<String> c = new ArrayList<String>();
		ResultSet rs = stat.executeQuery(query2);
		while (rs.next()) {
			String pro = rs.getString("PRO_NB");
			c.add(pro);
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();

		return c;
	}

	static public ArrayList<Object> CheckEQPStatusUpdate(String SCAC, String TrailerNB)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn3 = DataConnection.getConnection();
		Statement stat = conn3.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		String query3 = "select top 1 [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],Equipment_Status_Type_CD,Statusing_Facility_CD,[Equipment_Dest_Facility_CD],Source_Create_ID,Actual_Capacity_Consumed_PC,Modify_TS,create_ts,Equipment_Status_TS,Observed_Shipment_QT,Observed_Weight_QT,Seal_NB,Equipment_Status_System_TS from [EQP].[Equipment_Status] where Equipment_Unit_NB='"
				+ TrailerNB + "' and Standard_Carrier_Alpha_CD='" + SCAC + "'"
				+ " ORDER BY [Equipment_Status_TS] DESC,[Equipment_Status_System_TS] DESC";
		ResultSet rs = stat.executeQuery(query3);
		ArrayList<Object> status = new ArrayList<Object>();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		rs.absolute(1);
		String Equipment_Status_Type_CD = rs.getString("Equipment_Status_Type_CD");
		String Statusing_Facility_CD = rs.getString("Statusing_Facility_CD");
		String equipment_dest_facility_cd = rs.getString("equipment_dest_facility_cd");
		String Source_Create_ID = rs.getString("Source_Create_ID");
		String Actual_Capacity_Consumed_PC = rs.getString("Actual_Capacity_Consumed_PC");
		String Observed_Shipment_QT = rs.getString("Observed_Shipment_QT");
		String Observed_Weight_QT = rs.getString("Observed_Weight_QT");
		String Seal_NB = rs.getString("Seal_NB");
		Timestamp Modify_TS = rs.getTimestamp("Modify_TS");
		Timestamp create_ts = rs.getTimestamp("create_ts");
		Timestamp Equipment_Status_TS = rs.getTimestamp("Equipment_Status_TS");
		Timestamp Equipment_Status_System_TS = rs.getTimestamp("Equipment_Status_System_TS");
		status.add(Equipment_Status_Type_CD);// 0
		status.add(Statusing_Facility_CD);// 1
		status.add(equipment_dest_facility_cd);// 2
		status.add(Source_Create_ID);// 3
		status.add(Actual_Capacity_Consumed_PC);// 4
		status.add(Modify_TS);// 5
		status.add(create_ts);// 6
		status.add(Equipment_Status_TS);// 7
		status.add(Equipment_Status_System_TS);// 8
		status.add(Observed_Shipment_QT);// 9
		status.add(Observed_Weight_QT);// 10
		status.add(Seal_NB);// 11
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn3 != null)
			conn3.close();

		return status;
	}

	public static ArrayList<Object> GetWBandWBTupdate(String PRO) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn4 = DataConnection.getConnection();
		Statement stat = conn4.createStatement();
		String query4 = " select wb.pro_nb,WB.Standard_Carrier_Alpha_CD,WB.Equipment_Unit_NB,wb.Destination_Facility_CD, wb.Modify_TS,wbt.To_Standard_Carrier_Alpha_CD,wbt.To_Equipment_Unit_NB,wbT.Waybill_Transaction_Type_NM,wbt.Manifest_Destination_Fclty_CD,wbt.Waybill_Transaction_End_TS,wbt.Create_ts, wbt.Modify_ts"
				+ " from eqp.waybill wb,EQP.Waybill_Transaction wbt"
				+ " where wb.Pro_NB=wbt.Pro_NB and wbt.Waybill_Transaction_Index_NB=(SELECT MAX(wbt2.Waybill_Transaction_Index_NB) FROM EQP.Waybill_Transaction wbt2 where wb.Pro_NB=wbt2.Pro_NB ) AND WB.PRO_nb='"
				+ PRO + "'";
		ArrayList<Object> wbt = new ArrayList<Object>();
		ResultSet rs = stat.executeQuery(query4);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		rs.absolute(1);
		String Standard_Carrier_Alpha_CD = rs.getString("Standard_Carrier_Alpha_CD");
		String Equipment_Unit_NB = rs.getString("Equipment_Unit_NB");
		String Destination_Facility_CD = rs.getString("Destination_Facility_CD");
		Timestamp wb_Modify_TS = rs.getTimestamp("Modify_TS");
		String To_Standard_Carrier_Alpha_CD = rs.getString("To_Standard_Carrier_Alpha_CD");
		String To_Equipment_Unit_NB = rs.getString("To_Equipment_Unit_NB");
		String Waybill_Transaction_Type_NM = rs.getString("Waybill_Transaction_Type_NM");
		String Manifest_Destination_Fclty_CD = rs.getString("wbt.Manifest_Destination_Fclty_CD");
		Timestamp Waybill_Transaction_End_TS = rs.getTimestamp("Waybill_Transaction_End_TS");
		Timestamp Create_ts = rs.getTimestamp("Create_ts");
		Timestamp wbt_Modify_ts = rs.getTimestamp("Modify_ts");
		wbt.add(Standard_Carrier_Alpha_CD);// 0
		wbt.add(Equipment_Unit_NB);// 1
		wbt.add(Destination_Facility_CD);// 2
		wbt.add(To_Standard_Carrier_Alpha_CD);// 3
		wbt.add(To_Equipment_Unit_NB);// 4
		wbt.add(Waybill_Transaction_Type_NM);// 5
		wbt.add(Manifest_Destination_Fclty_CD);// 6
		wbt.add(wb_Modify_TS);// 7
		wbt.add(Waybill_Transaction_End_TS);// 8
		wbt.add(Create_ts);// 9
		wbt.add(wbt_Modify_ts);// 10

		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn4 != null)
			conn4.close();

		return wbt;
	}
}
