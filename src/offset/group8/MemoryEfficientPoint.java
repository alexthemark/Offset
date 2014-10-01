package offset.group8;

import offset.sim.Point;

public class MemoryEfficientPoint {
    public byte x;
    public byte y;
    public byte value;
    public byte owner;
    public boolean change;

    public MemoryEfficientPoint() { x = 0; y = 0; value =1; owner = -1;}

    public MemoryEfficientPoint(int xx, int yy, int va, int ow) {
        x = (byte) xx;
        y = (byte) yy;
        this.value = (byte) va;
        this.owner = (byte) ow;
        this.change = false;
    }
    
    public MemoryEfficientPoint(Point point) {
		x = (byte) point.x;
		y = (byte) point.y;
		value = (byte) point.value;
		owner = (byte) point.owner;
		change = point.change;
	}

	public Point toPoint() {
    	return new Point(x, y, value, owner);
    }
}
