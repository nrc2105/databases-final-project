package database;

/*
 * The BHDbms is a Dbms with a Binary Heap structure
 * lock system.  All locks must be acquired according to
 * the heap structure, starting at the root.
 * 
 * Author: Nicholas Cummins
 */

public class BHDbms extends Dbms{
	

	public BHDbms(int size){
		super(size);
	}
	
	public BHDbms(int size, String weight, boolean dummyRoot){
		super(size, weight, dummyRoot);
	}
	
	@Override
	public int getPathLength(){
		return (int)Math.ceil(Math.log((double)size)/Math.log(2));
	}

	/**
	 * Returns parent index of current index based on
	 * the implementation of the Dbms.
	 *
	 * @param the index of the child.
	 * @return the index of the parent.
	 */
	@Override
	protected int getParentIndex(int childIndex){
		if(childIndex == 0){
			return -1;
		}
		
		return (childIndex - 1) / 2;
			
	}
	
	@Override
	public String toString(){
		return super.toString() + " STRUCTURE: BINARY HEAP";
	}


}