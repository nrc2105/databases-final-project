package main;

import java.util.HashMap;

public class Shell {
	
	public static final int MEAN_IO_TIME_MILLIS = 10; 
	public static final String 

	public static void main(String[] args) {
		if(args[0].equals("help") || args[0].equals("-h")){
			printHelp();
		} else{
			params = defaultParameterMap();
		}
		// TODO Auto-generated method stub

		
		// Configure database
		
		
		
		//Configure transaction manager
		
		
		
		// Run batch
		
		
		
		// Return results
		
		
		
	}
	
	public static HashMap<String,String> defaultParameterMap(){
		HashMap<String,String> defaultParams = new HashMap<String,String>();
		
		return defaultParams;
	}
	public static void printHelp(){
		System.out.println("Don't you wish this message was more useful?");
	}
	
	public static long getTime() {
		return System.currentTimeMillis();
	}

}
