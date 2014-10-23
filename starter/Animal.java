import java.util.*;

/**
 * Nicholas Cummins
 * 10/31/13
 *
 * An Animal is a Runnable which eats at anthills. You must subclass
 * Animal to customize its behavior (e.g., Aardvark and Anteater).
 *
 * When you start a thread which runs an Animal, the Animal will
 * immediately begin trying to eat at the anthill or anthills passed into
 * its constructor by calling tryToEatAtWithAnimal on each Anthill instance. As
 * long as tryToEatAtWithAnimal returns false (indicating that the Animal did
 * not eat at that Anthill), the Animal will keep trying. This is
 * called busy-waiting.
 *
 * In addition to recreating the constructors, the only method that
 * you must override in Animal subclasses is getDefaultSpeed. This
 * instance method is called from the private init method, and the
 * integer that it returns is used as the speed for the Animal.
 */
public abstract class Animal implements Runnable {
    private String             name;
    private Collection<Anthill> anthills;
    private int                priority;
    private int                speed;

    /**
     * Initialize an Animal; called from Animal constructors.
     */
    private void init(String name,
                      Collection<Anthill> anthills, int priority) {
        this.name      = name;
        this.anthills  = anthills;
        this.priority  = 0;
        this.speed     = getDefaultSpeed();

        if(this.speed < 0 || this.speed > 9) {
            throw new RuntimeException("Animal has invalid speed");
        }
    }

    /**
     * Override in a subclass to determine the speed of the
     * Animal.
     *
     * Must return a number between 0 and 9 (inclusive). Higher
     * numbers indicate greater speed. The faster an Animal, the less
     * time it will spend at an anthill.
     */
    protected abstract int getDefaultSpeed();

    /**
     * Create an Animal with default priority that eat from one of a collection of anthills.
     * 
     * @param name      The name of this Animal to be displayed in the
     *                  output.
     * @param anthills  A collection of anthills that are available to
     *                  eat from.
     */
    public Animal(String name,
                   Collection<Anthill> anthills) {
        init(name, anthills, 0);
    }
  
    /**
     * Create a Animal with default priority that can eat from only one
     * anthill.
     * 
     * @param name      The name of this Animal to be displayed in the
     *                  output.
     * @param anthill   A single anthill that can be eaten from.
     */
    public Animal(String name, Anthill anthill) {
        ArrayList<Anthill> anthills = new ArrayList<Anthill>();
        anthills.add(anthill);
        init(name, anthills, 0);
    }
    
    /** 
     * Sets this Animal's priority - used for priority scheduling
     *
     * @param priority The new priority (between 0 and 4 inclusive)
     */
    public final void setPriority(int priority) {
        if(priority < 0 || priority > 4) {
            throw new RuntimeException("Invalid priority: " + priority);
        }
        this.priority = priority;
    }
    
    /**
     * Returns the priority of this Animal
     *
     * @return This Animal's priority.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Returns the name of this Animal
     *
     * @return The name of this Animal
     */
    public final String getName() {
        return name;
    }

    /**
     * By default, a Animal returns its name when you transform it
     * into a string.
     *
     * @return Name of the Animal
     */
    public String toString() {
        return getName();
    }
    
    /**
     * Find and eat from one of the anthills.
     * 
     * When a thread is run, it keeps looping through its collection
     * of available anthills until it succeeds in starting to eat at one of
     * them. Then, it will call doWhileAtAnthill (to simulate doing
     * some work of eating, i.e., that it takes time to eat
     * one of the ants), then leave that anthill.
     */
    public final void run() {
        // Loop over all anthills repeated until we can eat at one, then
        // think inside the anthill, exit the anthill, and leave this
        // entire method.
        //
        while(true) {
            for(Anthill anthill : anthills) {
                if(anthill.tryToEatAtWithAnimal(this)) {
                    doWhileAtAnthill();
                    anthill.exitAnthillWithAnimal(this);
                    return; // done, so leave the whole function
                }
            }
        }
    }
    
    /**
     * This is what your Animal does while inside the anthill to
     * simulate taking time to eat an ant. The faster your
     * Animal is, the less time this will take.
     */
    private final void doWhileAtAnthill() {
         try {
             Thread.sleep((10 - speed) * 100);
         } catch(InterruptedException e) {
             System.err.println("Interrupted Animal " + getName());
         }
    }
}
