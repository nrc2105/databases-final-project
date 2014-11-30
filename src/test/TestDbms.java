package test;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;

import java.util.List;

import database.BHDbms;
import database.DHDbms;
import database.DbmsFactory;
import database.Table;
import database.Dbms;
import static org.junit.Assert.*;

public class TestDbms {
	/*Deprecated
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
	*/
	
	@Test
	public void testDbmsFactory(){
		Dbms bw = DbmsFactory.getDbms(DbmsFactory.BH,Dbms.BOTTOMWEIGHT,false,1,2);
		Dbms tw = DbmsFactory.getDbms(DbmsFactory.BH,Dbms.TOPWEIGHT,false,1,2);
		Dbms eq = DbmsFactory.getDbms(DbmsFactory.BH,Dbms.EQWEIGHT,false,1,2);
		Dbms wrong = DbmsFactory.getDbms("TREE","MIDDLE",false,1,2);
		assertEquals("DBMS SIZE: 1 DIST: BOTTOM WEIGHTED ROOT: TABLE STRUCTURE: BINARY HEAP", 
																		bw.toString());
		assertEquals("DBMS SIZE: 1 DIST: TOP WEIGHTED ROOT: TABLE STRUCTURE: BINARY HEAP", tw.toString());
		assertEquals("DBMS SIZE: 1 DIST: EQUAL WEIGHTS ROOT: TABLE STRUCTURE: BINARY HEAP", eq.toString());
		assertNull(wrong);		
	}
	
	@Test
	public void testBHDbms(){
		Dbms db = new BHDbms(7);
		Table[] tables = db.getTables();
		assertEquals("Size should be 7", 7, tables.length);
		assertEquals("Height should be 3", 3, db.getPathLength());
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
	
	@Test
	public void testDHDbms(){
		Dbms db = new DHDbms(7,Dbms.EQWEIGHT,false,2);
		Table[] tables = db.getTables();
		assertEquals("Size should be 7", 7, tables.length);
		assertEquals("Height should be 3", 3, db.getPathLength());
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
	
	@Test
	public void testBHDbmsDummyRoot(){
		Dbms db = new BHDbms(6,Dbms.EQWEIGHT,true);
		Table[] tables = db.getTables();
		assertEquals(6, tables.length);
		assertEquals("TABLE 1", tables[0].toString());
	}
	
	@Test
	public void testBHDbmsTopWeight(){
		Dbms db = new BHDbms(6,Dbms.TOPWEIGHT,false);
		List<Table> tables = db.getTables(4);
		assertEquals(4, tables.size());
		for(Table table: tables){
			assertNotNull(table);
		}
	}
	
	@Test
	public void testBHDbmsBottomWeight(){
		Dbms db = new BHDbms(6,Dbms.BOTTOMWEIGHT,false);
		List<Table> tables = db.getTables(4);
		assertEquals(4, tables.size());
		for(Table table: tables){
			assertNotNull(table);
		}
	}
	

	
}
