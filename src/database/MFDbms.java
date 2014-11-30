package database;

/*
 * MFDbms is a Maximum Fanout Dbms that has a single
 * root and only one level of children, ie all tables 
 * that are not the root are children of the root.
 */

public class MFDbms extends Dbms {
	
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
