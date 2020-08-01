
/** Coordinate in 2d space.
 * @author tomharris44
 *
 */
public class Coordinate {
	private int x;
	private int y;
	private int id;

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
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

	
}
