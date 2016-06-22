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

import Function.DataCommon;
import Function.DataConnection;



public class DataForUS470 {
  
  @DataProvider(name="4.70")
  public static Iterator<String[]> CreateData(Method m) throws ClassNotFoundException, SQLException {	
  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
  Connection conn1=DataConnection.getConnection();
  Statement stat=null; 
  stat= conn1.createStatement();
  String query1=null;
  if(m.getName().contains("NoPro")){
	 query1=DataCommon.query1;
  }else if(m.getName().contains("WithPro")){
	  query1=DataCommon.query2;
  }
  
ArrayList<String[]> b=new ArrayList<String[]>();
  ResultSet rs = stat.executeQuery(query1);
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

  public static ArrayList<ArrayList<String>> GetProFromTrailerOnDifferentTerminal(String terminalcd,String SCAC,String TrailerNB) throws ClassNotFoundException, SQLException {
	  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	  Connection conn2=DataConnection.getConnection();
	  Statement stat=null; 
	  stat= conn2.createStatement();
	  String query2 = 
" select neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,wb.pro_nb"
+" from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT"
+" from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"	
+" where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
+"  and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldg' and neqps.[Equipment_Status_Type_CD] in ('ldg','arv','ldd')"
+"  and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
+"  and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
+"  and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
+" and (neqps.Equipment_Unit_NB <>'"+TrailerNB+"' or neqps.Standard_Carrier_Alpha_CD<>'"+SCAC+"') and neqps.Statusing_Facility_CD <>'"+terminalcd+"' group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,wb.pro_nb "
+" order by newid()"; 
	  ArrayList<ArrayList<String>> c=new ArrayList<ArrayList<String>>();
	  ResultSet rs = stat.executeQuery(query2);
	  while (rs.next()) {
		 ArrayList<String> proinfo=new ArrayList<String>();
		 String pro=rs.getString("PRO_NB");
		 String FromSCAC=rs.getString("Standard_Carrier_Alpha_CD");
		 String FromTrailerNB=rs.getString("Equipment_Unit_NB");
		 proinfo.add(pro);
		 proinfo.add(FromSCAC);
		 proinfo.add(FromTrailerNB);
		 c.add(proinfo);
	     
	  }
	  if(rs !=null)rs.close();
	  if(stat !=null)stat.close();
	  if(conn2 !=null)conn2.close();
	  
	return c;
	  
	  
  }
 
  
  public static ArrayList<ArrayList<String>> GetProFromTrailerOnSameTerminal(String terminalcd,String SCAC,String TrailerNB) throws ClassNotFoundException, SQLException {
	  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	  Connection conn3=DataConnection.getConnection();
	  Statement stat=null; 
	  stat= conn3.createStatement();
	  String query2 = 
" select neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,wb.pro_nb"
+" from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT"
+" from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"	
+" where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
+"  and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldg' and neqps.[Equipment_Status_Type_CD] in ('ldg','arv','ldd')"
+"  and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
+"  and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
+"  and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
+" and (neqps.Equipment_Unit_NB<>'"+TrailerNB+"' or neqps.Standard_Carrier_Alpha_CD<>'"+SCAC+"') and neqps.Statusing_Facility_CD ='"+terminalcd+"' group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,wb.pro_nb "
+" order by newid()"; 
	  ArrayList<ArrayList<String>> d=new ArrayList<ArrayList<String>>();
	  ResultSet rs = stat.executeQuery(query2);
	  while (rs.next()) {
		 ArrayList<String> proinfo=new ArrayList<String>();
		 String pro=rs.getString("PRO_NB");
		 String FromSCAC=rs.getString("Standard_Carrier_Alpha_CD");
		 String FromTrailerNB=rs.getString("Equipment_Unit_NB");
		 proinfo.add(pro);
		 proinfo.add(FromSCAC);
		 proinfo.add(FromTrailerNB);
		 d.add(proinfo);
	     
	  }
	  if(rs !=null)rs.close();
	  if(stat !=null)stat.close();
	  if(conn3 !=null)conn3.close();
	  
	return d;
	  
	  
  }
  
}
