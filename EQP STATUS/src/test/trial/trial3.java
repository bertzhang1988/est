package test.trial;

import java.awt.AWTException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;

import Function.ConfigRd;
import Page.EqpStatusPageS;

public class trial3 {
	EqpStatusPageS page;
	WebDriver driver;
	Actions builder;

	@Test
	public void f() throws InterruptedException, AWTException {
		ConfigRd Conf = new ConfigRd();
		System.setProperty("webdriver.chrome.driver",
				"C:\\Users\\uyr27b0\\Desktop\\selenium\\selenium//chromedriver.exe");
		driver = new ChromeDriver();
		page = new EqpStatusPageS(driver);
		driver.get(Conf.GetURL());
		// driver.manage().window().maximize();
		page.SetStatus("ldg");
		builder = new Actions(driver);
		page.SetLocation("108");
		page.EnterTrailer("RDWY", "94649");
		System.out.println(page.DateInput.getAttribute("value"));
		System.out.println(page.HourInput.getAttribute("value"));
		System.out.println(page.MinuteInput.getAttribute("value"));
		System.out.println(
				driver.findElement(By.cssSelector("div[model='trailerStatusVM.trailer.trailerStatusDateTime']"))
						.getAttribute("value"));
		System.out
				.println(driver
						.findElement(By
								.xpath("html/body/div[2]/div/ui-view/div/ui-view/div/div/div[1]/div[2]/div/div[2]/div"))
						.getText());

		// Action ss=builder.sendKeys(page.TerminalField,Keys.TAB).build();
		// ss.perform();
		// page.TerminalField.click();
		// System.out.println(page.TerminalField.equals(driver.switchTo().activeElement()));
		// System.out.println(page.AddProField.equals(driver.switchTo().activeElement()));

	}

	// @Test
	public void e() throws ParseException {

		SimpleDateFormat fromformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat toformatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		String dateInString = "12/15/2015 23:18:21";
		// Calendar c1 = Calendar.getInstance();
		// c1.set(Calendar.HOUR_OF_DAY);

		// TimeZone tz = TimeZone.getDefault();
		TimeZone tz = TimeZone.getTimeZone("UTC");

		fromformatter.setTimeZone(tz);
		Date date = toformatter.parse(dateInString);
		System.out.println("Date : " + date);
		System.out.println("Date : " + fromformatter.format(date));
	}

	// @Test
	public void r() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TimeZone tz = TimeZone.getTimeZone("UTC");
		formatter.setTimeZone(tz);
		Calendar c = Calendar.getInstance();
		// c.add(Calendar.MINUTE, 20);
		System.out.println("Date : " + formatter.format(c.getTime()));

	}

	// @Test
	public void v() {
		// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd
		// HH:mm:ss");

		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("UTC"));
		// c.add(Calendar.MINUTE, 20);
		// TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		System.out.println(c.getTime());// The
										// System.out.println(cal_Two.getTime())
										// invocation returns a Date from
										// getTime(). It is the Date which is
										// getting converted to a string for
										// println, and that conversion will use
										// the default timezone .
	}

	// @Test
	public void h() {
		// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd
		// HH:mm:ss");

		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		// c.setTimeZone(TimeZone.getTimeZone("UTC"));
		// c.add(Calendar.MINUTE, 20);
		// TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		System.out.println(c.getTime());// The
										// System.out.println(cal_Two.getTime())
										// invocation returns a Date from
										// getTime(). It is the Date which is
										// getting converted to a string for
										// println, and that conversion will use
										// the default timezone .
	}

	// @Test
	public void d() throws ParseException {

		SimpleDateFormat toformatter = new SimpleDateFormat("MM/DD/YYYY HH:mm");
		String dateInString = "12/15/2015 15:36:21";
		TimeZone tz = TimeZone.getTimeZone("UTC");
		toformatter.setTimeZone(tz);
		System.out.println("Date : " + toformatter.parse(dateInString));

	}

	@SuppressWarnings("deprecation")
	// @Test
	public void uu() {

		Calendar c = Calendar.getInstance();
		// Calendar c = new GregorianCalendar(2015,12,18,15,58,9);

		c.setTime(new Date(2015, 12, 15, 15, 58, 9));
		c.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Date d=
		Date a = c.getTime();

		c.add(Calendar.SECOND, -30);

		Date b = c.getTime();
		System.out.print(b.getTime() - a.getTime());

	}

	@SuppressWarnings("deprecation")
	// @Test
	public void ud() {

		Calendar c = Calendar.getInstance();
		// Calendar c = new GregorianCalendar(2015,12,18,15,58,9);
		c.setTimeZone(TimeZone.getTimeZone("UTC"));
		c.setTime(new Date(2015, 12, 15, 15, 58, 9));

		// Date d=
		c.getTime();
	}

	// @Test
	public static void getDate() throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date today = dateFormat.parse(dateFormat.format(new Date()));
		// System.out.println(today);
	}

}
