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

	private int id;								// Transaction ID number
	private Dbms database;						// Reference to master DB
	private List<Table> requiredLocks;			// List of locks required by this transaction
	private ConcurrentHashMap<Table, Integer> availableLocks; // Set of locks available for writing
	private BlockingQueue<Table> finishedLocks;	// Locks are placed in here after writing
	private List<String> log;					// Log of this transaction's events
	private Random sleepTime;					// Used to randomize sleep timers for sim writes
		
	
	/**
	 *	Creates a new transaction.
	 *
	 *	@param id the int id.
	 *  @param size the number of data items accessed by this transaction
	 *	@param database the Dbms to run on.
	 */
	public Transaction(int id, int size, Dbms database){
		this.id = id;
		this.database = database;
		this.log = new ArrayList<String>();
		this.sleepTime = new Random();
		
		this.requiredLocks = new ArrayList<Table>(database.getTables(size));
		
		assert size > 0 : "ERROR: transaction size is " + size;

		int finishedSize = size * database.getPathLength();
		
		assert database.getPathLength() >= 1 : "ERROR: database.getPathLength "
				+ "returned a value less than zero: " + database.getPathLength();
		
		finishedLocks = new ArrayBlockingQueue<Table>(finishedSize);
		availableLocks = new ConcurrentHashMap<Table, Integer>();
	}
	

	/**
	 * Runs the query on the Dbms, first creating and starting the LockReleaser
	 * and LockSeekers, then simulating writes on each table as the lock becomes
	 * available.
	 */
	public final void run() {
		logEvent("began transaction");
		
		startLocking();
		
		logEvent("sent lock request");
		
		for (Table table : requiredLocks) {
			boolean hadToWait = false;
			
			// Begin waiting
			logEvent(String.format("requesting table %s", table.toString()));
			
			// Wait for each table in sequence
			while (!availableLocks.containsKey(table)) {
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
			
			// Debug check against race conditions
			assert availableLocks.contains(table) : "RACE CONDITION ERROR: "
					+ "Table was unlocked while writing to it.";
			
			/* Mark lock for releasing
			 * Note that this method will throw an exception if the Queue is full
			 * It is unacceptable for the Queue to be full, so this is the preferred
			 * behavior to threads blocking and corrupting run time data
			 * */
			finishedLocks.add(table);
			
		}
		
		logEvent("completed transaction");
	}
	
	
	private void simulateWrite() {
		long waitTime = (long) (sleepTime.nextGaussian() + Shell.MEAN_IO_TIME_MILLIS);
		try {
			Thread.sleep( waitTime < 1 ? waitTime : 1 );
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








