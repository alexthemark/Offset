package offset.group8;

import java.util.*;

import offset.sim.Pair;
import offset.sim.Point;
import offset.sim.movePair;

public class Player extends offset.sim.Player {
	static int size = 32;
	static int MAX_DEPTH = 2;
	static int opponent_id;
	static int MAX_MOVES_TO_CHECK = 10;
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

	public movePair move(Point[] grid, Pair pr, Pair pr0, ArrayList<ArrayList> history) {
		if (!initiated) {
			init();
		}
		movePair rtn = makeDecision(grid, pr, pr0);
		System.out.println(expandedNodes);
		return rtn;
	}
	
	Point[] cloneAndUpdateGrid(Point[] grid, movePair movepr, int id) {
		Point[] newGrid = new Point[grid.length];
		for (int i = 0; i < grid.length; i++) {
			newGrid[i] = new Point(grid[i]);
		}
		Point target = movepr.target;
		Point src = movepr.src;
		newGrid[target.x*size+target.y].value = newGrid[target.x*size+target.y].value * 2;
		newGrid[src.x*size+src.y].value = 0; 
		newGrid[target.x*size+target.y].owner = id;
		return newGrid;
	}
	
	public movePair makeDecision(Point[] grid, Pair pr, Pair pr0) {
		opponentPr = pr0;
        movePair result = null;
        int resultValue = Integer.MIN_VALUE;
        List<movePair> moves = getAvailableMoves(grid, pr);
        //Collections.shuffle(moves);
        int moveNo = 0;
        for (movePair move : moves) {
        	moveNo++;
        	if (moveNo > MAX_MOVES_TO_CHECK)
        		break;
        	Point[] newGrid = cloneAndUpdateGrid(grid, move, id);
            int value = minValue(newGrid, id,
                            Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
            if (value > resultValue) {
                    result = move;
                    resultValue = value;
            }
        }
        return result;
	}
	
	public int maxValue(Point[] grid, int player, double alpha, double beta, int depth) {
		expandedNodes++;
        if (noMove(grid, pr) || depth == MAX_DEPTH)
            return calculateScore(grid, player);
        int value = Integer.MIN_VALUE;
        List<movePair> moves = getAvailableMoves(grid, pr);
        Collections.shuffle(moves);
        int moveNo = 0;
        for (movePair move : moves) {
        	moveNo++;
        	if (moveNo > MAX_MOVES_TO_CHECK)
        		break;
        	Point[] newGrid = cloneAndUpdateGrid(grid, move, id);
            value = Math.max(value, minValue(newGrid, player, alpha, beta, depth + 1));
            if (value >= beta)
                    return value;
            alpha = Math.max(alpha, value);
        }
        return value;
	}

	public int minValue(Point[] grid, int player, double alpha, double beta, int depth) {
		expandedNodes++;
		if (noMove(grid, pr) || depth == MAX_DEPTH)
			return calculateScore(grid, player);
        int value = Integer.MAX_VALUE;
        List<movePair> moves = getAvailableMoves(grid, opponentPr);
        Collections.shuffle(moves);
        int moveNo = 0;
        for (movePair move : moves) {
        	moveNo++;
        	if (moveNo > MAX_MOVES_TO_CHECK)
        		break;
        	Point[] newGrid = cloneAndUpdateGrid(grid, move, opponent_id);
            value = Math.min(value, maxValue(newGrid, player, alpha, beta, depth + 1));
            if (value <= alpha)
                    return value;
            beta = Math.min(beta, value);
        }
        return value;
	}
	
	 boolean noMove(Point[] grid, Pair pr) {
		   for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					for (int i_pr=0; i_pr<size; i_pr++) {
						for (int j_pr=0; j_pr <size; j_pr++) {
							movePair movepr = new movePair(false, grid[i*size+j], grid[size*i_pr+j_pr]);
							if (validateMove(movepr, pr)) {
								return false;
							}
						}
					}
				}
		   }
		   return true;
	   }
	
	private int calculateScore(Point[] grid, int id) {
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
	
	private static List<movePair> getAvailableMoves(Point[] grid, Pair pr) {
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
						movepr.src = grid[i*size+j];
						movepr.target = grid[i_pr*size+j_pr];
						if (validateMove(movepr, pr)) {
							movepr.move = true;						
							if(movepr.src.owner == opponent_id && movepr.target.owner == opponent_id)
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