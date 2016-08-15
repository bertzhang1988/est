package TestCase.OFDtesting;

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

public class DataForOFDScreenTesting {

	@DataProvider(name = "OFDScreen")
	public static Iterator<Object[]> CreateData(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DataConnection.getConnection();
		Statement stat = conn.createStatement();

		String query = null;
		if (m.getName().contains("SetTrailerNoProToOFD")) {
			query = DataCommon.query40;
		}
		if (m.getName().contains("SetTrailerWithProToOFD")) {
			query = DataCommon.query41;
		}

		ArrayList<Object[]> b = new ArrayList<Object[]>();
		ResultSet rs = stat.executeQuery(query);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		while (rs.next()) {
			String terminalcd = rs.getString("Statusing_Facility_CD");
			String SCAC = rs.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs.getString("Equipment_Unit_NB");
			String Status = rs.getString("Equipment_Status_Type_CD");
			String City_Route_NM = rs.getString("City_Route_NM");
			String City_Route_Type_NM = rs.getString("City_Route_Type_NM");
			Timestamp Planned_Delivery_DT = rs.getTimestamp("Planned_Delivery_DT");
			Timestamp Equipment_Status_TS = rs.getTimestamp("Equipment_Status_TS");
			if (!Status.equalsIgnoreCase("CLTG")) {
				City_Route_NM = "";
				City_Route_Type_NM = "";
				Planned_Delivery_DT = null;
			}
			ArrayList<Object> a = new ArrayList<Object>();
			a.add(terminalcd);
			a.add(SCAC);
			a.add(TrailerNB);
			a.add(Status);
			a.add(City_Route_NM);
			a.add(City_Route_Type_NM);
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
}
