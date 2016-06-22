package Data;

import java.lang.reflect.Method;
import java.sql.Connection;
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



public class DataForUS1215 {
  @DataProvider(name="12.15")
  public static Iterator<Object[]> CreateData(Method m) throws ClassNotFoundException, SQLException {	
  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
  Connection conn=DataConnection.getConnection();
  Statement stat=null; 
  stat= conn.createStatement();
  
String query1 = 
 " select top 5 Equipment_Unit_NB, Standard_Carrier_Alpha_CD, Statusing_Facility_CD from"
+" (select rank() over(order by esi.Equipment_Unit_NB)  num1, ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,esi.Equipment_Status_Type_CD, ESi.Observed_Shipment_QT ,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip"
+" from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.Equipment_Status_Type_CD,eqps.[Statusing_Facility_CD],eqps.Observed_Shipment_QT,Eqps.Equipment_Dest_Facility_CD"
+" from [EQP].[Trailers_VW] eqps, [EQP].[Equipment] eqp,[EQP].[Equipment_Availability] eqpa"
+" where eqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and eqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n'"
+" and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and eqps.[Equipment_Status_Type_CD]='ldg'  AND Eqps.Equipment_Dest_Facility_CD IS NOT NULL"
+" and [Equipment_Avbl_Status_NM]='available' and  eqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and eqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB]" 
+" and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD  and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) ESi"
+" LEFT JOIN EQP.Waybill WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
+" group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Observed_Shipment_QT,ESi.Equipment_Unit_NB,ESi.Equipment_Dest_Facility_CD,esi.Equipment_Status_Type_CD having COUNT(wb.pro_nb)>0 ) t"
+" where t.num1 between 1 and 5 ";

String query = null;
if(m.getName().equalsIgnoreCase("LDGTrailerWithPROQuickClose")){
	query=DataCommon.query2;
}else if(m.getName().equalsIgnoreCase("NONLDGTrailerWithoutPROAddPRO")){
	query=DataCommon.query3;
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
	     String seal=rs.getString("Seal_NB");
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
	     a.add(seal);
	     a.add(hld);
	     a.add(hlCube);
	     a.add(Equipment_Status_TS);
	     Collections.replaceAll(a, null,"");
	     b.add( a.toArray(new Object[10]));
  }
  if(rs !=null)rs.close();
  if(stat !=null)stat.close();
  if(conn !=null)conn.close();
  return b.iterator(); 
}


 
  
  
}
