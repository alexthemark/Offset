package offset.group8;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import offset.sim.Pair;
import offset.sim.Point;
import offset.sim.movePair;

public class GameState {

	private Point[][] grid;
	public Pair playerPair;
	public Pair opponentPair;
	public int playerId;
	public int opponentId;
	public int playerScore;
	public int opponentScore;
	public MovePackage playerMoves;
	public MovePackage opponentMoves;
	private Point lastPoint;
	
	public static int size = 32;
	
	public GameState (Point[] oneDGrid, Pair playerPair, Pair opponentPair, int playerId, int opponentId, movePair lastMove) {
		grid = new Point[size][size];
		for (Point point : oneDGrid) {
			grid[point.x][point.y] = point;
		}
		this.playerPair = playerPair;
		this.opponentPair = opponentPair;
		this.playerId = playerId;
		this.opponentId = opponentId;
		this.playerScore = calculateScore(playerId);
		this.opponentScore = calculateScore(opponentId);
		playerMoves = new MovePackage(playerId, opponentId, playerPair, grid);
		opponentMoves = new MovePackage(opponentId, playerId, opponentPair, grid);
		if (lastMove != null)
			lastPoint = lastMove.target;
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
		this.playerMoves = oldGame.playerMoves.clone();
		this.opponentMoves = oldGame.opponentMoves.clone();
		playerMoves.grid = grid;
		opponentMoves.grid = grid;
		lastPoint = null;
	}
	
	public void makeMove(movePair movepr, int playerId) {
		Point target = movepr.target;
		Point src = movepr.src;
		if (src.owner != playerId && target.owner != playerId) {
			playerScore += src.value * 2;
			opponentScore -= src.value * 2;
		}
		else if (src.owner != playerId || target.owner != playerId) {
			playerScore += src.value;
			opponentScore -= src.value;
		}
		grid[target.x][target.y].value = grid[target.x][target.y].value + grid[src.x][src.y].value;
		grid[src.x][src.y].value = 0; 
		grid[target.x][target.y].owner = playerId;
		grid[src.x][src.y].owner = -1;
		playerMoves.registerChange(movepr);
		opponentMoves.registerChange(movepr);
		lastPoint = movepr.target;
	}
	
	private int calculateScore(int id) {
    	int score =0;
    	for (int i=0; i<size; i++) {
    		for (int j =0; j<size; j++) {
    			if (grid[i][j].owner ==id) {
    				score = score+grid[i][j].value;
    			}
    		}
    	}
    	return score;
    }
	
	public static int opponentPossibleMoves(Point[][] grid, Pair pr) {
		int availableMoves=0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				Point currentPoint = pointAtIndex(grid, i, j);
				if (currentPoint.value == 0) {
					continue;
				}
				for (Pair d : MovePackage.moveForPair(pr)) {
					if (MovePackage.isValidBoardIndex(i + d.p, j + d.q)){
						Point possiblePairing = pointAtIndex(grid, i + d.p, j + d.q);
						if (currentPoint.value == possiblePairing.value) {
							availableMoves+=2;
						}
					}
					
				}
			}
		}		
		return availableMoves;
	}
	
	
	
	public static Point[][] gridAfterMove(Point[][] grid, movePair move, int newOwner) {
		Point[][] newGrid = new Point[grid.length][grid.length];
		for (int i = 0; i < grid.length; i++) {
			for(int j=0;j<grid.length;j++){
				Point newPoint = new Point();
				newPoint.change = grid[i][j].change;
				newPoint.owner = grid[i][j].owner;
				newPoint.value = grid[i][j].value;
				newPoint.x = grid[i][j].x;
				newPoint.y = grid[i][j].y;
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

	public static Point pointAtIndex(Point[][] grid, int i, int j) {
		return grid[i][j];
	}
	
	
	ArrayList<movePair> possibleMoves(Point[][] grid, Pair pr) {
		ArrayList<movePair> possible = new ArrayList<movePair>();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				Point currentPoint = pointAtIndex(grid, i, j);
				if (currentPoint.value == 0) {
					continue;
				}
				for (Pair d : MovePackage.moveForPair(pr)) {
					if (MovePackage.isValidBoardIndex(i + d.p, j + d.q)){
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

	// return best 10 moves maximize my player and minimize opponent
	public ArrayList<movePair> getMinMaxMoves(Pair pr, Pair pr0) {
		// TODO Auto-generated method stub
		ArrayList<movePair>bestMoves=new ArrayList<movePair>();
		//my possible moves
		ArrayList<movePair>possibleMoves=possibleMoves(grid, pr);
		//lowering opponent moves
		PriorityQueue<MovePair>minimizingOpponentMoves=opponentMinimizingMoves(grid,pr0,possibleMoves);
		//max Player move
		PriorityQueue<MovePair>maxPlayerMove=maxMyMove(grid,pr,minimizingOpponentMoves);
		while(!maxPlayerMove.isEmpty()){
			bestMoves.add(maxPlayerMove.poll().getMovePair());
		}
		return bestMoves;
	}

	private PriorityQueue<MovePair> maxMyMove(Point[][] grid2, Pair pr,
			PriorityQueue<MovePair> minimizingOpponentMoves) {
		// TODO Auto-generated method stub
		movePair next=new movePair();
		
		
		Comparator<MovePair>movePairComparator=new Comparator<MovePair>(){
			public int compare(MovePair p1, MovePair p2){
						return p2.getNumMoves()-p1.getNumMoves();
			}
		};
		//30 best minimizing moves
		PriorityQueue<MovePair>maxMovesHeap=new PriorityQueue<>(10, movePairComparator);
			
		int playerMaxMoves = Integer.MIN_VALUE;
		while(!minimizingOpponentMoves.isEmpty()){
		//for (movePair mp : possibleMoves) {
			movePair mp=minimizingOpponentMoves.poll().getMovePair();
			Point[][] newGrid = gridAfterMove(grid, mp, this.playerId);
			int numMoves=possibleMoves(newGrid, pr).size();
			if (numMoves > playerMaxMoves){
				playerMaxMoves = numMoves;
				next = mp;
				next.move = true;
				maxMovesHeap.add(new MovePair(next,playerMaxMoves));
			} 
		}
		
		
		return maxMovesHeap;
		
	}

	private PriorityQueue<MovePair> opponentMinimizingMoves(Point[][] grid2,
			Pair pr0, ArrayList<movePair> possibleMoves) {
		
		movePair next=new movePair();
		
		Comparator<MovePair>movePairComparator=new Comparator<MovePair>(){
			public int compare(MovePair p1, MovePair p2){
						return p1.getNumMoves()-p2.getNumMoves();
			}
		};
		//30 best minimizing moves
		PriorityQueue<MovePair>minimizingMovesHeap=new PriorityQueue<>(30, movePairComparator);
			
		int leastOpponentMove = Integer.MAX_VALUE;
		for (movePair mp : possibleMoves) {
			Point[][] newGrid = gridAfterMove(grid, mp, this.playerId);
			int numOpponentMoves=opponentPossibleMoves(newGrid, pr0);
			if (numOpponentMoves < leastOpponentMove||numOpponentMoves==0){
				leastOpponentMove = numOpponentMoves;
				next = mp;
				next.move = true;
				minimizingMovesHeap.add(new MovePair(next,numOpponentMoves));
			} 
		}
		return minimizingMovesHeap;
	}
}
