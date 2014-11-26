package main;

import java.util.HashMap;

import transactionManagement.TransactionManager;
import database.Dbms;

public class Shell {
	
	public static final int MEAN_IO_TIME_MILLIS = 10;  

	public static void main(String[] args) {
		if(args[0].equals("help") || args[0].equals("-h")){
			printHelp();
		} else{
			params = defaultParameterMap();
		}

		
		// Configure database
		// TODO Nick completes this
		
		
		//Configure transaction manager
		TransactionManager manager = new TransactionManager(numXactions, writesPerXaction, 
				homogeneous, database);
		manager.createBatch();
		
		
		// Run batch
		manager.runBatch();
		
		
		// Return results (log stuff)
		LogReporter.analyze(manager.getFullResults());
		
		
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
