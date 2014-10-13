package offset.group8;

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
	}
	
	public List<movePair> opponentMinimizingMoves() {
		List<movePair> returnList = new LinkedList<movePair>();
		Set<movePair>playerMoves=possibleMoves(grid, playerPair);
		Set<movePair>opponentMoves = possibleMoves(grid, opponentPair);
		int bestOpponentDelta = Integer.MIN_VALUE;
		int bestPlayerDelta = Integer.MAX_VALUE;
		int startingPossibleMoves = playerMoves.size();
		int opponentStartingPossibleMoves = opponentMoves.size();
		for (movePair mp : playerMoves) {
			Point[][] newGrid = gridAfterMove(grid, mp, this.playerId);
			int opponentMovesLeft = numPossibleMoves(newGrid, opponentPair);
			int playerMovesLeft = numPossibleMoves(newGrid, playerPair);
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
	
	private static Pair[] moveForPair(Pair pr) {
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
	
	private static Point[][] gridAfterMove(Point[][] grid, movePair move, int newOwner) {
		Point[][] newGrid = new Point[grid.length][grid.length];
		for (int i = 0; i < grid.length; i++) {
			for(int j=0;j<grid.length;j++){
				Point newPoint = new Point(grid[i][j]);
				newGrid[i][j] = newPoint;	
			}
		}
		
		Point src = move.src;
		Point target = move.target;
			
		Point newSrc = newGrid[src.x][src.y];
		Point newTarget = newGrid[target.x][target.y];
		
		newTarget.value += newSrc.value;
		newTarget.owner = newOwner;
		newTarget.change = true;
		newSrc.value = 0;
		newSrc.owner = -1;
		
		return newGrid;
	}
	
	public Set<movePair> possibleMoves(Pair pr) {
		return possibleMoves(grid, pr);
	}
	
	private static Set<movePair> possibleMoves(Point[][] grid, Pair pr) {
		Set<movePair> possible = new HashSet<movePair>();
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
							possible.add(new movePair(true, currentPoint, possiblePairing));
							possible.add(new movePair(true, possiblePairing, currentPoint));
						}
					}
				}
			}
		}
		return possible;
	}
	
	private static int numPossibleMoves(Point[][] grid, Pair pr) {
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
}
