import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** RT - FC querying algorithm
 * @author tomharris44
 *
 */
public class RangeTreeFC {
	
	private RangeTreeNode root;
	private ArrayList<CoordinateFC> coordsy;


	public RangeTreeFC(ArrayList<Coordinate> coords) {
		
		// sort data set on x coordinates
		// break ties with y, then id
		Collections.sort(coords, Comparator.comparing(Coordinate::getX).thenComparing(Coordinate::getY).thenComparing(Coordinate::getId));
		
		ArrayList<Coordinate> cy = (ArrayList<Coordinate>)coords.clone();
		
		// sort data set on y coordinates and store in new list
		Collections.sort(cy, Comparator.comparing(Coordinate::getY));
		
		// convert coordinates to CoordinateFC variant
		this.coordsy = this.convertCoords(cy);
		
		// find median for root definition
		int median = coords.size() / 2;
		
		// define root
		this.root = new RangeTreeNode(coords.get(median).getX(),coords.get(median).getY(),null);
		
		// generate WB-BST on root
		generate_wb_bst(this.root, coords.subList(0, median), coords.subList(median+1, coords.size()));
		
		
		
	}

	/** Generate WB-BST on given root
	 * @param root: root of tree
	 * @param L: coordinates for left side of WB-BST
	 * @param R: coordinates for right side of WB-BST
	 */
	private void generate_wb_bst(RangeTreeNode root, List<Coordinate> L, List<Coordinate> R) {
		
		// generate using standard WB-BST creation algorithm
		// include exceptions for 1-2 node lengths for L/R side
		
		// generate left side
		if (L.size() == 1){
			RangeTreeNode lrtn = new RangeTreeNode(L.get(0).getX(),L.get(0).getY(),root);
			root.setLeft(lrtn);
			
		} else if(L.size() == 2){
			RangeTreeNode lrtn = new RangeTreeNode(L.get(1).getX(),L.get(1).getY(),root);
			
			root.setLeft(lrtn);
			
			RangeTreeNode lrtn_final = new RangeTreeNode(L.get(0).getX(),L.get(0).getY(),lrtn);
			
			lrtn.setLeft(lrtn_final);
		}else if(L.size() > 2){
		
			int median_left = L.size() / 2;
			
			RangeTreeNode lrtn = new RangeTreeNode(L.get(median_left).getX(),L.get(median_left).getY(),root);
			
			root.setLeft(lrtn);
			
			generate_wb_bst(lrtn, L.subList(0, median_left), L.subList(median_left+1, L.size()));
		}
		
		// generate right side
		if (R.size() == 1){
			RangeTreeNode rrtn = new RangeTreeNode(R.get(0).getX(),R.get(0).getY(),root);
			root.setRight(rrtn);
			
		} else if(R.size() == 2){
			RangeTreeNode rrtn = new RangeTreeNode(R.get(1).getX(),R.get(1).getY(),root);
			
			root.setRight(rrtn);
			
			RangeTreeNode rrtn_final = new RangeTreeNode(R.get(0).getX(),R.get(0).getY(),rrtn);
			
			rrtn.setLeft(rrtn_final);
		}else if(R.size() > 2){
		
			int median_right = R.size() / 2;
			
			RangeTreeNode rrtn = new RangeTreeNode(R.get(median_right).getX(),R.get(median_right).getY(),root);
			
			root.setRight(rrtn);
			
			generate_wb_bst(rrtn, R.subList(0, median_right), R.subList(median_right+1, R.size()));
		}
		
	}
	
	/** Generate successor pointers for internal nodes on y
	 * @param node: root of tree for internal pointer generation
	 * @param sorted: sorted list of nodes on y
	 */
	public void generate_internal_pointers(RangeTreeNode node, ArrayList<CoordinateFC> sorted) {
		
		if (sorted == null) {
			sorted = this.coordsy;
		}
		
		// generate sorted split as per sorted algorithm
		ArrayList<ArrayList<CoordinateFC>> lr = get_sorted_y(node.getX(), sorted);
		
		ArrayList<CoordinateFC> left = new ArrayList<CoordinateFC>();
		for (CoordinateFC c: lr.get(0)) {
			left.add( new CoordinateFC(c.getX(),c.getY()));
		}
		
		ArrayList<CoordinateFC> right = new ArrayList<CoordinateFC>();
		for (CoordinateFC c: lr.get(1)) {
			right.add( new CoordinateFC(c.getX(),c.getY()));
		}
		
		// recursively call function on left subtree
		if (node.getLeft() != null) {
			generate_internal_pointers(node.getLeft(),left);
		}
		
		// recursively call function on right subtree
		if (node.getRight() != null) {
			generate_internal_pointers(node.getRight(),right);
		}
		
		// generate internal pointers on Y as per FC construction method
		if (sorted.size() > 1) {
			
			int p = 0;
			int q = 0;
			
			while (p < sorted.size() && q < left.size()) {
				if (sorted.get(p).getY() <= left.get(q).getY()) {
					sorted.get(p).setCoordLeft(left.get(q));
					p ++;
				} else {
					q++;	
				}
			}
			
			p = 0;
			q = 0;
			
			while (p < sorted.size() && q < right.size()) {
				if (sorted.get(p).getY() <= right.get(q).getY()) {
					sorted.get(p).setCoordRight(right.get(q));
					p ++;
				} else {
					q++;	
				}
			}
		}
		
		// set relevant pointers to node attribute
		node.setPointers(sorted);
		
	}
	
