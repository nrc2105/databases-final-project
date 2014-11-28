package test;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import lockManagement.LockSeeker;

import org.junit.Test;

import database.Table;
import static org.junit.Assert.*;


public class TestLocks {
	@Test
	public void testSeeker() throws InterruptedException{
		int id = 1;
		Table table1 = new Table(1);
		Table table2 = new Table(2);
		Table table3 = new Table(3);
		ArrayList<Table> lockpath = new ArrayList<Table>();
		lockpath.add(table1);
		lockpath.add(table2);
		lockpath.add(table3);
		BlockingQueue<Table> unnecessaryLocks = new ArrayBlockingQueue<Table>(3);
		ConcurrentHashMap<Table,Integer> requiredLocks = new ConcurrentHashMap<Table,Integer>();
		LockSeeker seeker = new LockSeeker(id, lockpath, unnecessaryLocks, requiredLocks);
		assertNotNull(seeker);
		Thread seekerThread = new Thread(seeker);
		seekerThread.start();
		seekerThread.join();
		assertEquals(table1, unnecessaryLocks.take());
		assertEquals(table2, unnecessaryLocks.take());
		assertEquals(new Integer(1), requiredLocks.get(table3));
		
	}
}
