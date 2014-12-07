package test;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;

import java.util.List;

import database.BHDbms;
import database.DHDbms;
import database.DbmsFactory;
import database.LBDbms;
import database.Entity;
import database.Dbms;
import static org.junit.Assert.*;

public class TestDbms {
	
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
		Entity[] tables = db.getEntities();
		assertEquals("Size should be 7", 7, tables.length);
		assertEquals("Height should be 3", 3, db.getPathLength());
		List<Entity> subTables = db.getEntities(3);
		assertEquals("Size should be 3", 3, subTables.size());
		for(Entity entity : subTables){
			assertEquals("class database.Entity", entity.getClass().toString());
		}
		HashSet<Entity> tableSet = new HashSet<Entity>(subTables);
		assertEquals("Size should still be 3",3, tableSet.size());
		
		List<Entity> dependtest1 = db.getDependencies(tables[6]);
		assertEquals(3, dependtest1.size());
		assertEquals(tables[0], dependtest1.get(0));
		assertEquals(tables[2], dependtest1.get(1));
		assertEquals(tables[6], dependtest1.get(2));
		ArrayList<Entity> tableList = new ArrayList<Entity>();
		tableList.add(tables[0]);
		tableList.add(tables[0]);
		tableList.add(tables[6]);
		List<List<Entity>> dependtest2 = db.getDependencies(tableList);
		assertEquals(3, dependtest2.size());
		assertEquals(dependtest2.get(0),dependtest2.get(1));
		assertEquals(dependtest1, dependtest2.get(2));
	}
	
	@Test
	public void testDHDbms(){
		Dbms db = new DHDbms(7,Dbms.EQWEIGHT,false,2);
		Entity[] tables = db.getEntities();
		assertEquals("Size should be 7", 7, tables.length);
		assertEquals("Height should be 3", 3, db.getPathLength());
		List<Entity> subTables = db.getEntities(3);
		assertEquals("Size should be 3", 3, subTables.size());
		for(Entity entity : subTables){
			assertEquals("class database.Entity", entity.getClass().toString());
		}
		HashSet<Entity> tableSet = new HashSet<Entity>(subTables);
		assertEquals("Size should still be 3",3, tableSet.size());
		
		List<Entity> dependtest1 = db.getDependencies(tables[6]);
		assertEquals(3, dependtest1.size());
		assertEquals(tables[0], dependtest1.get(0));
		assertEquals(tables[2], dependtest1.get(1));
		assertEquals(tables[6], dependtest1.get(2));
		ArrayList<Entity> tableList = new ArrayList<Entity>();
		tableList.add(tables[0]);
		tableList.add(tables[0]);
		tableList.add(tables[6]);
		List<List<Entity>> dependtest2 = db.getDependencies(tableList);
		assertEquals(3, dependtest2.size());
		assertEquals(dependtest2.get(0),dependtest2.get(1));
		assertEquals(dependtest1, dependtest2.get(2));
	}
	
	@Test
	public void testLBDbms(){
		Dbms db = new LBDbms(7,Dbms.EQWEIGHT,false);
		Entity[] tables = db.getEntities();
		assertEquals("Size should be 7", 7, tables.length);
		assertEquals("Height should be 4", 4, db.getPathLength());
		List<Entity> subTables = db.getEntities(3);
		assertEquals("Size should be 3", 3, subTables.size());
		for(Entity entity : subTables){
			assertEquals("class database.Entity", entity.getClass().toString());
		}
		HashSet<Entity> tableSet = new HashSet<Entity>(subTables);
		assertEquals("Size should still be 3",3, tableSet.size());
		
		List<Entity> dependtest1 = db.getDependencies(tables[6]);
		assertEquals(4, dependtest1.size());
		assertEquals(tables[0], dependtest1.get(0));
		assertEquals(tables[1], dependtest1.get(1));
		assertEquals(tables[2], dependtest1.get(2));
		assertEquals(tables[6], dependtest1.get(3));
		ArrayList<Entity> tableList = new ArrayList<Entity>();
		tableList.add(tables[4]);
		tableList.add(tables[4]);
		tableList.add(tables[6]);
		List<List<Entity>> dependtest2 = db.getDependencies(tableList);
		assertEquals(3, dependtest2.size());
		assertEquals(dependtest2.get(0),dependtest2.get(1));
		assertEquals(dependtest1, dependtest2.get(2));
	}
	
	@Test
	public void testBHDbmsDummyRoot(){
		Dbms db = new BHDbms(6,Dbms.EQWEIGHT,true);
		Entity[] tables = db.getEntities();
		assertEquals(6, tables.length);
		assertEquals("TABLE 1", tables[0].toString());
	}
	
	@Test
	public void testBHDbmsTopWeight(){
		Dbms db = new BHDbms(6,Dbms.TOPWEIGHT,false);
		List<Entity> entities = db.getEntities(4);
		assertEquals(4, entities.size());
		for(Entity entity: entities){
			assertNotNull(entity);
		}
	}
	
	@Test
	public void testBHDbmsBottomWeight(){
		Dbms db = new BHDbms(6,Dbms.BOTTOMWEIGHT,false);
		List<Entity> entities = db.getEntities(4);
		assertEquals(4, entities.size());
		for(Entity entity: entities){
			assertNotNull(entity);
		}
	}
	

	
}
