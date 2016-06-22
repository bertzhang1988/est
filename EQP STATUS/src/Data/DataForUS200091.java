package Data;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TimeZone;

import org.testng.annotations.Test;

import Function.DataCommon;
import Function.DataConnection;

import org.testng.annotations.DataProvider;


public class DataForUS200091 {

  @DataProvider(name="2000.91")
  public static Iterator<Object[]> CreateData1(Method m) throws ClassNotFoundException, SQLException {	
	 Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	 Connection conn1=DataConnection.getConnection();
	 Statement stat= conn1.createStatement();

String Query = null;
	  if(m.getName().contains("TrailerWithoutPRO")){
		  Query=DataCommon.query23;
	  }else if(m.getName().contains("LdgTrailerWithoutPro")){
		  Query=DataCommon.query1;
	  }else if (m.getName().contains("LdgTrailerWithPro")){
		  Query=DataCommon.query2;
	  }else if(m.getName().equalsIgnoreCase("NLDDTrailerNoPRO")){
		  Query=DataCommon.query8;
	  }else if(m.getName().equalsIgnoreCase("NLDDTrailerHasPRO")){
		  Query=DataCommon.query2;
	  }else if(m.getName().equalsIgnoreCase("LDDTrailerHasProChangeDestination")||m.getName().equalsIgnoreCase("LDDTrailerHasProChangeNonDestinationField")) {
		  Query=DataCommon.query5;
	  }else if(m.getName().equalsIgnoreCase("LDDTrailerNoProChangeDestination")||m.getName().equalsIgnoreCase("LDDTrailerNoProChangeNonDestinationField")){  
		 Query=DataCommon.query6;
	  }else if(m.getName().contains("TrailerWithPRO")){
		Query=DataCommon.query24;
	  }else if(m.getName().equalsIgnoreCase("LDDTrailerWithoutProNoChange")){
		Query=DataCommon.query5;
	  }
     
	  ArrayList<Object[]> b1=new ArrayList<Object[]>();
	  ResultSet rs1 = stat.executeQuery(Query);
	  while (rs1.next()) {
	  String terminalcd=rs1.getString("Statusing_Facility_CD");
	  String SCAC=rs1.getString("Standard_Carrier_Alpha_CD");
	  String TrailerNB=rs1.getString("Equipment_Unit_NB");
	  String desti=rs1.getString("Equipment_Dest_Facility_CD");
	  Timestamp Equipment_Status_TS=rs1.getTimestamp("Equipment_Status_TS");
	  ArrayList<Object> a1= new ArrayList<Object>();
	  a1.add(terminalcd);
	  a1.add(SCAC);
	  a1.add(TrailerNB);
	  a1.add(desti);
	  a1.add(Equipment_Status_TS);
	  b1.add( a1.toArray(new Object[5]));
	  }
	  if(rs1 !=null)rs1.close();
	  if(stat !=null)stat.close();
	  if(conn1 !=null)conn1.close();
	  return b1.iterator(); 
	 }





}
