package Function;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DataConnection extends Setup {

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(conf.GetDatabase(), conf.GetDbUserName(), conf.GetDbPassword());

	}

}
