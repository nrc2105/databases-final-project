package test;

import org.junit.Test;

import database.BottomWeightBHDbms;
import database.DbmsFactory;
import database.Table;
import database.Dbms;
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
	
	@Test
	public void testDbmsFactory(){
		Dbms bw = DbmsFactory.getDbms(DbmsFactory.BH,DbmsFactory.BOTTOMWEIGHT,1);
		Dbms tw = DbmsFactory.getDbms(DbmsFactory.BH,DbmsFactory.TOPWEIGHT,1);
		Dbms eq = DbmsFactory.getDbms(DbmsFactory.BH,DbmsFactory.EQWEIGHT,1);
		Dbms wrong = DbmsFactory.getDbms("TREE","MIDDLE",1);
		assertEquals("class database.BottomWeightBHDbms", bw.getClass().toString());
		assertEquals("class database.TopWeightBHDbms", tw.getClass().toString());
		assertEquals("class database.EqualWeightBHDbms", eq.getClass().toString());
		assertNull(wrong);		
	}
	
	@Test
	public void 
	

	
}
