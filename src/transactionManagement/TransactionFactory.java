package transactionManagement;

import database.Dbms;

public class TransactionFactory {
	
	public static Transaction getTransaction(int id, int size, Dbms database) {
		assert size > 0 : "ERROR: Factory reports transaction size is " + size;
		return new Transaction(id, size, database);
	}

}
