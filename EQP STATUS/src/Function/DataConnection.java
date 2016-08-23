package Function;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DataConnection {
	
	
	public static Connection getConnection() throws SQLException{
		ConfigRd conf = new ConfigRd();	
		return DriverManager.getConnection(conf.GetDatabase(), conf.GetDbUserName(),conf.GetDbPassword());	
		
	}	
	
}
