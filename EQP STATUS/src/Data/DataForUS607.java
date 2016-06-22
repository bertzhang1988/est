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
import java.util.Iterator;
import java.util.TimeZone;

import org.testng.annotations.DataProvider;

import Function.DataCommon;
import Function.DataConnection;

public class DataForUS607 {

@DataProvider(name="6.07")	
public static Iterator<String[]> CreateData(Method m) throws ClassNotFoundException, SQLException {	
	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	Connection conn1=DataConnection.getConnection();
	
	Statement stat=null; 
	stat= conn1.createStatement();
    String query = null;
		
if (m.getName().contains("lddTrailerWithPros")){
	query=DataCommon.query5;
}else if (m.getName().contains("ldgTrailerWithPros")){
	query=DataCommon.query2;
} else if(m.getName().contains("ldgTrailerWithHeadloadPros")){
	query=DataCommon.query20;
}else if (m.getName().contains("ldgTrailerWithoutPros")){
	query=DataCommon.query1;
}else if (m.getName().contains("lddTrailerWithHeadloadPros")){
	query=DataCommon.query21;
}
		ArrayList<String[]> b=new ArrayList<String[]>();
		ResultSet rs = stat.executeQuery(query);
		while (rs.next()) {
		String terminalcd=rs.getString("Statusing_Facility_CD");
	    String SCAC=rs.getString("Standard_Carrier_Alpha_CD");
	    String TrailerNB=rs.getString("Equipment_Unit_NB");
	    String Desti=rs.getString("Equipment_Dest_Facility_CD");
		if(Desti==null){Desti="";}
		String Cube=rs.getString("Actual_Capacity_Consumed_PC");
		if(Cube==null){Cube="";}
		String HLCube=rs.getString("Headload_Capacity_Consumed_PC");    
		String HLDesti=rs.getString("Headload_Dest_Facility_CD");    
		ArrayList<String> a= new ArrayList<String>();
		a.add(terminalcd);
		a.add(SCAC);
		a.add(TrailerNB);
		a.add(Desti);
		a.add(Cube);
		a.add(HLCube);
		a.add(HLDesti);
		b.add( a.toArray(new String[7]));
		}
		if(rs !=null)rs.close();
		if(stat !=null)stat.close();
		if(conn1 !=null)conn1.close();
		return b.iterator(); 				
	}




}







