import java.util.Collection;

/**
 * Nicholas Cummins
 * 10/31/13
 *
 * An Anteater is a slow Animal.
 */
public class Anteater extends Animal {
    public Anteater(String name, Collection<Anthill> anthills) {
        super(name, anthills);
    }

    public Anteater(String name, Anthill anthill) {
        super(name, anthill);
    }

    protected int getDefaultSpeed() {
        return 4;
    }
}
