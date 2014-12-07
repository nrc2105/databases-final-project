package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import transactionManagement.TransactionManager;

/**
 * Handles parsing and analyzing log data from each run. Designed to be run as part of
 * a transaction batch or stand alone from saved log data
 * 
 * @author Michael
 *
 */
public class LogReporter {
	
	private List<String> logs;				// List of all logs
	private int numXactions;					// Number of transactions in this batch
	private List<List<String>> xactionLogs;	// List of logs for each transaction
	
	
	/**
	 * Used to analyze log data from memory. Normally called by Shell at the completion of a run.
	 * 
	 * NOTE: THIS METHOD IS NOW DEPRECATED. USE PUBLIC CONSTRUCTOR TO CREATE LogReporter
	 * AND USE PUBLIC INSTANCE METHODS TO OBTAIN DESIRED INFORMATION
	 * 
	 * @param logs List<String> of log data items from a batch run.
	 */
	@Deprecated
	public static void analyze(List<String> logs) {
		LogReporter reporter = new LogReporter(logs);
		
		reporter.printSummary();
		reporter.printAggregate();
	}	
	
	/**
	 * Takes a log from TransactionManager following a run and writes it to 
	 * a file for later analysis
	 * 
	 * @param logSet Set of logs from a run. Full log set preferred.
	 * @param filename Name for output file. Overwrites previous entry.
	 */
	public void dumpToFile(String filename) {
		try (BufferedWriter writer = Files.newBufferedWriter(
				Paths.get(filename), StandardCharsets.UTF_8)) {
			
			for (String s : logs) {
				writer.write(s + "\n");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Prints summary information for a run
	 */
	public void printSummary() {
		System.out.println(managerToHuman());
		System.out.println();
		System.out.println(dbmsToHuman());
		System.out.println();
		System.out.printf("Total run time: %s\n", millisToHuman(totalRunTime()));
		System.out.println();
	}

	/**
	 * Prints various human-friendly aggregate information
	 */
	public void printAggregate() {
		
		System.out.printf("Mean transaction runtime: %s\n", 
				millisToHuman(getAvgRuntime(getXactionRuntimes())));
		System.out.printf("Standard deviation: %s\n\n", 
				millisToHuman(getStdDevRuntime(getXactionRuntimes())));
		
		System.out.printf("Mean time spent waiting on locks (per xaction): %s\n", 
				millisToHuman(getAvgRuntime(getXactionWaitingTimes())));
		System.out.printf("Standard deviation: %s\n\n", 
				millisToHuman(getStdDevRuntime(getXactionWaitingTimes())));
		
		System.out.printf("Mean time spent writing to entities (per xaction): %s\n", 
				millisToHuman(getAvgRuntime(getXactionSleepTimes())));
		System.out.printf("Standard deviation: %s\n\n", 
				millisToHuman(getStdDevRuntime(getXactionSleepTimes())));
		
		System.out.printf("Utilization: %2.2f%%\n\n", getUtilization() * 100);
		System.out.printf("Wasted time: %s\n", millisToHuman(Math.round(getWastedTime())));
		
	}
	
	/**
	 * Prints time spent on I/O for each transaction
	 */
	public void printXactionIOTimes() {
		System.out.println("Transaction waiting times:");
		for (String s : getXactionSleepTimes()) {
			System.out.println(s);
		}
	}
	
	/**
	 * Prints time spent waiting on locks for each transaction
	 */
	public void printXactionWaitingTimes() {
		System.out.println("Time spent by transactions waiting on locks:");
		for (String s : getHumanRuntimes(getXactionWaitingTimes())) {
			System.out.println(s);
		}
	}
	
	/**
	 * Prints total run time for each transaction
	 */
	public void printXactionRuntimes() {
		System.out.println("Transaction run times:");
		for (String s : getHumanRuntimes(getXactionRuntimes())) {
			System.out.println(s);
		}
	}
	
	/**
	 * Prints number of accesses to each Entity
	 */
	public void printTableAcessFreq() {
		System.out.println("Entity access frequencies: ");
		for (String s : mapToValueSortedList(getTableFreqMap())) {
			System.out.println(s);
		}
	}
	
	/**
	 * Reads a log file and returns a log reporter object
	 * 
	 * @param inputData Path object to log file
	 * @return LogReporter built with read log data
	 */
	public static LogReporter readFromFile(Path inputData) {
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
	public LogReporter(List<String> logSet) {
		this.logs = new ArrayList<String>(logSet);
		Collections.sort(logs);
		
		this.numXactions = getXactionNum();
		this.xactionLogs = getXactionLogs();
	}
	
	/**
	 * Constructor turns a TransactionManager into a LogReporter object by reading its logs.
	 * 
	 * @param logSet List<String> of log entries from file or a completed run.
	 */
	public LogReporter(TransactionManager manager) {
		this.logs = new ArrayList<String>(manager.getFullResults());
		Collections.sort(logs);
		
		this.numXactions = getXactionNum();
		this.xactionLogs = getXactionLogs();
	}
	
	/**
	 * Writes the entire log contents to console for debugging
	 */
	public void dumpLogToConsole() {
		for (String s : logs) {
			System.out.println(s);
		}
	}
	
	private double getWastedTime() {
		return totalRunTime() * (1 - getUtilization());
	}
	
	
	/**
	 * Calculates percentage of total run time of the batch spent on activities other than
	 * overhead. This time is spent either writing or waiting for locks.
	 * 
	 * @return utilization fraction
	 */
	private double getUtilization() {
		return (double)getAvgRuntime(getUsefulTime()) / getAvgRuntime(getXactionRuntimes());
	}
	
	/**
	 * Gets the amount of time spent writing or waiting for locks by each transaction.
	 * The rest of the time is assumed to be overhead.
	 * 
	 * @return useful time in milliseconds per transaction
	 */
	private List<String> getUsefulTime() {
		List<String> sleepTimes = getXactionSleepTimes();
		List<String> waitTimes = getXactionWaitingTimes();
		List<String> usefulTimes = new ArrayList<String>();
		for (int xNum = 0; xNum < numXactions; xNum++) {
			long sleepTime = Long.parseLong(sleepTimes.get(xNum).split("\t")[1]);
			String[] waitParsed = waitTimes.get(xNum).split("\t");
			long waitTime = Long.parseLong(waitParsed[1]);
			usefulTimes.add(waitParsed[0] + "\t" + (waitTime + sleepTime));
		}
		
		return usefulTimes;
	}
	
	/**
	 * Used as part of the constructor to separate out the transaction log records
	 * and divide them into a list for each transaction
	 * 
	 * @return List of List<String> of transaction logs
	 */
	private List<List<String>> getXactionLogs() {
		xactionLogs = new ArrayList<List<String>>();
		for (int xactionIndex = 0; xactionIndex < numXactions; xactionIndex++) {
			ArrayList<String> xLog = new ArrayList<String>();
			for (String logEntry : logs) {
				if (logEntry.contains("XACTION," + String.format("%02d", xactionIndex))) {
					xLog.add(logEntry);
				}
			}
			assert !xLog.isEmpty() : "ERROR: Found no logs for transaction number " + xactionIndex;
			xactionLogs.add(xLog);
		}
		
		assert xactionLogs.size() == numXactions;
		
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
		
		assert runtimes.size() == numXactions : 
			"ERROR: Number of runtimes doesn't match number of transactions.";
		
		return runtimes;
	}
	
	
	/**
	 * Gets Transaction sleep times and returns them as a list of Strings,
	 * with one entry for each pair of transaction and sleep time.
	 * Sleep time is the time spent simulating I/O events.
	 * 
	 * @return List of strings: Transaction: total sleep time
	 */
	List<String> getXactionSleepTimes() {
		List<String> sleepTimes = new ArrayList<String>();
		for (List<String> xLog : xactionLogs) {
			String[] entrys = xLog.get(xLog.size() - 1).split("\t");
			Long sleepTime = Long.parseLong(entrys[2].split(": ")[1]);
			sleepTimes.add(entrys[1] + "\t" + sleepTime);			
		}
		
		assert sleepTimes.size() == numXactions : 
			"ERROR: Number of sleep times doesn't match number of transactions.";
		
		return sleepTimes;
	}
	
	
	/**
	 * Determines total time spent by each transaction waiting on locks.
	 * Returns that in the class-standard List<String> for further processing by
	 * other class functions.
	 * 
	 * @return Total time spent waiting, per transaction
	 */
	private List<String> getXactionWaitingTimes() {
		List<String> waitTimes = new ArrayList<String>();
		for (List<String> xLog : xactionLogs) {
			long waitTime = 0;
					
			Iterator<String> it = xLog.iterator();
			String startWaiting = null;
			String stopWaiting = null;
			while (it.hasNext()) {
				String entry = it.next();
				if (entry.contains("requesting entity")) {
					startWaiting = entry;
				} else if (entry.contains("done waiting for entity")) {
					stopWaiting = entry;
				}
				
				if (startWaiting != null &&
						stopWaiting != null &&
						getEntityName(startWaiting).equals(getEntityName(stopWaiting))) {
					waitTime += getRuntime(startWaiting, stopWaiting);
					startWaiting = null;
					stopWaiting = null;
				}

			}
			waitTimes.add(xLog.get(0).split("\t")[1] + "\t" + waitTime);
		}
		
		assert waitTimes.size() == numXactions : 
			"ERROR: Number of sleep times doesn't match number of transactions.";
		
		return waitTimes;
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
	long totalRunTime() {
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
	 * Gets a map of entity access frequencies from master log data
	 * 
	 * @return Map of Entity name (string) to table access frequency
	 */
	private Map<String, Integer> getTableFreqMap() {
		Map<String, Integer> tableFreq = new HashMap<String, Integer>();
		
		for (String logEntry : logs) {
			if (logEntry.contains("writing to entity")) {
				String entity = getEntityName(logEntry);
				if (tableFreq.containsKey(entity)) {
					tableFreq.put(entity, tableFreq.get(entity) + 1);
				} else {
					tableFreq.put(entity, 1);
				}
			}
		}
		
		return tableFreq;
	}
	
	/**
	 * Utility to turn a map of Strings and Integers (as in the Entity Frequency Map)
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
	 * Utility to turn a map of Strings and Integers (as in the Entity Frequency Map)
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
	 * Takes a logEntry concerning a Entity in the database and extracts the name of that table
	 * 
	 * @param logEntry Any log entry concerning a table
	 * @return Entity name
	 */
	private static String getEntityName(String logEntry) {		
		String tableNum = logEntry.split("ENTITY")[1];
		
		
		return "ENTITY" + tableNum;
	}
	
	/**
	 * Takes in two log entries and returns the number of milliseconds between them.
	 * Normally used for calculating the run time of a transaction of batch of transactions.
	 * 
	 * @param start Log entry for start of event
	 * @param end Log entry to end of event
	 * @return Time elapsed during event
	 */
	private static long getRuntime(String start, String end) {
		
		assert !(start== null || end == null) : 
			"ERROR: Failed to find start or end timestamps for the batch.";
		
		long startTime = Long.parseLong(start.split("\t")[0]);
		long endTime = Long.parseLong(end.split("\t")[0]);
		
		assert endTime > startTime : "ERROR: End time is before start time.";
		
		return endTime - startTime;
	}
	
	/**
	 * Converts long millisecond time period into human readable minutes:seconds.fraction
	 * format
	 * 
	 * @param time
	 * @return
	 */
	static String millisToHuman(long time) {
		long minutes = time / (1000 * 60);
		long seconds = (time / 1000) % 60;
		long millis = time % 1000;
		return String.format("%d:%02d.%03d", minutes, seconds, millis);
	}
	
	/**
	 * Takes output of a function that generates a list of strings and long millisecond counts
	 * and converts times from millisecond counts to human readable mm:ss.s format
	 * 
	 * @return List of run times in human-readable format
	 */
	private static List<String> getHumanRuntimes(List<String> list) {
		List<String> humanRuntimes = new ArrayList<String>();
		for (String entry : list) {
			String[] parsed = entry.split("\t");
			humanRuntimes.add(parsed[0] + ": " + millisToHuman(Long.parseLong(parsed[1])));
		}
		return humanRuntimes;		
	}
	
	/**
	 * Calculates and returns (in milliseconds) average time from a standard time list
	 * 
	 * @param List of runtimes in class-standard format
	 * @return average runtime
	 */
	static long getAvgRuntime(List<String> runTimeList) {
		long total = 0;
		for (String entry : runTimeList) {
			total += Long.parseLong(entry.split("\t")[1]);
		}
		
		return total / runTimeList.size();
	}
	
	/**
	 * Calculates and returns (in milliseconds) standard devation of runtimes
	 * from a standard time list
	 * 
	 * @param list List of runtimes in class-standard format
	 * @return Standard deviation of runtimes in milliseconds
	 */
	private static long getStdDevRuntime(List<String> list) {
		long mean = getAvgRuntime(list);
		long total = 0;
		for (String entry : list) {
			long value = Long.parseLong(entry.split("\t")[1]);
			total += (value - mean)*(value - mean);
		}
		
		return Math.round(Math.sqrt(total / list.size()));
	}

}







