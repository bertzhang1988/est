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

public class DataForUS200068AndUS445 {

	@DataProvider(name = "2000.68")
	public static Iterator<String[]> CreateData(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DataConnection.getConnection();
		Statement stat = null;
		stat = conn.createStatement();
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

}
