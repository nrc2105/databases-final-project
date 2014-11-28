package test;

import org.junit.Test;

import database.Table;
import static org.junit.Assert.*;

public class TestDbms {
	
	@Test
	public void testTables(){
		//Test table creation
		Table table = new Table();
		assertNotNull("table should not be null", table);
		boolean success = table.acquireLock(1);
		assertTrue("id 1 should have acquired table lock", success);
		success = table.acquireLock(1);
		assertTrue("id 1 should still acquire lock", success);
		success = table.acquireLock(2);
		assertFalse("id 2 should not be able to acquire lock", success);
		table.releaseLock(1);
		success = table.acquireLock(2);
		assertTrue("id 2 should now be able to acquire lock", success);
	}
	
}
