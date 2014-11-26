package transactionManagement;

public class TransactionManager {	
	
	int numXactions;
	int writesPerXaction;
	boolean homogeneous;
	Transaction[] transactionSet;
	Dbms database;
	
	
	
	
	public TransactionManager(int numXactions, int writesPerXaction, boolean homogeneous, 
			Dbms database) {
		
		this.numXactions = numXactions;
		this.writesPerXaction = writesPerXaction;
		this.homogeneous = homogeneous;
		this.database = database;
		transactionSet = new Transaction[numXactions];
		
		//TODO put additional constructor arguments (setup parameters?) in here
		
		
		
	}
	
	

	// Create a batch of transactions
	public void createBatch() {
		if (homogeneous) {
			for (int tIndex = 0; tIndex < numXactions; tIndex++) {
				transactionSet[tIndex] = 
						TransactionFactory.getTransaction(writesPerXaction, database);
			}
		} else {
			for (int tIndex = 0; tIndex < numXactions; tIndex++) {
				transactionSet[tIndex] = 
						TransactionFactory.getTransaction((int)Math.random() * writesPerXaction, database);
			}
		}
	}
	
	
	// Start transactions
	public void runBatch() {
		for (Transaction x : transactionSet) {
			Thread t = new Thread(x);
			t.start();
		}
		
	}
	//TODO get transaction stats back from them!
	
	
	// Block on transactions
	
	
	
	// Report data

}
