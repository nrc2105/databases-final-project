package database;

public class TopWeightBHDbms extends BHDbms {

	public TopWeightBHDbms(int size) {
		super(size);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected int getNextRandom(){
		return Math.abs((int)rand.nextGaussian()*size);
	}
	
	@Override
	public String toString(){
		return super.toString() + " DIST: TOP WEIGHTED";
	}


}
