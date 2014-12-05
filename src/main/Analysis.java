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

		String directoryPath = "experiments/";
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
		for (Path entry : dir) {
			list.add(LogReporter.readFromFile(entry));			
		}
		
		return list;
	}
	
	public static long getAverageRuntime(List<LogReporter> list) {
		long total = 0;
		for (LogReporter lr : list) {
			
			total+=lr.totalRunTime();			
		}
		return total / list.size();
	}

}









