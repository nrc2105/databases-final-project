package database;


/*
 * This factory returns instances of Dbms with desired properties.
 *
 */

 public class DbmsFactory{
	public static final String BH = "bh";
	
	public static Dbms getDbms(String struct, String weight, boolean dummyRoot, int size){
		if(struct.equalsIgnoreCase(BH)){
			return new BHDbms(size, weight, dummyRoot);
		} else {
			return null;
		}
	}

 }