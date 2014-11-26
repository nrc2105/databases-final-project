package database;


public abstract class BHDbms extends Dbms{
	

	public BHDbms(int size){
		super(size);
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
		return (childIndex - 1) / 2;
	}
	
	@Override
	public String toString(){
		return super.toString() + " STRUCTURE: BINARY HEAP";
	}


}