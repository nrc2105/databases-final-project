package database;

import java.util.*;
/*
 *	The Dbms is the control structure of the simulator.
 *	It holds the Entities, which are lockable
 *	and unlockable objects that can represent any lockable
 *  portion of a database, and 
 *	sub classes of Dbms will determine the structure
 *	of dependencies between entities.  
 *
 *	Author: Nicholas Cummins
 *	email: ncummins@brandeis.edu
 *	10/25/14
 */

public abstract class Dbms{

	/**
	 * Constructs a new DBMS with a specified
	 * number of entities.
	 *
	 * @param the integer size of the table.
	 */

	public Dbms(int size){
		this.size = size;
		dummyRoot = false;
		weight = EQWEIGHT;
		rand = new Random();
	}
	

	
	/**
	 * Constructs a new DBMS with a specified
	 * number of entities and a boolean indicating
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
	}
	
	/**
	 * Initializes entities and dependencies.
	 */
	protected void initialize(){
		this.constructTables();
		this.constructDependencies();
	}

	/**
	 *	Creates enough entities to fill the list
	 *	to the appropriate size of the Dbms.
	 */

	private void constructTables(){
		entities =  new Entity[size];

		for(int i = 0; i < size; i++){
			entities[i] = new Entity(i);
		}
	}

	/**
	 *	Constructs partial ordering of the entities.
	 *
	 */

	private void constructDependencies(){
		paths = new HashMap<Entity, List<Entity>>();
		for(int i = 0; i < size; i++){
			paths.put(entities[i], getPath(i));
		}
	}

	/**
	 * Constructs path to a table from the root.
	 *
	 * @param the starting integer
	 */
	private List<Entity> getPath(int start){
		int currentIndex = start;
		Entity currentTable;
		ArrayList<Entity> path = new ArrayList<Entity>();
		while(currentIndex >= 0){
			currentTable = entities[currentIndex];
			path.add(0,currentTable);
			currentIndex = getParentIndex(currentIndex);
		}

		return path;

	}


	/**
	 * 	Gets the list of entities in the Dbms.
	 *
	 *	@return an Array of all Entities.
	 *
	 */

	public Entity[] getEntities(){
		if(dummyRoot){
			return Arrays.copyOfRange(entities, 1, size);
		}else{
			return entities;
		}
	}

	/**
	 * Returns a subset of the entities
	 * @param count the requested number of entities
	 * @return the subset of entities
	 */
	public List<Entity> getEntities(int count){
		if(count > size){
			throw new RuntimeException();
		}
		HashSet<Integer> tableSet = new HashSet<Integer>();
		while(tableSet.size() < count){
			tableSet.add(getNextRandom());
		}
		ArrayList<Entity> tableSubset = new ArrayList<Entity>();
		for (Integer i : tableSet){
			tableSubset.add(this.entities[i]);
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
	 *	@param the Entity sought.
	 *	@return the List (in order) of Tables needed.
	 *
	 */

	public List<Entity> getDependencies(Entity goal){
		return paths.get(goal);
	}

	/**
	 *	Returns a list of lists of the dependencies of
	 * 	a given Plan.
	 *
	 *	@param the Plan requiring locks
	 *	@param the List<List<Entity>> of dependencies
	 *
	 */

	public List<List<Entity>> getDependencies(List<Entity> plan){
		List<List<Entity>> dependencies = new ArrayList<List<Entity>>();
		for (Entity entity : plan){
			dependencies.add(this.getDependencies(entity));
		}
		return dependencies;
	}

	@Override
	public String toString(){
		String out;
		if(dummyRoot){
			out = "DBMS SIZE: " + (size - 1);
		}else{
			out = "DBMS SIZE: " + size;
		}
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
	
	private Entity[] entities;
	private HashMap<Entity, List<Entity>> paths;
	private boolean dummyRoot;
	String weight;
	int size;
	private Random rand;
}