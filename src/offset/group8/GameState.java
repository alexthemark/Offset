package offset.group8;

import java.util.ArrayList;
import java.util.List;

import offset.sim.Pair;
import offset.sim.Point;
import offset.sim.movePair;

public class GameState {

	private Point[][] grid;
	public Pair playerPair;
	public Pair opponentPair;
	public int playerId;
	public int opponentId;
	
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
	}

	// return the best moves to maximize my player and minimize opponent's possible moves
	public List<movePair> getMinMaxMoves(Pair pr, Pair pr0) {
		//my possible moves
		ArrayList<movePair>possibleMoves=possibleMoves(grid, pr);
		//lowering opponent moves
		List<movePair> bestMoves = opponentMinimizingMoves(possibleMoves);
		return bestMoves;
	}
	
	private List<movePair> opponentMinimizingMoves(ArrayList<movePair> possibleMoves) {
		List<movePair> returnList = new ArrayList<movePair>();
		int bestOpponentDelta = Integer.MIN_VALUE;
		int bestPlayerDelta = Integer.MAX_VALUE;
		int startingPossibleMoves = possibleMoves.size();
		int opponentStartingPossibleMoves = possibleMoves(grid, opponentPair).size();
		for (movePair mp : possibleMoves) {
			Point[][] newGrid = gridAfterMove(grid, mp, this.playerId);
			int opponentMovesLeft = numPossibleMoves(newGrid, opponentPair);
			int playerMovesLeft = numPossibleMoves(newGrid, playerPair);
			int opponentDelta= opponentStartingPossibleMoves - opponentMovesLeft;
			int playerDelta = startingPossibleMoves - playerMovesLeft;
			if (opponentDelta > bestOpponentDelta || (opponentDelta == bestOpponentDelta && playerDelta < bestPlayerDelta)) {
				bestOpponentDelta = opponentDelta;
				bestPlayerDelta = playerDelta;
				mp.move = true;
				returnList.clear();
				returnList.add(mp);
				System.out.println("Opponent possible moves left: " + opponentMovesLeft);
				System.out.println("My possible moves left: " + playerMovesLeft);
			}
			else if (opponentDelta == bestOpponentDelta && playerDelta == bestPlayerDelta) {
				mp.move = true;
				returnList.add(mp);
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
			
		Point newSrc = pointAtIndex(newGrid, src.x, src.y);
		Point newTarget = pointAtIndex(newGrid, target.x, target.y);
		
		newTarget.value += newSrc.value;
		newTarget.owner = newOwner;
		newTarget.change = true;
		newSrc.value = 0;
		newSrc.owner = -1;
		
		return newGrid;
	}

	private static Point pointAtIndex(Point[][] grid, int i, int j) {
		return grid[i][j];
	}
	
	private static ArrayList<movePair> possibleMoves(Point[][] grid, Pair pr) {
		ArrayList<movePair> possible = new ArrayList<movePair>();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				Point currentPoint = pointAtIndex(grid, i, j);
				if (currentPoint.value == 0) {
					continue;
				}
				for (Pair d : moveForPair(pr)) {
					if (isValidBoardIndex(i + d.p, j + d.q)){
						Point possiblePairing = pointAtIndex(grid, i + d.p, j + d.q);
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
				Point currentPoint = pointAtIndex(grid, i, j);
				if (currentPoint.value == 0) {
					continue;
				}
				for (Pair d : moveForPair(pr)) {
					if (isValidBoardIndex(i + d.p, j + d.q)){
						Point possiblePairing = pointAtIndex(grid, i + d.p, j + d.q);
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
