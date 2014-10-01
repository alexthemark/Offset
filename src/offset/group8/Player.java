package offset.group8;

import java.util.*;

import offset.sim.Pair;
import offset.sim.Point;
import offset.sim.movePair;

public class Player extends offset.sim.Player {
	static int size = 32;
	static int MAX_DEPTH = 2;
	static int opponent_id;
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

	public movePair move(Point[] grid, Pair pr, Pair pr0, ArrayList<ArrayList> history) {
		return makeDecision(grid, pr, pr0);
	}
	
	MemoryEfficientPoint[] cloneAndUpdateGrid(MemoryEfficientPoint[] grid, movePair movepr, int id) {
		MemoryEfficientPoint[] newGrid = grid.clone();
		Point target = movepr.target;
		newGrid[target.x*size+target.y].value = (byte) (newGrid[target.x*size+target.y].value * 2);
		newGrid[target.x*size+target.y].owner = (byte) id;
		return newGrid;
	}
	
	MemoryEfficientPoint[] cloneAndUpdateGrid(Point[] grid, movePair movepr, int id) {
		MemoryEfficientPoint[] newGrid = new MemoryEfficientPoint[grid.length];
		for (int i = 0; i < grid.length; i++) {
			newGrid[i] = new MemoryEfficientPoint(grid[i]);
		}
		Point target = movepr.target;
		newGrid[target.x*size+target.y].value = (byte) (newGrid[target.x*size+target.y].value * 2);
		newGrid[target.x*size+target.y].owner = (byte) id;
		return newGrid;
	}
	
	public movePair makeDecision(Point[] grid, Pair pr, Pair pr0) {
        movePair result = null;
        int resultValue = Integer.MIN_VALUE;
        List<movePair> moves = getAvailableMoves(grid, pr);
        int moveNo = 0;
        for (movePair move : moves) {
        	moveNo++;
        	if (moveNo > 10)
        		break;
        	MemoryEfficientPoint[] newGrid = cloneAndUpdateGrid(grid, move, id);
            int value = minValue(newGrid, id,
                            Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
            if (value > resultValue) {
                    result = move;
                    resultValue = value;
            }
        }
        return result;
}
	
	public int maxValue(MemoryEfficientPoint[] grid, int player, double alpha, double beta, int depth) {
        if (noMove(grid, pr) || depth == MAX_DEPTH)
            return calculateScore(grid, player);
        int value = Integer.MIN_VALUE;
        List<movePair> moves = getAvailableMoves(grid, pr);
        int moveNo = 0;
        for (movePair move : moves) {
        	moveNo++;
        	if (moveNo > 10)
        		break;
        	MemoryEfficientPoint[] newGrid = cloneAndUpdateGrid(grid, move, id);
            value = Math.max(value, minValue(newGrid, player, alpha, beta, depth + 1));
            if (value >= beta)
                    return value;
            alpha = Math.max(alpha, value);
        }
        return value;
}

	public int minValue(MemoryEfficientPoint[] grid, int player, double alpha, double beta, int depth) {
			if (noMove(grid, pr) || depth == MAX_DEPTH)
				return calculateScore(grid, player);
	        int value = Integer.MAX_VALUE;
	        List<movePair> moves = getAvailableMoves(grid, pr);
	        int moveNo = 0;
	        for (movePair move : moves) {
	        	moveNo++;
	        	if (moveNo > 10)
	        		break;
	        	MemoryEfficientPoint[] newGrid = cloneAndUpdateGrid(grid, move, opponent_id);
                value = Math.min(value, maxValue(newGrid, player, alpha, beta, depth + 1));
                if (value <= alpha)
                        return value;
                beta = Math.min(beta, value);
	        }
	        return value;
	}
	
	 boolean noMove(MemoryEfficientPoint[] grid, Pair pr) {
		   for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					for (int i_pr=0; i_pr<size; i_pr++) {
					for (int j_pr=0; j_pr <size; j_pr++) {
						movePair movepr = new movePair(false, grid[i*size+j].toPoint(), grid[size*i_pr+j_pr].toPoint());
						if (validateMove(movepr, pr)) {
							return false;
						}
					}
					}
				}
		   }
		   return true;
	   }
	
	private int calculateScore(MemoryEfficientPoint[] grid, int id) {
    	int score =0;
    	for (int i=0; i<size; i++) {
    		for (int j =0; j<size; j++) {
    			if (grid[i*size+j].owner ==id) {
    				score = score+grid[i*size+j].value;
    			}
    		}
    	}
    	return score;
    }
	
	private static List<movePair> getAvailableMoves(MemoryEfficientPoint[] grid, Pair pr) {
		List<movePair>rtn = new ArrayList<movePair>();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				for (int i_pr=0; i_pr<size; i_pr++) {
					for (int j_pr=0; j_pr <size; j_pr++) {
						movePair movepr = new movePair();
						movepr.move = false;
						movepr.src = grid[i*size+j].toPoint();
						movepr.target = grid[i_pr*size+j_pr].toPoint();
						if (validateMove(movepr, pr)) {
							movepr.move = true;
							rtn.add(movepr);
						}
					}
				}
			}
		}
		return rtn;
	}
	
	private static List<movePair> getAvailableMoves(Point[] grid, Pair pr) {
		List<movePair>rtn = new ArrayList<movePair>();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				for (int i_pr=0; i_pr<size; i_pr++) {
					for (int j_pr=0; j_pr <size; j_pr++) {
						movePair movepr = new movePair();
						movepr.move = false;
						movepr.src = grid[i*size+j];
						movepr.target = grid[i_pr*size+j_pr];
						if (validateMove(movepr, pr)) {
							movepr.move = true;
							rtn.add(movepr);
						}
					}
				}
			}
		}
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