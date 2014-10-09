package offset.group8;

import offset.sim.movePair;

public class MovePair {

	private movePair moveP;
	private int numMoves;
	
	public MovePair(movePair mp, int nMoves){
		moveP=mp;
		numMoves=nMoves;
	}
	public MovePair(){
		moveP=null;
		numMoves=-1;
	}
	
	public movePair getMovePair(){
		return moveP;
	}
	
	public int getNumMoves(){
		return numMoves;
	}
}
