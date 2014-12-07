package lockManagement;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import database.Entity;

/*
 * LockSeekers simply take a list of locked tables
 * and acquire the locks in the given order.  Placing
 * the final lock in the appropriate shared queue.
 */

public class LockSeeker implements Runnable{

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

	public LockSeeker(int id, List<Entity> locks, BlockingQueue<Entity> obsoleteLocks,
											ConcurrentHashMap<Entity,Integer> requiredLocks){
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
	public final void run(){
		for(Entity lock : locks){
			while (!lock.acquireLock(id)){
				//Livespin
			}
			if(lastLock != null){
				try{
					obsoleteLocks.put(lastLock);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
			lastLock = lock;
		}
		requiredLocks.put(lastLock, 1);
	}

	private int id;
	private List<Entity> locks;
	private BlockingQueue<Entity> obsoleteLocks;
	private ConcurrentHashMap<Entity,Integer> requiredLocks;
	private Entity lastLock;
}