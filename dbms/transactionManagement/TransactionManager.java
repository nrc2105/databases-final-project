package transactionManagement;

public class TransactionManager {	
	
	int numXactions;
	int writesPerXaction;
	boolean homogeneous;
	Transaction[] transactionSet;	
	
	
	
	
	public TransactionManager(int numXactions, int writesPerXaction, boolean homogeneous) {
		
		this.numXactions = numXactions;
		this.writesPerXaction = writesPerXaction;
		this.homogeneous = homogeneous;
		transactionSet = new Transaction[numXactions];
		
		//TODO put additional constructor arguments (setup parameters?) in here
		
		
		
	}
	
	

	// Create a batch of transactions
	public void createBatch() {
		if (homogeneous) {
			for (int tIndex = 0; tIndex < numXactions; tIndex++) {
				transactionSet[tIndex] = new Transaction(writesPerXaction);
			}
		} else {
			for (int tIndex = 0; tIndex < numXactions; tIndex++) {
				transactionSet[tIndex] = new Transaction(Math.random() * writesPerXaction);
			}
		}
	}
	
	
	// Start transactions
	public void runBatch() {
		for (Transaction t : transactionSet) {
			t.start();
		}
		
	}
	//TODO get transaction stats back from them!
	
	
	// Block on transactions
	
	
	
	// Report data

}
