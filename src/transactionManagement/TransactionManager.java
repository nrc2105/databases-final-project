package transactionManagement;

import java.util.ArrayList;
import java.util.List;

import main.Shell;
import database.Dbms;

public class TransactionManager {	
	
	private int numXactions;
	private int writesPerXaction;
	private boolean xactionVaritey;
	private Transaction[] transactionSet;
	private Dbms database;
	private List<String> masterLog;
	
	
	/**
	 * Constructs Transaction Manager according to parameters.
	 * Does not create individual Transactions at this time.
	 * 
	 * @param numXactions Total number of Transactions in this batch
	 * @param writesPerXaction Average number of writes per Transaction
	 * @param homogeneous Specify whether transactions have variable (!homogeneous) number
	 * of writes or identical (homogeneous) number of writes
	 * @param database Underlying database on which these transactions are run
	 */
	public TransactionManager(int numXactions, int writesPerXaction, boolean xactionVaritey, 
			Dbms database) {
		
		this.numXactions = numXactions;
		assert this.numXactions > 0 : "ERROR: numXactions is " + this.numXactions;
		this.writesPerXaction = writesPerXaction;
		assert this.writesPerXaction > 0 : "ERROR: writesPerXaction is " + this.writesPerXaction;
		this.xactionVaritey = xactionVaritey;
		this.database = database;
		transactionSet = new Transaction[numXactions];
		masterLog = new ArrayList<String>();
		
		// Log basic information about this run
		logEvent("created transaction manager");
		masterLog.add(String.format("%d\t%s\tis database type", 
				Shell.getTime(), database.toString()));
	}
	

	/**
	 * Creates a batch of transactions based on parameters passed in to the constructor.
	 * Logs creation into master log.
	 * Does not start any timed actions.
	 */
	public void createBatch() {
		
		logEvent("creating batch");
		
		if (!xactionVaritey) {
			for (int tIndex = 0; tIndex < numXactions; tIndex++) {
				transactionSet[tIndex] = 
						TransactionFactory.getTransaction(tIndex, writesPerXaction, database);
			}
		} else {
			for (int tIndex = 0; tIndex < numXactions; tIndex++) {
				transactionSet[tIndex] = TransactionFactory.getTransaction(
						tIndex, (int)(Math.random() * writesPerXaction), database);
			}
		}
		
		logEvent("batch created");
	}
	
	/**
	 * Wraps all transactions in Thread, starts all transactions, and blocks on their
	 * completion. Catches any interrupted exceptions and prints stack trace. 
	 */
	public void runBatch() {
		ArrayList<Thread> threadList = new ArrayList<Thread>();
		
		// Wrap transactions in Threads
		for (Transaction x : transactionSet) {
			Thread t = new Thread(x);
			threadList.add(t);
		}
		
		logEvent("beginning transaction batch");
		
		// Start transactions
		for (Thread t : threadList) {
			t.start();
		}
		
		// Wait for completion
		for (Thread t : threadList) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		logEvent("completed transaction batch");
	}
	
	
	@Override
	public String toString() {
		return "MANAGER," + numXactions + "," + writesPerXaction + "," + xactionVaritey;
	}
	
	
	/**
	 * Logs an event with standard time stamp and naming format
	 * @param event String description of event to be logged
	 */
	private void logEvent(String event) {
		masterLog.add(String.format("%d\t%s\t%s", Shell.getTime(), this.toString(), event));
	}
	
	/**
	 * Queries all (completed) transactions, collects their logs (unsorted) and
	 * reports log data. This method is itempotent in that the masterLog of the manager
	 * is not affected by this operation and so the returned results will not be adversely
	 * affected by repeated calls to this method.
	 * 
	 * @return List<String> of log data from all transactions
	 */
	public List<String> getFullResults() {
		ArrayList<String> outputLog = new ArrayList<String>(masterLog);

		for (Transaction t : transactionSet) {
			outputLog.addAll(t.getLog());
		}
		return outputLog;
	}
	
	
	/**
	 * Returns the master log of the Transaction Manager without first collecting
	 * logs from individual transactions. This is intended for summary data.
	 * 
	 * @return masterLog of the Transaction Manager
	 */
	public List<String> getLog() {
		return new ArrayList<String>(masterLog);
	}

}








