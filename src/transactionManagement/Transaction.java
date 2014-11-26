package transactionManagement;
/**
 *
 *	A Transaction is a series of actions to be
 *	carried out on a Dbms.  To do so it launches
 *	helper functions to acquire and release
 *	the necessary locks.
 *
 *	Author: Michael Partridge
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import lockManagement.LockManager;
import main.Shell;
import database.Dbms;
import database.Table;


public class Transaction implements Runnable{

	private int id;
	private Dbms database;
	private List<Table> requiredLocks;
	private ConcurrentHashMap<Table, Integer> availableLocks;
	private BlockingQueue<Table> finishedLocks;
	private List<String> log;
	private Random sleepTime;
		
	
	/**
	 *	Creates a new transaction.
	 *
	 *	@param the int id.
	 *	@param the Dbms to run on.
	 *	@param the plan of the transaction. //TODO: MIKE I NEED SOME HELP ON THIS PART
	 */
	public Transaction(int id, int size, Dbms database){
		this.id = id;
		this.database = database;
		this.log = new ArrayList<String>();
		this.sleepTime = new Random();
		
		this.requiredLocks = new ArrayList<Table>(size);
		requiredLocks.addAll(getRandomTables(size));
		
		finishedLocks = new ArrayBlockingQueue<Table>(size);
		availableLocks = new ConcurrentHashMap<Table, Integer>();
	}
	
	
	/**
	 *	Runs the query on the Dbms, first creating and
	 *	starting the LockReleaser and LockSeekers.
	 */
	public final void run() {
		startLocking();
		
		for (Table table : requiredLocks) {
			boolean hadToWait = false;
			
			// Begin waiting
			logEvent(String.format("requesting table %s", table.toString()));
			
			// Wait for each table in sequence
			while (!availableLocks.contains(table)) {
				// Live spin
				hadToWait = true;
			}
			
			// Table obtained
			if (hadToWait) {
				logEvent(String.format("done waiting for table %s", table.toString()));
			} else {
				logEvent(String.format("did not wait for table %s", table.toString()));
			}
			logEvent(String.format("writing to table %s", table.toString()));
			
			// Simulate write operation
			simulateWrite();
			
			logEvent(String.format("done with table %s", table.toString()));
			
		}
		

	}
	
	
	private void simulateWrite() {
		long waitTime = (long) (sleepTime.nextGaussian() + Shell.MEAN_IO_TIME_MILLIS);
		try {
			this.wait( waitTime < 1 ? waitTime : 1 );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}

	/**
	 *	Called once at the start of a run to initialize
	 *	helpers.
	 */
	private void startLocking(){
		(new LockManager(database)).getAccess(id, requiredLocks, availableLocks, finishedLocks);	
		
	}
	
	/**
	 * Fetches a copy of this Transaction's log data
	 * @return ArrayList containing log data
	 */
	public List<String> getLog() {
		return new ArrayList<String>(log);
	}
	
	
	/**
	 * Logs an event into this Transaction's log with standard time stamp and formatting
	 * @param event
	 */
	private void logEvent(String event) {
		log.add(String.format("%d\t%s\t%s", Shell.getTime(), this.toString(), event));
	}
	
	
	@Override
	public String toString() {
		return String.format("XACTION,%d", id);
	}

	
}








