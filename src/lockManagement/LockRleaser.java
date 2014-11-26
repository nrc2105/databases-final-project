package lockManagement;
import database.Table;

/*
 * LockReleaser will be spawned by a Transaction and
 * is in charge of releasing unnecessary locks.
 *
 * Author: Nicholas Cummins
 * email: ncummins@brandeis.edu
 * 10/26/14
 */

public class LockReleaser{

	/**
	 *	Creates a new LockReleaser
	 *
	 *	@param the int id of the parent transaction
	 *	@param the List<List<Table>> of dependencies
	 *	@param the BlockingQueue used to communicate
	 *
	 */

	public LockReleaser(int id, List<List<Table>> dependencies, 
									BlockingQueue<Table> unnecessaryLocks){
		this.id = id;
		this.dependencies = dependencies;
		this.unnecessaryLocks = unnecessaryLocks;
		this.initMap();
	}

	/**
	 * Initializes the dependency map so the releaser
	 * can keep track of what locks are ready to be 
	 * released
	 *
	 */

	private void initMap(){
		dependecyMap = new HashMap<Table>();
		for(List<Table> list : dependencies){
			for(Table table : list){
				if((value = dependecyMap.get(table)) == null){
					dependecyMap.put(table, 1);
				} else {
					dependecyMap.put(table, value + 1);
				}
			}
		}

	}

	/**
	 *	Monitors the unnecessaryLocks Queue and
	 * 	decrements the count of any locks placed
	 *	there.  When a lock is no longer needed
	 *	at all it will be released. 
	 *
	 */

	public final void run(){
		while(true){
			Table lock = unnecessaryLocks.take();
			if((value = dependecyMap.get(lock)) == null){
				//Throw an exception
			} else if (value <= 1){
				dependecyMap.remove(lock);
				lock.releaseLock(id);
				if(dependecyMap.isEmpty()){
					return; //No more dependencies, finished.
				}
			} else {
				dependecyMap.put(lock, value - 1);
			}
		}
	}


	private int id;
	private BlockingQueue<Table> unnecessaryLocks;
	private HashMap<Table, Integer> dependecyMap;
	private List<List<Table>> dependencies;
}