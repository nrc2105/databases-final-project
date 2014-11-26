package transactionManagement;

public class TransactionManager {	
	
	int numXactions;
	int writesPerXaction;
	Transaction[] transactionSet;
	
	
	
	
	
	public TransactionManager(int numXactions, int writesPerXaction) {
		
		this.numXactions = numXactions;
		this.writesPerXaction = writesPerXaction;
		transactionSet = new Transaction[numXactions];
		
		//TODO put additional constructor arguments (setup parameters?) in here
		
		
		
	}
	
	

	// Create a batch of transactions
	public void createBatch() {
		
	}
	
	
	//Start lock manager
	//TODO Decide whether to start manager before needing it. depends whether there are startup ops.
	
	
	
	// Start transactions
	public void runBatch() {
		
	}
	
	
	// Block on transactions
	
	
	
	// Report data

}
