package Data;

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

import Function.DataCommon;
import Function.DataConnection;

public class DataForUS461AndUS465AndUS457 {

@DataProvider(name="4.61And4.65And4.57")
public static Iterator<Object[]> CreateData(Method m) throws ClassNotFoundException, SQLException {	
	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	Connection conn=DataConnection.getConnection();
	Statement stat= conn.createStatement();
	String query1 = DataCommon.query34;
 
	ArrayList<Object[]> b=new ArrayList<Object[]>();
	  ResultSet rs = stat.executeQuery(query1);
	  TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	  while (rs.next()) {
		  String terminalcd=rs.getString("Statusing_Facility_CD");
	  	  String SCAC=rs.getString("Standard_Carrier_Alpha_CD");
	  	  String TrailerNB=rs.getString("Equipment_Unit_NB");
	  	  String Desti=rs.getString("Equipment_Dest_Facility_CD");
	  	  String Cube=rs.getString("Actual_Capacity_Consumed_PC");
	  	  String hld=rs.getString("Headload_Dest_Facility_CD");
	  	  String hlCube=rs.getString("Headload_Capacity_Consumed_PC");
	  	  String AmountPRO;
	      String AmountWeight;	 
	  	  AmountPRO=rs.getString("AmountShip");
	      AmountWeight=rs.getString("AmountWeight");	 
	  	    if(AmountPRO.equalsIgnoreCase("0")){AmountPRO="";}
	  	    Timestamp Equipment_Status_TS=rs.getTimestamp("Equipment_Status_TS");
	  	    ArrayList<Object> a= new ArrayList<Object>();
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
	  	     Collections.replaceAll(a, null,"");
	  	     b.add( a.toArray(new Object[10]));
	  }
	  if(rs !=null)rs.close();
	  if(stat !=null)stat.close();
	  if(conn !=null)conn.close();
	  return b.iterator(); 
	}


}
