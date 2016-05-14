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


public class DataForUS458 {
	  @DataProvider(name="458")
	  public static Iterator<String[]> CreateData(Method m) throws ClassNotFoundException, SQLException {	
	  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	  Connection conn1=DriverManager.getConnection(DataCommon.db, DataCommon.user,DataCommon.password);
	  Statement stat=null; 
	  stat= conn1.createStatement();

	String query1 = DataCommon.query2;
	ArrayList<String[]> b=new ArrayList<String[]>();
	  ResultSet rs = stat.executeQuery(query1);
	  while (rs.next()) {
		 String terminalcd=rs.getString("Statusing_Facility_CD");
	     String SCAC=rs.getString("Standard_Carrier_Alpha_CD");
	     String TrailerNB=rs.getString("Equipment_Unit_NB");
	     ArrayList<String> a= new ArrayList<String>();
	     a.add(terminalcd);
	     a.add(SCAC);
	     a.add(TrailerNB);
	  b.add( a.toArray(new String[3]));
	  }
	  if(rs !=null)rs.close();
	  if(stat !=null)stat.close();
	  if(conn1 !=null)conn1.close();
	  return b.iterator(); 
	}

	  public static ArrayList<String> GetPro(String SCAC, String TrailerNB) throws ClassNotFoundException, SQLException {
		  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		  Connection conn2=DriverManager.getConnection(DataCommon.db, DataCommon.user,DataCommon.password);
		  Statement stat=null; 
		  stat= conn2.createStatement();
		  String query2 = 
	" select wb.pro_nb from eqp.Waybill_vw wb where wb.Standard_Carrier_Alpha_CD ='"+SCAC+"'  and wb.Equipment_Unit_NB='"+TrailerNB+"'"; 
		  ArrayList<String> c=new ArrayList<String>();
		  ResultSet rs = stat.executeQuery(query2);
		  while (rs.next()) {
			 String pro=rs.getString("PRO_NB");
		     c.add(pro);}
		  if(rs !=null)rs.close();
		  if(stat !=null)stat.close();
		  if(conn2 !=null)conn2.close();
		  
		return c;
		  
		  
	  }
}
