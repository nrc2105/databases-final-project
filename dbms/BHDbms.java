package dummy;

public class BHDbms{
	
	/**
	 *	Constructs partial ordering of the tables.
	 *
	 */

	@Override
	protected void constructDependencies(){
		paths = new HashMap<Table, List<Table>>();
		for(int i = 0; i < tables.size; i++){
			paths.put(tables[i], getPath(i));
		}
	}

	/**
	 * Constructs path from root based on BH structure
	 */
	private List<Table> getPath(int start){
		int currentIndex = start;
		Table currentTable;
		ArrayList<Table> path = new ArrayList<Table>();
		do{
			currentTable = tables[currentIndex];
			path.add(0,currentTable);
			currentIndex = (currentIndex - 1) / 2;
		} while (currentTable != tables[0])


	}
}