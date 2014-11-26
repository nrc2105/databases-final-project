package lockManagement;
import database.Table;

/*
 * LockSeekers simply take a list of locked tables
 * and acquire the locks in the given order.  Placing
 * the final lock in the appropriate shared queue.
 */

public abstract class LockSeeker{

	/**
	 *
	 * Constructs a new LockSeeker
	 *	
	 * @param the id of the parent Transaction
	 * @param the list of locks to acquire
	 * @param the shared queue for obsolete locks
	 * @param the shared queue for the desired lock
	 *
	 */

	public LockSeeker(int id, List locks, BlockingQueue<Table> obsoleteLocks,
											ConcurrentHashMap<Table,Integer> requiredLocks){
		this.id = id;
		this.locks = locks;
		this.obsoleteLocks = obsoleteLocks;
		this.requiredLocks = requiredLocks;
		lastLock = null;
	}


	/**
	 * Seeks out and acquires all locks
	 * adding unnecessary locks to obsoleteLocks
	 * and the final lock to requiredLocks
	 */
	private final void run(){
		for(Table lock : locks){
			while (!lock.acquireLock(id)){
				//Livespin
			}
			if(lastLock != null){
				obsoleteLocks.put(lastLock);
			}
			lastLock = lock;
		}

		requiredLocks.put(lastLock, 1);
	}

	private int id;
	private List locks;
	private BlockingQueue<Table> obsoleteLocks;
	private ConcurrentHashMap<Table,Integer> requiredLocks;
	private Table lastLock;
}