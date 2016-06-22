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

public class DataForUS1212AND1211 {
  @DataProvider(name="12.12")
  public static Iterator<String[]> CreateData(Method m) throws ClassNotFoundException, SQLException {	
  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
  Connection conn=DataConnection.getConnection();
  Statement stat=null; 
  stat= conn.createStatement();
  String query1 = 
 " select distinct top 200 eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD]"
+" from [EQP].[Trailers_VW] eqps, [EQP].[Equipment] eqp,[EQP].[Equipment_Availability] eqpa,[EQP].[Equipment_Status_Type_Transition] eqpst,[EQP].[Waybill] wb"
+" where eqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and eqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n'"
+" and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  "
+" and [Equipment_Avbl_Status_NM]='available' and  eqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and eqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB]" 
+" and eqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldd'"
+" and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1 where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD "
+" and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB) and eqp.[Standard_Carrier_Alpha_CD]=wb.[Standard_Carrier_Alpha_CD] and eqp.[Equipment_Unit_NB]=wb.[Equipment_Unit_NB] ";
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
  if(conn !=null)conn.close();
  return b.iterator(); 
}

  public static ArrayList<String> GetFlag(String SCAC, String TrailerNB,String flagtype) throws ClassNotFoundException, SQLException {
	  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	  Connection conn=DataConnection.getConnection();
	  Statement stat=null; 
	  stat= conn.createStatement();
	  String query2 = 
 " select distinct ss.[Shipment_Service_Sub_Type_NM],ssst.[Display_Sequence_NB] from [EQP].[Waybill] wb,[EQP].[Waybill_Service] wbs,[EQP].[Shipment_Service] ss,[EQP].[Shipment_Service_Sub_Type] ssst"
+" where wb.[Pro_NB]=wbs.[Pro_NB]  and wbs.[Service_CD]=ss.[Service_CD] and ss.[Shipment_Service_Sub_Type_NM]=ssst.[Shipment_Service_Sub_Type_NM]"
+" and wb.Standard_Carrier_Alpha_CD='"+SCAC+"' and  wb.Equipment_Unit_NB='"+TrailerNB+"'  and ss.[Shipment_Service_Type_NM]='"+flagtype+"' order by ssst.[Display_Sequence_NB]";
	  ArrayList<String> c=new ArrayList<String>();
	  ResultSet rs = stat.executeQuery(query2);
	  while (rs.next()) {
		 String flag=rs.getString("Shipment_Service_Sub_Type_NM");
	     c.add(flag);}
	  if(rs !=null)rs.close();
	  if(stat !=null)stat.close();
	  if(conn !=null)conn.close();
	  
	return c;
	  
	  
  }

}