package database;


public class BHDbms{
	
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


}