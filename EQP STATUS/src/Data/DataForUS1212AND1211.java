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

public class DataForUS1212AND1211 {
	@DataProvider(name = "12.12")
	public static Iterator<String[]> CreateData(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DataConnection.getConnection();
		Statement stat = null;
		stat = conn.createStatement();
		String query1 = DataCommon.query5;
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
		if (conn != null)
			conn.close();
		return b.iterator();
	}

}