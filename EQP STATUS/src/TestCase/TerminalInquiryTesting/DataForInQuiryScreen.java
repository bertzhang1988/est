package TestCase.TerminalInquiryTesting;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.TimeZone;

import org.testng.annotations.DataProvider;

import Function.CommonFunction;
import Function.DataConnection;

public class DataForInQuiryScreen {

	@DataProvider(name = "1000.02")
	public static Iterator<String[]> CreateData(Method m) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn1 = DataConnection.getConnection();
		Statement stat = null;
		stat = conn1.createStatement();
		String query11 = " select  top 5 *  from EQP.facility_status_vw fs1, eqp.facility ef where fs1.facility_Status_Effective_DT=(select max(fs2.Facility_Status_Effective_DT) from EQP.facility_status_vw fs2 where fs1.Company_CD=fs2.Company_CD AND  fs1.Facility_CD=fs2.Facility_CD and fs1.company_cd in ('002','185')) "
				+ " and (ltrim(fs1.facility_status_nm) in ('active','open 2wk') and  fs1.closed_in<>'y'  and ef.Facility_Type_NM   in ('port','railhead','terminal','rex salvage store','relay','breakbulk') ) and fs1.company_cd in ('002','185') and fs1.Company_CD=ef.Company_CD AND  fs1.Facility_CD=ef.Facility_CD  order by newid()";
		ArrayList<String[]> b1 = new ArrayList<String[]>();
		ResultSet rs1 = stat.executeQuery(query11);
		while (rs1.next()) {
			String terminalcd = rs1.getString("Facility_CD");
			ArrayList<String> a1 = new ArrayList<String>();
			a1.add(terminalcd);
			b1.add(a1.toArray(new String[1]));
		}
		if (rs1 != null)
			rs1.close();
		if (stat != null)
			stat.close();
		if (conn1 != null)
			conn1.close();
		return b1.iterator();
	}

	public static ArrayList<ArrayList<String>> GetStatusListAndPup(String terminal)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn3 = DataConnection.getConnection();
		Statement stat = conn3.createStatement();
		String query3 = "Select neqps.Equipment_Status_Type_CD,count(case when EQP.equipment_Exterior_Length_QT<35 THEN 1  END) AS PUP,count(case when EQP.equipment_Exterior_Length_QT>=35 THEN 1  END) AS VAN"
				+ " From (select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Dest_Facility_CD,Dispatch_Dest_Facility_CD from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Equipment_Dest_Facility_CD,eqps.Dispatch_Dest_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc,eqps.M204_EQPSTAT_DTTMSP_TS) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
				+ " , EQP.Equipment_vw eQP,[EQP].[Equipment_Availability_vw] eqpa WHERE  eqp.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and eqp.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] AND eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')"
				+ " and [Equipment_Avbl_Status_NM]='available' and  Neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and Neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
				+ " AND  eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB) "
				+ " AND ((neqps.Statusing_Facility_CD = '" + terminal
				+ "' and neqps.Equipment_Status_Type_CD <> 'ENR') or (neqps.Dispatch_Dest_Facility_CD = '" + terminal
				+ "' and Neqps.Equipment_Status_Type_CD = 'ENR')) AND Neqps.Equipment_Status_Type_CD IN (SELECT EQUIPMENT_STATUS_TYPE_CD FROM EQP.Equip_Stat_Typ_Grp_Mbr AS ESTGM WHERE ESTGM.Equipment_Status_Type_Grp_NM='TRAILERINQUIRY')  group by neqps.Equipment_Status_Type_CD Order by neqps.Equipment_Status_Type_CD";
		ArrayList<ArrayList<String>> b3 = new ArrayList<ArrayList<String>>();
		ResultSet rs3 = stat.executeQuery(query3);
		while (rs3.next()) {
			String Equipment_Status_Type_CD = rs3.getString("Equipment_Status_Type_CD");
			String Pup = rs3.getString("PUP");
			String Van = rs3.getString("VAN");
			String schedule = String.valueOf((float) Integer.parseInt(Pup) / 2 + Integer.parseInt(Van));
			ArrayList<String> a3 = new ArrayList<String>();
			a3.add(Equipment_Status_Type_CD);
			a3.add(schedule);
			a3.add(Pup);
			a3.add(Van);
			b3.add(a3);
		}
		if (rs3 != null)
			rs3.close();
		if (stat != null)
			stat.close();
		if (conn3 != null)
			conn3.close();
		return b3;
	}

	public static ArrayList<String> GetStatusList(String terminal) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		Statement stat = conn2.createStatement();
		String query2 = " select distinct neqps.Equipment_Status_Type_CD From (select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Dispatch_Dest_Facility_CD from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.Dispatch_Dest_Facility_CD,eqps.Statusing_Facility_CD ,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc,eqps.M204_EQPSTAT_DTTMSP_TS) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps, EQP.Equipment_vw eQP,[EQP].[Equipment_Availability_vw] eqpa"
				+ " WHERE  eqp.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and eqp.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')"
				+ " and [Equipment_Avbl_Status_NM]='available' and  Neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and Neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
				+ " AND eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)  AND ((neqps.Statusing_Facility_CD = '"
				+ terminal + "' and neqps.Equipment_Status_Type_CD <> 'ENR') or (neqps.Dispatch_Dest_Facility_CD = '"
				+ terminal
				+ "' and Neqps.Equipment_Status_Type_CD = 'ENR'))  AND Neqps.Equipment_Status_Type_CD IN (SELECT EQUIPMENT_STATUS_TYPE_CD FROM EQP.Equip_Stat_Typ_Grp_Mbr AS ESTGM WHERE ESTGM.Equipment_Status_Type_Grp_NM='TRAILERINQUIRY') Order by neqps.Equipment_Status_Type_CD";

		ArrayList<String> d = new ArrayList<String>();
		ResultSet rs = stat.executeQuery(query2);
		while (rs.next()) {
			String Equipment_Status_Type_CD = rs.getString("Equipment_Status_Type_CD");
			d.add(Equipment_Status_Type_CD);
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();

		return d;
	}

	public static ArrayList<ArrayList<String>> GetTrailerInformation(String terminal, String status, Date d)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn3 = DataConnection.getConnection();

		String query3 = "Select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_TS],neqps.[Equipment_Origin_Facility_CD],neqps.Equipment_Dest_Facility_CD,neqps.City_Route_NM ,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) as AmountWeight,Observed_Shipment_QT,Observed_Weight_QT,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM"
				+ " From (select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Dest_Facility_CD,City_Route_NM,[Equipment_Status_TS],[Equipment_Origin_Facility_CD],Observed_Shipment_QT,Observed_Weight_QT,Dispatch_Dest_Facility_CD from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Equipment_Dest_Facility_CD,eqps.City_Route_NM,eqps.[Equipment_Status_TS],eqps.[Equipment_Origin_Facility_CD],eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.Dispatch_Dest_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc,eqps.M204_EQPSTAT_DTTMSP_TS) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
				+ " inner join EQP.Equipment_vw eqp on eqp.[Standard_Carrier_Alpha_CD]=neqps.[Standard_Carrier_Alpha_CD] and eqp.[Equipment_Unit_NB]=neqps.[Equipment_Unit_NB]"
				+ " inner join [EQP].[Equipment_Availability_vw] eqpa on Neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and Neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB]"
				+ " left join eqp.waybill_vw wb on Neqps.[Standard_Carrier_Alpha_CD]=wb.[Standard_Carrier_Alpha_CD] and Neqps.[Equipment_Unit_NB]=wb.[Equipment_Unit_NB]"
				+ " WHERE eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and [Equipment_Avbl_Status_NM]='available' and neqps.Equipment_Status_Type_CD= ?"
				+ " AND  eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB) "
				+ " AND ((neqps.Statusing_Facility_CD = '" + terminal
				+ "' and neqps.Equipment_Status_Type_CD <> 'ENR') or (Neqps.Dispatch_Dest_Facility_CD = '" + terminal
				+ "' and Neqps.Equipment_Status_Type_CD = 'ENR'))   "
				+ " group by neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_TS],neqps.[Equipment_Origin_Facility_CD] ,neqps.Equipment_Dest_Facility_CD,neqps.City_Route_NM,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT order by CASE neqps.Standard_Carrier_Alpha_CD WHEN 'rdwy' THEN 0  else 1 end, neqps.Standard_Carrier_Alpha_CD asc,neqps.[Equipment_Unit_NB] * 1 asc";

		String query9 = "select Equipment_Sub_Type_NM from eqp.Equipment_Sub_Type_vw where Standard_Carrier_Alpha_CD= ? and Equipment_Unit_NB= ?";
		String query11 = " select distinct ss.[Shipment_Service_Sub_Type_NM],ssst.[Display_Sequence_NB] from [EQP].[Waybill_vw] wb,[EQP].[Waybill_Service] wbs,[EQP].[Shipment_Service_vw] ss,[EQP].[Shipment_Service_Sub_Type_vw] ssst"
				+ " where wb.[Pro_NB]=wbs.[Pro_NB]  and wbs.[Service_CD]=ss.[Service_CD] and ss.[Shipment_Service_Sub_Type_NM]=ssst.[Shipment_Service_Sub_Type_NM]"
				+ " and wb.Standard_Carrier_Alpha_CD= ? and  wb.Equipment_Unit_NB=?  and ss.[Shipment_Service_Type_NM]='SERV' order by ssst.[Display_Sequence_NB] ";
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		ArrayList<ArrayList<String>> b3 = new ArrayList<ArrayList<String>>();
		PreparedStatement stat = conn3.prepareStatement(query3);
		PreparedStatement stat2 = conn3.prepareStatement(query9);
		PreparedStatement stat3 = conn3.prepareStatement(query11);
		stat.setString(1, status);
		String[] UseCityRoute = { "CL", "CLTG", "OFD", "SPT", "CPU" };
		String[] NonTabulated = { "ARV", "LDD", "ENR", "ARR", "CLTG" };
		ResultSet rs3 = stat.executeQuery();
		while (rs3.next()) {
			String SCAC = rs3.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs3.getString("Equipment_Unit_NB");
			Timestamp Equipment_Status_TS = rs3.getTimestamp("Equipment_Status_TS");
			String StatusTime = ConvertStatusTime(CommonFunction.getLocalTime(terminal, Equipment_Status_TS));
			String Orig = rs3.getString("Equipment_Origin_Facility_CD");
			String Dest;
			if (Arrays.asList(UseCityRoute).contains(status)) {
				Dest = rs3.getString("City_Route_NM");
			} else {
				Dest = rs3.getString("Equipment_Dest_Facility_CD");
			}
			String Bills;
			String WGT;
			if (Arrays.asList(NonTabulated).contains(status)) {
				Bills = rs3.getString("Observed_Shipment_QT");
				WGT = rs3.getString("Observed_Weight_QT");
			} else {
				Bills = rs3.getString("AmountShip");
				if (Bills.equalsIgnoreCase("0"))
					Bills = null;
				WGT = rs3.getString("AmountWeight");
			}
			String length = rs3.getString("equipment_Exterior_Length_QT");
			String Use = rs3.getString("Primary_Use_NM");
			stat2.setString(1, SCAC);
			stat2.setString(2, TrailerNB);
			ResultSet rs4 = stat2.executeQuery();
			ArrayList<String> e = new ArrayList<String>();
			while (rs4.next()) {
				String Equipment_Sub_Type_NM = rs4.getString("Equipment_Sub_Type_NM");
				e.add(Equipment_Sub_Type_NM);
			}
			long diff = d.getTime() - Equipment_Status_TS.getTime();
			long diffHours = diff / (60 * 60 * 1000);
			String Hrs = Long.toString(diffHours);
			String SubType;
			if (e.size() != 0) {
				SubType = e.toString().replaceAll("[\\[\\]]", "").replaceAll(", ", ",");
			} else {
				SubType = null;
			}
			stat3.setString(1, SCAC);
			stat3.setString(2, TrailerNB);
			ResultSet rs5 = stat3.executeQuery();
			ArrayList<String> s = new ArrayList<String>();
			while (rs5.next()) {
				String ServiceName = rs5.getString("Shipment_Service_Sub_Type_NM");
				s.add(ServiceName);
				s.removeAll(Collections.singleton(null));
			}
			String SERV;
			if (s.size() != 0) {
				SERV = s.toString().replaceAll("[\\[\\] ]", "");
			} else {
				SERV = null;
			}
			if (SCAC.equalsIgnoreCase("RDWY"))
				SCAC = "";
			String SacaTrailer = SCAC + TrailerNB;

			ArrayList<String> a3 = new ArrayList<String>();
			a3.add(SacaTrailer);
			a3.add(StatusTime);
			a3.add(Orig);
			a3.add(Dest);
			a3.add(Bills);
			a3.add(WGT);
			a3.add(length);
			a3.add(Use);
			a3.add(SubType);
			a3.add(SERV);
			a3.add(Hrs);
			a3.removeAll(Collections.singleton(null));
			b3.add(a3);
		}
		if (rs3 != null)
			rs3.close();
		if (stat != null)
			stat.close();
		if (conn3 != null)
			conn3.close();
		return b3;
	}

	public static LinkedHashSet<ArrayList<String>> GetProListInQuiry(String SCAC, String TRAILER)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn3 = DataConnection.getConnection();
		String query6 = "select wb.pro_nb, wb.Origin_Facility_CD,wb.Planned_Destination_Facility_CD,wb.Total_Handling_Units_QT,wb.Total_Actual_Weight_QT,wb.Shipment_Due_DT,wb.Manifest_Destination_Fclty_CD,wb.Headload_IN "
				+ " from eqp.waybill_vw wb  where wb.Standard_Carrier_Alpha_CD= ? and wb.Equipment_Unit_NB= ? order by wb.Headload_IN desc,wb.Waybill_Transaction_End_TS,wb.pro_nb";

		String query10 = " select wb.pro_nb,ssst.[Shipment_Service_Sub_Type_NM], ss.[Shipment_Service_Type_NM] from [EQP].[Waybill_vw] wb left join [EQP].[Waybill_Service_vw] wbs on wb.[Pro_NB]=wbs.pro_nb"
				+ " left join [EQP].[Shipment_Service_vw] ss on wbs.[Service_CD]=ss.[Service_CD] left join [EQP].[Shipment_Service_Sub_Type_vw] ssst on ss.[Shipment_Service_Sub_Type_NM]=ssst.[Shipment_Service_Sub_Type_NM]"
				+ " where wb.pro_nb= ? and ss.[Shipment_Service_Type_NM]= ?  order by ssst.[Display_Sequence_NB] ";

		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		LinkedHashSet<ArrayList<String>> b3 = new LinkedHashSet<ArrayList<String>>();
		PreparedStatement stat = conn3.prepareStatement(query6);
		PreparedStatement stat2 = conn3.prepareStatement(query10);
		stat.setString(1, SCAC);
		stat.setString(2, TRAILER);
		ResultSet rs3 = stat.executeQuery();
		while (rs3.next()) {
			String Pro = rs3.getString("pro_nb");
			String proH = CommonFunction.addHyphenToPro(Pro);
			String orig = rs3.getString("Origin_Facility_CD");
			String Destination = rs3.getString("Planned_Destination_Facility_CD");
			Timestamp Shipment_Due_DT = rs3.getTimestamp("Shipment_Due_DT");
			String DueDate;
			if (Shipment_Due_DT != null) {
				DueDate = ConvertDueDate(Shipment_Due_DT);
			} else {
				DueDate = null;
			}
			String HU = rs3.getString("Total_Handling_Units_QT");
			String WGT = rs3.getString("Total_Actual_Weight_QT");
			String ManifestDest = rs3.getString("Manifest_Destination_Fclty_CD");
			String HLI = rs3.getString("Headload_IN");
			if (HLI != null)
				if (!HLI.equalsIgnoreCase("Y"))
					HLI = null;

			stat2.setString(1, Pro);
			stat2.setString(2, "serv");
			ResultSet rs4 = stat2.executeQuery();
			ArrayList<String> e = new ArrayList<String>();
			while (rs4.next()) {
				String ServiceName = rs4.getString("Shipment_Service_Sub_Type_NM");
				e.add(ServiceName);
				e.removeAll(Collections.singleton(null));
			}

			String Services;
			if (e.size() != 0) {
				Services = e.toString().replaceAll("[\\[\\]]", "");
			} else {
				Services = null;
			}
			if (rs4 != null)
				rs4.close();

			stat2.setString(1, Pro);
			stat2.setString(2, "flag");
			ResultSet rs5 = stat2.executeQuery();
			ArrayList<String> f = new ArrayList<String>();
			while (rs5.next()) {
				String ServiceName = rs5.getString("Shipment_Service_Sub_Type_NM");
				f.add(ServiceName);
				f.removeAll(Collections.singleton(null));
			}
			String Flag;
			if (f.size() != 0) {
				Flag = f.toString().replaceAll("[\\[\\]]", "");
			} else {
				Flag = null;
			}
			if (rs5 != null)
				rs5.close();

			ArrayList<String> a3 = new ArrayList<String>();
			a3.add(proH);
			a3.add(orig);
			a3.add(Destination);
			a3.add(HU);
			a3.add(WGT);
			a3.add(DueDate);
			a3.add(Services);
			a3.add(Flag);
			a3.add(ManifestDest);
			a3.add(HLI);
			a3.removeAll(Collections.singleton(null));
			b3.add(a3);
		}
		if (rs3 != null)
			rs3.close();
		if (stat != null)
			stat.close();
		if (stat2 != null)
			stat2.close();
		if (conn3 != null)
			conn3.close();
		return b3;
	}

	public static String ConvertStatusTime(Date d) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd  HH:mm");
		String StatusTime = dateFormat.format(d);
		return StatusTime;
	}

	public static String ConvertDueDate(Date d) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd");
		String StatusTime = dateFormat.format(d);
		return StatusTime;
	}

	public static LinkedHashSet<ArrayList<String>> GetTrailerInformationByFilter(String terminal, String status, Date d,
			ArrayList<String> Length, ArrayList<String> subtype) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn3 = DataConnection.getConnection();

		String aa = "and ESBT.Equipment_Sub_Type_NM is not null ";
		if (subtype.size() == 0)
			aa = " OR ESBT.Equipment_Sub_Type_NM is null ";
		String bb = "and EQP.Equipment_Exterior_Length_QT is not null ";
		if (Length.size() == 0)
			bb = " OR EQP.Equipment_Exterior_Length_QT is null ";

		String query3 = "Select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_TS],neqps.[Equipment_Origin_Facility_CD],neqps.Equipment_Dest_Facility_CD,neqps.City_Route_NM ,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) as AmountWeight,Observed_Shipment_QT,Observed_Weight_QT,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM,ESBT.Equipment_Sub_Type_NM"
				+ " From (select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Dest_Facility_CD,City_Route_NM,[Equipment_Status_TS],[Equipment_Origin_Facility_CD],Observed_Shipment_QT,Observed_Weight_QT,Dispatch_Dest_Facility_CD from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Equipment_Dest_Facility_CD,eqps.City_Route_NM,eqps.[Equipment_Status_TS],eqps.[Equipment_Origin_Facility_CD],eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.Dispatch_Dest_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc,eqps.M204_EQPSTAT_DTTMSP_TS) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
				+ " inner join EQP.Equipment_vw eqp on eqp.[Standard_Carrier_Alpha_CD]=neqps.[Standard_Carrier_Alpha_CD] and eqp.[Equipment_Unit_NB]=neqps.[Equipment_Unit_NB]"
				+ " inner join [EQP].[Equipment_Availability_vw] eqpa on Neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and Neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB]"
				+ " left join eqp.waybill_vw wb on Neqps.[Standard_Carrier_Alpha_CD]=wb.[Standard_Carrier_Alpha_CD] and Neqps.[Equipment_Unit_NB]=wb.[Equipment_Unit_NB]"
				+ "  LEFT JOIN eqp.Equipment_Sub_Type_vw ESBT ON ESBT.[Standard_Carrier_Alpha_CD]=neqps.[Standard_Carrier_Alpha_CD] and ESBT.[Equipment_Unit_NB]=neqps.[Equipment_Unit_NB]"
				+ " WHERE eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and [Equipment_Avbl_Status_NM]='available' and neqps.Equipment_Status_Type_CD= ?"
				+ " AND  eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB) "
				+ " AND ((neqps.Statusing_Facility_CD = '" + terminal
				+ "' and neqps.Equipment_Status_Type_CD <> 'ENR') or (Neqps.Dispatch_Dest_Facility_CD = '" + terminal
				+ "' and Neqps.Equipment_Status_Type_CD = 'ENR'))  "
				+ "  AND (EQP.equipment_Exterior_Length_QT IN ( ?,?,?,?,? ) OR EQP.equipment_Exterior_Length_QT< ? or EQP.equipment_Exterior_Length_QT >= ? "
				+ bb + " )   and (ESBT.Equipment_Sub_Type_NM in ( ?,?,?,?,?,?,?,? )    " + aa + " )"
				+ " group by neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_TS],neqps.[Equipment_Origin_Facility_CD] ,neqps.Equipment_Dest_Facility_CD,neqps.City_Route_NM,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,ESBT.Equipment_Sub_Type_NM order by CASE neqps.Standard_Carrier_Alpha_CD WHEN 'rdwy' THEN 0  else 1 end, neqps.Standard_Carrier_Alpha_CD asc,neqps.[Equipment_Unit_NB] * 1 asc";

		String query9 = "select Equipment_Sub_Type_NM from eqp.Equipment_Sub_Type_vw where Standard_Carrier_Alpha_CD= ? and Equipment_Unit_NB= ?";

		String query11 = " select distinct ss.[Shipment_Service_Sub_Type_NM],ssst.[Display_Sequence_NB] from [EQP].[Waybill_vw] wb,[EQP].[Waybill_Service] wbs,[EQP].[Shipment_Service_vw] ss,[EQP].[Shipment_Service_Sub_Type_vw] ssst"
				+ " where wb.[Pro_NB]=wbs.[Pro_NB]  and wbs.[Service_CD]=ss.[Service_CD] and ss.[Shipment_Service_Sub_Type_NM]=ssst.[Shipment_Service_Sub_Type_NM]"
				+ " and wb.Standard_Carrier_Alpha_CD= ? and  wb.Equipment_Unit_NB=?  and ss.[Shipment_Service_Type_NM]='SERV' order by ssst.[Display_Sequence_NB]";
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		LinkedHashSet<ArrayList<String>> b3 = new LinkedHashSet<ArrayList<String>>();
		PreparedStatement stat = conn3.prepareStatement(query3);
		PreparedStatement stat2 = conn3.prepareStatement(query9);
		PreparedStatement stat3 = conn3.prepareStatement(query11);
		stat.setString(1, status);
		String Type[] = { "VDCK", "DECK", "DOMESTIC CONTAINER", "RAIL TRAILER", "INTL CONTAINER", "LIFTGATE", "CONV",
				"TEMP CONTROL" };
		String Le[] = { "28", "40", "45", "48", "53" };
		String L2 = null;
		String L3 = null;
		int i = 1;
		if (subtype.size() != 0) {
			while (i <= subtype.size()) {
				stat.setString(i + 8, subtype.get(i - 1));
				i++;
			}
			while (i < 9) {
				stat.setNull(i + 8, Types.VARCHAR);
				i++;
			}
			// stat.setString(17," not null ");
		} else {

			while (i <= Arrays.asList(Type).size()) {
				stat.setString(i + 8, Arrays.asList(Type).get(i - 1));
				i++;
			}
			// stat.setNull(i+8,Types.VARCHAR);
		}

		HashSet<String> newLenght = new HashSet<String>();
		for (int j = 0; j < Length.size(); j++) {
			String L = Length.get(j);
			if (L.equalsIgnoreCase("28 Feet")) {
				newLenght.add("28");
			} else if (L.equalsIgnoreCase("40 Feet")) {
				newLenght.add("40");
			} else if (L.equalsIgnoreCase("45 Feet")) {
				newLenght.add("45");
			} else if (L.equalsIgnoreCase("48 Feet")) {
				newLenght.add("48");
			} else if (L.equalsIgnoreCase("53 Feet")) {
				newLenght.add("53");
			} else if (L.equalsIgnoreCase("Pup")) {
				newLenght.clear();
				L2 = "35";
			} else if (L.equalsIgnoreCase("Van")) {
				newLenght.clear();
				L3 = "35";
			}
		}
		ArrayList<String> newleng2 = new ArrayList<String>();
		newleng2.addAll(newLenght);
		int m = 1;
		if (Length.size() != 0) {
			while (m <= newleng2.size()) {
				stat.setString(m + 1, newleng2.get(m - 1));
				m++;
			}
			while (m < 6) {
				stat.setNull(m + 1, Types.VARCHAR);
				m++;
			}
		} else {
			while (m <= Arrays.asList(Le).size()) {
				stat.setString(m + 1, Arrays.asList(Le).get(m - 1));
				m++;
			}
			L2 = "35";
			L3 = "35";
		}

		stat.setString(7, L2);
		stat.setString(8, L3);
		String[] UseCityRoute = { "CL", "CLTG", "OFD", "SPT" };
		String[] NonTabulated = { "ARV", "LDD", "ENR", "ARR", "CLTG" };
		ResultSet rs3 = stat.executeQuery();
		while (rs3.next()) {
			String SCAC = rs3.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs3.getString("Equipment_Unit_NB");
			Timestamp Equipment_Status_TS = rs3.getTimestamp("Equipment_Status_TS");
			String StatusTime = ConvertStatusTime(CommonFunction.getLocalTime(terminal, Equipment_Status_TS));
			String Orig = rs3.getString("Equipment_Origin_Facility_CD");
			String Dest;
			if (Arrays.asList(UseCityRoute).contains(status)) {
				Dest = rs3.getString("City_Route_NM");
			} else {
				Dest = rs3.getString("Equipment_Dest_Facility_CD");
			}
			String Bills;
			String WGT;
			if (Arrays.asList(NonTabulated).contains(status)) {
				Bills = rs3.getString("Observed_Shipment_QT");
				WGT = rs3.getString("Observed_Weight_QT");
			} else {
				Bills = rs3.getString("AmountShip");
				if (Bills.equalsIgnoreCase("0"))
					Bills = null;
				WGT = rs3.getString("AmountWeight");
			}
			String length = rs3.getString("equipment_Exterior_Length_QT");
			String Use = rs3.getString("Primary_Use_NM");
			stat2.setString(1, SCAC);
			stat2.setString(2, TrailerNB);
			ResultSet rs4 = stat2.executeQuery();
			ArrayList<String> e = new ArrayList<String>();
			while (rs4.next()) {
				String Equipment_Sub_Type_NM = rs4.getString("Equipment_Sub_Type_NM");
				e.add(Equipment_Sub_Type_NM);
			}
			long diff = d.getTime() - Equipment_Status_TS.getTime();
			long diffHours = diff / (60 * 60 * 1000);
			String Hrs = Long.toString(diffHours);
			String SubType;
			if (e.size() != 0) {
				SubType = e.toString().replaceAll("[\\[\\]]", "").replaceAll(", ", ",");
			} else {
				SubType = null;
			}
			stat3.setString(1, SCAC);
			stat3.setString(2, TrailerNB);
			ResultSet rs5 = stat3.executeQuery();
			ArrayList<String> s = new ArrayList<String>();
			while (rs5.next()) {
				String ServiceName = rs5.getString("Shipment_Service_Sub_Type_NM");
				s.add(ServiceName);
				s.removeAll(Collections.singleton(null));
			}
			String SERV;
			if (s.size() != 0) {
				SERV = s.toString().replaceAll("[\\[\\] ]", "");
			} else {
				SERV = null;
			}
			if (SCAC.equalsIgnoreCase("RDWY"))
				SCAC = "";
			String SacaTrailer = SCAC + TrailerNB;
			ArrayList<String> a3 = new ArrayList<String>();
			a3.add(SacaTrailer);
			a3.add(StatusTime);
			a3.add(Orig);
			a3.add(Dest);
			a3.add(Bills);
			a3.add(WGT);
			a3.add(length);
			a3.add(Use);
			a3.add(SubType);
			a3.add(SERV);
			a3.add(Hrs);
			a3.removeAll(Collections.singleton(null));
			b3.add(a3);
		}
		if (rs3 != null)
			rs3.close();
		if (stat != null)
			stat.close();
		if (conn3 != null)
			conn3.close();
		return b3;
	}

}
