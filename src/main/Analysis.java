package main;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Analysis {

	public static void main(String[] args) {

		String directoryPath = "experiments/l_experiments/benchmark";
		List<LogReporter> list = getLogReporters(getDirectoryContents(directoryPath));
		System.out.printf("Average runtime is %s", 
				LogReporter.millisToHuman(getAverageRuntime(list)));
		
		
		
		
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
			if (logsRead++ % 10 == 0) {
				System.out.println("Read " + logsRead + " logs");
			}
		}
		
		return list;
	}
	
	public static long getAverageRuntime(List<LogReporter> list) {
		long total = 0;
		int logsRead = 0;
		for (LogReporter lr : list) {
			
			total+=lr.totalRunTime();
			if (logsRead++ % 10 == 0) {
				System.out.println("Processed " + logsRead + " logs");
			}
		}
		return total / list.size();
	}

}









