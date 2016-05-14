package trial;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.testng.annotations.Test;

import Page.CommonFunction;

public class trial11 {
  @Test
  public void f() throws ClassNotFoundException, SQLException, ParseException {
	  Date time= CommonFunction.gettime("UTC");
	  Date time2=CommonFunction.getLocalTime("V04", time);
	  Calendar cal = Calendar.getInstance();
	  SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY HH:mm");
	 
	  cal.setTime(time2); 
	  System.out.println(cal.getTime());
	  cal.add(Calendar.MINUTE,1);
	  int hourOfDay = cal.get(Calendar.HOUR_OF_DAY); // 24 hour clock
	  int minute = cal.get(Calendar.MINUTE);
	  String today = dateFormat.format(cal.getTime());
	  System.out.println(today+" "+hourOfDay+" "+minute);
  }
}
