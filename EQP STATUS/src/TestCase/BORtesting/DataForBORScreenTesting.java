package TestCase.BORtesting;

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

public class DataForBORScreenTesting {

	@DataProvider(name = "BORscreen")
	public static Iterator<Object[]> CreateData(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DataConnection.getConnection();
		Statement stat = conn.createStatement();

		String query = null;

		if (m.getName().equalsIgnoreCase("twoBlOBRDockFromONH")) {
			query = DataCommon.query19;
		}
		if (m.getName().equalsIgnoreCase("SetTrailerWithoutProToBorFromONH")) {
			query = DataCommon.query18;
		}
		if (m.getName().equalsIgnoreCase("SetTrailerWithoutProToBorNOTFromONH")) {
			query = DataCommon.query38;
		} else if (m.getName().equalsIgnoreCase("twoBlOBRAllShortFromONH")) {
			query = DataCommon.query19;
		}
		if (m.getName().equalsIgnoreCase("SmartEnterSetTrailerWithoutProToBorFromONH")) {
			query = DataCommon.query18;
		}
		if (m.getName().equalsIgnoreCase("SmartEnterSetTrailerWithoutProToBorNOTFromONH")) {
			query = DataCommon.query38;
		}
		if (m.getName().equalsIgnoreCase("twoBlOBRDockNOTFromONH")) {
			query = DataCommon.query39;
		} else if (m.getName().equalsIgnoreCase("twoBlOBRAllShortNOTFromONH")) {
			query = DataCommon.query39;
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
}
