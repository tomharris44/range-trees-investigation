import java.util.ArrayList;

/** RT node
 * @author tomharris44
 *
 */
public class RangeTreeNode{
	
	private RangeTreeNode parent;

	private RangeTreeNode right;
	private RangeTreeNode left;
	
	private RangeTreeNode sec_tree_root;
	
	private int X;
	private int Y;
	
	private ArrayList<CoordinateFC> pointers;

	public RangeTreeNode(int X, int Y, RangeTreeNode parent) {
		this.setX(X);
		this.setY(Y);
		this.setParent(parent);
		this.setRight(null);
		this.setLeft(null);
		this.setSec_tree_root(null);
		this.setPointers(null);
	}

	public RangeTreeNode getParent() {
		return parent;
	}

	public void setParent(RangeTreeNode parent) {
		this.parent = parent;
	}
	
	public RangeTreeNode getRight() {
		return right;
	}

	public void setRight(RangeTreeNode right) {
		this.right = right;
	}

	public RangeTreeNode getLeft() {
		return left;
	}

	public void setLeft(RangeTreeNode left) {
		this.left = left;
	}

	public int getX() {
		return X;
	}

	public void setX(int X) {
		this.X = X;
	}

	public int getY() {
		return Y;
	}

	public void setY(int y) {
		Y = y;
	}
	
	public ArrayList<RangeTreeNode> getSubTree(){
		ArrayList<RangeTreeNode> subtree = new ArrayList<RangeTreeNode>();
		
		if (this.getLeft() != null) {
			subtree.add(this.getLeft());
			for (RangeTreeNode s : this.getLeft().getSubTree()) {
				subtree.add(s);
			}
		} 
		if (this.getRight() != null) {
			subtree.add(this.getRight());
			for (RangeTreeNode s : this.getRight().getSubTree()) {
				subtree.add(s);
			}
		}
		return subtree;
	}

	public RangeTreeNode getSec_tree_root() {
		return sec_tree_root;
	}

	public void setSec_tree_root(RangeTreeNode sec_tree_root) {
		this.sec_tree_root = sec_tree_root;
	}
	
	public ArrayList<CoordinateFC> getPointers() {
		return pointers;
	}

	public void setPointers(ArrayList<CoordinateFC> sorted) {
		this.pointers = sorted;
	}

}
