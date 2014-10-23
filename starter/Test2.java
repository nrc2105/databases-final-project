import java.util.ArrayList;

/**
 * Nicholas Cummins
 * ncummins@brandeis.edu
 * 10/31/13
 *
 * This test class tests the implementation of the PriorityScheduler
 * by starting 200 threads of Aardvarks and Anteaters with priorities
 * ranging from 0 - 4 on 2 BasicAnthills.
 */

public class Test2{
    public static void main(String[] args){
	Anthill h1 = new BasicAnthill(0+"", 100);
	Anthill h2 = new BasicAnthill(1+"", 100);
	ArrayList<Anthill> a = new ArrayList<Anthill>();
	a.add(h1);
	a.add(h2);
	Anthill p = new PriorityScheduler(a);

	ArrayList<Thread> aardvarks1 = new ArrayList<Thread>();
	ArrayList<Thread> anteaters = new ArrayList<Thread>();
	ArrayList<Thread> aardvarks2 = new ArrayList<Thread>();

	for(int i = 0; i < 50; i++){
	    Aardvark e = new Aardvark(i+"-Aardvark",p);
	    e.setPriority(i%5);
	    Thread t = new Thread(e);
	    aardvarks1.add(t);
	}
	for(int i = 50; i < 150; i++){
	    Aardvark e = new Aardvark(i+"-Aardvark",p);
	    e.setPriority(i%5);
	    Thread t = new Thread(e);
	    aardvarks2.add(t);
	}
	for(int i = 150; i < 200; i++){
	    Anteater e = new Anteater(i+"-Anteater",p);
	    e.setPriority(i%5);
	    Thread t = new Thread(e);
	    anteaters.add(t);
	}

	for(Thread t: aardvarks1)
	    t.start();
	for(Thread t: anteaters)
	    t.start();
	for(Thread t: aardvarks2)
	    t.start();
    }
}
