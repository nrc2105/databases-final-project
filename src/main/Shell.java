package main;

import java.util.HashMap;

import transactionManagement.TransactionManager;
import database.Dbms;
import database.DbmsFactory;

public class Shell {
	
	public static final int MEAN_IO_TIME_MILLIS = 10;
	public static final String DBSIZE = "dbsize";
	public static final String DEFAULTSIZE = "100";
	public static final String STRUCT = "struct";
	public static final String DEFAULTSTRUCT= DbmsFactory.BH;
	public static final String WEIGHT = "weight";
	public static final String DEFAULTWEIGHT = DbmsFactory.EQWEIGHT;
	public static final String BATCHSIZE = "batchsize";
	public static final String DEFAULTBSIZE = "100";
	public static final String XACTIONSIZE = "xactionsize";
	public static final String DEFAULTXSIZE = "10";
	public static final String XACTIONVARIETY = "xactionvariety";
	public static final String DEFAULTXVARIETY = "false";
	public static final String VERBOSE = "verbose";
	public static final String DEFAULTVERBOSE = "true";

	public static void main(String[] args) {
		if(args.length != 0 && (args[0].equals("help") || args[0].equals("-h"))){
			printHelp();
		} else{
			
			System.out.println("INITIALIZING PARAMETER MAP");
			HashMap<String,String> params = getParameterMap(args);
			String struct = params.get(STRUCT);
			String weight = params.get(WEIGHT);
			int size = Integer.parseInt(params.get(DBSIZE));
			System.out.println("INITIALIZING DATABASE");
			Dbms database = DbmsFactory.getDbms(struct, weight, size);
			if(database == null){
				throw new RuntimeException();
			}
			int numXactions = Integer.parseInt(params.get(BATCHSIZE));
			int writesPerXaction = Integer.parseInt(params.get(XACTIONSIZE));
			boolean xactionVariety = Boolean.parseBoolean(params.get(XACTIONVARIETY));
			//Configure transaction manager
			System.out.println("CREATING TRANSACTION MANAGER");
			TransactionManager manager = new TransactionManager(numXactions, writesPerXaction, 
															xactionVariety, database);
			System.out.println("CREATING TRANSACTION BATCH");
			manager.createBatch();
		
		
			// Run batch
			System.out.println("RUNNING TRANSACTION BATCH");
			manager.runBatch();
		
		
			// Return results (log stuff)
			System.out.println("RUNNING ANALYSIS");
			LogReporter.analyze(manager.getFullResults());
		
		}
	}
	
	
	public static HashMap<String,String> getParameterMap(String[] args)
			throws RuntimeException{
		HashMap<String,String> params = new HashMap<String,String>();
		params.put(DBSIZE, DEFAULTSIZE);
		params.put(STRUCT, DEFAULTSTRUCT);
		params.put(WEIGHT, DEFAULTWEIGHT);
		params.put(BATCHSIZE, DEFAULTBSIZE);
		params.put(XACTIONSIZE, DEFAULTXSIZE);
		params.put(XACTIONVARIETY, DEFAULTXVARIETY);
		params.put(VERBOSE, DEFAULTVERBOSE);
		for(String a : args){
			String[] parsed = a.split("=");
			if(parsed.length == 2){
				params.put(parsed[0].trim(), parsed[1].trim());
			} else{
				throw new RuntimeException();
			}
		}
		return params;
	}
	
	public static void printHelp(){
		System.out.println("Don't you wish this message was more useful?");
	}
	
	public static long getTime() {
		return System.currentTimeMillis();
	}

}
