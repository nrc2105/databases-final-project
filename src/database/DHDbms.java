package database;

/*
 * The DHDbms is a Dbms with a D Heap structure
 * lock system.  All locks must be acquired according to
 * the heap structure, starting at the root.
 * 
 * Author: Nicholas Cummins
 */

public class DHDbms extends Dbms{
	

	public DHDbms(int size){
		super(size);
		this.d = 2;
		initialize();
	}
	
	public DHDbms(int size, String weight, boolean dummyRoot, int d){
		super(size, weight, dummyRoot);
		this.d = d;
		initialize();
	}

	@Override
	public int getPathLength(){
		return (int)Math.ceil(Math.log((double)size)/Math.log(d));
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
		return (childIndex - 1) / d;
			
	}
	
	@Override
	public String toString(){
		return super.toString() + " STRUCTURE: "+d+" HEAP";
	}

	private int d;

}