	/** Convert Coordinate set to CoordinateFC variant
	 * @param coordsy2: coordinate set
	 * @return CoordinateFC set
	 */
	private ArrayList<CoordinateFC> convertCoords(ArrayList<Coordinate> coordsy2) {
		
		ArrayList<CoordinateFC> fc = new ArrayList<CoordinateFC>();
		
		for (Coordinate c: coordsy2) {
			fc.add(new CoordinateFC(c.getX(),c.getY()));
		}
		
		return fc;
	}

	/**
	 * @param nodeX: splitting node
	 * @param sorted: sorted list on y
	 * @return left/right lists
	 */
	public ArrayList<ArrayList<CoordinateFC>> get_sorted_y(int nodeX, ArrayList<CoordinateFC> sorted){
		
		ArrayList<CoordinateFC> left = new ArrayList<CoordinateFC>();
		ArrayList<CoordinateFC> right = new ArrayList<CoordinateFC>();
		
		for (CoordinateFC p : sorted) {
			if (p.getX() < nodeX) {
				left.add(p);
			} else if(p.getX() > nodeX) {
				right.add(p);
			}
		}
		ArrayList<ArrayList<CoordinateFC>> lr = new ArrayList<ArrayList<CoordinateFC>>();
		lr.add(left);
		lr.add(right);
		
		return lr;
		
	}
	
	/** Query sorted RT tree using FC RT querying algorithm
	 * @param query: query space 
	 * @return relevant nodes to query
	 */
	public ArrayList<RangeTreeNode> query(ArrayList<Coordinate> query){
		ArrayList<RangeTreeNode> output = new ArrayList<RangeTreeNode>();
		
		Coordinate p1 = query.get(0);
		Coordinate p2 = query.get(1);
		
		// find successor and predecessor
		RangeTreeNode lower = this.succ(this.root, p1.getX(), null);
		RangeTreeNode upper = this.pred(this.root, p2.getX(), null);
		
		// find LCA of succ and prec
		RangeTreeNode usplit = this.LCA(this.root, lower, upper);
		
		// generate paths from LCA to succ and prec
		ArrayList<RangeTreeNode> La = this.generate_path(usplit, lower);
		ArrayList<RangeTreeNode> Lb = this.generate_path(usplit, upper);
		
		// check if usplit in query
		if (this.checkRange(query, usplit)) {
			output.add(usplit);
		}
		
		// find succ using binary search in usplit pointers
		int ind = this.binarySearch(usplit.getPointers(), 0, usplit.getPointers().size()-1, p1.getY());
		
		// assign succ
		CoordinateFC ind_coord = usplit.getPointers().get(ind);
		
		// check La for relevant nodes as per FC query algorithm
		for (int i=La.size()-2;i>=0;i--) {
			
			RangeTreeNode u = La.get(i);
			
			if (this.checkRange(query, u)) {
				output.add(u);
			}
			
			if (ind_coord != null) {
				
				if (u == La.get(i+1).getLeft()) {
					if (ind_coord.getCoordLeft() != null) {
						ind_coord = ind_coord.getCoordLeft();
					} else {
						ind_coord = null;
					}
				} else {
					if (ind_coord.getCoordRight() != null) {
						ind_coord = ind_coord.getCoordRight();
					} else {
						ind_coord = null;
					}
				}
				
				
				if (u.getRight() != null && ind_coord != null) {
					u = u.getRight();
		
					if (p1.getX() <= u.getParent().getX()) {
						CoordinateFC succ = ind_coord.getCoordRight();
						if (succ != null) {
							int cur = u.getPointers().indexOf(succ);
							while (cur < u.getPointers().size() && u.getPointers().get(cur).getY() < p2.getY()) {
								output.add(new RangeTreeNode(u.getPointers().get(cur).getX(),u.getPointers().get(cur).getY(),null));
								cur ++;
							}
						}
					}
				}
			}
			
		}
		
		ind_coord = usplit.getPointers().get(ind);
		
		// check Lb for relevant nodes as per FC query algorithm
		for (int i=Lb.size()-2;i>=0;i--) {
			
			RangeTreeNode u = Lb.get(i);
			
			if (this.checkRange(query, u)) {
				output.add(u);
			}
			
			if (ind_coord != null) {
				if (u == Lb.get(i+1).getLeft()) {
					if (ind_coord.getCoordLeft() != null) {
						ind_coord = ind_coord.getCoordLeft();
					} else {
						ind_coord = null;
					}
				} else {
					if (ind_coord.getCoordRight() != null) {
						ind_coord = ind_coord.getCoordRight();
					} else {
						ind_coord = null;
					}
				}
				
				if (u.getLeft() != null && ind_coord !=null) {
					u = u.getLeft();
					
					if (p2.getX() >= u.getParent().getX()) {
						CoordinateFC succ = ind_coord.getCoordLeft();
						if (succ != null) {
							int cur = u.getPointers().indexOf(succ);
							while (cur < u.getPointers().size() && u.getPointers().get(cur).getY() < p2.getY()) {
								output.add(new RangeTreeNode(u.getPointers().get(cur).getX(),u.getPointers().get(cur).getY(),null));
								cur ++;
							}
						}
					}
				}
			}
			

			
		}
		return output;
	}
	
