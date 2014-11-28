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
		
		
		System.out.printf("Total run time: %s", millisToHuman(reporter.totalRunTime()));
		
		
		
			
	}
	
	private static String millisToHuman(long time) {
		long minutes = time / (1000 * 60);
		long seconds = (time / 1000) % 60;
		long millis = time % 1000;
		return String.format("%d:%2d.%d", minutes, seconds, millis);
	}

	private LogReporter(List<String> logSet) {
		this.logs = new ArrayList<String>(logSet);
		Collections.sort(logs);
		
		this.xactionNum = getXactionNum();
		this.xactionLogs = getXactionLogs();
	}
	
	private List<List<String>> getXactionLogs() {
		// TODO Auto-generated method stub
		return null;
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
		return null;
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
	
	private long totalRunTime() {
		
		return 0;
	}

}







