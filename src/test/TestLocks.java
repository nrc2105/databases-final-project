package test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import lockManagement.LockReleaser;
import lockManagement.LockSeeker;

import org.junit.Test;

import database.Entity;
import static org.junit.Assert.*;


public class TestLocks {
	@Test
	public void testSeeker() throws InterruptedException{
		int id = 1;
		Entity table1 = new Entity(1);
		Entity table2 = new Entity(2);
		Entity table3 = new Entity(3);
		ArrayList<Entity> lockpath = new ArrayList<Entity>();
		lockpath.add(table1);
		lockpath.add(table2);
		lockpath.add(table3);
		BlockingQueue<Entity> unnecessaryLocks = new ArrayBlockingQueue<Entity>(3);
		ConcurrentHashMap<Entity,Integer> requiredLocks = new ConcurrentHashMap<Entity,Integer>();
		LockSeeker seeker = new LockSeeker(id, lockpath, unnecessaryLocks, requiredLocks);
		assertNotNull(seeker);
		Thread seekerThread = new Thread(seeker);
		seekerThread.start();
		seekerThread.join();
		assertEquals(table1, unnecessaryLocks.take());
		assertEquals(table2, unnecessaryLocks.take());
		assertEquals(new Integer(1), requiredLocks.get(table3));
		
	}
	
	@Test
	public void testReleaser() throws InterruptedException{
		int id = 1;
		Entity table1 = new Entity(1);
		Entity table2 = new Entity(2);
		Entity table3 = new Entity(3);
		ArrayList<Entity> dependency1 = new ArrayList<Entity>();
		dependency1.add(table1);
		ArrayList<Entity> dependency2 = new ArrayList<Entity>();
		dependency2.add(table1);
		dependency2.add(table2);
		ArrayList<Entity> dependency3 = new ArrayList<Entity>();
		dependency3.add(table1);
		dependency3.add(table2);
		dependency3.add(table3);
		ArrayList<List<Entity>> dependencies = new ArrayList<List<Entity>>();
		dependencies.add(dependency1);
		dependencies.add(dependency2);
		dependencies.add(dependency3);
		BlockingQueue<Entity> unnecessaryLocks = new ArrayBlockingQueue<Entity>(1);
		LockReleaser releaser = new LockReleaser(id, dependencies, unnecessaryLocks);
		assertNotNull(releaser);
		Thread releaserThread = new Thread(releaser);
		releaserThread.start();
		table1.acquireLock(id);
		table2.acquireLock(id);
		table3.acquireLock(id);
		assertFalse(table1.acquireLock(2));
		assertFalse(table2.acquireLock(2));
		assertFalse(table3.acquireLock(2));
		unnecessaryLocks.put(table3);
		unnecessaryLocks.put(table1);
		unnecessaryLocks.put(table2);
		assertFalse(table1.acquireLock(2));
		assertFalse(table2.acquireLock(2));
		assertTrue(table3.acquireLock(2));
		unnecessaryLocks.put(table2);
		unnecessaryLocks.put(table1);
		assertFalse(table1.acquireLock(2));
		assertTrue(table2.acquireLock(2));
		unnecessaryLocks.put(table1);
		releaserThread.join();
		assertTrue(table1.acquireLock(2));
		assertTrue(true);
	}
}
