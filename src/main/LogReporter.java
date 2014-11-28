package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
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
	
	private List<String> logs;
	private int xactionNum;
	private List<List<String>> xactionLogs;	// List of logs for each transaction
	
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
	
	
	public static void printHumanReadable(LogReporter reporter) {
		
		System.out.println(reporter.managerToHuman());
		System.out.println(reporter.dbmsToHuman());
		System.out.printf("Total run time: %s\n", millisToHuman(reporter.totalRunTime()));
		
		// Print transaction runtimes
		System.out.println("Transaction run times:");
		for (String s : reporter.getXactionRuntimes()) {
			System.out.println(s);
		}
		
		System.out.println("Table access frequencies: ");
		for (String s : mapToValueSortedList(reporter.getTableFreqMap())) {
			System.out.println(s);
		}
		
	}
	

	private static String millisToHuman(long time) {
		long minutes = time / (1000 * 60);
		long seconds = (time / 1000) % 60;
		long millis = time % 1000;
		return String.format("%d:%02d.%d", minutes, seconds, millis);
	}
	
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

	private LogReporter(List<String> logSet) {
		this.logs = new ArrayList<String>(logSet);
		Collections.sort(logs);
		
		this.xactionNum = getXactionNum();
		this.xactionLogs = getXactionLogs();
	}
	
	private List<List<String>> getXactionLogs() {
		xactionLogs = new ArrayList<List<String>>();
		for (int xactionIndex = 0; xactionIndex < xactionNum; xactionIndex++) {
			ArrayList<String> xLog = new ArrayList<String>();
			for (String logEntry : logs) {
				if (logEntry.contains("XACTION," + xactionIndex)) {
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
			runtimes.add(xLog.get(0).split("\t")[1] + ": " + millisToHuman(runtime));			
		}
		
		assert runtimes.size() == xactionNum : 
			"ERROR: Number of runtimes doesn't match number of transactions.";
		
		return runtimes;
	}

	private long getRuntime(String start, String end) {
		
		assert !(start== null || end == null) : 
			"ERROR: Failed to find start or end timestamps for the batch.";
		
		long startTime = Long.parseLong(start.split("\t")[0]);
		long endTime = Long.parseLong(end.split("\t")[0]);
		
		assert endTime > startTime : "ERROR: End time is before start time.";
		
		return endTime - startTime;
	}

	private int getXactionNum() {

		String num = getManagerRecords().get(0).split("\t")[0].split(",")[1]; 
		
		return Integer.parseInt(num);
	}
	
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
	
	private String getDatabaseRecord() {
		
		for (String record : logs) {
			if (record.contains("DBMS")) {
				return record;
			}
		}
		
		throw new RuntimeException("ERROR: No Database Record Found");
	}

	private String dbmsToHuman(String databaseRecord) {
		// TODO update this to make more human readable if desired
		return databaseRecord.split("\t")[1];
	}
	
	private String dbmsToHuman() {
		return dbmsToHuman(getDatabaseRecord());
	}
	
	private String managerToHuman(String managerRecord) {
		String[] stats = managerRecord.split("\t")[1].split(",");
		return String.format("Transaction Manager:\n%s transactions\n%s writes per transaction\n"
				+ "Transaction variety: %s", stats[0], stats[1], stats[2]);
	}
	
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
			list.add(key + "\t" + map.get(key));
		}
		
		Collections.sort(list);
		
		return list;
	}
	
	private static String getTableName(String logEntry) {		
		String tableNum = logEntry.split("TABLE")[1];
		
		
		return "TABLE" + tableNum;
	}

}







