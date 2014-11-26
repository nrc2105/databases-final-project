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

	public void getAccess(int id, List<Table> plan, ConcurrentHashMap<Table,Integer> requiredLocks, 
														BlockingQueue<Table> unnecessaryLocks){
		dependencies = db.getDependecies(plan);
		releaser = new LockReleaser(id, dependencies, unnecessaryLocks);
		new Thread(releaser).start();
		for(List<Table> list : dependencies){
			new Thread(new LockSeeker(id, new ArrayList<Table>(list),
						unnecessaryLocks, requiredLocks)).start();

		}

	}

	private Dbms db;
	private LockReleaser releaser;
	private List<List<Table>> dependencies;
	
}