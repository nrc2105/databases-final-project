package database;


/*
 * This factory returns instances of Dbms with desired properties.
 *
 */

 public class DbmsFactory{
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