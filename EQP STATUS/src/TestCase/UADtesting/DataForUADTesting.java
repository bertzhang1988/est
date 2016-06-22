package TestCase.UADtesting;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimeZone;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import Function.DataCommon;
import Function.DataConnection;



public class DataForUADTesting{
	@DataProvider(name="UADscreen")
	public static Iterator<Object[]> CreateData1(Method m) throws ClassNotFoundException, SQLException {	
	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	Connection conn1=DataConnection.getConnection();
	Statement stat= conn1.createStatement();

	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	String Query=null;
		  if(m.getName().contains("SetONHToUAD")){
			  Query=DataCommon.query13;
		  }else if(m.getName().contains("SetNOTONHToUAD")){
			  Query=DataCommon.query12;
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
}