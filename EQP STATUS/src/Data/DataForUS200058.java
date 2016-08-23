package Data;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.testng.annotations.DataProvider;

import Function.DataConnection;
import TestCase.ReusableFunctionTest.US200058ValidateTrailerAtStatusingLocation;

public class DataForUS200058 {

	@DataProvider(name = "2000.58")
	public static Iterator<String[]> CreateData(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn1 = DataConnection.getConnection();
		Statement stat = null;
		stat = conn1.createStatement();
		String status = US200058ValidateTrailerAtStatusingLocation.SetToStatus;
		String query1 = "select top 10 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
				+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT"
				+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT"
				+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
				+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
				+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='"
				+ status + "' "
				+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
				+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
				+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
				+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
				+ " having COUNT(wb.pro_nb)=0 order by newid()";
		ArrayList<String[]> b = new ArrayList<String[]>();
		ResultSet rs = stat.executeQuery(query1);
		while (rs.next()) {
			String terminalcd = rs.getString("Statusing_Facility_CD");
			String SCAC = rs.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs.getString("Equipment_Unit_NB");
			ArrayList<String> a = new ArrayList<String>();
			a.add(terminalcd);
			a.add(SCAC);
			a.add(TrailerNB);
			b.add(a.toArray(new String[3]));
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn1 != null)
			conn1.close();
		return b.iterator();

	}
}
