package offset.sim;

import offset.sim.Offset;

public class movePair {

	public boolean move;
    public Point src;
    public Point target;

    public movePair() {  }

    public movePair(boolean flag, Point xx, Point yy) {
        move = flag;
        src = xx;
        target = yy;
    }
}