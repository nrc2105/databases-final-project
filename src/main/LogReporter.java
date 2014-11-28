package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		
		System.out.println(reporter.managerToHuman());
		System.out.println(reporter.dbmsToHuman());
		System.out.printf("Total run time: %s\n", millisToHuman(reporter.totalRunTime()));
		
		
		
			
	}
	
	private static String millisToHuman(long time) {
		long minutes = time / (1000 * 60);
		long seconds = (time / 1000) % 60;
		long millis = time % 1000;
		return String.format("%d:%02d.%d", minutes, seconds, millis);
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
	
	private List<String> getXactionRuntimes() {
		List<String> runtimes = new ArrayList<String>();
		for (List<String> xLog : xactionLogs) {
			runtimes.add(xLog.get(0).split("\t")[1] + ": " + millisToHuman(getRuntime(xLog)));			
		}
		
		assert runtimes.size() == xactionNum : 
			"ERROR: Number of runtimes doesn't match number of transactions.";
		
		return runtimes;
	}

	private long getRuntime(List<String> xLog) {
		
		return 0;
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
		String startTime = "0";
		String endTime = "0";
		for (String record : getManagerRecords()) {
			if (record.contains("beginning transaction batch")) {
				startTime = record.split("\t")[0];
			} else if (record.contains("completed transaction batch")) {
				endTime = record.split("\t")[0];
			}
		}
		
		assert !(startTime.equals("0") || endTime.equals("0")) : 
			"ERROR: Failed to find start or end timestamps for the batch.";
		
		long start = Long.parseLong(startTime);
		long end = Long.parseLong(endTime);
		
		assert end - start > 0 : "ERROR: Batch started after it ended.";
		
		return end - start;
	}

}







