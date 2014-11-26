package database;


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
		this.constructTables();
		this.constructDependencies();
	}

	/**
	 *	Creates enough tables to fill the list
	 *	to the appropriate size of the Dbms.
	 */

	private void constructTables(){
		tables = Table[size];

		for(int i = 0; i < size; i++){
			tables[i] = new Table();
		}
	}

	/**
	 *	Constructs partial ordering of the tables.
	 *
	 */

	protected void constructDependencies(){
		paths = new HashMap<Table, List<Table>>();
		for(int i = 0; i < tables.size; i++){
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
	 * Returns parent index of current index based on
	 * the implementation of the Dbms.
	 *
	 * @param the index of the child.
	 * @return the index of the parent.
	 */
	protected abstract int getParentIndex(int childIndex);


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
	 *	Returns the locks necessary to get to a
	 * given table.
	 *
	 *	@param the Table sought.
	 *	@return the List (in order) of Tables needed.
	 *
	 */

	public List<Table> getDependecies(Table goal){
		return paths.get(goal);
	}

	/**
	 *	Returns a list of lists of the dependencies of
	 * 	a given Plan.
	 *
	 *	@param the Plan requiring locks
	 *	@param the List<List<Tables>> of dependencies
	 *
	 */

	public List<List<Tables>> getDependecies(Plan plan){
		List<List<Tables>> dependencies = new ArrayList<ArrayList<Table>>();
		for (Table table : plan.requiredTables()){
			dependencies.add(this.getDependecies(table));
		}
		return dependencies;
	}

	Table[] tables;
	private HashMap<Table, List<Table>> paths;
}