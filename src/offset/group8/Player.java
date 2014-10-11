package offset.group8;

import java.util.*;

import offset.group8.GameState;
import offset.sim.Pair;
import offset.sim.Point;
import offset.sim.movePair;

public class Player extends offset.sim.Player {
	static int size = 32;
	static int opponent_id;
	Pair opponentPr;
	boolean initiated = false;
	int expandedNodes = 0;
	public Player(Pair prin, int idin) {
		super(prin, idin);
		// TODO Auto-generated constructor stub
	}

	public void init() {
		if (id == 1)
			opponent_id = 0;
		else
			opponent_id = 1;
	}

	
	public static Point[][] get2DGrid(Point[]grid){
		Point [][]grid2D = new Point[size][size];
		for (Point point : grid) {
			grid2D[point.x][point.y] = point;
		}
		return grid2D;
	}
	
	public movePair move(Point[] grid, Pair pr, Pair pr0, ArrayList<ArrayList> history) {
		long startTime = System.currentTimeMillis();
		if (!initiated) {
			init();
		}
		GameState startState = new GameState(grid, pr, pr0, id, opponent_id);		
		movePair rtn=null;
		
		List<movePair> bestMoves=startState.getMinMaxMoves(pr,pr0);
		
		if(bestMoves.size()>0) {
			rtn=bestMoves.get(0);
		}
			
		
		if(rtn==null){
			System.out.println("No moves left");
			rtn = new movePair();
			rtn.move = false;
		}
		
		
		long endTime = System.currentTimeMillis();
		System.out.println("Completed the move in " + (endTime - startTime) + " milliseconds");
		return rtn;
	}
}