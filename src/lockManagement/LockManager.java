package lockManagement;

import database.Dbms;
import database.Table;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/*
 * The LockManager controls how table locks can be accessed
 * and starts all necessary lock seekers and releasers at 
 * the request of a transaction.
 * 
 * Author: Nicholas Cummins
 */

public class LockManager{

	/**
	 * Initializes a lock manager with a DBMS
	 *
	 * @param the DBMS
	 */

	public LockManager(Dbms db){
		this.db = db;

	}

	/**
	 * When called launches all necessary LockSeekers and 
	 * LockReleasers to support a transaction, identified
	 * by id.
	 * @param id the integer id of the transaction.
	 * @param plan the required table locks.
	 * @param requiredLocks the place where necessary locks are
	 * placed once acquired.
	 * @param unnecessaryLocks after locks are no longer needed
	 * they are put in this List to be released by the releaser.
	 */
	public void getAccess(int id, List<Table> plan, ConcurrentHashMap<Table,Integer> requiredLocks, 
														BlockingQueue<Table> unnecessaryLocks){
		dependencies = db.getDependencies(plan);
		releaser = new LockReleaser(id, dependencies, unnecessaryLocks);
		new Thread(releaser).start();
		for(List<Table> list : dependencies){
			(new Thread(new LockSeeker(id, new ArrayList<Table>(list),
						unnecessaryLocks, requiredLocks))).start();

		}

	}

	private Dbms db;
	private LockReleaser releaser;
	private List<List<Table>> dependencies;
	
}