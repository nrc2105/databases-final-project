package main;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Analysis {

	public static void main(String[] args) {

		experiment7();
		
		
	}
	
public static void experiment1 () {
		
		String directoryPath = "experiments/m_experiments/experiment1/experiment1long";
		List<LogReporter> list = getLogReporters(getDirectoryContents(directoryPath));
		list.get(0).printSummary();
		System.out.printf("Average runtime is %s\n", 
				LogReporter.millisToHuman(getAverageRuntime(list)));
		System.out.printf("Average sleep time is %s\n", 
				LogReporter.millisToHuman(getAverageSleeptime(list)));
		
		String directoryPath1 = "experiments/m_experiments/experiment1/experiment1short";
		List<LogReporter> list1 = getLogReporters(getDirectoryContents(directoryPath1));
		list1.get(0).printSummary();
		System.out.printf("Control Average runtime is %s\n", 
				LogReporter.millisToHuman(getAverageRuntime(list1)));
		System.out.printf("Average sleep time is %s\n", 
				LogReporter.millisToHuman(getAverageSleeptime(list1)));

	}
	
	public static void experiment2 () {
		
		String directoryPath = "experiments/m_experiments/experiment2";
		List<LogReporter> list = getLogReporters(getDirectoryContents(directoryPath));
		list.get(0).printSummary();
		System.out.printf("Average runtime is %s\n", 
				LogReporter.millisToHuman(getAverageRuntime(list)));
		
		
		String directoryPath1 = "experiments/m_experiments/experiment2_control";
		List<LogReporter> list1 = getLogReporters(getDirectoryContents(directoryPath1));
		list1.get(0).printSummary();
		System.out.printf("Control Average runtime is %s\n", 
				LogReporter.millisToHuman(getAverageRuntime(list1)));

	}
	
	public static void experiment3 () {
		
		String directoryPath = "experiments/n_experiments/experiment3.xactionvariety_true";
		List<LogReporter> list = getLogReporters(getDirectoryContents(directoryPath));
		list.get(0).printSummary();
		System.out.printf("Average runtime is %s\n", 
				LogReporter.millisToHuman(getAverageRuntime(list)));
		
		
		String directoryPath1 = "experiments/n_experiments/benchmark";
		List<LogReporter> list1 = getLogReporters(getDirectoryContents(directoryPath1));
		list1.get(0).printSummary();
		System.out.printf("Control Average runtime is %s\n", 
				LogReporter.millisToHuman(getAverageRuntime(list1)));

	}
	
	public static void experiment4 () {
		
		String directoryPath = "experiments/n_experiments/experiment6.mf.bottom.dummyroot_false";
		List<LogReporter> list = getLogReporters(getDirectoryContents(directoryPath));
		list.get(0).printSummary();
		System.out.printf("Bottom weight Average runtime is %s\n", 
				LogReporter.millisToHuman(getAverageRuntime(list)));
		
		
		String directoryPath1 = "experiments/n_experiments/experiment6.mf.equal.dummyroot_false";
		List<LogReporter> list1 = getLogReporters(getDirectoryContents(directoryPath1));
		list1.get(0).printSummary();
		System.out.printf("Equal Weight Average runtime is %s\n", 
				LogReporter.millisToHuman(getAverageRuntime(list1)));
		
		String directoryPath2 = "experiments/n_experiments/experiment6.mf.top.dummyroot_false";
		List<LogReporter> list2 = getLogReporters(getDirectoryContents(directoryPath2));
		list1.get(0).printSummary();
		System.out.printf("Top Weight Average runtime is %s\n", 
				LogReporter.millisToHuman(getAverageRuntime(list2)));

	}
	
	public static void experiment5 () {
		
		String directoryPath = "experiments/n_experiments/experiment5.dh.equal.dummyroot_true";
		List<LogReporter> list = getLogReporters(getDirectoryContents(directoryPath));
		list.get(0).printSummary();
		System.out.printf("Dummy Root Average runtime is %s\n", 
				LogReporter.millisToHuman(getAverageRuntime(list)));
		
		
		String directoryPath1 = "experiments/n_experiments/benchmark";
		List<LogReporter> list1 = getLogReporters(getDirectoryContents(directoryPath1));
		list1.get(0).printSummary();
		System.out.printf("Control Average runtime is %s\n", 
				LogReporter.millisToHuman(getAverageRuntime(list1)));

	}
	
	public static void experiment7 () {
		System.out.println("Experiment 7 results");
		
		Path writePath = Paths.get("Results", "experiment7.csv");
		try (BufferedWriter writer = Files.newBufferedWriter(writePath, Charset.defaultCharset())) {
			for (int dbsize = 20; dbsize <= 1000; dbsize += 20) {
				String directoryPath = "experiments/m_experiments/dbsizevariety/" + dbsize;
				List<LogReporter> list = getLogReporters(getDirectoryContents(directoryPath));
				list.get(0).printSummary();
				long runtime = getAverageRuntime(list);
				System.out.printf("Size " + dbsize + " Average runtime is %s\n", 
						LogReporter.millisToHuman(runtime));
				writer.write(String.format("%d,%d\n", dbsize, runtime));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
	
	public static void experiment9 () {
		
		String directoryPath = "experiments/n_experiments/experiment9.dh";
		List<LogReporter> list = getLogReporters(getDirectoryContents(directoryPath));
		list.get(0).printSummary();
		System.out.printf("DH Average runtime is %s\n", 
				LogReporter.millisToHuman(getAverageRuntime(list)));
		
		
		String directoryPath1 = "experiments/n_experiments/experiment9.lb";
		List<LogReporter> list1 = getLogReporters(getDirectoryContents(directoryPath1));
		list1.get(0).printSummary();
		System.out.printf("LB Average runtime is %s\n", 
				LogReporter.millisToHuman(getAverageRuntime(list1)));
		
		String directoryPath2 = "experiments/n_experiments/experiment9.mf";
		List<LogReporter> list2 = getLogReporters(getDirectoryContents(directoryPath2));
		list1.get(0).printSummary();
		System.out.printf("MF Average runtime is %s\n", 
				LogReporter.millisToHuman(getAverageRuntime(list2)));

	}
	
	
	/**
	 * Gets contents of a directory
	 * Intended to get all logs from an experiment
	 * 
	 * @param directoryPath
	 * @return DirectoryStream for chosen directory
	 */
	public static DirectoryStream<Path> getDirectoryContents(String directoryPath) {
		Path dirPath = Paths.get(directoryPath);
		DirectoryStream<Path> dir = null;
		try {
			dir = Files.newDirectoryStream(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dir;
	}
	
	/**
	 * Gets a list of LogReporter for a run of experiments
	 * 
	 * @param dir DirectoryStream<Path> for directory containing experiment logs
	 * @return List<LogReporter> of LogReporters for the experiment logs
	 */
	public static List<LogReporter> getLogReporters(DirectoryStream<Path> dir) {
		
		ArrayList<LogReporter> list = new ArrayList<LogReporter>();
		System.out.println("Reading logs...");
		int logsRead = 0;
		for (Path entry : dir) {
			list.add(LogReporter.readFromFile(entry));
			if (logsRead++ % 25 == 0) {
				System.out.println("Read " + logsRead + " logs");
			}
		}
		
		return list;
	}
	
	
	/**
	 * Gets average runtime for every LogReporter in the directory
	 * 
	 * @param list
	 * @return
	 */
	public static long getAverageRuntime(List<LogReporter> list) {
		long total = 0;
		int logsRead = 0;
		for (LogReporter lr : list) {
			
			total+=lr.totalRunTime();
			if (logsRead++ % 25 == 0) {
				System.out.println("Processed " + logsRead + " logs");
			}
		}
		return total / list.size();
	}

	
	/**
	 * Gets average sleep time for every LogReporter in the directory
	 * 
	 * @param list
	 * @return
	 */
	public static long getAverageSleeptime(List<LogReporter> list) {
		long total = 0;
		int logsRead = 0;
		for (LogReporter lr : list) {
			
			total+=LogReporter.getAvgRuntime(lr.getXactionSleepTimes());
			if (logsRead++ % 25 == 0) {
				System.out.println("Processed " + logsRead + " logs");
			}
		}
		return total / list.size();
	}
}









