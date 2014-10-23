import java.util.Collection;

/**
 * Nicholas Cummins
 * 10/31/13
 * An Aardvark is a fast Animal.
 */
public class Aardvark extends Animal {
    public Aardvark(String name, Collection<Anthill> anthills) {
        super(name, anthills);
    }

    public Aardvark(String name, Anthill anthill) {
        super(name, anthill);
    }

    protected int getDefaultSpeed() {
        return 6;
    }
}
