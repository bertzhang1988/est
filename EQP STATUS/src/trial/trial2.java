package trial;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.Test;

public class trial2 {
	@Test
	public void wee(){
	String cubenum="000";
	    while(cubenum.startsWith("0")){
	    	cubenum=cubenum.substring(1);
	    }
	    
	    System.out.println(cubenum);
	    
	    
	  // Set<String> e= new HashSet<String>();
	    ArrayList<String> e= new ArrayList<String>();
	    e.add("RAIL TRAILER");
	    e.add("VDCK");
	    e.add("INTL CONTAINER");
	    System.out.println(e.toString());
	    System.out.println(e.toString().replaceAll("[\\[\\] ]",""));
	    System.out.println( e.toString().replaceAll("[\\[\\]]","").replaceAll(", ", "," ));
}}
