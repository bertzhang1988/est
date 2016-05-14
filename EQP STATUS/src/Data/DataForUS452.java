package Data;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;

import org.testng.annotations.DataProvider;

import Page.DataCommon;



public class DataForUS452 {
  @DataProvider(name="4.52")
  public static Iterator<String[]> CreateData(Method m) throws ClassNotFoundException, SQLException {	
  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
  //conn=DriverManager.getConnection(dev1db, "estdev","equipment");
  Connection conn1=DriverManager.getConnection(DataCommon.db, DataCommon.user,DataCommon.password);
  Statement stat=null; 
  stat= conn1.createStatement();
  String query1 =null;
  if(m.getName().equalsIgnoreCase("EmptyTrailerAddSomeThenDock")){
  query1=DataCommon.query1;  
  }else {
  query1= DataCommon.query2;}
  ArrayList<String[]> b1=new ArrayList<String[]>();
  ResultSet rs1 = stat.executeQuery(query1);
  while (rs1.next()) {
	 String terminalcd=rs1.getString("Statusing_Facility_CD");
     String SCAC=rs1.getString("Standard_Carrier_Alpha_CD");
     String TrailerNB=rs1.getString("Equipment_Unit_NB");
     ArrayList<String> a1= new ArrayList<String>();
     a1.add(terminalcd);
     a1.add(SCAC);
     a1.add(TrailerNB);
  b1.add( a1.toArray(new String[3]));
  }
  if(rs1 !=null)rs1.close();
  if(stat !=null)stat.close();
  if(conn1 !=null)conn1.close();
  return b1.iterator(); 
}	  
  }
 
 





  
  
