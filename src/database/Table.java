package database;
/*
 *	Tables are the simulated version of real Dbms
 *	tables, serving no real purpose beyond managing
 *	their associated lock access.
 *
 *	Author: Nicholas Cummins
 *	email: ncummins@brandeis.edu
 *	10/25/14
 */

public class Table{

	/**
	 * Create a table with no
	 * current owner
	 *
	 */

	public Table(){
		currentOwner = -1;
	}

	public synchronized boolean acquireLock(int id){
		if(currentOwner == -1 || currentOwner == id){
			currentOwner = id;
			return true;
		} else {
			return false;
		}
	}

	public synchronized void releaseLock(int id){
		if(currentOwner == id){
			currentOwner = -1;
		}
	}

	private int currentOwner;
}