	/** Internal method for checking if in query range
	 * @param query: query range
	 * @param node: node to be tested
	 * @return: boolean True if in range
	 */
	public boolean checkRange(ArrayList<Coordinate> query, RangeTreeNode node) {
		Coordinate a = query.get(0);
		Coordinate b = query.get(1);
		if ((node.getX() < a.getX()) || (node.getX() > b.getX()) || (node.getY() < a.getY()) || (node.getY() > b.getY())) {
			return false;
		}
		return true;
	}
	
	/** Internal method for checking if in query range
	 * @param y1: lower y bound
	 * @param y2: upper y bound
	 * @param node: node to be tested
	 * @return: boolean True if in range
	 */
	public boolean checkRange(int y1, int y2, RangeTreeNode node) {
		if (node.getY() < y1 || node.getY() > y2) {
			return false;
		}
		return true;
	}
	
	/** Method for finding predecessor on x
	 * @param node: root of search
	 * @param a: search value
	 * @param current: current pred
	 * @return: predecessor in RT tree
	 */
	public RangeTreeNode pred(RangeTreeNode node, int a, RangeTreeNode current) {
		if (node.getX() == a) {
			return node;
		} else if (node.getX() < a) {
			current = node;
			if (node.getRight() != null) {
				return pred(node.getRight(),a,current);
			} else {
				return current;
			}
		} else {
			if (node.getLeft() != null) {
				return pred(node.getLeft(),a,current);
			} else {
				return current;
			}
		}
	}
	
	/** Method for finding successor on x
	 * @param node: root of search
	 * @param a: search value
	 * @param current: current succ
	 * @return: successor in RT tree
	 */
	public RangeTreeNode succ(RangeTreeNode node, int a, RangeTreeNode current) {
		if (node.getX() == a) {
			return node;
		} else if (node.getX() > a) {
			current = node;
			if (node.getLeft() != null) {
				return succ(node.getLeft(),a,current);
			} else {
				return current;
			}
		} else {
			if (node.getRight() != null) {
				return succ(node.getRight(),a,current);
			} else {
				return current;
			}
		}
	}
	
	/** Lowest Common Ancestor finder on x
	 * @param node: root node
	 * @param a: left descendant
	 * @param b: right descendant
	 * @return LCA node
	 */
	public RangeTreeNode LCA(RangeTreeNode node, RangeTreeNode a, RangeTreeNode b) {
		if (node.getX() > a.getX()) {
			if (node.getX() <= b.getX()) {
				return node;
			} else {
				if (node.getLeft() != null) {
					return LCA(node.getLeft(),a,b);
				}
			}
		} else if (node.getX() < a.getX()){
			if (node.getX() >= b.getX()) {
				return node;
			} else {
				if (node.getRight() != null) {
					return LCA(node.getRight(),a,b);
				}
			}
		} else {
			return node;
		}
		return null;
	}
	
	/** Generate path from one node to another
	 * @param anc: ancestor node
	 * @param dec: descendant node
	 * @return list of nodes
	 */
	public ArrayList<RangeTreeNode> generate_path(RangeTreeNode anc, RangeTreeNode dec){
		ArrayList<RangeTreeNode> L = new ArrayList<RangeTreeNode>();
		
		if (dec == anc) {
			L.add(dec);
			return L;
		}
		
		RangeTreeNode current = dec;
		while (current.getParent() != anc) {
			L.add(current);
			current = current.getParent();
		}
		L.add(current);
		L.add(anc);
		return L;
	}
	
	
	/** Standard binary search algorithm for finding succ
	 * @param arr: array for search
	 * @param l: left
	 * @param r: right
	 * @param y: search key
	 * @return index of search key
	 */
	private int binarySearch(ArrayList<CoordinateFC> arr, int l, int r, int y) 
    { 
        if (r >= l) { 
            int mid = l + (r - l) / 2; 
            if (arr.get(mid).getY() == y) 
                return mid; 
  
            if (arr.get(mid).getY() > y) 
                return binarySearch(arr, l, mid - 1, y); 
  
            return binarySearch(arr, mid + 1, r, y); 
        } 
  
        return l; 
    }
	
	public RangeTreeNode getRoot() {
		return root;
	}
	
	public ArrayList<CoordinateFC> getCoordsy() {
		return coordsy;
	}
	
}
