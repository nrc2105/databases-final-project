package test;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;

import java.util.List;

import database.BottomWeightBHDbms;
import database.DbmsFactory;
import database.EqualWeightBHDbms;
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
	public void testBHDbms(){
		Dbms db = new EqualWeightBHDbms(7);
		Table[] tables = db.getTables();
		assertEquals("Size should be 6", 7, tables.length);
		List<Table> subTables = db.getTables(3);
		assertEquals("Size should be 3", 3, subTables.size());
		for(Table table : subTables){
			assertEquals("class database.Table", table.getClass().toString());
		}
		HashSet<Table> tableSet = new HashSet<Table>(subTables);
		assertEquals("Size should still be 3",3, tableSet.size());
		
		List<Table> dependtest1 = db.getDependencies(tables[6]);
		assertEquals(3, dependtest1.size());
		assertEquals(tables[0], dependtest1.get(0));
		assertEquals(tables[2], dependtest1.get(1));
		assertEquals(tables[6], dependtest1.get(2));
		ArrayList<Table> tableList = new ArrayList<Table>();
		tableList.add(tables[0]);
		tableList.add(tables[0]);
		tableList.add(tables[6]);
		List<List<Table>> dependtest2 = db.getDependencies(tableList);
		assertEquals(3, dependtest2.size());
		assertEquals(dependtest2.get(0),dependtest2.get(1));
		assertEquals(dependtest1, dependtest2.get(2));
	}
	

	
}
