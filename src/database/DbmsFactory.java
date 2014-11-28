package database;


/*
 * This factory returns instances of Dbms with desired properties.
 *
 */

 public class DbmsFactory{
	public static final String BH = "bh";
	public static final String EQWEIGHT = "equal";
	public static final String TOPWEIGHT = "top";
	public static final String BOTTOMWEIGHT = "bottom";
	
	public static Dbms getDbms(String struct, String weight, int size){
		if(struct.equalsIgnoreCase(BH) && weight.equalsIgnoreCase(EQWEIGHT)){
			return getEqualWeightBHDbms(size);
		} else if(struct.equalsIgnoreCase(BH) && weight.equalsIgnoreCase(TOPWEIGHT)){
			return getTopWeightBHDbms(size);
		} else if(struct.equalsIgnoreCase(BH) && weight.equalsIgnoreCase(BOTTOMWEIGHT)){
			return getBottomWeightBHDbms(size);
		} else {
			return null;
		}
	}
 	public static Dbms getEqualWeightBHDbms(int size){
 		return new EqualWeightBHDbms(size);
 	}
 	
 	public static Dbms getTopWeightBHDbms(int size){
 		return new TopWeightBHDbms(size);
 	}
 	
 	public static Dbms getBottomWeightBHDbms(int size){
 		return new BottomWeightBHDbms(size);
 	}
 }