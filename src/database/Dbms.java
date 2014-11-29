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
	 * @param the integer size of the table.
	 */

	public Dbms(int size){
		this.size = size;
		dummyRoot = false;
		weight = EQWEIGHT;
		rand = new Random();
		this.constructTables();
		this.constructDependencies();
	}
	
	/**
	 * Constructs a new DBMS with a specified
	 * number of tables and a boolean indicating
	 * whether the root is a table or a dummy.
	 * 
	 * @param the integer size.
	 * @param the integer weight.
	 * @param the boolean indicating dummy node.
	 */

	public Dbms(int size, String weight, boolean dummyRoot){
		this.dummyRoot = dummyRoot;
		this.weight = weight;
		this.size = size;
		if(dummyRoot){
			this.size++;
		}
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
		if(dummyRoot){
			return Arrays.copyOfRange(tables, 1, size);
		}else{
			return tables;
		}
	}

	/**
	 * Returns a subset of the tables
	 * @param count the requested number of tables
	 * @return the subset of tables
	 */
	public List<Table> getTables(int count){
		if(count > size){
			throw new RuntimeException();
		}
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
	 * Returns a random int based on the desired
	 * distribution.
	 * @return the next random int
	 */
	protected int getNextRandom(){
		int range = size;
		int out;
		if(dummyRoot){
			range--;
		}
		if(weight.equalsIgnoreCase(EQWEIGHT)){
			out = rand.nextInt(range);
		} else if(weight.equalsIgnoreCase(TOPWEIGHT)){
			out = Math.abs((int)(rand.nextGaussian()*range));
			if(out >= range){
				out = range - 1;
			}
		} else{
			out = (int)(range - Math.abs(rand.nextGaussian() * range));
			if(out < 0){
				out = 0;
			} else if (out >= range){
				out = range - 1;
			}
		}
		
		if(dummyRoot){
			out++;
		}
		return out;
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
		String out = "DBMS SIZE: " + size;
		if(weight.equalsIgnoreCase(EQWEIGHT)){
			out += " DIST: EQUAL WEIGHTS";
		} else if(weight.equalsIgnoreCase(TOPWEIGHT)){
			out += " DIST: TOP WEIGHTED";
		} else {
			out += " DIST: BOTTOM WEIGHTED";
		}
		if(dummyRoot){
			out += " ROOT: DUMMY";
		}else{
			out += " ROOT: TABLE";
		}
		return out;
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
	 * Returns the longest path in the Dbms
	 * @return the path length
	 */
	public abstract int getPathLength();

	public static final String EQWEIGHT = "equal";
	public static final String TOPWEIGHT = "top";
	public static final String BOTTOMWEIGHT = "bottom";
	
	private Table[] tables;
	private HashMap<Table, List<Table>> paths;
	private boolean dummyRoot;
	private String weight;
	int size;
	private Random rand;
}