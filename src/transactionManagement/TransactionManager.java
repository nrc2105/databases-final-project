package transactionManagement;

import java.util.ArrayList;
import java.util.List;

import database.Dbms;

public class TransactionManager {	
	
	private int numXactions;
	private int writesPerXaction;
	private boolean homogeneous;
	private Transaction[] transactionSet;
	private Dbms database;
	private List<String> masterLog;
	
	
	
	public TransactionManager(int numXactions, int writesPerXaction, boolean homogeneous, 
			Dbms database) {
		
		this.numXactions = numXactions;
		this.writesPerXaction = writesPerXaction;
		this.homogeneous = homogeneous;
		this.database = database;
		transactionSet = new Transaction[numXactions];
		masterLog = new ArrayList<String>();
		
		
	}
	

	/**
	 * Creates a batch of transactions based on parameters passed in to the constructor.
	 * Logs creation into master log.
	 * Does not start any timed actions.
	 */
	public void createBatch() {
		if (homogeneous) {
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
		
	}
	
	
	
	/**
	 * Queries all (completed) transactions, collects their logs (unsorted) and
	 * reports log data.
	 * @return List<String> of log data from all transactions
	 */
	public List<String> getResults() {

		for (Transaction t : transactionSet) {
			masterLog.addAll(t.getLog());
		}
		
		return masterLog;
	}

}








