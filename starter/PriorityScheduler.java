import java.util.ArrayList;

/**
 * The PriorityScheduler class takes a collectin of anthills
 * and controlls access to them based on priority 0-4 with 4
 * being the highest.
 *
 * Author: Nicholas Cummins
 * email: ncummins@brandeis.edu
 * 10/31/13
 */

public class PriorityScheduler implements Anthill{

    private ArrayList<Anthill> hills;

    //Counters of animals with each priority except 0
    int one;
    int two;
    int three;
    int four;
  
    String name;
    int ants;

    //Keeps track of what animals are at
    //what hills
    Animal[][] atHill;
    


    /**
     * PriorityScheduler constructor, sets the
     * hills ArrayList, sets all counters to zero.
     * 
     *
     * @param an ArrayList of Anthills
     */

    public PriorityScheduler(ArrayList<Anthill> h){
	hills = h;
	atHill = new Animal[hills.size()][3];
     
	one = 0;
	two = 0;
	three = 0;
	four = 0;
	name = null;
	ants = 0;
    }

    /**
     * Takes an animal, reads its priority, and determines
     * whether it has a high enough one to attempt to eat.
     * Any animal that can't eat is told to wait, and counters
     * of the number of animals waiting with each priority keep
     * track of when the lower priority animals can go.
     *
     * @param the animal trying to eat
     * @return a boolean indicating success, will always return true.
     */

    @Override
    public synchronized boolean tryToEatAtWithAnimal(Animal animal){

	boolean done = false;

	//Priority 0 animals wait for all others

	if(animal.getPriority() == 0){
		while(one > 0 || two > 0 || three > 0 || four > 0){
		    try{
			wait();
		    }
		    catch(InterruptedException e){
		    }
		}	    
	}

	//Priority 1 animals wait for 2-4

	else if(animal.getPriority() == 1){
	    one++;
	    while(two > 0 || three > 0 || four > 0){
		try{
		    wait();
		}
		catch(InterruptedException e){
		}
	    }
	}

	//Priority 2 animals wait for 3 and 4

	else if(animal.getPriority() == 2){
	    two++;
	    while(three > 0 ||four > 0){
		try{
		    wait();
		}
		catch(InterruptedException e){
		}
	    }
	    
	}

	//Priority 3 animals wait for 4

	else if(animal.getPriority() == 3){
	    three++;
	    while(four > 0){
		try{
		    wait();
		}
		catch(InterruptedException e){
		}
	    }
	    
	}

	//Priority 4 animals don't wait

	else if(animal.getPriority() == 4){
	    four++;
	}
        

	while(!done){
       	
	    for(Anthill a:hills){
		if(a.tryToEatAtWithAnimal(animal)){
		    if(animal.getPriority() == 1)
			one--;    
		    else if(animal.getPriority() == 2)
			two--;
		    else if(animal.getPriority() == 3)
			three--;
		    else if(animal.getPriority() == 4)
			four--;
		    
		    if(atHill[hills.indexOf(a)][0] == null)
			atHill[hills.indexOf(a)][0] = animal;
		    else if(atHill[hills.indexOf(a)][1] == null)
			atHill[hills.indexOf(a)][1] = animal;
		    else
			atHill[hills.indexOf(a)][2] = animal;
		    return true;
		}
	    }
	    
	    try{
		wait();
	    }
	    catch(InterruptedException e){
		System.out.println("Interrupted");
	    }
	}
	
	
	return true;
	
    }

    /**
     * Called when an animal is done eating and wishes to leave
     * its Anthill.  First it determined which anthill the animal
     * was eating at, then exitAnthillWithAnimal is called on
     * that Anthill.  Finally, notifyAll is called so all waiting
     * Threads wake up and attempt to eat at the Anhills.
     *
     * @param the animal ready to exit
     */

    @Override
    public synchronized void exitAnthillWithAnimal(Animal animal){
	for(int i = 0; i < atHill.length;i++){
	    for(int j = 0; j < 3; j++){
		if(atHill[i][j] == animal){
		    atHill[i][j] = null;
		    hills.get(i).exitAnthillWithAnimal(animal);
		    notifyAll();
		    return;
		    
		}
	    }
	}
	
    }

    /**
     * This method is implemented to fulfill the Anthill inteface,
     * will always return 0.
     *
     * @return the number of ants, 0.
     */

    @Override
    public int antsLeft(){
	return ants;
    }

    /**
     * Sets the name of the PriorityScheduler
     *
     * @param the new name
     */

    @Override
    public void setName(String name){
	this.name = name;
    }

    /**
     * Returns the name of the PriorityScheduler
     * 
     * @return the name
     */

    @Override
    public String getName(){
	return name;
    }

    
}
