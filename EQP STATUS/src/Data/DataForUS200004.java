package Data;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.testng.annotations.DataProvider;

import Page.DataCommon;


public class DataForUS200004 {

@DataProvider(name="ldgtrailerNoPro")
 public static Iterator<String[]> CreateData1(Method m) throws ClassNotFoundException, SQLException {	
 Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
  Connection conn1=DriverManager.getConnection(DataCommon.db, DataCommon.user,DataCommon.password);
	 Statement stat=null; 
	 stat= conn1.createStatement();
	 String query1 = DataCommon.query1;
	 ArrayList<String[]> b1=new ArrayList<String[]>();
	 ResultSet rs1 = stat.executeQuery(query1);
	 while (rs1.next()) {
		String terminalcd=rs1.getString("Statusing_Facility_CD");
	    String SCAC=rs1.getString("Standard_Carrier_Alpha_CD");
	    String TrailerNB=rs1.getString("Equipment_Unit_NB");
	    ArrayList<String> a1= new ArrayList<String>();
	    a1.add(terminalcd);
	    a1.add(SCAC);
	    a1.add(TrailerNB);
	 b1.add( a1.toArray(new String[3]));
	 }
	 if(rs1 !=null)rs1.close();
	 if(stat !=null)stat.close();
	 if(conn1 !=null)conn1.close();
	 return b1.iterator(); 
	}
  
 public static String[] prolist=
	 {"1234567890",
		 "123456789x",
		 "1234567898",
		 "41ab450828",
		 "275504175Y",
		 "41780%1@70",
		 "2534970186",
		 "417146642X",
		 "2759135536",
		 "4178051671",
		 "abcdefghij",
		 "4831667.18",
		 "4534307179",
		 "5994370457",
		 "4464557219",
		 "2155482712",
		 "2759129304",
		 "417146642X",
		 "453430717X",
		 "534616309X",
		 "058026401X",
		 "215548263X",
		 "275746846X",
		 "087513756X",
		 "275909943X",
		 "41ab450828"};
 
 @DataProvider(name="2000.04")
  public static String[][] dp() {
    return new String[][] {
{"1234567890"},
{"123456789x"},
{"1234567898"},
{"41ab450828"},
{"275504175Y"},
{"41780%1@70"},
{"2534970186"},
{"417146642X"},
{"2759135536"},
{"4178051671"},
{"abcdefghij"},
{"4831667.18"},
{"4534307179"},
{"5994370457"},
{"4464557219"},
{"2155482712"},
{"2759129304"},
{"417146642X"},
{"453430717X"},
{"534616309X"},
{"058026401X"},
{"215548263X"},
{"275746846X"},
{"087513756X"},
{"275909943X"},
{"41ab450828"}


    };
  }
}
