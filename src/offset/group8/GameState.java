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
	public int playerScore;
	public int opponentScore;
	
	static int size = 32;
	
	public GameState (Point[] oneDGrid, Pair playerPair, Pair opponentPair, int playerId, int opponentId) {
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
	
	public List<movePair> getAvailableMoves(Pair pr, int playerId) {
		int opponentId = 0;
		if (playerId == 0)
			opponentId = 1;
		List<movePair>rtn = new ArrayList<movePair>();				
		List<movePair>p1 = new ArrayList<movePair>();
		List<movePair>p2 = new ArrayList<movePair>();
		List<movePair>p3 = new ArrayList<movePair>();
		List<movePair>p4 = new ArrayList<movePair>();		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				for (int i_pr=0; i_pr<size; i_pr++) {
					for (int j_pr=0; j_pr <size; j_pr++) {
						movePair movepr = new movePair();
						movepr.move = false;
						movepr.src = grid[i][j];
						movepr.target = grid[i_pr][j_pr];
						if (validateMove(movepr, pr)) {
							movepr.move = true;						
							if(movepr.src.owner == opponentId && movepr.target.owner == opponentId)
								p1.add(movepr);
							else if(movepr.src.owner != movepr.target.owner && movepr.src.value !=1)
								p2.add(movepr);
							else if(movepr.src.owner != movepr.target.owner)
								p3.add(movepr);
							else p4.add(movepr);		
						}
					}
				}
			}
		}
		
		//Create the rtn list in decreasing order of priority
		if(p1.size()>0)
			rtn.addAll(p1);
		if(p2.size()>0)
			rtn.addAll(p2);
		if(p3.size()>0)
			rtn.addAll(p3);
		if(p4.size()>0)
			rtn.addAll(p4);
		return rtn;
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
