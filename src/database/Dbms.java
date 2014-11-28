package database;

import java.util.*;
/**
 *	The Dbms is the control structure of the simulator.
 *	It holds the Tables, which are essentially lockable
 *	and unlockable objects with no other purpose, and starts the 
 *	Transactions, which will have generated queries and associated 
 *	lock requirements.  
 *
 *	Author: Nicholas Cummins
 *	email: ncummins@brandeis.edu
 *	10/25/14
 */

public abstract class Dbms{

	/**
	 * Constructs a new DBMS with a specified
	 * number of tables.
	 *
	 */

	public Dbms(int size){
		this.size = size;
		rand = new Random();
		this.constructTables();
		this.constructDependencies();
	}

	/**
	 *	Creates enough tables to fill the list
	 *	to the appropriate size of the Dbms.
	 */

	private void constructTables(){
		tables =  new Table[size];

		for(int i = 0; i < size; i++){
			tables[i] = new Table(i);
		}
	}

	/**
	 *	Constructs partial ordering of the tables.
	 *
	 */

	private void constructDependencies(){
		paths = new HashMap<Table, List<Table>>();
		for(int i = 0; i < size; i++){
			paths.put(tables[i], getPath(i));
		}
	}

	/**
	 * Constructs path to a table from the root.
	 *
	 * @param the starting integer
	 */
	private List<Table> getPath(int start){
		int currentIndex = start;
		Table currentTable;
		ArrayList<Table> path = new ArrayList<Table>();
		while(currentIndex >= 0){
			currentTable = tables[currentIndex];
			path.add(0,currentTable);
			currentIndex = getParentIndex(currentIndex);
		}

		return path;

	}


	/**
	 * 	Gets the list of tables in the Dbms.
	 *
	 *	@return the List of Tables.
	 *
	 */

	public Table[] getTables(){
		return tables;
	}

	/**
	 * Returns a subset of the tables
	 * @param count the requested number of tables
	 * @return the subset of tables
	 */
	public List<Table> getTables(int count){
		HashSet<Integer> tableSet = new HashSet<Integer>();
		while(tableSet.size() < count){
			tableSet.add(getNextRandom());
		}
		ArrayList<Table> tableSubset = new ArrayList<Table>();
		for (Integer i : tableSet){
			tableSubset.add(this.tables[i]);
		}
		return tableSubset;
	}
		
	/**
	 *	Returns the locks necessary to get to a
	 * given table.
	 *
	 *	@param the Table sought.
	 *	@return the List (in order) of Tables needed.
	 *
	 */

	public List<Table> getDependencies(Table goal){
		return paths.get(goal);
	}

	/**
	 *	Returns a list of lists of the dependencies of
	 * 	a given Plan.
	 *
	 *	@param the Plan requiring locks
	 *	@param the List<List<Table>> of dependencies
	 *
	 */

	public List<List<Table>> getDependencies(List<Table> plan){
		List<List<Table>> dependencies = new ArrayList<List<Table>>();
		for (Table table : plan){
			dependencies.add(this.getDependencies(table));
		}
		return dependencies;
	}

	@Override
	public String toString(){
		return "DBMS SIZE: " + size;
	}
	
	/**
	 * Returns parent index of current index based on
	 * the implementation of the Dbms.
	 *
	 * @param the index of the child.
	 * @return the index of the parent.
	 */
	protected abstract int getParentIndex(int childIndex);
	
	/**
	 * Returns a random int based on the desired
	 * distribution.
	 * @return the next random int
	 */
	protected abstract int getNextRandom();
	
	/**
	 * Returns the longest path in the Dbms
	 * @return the path length
	 */
	public abstract int getPathLength();

	
	private Table[] tables;
	private HashMap<Table, List<Table>> paths;
	int size;
	Random rand;
}