package offset.group8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import offset.sim.Pair;
import offset.sim.Point;
import offset.sim.movePair;

public class MovePackage {
	private int myId;
	private int opponentId;
	private Point[][] grid;
	Map<Point, movePair> doubleOpponentMoves;
	Map<Point, movePair> singleOpponentMoves;
	Map<Point, movePair> doubleMyMoves;
	Map<Point, movePair> unclaimedMoves;
	Pair myPair;
	boolean initi;
	
	public MovePackage(int myId, int opponentId, Pair myPair, Point[][] grid) {
		this.myId = myId;
		this.opponentId = opponentId;
		doubleOpponentMoves = new HashMap<Point, movePair>();
		singleOpponentMoves = new HashMap<Point, movePair>();
		doubleMyMoves = new HashMap<Point, movePair>();
		unclaimedMoves = new HashMap<Point, movePair>();
		this.myPair = myPair;
		this.grid = grid;
		initi = false;
	}
	
	public List<movePair> getPossibleMovesByPriority(int numberToReturn) {
		List<movePair> rtn = new ArrayList<movePair>();
		if (!initi) {
			initializeMoves();
		}
		rtn.addAll(doubleOpponentMoves.values());
		rtn.addAll(singleOpponentMoves.values());
		rtn.addAll(doubleMyMoves.values());
		rtn.addAll(unclaimedMoves.values());
		return rtn;
	}
	
	public int getNumberOfRemainingMoves() {
		return doubleOpponentMoves.size() + singleOpponentMoves.size() + doubleMyMoves.size() + unclaimedMoves.size();
	}
	
	public void registerChange(movePair oldMove) {
		ArrayList<Map<Point, movePair>> moves = new ArrayList<Map<Point, movePair>>();
		moves.add(doubleOpponentMoves);
		moves.add(singleOpponentMoves);
		moves.add(doubleMyMoves);
		moves.add(unclaimedMoves);
		Point[] changedPoints = new Point[2];
		changedPoints[0] = oldMove.src;
		changedPoints[1] = oldMove.target;
		for (Map<Point, movePair> moveMap : moves) {
			for (Point point : changedPoints) {
				if (moveMap.containsKey(point)) {
					moveMap.remove(point);
					int i = point.x;
					int j = point.y;
					Point currentPoint = grid[i][j];
					for (Pair d : moveForPair(myPair)) {
						if (isValidBoardIndex(i + d.p, j + d.q)){
							Point possiblePairing = grid[i+d.p][j+d.q];
							if (currentPoint.value == possiblePairing.value) {
								movePair movepr = new movePair();
								movepr.src = grid[i][j];
								movepr.target = grid[i+d.p][j+d.q];
								movepr.move = true;						
								if(movepr.src.owner == opponentId && movepr.target.owner == opponentId) {
									doubleOpponentMoves.put(currentPoint, movepr);
									doubleOpponentMoves.put(possiblePairing, movepr);
								}
								else if(movepr.src.owner != movepr.target.owner && movepr.src.value !=1) {
									singleOpponentMoves.put(currentPoint, movepr);
									singleOpponentMoves.put(possiblePairing, movepr);
								}
								else if(movepr.src.owner != movepr.target.owner) {
									doubleMyMoves.put(currentPoint, movepr);
									doubleMyMoves.put(possiblePairing, movepr);
								}
								else {
									unclaimedMoves.put(currentPoint, movepr);
									unclaimedMoves.put(possiblePairing, movepr);
								}
							}
						}
					}
				}
			}
		}
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
							if(movepr.src.owner == opponentId && movepr.target.owner == opponentId) {
								doubleOpponentMoves.put(currentPoint, movepr);
								doubleOpponentMoves.put(possiblePairing, movepr);
							}
							else if(movepr.src.owner != movepr.target.owner && movepr.src.value !=1) {
								singleOpponentMoves.put(currentPoint, movepr);
								singleOpponentMoves.put(possiblePairing, movepr);
							}
							else if(movepr.src.owner != movepr.target.owner) {
								doubleMyMoves.put(currentPoint, movepr);
								doubleMyMoves.put(possiblePairing, movepr);
							}
							else {
								unclaimedMoves.put(currentPoint, movepr);
								unclaimedMoves.put(possiblePairing, movepr);
							}
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
