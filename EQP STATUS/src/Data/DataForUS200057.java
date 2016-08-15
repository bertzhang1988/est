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
import TestCase.ReusableFunctionTest.US200057EvaluateStatusTransitions;

public class DataForUS200057 {

	@DataProvider(name = "2000.57")
	public static Iterator<String[]> CreateData(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn1 = DataConnection.getConnection();
		Statement stat = null;
		stat = conn1.createStatement();

		String status = US200057EvaluateStatusTransitions.SetToStatus;
		String query1 = "select top 20 eqp.[Standard_Carrier_Alpha_CD],eqp.[Equipment_Unit_NB],Neqps.[Equipment_Status_Type_CD],Neqps.[Statusing_Facility_CD]"
				+ " from [EQP].[Equipment_vw] eqp, [EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC"
				+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
				+ " where eqp.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and eqp.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' AND eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')"
				+ " and [Equipment_Avbl_Status_NM]='available'  and  Neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and Neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
				+ " and Neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[From_Equipment_Status_Type_CD] not in( select From_Equipment_Status_Type_CD from EQP.Equipment_Status_Type_Transition_vw where To_Equipment_Status_Type_CD='"
				+ status + "')"
				+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1 where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB) "
				+ " and Neqps.statusing_Facility_CD is not null GROUP BY eqp.[Standard_Carrier_Alpha_CD],eqp.[Equipment_Unit_NB],Neqps.[Equipment_Status_Type_CD],Neqps.[Statusing_Facility_CD] order by NEWID()";

		ArrayList<String[]> b = new ArrayList<String[]>();
		ResultSet rs = stat.executeQuery(query1);
		while (rs.next()) {
			String terminalcd = rs.getString("Statusing_Facility_CD");
			String SCAC = rs.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs.getString("Equipment_Unit_NB");
			String CurrentStatus = rs.getString("Equipment_Status_Type_CD");
			ArrayList<String> a = new ArrayList<String>();
			a.add(terminalcd);
			a.add(SCAC);
			a.add(TrailerNB);
			a.add(CurrentStatus);
			b.add(a.toArray(new String[4]));
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
