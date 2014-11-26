package transactionManagement;

public class TransactionFactory {
	
	public static Transaction getTransaction(int size, Dbms database) {
		return new Transaction(size, database);
	}
	
	//TODO: Add more specific creations here later

}
