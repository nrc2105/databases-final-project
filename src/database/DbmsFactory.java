package database;


/*
 * This factory returns instances of Dbms with desired properties.
 * 
 * Author: Nicholas Cummins
 *
 */

 public class DbmsFactory{
	public static final String BH = "bh";
	public static final String MF = "mf";
	public static final String DH = "dh";
	public static final String LB = "lb";
	
	/**
	 * Returns an instance of Dbms of the appropriate type
	 * as determined by the parameters.
	 * @param struct, a String representing the desired structure.
	 * @param weight, top, bottom, or equal weighting of table use.
	 * @param dummyRoot, boolean to be passed on to Dbms
	 * @param size, the size of the Dbms
	 * @param heapSize, if struct dh this determines order of heap
	 * @return the desired Dbms object
	 */
	public static Dbms getDbms(String struct, String weight, 
						boolean dummyRoot, int size, int heapSize){
		if(struct.equalsIgnoreCase(BH)){
			return new BHDbms(size, weight, dummyRoot);
		} else if (struct.equalsIgnoreCase(MF)){
			return new MFDbms(size, weight, dummyRoot);
		} else if(struct.equalsIgnoreCase(DH)){ 	
			return new DHDbms(size, weight, dummyRoot, heapSize);
		} else if(struct.equalsIgnoreCase(LB)){
			return new LBDbms(size, weight, dummyRoot);
		}else{
			return null;
		}
	}

 }