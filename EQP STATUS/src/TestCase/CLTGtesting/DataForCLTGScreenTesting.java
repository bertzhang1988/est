package TestCase.CLTGtesting;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import org.testng.annotations.DataProvider;

import Function.CommonFunction;
import Function.DataCommon;
import Function.DataConnection;

public class DataForCLTGScreenTesting {

	@DataProvider(name = "cltg screen 1")
	public static Iterator<Object[]> CreateData(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DataConnection.getConnection();
		String MethodName = m.getName();
		String query1 = null;
		if (MethodName.contains("CLWithPro")) {
			query1 = DataCommon.query46;
		} else if (MethodName.contains("CLTGWithPro")) {
			query1 = DataCommon.query47;
		} else if (MethodName.contains("ToCLTGHasPro")) {
			query1 = DataCommon.query49;
		}

		String query2 = " select distinct ss.[Shipment_Service_Sub_Type_NM],ssst.[Display_Sequence_NB] from [EQP].[Waybill_vw] wb,[EQP].[Waybill_Service_vw] wbs,[EQP].[Shipment_Service_vw] ss,[EQP].[Shipment_Service_Sub_Type_vw] ssst"
				+ " where wb.[Pro_NB]=wbs.[Pro_NB]  and wbs.[Service_CD]=ss.[Service_CD] and ss.[Shipment_Service_Sub_Type_NM]=ssst.[Shipment_Service_Sub_Type_NM]"
				+ " and wb.Standard_Carrier_Alpha_CD= ? and  wb.Equipment_Unit_NB= ?  and ss.[Shipment_Service_Type_NM]= ? order by ssst.[Display_Sequence_NB]";
		PreparedStatement stat = conn.prepareStatement(query1);
		ArrayList<Object[]> b = new ArrayList<Object[]>();
		ResultSet rs = stat.executeQuery();
		stat = conn.prepareStatement(query2);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		while (rs.next()) {
			String terminalcd = rs.getString("Statusing_Facility_CD");
			String SCAC = rs.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs.getString("Equipment_Unit_NB");
			String City_Route_NM = rs.getString("City_Route_NM");
			if (City_Route_NM == null) {
				City_Route_NM = "";
			}
			String City_Route_Type_NM = rs.getString("City_Route_Type_NM");
			if (City_Route_Type_NM == null) {
				City_Route_Type_NM = "PEDDLE";
			}

			String AmountPRO = rs.getString("AmountShip");
			if (AmountPRO.equalsIgnoreCase("0")) {
				AmountPRO = "";
			}
			String AmountWeight = rs.getString("AmountWeight");
			if (AmountWeight == null) {
				AmountWeight = "";
			}
			Date Equipment_Status_TS = CommonFunction.SETtime(rs.getTimestamp("Equipment_Status_TS"));
			Date Planned_Delivery_DT = CommonFunction.SETtime(rs.getTimestamp("Planned_Delivery_DT"));
			String flag;
			stat.setString(1, SCAC);
			stat.setString(2, TrailerNB);
			stat.setString(3, "flag");
			ResultSet rs4 = stat.executeQuery();
			ArrayList<String> e = new ArrayList<String>();
			while (rs4.next()) {
				String ServiceName = rs4.getString("Shipment_Service_Sub_Type_NM");
				e.add(ServiceName);
				e.removeAll(Collections.singleton(null));
			}
			if (e.size() != 0) {
				flag = e.toString().replaceAll("[\\[\\] ]", "");
			} else {
				flag = null;
			}
			rs4.close();
			String SERV;
			stat.setString(1, SCAC);
			stat.setString(2, TrailerNB);
			stat.setString(3, "serv");
			ResultSet rs5 = stat.executeQuery();
			ArrayList<String> S = new ArrayList<String>();
			while (rs5.next()) {
				String ServiceName = rs5.getString("Shipment_Service_Sub_Type_NM");
				S.add(ServiceName);
				S.removeAll(Collections.singleton(null));
			}
			if (S.size() != 0) {
				SERV = S.toString().replaceAll("[\\[\\] ]", "");
			} else {
				SERV = null;
			}
			rs5.close();
			ArrayList<Object> a = new ArrayList<Object>();
			a.add(terminalcd);
			a.add(SCAC);
			a.add(TrailerNB);
			a.add(City_Route_NM);
			a.add(City_Route_Type_NM);
			a.add(AmountPRO);
			a.add(AmountWeight);
			a.add(flag);
			a.add(SERV);
			Collections.replaceAll(a, null, "");// string value change null to
												// "", time value keep null
			a.add(Planned_Delivery_DT);
			a.add(Equipment_Status_TS);
			b.add(a.toArray(new Object[11]));
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn != null)
			conn.close();
		return b.iterator();
	}

	@DataProvider(name = "cltg screen 2")
	public static Iterator<Object[]> CreateData2(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DataConnection.getConnection();
		String MethodName = m.getName();
		String query1 = null;
		if (MethodName.contains("ToCLTGNoPro")) {
			query1 = DataCommon.query50;
		}
		PreparedStatement stat = conn.prepareStatement(query1);
		ArrayList<Object[]> b = new ArrayList<Object[]>();
		ResultSet rs = stat.executeQuery();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		while (rs.next()) {
			String terminalcd = rs.getString("Statusing_Facility_CD");
			String SCAC = rs.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs.getString("Equipment_Unit_NB");
			String City_Route_NM = rs.getString("City_Route_NM");
			if (City_Route_NM == null) {
				City_Route_NM = "";
			}
			String City_Route_Type_NM = rs.getString("City_Route_Type_NM");
			if (City_Route_Type_NM == null) {
				City_Route_Type_NM = "PEDDLE";
			}

			String AmountPRO = rs.getString("AmountShip");
			if (AmountPRO.equalsIgnoreCase("0")) {
				AmountPRO = "";
			}
			String AmountWeight = rs.getString("AmountWeight");
			if (AmountWeight == null) {
				AmountWeight = "";
			}
			Date Equipment_Status_TS = CommonFunction.SETtime(rs.getTimestamp("Equipment_Status_TS"));
			Date Planned_Delivery_DT = CommonFunction.SETtime(rs.getTimestamp("Planned_Delivery_DT"));

			ArrayList<Object> a = new ArrayList<Object>();
			a.add(terminalcd);
			a.add(SCAC);
			a.add(TrailerNB);
			a.add(City_Route_NM);
			a.add(City_Route_Type_NM);
			a.add(AmountPRO);
			a.add(AmountWeight);

			Collections.replaceAll(a, null, "");// string value change null to
												// "", time value keep null
			a.add(Planned_Delivery_DT);
			a.add(Equipment_Status_TS);
			b.add(a.toArray(new Object[9]));
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn != null)
			conn.close();
		return b.iterator();
	}

}
