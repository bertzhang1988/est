package trial;

import org.testng.annotations.Test;

public class tiral8 {
	int i;
	static int m=3;
	
   
  @Test
  public void f() {
	  
	  System.out.println(i);
	  i=7;
	  i=9;
	  m=2;
	  tiral8 s=new tiral8();
	  tiral8 w=new tiral8();
	  s.i=1;
	  System.out.println(i);
	  System.out.println(s.i);
	  System.out.println(w.i);
	  System.out.println(m);
	  System.out.println(s.m);
	  System.out.println(w.m);
	  s.m=90;
	  System.out.println(w.m);
  }
}
