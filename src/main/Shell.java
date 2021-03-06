package main;

import java.util.HashMap;

import transactionManagement.TransactionManager;
import database.Dbms;
import database.DbmsFactory;

/*
 * The Shell is the main interface class of the
 * Dbms tree-protocol locking simulator.  For
 * information on specific command-line arguments
 * run with argument "help"
 * 
 * Author: Nicholas Cummins
 */
public class Shell {
	
	public static int MEAN_IO_TIME_MILLIS = 100;
	public static final String IOTIME="iotime";
	public static final String DEFAULTIO = "100";
	public static final String DBSIZE = "dbsize";
	public static final String DEFAULTSIZE = "100";
	public static final String STRUCT = "struct";
	public static final String DEFAULTSTRUCT= DbmsFactory.DH;
	public static final String WEIGHT = "weight";
	public static final String DUMMYROOT = "dummyroot";
	public static final String DEFAULTROOT = "false";
	public static final String HEAPSIZE = "heapsize";
	public static final String DEFAULTHSIZE = "2";
	public static final String DEFAULTWEIGHT = Dbms.EQWEIGHT;
	public static final String BATCHSIZE = "batchsize";
	public static final String DEFAULTBSIZE = "100";
	public static final String XACTIONSIZE = "xactionsize";
	public static final String DEFAULTXSIZE = "10";
	public static final String XACTIONVARIETY = "xactionvariety";
	public static final String DEFAULTXVARIETY = "false";
	public static final String VERBOSE = "verbose";
	public static final String DEFAULTVERBOSE = "true";
	public static final String CONCURRENT = "concurrent";
	public static final String DEFAULTCONCURRENT = "true";
	public static final String FILENAME = "filename";
	public static final String DEFAULTFILE = "NOPE";

	public static void main(String[] args) {
		if(args.length != 0 && (args[0].equals("help") || args[0].equals("-h"))){
			printHelp();
		} else{
			
			System.out.println("INITIALIZING PARAMETER MAP");
			HashMap<String,String> params = getParameterMap(args);
			MEAN_IO_TIME_MILLIS = Integer.parseInt(params.get(IOTIME));
			String struct = params.get(STRUCT);
			String weight = params.get(WEIGHT);
			boolean dummyRoot = Boolean.parseBoolean(params.get(DUMMYROOT));
			int size = Integer.parseInt(params.get(DBSIZE));
			int heapSize = Integer.parseInt(params.get(HEAPSIZE));
			System.out.println("INITIALIZING DATABASE");
			Dbms database = DbmsFactory.getDbms(struct, weight, dummyRoot, size, heapSize);
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
			manager.runBatch(Boolean.parseBoolean(params.get(CONCURRENT)));
		
		
			// Return results (log stuff)
			System.out.println("RUNNING ANALYSIS");
			
			// Old analysis
//			LogReporter.analyze(manager.getFullResults());
			
			// Build LogReporter
			LogReporter reporter = new LogReporter(manager);
			reporter.printSummary();
			reporter.printAggregate();
//			reporter.printTableAcessFreq();
			if(!(params.get(FILENAME)).equals(DEFAULTFILE)){
				reporter.dumpToFile(params.get(FILENAME));
			}
		
		}
	}
	
	
	public static HashMap<String,String> getParameterMap(String[] args)
			throws RuntimeException{
		HashMap<String,String> params = new HashMap<String,String>();
		params.put(DBSIZE, DEFAULTSIZE);
		params.put(STRUCT, DEFAULTSTRUCT);
		params.put(WEIGHT, DEFAULTWEIGHT);
		params.put(DUMMYROOT, DEFAULTROOT);
		params.put(BATCHSIZE, DEFAULTBSIZE);
		params.put(XACTIONSIZE, DEFAULTXSIZE);
		params.put(XACTIONVARIETY, DEFAULTXVARIETY);
		params.put(VERBOSE, DEFAULTVERBOSE);
		params.put(HEAPSIZE, DEFAULTHSIZE);
		params.put(CONCURRENT, DEFAULTCONCURRENT);
		params.put(FILENAME, DEFAULTFILE);
		params.put(IOTIME,DEFAULTIO);
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
		System.out.println("Option format: <parameter>=<value>\n");
		System.out.println("Example: dbsize=100\n");
		System.out.println("Parameters:\tValues:\n");
		System.out.println("dbsize\t\tAny positive integer, default is 100");
		System.out.println("struct\t\tdh,mf,lb for D-Heap, Max Fanout, and Left-Branching.");
		System.out.println("weight\t\tequal,top,bottom");
		System.out.println("heapsize\tDegree of heap, any positive integer.");
		System.out.println("dummyroot\ttrue,false for whether or not the root should be blank placeholder");
		System.out.println("batchsize\tNumber of transactions to run.  Any positive integer, default is 100");
		System.out.println("xactionsize\tNumber of writes per transaction.  Any positive integer, default is 10");
		System.out.println("xactionvariety\ttrue,false, indicating whether or not to vary the size of transactions.");
		System.out.println("iotime\t\tthe average time (in milliseconds) of each write");
		System.out.println("concurrent\ttrue,false, indicating whether the transactions should be run concurrently or not.");
		System.out.println("filename\tthe name of a file if you wish to save the log dump.");
	}
	
	public static long getTime() {
		return System.currentTimeMillis();
	}

}
