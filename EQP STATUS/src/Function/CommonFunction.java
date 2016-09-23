package Function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CommonFunction {

	static String[][] Statuslist = { { "ARR", "LH Arrived Relay", "ARR - LH Arrived Relay" },
			{ "ARV", "LH Arrived Dest", "ARV - LH Arrived Dest" },
			{ "BOR", "Bad Order Requested", "BOR - Bad Order Requested" },
			{ "CL", "City Loading", "CL - City Loading" }, { "CLTG", "City Closed", "CLTG - City Closed" },
			{ "CPU", "City Pickup", "CPU - City Pickup" }, { "ENR", "Enroute", "ENR - Enroute" },
			{ "LDD", "Linehaul Closed", "LDD - Linehaul Closed" },
			{ "LDG", "Linehaul Loading", "LDG - Linehaul Loading" }, { "MTY", "Empty", "MTY - Empty" },
			{ "OFD", "Out for Delivery", "OFD - Out for Delivery" }, { "SPT", "Spotted", "SPT - Spotted" },
			{ "UAD", "Unloading", "UAD - Unloading" } };

	public static int CheckProPattern(String pronumber) {
		String ProNumberPattern = "(\\d{9}.)";
		if (pronumber.matches(ProNumberPattern)) {
			if (!pronumber.substring(9).toUpperCase().matches("\\d||X")) {
				return 3;
			} else {
				if (Integer.parseInt(pronumber.substring(3, 9)) % 11 == 0) {
					if (pronumber.substring(9).equalsIgnoreCase("0")) {
						return 2;
					} else {
						return 3;
					} // 1
				} else if (Integer.parseInt(pronumber.substring(3, 9)) % 11 == 1) {
					if (pronumber.substring(9).equalsIgnoreCase("x")) {
						return 2;
					} else {
						return 3;
					} // 1
				} else {
					if (11 - (Integer.parseInt(pronumber.substring(3, 9)) % 11) == (pronumber.charAt(9) - '0')) {
						return 2;
					} else {
						return 3;
					}
				}
			} // 1
		} else {
			return 1;
		}

	}

	public static String GetFullNameOfStatus(String Status) {
		String FullName = null;
		for (String[] list : Statuslist) {
			if (Status.equalsIgnoreCase(list[0]))
				FullName = list[1];
		}

		return FullName;
	}

	public static String GetAbbreNameOfStatus(String Status) {

		String AbbrevateName = null;
		for (String[] list : Statuslist) {
			if (Status.equalsIgnoreCase(list[2]))
				AbbrevateName = list[0];
		}

		return AbbrevateName;
	}

	public static int CheckCubePattern(String Cube) {
		// String cubenum=Cube.replace(" ", "").trim();
		String cubenum = Cube.trim();
		/*
		 * while(cubenum.startsWith("0")){ cubenum=cubenum.substring(1); }
		 */
		// String CubePattern="(\\d{1,2}||100)";
		String CubePattern = "([1-9]|[1-9]\\d|100)";
		if (cubenum.matches(CubePattern)/* ||cubenum.equalsIgnoreCase("0") */) {
			return 1;
		} else {
			return 2;
		}
	}

	public static Date gettime(String timezone) {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone(timezone));
		return c.getTime();
	}

	public static Date SETtime(Date time) {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		if (time != null) {
			c.setTime(time);
			return c.getTime();
		} else {
			return null;
		}
	}

	public static Date getDay(Date t) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date today = dateFormat.parse(dateFormat.format(t));
		return today;
	}

	public static String getTommorrow() throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		String getTommorrow = dateFormat.format(cal.getTime());
		return getTommorrow;
	}

	public static String getCurrentTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("MMddHHmm");// dd/MM/yyyy
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}

	public static Date getPrepopulateTimeStatusChange(String terminalcd, Date CurrentTime, Date LastStatusTime)
			throws ClassNotFoundException, SQLException {
		// check date&time field STATUS IS DIFFERENT should a. eqpst after
		// current-time use eqpst minute+1 b. eqpst before current time use
		// current time
		Calendar cal = Calendar.getInstance();
		Date LocalTime = null;
		if (LastStatusTime.before(CurrentTime)) {
			LocalTime = CommonFunction.getLocalTime(terminalcd, CurrentTime);
		} else if (LastStatusTime.after(CurrentTime)) {
			LocalTime = CommonFunction.getLocalTime(terminalcd, LastStatusTime);
		}
		cal.setTime(LocalTime);
		if (LastStatusTime.after(CurrentTime))
			cal.add(Calendar.MINUTE, 1);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getPrepopulateTimeNoStatusChange(String terminalcd, Date LastStatusTime)
			throws ClassNotFoundException, SQLException {
		// check date&time field STATUS TIME IS SAME should display eqpst time
		// IN LOCAL TIME
		Calendar cal = Calendar.getInstance();
		Date LocalTime = CommonFunction.getLocalTime(terminalcd, LastStatusTime);
		cal.setTime(LocalTime);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getPrepopulatePlanDay(String terminalcd, Date CurrentTime, Date Planned_Delivery_DT)
			throws ClassNotFoundException, SQLException {
		Calendar cal = Calendar.getInstance();
		if (Planned_Delivery_DT == null) {
			Planned_Delivery_DT = CommonFunction.getLocalTime(terminalcd, CurrentTime);
			cal.setTime(Planned_Delivery_DT);
			int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
			if (day_of_week == 1) {
				cal.add(Calendar.DAY_OF_WEEK, 1);
			} else if (day_of_week == 7) {
				cal.add(Calendar.DAY_OF_WEEK, 2);
			}
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

		} else {
			cal.setTime(Planned_Delivery_DT);
		}

		return cal.getTime();
	}

	public static Date getLocalTime(String terminal, Date Utctime) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		String query2 = " SELECT OPSF.Facility_CD,OPSF.utc_Offset_NB,OPSF.Daylight_Savings_IN, SS.[Calendar_Year_NB],SS.[Daylight_Savings_Start_TS],SS.[Daylight_Savings_End_TS] FROM EQP.Facility_vw OPSF, Shared.Daylight_Savings_Schedule SS WHERE  OPSF.Country_abbreviated_NM=SS.ISO_3_Country_CD AND Calendar_Year_NB= ? and OPSF.Facility_CD= ? ";
		PreparedStatement stat = conn2.prepareStatement(query2, ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Calendar cal = Calendar.getInstance();
		cal.setTime(Utctime);
		int year = cal.get(Calendar.YEAR);
		stat.setInt(1, year);
		stat.setString(2, terminal);
		ResultSet rs = stat.executeQuery();
		rs.absolute(1);
		int Offset_NB = rs.getInt("utc_Offset_NB");
		String Daylight_Savings_IN = rs.getString("Daylight_Savings_IN");
		Timestamp Daylight_Savings_Start_TS = rs.getTimestamp("Daylight_Savings_Start_TS");
		Timestamp Daylight_Savings_End_TS = rs.getTimestamp("Daylight_Savings_End_TS");
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();
		if (Daylight_Savings_IN.equalsIgnoreCase("Y")) {
			if (Utctime.after(Daylight_Savings_Start_TS) && Utctime.before(Daylight_Savings_End_TS)) {
				cal.add(Calendar.HOUR, Offset_NB + 1);
			} else {
				cal.add(Calendar.HOUR, Offset_NB);
			}
		} else {
			cal.add(Calendar.HOUR, Offset_NB);
		}
		Date ConvertedTime = cal.getTime();

		return ConvertedTime;

	}

	public static Date ConvertUtcTime(String terminal, Date LocalTime) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		String query2 = " SELECT OPSF.Facility_CD,OPSF.utc_Offset_NB,OPSF.Daylight_Savings_IN, SS.[Calendar_Year_NB],SS.[Daylight_Savings_Start_TS],SS.[Daylight_Savings_End_TS] FROM EQP.Facility_vw OPSF, Shared.Daylight_Savings_Schedule SS WHERE  OPSF.Country_abbreviated_NM=SS.ISO_3_Country_CD AND Calendar_Year_NB= ? and OPSF.Facility_CD= ? ";
		PreparedStatement stat = conn2.prepareStatement(query2, ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Calendar cal = Calendar.getInstance();
		cal.setTime(LocalTime);
		int year = cal.get(Calendar.YEAR);
		stat.setInt(1, year);
		stat.setString(2, terminal);
		ResultSet rs = stat.executeQuery();
		rs.absolute(1);
		int Offset_NB = rs.getInt("utc_Offset_NB");
		String Daylight_Savings_IN = rs.getString("Daylight_Savings_IN");
		Timestamp Daylight_Savings_Start_TS = rs.getTimestamp("Daylight_Savings_Start_TS");
		Timestamp Daylight_Savings_End_TS = rs.getTimestamp("Daylight_Savings_End_TS");
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();

		Date ConvertedTime;
		if (Daylight_Savings_IN.equalsIgnoreCase("Y")) {
			if (LocalTime.after(Daylight_Savings_Start_TS) && LocalTime.before(Daylight_Savings_End_TS)) {
				cal.add(Calendar.HOUR, -Offset_NB - 1);
			} else {
				cal.add(Calendar.HOUR, -Offset_NB);
			}
		} else {
			cal.add(Calendar.HOUR, -Offset_NB);
		}

		ConvertedTime = cal.getTime();
		return ConvertedTime;

	}

	public static ArrayList<Object> GetValidStatusTimeRange(Date CurrentTime, Date LastStatusTime, String ToStatus,
			String Terminal) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		String query2 = " Select future_dating_hours_qt,Past_Dating_Hours_QT from EQP.Equipment_Status_Type where Equipment_Status_Type_CD = ?";
		PreparedStatement stat = conn2.prepareStatement(query2, ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		stat.setString(1, ToStatus);
		ResultSet rs = stat.executeQuery();
		rs.absolute(1);
		int Future = rs.getInt("future_dating_hours_qt");
		int Past = rs.getInt("Past_Dating_Hours_QT");
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Calendar cal = Calendar.getInstance();
		cal.setTime(getLocalTime(Terminal, CurrentTime));
		cal.add(Calendar.HOUR_OF_DAY, Future);
		cal.add(Calendar.MINUTE, 59);
		Date Max = cal.getTime();
		cal.setTime(getLocalTime(Terminal, CurrentTime));
		cal.add(Calendar.HOUR_OF_DAY, -Past);
		Date Min = cal.getTime();
		Date LocalStatusTime = getLocalTime(Terminal, LastStatusTime);
		if (Min.before(LocalStatusTime)) {
			Min = LocalStatusTime;
		}
		SimpleDateFormat Message = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		ArrayList<Object> TimeRange = new ArrayList<Object>();
		TimeRange.add(Max);
		TimeRange.add(Min);
		TimeRange.add(Message.format(Max));
		TimeRange.add(Message.format(Min));
		return TimeRange;
	}

	public static String addHyphenToPro(String PRONB) {
		String pronb = PRONB.substring(0, 3) + "-" + PRONB.substring(3, 9) + "-" + PRONB.substring(9, PRONB.length());
		return pronb;
	}

	public static void SetTrailerToEQPdttmsh(String SCAC, String TrailerNB)
			throws InterruptedException, ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn4 = DataConnection.getConnection();
		String query3 = " UPDATE EQP.Equipment_VW  SET M204_EQP_DTTMSP_TS = 0 where Standard_Carrier_Alpha_CD= ? and Equipment_Unit_NB= ? ";
		PreparedStatement stat = conn4.prepareStatement(query3);
		stat.setString(1, SCAC);
		stat.setString(2, TrailerNB);
		stat.executeUpdate();

		if (stat != null)
			stat.close();
		if (conn4 != null)
			conn4.close();
	}

	public static void SetProToWGPdttmsh(ArrayList<String> PRONBlist)
			throws InterruptedException, ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn4 = DataConnection.getConnection();
		String query1 = "  UPDATE EQP.Waybill_vw SET M204_WGP_DTTMSP_TS = 0 where Pro_NB = ? ";
		PreparedStatement stat = conn4.prepareStatement(query1);
		for (int i = 0; i < PRONBlist.size(); i++) {
			String pro = PRONBlist.get(i);
			stat.setString(1, pro);
			stat.executeUpdate();
			if (i == 4)
				break;
		}
		if (stat != null)
			stat.close();
		if (conn4 != null)
			conn4.close();
	}

	public static String GetFlag(String SCAC, String TrailerNB, String flagtype)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DataConnection.getConnection();
		Statement stat = conn.createStatement();
		String query2 = " select distinct ss.[Shipment_Service_Sub_Type_NM],ssst.[Display_Sequence_NB] from [EQP].[Waybill] wb,[EQP].[Waybill_Service] wbs,[EQP].[Shipment_Service] ss,[EQP].[Shipment_Service_Sub_Type] ssst"
				+ " where wb.[Pro_NB]=wbs.[Pro_NB]  and wbs.[Service_CD]=ss.[Service_CD] and ss.[Shipment_Service_Sub_Type_NM]=ssst.[Shipment_Service_Sub_Type_NM]"
				+ " and wb.Standard_Carrier_Alpha_CD='" + SCAC + "' and  wb.Equipment_Unit_NB='" + TrailerNB
				+ "'  and ss.[Shipment_Service_Type_NM]='" + flagtype + "' order by ssst.[Display_Sequence_NB]";
		ArrayList<String> c = new ArrayList<String>();
		ResultSet rs = stat.executeQuery(query2);
		while (rs.next()) {
			String flag = rs.getString("Shipment_Service_Sub_Type_NM");
			c.add(flag);
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn != null)
			conn.close();

		return c.toString().replaceAll("[\\[\\] ]", "");

	}

}
