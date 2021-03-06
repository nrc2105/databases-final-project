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
import database.Entity;


public class Transaction implements Runnable{

	private int id;								// Transaction ID number
	private int size;							// Number of operations
	private Dbms database;						// Reference to master DB
	private List<Entity> requiredLocks;			// List of locks required by this transaction
	private ConcurrentHashMap<Entity, Integer> availableLocks; // Set of locks available for writing
	private BlockingQueue<Entity> finishedLocks;	// Locks are placed in here after writing
	private List<String> log;					// Log of this transaction's events
	private Random sleepTime;					// Used to randomize sleep timers for sim writes
	private long sleepCounter;					// Running total of time spent sleeping
		
	
	/**
	 *	Creates a new transaction.
	 *
	 *	@param id the int id.
	 *  @param size the number of data items accessed by this transaction
	 *	@param database the Dbms to run on.
	 */
	public Transaction(int id, int size, Dbms database){
		this.id = id;
		this.size = size;
		this.database = database;
		this.log = new ArrayList<String>(4 + size * 4);
		this.sleepTime = new Random();
		
		this.requiredLocks = new ArrayList<Entity>(database.getEntities(size));
		
		assert size > 0 : "ERROR: transaction size is " + size;

		int finishedSize = size * database.getPathLength();
		
		assert database.getPathLength() >= 1 : "ERROR: database.getPathLength "
				+ "returned a value less than zero: " + database.getPathLength();
		
		finishedLocks = new ArrayBlockingQueue<Entity>(finishedSize);
		availableLocks = new ConcurrentHashMap<Entity, Integer>();
	}
	

	/**
	 * Runs the query on the Dbms, first creating and starting the LockReleaser
	 * and LockSeekers, then simulating writes on each table as the lock becomes
	 * available.
	 */
	public final void run() {
		logEvent("began transaction");
		
		startLocking();
		
		logEvent("sent all lock requests");
		
		for (Entity entity : requiredLocks) {
			boolean hadToWait = false;
			
			// Begin waiting
			logEvent(String.format("requesting entity %s", entity.toString()));
			
			// Wait for each table in sequence
			while (!availableLocks.containsKey(entity)) {
				// Live spin
				hadToWait = true;
			}
			
			// Entity obtained
			if (hadToWait) {
				logEvent(String.format("done waiting for entity %s", entity.toString()));
			} else {
				logEvent(String.format("did not wait for entity %s", entity.toString()));
			}
			logEvent(String.format("writing to entity %s", entity.toString()));
			
			// Simulate write operation
			simulateWrite();
			
			logEvent(String.format("done with entity %s", entity.toString()));
			
			// Debug check against race conditions
			assert availableLocks.containsKey(entity) : "RACE CONDITION ERROR: "
					+ "Entity was unlocked while writing to it.";
			
			/* Mark lock for releasing
			 * Note that this method will throw an exception if the Queue is full
			 * It is unacceptable for the Queue to be full, so this is the preferred
			 * behavior to threads blocking and corrupting run time data
			 * */
			finishedLocks.add(entity);
			
		}
		
		logEvent("completed transaction");
		logEvent("total read/write time: " + sleepCounter);
	}
	
	
	/**
	 * Simulates the write I/O time
	 * Causes thread to sleep for a normally distributed amount of time with mean
	 * at Shell.MEAN_IO_TIME_MILLIS and absolute minimum of 1ms.
	 */
	private void simulateWrite() {
		long waitTime = (long) (Shell.MEAN_IO_TIME_MILLIS * sleepTime.nextGaussian()
				+ Shell.MEAN_IO_TIME_MILLIS);
		waitTime = waitTime > 1 ? waitTime : 1;
		sleepCounter += waitTime;
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}

	/**
	 *	Called once at the start of a run to initialize
	 *	helpers.
	 */
	private void startLocking(){
		LockManager locker = new LockManager(database);
		
		locker.getAccess(id, requiredLocks, availableLocks, finishedLocks);	
		
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
		return String.format("XACTION,%02d,%d", id, size);
	}

	
}








