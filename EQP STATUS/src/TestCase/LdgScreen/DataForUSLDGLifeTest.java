package TestCase.LdgScreen;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TimeZone;

import org.testng.annotations.DataProvider;

import Function.CommonFunction;
import Function.DataCommon;
import Function.DataConnection;

public class DataForUSLDGLifeTest {

	@DataProvider(name = "ldgscreen")
	public static Iterator<Object[]> CreateData1(Method m) throws ClassNotFoundException, SQLException {
		java.util.Date d1 = CommonFunction.gettime("UTC");
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		java.util.Date d2 = CommonFunction.gettime("UTC");
		Connection conn1 = DataConnection.getConnection();
		java.util.Date d3 = CommonFunction.gettime("UTC");
		Statement stat = conn1.createStatement();
		java.util.Date d4 = CommonFunction.gettime("UTC");

		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		String Query = null;
		if (m.getName().contains("SetTrailerToLoadingNoShipments")) {
			Query = DataCommon.query3;
		} else if (m.getName().contains("SetTrailerToLDGWithPro")) {
			Query = DataCommon.query9;
		} else if (m.getName().contains("LDGTrailerWithProNotInLoading")) {
			Query = DataCommon.query45;
		}

		ArrayList<Object[]> b1 = new ArrayList<Object[]>();
		java.util.Date d5 = CommonFunction.gettime("UTC");
		ResultSet rs1 = stat.executeQuery(Query);
		java.util.Date d6 = CommonFunction.gettime("UTC");
		while (rs1.next()) {
			String terminalcd = rs1.getString("Statusing_Facility_CD");
			String SCAC = rs1.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs1.getString("Equipment_Unit_NB");
			Timestamp Equipment_Status_TS = rs1.getTimestamp("Equipment_Status_TS");
			ArrayList<Object> a1 = new ArrayList<Object>();
			a1.add(terminalcd);
			a1.add(SCAC);
			a1.add(TrailerNB);
			a1.add(Equipment_Status_TS);
			b1.add(a1.toArray(new Object[4]));
		}
		java.util.Date d7 = CommonFunction.gettime("UTC");
		if (rs1 != null)
			rs1.close();
		if (stat != null)
			stat.close();
		if (conn1 != null)
			conn1.close();
		java.util.Date d8 = CommonFunction.gettime("UTC");
		System.out.println(" 1 " + (d2.getTime() - d1.getTime()) / 1000.0);
		System.out.println(" 2 " + (d3.getTime() - d2.getTime()) / 1000.0);
		System.out.println(" 3 " + (d4.getTime() - d3.getTime()) / 1000.0);
		System.out.println(" 4 " + (d5.getTime() - d4.getTime()) / 1000.0);
		System.out.println(" 5 " + (d6.getTime() - d5.getTime()) / 1000.0);
		System.out.println(" 6 " + (d7.getTime() - d6.getTime()) / 1000.0);
		System.out.println(" 7 " + (d8.getTime() - d7.getTime()) / 1000.0);
		return b1.iterator();

	}

	@DataProvider(name = "ldgscreen2")
	public static Iterator<Object[]> CreateData(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn1 = DataConnection.getConnection();
		Statement stat = conn1.createStatement();
		String query2 = null;

		if (m.getName().equalsIgnoreCase("LDGWithProChangeCube")) {
			query2 = DataCommon.query2;
		} else if (m.getName().equalsIgnoreCase("LDGWithoutProChangeDestination")) {
			query2 = DataCommon.query1;
		} else if (m.getName().equalsIgnoreCase("LDGTrailerWithProChangeDestination")) {
			query2 = DataCommon.query2;
		}
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		ArrayList<Object[]> b = new ArrayList<Object[]>();
		ResultSet rs = stat.executeQuery(query2);
		while (rs.next()) {
			String terminalcd = rs.getString("Statusing_Facility_CD");
			String SCAC = rs.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs.getString("Equipment_Unit_NB");
			String Desti = rs.getString("Equipment_Dest_Facility_CD");
			String Cube = rs.getString("Actual_Capacity_Consumed_PC");
			String hld = rs.getString("Headload_Dest_Facility_CD");
			String hlCube = rs.getString("Headload_Capacity_Consumed_PC");
			String AmountPRO;
			String AmountWeight;
			AmountPRO = rs.getString("AmountShip");
			AmountWeight = rs.getString("AmountWeight");
			if (AmountPRO.equalsIgnoreCase("0")) {
				AmountPRO = "";
			}
			Timestamp Equipment_Status_TS = rs.getTimestamp("Equipment_Status_TS");
			ArrayList<Object> a = new ArrayList<Object>();
			a.add(terminalcd);
			a.add(SCAC);
			a.add(TrailerNB);
			a.add(Desti);
			a.add(AmountPRO);
			a.add(AmountWeight);
			a.add(Cube);
			a.add(hld);
			a.add(hlCube);
			a.add(Equipment_Status_TS);
			Collections.replaceAll(a, null, "");
			b.add(a.toArray(new Object[10]));
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn1 != null)
			conn1.close();
		return b.iterator();
	}

	@DataProvider(name = "3000.05")
	public static Iterator<Object[]> TrailerCanSetToLDGWithoutPro(Method m)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn1 = DataConnection.getConnection();
		Statement stat = conn1.createStatement();
		String query = null;

		if (m.getName().contains("NoLdgEmptyTrailer")) {
			query = DataCommon.query3;
		}
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		ArrayList<Object[]> b = new ArrayList<Object[]>();
		ResultSet rs = stat.executeQuery(query);
		while (rs.next()) {
			String terminalcd = rs.getString("Statusing_Facility_CD");
			String SCAC = rs.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs.getString("Equipment_Unit_NB");
			String Desti = rs.getString("Equipment_Dest_Facility_CD");
			String Cube = rs.getString("Actual_Capacity_Consumed_PC");
			String CurrentStatusType = rs.getString("Equipment_Status_Type_CD");
			if (!CurrentStatusType.equalsIgnoreCase("LDG")) {
				Desti = "___";
				Cube = null;
			}
			Timestamp Equipment_Status_TS = rs.getTimestamp("Equipment_Status_TS");
			ArrayList<Object> a = new ArrayList<Object>();
			a.add(terminalcd);
			a.add(SCAC);
			a.add(TrailerNB);
			a.add(Desti);
			a.add(Cube);

			a.add(Equipment_Status_TS);
			Collections.replaceAll(a, null, "");
			b.add(a.toArray(new Object[6]));
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn1 != null)
			conn1.close();
		return b.iterator();
	}

}
