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

import Function.DataCommon;
import Function.DataConnection;


public class DataForUS439 {
 
  public static ArrayList<String> Getpro1(String stuff,String SCAC,String trailernb) throws ClassNotFoundException, SQLException {
	  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	  Connection conn=DataConnection.getConnection();
	  Statement stat=null; 
	  stat= conn.createStatement();
	  String query1 = null;
	  if(stuff.equalsIgnoreCase("mr")||stuff.equalsIgnoreCase("su")){
	   query1 = "select  pro_nb from EQP.Waybill_vw where Shipment_Purpose_CD ='"+stuff+"' and ([Standard_Carrier_Alpha_CD]<>'"+SCAC+"' or [Equipment_Unit_NB]<>'"+trailernb+"') and Create_TS >'2014-11-17' ORDER BY NEWID()";
	  }else if(stuff.equalsIgnoreCase("vo")){
	   query1 = "select  pro_nb from EQP.Waybill_vw where Shipment_Correction_Type_CD ='"+stuff+"' and  Shipment_Purpose_CD not in ('mr', 'su') and (isnull([Standard_Carrier_Alpha_CD],0)<>'"+SCAC+"' or isnull([Equipment_Unit_NB],0)<>'"+trailernb+"') and Create_TS >'2014-11-17' ORDER BY NEWID()";  
	  }else{
	   query1="Select  Pro_NB from EQP.Waybill_vw where Delivery_TS IS NOT NULL AND Delivery_DT IS NOT NULL and (isnull([Standard_Carrier_Alpha_CD],0)<>'"+SCAC+"' or isnull([Equipment_Unit_NB],0)<>'"+trailernb+"') and Create_TS >'2014-11-17' ORDER BY NEWID()";  
	  }
	  ArrayList<String> c=new ArrayList<String>();
	  ResultSet rs = stat.executeQuery(query1);
	  while (rs.next()) {
		 String pro=rs.getString("pro_nb");
	     c.add(pro);}
	  if(rs !=null)rs.close();
	  if(stat !=null)stat.close();
	  if(conn !=null)conn.close();
	  
	return c;
  }
  
  @DataProvider(name="Invalid pro")
  public static Iterator<String[]> CreateData2(Method m) throws ClassNotFoundException, SQLException {	
  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
  Connection conn1=DataConnection.getConnection();
  Statement stat=null; 
  stat= conn1.createStatement();
  String query1 = null ;
 	 if(m.getName().contains("MR")){
 		query1="select top 3 pro_nb from EQP.Waybill_vw where Shipment_Purpose_CD ='MR' and Create_TS >'2014-11-17' ORDER BY NEWID()";	 
 	 }else if(m.getName().contains("SU")){
 		query1="select top 3 pro_nb from EQP.Waybill_vw where Shipment_Purpose_CD ='SU' and Create_TS >'2014-11-17' ORDER BY NEWID()";	  
 	 }else if(m.getName().contains("VO")){
 		query1="select top 3 pro_nb from EQP.Waybill_vw where Shipment_Correction_Type_CD ='VO' and Shipment_Purpose_CD not in ('MR','SU') and Create_TS >'2014-11-17' ORDER BY NEWID()";	   
 	 }else {
 		query1="select top 3 pro_nb from EQP.Waybill_vw where Delivery_TS IS NOT NULL AND Delivery_DT IS NOT NULL and Create_TS >'2014-11-17' ORDER BY NEWID()";	   
 	 }
 	 
 	 ArrayList<String[]> b1=new ArrayList<String[]>();
 	 ResultSet rs1 = stat.executeQuery(query1);
 	 while (rs1.next()) {
 		String pro=rs1.getString("PRO_NB");
 	    ArrayList<String> a1= new ArrayList<String>();
 	    a1.add(pro);
 	 b1.add( a1.toArray(new String[1]));
 	 }
 	 if(rs1 !=null)rs1.close();
 	 if(stat !=null)stat.close();
 	 if(conn1 !=null)conn1.close();
 	 return b1.iterator(); 
 	}
  
  @DataProvider(name="ldgtrailerNoPro")
  public static Iterator<String[]> CreateData1(Method m) throws ClassNotFoundException, SQLException {	
  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
   Connection conn1=DataConnection.getConnection();
 	 Statement stat=null; 
 	 stat= conn1.createStatement();
 	 String query1 = DataCommon.query1;
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
