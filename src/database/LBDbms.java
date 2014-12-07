package database;

import java.util.Arrays;

/*
 * The LBDms is a left branching tree structure. Beyond that
 * the only feature of note is that dummyRoot will not only 
 * create a fake root, but all internals will be dummy nodes
 * as well.
 * 
 * Author: Nicholas Cummins
 */
public class LBDbms extends Dbms {

	/**
	 * Constructs a new LBDbms (left branching Dbms)
	 * @param size
	 * @param weight
	 * @param dummyRoot indicates whether regular tree
	 * or tree with dummy internal nodes.
	 */
	public LBDbms(int size, String weight, boolean dummyRoot){
		super(size,weight,false);
		this.dummyInternal = dummyRoot;
		if(dummyInternal){
			length = size;
			this.size *= 2;
		}else{
			length = size / 2;
			if(size % 2 != 0){
				length++;
			}
		}
		initialize();
	}
	
	@Override
	public Table[] getTables(){
		if(dummyInternal){
			return Arrays.copyOfRange(super.getTables(), length, size);
		}else{
			return super.getTables();
		}
	}
	
	@Override
	protected int getNextRandom(){
		int out = super.getNextRandom();
		if(dummyInternal){
			out = (out / 2) + length;
			if(out >= size){
				return size - 1;
			} else{
				return out;
			}
		} else{
			return out;
		}
	}
	
	@Override
	protected int getParentIndex(int childIndex) {
		if(childIndex == 0){
			return -1;
		} else if(childIndex >= length){
			return childIndex % length;
		} else{
			return childIndex - 1;
		}
	}

	@Override
	public int getPathLength() {
		return length;
	}
	
	@Override
	public String toString(){
		int tableSize;
		if(dummyInternal){
			tableSize = size / 2;
		} else{
			tableSize = size;
		}
		String out = "DBMS SIZE: " + tableSize;
		if(weight.equalsIgnoreCase(EQWEIGHT)){
			out += " DIST: EQUAL WEIGHTS";
		} else if(weight.equalsIgnoreCase(TOPWEIGHT)){
			out += " DIST: TOP WEIGHTED";
		} else {
			out += " DIST: BOTTOM WEIGHTED";
		}
		if(dummyInternal){
			out += " ROOT: DUMMY";
		}else{
			out += " ROOT: TABLE";
		}
		out += " STRUCTURE: LEFT BRANCHING";
		return out;
	}

	private int length;
	private boolean dummyInternal;
}
