package Data;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;

import org.testng.annotations.DataProvider;

import Function.DataCommon;
import Function.DataConnection;


public class DataForUS51212 {
  @DataProvider(name="512.12")
  public static Iterator<String[]> CreateData(Method m) throws ClassNotFoundException, SQLException {	
  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
  Connection conn1=DataConnection.getConnection();
  Statement stat= conn1.createStatement();
  ArrayList<String[]> b=new ArrayList<String[]>();
  String query=DataCommon.query44;
  ResultSet rs = stat.executeQuery(query);
  while (rs.next()) {
	 String terminalcd=rs.getString("Statusing_Facility_CD");
     String SCAC=rs.getString("Standard_Carrier_Alpha_CD");
     String TrailerNB=rs.getString("Equipment_Unit_NB");
     String Destination=rs.getString("Equipment_Dest_Facility_CD");
     ArrayList<String> a= new ArrayList<String>();
     a.add(terminalcd);
     a.add(SCAC);
     a.add(TrailerNB);
     a.add(Destination);
  b.add( a.toArray(new String[4]));
  }
  if(rs !=null)rs.close();
  if(stat !=null)stat.close();
  if(conn1 !=null)conn1.close();
  return b.iterator(); 
}

  public static ArrayList<String> GetTrailerOnSameTerminal(String terminalcd, String statustype) throws ClassNotFoundException, SQLException {
	  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	  Connection conn2=DataConnection.getConnection();
	  Statement stat= conn2.createStatement();
	  String query2 = 
" select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,nEqps.Equipment_Dest_Facility_CD"
+" from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB"
+" from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps"				 
+" where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM IN('TRAILER','STRAIGHT TRUCK')"
+" and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]='"+statustype+"' and neqps.Statusing_Facility_CD='"+terminalcd+"'"
+" and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
+" and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
+" and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD group by neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,nEqps.Equipment_Dest_Facility_CD order by newid()"; 
	  ArrayList<String> PickedTrailer=new ArrayList<String>();
	  ResultSet rs = stat.executeQuery(query2);
	  while (rs.next()) {
		 String SCAC=rs.getString("Standard_Carrier_Alpha_CD");
		 String TrailerNB=rs.getString("Equipment_Unit_NB");
		 PickedTrailer.add(SCAC);
		 PickedTrailer.add(TrailerNB); 
	  }
	  if(rs !=null)rs.close();
	  if(stat !=null)stat.close();
	  if(conn2 !=null)conn2.close();
	  
	return PickedTrailer;
  
  } 
  
  public static ArrayList<String> GetTrailerOnDifferentTerminal(String terminalcd, String statustype) throws ClassNotFoundException, SQLException {
	  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	  Connection conn2=DataConnection.getConnection();
	  Statement stat= conn2.createStatement();
	  String query2 = 
 " select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,nEqps.Equipment_Dest_Facility_CD"
+" from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB"
+" from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps"				 
+" where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM IN('TRAILER','STRAIGHT TRUCK')"
+" and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]='"+statustype+"' and neqps.Statusing_Facility_CD <>'"+terminalcd+"'"
+" and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
+" and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
+" and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD group by neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,nEqps.Equipment_Dest_Facility_CD order by newid()"; 
	  ArrayList<String> PickedTrailer=new ArrayList<String>();
	  ResultSet rs = stat.executeQuery(query2);
	  while (rs.next()) {
		 String SCAC=rs.getString("Standard_Carrier_Alpha_CD");
		 String TrailerNB=rs.getString("Equipment_Unit_NB");
		 PickedTrailer.add(SCAC);
		 PickedTrailer.add(TrailerNB); 
	  }
	  if(rs !=null)rs.close();
	  if(stat !=null)stat.close();
	  if(conn2 !=null)conn2.close();
	  
	return PickedTrailer;
  
  } 
}
