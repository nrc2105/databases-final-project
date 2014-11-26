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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.sun.org.apache.bcel.internal.generic.Instruction;


public class Transaction implements Runnable{

	private int id;
	private Dbms database;
	private Set<Table> requiredLocks;
	private Map<Table, Integer> availableLocks;
	private BlockingQueue<Table> finishedLocks;
		
	
	/**
	 *	Creates a new transaction.
	 *
	 *	@param the int id.
	 *	@param the Dbms to run on.
	 *	@param the plan of the transaction. //TODO: MIKE I NEED SOME HELP ON THIS PART
	 */

	public Transaction(int id, Dbms database){
		this.id = id;
		this.database = database;
		this.plan = plan;
		unnecessaryLocks = new ArrayBlockingQueue<Table>();
		requiredLocks = new ArrayBlockingQueue<Table>();
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