package database;


/*
 * This factory returns instances of Dbms with desired properties.
 *
 */

 public class DbmsFactory{
	public static final String BH = "bh";
	public static final String MF = "mf";
	public static final String DH = "dh";
	public static final String LB = "lb";
	
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