
/** Coordinate variant for FC
 * @author tomharris44
 *
 */
public class CoordinateFC {
	
	private int x;
	private int y;
	private int id;
	
	private CoordinateFC coordLeft;
	private CoordinateFC coordRight;

	public CoordinateFC(int x, int y) {
		this.x = x;
		this.y = y;
		this.coordLeft = null;
		this.coordRight = null;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        CoordinateFC comp = (CoordinateFC)obj;

        if (comp.getX() != this.getX() || comp.getY() != this.getY()) {
        	return false;
        }
        
        return true;
    }

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CoordinateFC getCoordLeft() {
		return coordLeft;
	}

	public void setCoordLeft(CoordinateFC coordLeft) {
		this.coordLeft = coordLeft;
	}

	public CoordinateFC getCoordRight() {
		return coordRight;
	}

	public void setCoordRight(CoordinateFC coordRight) {
		this.coordRight = coordRight;
	}
	

	
}
