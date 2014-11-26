package database;


/*
 * This factory returns instances of Dbms with desired properties.
 *
 */

 public class DbmsFactory{
 	public static Dbms getBHDbms(int size){
 		return new BHDbms(size);
 	}
 }