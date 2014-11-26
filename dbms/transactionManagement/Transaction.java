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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.*;

import LockManager;
import Table;
import Dbms;
import Plan;


public abstract class Transaction implements Runnable{

	/**
	 *	Creates a new transaction.
	 *
	 *	@param the int id.
	 *	@param the Dbms to run on.
	 *	@param the plan of the transaction. //TODO: MIKE I NEED SOME HELP ON THIS PART
	 */

	public Transaction(int id, Dbms database, Plan plan){
		this.id = id;
		this.database = database;
		this.plan = plan;
		unnecessaryLocks = new ArrayBlockingQueue<Table>();
		requiredLocks = new ArrayBlockingQueue<Table>();
	}

	/**
	 *	Called once at the start of a run to initialize
	 *	helpers.
	 * MIKE DON"T TOUCH THIS (OR IF YOU DO, ONLY ADD)
	 */
	private void init(){
		(new LockManager(database)).getAccess(id, plan, requiredLocks, unnecessaryLocks);	
		
	}
	/**
	 *	Runs the query on the Dbms, first creating and
	 *	starting the LockReleaser and LockSeekers.
	 */

	public final void run() {
		this.init();
		while(true){
			if((instruction = plan.nextInstruction()) != null){
				execute(instruction);
			} else{
				return; //Done so exit
			}
		}
		

	}

	/**
	 *	Executes the given instruction
	 *	@param the instruction
	 */

	protected abstract void execute(Instruction instruction);

	private int id;
	private Plan plan;
	private Dbms database;
	private ArrayBlockingQueue<Table> unnecessaryLocks;
	private ArrayBlockingQueue<Table> requiredLocks;

}