package offset.group8;

import java.util.ArrayList;
import java.util.Comparator;
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
		movePair next=new movePair();
		
		Comparator<MovePair>movePairComparator=new Comparator<MovePair>(){
			public int compare(MovePair p1, MovePair p2){
						return p2.getNumMoves()-p1.getNumMoves();
			}
		};
		PriorityQueue<MovePair>maxMovesHeap=new PriorityQueue<>(10, movePairComparator);
			
		int playerMaxMoves = Integer.MIN_VALUE;
		while(!minimizingOpponentMoves.isEmpty()){
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
			int numOpponentMoves=possibleMoves(newGrid, pr0).size();
			if (numOpponentMoves < leastOpponentMove||numOpponentMoves==0){
				leastOpponentMove = numOpponentMoves;
				next = mp;
				next.move = true;
				minimizingMovesHeap.add(new MovePair(next,numOpponentMoves));
			} 
		}
		return minimizingMovesHeap;
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
}
