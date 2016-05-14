package trial;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.testng.annotations.Test;

public class trial {
  @Test
  public void f() {
	  
	  DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	  dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	  //get current date time with Date()
	  Date date = new Date();
	  
	  // Now format the date
	  String date1= dateFormat.format(date);
	  
	  // Print the Date
	  System.out.println("Current date and time is " +date1);
	  	  
	  
	  Calendar calendar = Calendar.getInstance();
	  System.out.println(calendar);
      TimeZone fromTimeZone = calendar.getTimeZone();
      System.out.println(fromTimeZone+"\\\\");
      TimeZone toTimeZone = TimeZone.getTimeZone("CST");

      calendar.setTimeZone(fromTimeZone);
      calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);
      if (fromTimeZone.inDaylightTime(calendar.getTime())) {
          calendar.add(Calendar.MILLISECOND, calendar.getTimeZone().getDSTSavings() * -1);
      }

      calendar.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());
      if (toTimeZone.inDaylightTime(calendar.getTime())) {
          calendar.add(Calendar.MILLISECOND, toTimeZone.getDSTSavings());
      }

      System.out.println(calendar.getTime());
  }
 
  private static Date offsetTimeZone(Date date, String fromTZ, String toTZ){
	  
	// Construct FROM and TO TimeZone instances
	TimeZone fromTimeZone = TimeZone.getTimeZone(fromTZ);
	TimeZone toTimeZone = TimeZone.getTimeZone(toTZ);
	 
	// Get a Calendar instance using the default time zone and locale.
	Calendar calendar = Calendar.getInstance();
	 
	// Set the calendar's time with the given date
	calendar.setTimeZone(fromTimeZone);
	calendar.setTime(date);
	 
	System.out.println("Input: " + calendar.getTime() + " in " + fromTimeZone.getDisplayName());
	 
	// FROM TimeZone to UTC
	calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);
	 
	if (fromTimeZone.inDaylightTime(calendar.getTime())) {
	calendar.add(Calendar.MILLISECOND, calendar.getTimeZone().getDSTSavings() * -1);
	}
	 
	// UTC to TO TimeZone
	calendar.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());
	 
	if (toTimeZone.inDaylightTime(calendar.getTime())) {
	calendar.add(Calendar.MILLISECOND, toTimeZone.getDSTSavings());
	}
	 
	return calendar.getTime();
	 
	}

}
