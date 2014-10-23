/**
 * Nicholas Cummins
 * 10/31/13
 *
 * An Anthill is an object which can be eaten at by animals. Animals
 * themselves are responsible for indicating when they want to eat at
 * and when they are ready to leave. Anthills are responsible for
 * indicating if it is safe for an Animal to enter.
 *
 * When an animal wants to enter a anthill, it calls tryToJoin on the
 * anthill instance. If the animal has entered the anthill
 * successfully, tryToJoin returns true. Otherwise, tryToJoin
 * returns false. The animal simulates the time spent at the anthill,
 * and then must call exitAnthill on the same anthill instance it
 * entered.
 */
public interface Anthill {
    /**
     * animal tries to eat at an anthill.
     *
     * @param  animal The animal that is attempting to ent at the anthill
     * @return true if the animal was able to start eating at the anthill,
     * false otherwise
     */
    public boolean tryToEatAtWithAnimal(Animal animal);
    
    /**
     * animal exits the anthill.
     * 
     * @param animal The animal that is leaving the anthill
     */
    public void exitAnthillWithAnimal(Animal animal);

    /**
    * returns the number of ants left in the anthill
    * must be checked before an animal tries to eat at to make sure there are ants left
    */
    public int antsLeft();

    /**
     * Sets the name of this anthill
     *
     * @param name The name of this anthill
     */
    public void setName(String name);

    /**
     * Returns the name of this anthill
     *
     * @return The name of this anthill
     */
    public String getName();
}
