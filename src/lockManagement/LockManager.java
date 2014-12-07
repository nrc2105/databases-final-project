package lockManagement;

import database.Dbms;
import database.Entity;

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
	public void getAccess(int id, List<Entity> plan, ConcurrentHashMap<Entity,Integer> requiredLocks, 
														BlockingQueue<Entity> unnecessaryLocks){
		dependencies = db.getDependencies(plan);
		releaser = new LockReleaser(id, dependencies, unnecessaryLocks);
		new Thread(releaser).start();
		for(List<Entity> list : dependencies){
			(new Thread(new LockSeeker(id, new ArrayList<Entity>(list),
						unnecessaryLocks, requiredLocks))).start();

		}

	}

	private Dbms db;
	private LockReleaser releaser;
	private List<List<Entity>> dependencies;
	
}