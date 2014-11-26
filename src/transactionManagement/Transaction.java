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
			
			logEvent(String.format("accessing table %s", table.toString()));
			
			while (!availableLocks.contains(table)) {
				// Live spin
			}
			
			
		}
		

	}

	/**
	 *	Called once at the start of a run to initialize
	 *	helpers.
	 * MIKE DON"T TOUCH THIS (OR IF YOU DO, ONLY ADD)
	 */
	private void startLocking(){
		(new LockManager(database)).getAccess(id, requiredLocks, availableLocks, finishedLocks);	
		
	}
	
	
	public List<String> getLog() {
		return log;
	}
	
	
	private void logEvent(String event) {
		log.add(String.format("%d\t%s\t%s", Shell.getTime(), this.toString(), event));
	}
	
	
	@Override
	public String toString() {
		return String.format("XACTION, %d", id);
	}
	
	
	private Collection<Table> getRandomTables(int count) {
		Table[] dbTables = database.getTables();
		ArrayList<Table> xactionTables = new ArrayList<Table>(count);
		
		// Add a random set of tables, avoiding duplicates
		while (xactionTables.size() < count) {
			int tID = (int) (Math.random() * dbTables.length);
			
			if (!xactionTables.contains(dbTables[tID])) {
				xactionTables.add(dbTables[tID]);
			}
		}
		
		return xactionTables;
	}

	
}








