package database;

public class BottomWeightBHDbms extends BHDbms {

	public BottomWeightBHDbms(int size) {
		super(size);
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
