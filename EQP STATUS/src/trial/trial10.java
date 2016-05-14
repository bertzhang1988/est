package trial;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class trial10 {
 int i =2;
	@BeforeTest
	@Parameters({"browser"})
	public void SetUp(String browser){
		if(browser.equalsIgnoreCase("a")){
			i=4;
		}else if(browser.equalsIgnoreCase("b")){
			i=5;
		}
	}
  
  @Test
  public void f(){
	  System.out.println(i);
  }
}
