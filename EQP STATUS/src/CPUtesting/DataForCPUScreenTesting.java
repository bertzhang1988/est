package CPUtesting;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TimeZone;

import org.testng.annotations.DataProvider;

import Page.DataCommon;


public class DataForCPUScreenTesting {

@DataProvider(name="CPUScreen")
public static Iterator<Object[]> CreateData(Method m) throws ClassNotFoundException, SQLException {	
	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	Connection conn=DriverManager.getConnection(DataCommon.db, DataCommon.user,DataCommon.password);
	Statement stat= conn.createStatement();
	
	String query = null;
	if(m.getName().contains("MtyfromONH")){
		query=DataCommon.query25;
	}if(m.getName().contains("MtyNOTfromONH")){
		query=DataCommon.query26;
	}
	
	ArrayList<Object[]> b=new ArrayList<Object[]>();
	  ResultSet rs = stat.executeQuery(query);
	  TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	  while (rs.next()) {
		 String terminalcd=rs.getString("Statusing_Facility_CD");
	     String SCAC=rs.getString("Standard_Carrier_Alpha_CD");
	     String TrailerNB=rs.getString("Equipment_Unit_NB");
	     String Desti=rs.getString("Equipment_Dest_Facility_CD");
	     if(Desti==null){Desti="";}
	     String Cube=rs.getString("Actual_Capacity_Consumed_PC");
	     if(Cube==null){Cube="";}
	     String AmountPRO=rs.getString("AmountShip");
	     if(AmountPRO.equalsIgnoreCase("0")){AmountPRO="";}
	     String AmountWeight=rs.getString("AmountWeight");
	     if(AmountWeight==null){AmountWeight="";}
	     Timestamp Equipment_Status_TS=rs.getTimestamp("Equipment_Status_TS");
	     ArrayList<Object> a= new ArrayList<Object>();
	     a.add(terminalcd);
	     a.add(SCAC);
	     a.add(TrailerNB);
	     a.add(Desti);
	     a.add(Cube);
	     a.add(AmountPRO);
	     a.add(AmountWeight);
	     a.add(Equipment_Status_TS);
	  b.add( a.toArray(new Object[7]));
	  }
	  if(rs !=null)rs.close();
	  if(stat !=null)stat.close();
	  if(conn !=null)conn.close();
	  return b.iterator(); 
	}
}
