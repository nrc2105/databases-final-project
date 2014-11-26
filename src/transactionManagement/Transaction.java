package transactionManagement;
/**
 *
 *	A Transaction is a series of actions to be
 *	carried out on a Dbms.  To do so it launches
 *	helper functions to acquire and release
 *	the necessary locks.
 *
 *	Author: Nicholas Cummins
 *	email: ncummins@brandeis.edu
 *	10/25/14
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import lockManagement.LockManager;
import database.Dbms;
import database.Table;


public class Transaction implements Runnable{

	private int id;
	private Dbms database;
	private List<Table> requiredLocks;
	private Map<Table, Integer> availableLocks;
	private BlockingQueue<Table> finishedLocks;
		
	
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
		
		this.requiredLocks = new ArrayList<Table>(size);
		requiredLocks.addAll(getRandomTables(size));
		
		finishedLocks = new ArrayBlockingQueue<Table>(size);
		availableLocks = new ConcurrentHashMap<Table, Integer>();
	}
	
	
	private Collection<Table> getRandomTables(int count) {
		Table[] dbTables = database.getTables();
		ArrayList<Table> xactionTables = new ArrayList<Table>(count);
		while (xactionTables.size() < count) {
			int tID = (int) (Math.random() * dbTables.length);
			
			
			
		}
		
		
		
	}
	
	
	/**
	 *	Runs the query on the Dbms, first creating and
	 *	starting the LockReleaser and LockSeekers.
	 */
	public final void run() {
		startLocking();
		
		// TODO: build transaction internal behavior
		

	}

	/**
	 *	Called once at the start of a run to initialize
	 *	helpers.
	 * MIKE DON"T TOUCH THIS (OR IF YOU DO, ONLY ADD)
	 */
	private void startLocking(){
		(new LockManager(database)).getAccess(id, requiredLocks, unnecessaryLocks, finishedLocks);	
		
	}
	

	
}