package test.trial;

import org.testng.annotations.Test;

public class trial7 {
  @Test
  public void f() {
	  String a="123";
	  a.replace("1", "2");
	  System.out.println(a);
 
	  
	  String b= new String("123");
	  b.replace("1", "2");
	  System.out.println(b);
	 
	  
	  
	  String c=a.replace("1", "2");
	  System.out.println(c);
	  
	  
	  String d=b.replace("1", "2");
	  System.out.println(d);
  }
}

