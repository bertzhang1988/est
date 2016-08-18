package Data;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.testng.annotations.DataProvider;

import Function.DataConnection;

public class DataForUS439 {

	public static ArrayList<String> Getpro1(String stuff, String SCAC, String trailernb)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DataConnection.getConnection();
		Statement stat = null;
		stat = conn.createStatement();
		String query1 = null;
		if (stuff.equalsIgnoreCase("mr") || stuff.equalsIgnoreCase("su")) {
			query1 = "select  pro_nb from EQP.Waybill_vw where Shipment_Purpose_CD ='" + stuff
					+ "' and ([Standard_Carrier_Alpha_CD]<>'" + SCAC + "' or [Equipment_Unit_NB]<>'" + trailernb
					+ "') and Create_TS >'2014-11-17' ORDER BY NEWID()";
		} else if (stuff.equalsIgnoreCase("vo")) {
			query1 = "select  pro_nb from EQP.Waybill_vw where Shipment_Correction_Type_CD ='" + stuff
					+ "' and  Shipment_Purpose_CD not in ('mr', 'su') and (isnull([Standard_Carrier_Alpha_CD],0)<>'"
					+ SCAC + "' or isnull([Equipment_Unit_NB],0)<>'" + trailernb
					+ "') and Create_TS >'2014-11-17' ORDER BY NEWID()";
		} else {
			query1 = "Select  Pro_NB from EQP.Waybill_vw where Delivery_TS IS NOT NULL AND Delivery_DT IS NOT NULL and (isnull([Standard_Carrier_Alpha_CD],0)<>'"
					+ SCAC + "' or isnull([Equipment_Unit_NB],0)<>'" + trailernb
					+ "') and Create_TS >'2014-11-17' ORDER BY NEWID()";
		}
		ArrayList<String> c = new ArrayList<String>();
		ResultSet rs = stat.executeQuery(query1);
		while (rs.next()) {
			String pro = rs.getString("pro_nb");
			c.add(pro);
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn != null)
			conn.close();

		return c;
	}

	@DataProvider(name = "Invalid pro")
	public static Iterator<String[]> CreateData2(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn1 = DataConnection.getConnection();
		Statement stat = null;
		stat = conn1.createStatement();
		String query1 = null;
		if (m.getName().contains("MR")) {
			query1 = "select top 3 pro_nb from EQP.Waybill_vw where Shipment_Purpose_CD ='MR' and Create_TS >'2014-11-17' ORDER BY NEWID()";
		} else if (m.getName().contains("SU")) {
			query1 = "select top 3 pro_nb from EQP.Waybill_vw where Shipment_Purpose_CD ='SU' and Create_TS >'2014-11-17' ORDER BY NEWID()";
		} else if (m.getName().contains("VO")) {
			query1 = "select top 3 pro_nb from EQP.Waybill_vw where Shipment_Correction_Type_CD ='VO' and Shipment_Purpose_CD not in ('MR','SU') and Create_TS >'2014-11-17' ORDER BY NEWID()";
		} else {
			query1 = "select top 3 pro_nb from EQP.Waybill_vw where Delivery_TS IS NOT NULL AND Delivery_DT IS NOT NULL and Create_TS >'2014-11-17' ORDER BY NEWID()";
		}

		ArrayList<String[]> b1 = new ArrayList<String[]>();
		ResultSet rs1 = stat.executeQuery(query1);
		while (rs1.next()) {
			String pro = rs1.getString("PRO_NB");
			ArrayList<String> a1 = new ArrayList<String>();
			a1.add(pro);
			b1.add(a1.toArray(new String[1]));
		}
		if (rs1 != null)
			rs1.close();
		if (stat != null)
			stat.close();
		if (conn1 != null)
			conn1.close();
		return b1.iterator();
	}

	@DataProvider(name = "Inbond pro")
	public static Iterator<String[]> GetInbondPro(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn4 = DataConnection.getConnection();
		Statement stat = conn4.createStatement();
		String query4 = " select top 3 w.Pro_NB, sc.Shipment_Is_In_Bond_IN, sc.Shipment_Is_Cntry_Mismatch_IN FROM EQP.Waybill w , SHIP.Shipment_Characteristic sc "
				+ " where w.Shipment_Characteristic_KEY = sc.Shipment_Characteristic_KEY AND sc.Shipment_Is_In_Bond_IN= 'Y' AND ((w.Shipment_Purpose_CD <> 'SU' AND w.Shipment_Purpose_CD <> 'MR') OR w.Shipment_Purpose_CD is null)"
				+ "  AND (w.Shipment_Correction_Type_CD <> 'VO' OR w.Shipment_Correction_Type_CD is null) AND w.Delivery_DT is null and w.Delivery_TS is null AND w.Create_TS > '2015-09-30' group by w.Pro_NB, sc.Shipment_Is_In_Bond_IN, sc.Shipment_Is_Cntry_Mismatch_IN ORDER BY NEWID()";

		String query5 = " select top 3 w.Pro_NB, sc.Shipment_Is_In_Bond_IN, sc.Shipment_Is_Cntry_Mismatch_IN FROM EQP.Waybill w , SHIP.Shipment_Characteristic sc , EQP.Waybill_Delivery_Codeword wdc where w.Shipment_Characteristic_KEY = sc.Shipment_Characteristic_KEY  AND w.Pro_NB = wdc.Pro_NB AND sc.Shipment_Is_Cntry_Mismatch_IN = 'Y' "
				+ " AND wdc.Pro_NB not in (Select wdc2.Pro_NB FROM EQP.Waybill_Delivery_Codeword wdc2 Where wdc.Pro_NB = wdc2.Pro_NB AND wdc2.Delivery_Codeword_CD in ('PARS','RCOD','RA49','SZCU','RCSA','CL')) AND ((w.Shipment_Purpose_CD <> 'SU' AND w.Shipment_Purpose_CD <> 'MR') OR w.Shipment_Purpose_CD is null) "
				+ "  AND (w.Shipment_Correction_Type_CD <> 'VO' OR w.Shipment_Correction_Type_CD is null) AND w.Delivery_DT is null and w.Delivery_TS is null AND w.Create_TS > '2015-09-30' group by w.Pro_NB, sc.Shipment_Is_In_Bond_IN, sc.Shipment_Is_Cntry_Mismatch_IN ORDER BY NEWID()";
		String query = null;
		if (m.getName().contains("InbondProToUS"))
			query = query4;
		else if (m.getName().contains("InbondProToCAN"))
			query = query5;

		ResultSet rs = stat.executeQuery(query);
		ArrayList<String[]> b1 = new ArrayList<String[]>();
		while (rs.next()) {
			ArrayList<String> d = new ArrayList<String>();
			String pro = rs.getString("PRO_NB");
			d.add(pro);
			b1.add(d.toArray(new String[1]));
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn4 != null)
			conn4.close();

		return b1.iterator();

	}

}
