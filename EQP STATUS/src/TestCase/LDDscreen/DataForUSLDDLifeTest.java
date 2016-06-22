package TestCase.LDDscreen;

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
import java.util.TimeZone;

import org.testng.annotations.Test;

import Function.DataCommon;
import Function.DataConnection;

import org.testng.annotations.DataProvider;

public class DataForUSLDDLifeTest {



  @DataProvider(name="lddscreen")
  public static Iterator<Object[]> CreateData1(Method m) throws ClassNotFoundException, SQLException {	
	 Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	 Connection conn1=DataConnection.getConnection();
	 Statement stat= conn1.createStatement();
     TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
     String Query = null;
	  if(m.getName().equalsIgnoreCase("NonLDDtrailerNoProSetclosed")){
		  Query=DataCommon.query8;
	  }else if(m.getName().equalsIgnoreCase("NonLDDtrailerHasProSetldg")){
		  Query=DataCommon.query7;
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
  
  @DataProvider(name="lddscreen2")
  public static Iterator<Object[]> CreateData2(Method m) throws ClassNotFoundException, SQLException {	
  	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
  	Connection conn1=DataConnection.getConnection();
 	String query=null;
  	
 	String query22 = 
" select distinct ss.[Shipment_Service_Sub_Type_NM],ssst.[Display_Sequence_NB] from [EQP].[Waybill_vw] wb,[EQP].[Waybill_Service_vw] wbs,[EQP].[Shipment_Service_vw] ss,[EQP].[Shipment_Service_Sub_Type_vw] ssst"
+" where wb.[Pro_NB]=wbs.[Pro_NB]  and wbs.[Service_CD]=ss.[Service_CD] and ss.[Shipment_Service_Sub_Type_NM]=ssst.[Shipment_Service_Sub_Type_NM]"
+" and wb.Standard_Carrier_Alpha_CD= ? and  wb.Equipment_Unit_NB= ?  and ss.[Shipment_Service_Type_NM]= ? order by ssst.[Display_Sequence_NB]";
  	Statement stat= conn1.createStatement();
  	PreparedStatement stat2 = conn1.prepareStatement(query22);

	if(m.getName().equalsIgnoreCase("LDGtrailerWithProSetToclosed")){
  		query=DataCommon.query2;
  	}else if(m.getName().equalsIgnoreCase("LDDTrailerHasProChangeDestination")){
  		query=DataCommon.query5;
  	}else if(m.getName().equalsIgnoreCase("LDDTrailerWithoutProAlterCubeAndSeal")){
  		query=DataCommon.query6;
  	}else if(m.getName().equalsIgnoreCase("")){
  	    query = DataCommon.query35;
  	}
  	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  	ArrayList<Object[]> b=new ArrayList<Object[]>();
  	  ResultSet rs = stat.executeQuery(query);
  	  while (rs.next()) {
  		 String terminalcd=rs.getString("Statusing_Facility_CD");
  	     String SCAC=rs.getString("Standard_Carrier_Alpha_CD");
  	     String TrailerNB=rs.getString("Equipment_Unit_NB");
  	     String CurrentStatus=rs.getString("Equipment_Status_Type_CD");
  	     String Desti=rs.getString("Equipment_Dest_Facility_CD");
  	     String Cube=rs.getString("Actual_Capacity_Consumed_PC");
  	     String Seal=rs.getString("Seal_NB");
  	     String hld=rs.getString("Headload_Dest_Facility_CD");
  	     String hlCube=rs.getString("Headload_Capacity_Consumed_PC");
  	     if((!CurrentStatus.equalsIgnoreCase("ldg")&&!CurrentStatus.equalsIgnoreCase("ldd"))){
  	    	 Desti=null; 
  	    	 Cube=null;
  	    	 Seal=null;
  	    	 hld=null;
  	    	 hlCube=null;
  	     }
  	     String AmountPRO;
  		 String AmountWeight;	 
  	     if(CurrentStatus.equalsIgnoreCase("ldd")){
  	      AmountPRO=rs.getString("Observed_Shipment_QT");
  		  AmountWeight=rs.getString("Observed_Weight_QT");	 
  	     }else{
  	       AmountPRO=rs.getString("AmountShip");
  		   AmountWeight=rs.getString("AmountWeight");	 
  	     }
  	    if(AmountPRO.equalsIgnoreCase("0")){AmountPRO="";}
  	    Timestamp Equipment_Status_TS=rs.getTimestamp("Equipment_Status_TS");
  	    String flag;
        stat2.setString(1,SCAC);
        stat2.setString(2,TrailerNB);
        stat2.setString(3, "flag");
		 ResultSet rs4 = stat2.executeQuery();
		 ArrayList<String> e=new ArrayList<String>();
		 while (rs4.next()) {
		 String ServiceName=rs4.getString("Shipment_Service_Sub_Type_NM");   
		 e.add(ServiceName);
		 e.removeAll(Collections.singleton(null));
		 } 
		 if(e.size()!=0){	  
		 flag=  e.toString().replaceAll("[\\[\\] ]","");
	     }else{flag=null;}	
		 String SERV;
	     stat2.setString(1,SCAC);
	     stat2.setString(2,TrailerNB);
	     stat2.setString(3, "serv");
	     ResultSet rs5 = stat2.executeQuery();
	     ArrayList<String> S=new ArrayList<String>();
	     while (rs5.next()) {
		 String ServiceName=rs5.getString("Shipment_Service_Sub_Type_NM");   
		 S.add(ServiceName);
		 S.removeAll(Collections.singleton(null));
		 } 
		 if(S.size()!=0){	  
		 SERV=  S.toString().replaceAll("[\\[\\] ]","");
		 }else{SERV=null;}	
  	    ArrayList<Object> a= new ArrayList<Object>();
  	     a.add(terminalcd);
  	     a.add(SCAC);
  	     a.add(TrailerNB);
  	     a.add(Desti);
  	     a.add(AmountPRO);
  	     a.add(AmountWeight);
  	     a.add(Cube);
  	     a.add(Seal);
  	     a.add(hld);
  	     a.add(hlCube);
  	     a.add(Equipment_Status_TS);
  	     a.add(CurrentStatus);
  	     a.add(flag);
  	     a.add(SERV);
  	     Collections.replaceAll(a, null,"");
  	  b.add( a.toArray(new Object[14]));
  	  }
  	  if(rs !=null)rs.close();
  	  if(stat !=null)stat.close();
  	  if(conn1 !=null)conn1.close();
  	  return b.iterator(); 
  	}
  
	@DataProvider(name="3000.02")
  public static Iterator<Object[]> CreateData(Method m) throws ClassNotFoundException, SQLException {	
  	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
  	Connection conn1=DataConnection.getConnection();
  	Statement stat=conn1.createStatement();
  	String query2 = 
   "select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC"
+" from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,EQP.Waybill_vw WB, EQP.Waybill_Transaction_vw wbt,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC"
+" from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"			 				 
+" where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')"
+" and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]='ldg' and neqps.Equipment_Dest_Facility_CD is not null"
+" and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
+" and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
+" and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD and  wb.Pro_NB=wbt.Pro_NB and wbt.Waybill_Transaction_Index_NB=(SELECT MAX(wbt2.Waybill_Transaction_Index_NB) FROM EQP.Waybill_Transaction wbt2 where wb.Pro_NB=wbt2.Pro_NB )"
+" group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC order by newid()";
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
  	Connection conn1=DataConnection.getConnection();
  	Statement stat=conn1.createStatement();
String query1 = 
 " select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD, ESi.Equipment_Status_Type_CD,esi.Equipment_Status_TS,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,ESI.Actual_Capacity_Consumed_PC" 
+" from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB"
+" from  [EQP].[Equipment_VW] eqp,[EQP].[Equipment_Availability_VW] eqpa,[EQP].[Equipment_Status_Type_Transition_VW] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Dest_Facility_CD,Equipment_Status_TS,Actual_Capacity_Consumed_PC,Seal_NB"
+" from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_VW] eqps) as eqq where eqq.num1=1) Neqps	"
+" where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')"
+" and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldg'  and neqps.[Equipment_Status_Type_CD] <>'ldg'" 
+" and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
+" and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
+" LEFT JOIN EQP.Waybill_VW WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
+" group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Equipment_Unit_NB,ESi.Equipment_Dest_Facility_CD,esi.Equipment_Status_Type_CD,esi.Equipment_Status_TS,ESI.Actual_Capacity_Consumed_PC having COUNT(wb.pro_nb) = 0 order by newid()";

String query2 = 
" select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD, ESi.Equipment_Status_Type_CD,esi.Equipment_Status_TS,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,ESI.Actual_Capacity_Consumed_PC" 
+" from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB"
+" from  [EQP].[Equipment_VW] eqp,[EQP].[Equipment_Availability_VW] eqpa,[EQP].[Equipment_Status_Type_Transition_VW] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Dest_Facility_CD,Equipment_Status_TS,Actual_Capacity_Consumed_PC,Seal_NB"
+" from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_VW] eqps) as eqq where eqq.num1=1) Neqps	"
+" where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')"
+" and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldg' and neqps.[Equipment_Status_Type_CD]='ldg' and neqps.Equipment_Dest_Facility_CD is not null" 
+" and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
+" and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
+" LEFT JOIN EQP.Waybill_VW WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
+" group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Equipment_Unit_NB,ESi.Equipment_Dest_Facility_CD,esi.Equipment_Status_Type_CD,esi.Equipment_Status_TS,ESI.Actual_Capacity_Consumed_PC having COUNT(wb.pro_nb) = 0 order by newid()";

    String query = null;

    if(m.getName().contains("NoLdgEmptyTrailer")){
    	query=query1;
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
