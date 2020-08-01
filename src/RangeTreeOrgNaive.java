import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** RT - naive construction
 * @author tomharris44
 *
 */
public class RangeTreeOrgNaive {
	
	private RangeTreeNode root;

	public RangeTreeOrgNaive(ArrayList<Coordinate> coords) {
		
		// sort data set on x coordinates
		// break ties with y, then id
		Collections.sort(coords, Comparator.comparing(Coordinate::getX).thenComparing(Coordinate::getY).thenComparing(Coordinate::getId));
		
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
		}else {
		
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
		}else {
		
			int median_right = R.size() / 2;
			
			RangeTreeNode rrtn = new RangeTreeNode(R.get(median_right).getX(),R.get(median_right).getY(),root);
			
			root.setRight(rrtn);
			
			generate_wb_bst(rrtn, R.subList(0, median_right), R.subList(median_right+1, R.size()));
		}
		
	}
	
	/** Generate WB-BST on y for internal nodes
	 * @param node: root of tree for secondary tree generation
	 * @param rt_coords: coordinates of nodes in subtree rooted at this point
	 */
	public void generate_internal_rts(RangeTreeNode node, ArrayList<Coordinate> rt_coords) {
		
		// recursively call function on left subtree
		if (node.getLeft() != null) {
			ArrayList<Coordinate> rt_left = new ArrayList<Coordinate>();
			rt_left.add(new Coordinate(node.getLeft().getX(),node.getLeft().getY()));
			for (RangeTreeNode s: node.getLeft().getSubTree()) {
				rt_left.add(new Coordinate(s.getX(),s.getY()));
			}
			generate_internal_rts(node.getLeft(),rt_left);
		}
		
		// recursively call function on right subtree
		if (node.getRight() != null) {
			ArrayList<Coordinate> rt_right = new ArrayList<Coordinate>();
			rt_right.add(new Coordinate(node.getRight().getX(),node.getRight().getY()));
			for (RangeTreeNode s: node.getRight().getSubTree()) {
				rt_right.add(new Coordinate(s.getX(),s.getY()));
			}
			generate_internal_rts(node.getRight(),rt_right);
		}
		
		ArrayList<RangeTreeNode> subtree = node.getSubTree();
		
		// generate WB-BST on Y as per naive construction method
		if (!subtree.isEmpty()) {
			
			// re-sort on Y
			Collections.sort(rt_coords, Comparator.comparing(Coordinate::getY));
			
			if (subtree.size() == 1) {
				RangeTreeNode new_root = new RangeTreeNode(rt_coords.get(1).getX(),rt_coords.get(1).getY(),null);
				RangeTreeNode new_child = new RangeTreeNode(rt_coords.get(0).getX(),rt_coords.get(0).getY(),new_root);
				
				new_root.setLeft(new_child);

				node.setSec_tree_root(new_root);
				
			} else {
				int median = rt_coords.size() / 2;
				
				RangeTreeNode new_root = new RangeTreeNode(rt_coords.get(median).getX(),rt_coords.get(median).getY(),null);
				
				generate_wb_bst(new_root, rt_coords.subList(0, median), rt_coords.subList(median+1, rt_coords.size()));
				
				node.setSec_tree_root(new_root);
			}
		}
		
	}
	
	
	/** Query naive RT tree using standard RT querying algorithm
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
		
		// check La for relevant nodes as per standard query algorithm
		for (int i=0;i<La.size()-1;i++) {
			
			RangeTreeNode u = La.get(i);
			
			if (this.checkRange(query, u)) {
				output.add(u);
			}
			
			if (lower.getX() <= u.getX()) {
				if (u.getRight() != null) {
					if (u.getRight().getSec_tree_root() != null) {
						for (RangeTreeNode node: query_y(p1.getY(),p2.getY(),u.getRight().getSec_tree_root())) {
							output.add(node);
						}
					} else if (checkRange(p1.getY(),p2.getY(), u.getRight())){
						output.add(u.getRight());
					}
				}
			}
		}
		
		// check Lb for relevant nodes as per standard query algorithm
		for (int i=0;i<Lb.size()-1;i++) {
			
			RangeTreeNode u = Lb.get(i);
			
			if (this.checkRange(query, u)) {
				output.add(u);
			}
			
			if (upper.getX() >= u.getX()) {
				if (u.getLeft() != null) {
					if (u.getLeft().getSec_tree_root() != null) {
						for (RangeTreeNode node: query_y(p1.getY(),p2.getY(),u.getLeft().getSec_tree_root())) {
							output.add(node);
						}
					} else if (checkRange(p1.getY(),p2.getY(), u.getLeft())){
						output.add(u.getLeft());
					}
				}
				
			}
		}
		return output;
	}
	
	/** Query method for internal WB-BST on y
	 * @param y1: lower y bound
	 * @param y2: upper y bound
	 * @param root_y: root of internal WB-BST
	 * @return nodes relevant to query
	 */
	public ArrayList<RangeTreeNode> query_y(int y1, int y2, RangeTreeNode root_y){
		
		ArrayList<RangeTreeNode> output = new ArrayList<RangeTreeNode>();
		
		// find successor and predecessor
		RangeTreeNode lower = this.succ_y(root_y, y1, null);
		RangeTreeNode upper = this.pred_y(root_y, y2, null);
		
		// if succ/prec not found stop search
		if(lower == null || upper == null) {
			return output;
		}
		
		// find LCA of succ and prec
		RangeTreeNode usplit = this.LCA_y(root_y, lower, upper);
		
		// genrate paths from LCA to succ and prec
		ArrayList<RangeTreeNode> La = this.generate_path(usplit, lower);
		ArrayList<RangeTreeNode> Lb = this.generate_path(usplit, upper);
		
		// check if usplit in query
		if (this.checkRange(y1, y2, usplit)) {
			output.add(usplit);
		}
		
		// check La for relevant nodes as per standard query algorithm
		for (int i=0;i<La.size()-1;i++) {
			
			RangeTreeNode u = La.get(i);
			
			if (this.checkRange(y1, y2, u)) {
				output.add(u);
			}
			
			if (y1 <= u.getY()) {
				if (u.getRight() != null) {
					output.add(u.getRight());
					for (RangeTreeNode node: u.getRight().getSubTree()) {
						output.add(node);
					}
				}
			}
		}
		
		// check Lb for relevant nodes as per standard query algorithm
		for (int i=0;i<Lb.size()-1;i++) {
			
			RangeTreeNode u = Lb.get(i);
			
			if (this.checkRange(y1, y2, u)) {
				output.add(u);
			}
			
			if (y2 >= u.getY()) {
				if (u.getLeft() != null) {
					output.add(u.getLeft());
					for (RangeTreeNode node: u.getLeft().getSubTree()) {
						output.add(node);
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
	
	/** Method for finding predecessor on y
	 * @param node: root of search
	 * @param a: search value
	 * @param current: current pred
	 * @return: predecessor in RT tree
	 */
	public RangeTreeNode pred_y(RangeTreeNode node, int a, RangeTreeNode current) {
		if (node.getY() == a) {
			return node;
		} else if (node.getY() < a) {
			current = node;
			if (node.getRight() != null) {
				return pred_y(node.getRight(),a,current);
			} else {
				return current;
			}
		} else {
			if (node.getLeft() != null) {
				return pred_y(node.getLeft(),a,current);
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
	
	/** Method for finding successor on y
	 * @param node: root of search
	 * @param a: search value
	 * @param current: current succ
	 * @return: successor in RT tree
	 */
	public RangeTreeNode succ_y(RangeTreeNode node, int a, RangeTreeNode current) {
		if (node.getY() == a) {
			return node;
		} else if (node.getY() > a) {
			current = node;
			if (node.getLeft() != null) {
				return succ_y(node.getLeft(),a,current);
			} else {
				return current;
			}
		} else {
			if (node.getRight() != null) {
				return succ_y(node.getRight(),a,current);
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
	
	/** Lowest Common Ancestor finder on y
	 * @param node: root node
	 * @param a: left descendant
	 * @param b: right descendant
	 * @return LCA node
	 */
	public RangeTreeNode LCA_y(RangeTreeNode node, RangeTreeNode a, RangeTreeNode b) {
		if (node.getY() > a.getY()) {
			if (node.getY() <= b.getY()) {
				return node;
			} else {
				if (node.getLeft() != null) {
					return LCA_y(node.getLeft(),a,b);
				}
			}
		} else if (node.getY() < a.getY()){
			if (node.getY() >= b.getY()) {
				return node;
			} else {
				if (node.getRight() != null) {
					return LCA_y(node.getRight(),a,b);
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
	
	public RangeTreeNode getRoot() {
		return root;
	}
	
}
