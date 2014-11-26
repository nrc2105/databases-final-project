package database;


public class EqualWeightBHDbms extends BHDbms {

	public EqualWeightBHDbms(int size) {
		super(size);
	}
	
	@Override
	protected int getNextRandom(){
		return rand.nextInt(size);
	}
	
	@Override
	public String toString(){
		return super.toString() + " DIST: EQUAL WEIGHTS";
	}
	
	

}
