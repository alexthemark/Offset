package offset.group8;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import offset.sim.Pair;
import offset.sim.Point;
import offset.sim.movePair;

public class GameState {

	private Point[][] grid;
	public Pair playerPair;
	public Pair opponentPair;
	public int playerId;
	public int opponentId;
	public int playerDeltaScore;
	public int opponentDeltaScore;
	private int numberOfPlayerMovesRemaining;
	private int numberOfOpponentMovesRemaining;
	
	public static int size = 32;
	
	public GameState (Point[] oneDGrid, Pair playerPair, Pair opponentPair, int playerId, int opponentId) {
		grid = new Point[size][size];
		for (Point point : oneDGrid) {
			grid[point.x][point.y] = point;
		}
		this.playerPair = playerPair;
		this.opponentPair = opponentPair;
		this.playerId = playerId;
		this.opponentId = opponentId;
	}
	
	public GameState(GameState oldGame) {
		grid = new Point[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				grid[i][j] = new Point(oldGame.grid[i][j]);
			}
		}
		this.playerPair = oldGame.playerPair;
		this.opponentPair = oldGame.opponentPair;
		this.playerId = oldGame.playerId;
		this.opponentId = oldGame.playerId;
		this.playerDeltaScore = oldGame.playerDeltaScore;
		this.opponentDeltaScore = oldGame.opponentDeltaScore;
		this.numberOfPlayerMovesRemaining = oldGame.numberOfPlayerMovesRemaining;
		this.numberOfOpponentMovesRemaining = oldGame.numberOfOpponentMovesRemaining;
	}
	
	public void makeMove(movePair movepr, int playerId) {
		Point target = movepr.target;
		Point src = movepr.src;
		if (src.owner != playerId && target.owner != playerId) {
			playerDeltaScore += src.value * 2;
			opponentDeltaScore -= src.value * 2;
		}
		else if (src.owner != playerId || target.owner != playerId) {
			playerDeltaScore += src.value;
			opponentDeltaScore -= src.value;
		}
		grid[target.x][target.y].value = grid[target.x][target.y].value + grid[src.x][src.y].value;
		grid[src.x][src.y].value = 0; 
		grid[target.x][target.y].owner = playerId;
		grid[src.x][src.y].owner = -1;
		numberOfPlayerMovesRemaining += numMovesDelta(movepr, playerPair);
		numberOfOpponentMovesRemaining += numMovesDelta(movepr, opponentPair);
	}

	public List<movePair> opponentMinimizingMoves() {
		List<movePair> returnList = new LinkedList<movePair>();
		Set<movePair>playerMoves=possibleMoves(playerPair);
		Set<movePair>opponentMoves = possibleMoves(opponentPair);
		int bestOpponentDelta = Integer.MIN_VALUE;
		int bestPlayerDelta = Integer.MAX_VALUE;
		int startingPossibleMoves = playerMoves.size();
		int opponentStartingPossibleMoves = opponentMoves.size();
		for (movePair mp : playerMoves) {
			GameState newState = new GameState(this);
			newState.makeMove(mp, playerId);
			int opponentMovesLeft = newState.numPossibleMoves(opponentPair);
			int playerMovesLeft = newState.numPossibleMoves(playerPair);
			int opponentDelta= opponentStartingPossibleMoves - opponentMovesLeft;
			int playerDelta = startingPossibleMoves - playerMovesLeft;
			// minimize the number of moves our opponent has, then maximize our own moves
			if (opponentDelta > bestOpponentDelta || (opponentDelta == bestOpponentDelta && playerDelta < bestPlayerDelta)) {
				bestOpponentDelta = opponentDelta;
				bestPlayerDelta = playerDelta;
				mp.move = true;
				returnList.clear();
				returnList.add(0, mp);
			}
			// if we can gain points while minimizing, might as well do that. 
			else if (opponentDelta == bestOpponentDelta && playerDelta == bestPlayerDelta) {
				mp.move = true;
				returnList.add(0, mp);
			}
		}
		return returnList;
	}
	
	private static boolean isValidBoardIndex(int i, int j) {
		return !(i < 0 || i >= GameState.size || j < 0 || j >= GameState.size);
	}
	
	/* Method for initial calculation of moves comes directly from group3's in class suggestions */
	public Set<movePair> possibleMoves(Pair pr) {
		Set<movePair> rtn = new HashSet<movePair>();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				Point currentPoint = grid[i][j];
				if (currentPoint.value == 0) {
					continue;
				}
				for (Pair d : moveForPair(pr)) {
					if (isValidBoardIndex(i + d.p, j + d.q)){
						Point possiblePairing = grid[i + d.p][j + d.q];
						if (currentPoint.value == possiblePairing.value) {
							rtn.add(new movePair(true, currentPoint, possiblePairing));
							rtn.add(new movePair(true, possiblePairing, currentPoint));
						}
					}
				}
			}
		}
		return rtn;
	}
	
	public int numPossibleMoves(Pair pr) {
		int counter = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				Point currentPoint = grid[i][j];
				if (currentPoint.value == 0) {
					continue;
				}
				for (Pair d : moveForPair(pr)) {
					if (isValidBoardIndex(i + d.p, j + d.q)){
						Point possiblePairing = grid[i + d.p][j + d.q];
						if (currentPoint.value == possiblePairing.value) {
							counter+=2;
						}
					}
				}
			}
		}
		return counter;
	}
	
	/* Method for initial calculation of moves comes directly from group3's in class suggestions */
	private static Pair[] moveForPair(Pair pr) {
		Pair[] moves = new Pair[4];
		moves[0] = new Pair(pr.p, pr.q);
		moves[1] = new Pair(pr.p, -pr.q);
		moves[2] = new Pair(pr.q, pr.p);
		moves[3] = new Pair(pr.q, -pr.p);
		return moves;
	}
	
	/*
	 * Motivation for this method of calculating move deltas came from Group 4's in class discussion on 10/13/2014
	 */
	private int numMovesDelta(movePair movepr, Pair pr) {
		// start off assuming that we've lost the maximum number of moves
		int counter = -32;
		//check around the source
		List<Point> neighbors = neighbors(movepr.src, pr);
		for (Point p : neighbors) {
			if (p.value == grid[movepr.src.x][movepr.src.y].value && p.value > 0)
				counter += 2;
		}
		neighbors = neighbors(movepr.target, pr);
		
		for (Point p : neighbors) {
			if (p.value == grid[movepr.target.x][movepr.target.y].value && p.value > 0)
				counter += 2;
		}
		return counter;
	}
	
	/*
	 * Get the points that are a certain pair away from a point
	 */
	private List<Point> neighbors(Point src, Pair pr) {
		ArrayList<Point> rtn = new ArrayList<Point>();
		for (Pair d : moveForPair(pr)) {
			if (isValidBoardIndex(src.x + d.p, src.y + d.q)){
				rtn.add(grid[src.x + d.p][src.y + d.q]);
			}
		}
		return rtn;
	}
}
