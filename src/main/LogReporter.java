package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles parsing and analyzing log data from each run. Designed to be run as part of
 * a transaction batch or stand alone from saved log data
 * 
 * @author Michael
 *
 */
public class LogReporter {
	
	private List<String> logs;				// List of all logs
	private int xactionNum;					// Number of transactions in this batch
	private List<List<String>> xactionLogs;	// List of logs for each transaction
	
	
	/**
	 * Used to analyze log data from memory. Normally called by Shell at the completion of a run.
	 * 
	 * @param logs List<String> of log data items from a batch run.
	 */
	public static void analyze(List<String> logs) {
		LogReporter reporter = new LogReporter(logs);
		
		printHumanReadable(reporter);
	}
	
	/**
	 * Used to analyze saved log data from a log file
	 * 
	 * @param fileName Name of log file
	 */
	public static void analyze(String fileName) {
		LogReporter reporter = readFromFile(FileSystems.getDefault().getPath(fileName));
		
		printHumanReadable(reporter);
	}
	
	
	/**
	 * Takes a log from TransactionManager following a run and writes it to 
	 * a file for later analysis
	 * 
	 * @param logSet Set of logs from a run. Full log set preferred.
	 * @param filename Name for output file. Overwrites previous entry.
	 */
	public static void dumpToFile(List<String> logSet, String filename) {
		try (BufferedWriter writer = Files.newBufferedWriter(
				Paths.get(filename), StandardCharsets.UTF_8)) {
			
			for (String s : logSet) {
				writer.write(s + "\n");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Takes a LogReporter object and prints various human-friendly information
	 * 
	 * @param reporter LogReporter object
	 */
	private static void printHumanReadable(LogReporter reporter) {
		
		System.out.println(reporter.managerToHuman());
		System.out.println();
		System.out.println(reporter.dbmsToHuman());
		System.out.println();
		System.out.printf("Total run time: %s\n", millisToHuman(reporter.totalRunTime()));
		System.out.println();
		
		System.out.printf("Average transaction runtime: %s", 
				millisToHuman(reporter.getAvgXactionRuntime()));
		
//		// Print transaction runtimes
//		System.out.println("Transaction run times:");
//		for (String s : reporter.getHumanXactionRuntimes()) {
//			System.out.println(s);
//		}
//		System.out.println();
//		
//		// Print transaction sleep times
//		System.out.println("Transaction waiting times:");
//		for (String s : reporter.getXactionSleeptimes()) {
//			System.out.println(s);
//		}
//		System.out.println();
//		
//		System.out.println("Table access frequencies: ");
//		for (String s : mapToValueSortedList(reporter.getTableFreqMap())) {
//			System.out.println(s);
//		}
		
	}
	
	/**
	 * Reads a log file and returns a log reporter object
	 * 
	 * @param inputData Path object to log file
	 * @return LogReporter built with read log data
	 */
	private static LogReporter readFromFile(Path inputData) {
		List<String> logs = new ArrayList<String>();
		try (BufferedReader reader = Files.newBufferedReader(inputData, StandardCharsets.UTF_8)) {
			
			while (reader.ready()) {
				logs.add(reader.readLine());
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new LogReporter(logs);
	}

	
	/**
	 * Constructor turns a List of log entries into a LogReporter object.
	 * 
	 * @param logSet List<String> of log entries from file or a completed run.
	 */
	private LogReporter(List<String> logSet) {
		this.logs = new ArrayList<String>(logSet);
		Collections.sort(logs);
		
		this.xactionNum = getXactionNum();
		this.xactionLogs = getXactionLogs();
	}
	
	/**
	 * Used as part of the constructor to separate out the transaction log records
	 * and divide them into a list for each transaction
	 * 
	 * @return List of List<String> of transaction logs
	 */
	private List<List<String>> getXactionLogs() {
		xactionLogs = new ArrayList<List<String>>();
		for (int xactionIndex = 0; xactionIndex < xactionNum; xactionIndex++) {
			ArrayList<String> xLog = new ArrayList<String>();
			for (String logEntry : logs) {
				if (logEntry.contains("XACTION," + String.format("%02d", xactionIndex))) {
					xLog.add(logEntry);
				}
			}
			assert !xLog.isEmpty() : "ERROR: Found no logs for transaction number " + xactionIndex;
			xactionLogs.add(xLog);
		}
		
		assert xactionLogs.size() == xactionNum;
		
		return xactionLogs;
	}
	
	/**
	 * Gets Transaction run times and returns them as a list of Strings,
	 * with one entry for each pair of transaction and run time.
	 * 
	 * @return List of strings: Transaction: run time
	 */
	private List<String> getXactionRuntimes() {
		List<String> runtimes = new ArrayList<String>();
		for (List<String> xLog : xactionLogs) {
			String startEntry = xLog.get(0);
			String endEntry = xLog.get(xLog.size() - 1);
			long runtime = getRuntime(startEntry, endEntry);
			runtimes.add(xLog.get(0).split("\t")[1] + "\t" + runtime);			
		}
		
		assert runtimes.size() == xactionNum : 
			"ERROR: Number of runtimes doesn't match number of transactions.";
		
		return runtimes;
	}
	
	/**
	 * Takes output of getXactionRuntimes() and converts times from millisecond counts
	 * to human readable mm:ss.s format
	 * 
	 * @return List of runtimes in human-readable format
	 */
	private List<String> getHumanXactionRuntimes() {
		List<String> humanRuntimes = new ArrayList<String>();
		for (String entry : getXactionRuntimes()) {
			String[] parsed = entry.split("\t");
			humanRuntimes.add(parsed[0] + ": " + millisToHuman(Long.parseLong(parsed[1])));
		}
		return humanRuntimes;		
	}
	
	
	/**
	 * Calculates and returns (in milliseconds) average transaction runtime
	 * 
	 * @return average runtime
	 */
	private long getAvgXactionRuntime() {
		long total = 0;
		for (String entry : getXactionRuntimes()) {
			total += Long.parseLong(entry.split("\t")[1]);
		}
		
		return total / xactionNum;
	}
	
	
	/**
	 * Gets Transaction sleep times and returns them as a list of Strings,
	 * with one entry for each pair of transaction and sleep time.
	 * 
	 * @return List of strings: Transaction: total waiting time
	 */
	private List<String> getXactionSleeptimes() {
		List<String> sleepTimes = new ArrayList<String>();
		for (List<String> xLog : xactionLogs) {
			String[] entrys = xLog.get(xLog.size() - 1).split("\t");
			Long sleepTime = Long.parseLong(entrys[2].split(": ")[1]);
			sleepTimes.add(entrys[1] + ": " + millisToHuman(sleepTime));			
		}
		
		assert sleepTimes.size() == xactionNum : 
			"ERROR: Number of sleep times doesn't match number of transactions.";
		
		return sleepTimes;
	}

	/**
	 * Takes in two log entries and returns the number of milliseconds between them.
	 * Normally used for calculating the run time of a transaction of batch of transactions.
	 * 
	 * @param start Log entry for start of event
	 * @param end Log entry to end of event
	 * @return Time elapsed during event
	 */
	private long getRuntime(String start, String end) {
		
		assert !(start== null || end == null) : 
			"ERROR: Failed to find start or end timestamps for the batch.";
		
		long startTime = Long.parseLong(start.split("\t")[0]);
		long endTime = Long.parseLong(end.split("\t")[0]);
		
		assert endTime > startTime : "ERROR: End time is before start time.";
		
		return endTime - startTime;
	}

	/**
	 * Queries the master log records and extracts the number of transactions in the batch.
	 * 
	 * @return Number of transactions in the batch.
	 */
	private int getXactionNum() {

		String num = getManagerRecords().get(0).split("\t")[1].split(",")[1]; 
		
		return Integer.parseInt(num);
	}
	
	
	/**
	 * Queries the master log records and extracts entries dealing with the transaction manager.
	 * 
	 * @return List of transaction manager records
	 */
	private List<String> getManagerRecords() {
		List<String> managerRecords = new ArrayList<String>();
		
		for (String record : logs) {
			if (record.contains("MANAGER")) {
				managerRecords.add(record);
			}
		}
		
		assert managerRecords.size() >= 5; 
		
		return managerRecords;
	}
	
	/**
	 * Queries the master log records and extracts the entry dealing with database configuration.
	 * 
	 * @return String of the database configuration.
	 */
	private String getDatabaseRecord() {
		
		for (String record : logs) {
			if (record.contains("DBMS")) {
				return record;
			}
		}
		
		throw new RuntimeException("ERROR: No Database Record Found");
	}

	/**
	 * Takes a string database record and extracts human-readable information from it.
	 * 
	 * @param databaseRecord Log entry concerning database 
	 * @return Human-readable version of database configuration
	 */
	private String dbmsToHuman(String databaseRecord) {
		// TODO update this to make more human readable if desired
		return databaseRecord.split("\t")[1];
	}
	
	/**
	 * From LogReporter object, extracts database record and returns it as human-readable
	 * 
	 * @return Human-readable version of database configuration
	 */
	private String dbmsToHuman() {
		return dbmsToHuman(getDatabaseRecord());
	}
	
	/**
	 * Takes any log entry concerning transaction manager and returns the information
	 * about the whole batch in human-readable format.
	 * 
	 * @param managerRecord Any record that includes transaction manager information
	 * @return Human-readable version of transaction manager configuration
	 */
	private String managerToHuman(String managerRecord) {
		String[] stats = managerRecord.split("\t")[1].split(",");
		return String.format("Transaction count:\t%s\n"
				+ "Writes per transaction:\t%s\n"
				+ "Transaction variety:\t%s", 
				stats[1], stats[2], stats[3]);
	}
	
	/**
	 * From LogReporter object, extracts transaction manager record
	 * and returns the information about the whole batch in human-readable format.
	 * 
	 * @return Human-readable version of transaction manager configuration
	 */
	private String managerToHuman() {
		String managerRecord = getManagerRecords().get(0);
		return managerToHuman(managerRecord);
	}
	
	/**
	 * Gets time stamp for "beginning transaction batch"
	 * and time stamp for "completed transaction batch"
	 * and returns the difference
	 * @return runtime (in milliseconds) of transaction batch
	 */
	private long totalRunTime() {
		String startTime = null;
		String endTime = null;
		for (String record : getManagerRecords()) {
			if (record.contains("beginning transaction batch")) {
				startTime = record;
			} else if (record.contains("completed transaction batch")) {
				endTime = record;
			}
		}
		
		return getRuntime(startTime, endTime);
	}
	
	/**
	 * Gets a map of table access frequencies from master log data
	 * 
	 * @return Map of Table name (string) to table access frequency
	 */
	private Map<String, Integer> getTableFreqMap() {
		Map<String, Integer> tableFreq = new HashMap<String, Integer>();
		
		for (String logEntry : logs) {
			if (logEntry.contains("writing to table")) {
				String table = getTableName(logEntry);
				if (tableFreq.containsKey(table)) {
					tableFreq.put(table, tableFreq.get(table) + 1);
				} else {
					tableFreq.put(table, 1);
				}
			}
		}
		
		return tableFreq;
	}
	
	/**
	 * Utility to turn a map of Strings and Integers (as in the Table Frequency Map)
	 * and turn it into a list of Strings, each containing the table name followed by
	 * access frequency, sorted by table access frequencies.
	 * 
	 * @param map Map of Strings to Integers
	 * @return List of Strings, sorted on original Map integers
	 */
	private static List<String> mapToValueSortedList(Map<String, Integer> map) {
		List<String> list;
		
		// Gets a key-sorted list, inverts and sorts it, then inverts it again
		list = mapToKeySortedList(map);
		invertTabSeparatedList(list);
		Collections.sort(list);
		invertTabSeparatedList(list);
		
		
		return list;
	}
	
	/**
	 * Takes any list of strings with tabs separating logical keys from values and inverts
	 * the key and value for each line
	 * 
	 * @param list of (KeyString) \t (ValueString)
	 */
	private static void invertTabSeparatedList(List<String> list) {
		for (int index = 0; index < list.size(); index++) {
			String[] entries = list.get(index).split("\t"); 
			list.set(index, entries[1] + "\t" + entries[0]);
		}
	}

	/**
	 * Utility to turn a map of Strings and Integers (as in the Table Frequency Map)
	 * and turn it into a list of Strings, each containing the table name followed by
	 * access frequency, sorted by table names.
	 * 
	 * @param map Map of Strings to Integers
	 * @return List of Strings, sorted on original Map strings
	 */
	private static List<String> mapToKeySortedList(Map<String, Integer> map) {
		List<String> list = new ArrayList<String>();
		for (String key : map.keySet()) {
			list.add(String.format("%-10s\t%d", key, map.get(key)));
		}
		
		Collections.sort(list);
		
		return list;
	}
	
	/**
	 * Takes a logEntry concerning a Table in the database and extracts the name of that table
	 * 
	 * @param logEntry Any log entry concerning a table
	 * @return Table name
	 */
	private static String getTableName(String logEntry) {		
		String tableNum = logEntry.split("TABLE")[1];
		
		
		return "TABLE" + tableNum;
	}
	
	/**
	 * Converts long millisecond time period into human readable minutes:seconds.fraction
	 * format
	 * 
	 * @param time
	 * @return
	 */
	private static String millisToHuman(long time) {
		long minutes = time / (1000 * 60);
		long seconds = (time / 1000) % 60;
		long millis = time % 1000;
		return String.format("%d:%02d.%03d", minutes, seconds, millis);
	}

}







