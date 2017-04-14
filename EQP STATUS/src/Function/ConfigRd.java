package Function;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ConfigRd {

	private Properties pro;
	private String URL;
	private String DBurl;
	private String DBuser;
	private String DBpassword;

	public ConfigRd() {
		try {
			File src = new File("./ESTConfiguration/EstConfig.property");
			FileInputStream fis = new FileInputStream(src);
			pro = new Properties();
			pro.load(fis);
			fis.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public ConfigRd(String environment) {
		try {
			File src = new File("./ESTConfiguration/EstConfig.property");
			FileInputStream fis = new FileInputStream(src);
			pro = new Properties();
			pro.load(fis);
			fis.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (environment.equalsIgnoreCase("sit")) {

			URL = pro.getProperty("EqpsSitURL");
			DBurl = pro.getProperty("Sitdb");
			DBuser = pro.getProperty("Situser");
			DBpassword = pro.getProperty("Sitpassword");

		} else if (environment.equalsIgnoreCase("dev")) {

			URL = pro.getProperty("EqpsDevURL");
			DBurl = pro.getProperty("Devdb");
			DBuser = pro.getProperty("Devuser");
			DBpassword = pro.getProperty("Devpassword");

		} else if (environment.equalsIgnoreCase("sita")) {

			URL = pro.getProperty("EqpsSitaURL");
			DBurl = pro.getProperty("Sitadb");
			DBuser = pro.getProperty("Sitauser");
			DBpassword = pro.getProperty("Sitapassword");

		} else if (environment.equalsIgnoreCase("qa")) {

			URL = pro.getProperty("EqpsQaURL");
			DBurl = pro.getProperty("Qadb");
			DBuser = pro.getProperty("Qauser");
			DBpassword = pro.getProperty("Qapassword");

		} else if (environment.equalsIgnoreCase("prod")) {

			URL = pro.getProperty("EqpsProdURL");
			DBurl = pro.getProperty("Proddb");
			DBuser = pro.getProperty("Produser");
			DBpassword = pro.getProperty("Prodpassword");
		}
	}

	public String GetChromePath() {
		String path = pro.getProperty("ChromeDriverPath");
		return path;
	}

	public String GetIEPath() {
		String path = pro.getProperty("IEdriverPath");
		return path;
	}

	public String GetPhantomJSDriverPath() {
		String path = pro.getProperty("PhantomJSDriverPath");
		return path;
	}

	public String GetURL() {
		String path = URL;
		return path;
	}

	public String GetDatabase() {
		String path = DBurl;
		return path;
	}

	public String GetDbUserName() {
		String path = DBuser;
		return path;
	}

	public String GetDbPassword() {
		String path = DBpassword;
		return path;
	}

	public String GetAD_ID() {
		String path = pro.getProperty("AD_ID");
		return path;
	}

	public String GetM_ID() {
		String path = pro.getProperty("M_ID");
		return path;
	}

	public String GetGrowlMessage(String GrowlMessage) {
		String path = pro.getProperty(GrowlMessage);
		return path;
	}
}
