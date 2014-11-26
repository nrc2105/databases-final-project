package transactionManagement;

import database.Dbms;

public class TransactionFactory {
	
	public static Transaction getTransaction(int id, int size, Dbms database) {
		return new Transaction(id, size, database);
	}
	
	//TODO: Add more specific creations here later

}
