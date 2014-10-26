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
		messageQueue = new ArrayBlockingQueue<Table>();
	}

	/**
	 *	Called once at the start of a run to initialize
	 *	helpers.
	 */
	private void init(){
		dependencies = database.getDependencies(plan);
		releaser = new LockReleaser(id, dependencies, unnecessaryLocks);
		releaser.start();
		for(List<Table> list : dependencies){
			new LockSeeker(id, new ArrayList(list),
						unnecessaryLocks, requiredLocks).start();
		}
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
	private LockReleaser releaser;
	private List<List<Table>> dependencies;

}