package database;

public class BottomWeightedBHDbms extends BHDbms {

	public BottomWeightedBHDbms(int size) {
		super(size);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected int getNextRandom(){
		return 1 - Math.abs((int)rand.nextGaussian()*size);
	}
	
	@Override
	public String toString(){
		return super.toString() + " DIST: BOTTOM WEIGHTED";
	}

}
