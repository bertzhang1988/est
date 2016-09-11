package TestCase.TrailerInquiryTesting;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.TimeZone;

import org.testng.annotations.DataProvider;

import Function.CommonFunction;
import Function.DataCommon;
import Function.DataConnection;
import TestCase.TerminalInquiryTesting.DataForInQuiryScreen;

public class DataForTrailerInquiryScreen {

	@DataProvider(name = "TrailerInquiry")
	public static Iterator<Object[]> CreateData1(Method m) throws ClassNotFoundException, SQLException {

		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn1 = DataConnection.getConnection();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		String Query = null;
		if (m.getName().contains("ldgTrailerWithPro")) {
			Query = DataCommon.query2;
		} else if (m.getName().contains("ldgTrailerWithoutPro")) {
			Query = DataCommon.query1;
		} else if (m.getName().contains("lddTrailerWithPro")) {
			Query = DataCommon.query5;
		} else if (m.getName().contains("lddTrailerWithoutPro")) {
			Query = DataCommon.query6;
		} else if (m.getName().contains("clTrailerWithoutPro")) {
			Query = DataCommon.query44;
		} else if (m.getName().contains("clTrailerWithPro")) {
			Query = DataCommon.query46;
		} else if (m.getName().contains("cltgTrailerWithPro")) {
			Query = DataCommon.query47;
		} else if (m.getName().contains("borTrailerWithPro")) {
			Query = DataCommon.query53;
		} else if (m.getName().contains("mtyTrailerWithPro")) {
			Query = DataCommon.query54;
		} else if (m.getName().contains("cpuTrailerWithPro")) {
			Query = DataCommon.query55;
		} else if (m.getName().contains("ofdTrailerWithPro")) {
			Query = DataCommon.query56;
		} else if (m.getName().contains("uadTrailerWithPro")) {
			Query = DataCommon.query57;
		} else if (m.getName().contains("enrTrailerWithPro")) {
			Query = DataCommon.query58;
		} else if (m.getName().contains("arrTrailerWithPro")) {
			Query = DataCommon.query59;
		} else if (m.getName().contains("arvTrailerWithPro")) {
			Query = DataCommon.query60;
		} else if (m.getName().contains("sptTrailerWithPro")) {
			Query = DataCommon.query61;
		}
		String query2 = "select Equipment_Sub_Type_NM from eqp.Equipment_Sub_Type_vw where Standard_Carrier_Alpha_CD= ? and Equipment_Unit_NB= ?";
		String query3 = " select distinct ss.[Shipment_Service_Sub_Type_NM],ssst.[Display_Sequence_NB] from [EQP].[Waybill_vw] wb,[EQP].[Waybill_Service] wbs,[EQP].[Shipment_Service_vw] ss,[EQP].[Shipment_Service_Sub_Type_vw] ssst"
				+ " where wb.[Pro_NB]=wbs.[Pro_NB]  and wbs.[Service_CD]=ss.[Service_CD] and ss.[Shipment_Service_Sub_Type_NM]=ssst.[Shipment_Service_Sub_Type_NM]"
				+ " and wb.Standard_Carrier_Alpha_CD= ? and  wb.Equipment_Unit_NB=?  and ss.[Shipment_Service_Type_NM]='SERV' order by ssst.[Display_Sequence_NB] ";
		PreparedStatement stat = conn1.prepareStatement(Query);
		PreparedStatement stat2 = conn1.prepareStatement(query2);
		PreparedStatement stat3 = conn1.prepareStatement(query3);
		ArrayList<Object[]> b1 = new ArrayList<Object[]>();
		String[] UseCityRouteAsDesti = { "CL", "CLTG", "OFD", "SPT", "CPU" };
		String[] NonTabulated = { "ARV", "LDD", "ENR", "ARR", "CLTG" };
		ResultSet rs1 = stat.executeQuery();
		while (rs1.next()) {
			ArrayList<Object> a1 = new ArrayList<Object>();
			String SCAC = rs1.getString("Standard_Carrier_Alpha_CD");
			String TrailerNB = rs1.getString("Equipment_Unit_NB");
			a1.add(SCAC);
			a1.add(TrailerNB);
			String Equipment_Status_Type_CD = rs1.getString("Equipment_Status_Type_CD");

			String Statusing_Facility_CD;
			if (Equipment_Status_Type_CD.equalsIgnoreCase("ENR"))
				Statusing_Facility_CD = rs1.getString("Dispatch_Dest_Facility_CD");
			else
				Statusing_Facility_CD = rs1.getString("Statusing_Facility_CD");
			Timestamp Equipment_Status_TS = rs1.getTimestamp("Equipment_Status_TS");
			String StatusTime = DataForInQuiryScreen
					.ConvertStatusTime(CommonFunction.getLocalTime(Statusing_Facility_CD, Equipment_Status_TS));
			String equipment_origin_facility_CD = rs1.getString("equipment_origin_facility_CD");

			String Dest;
			if (Arrays.asList(UseCityRouteAsDesti).contains(Equipment_Status_Type_CD)) {
				String City_Route_NM = rs1.getString("City_Route_NM");
				if (City_Route_NM != null)
					Dest = City_Route_NM;
				else
					Dest = rs1.getString("Equipment_Dest_Facility_CD");
			} else {
				Dest = rs1.getString("Equipment_Dest_Facility_CD");
			}
			String Bills;
			String WGT;
			if (Arrays.asList(NonTabulated).contains(Equipment_Status_Type_CD)) {
				Bills = rs1.getString("Observed_Shipment_QT");
				WGT = rs1.getString("Observed_Weight_QT");
			} else {
				Bills = rs1.getString("AmountShip");
				if (Bills.equalsIgnoreCase("0"))
					Bills = null;
				WGT = rs1.getString("AmountWeight");
			}

			String equipment_Exterior_Length_QT = rs1.getString("equipment_Exterior_Length_QT");
			String Primary_Use_NM = rs1.getString("Primary_Use_NM");

			// get Sub Type
			stat2.setString(1, SCAC);
			stat2.setString(2, TrailerNB);
			ResultSet rs2 = stat2.executeQuery();
			ArrayList<String> eqpSubType = new ArrayList<String>();
			while (rs2.next()) {
				String Equipment_Sub_Type_NM = rs2.getString("Equipment_Sub_Type_NM");
				eqpSubType.add(Equipment_Sub_Type_NM);
			}
			String SubType;
			if (eqpSubType.size() != 0) {
				SubType = eqpSubType.toString().replaceAll("[\\[\\]]", "").replaceAll(", ", ",");
			} else {
				SubType = null;
			}
			if (rs2 != null)
				rs2.close();

			// get services
			stat3.setString(1, SCAC);
			stat3.setString(2, TrailerNB);
			ResultSet rs3 = stat3.executeQuery();
			ArrayList<String> Services = new ArrayList<String>();
			while (rs3.next()) {
				String ServiceName = rs3.getString("Shipment_Service_Sub_Type_NM");
				Services.add(ServiceName);
				Services.removeAll(Collections.singleton(null));
			}
			String SERV;
			if (Services.size() != 0) {
				SERV = Services.toString().replaceAll("[\\[\\]]", "");
			} else {
				SERV = null;
			}
			if (rs3 != null)
				rs3.close();

			if (SCAC.equalsIgnoreCase("RDWY"))
				SCAC = "";
			String SacaTrailer = SCAC + TrailerNB;

			ArrayList<String> TI = new ArrayList<String>();
			TI.add(SacaTrailer);
			TI.add(Statusing_Facility_CD);
			TI.add(Equipment_Status_Type_CD);
			TI.add(StatusTime);
			TI.add(equipment_origin_facility_CD);
			TI.add(Dest);
			TI.add(Bills);
			TI.add(WGT);
			TI.add(equipment_Exterior_Length_QT);
			TI.add(Primary_Use_NM);
			TI.add(SubType);
			TI.add(SERV);
			TI.removeAll(Collections.singleton(null));
			a1.add(TI);
			a1.add(Equipment_Status_TS);
			b1.add(a1.toArray(new Object[4]));
		}
		if (stat2 != null)
			stat2.close();
		if (stat3 != null)
			stat3.close();
		if (rs1 != null)
			rs1.close();
		if (stat != null)
			stat.close();
		if (conn1 != null)
			conn1.close();

		return b1.iterator();

	}
}
