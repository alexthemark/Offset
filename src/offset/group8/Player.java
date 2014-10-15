package offset.group8;

import java.util.*;

import offset.group8.GameState;
import offset.sim.Pair;
import offset.sim.Point;
import offset.sim.movePair;

public class Player extends offset.sim.Player {
	private static final int MAX_MOVES_TO_CHECK = 10;
	private static final int MAX_DEPTH = 5;
	private static final int ALPHA_BETA_CUTOFF_TIME = 100;
	static int size = 32;
	static int opponent_id;
	Pair opponentPr;
	private boolean initiated = false;
	
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
		long startTime = System.currentTimeMillis();
		if (!initiated) {
			init();
		}
		movePair rtn = null;
		GameState startState = new GameState(grid, pr, pr0, id, opponent_id);		
		List<movePair> minMaxMoves = startState.opponentMinimizingMoves();	
		
		if (minMaxMoves.size() > 0) {
			rtn = minMaxMoves.get(0);
		}
		else {
			System.out.println("No moves left");
			rtn = new movePair();
			rtn.move = false;
			return rtn;
		}
		
		long endTime = System.currentTimeMillis();
		if (endTime - startTime < ALPHA_BETA_CUTOFF_TIME) {
			rtn = alphaBeta(startState, pr, pr0, minMaxMoves);
			System.out.println("Alpha beta-ing");
		}
		System.out.println("Completed the move in " + (endTime - startTime) + " milliseconds");
		return rtn;
	}
	
	public movePair alphaBeta(GameState startState, Pair pr, Pair pr0, List<movePair> movesToCheck) {
		opponentPr = pr0;
        movePair result = null;
        int resultValue = Integer.MIN_VALUE;
        int moveNo = 0;
        for (movePair move : movesToCheck) {
        	moveNo++;
        	if (moveNo > MAX_MOVES_TO_CHECK)
        		break;
        	GameState newState = new GameState(startState);
        	newState.makeMove(move, id);
            int value = minValue(newState, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
            if (value > resultValue) {
                    result = move;
                    resultValue = value;
            }
        }
        if (result == null) {
        	result = new movePair(false, new Point(0,0,0,0), new Point(0,0,0,0));
        }
        return result;
	}
	
	public int maxValue(GameState startState, double alpha, double beta, int depth) {
        if (depth == MAX_DEPTH)
            return startState.playerDeltaScore - startState.opponentDeltaScore;
        int value = Integer.MIN_VALUE;
        Set<movePair> moves = startState.possibleMoves(pr);
        if (moves.isEmpty())
        	return startState.playerDeltaScore;
        int moveNo = 0;
        for (movePair move : moves) {
        	moveNo++;
        	if (moveNo > MAX_MOVES_TO_CHECK)
        		break;
        	GameState newState = new GameState(startState);
        	newState.makeMove(move, id);
            value = Math.max(value, minValue(newState, alpha, beta, depth + 1));
            if (value >= beta)
                    return value;
            alpha = Math.max(alpha, value);
        }
        return value;
	}

	public int minValue(GameState startState, double alpha, double beta, int depth) {
		if (depth == MAX_DEPTH)
			return startState.opponentDeltaScore;
        int value = Integer.MAX_VALUE;
        Set<movePair> moves = startState.possibleMoves(opponentPr);
        if (moves.isEmpty())
        	return startState.opponentDeltaScore;
        int moveNo = 0;
        for (movePair move : moves) {
        	moveNo++;
        	if (moveNo > MAX_MOVES_TO_CHECK)
        		break;
        	GameState newState = new GameState(startState);
        	newState.makeMove(move, id);
            value = Math.min(value, maxValue(newState, alpha, beta, depth + 1));
            if (value <= alpha)
                    return value;
            beta = Math.min(beta, value);
        }
        return value;
	}
}