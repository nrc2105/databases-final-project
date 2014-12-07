package database;
/*
 *	Entities are the simulated version of lockable 
 *	structures in a real Dbms serving no real 
 *	purpose beyond managing their associated lock access.
 *
 *	Author: Nicholas Cummins
 *	email: ncummins@brandeis.edu
 *	10/25/14
 */

public class Entity{

	/**
	 * Create an Entity with no
	 * current owner
	 *
	 */

	public Entity(){
		currentOwner = -1;
	}
	
	/**
	 * Create an Entity with an int name.
	 * @param name an integer identifier.
	 */
	
	public Entity(int name){
		currentOwner = -1;
		this.name = name;
	}

	/**
	 * The method for acquiring a lock, if the lock
	 * is held by another transaction, the seeker is
	 * instructed to wait, otherwise the lock is granted.
	 * @param the int id.
	 * @return a boolean indicating success of acquisition.
	 */
	public synchronized boolean acquireLock(int id){
		if(currentOwner != -1 && currentOwner != id){
			try{
				wait();
			    }
			    catch(InterruptedException e){
			    }
		}
		
		if(currentOwner == -1 || currentOwner == id){
			currentOwner = id;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Releases the lock, only makes a change if it
	 * is called by the current owner of the lock.
	 * @param the int id of the transaction releasing the lock.
	 */
	public synchronized void releaseLock(int id){
		if(currentOwner == id){
			currentOwner = -1;
			notifyAll();
		}
	}
	
	@Override
	public String toString(){
		return "ENTITY " + name;
	}

	private int name;
	private int currentOwner;
}