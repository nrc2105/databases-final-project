package lockManagement;

import database.Dbms;
import database.Table;

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

	public void getAccess(int id, List<Table> plan, ConcurrentHashMap<Table> requiredLocks, 
													BlockingQueue<Table> unnecessaryLocks){
		dependencies = database.getDependencies(plan);
		releaser = new LockReleaser(id, dependencies, unnecessaryLocks);
		releaser.start();
		for(List<Table> list : dependencies){
			new LockSeeker(id, new ArrayList(list),
						unnecessaryLocks, requiredLocks).start();



	}

	private LockReleaser releaser;
	private List<List<Table>> dependencies;
	
}