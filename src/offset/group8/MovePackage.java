package offset.group8;

import java.util.ArrayList;
import java.util.List;

import offset.sim.Pair;
import offset.sim.Point;
import offset.sim.movePair;

public class MovePackage {
	private int myId;
	private int opponentId;
	private Point[][] grid;
	List<movePair> doubleOpponentMoves;
	List<movePair> singleOpponentMoves;
	List<movePair> doubleMyMoves;
	List<movePair> unclaimedMoves;
	Pair myPair;
	boolean initi;
	
	public MovePackage(int myId, int opponentId, Pair myPair, Point[][] grid) {
		this.myId = myId;
		this.opponentId = opponentId;
		doubleOpponentMoves = new ArrayList<movePair>();
		singleOpponentMoves = new ArrayList<movePair>();
		doubleMyMoves = new ArrayList<movePair>();
		unclaimedMoves = new ArrayList<movePair>();
		this.myPair = myPair;
		this.grid = grid;
		initi = false;
	}
	
	public List<movePair> getPossibleMovesByPriority() {
		List<movePair> rtn = new ArrayList<movePair>();
		if (!initi) {
			initializeMoves();
		}
		return rtn;
	}
	
	public void registerChange(movePair movepr) {
		
	}
	
	private void initializeMoves() {
		for (int i = 0; i < GameState.size; i++) {
			for (int j = 0; j < GameState.size; j++) {
				Point currentPoint = grid[i][j];
				for (Pair d : moveForPair(myPair)) {
					if (isValidBoardIndex(i + d.p, j + d.q)){
						Point possiblePairing = grid[i+d.p][j+d.q];
						if (currentPoint.value == possiblePairing.value) {
							movePair movepr = new movePair();
							movepr.src = grid[i][j];
							movepr.target = grid[i+d.p][j+d.q];
							movepr.move = true;						
							if(movepr.src.owner == opponentId && movepr.target.owner == opponentId)
								doubleOpponentMoves.add(movepr);
							else if(movepr.src.owner != movepr.target.owner && movepr.src.value !=1)
								singleOpponentMoves.add(movepr);
							else if(movepr.src.owner != movepr.target.owner)
								doubleMyMoves.add(movepr);
							else unclaimedMoves.add(movepr);
						}
					}
				}
			}
		}
	}
	
	public static boolean isValidBoardIndex(int i, int j) {
		return !(i < 0 || i >= GameState.size || j < 0 || j >= GameState.size);
	}
	
	public static boolean isValidBoardIndex(Point p) {
		return isValidBoardIndex(p.x, p.y);
	}
	
	public static Pair[] moveForPair(Pair pr) {
		Pair[] moves = new Pair[8];
		moves[0] = new Pair(pr); 
		moves[1] = new Pair(pr.p, -pr.q);
		moves[2] = new Pair(-pr.p, -pr.q);
		moves[3] = new Pair(-pr.p, -pr.q);
		moves[4] = new Pair(pr.q, pr.p);
		moves[5] = new Pair(-pr.q, pr.p);
		moves[6] = new Pair(pr.q, -pr.p);
		moves[7] = new Pair(-pr.q, -pr.p);
		return moves;
	}
	
	static boolean validateMove(movePair movepr, Pair pr) {
    	Point src = movepr.src;
    	Point target = movepr.target;
    	boolean rightposition = false;
    	if (Math.abs(target.x-src.x)==Math.abs(pr.p) && Math.abs(target.y-src.y)==Math.abs(pr.q)) {
    		rightposition = true;
    	}
        if (rightposition  && src.value == target.value && src.value >0) {
        	return true;
        }
        else {
        	return false;
        }
    }
	
}
