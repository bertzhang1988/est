package TestCase.CLtesting;

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

public class DataForCLScreenTesting {

	@DataProvider(name = "ClScreen1")
	public static Iterator<Object[]> CreateData(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DataConnection.getConnection();
		Statement stat = conn.createStatement();
		String query = null;
		if (m.getName().contains("ToCLNoPro")) {
			query = DataCommon.query42;
		} else if (m.getName().contains("ToCLWithPro")) {
			query = DataCommon.query43;
		} else if (m.getName().contains("CLHasPro")) {
			query = DataCommon.query46;
		} else if (m.getName().contains("CLTGHasPro")) {
			query = DataCommon.query47;
		} else if (m.getName().contains("CLTrailerWithProNotInLoading")) {
			query = DataCommon.query48;
		} else if (m.getName().contains("CLWithoutPro")) {
			query = DataCommon.query44;
		} else if (m.getName().contains("InCLStatusNoProAtCan")) {
			query = DataCommon.query51;
		} else if (m.getName().contains("ToClWithInbondProToUSLeaveOn")) {
			query = DataCommon.query62;
		} else if (m.getName().contains("ToClWithInbondProToCANLeaveOn")) {
			query = DataCommon.query63;
		}

		ArrayList<Object[]> b = new ArrayList<Object[]>();
		ResultSet rs = stat.executeQuery(query);
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
			Timestamp Equipment_Status_TS = rs.getTimestamp("Equipment_Status_TS");
			Timestamp Planned_Delivery_DT = rs.getTimestamp("Planned_Delivery_DT");

			ArrayList<Object> a = new ArrayList<Object>();
			a.add(terminalcd);
			a.add(SCAC);
			a.add(TrailerNB);
			a.add(City_Route_NM);
			a.add(City_Route_Type_NM);
			a.add(AmountPRO);
			a.add(AmountWeight);
			a.add(Planned_Delivery_DT);
			a.add(Equipment_Status_TS);
			b.add(a.toArray(new Object[8]));
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn != null)
			conn.close();
		return b.iterator();
	}

	@DataProvider(name = "ClScreen2")
	public static Iterator<Object[]> CreateData2(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DataConnection.getConnection();
		Statement stat = conn.createStatement();
		String query = null;
		if (m.getName().contains("ToClWithInbondProToUSLeaveOn")) {
			query = DataCommon.query62;
		} else if (m.getName().contains("ToClWithInbondProToCANLeaveOn")) {
			query = DataCommon.query63;
		}

		ArrayList<Object[]> b = new ArrayList<Object[]>();
		ResultSet rs = stat.executeQuery(query);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		while (rs.next()) {
			String terminalcd = rs.getString("Statusing_Facility_CD");
			String SCAC = rs.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs.getString("Equipment_Unit_NB");
			String CountryCode = rs.getString("Country_Abbreviated_NM");
			ArrayList<Object> a = new ArrayList<Object>();
			a.add(terminalcd);
			a.add(SCAC);
			a.add(TrailerNB);
			a.add(CountryCode);
			b.add(a.toArray(new Object[4]));
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
