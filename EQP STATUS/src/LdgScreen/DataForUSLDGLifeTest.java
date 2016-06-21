package LdgScreen;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.TimeZone;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import Page.DataCommon;
import Page.DataConnection;

public class DataForUSLDGLifeTest {


  @DataProvider(name="ldgscreen")
  public static Iterator<Object[]> CreateData1(Method m) throws ClassNotFoundException, SQLException {	
	 Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	 Connection conn1=DataConnection.getConnection();
	 Statement stat= conn1.createStatement();
	 

TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
String Query=null;
	  if(m.getName().contains("SetTrailerToLoadingNoShipments")){
		  Query=DataCommon.query3;
	  }else if(m.getName().contains("SetTrailerToLDGWithPro")){
		  Query=DataCommon.query9;
	  }
     
	  ArrayList<Object[]> b1=new ArrayList<Object[]>();
	  ResultSet rs1 = stat.executeQuery(Query);
	  while (rs1.next()) {
	  String terminalcd=rs1.getString("Statusing_Facility_CD");
	  String SCAC=rs1.getString("Standard_Carrier_Alpha_CD");
	  String TrailerNB=rs1.getString("Equipment_Unit_NB");
	  Timestamp Equipment_Status_TS=rs1.getTimestamp("Equipment_Status_TS");
	  ArrayList<Object> a1= new ArrayList<Object>();
	  a1.add(terminalcd);
	  a1.add(SCAC);
	  a1.add(TrailerNB);
	  a1.add(Equipment_Status_TS);
	  b1.add( a1.toArray(new Object[4]));
	  }
	  if(rs1 !=null)rs1.close();
	  if(stat !=null)stat.close();
	  if(conn1 !=null)conn1.close();
	  return b1.iterator(); 
	 }

  @DataProvider(name="ldgscreen2")
  public static Iterator<Object[]> CreateData(Method m) throws ClassNotFoundException, SQLException {	
  	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
  	Connection conn1=DriverManager.getConnection(DataCommon.db, DataCommon.user,DataCommon.password);
  	Statement stat=conn1.createStatement();
  	String query2 = null;
   
  if(m.getName().equalsIgnoreCase("LDGWithProChangeCube")){
	  query2=DataCommon.query2;
  }else if(m.getName().equalsIgnoreCase("LDGWithoutProChangeDestination")){
	  query2=DataCommon.query1;
  }else if(m.getName().equalsIgnoreCase("LDGTrailerWithProChangeDestination")){
	  query2=DataCommon.query2;
  }
  	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  	ArrayList<Object[]> b=new ArrayList<Object[]>();
  	ResultSet rs = stat.executeQuery(query2);
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
  	  if(conn1 !=null)conn1.close();
  	  return b.iterator(); 
  	}
  
  @DataProvider(name="3000.05")
  public static Iterator<Object[]> TrailerCanSetToLDGWithoutPro(Method m) throws ClassNotFoundException, SQLException {	
  	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
  	Connection conn1=DriverManager.getConnection(DataCommon.db, DataCommon.user,DataCommon.password);
  	Statement stat=conn1.createStatement();
    String query = null;

    if(m.getName().contains("NoLdgEmptyTrailer")){
    	query= DataCommon.query3;
    }
  	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  	ArrayList<Object[]> b=new ArrayList<Object[]>();
  	ResultSet rs = stat.executeQuery(query);
  	  while (rs.next()) {
  		 String terminalcd=rs.getString("Statusing_Facility_CD");
  	     String SCAC=rs.getString("Standard_Carrier_Alpha_CD");
  	     String TrailerNB=rs.getString("Equipment_Unit_NB");
  	     String Desti=rs.getString("Equipment_Dest_Facility_CD");
  	     String Cube=rs.getString("Actual_Capacity_Consumed_PC");
  	     String CurrentStatusType=rs.getString("Equipment_Status_Type_CD");
  	     if (!CurrentStatusType.equalsIgnoreCase("LDG")){
  	    	Desti="___";
  	    	Cube=null;
  	     }
  	    Timestamp Equipment_Status_TS=rs.getTimestamp("Equipment_Status_TS");
  	    ArrayList<Object> a= new ArrayList<Object>();
  	     a.add(terminalcd);
  	     a.add(SCAC);
  	     a.add(TrailerNB);
  	     a.add(Desti);
  	     a.add(Cube);
  	 
  	     a.add(Equipment_Status_TS);
  	     Collections.replaceAll(a, null,"");
  	     b.add( a.toArray(new Object[6]));
  	  }
  	  if(rs !=null)rs.close();
  	  if(stat !=null)stat.close();
  	  if(conn1 !=null)conn1.close();
  	  return b.iterator(); 
  	}
 
 

 




}
