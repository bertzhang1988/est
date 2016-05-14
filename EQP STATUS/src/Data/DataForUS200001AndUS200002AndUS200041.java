package Data;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.testng.annotations.DataProvider;

import Page.DataCommon;

public class DataForUS200001AndUS200002AndUS200041 { 

@DataProvider(name="2000.41")
 public static Iterator<String[]> CreateData(Method m) throws ClassNotFoundException, SQLException {	
	  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	 Connection conn1=DriverManager.getConnection(DataCommon.db, DataCommon.user,DataCommon.password);
	  Statement stat=null; 
	  stat= conn1.createStatement();
	  
	 
	  ArrayList<String[]> b1=new ArrayList<String[]>();
	  String query = null;
	  if(m.getName().equalsIgnoreCase("VerifyValidTerminal")){
		  query=DataCommon.query27;
	  }else if(m.getName().equalsIgnoreCase("VerifyInvalidTerminal")){
		  query=DataCommon.query28;
	  }
	  ResultSet rs1 = stat.executeQuery(query);
	  while (rs1.next()) {
		 String terminalcd=rs1.getString("Facility_CD");
		 ArrayList<String> a1= new ArrayList<String>();
	     a1.add(terminalcd);
	  b1.add( a1.toArray(new String[1]));
	  }
	  if(rs1 !=null)rs1.close();
	  if(stat !=null)stat.close();
	  if(conn1 !=null)conn1.close();
	  return b1.iterator(); 
	}
 @DataProvider(name="2000.02")
 public static Iterator<String[]> CreateData2(Method m) throws ClassNotFoundException, SQLException {	
	  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	   Connection conn2=DriverManager.getConnection(DataCommon.db, DataCommon.user,DataCommon.password);
	  Statement stat=null; 
	  stat= conn2.createStatement();
	  String query2 =DataCommon.query31;
	   ArrayList<String[]> b2=new ArrayList<String[]>();
	  ResultSet rs2 = stat.executeQuery(query2);
	  while (rs2.next()) {
		 String terminalcd=rs2.getString("Statusing_Facility_CD");
		 String SCAC=rs2.getString("Standard_Carrier_Alpha_CD");
		 String TrailerNB=rs2.getString("equipment_unit_nb");
		 ArrayList<String> a2= new ArrayList<String>();
	     a2.add(terminalcd);
	     a2.add(SCAC);
	     a2.add(TrailerNB);
	  b2.add( a2.toArray(new String[3]));
	  }
	  if(rs2 !=null)rs2.close();
	  if(stat !=null)stat.close();
	  if(conn2 !=null)conn2.close();
	  return b2.iterator(); 
	}
 @DataProvider(name="2000.01retired")
 public static Iterator<String[]> CreateData3(Method m) throws ClassNotFoundException, SQLException {	
	  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	  Connection conn3=DriverManager.getConnection(DataCommon.db, DataCommon.user,DataCommon.password);
	  Statement stat=null; 
	  stat= conn3.createStatement();
	  String query3 = DataCommon.query32;

	  ArrayList<String[]> b3=new ArrayList<String[]>();
	  ResultSet rs3 = stat.executeQuery(query3);
	  while (rs3.next()) {
		 String TrailerNB=rs3.getString("equipment_unit_nb");
		 ArrayList<String> a3= new ArrayList<String>();
	     a3.add(TrailerNB);
	  b3.add( a3.toArray(new String[1]));
	  }
	  if(rs3 !=null)rs3.close();
	  if(stat !=null)stat.close();
	  if(conn3 !=null)conn3.close();
	  return b3.iterator(); 
	}
 
 @DataProvider(name = "2000.01NotDb")
 public static Object[][] createData4() {
   return new Object[][] {
		   {"19880802"},{"333333333"},{"000000000"},{"878787"},{"4444444"},{"888888888"} };
   
 }
}

















