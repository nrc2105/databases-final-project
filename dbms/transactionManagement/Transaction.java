package transactionManagement;

import java.util.concurrent.ArrayBlockingQueue;

public class Transaction {
	
	private ArrayBlockingQueue<Table> finishedLocks;
	private ConcurrentHashSet<Table> lockTable;
	

}
