package database;

/*
 * MFDbms is a Maximum Fanout Dbms that has a single
 * root and only one level of children, ie all entities 
 * that are not the root are children of the root.
 * 
 * Author: Nicholas Cummins
 */

public class MFDbms extends Dbms {
	
	/**
	 * Constructs a new MFDbms (a tree with a single internal
	 * node and otherwise only leaves).
	 * @param size
	 * @param weight
	 * @param dummyRoot
	 */
	public MFDbms(int size, String weight, boolean dummyRoot){
		super(size, weight, dummyRoot);
		super.initialize();
	}

	@Override
	protected int getParentIndex(int childIndex) {
		if(childIndex == 0){
			return -1;
		}else{
			return 0;
		}
	}

	@Override
	public int getPathLength() {
		return 2;
	}
	
	@Override
	public String toString(){
		return super.toString() + " STRUCTURE: MAXIMUM FANOUT";
	}

}
