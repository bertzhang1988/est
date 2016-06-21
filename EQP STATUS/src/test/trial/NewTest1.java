package test.trial;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.annotations.Test;

public class NewTest1 {
  //@Test
  public void f() {
	  String Pup="3";
	  String Van="4";
	  //String schedule=String.format("%.1f",((float)Integer.parseInt(Pup)/2+Integer.parseInt(Van)));
	  String schedule=String.valueOf((float)Integer.parseInt(Pup)/2+Integer.parseInt(Van));
	  System.out.println(schedule);
  }
  //@Test
  public void e() {
	  float Pup=(float)3/2;
	  int Van=4;
	 // String schedule=String.format("%.1f",(float)(Integer.parseInt(Pup)/2+Integer.parseInt(Van)));
	  System.out.println(Pup+Van);
  }

@Test
public void r() throws ParseException{
	String time1 = "16:00:01";
	String time2 = "19:00:09";

	SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	Date date1 = format.parse(time1);
	Date date2 = format.parse(time2);
	long diff = date2.getTime() - date1.getTime();
	long diffHours = diff / (60 * 60 * 1000);
	System.out.print(diffHours + " hours, ");
}

}
