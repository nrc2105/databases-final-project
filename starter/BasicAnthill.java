/**
 * Nicholas Cummins
 * ncummins@brandeis.edu
 * 10/31/13
 *
 * BasicAnthill implements Anthill and upholds all of its requirements
 * ie only three Anteaters or one Aardvark at a time and no animal
 * may eat at an empty BasicAnthill all while preventing race conditions.
 */

public class BasicAnthill implements Anthill{

    private String name;
    private int ants;
    private int eater;
    private int vark;

    /**
     * Constructs a new BasicAnthill with a name and 
     * ants, as well as the number of animals of each type
     * allowed
     *
     * @param the name of the anthill
     * @param the number of ants it starts with
     */


    public BasicAnthill(String n, int a){
	name = n;
	ants = a;
	eater = 1;
	vark = 3;
    }

    /**
     * Tests whether or not an animal can eat an an BasicAnthill, and
     * maintains the restrictions of only one Anteater or up to 
     * three Aardvarks at a time
     *
     * @param animal The animal that is trying to eat at the anthill
     * @param true if the animal can start eating, false otherwise
     */

    @Override
    public synchronized boolean tryToEatAtWithAnimal(Animal animal){
        //If the hill is depleted, return false
	if(antsLeft()<1)
	    return false;
	//If this is an Aardvark
	if(animal instanceof Aardvark){
	    //If the  max number of Aardvarks have been reached,
	    //or there's an Anteater the Aardvark is denied.
	    if(vark < 1 || (vark == 3 && eater == 0))
		return false;
	    else
		vark--;
	}
	//If this is an Anteater
	if(animal instanceof Anteater){
	    //If there's an animal at the hill 
	    //the Anteater is denied
	    if(eater < 1)
		return false;
	}

	eater = 0;
	ants--;
	System.out.println(animal+" started eating at "+
			   getName()+" with priority "+
			   animal.getPriority());
	return true;
    
      
    }

    /**
     * Animal exits the anthill, and indicates that the BasicAnthill 
     * can accept an animal/animals again.
     *
     * @param animal The animal that is leaving the anthill
     */

    @Override
    public synchronized void exitAnthillWithAnimal(Animal animal){
	//If the animal was an Anteater, open the hill up
	if(animal instanceof Anteater)
	    eater = 1;
	//If the animal was an Aardvark, dec the number of
	//Aardvarks on the Anthill, if that was the last one
	//open up the hill to all animals
	if(animal instanceof Aardvark){
	    vark++;
	    if(vark == 3)
		eater = 1;
	    
	}
	System.out.println(animal+" has eaten from "+getName()+" with priority "+animal.getPriority());
    }


    /**
     * Returns the number of ants left in the BasicAnthill.
     *
     * @return the number of ants left
     */

    @Override
    public int antsLeft(){
	return ants;
    }

    /**
     * Sets the name of this BasicAnthill
     * @param name the String to set the name
     */

    @Override
    public void setName(String name){
	this.name = name;
    }

    /**
     * Returns the name of the BasicAnthill
     * @return The name of this BasicAnthill
     */
    
    @Override
    public String getName(){
	return name;
    }
	    
}
