package Function;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ConfigRd {

	Properties pro;

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
		String path = pro.getProperty("EqpsDevURL");
		return path;
	}

	public String GetDatabase() {
		String path = pro.getProperty("Devdb");
		return path;
	}

	public String GetDbUserName() {
		String path = pro.getProperty("Devuser");
		return path;
	}

	public String GetDbPassword() {
		String path = pro.getProperty("Devpassword");
		return path;
	}

	public String GetGrowlMessage(String GrowlMessage) {
		String path = pro.getProperty(GrowlMessage);
		return path;
	}
}
