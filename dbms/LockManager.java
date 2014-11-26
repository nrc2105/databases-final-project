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

	public void getAccess(int id, Plan plan, ArrayBlockingQueue<Table> requiredLocks, 
													ArrayBlockingQueue<Table> unnecessaryLocks){
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