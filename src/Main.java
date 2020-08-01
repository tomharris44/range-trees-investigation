import java.util.ArrayList;
import java.util.Random;

/**
 * @author tomharris44
 *
 */
public class Main {
	
	public static int M = 1000000;

	public static void main(String[] args) {
		
		// Function calls for each experiment outlined in report
		System.out.println("EXPERIMENT 1:");
		Experiment1();
		System.out.println("EXPERIMENT 2:");
		Experiment2();
		System.out.println("EXPERIMENT 3:");
		Experiment3();
		

	}

	public static void Experiment1() {
		
		// various lengths for construction algorithm experiment
		int[] L = new int[5];
		L[0] = 200000;
		L[1] = 400000;
		L[2] = 600000;
		L[3] = 800000;
		L[4] = 1000000;
		
		// generate naive and sorted rt trees for each L and measure runtime
		for (int m: L) {
			
			ArrayList<Coordinate> coords = generate_point_set(m);
			
			long startTimeNaive = System.nanoTime();
			RangeTreeOrgNaive rt_naive = new RangeTreeOrgNaive(coords);
			rt_naive.generate_internal_rts(rt_naive.getRoot(),coords);
			long stopTimeNaive = System.nanoTime();
			System.out.println("NAIVE  " + m + " :" + (stopTimeNaive - startTimeNaive));
			
			long startTimeSorted = System.nanoTime();
			RangeTreeOrgSorted rt_sorted = new RangeTreeOrgSorted(coords);
			rt_sorted.generate_internal_rts(rt_sorted.getRoot(),null);
			long stopTimeSorted = System.nanoTime();
			System.out.println("SORTED " + m + " :" + (stopTimeSorted - startTimeSorted));
		}
	}
	
	public static void Experiment2() {
		
		// fixed n 
		int n = 1000000;
		
		// generate fixed point set
		ArrayList<Coordinate> coords = generate_point_set(n);
		
		// generate org and FC trees
		RangeTreeOrgSorted rt_sorted = new RangeTreeOrgSorted(coords);
		RangeTreeFC rt_fc = new RangeTreeFC(coords);
		
		rt_sorted.generate_internal_rts(rt_sorted.getRoot(),null);
		rt_fc.generate_internal_pointers(rt_fc.getRoot(),null);
		
		// define various query lengths
		int[] q = new int[5];
		q[0] = 10000;
		q[1] = 20000;
		q[2] = 50000;
		q[3] = 100000;
		q[4] = 200000;
		
		// generate 100 queries for each defined length
		ArrayList<ArrayList<ArrayList<Coordinate>>> workload = new ArrayList<ArrayList<ArrayList<Coordinate>>>();
		
		for (int m: q) {
			ArrayList<ArrayList<Coordinate>> set = new ArrayList<ArrayList<Coordinate>>();
			for (int j=0;j<100;j++) {
				set.add(generate_a_query(m));
			}
			workload.add(set);
		}
		
		// define arrays for time measures
		long[] sorted = new long[100];
		long[] FC = new long[100];
		
		// query each rt tree and measure runtime
		for (int i=0;i<5;i++) {
			ArrayList<ArrayList<Coordinate>> set = workload.get(i);
			for (int j=0;j<set.size();j++) {
				ArrayList<Coordinate> c = set.get(j);
				
				long startTimeOrg = System.nanoTime();
				rt_sorted.query(c);
				long stopTimeOrg = System.nanoTime();
				sorted[j] = (stopTimeOrg - startTimeOrg);
				
				long startTimeFC = System.nanoTime();
				rt_fc.query(c);
				long stopTimeFC = System.nanoTime();
				FC[j] = (stopTimeFC - startTimeFC);
			}
			long sum_sorted = 0;
			long sum_FC = 0;
			
			for (int k = 0;k<100;k++) {
				sum_sorted += sorted[k];
				sum_FC += FC[k];
			}
			
			System.out.println("s: " + q[i] + " AVG. ORG QUERY TIME: " + (sum_sorted/100.0));
			System.out.println("s: " + q[i] + " AVG.  FC QUERY TIME: " + (sum_FC/100.0));
		}
	}
	
	public static void Experiment3() {
		
		// fixed s
		int s = 50000;
		
		// define various point set lengths for testing
		int[] n = new int[10];
		n[0] = 2000;
		n[1] = 4000;
		n[2] = 8000;
		n[3] = 16000;
		n[4] = 32000;
		n[5] = 64000;
		n[6] = 128000;
		n[7] = 256000;
		n[8] = 512000;
		n[9] = 1024000;
		
		// generate fixed query set
		ArrayList<ArrayList<Coordinate>> workload = new ArrayList<ArrayList<Coordinate>>();
		for (int i=0;i<100;i++) {
			workload.add(generate_a_query(s));
		}
		
		// generate point sets for each length
		ArrayList<ArrayList<Coordinate>> sets = new ArrayList<ArrayList<Coordinate>>();
		
		for (int i=0;i<10;i++) {
			sets.add(generate_point_set(n[i]));
		}
		
		// query each rt tree constructed with each point set and measure runtime
		for (ArrayList<Coordinate> c : sets) {
			RangeTreeOrgSorted rt_sorted = new RangeTreeOrgSorted(c);
			RangeTreeFC rt_fc = new RangeTreeFC(c);
			
			rt_sorted.generate_internal_rts(rt_sorted.getRoot(),null);
			rt_fc.generate_internal_pointers(rt_fc.getRoot(),null);
			
			long[] sorted = new long[100];
			long[] FC = new long[100];
			
			for (int j=0;j<workload.size();j++) {
				ArrayList<Coordinate> t = workload.get(j);
				
				long startTimeOrg = System.nanoTime();
				rt_sorted.query(t);
				long stopTimeOrg = System.nanoTime();
				sorted[j] = (stopTimeOrg - startTimeOrg);
				
				long startTimeFC = System.nanoTime();
				rt_fc.query(t);
				long stopTimeFC = System.nanoTime();
				FC[j] = (stopTimeFC - startTimeFC);
			}
			long sum_sorted = 0;
			long sum_FC = 0;
			
			for (int k = 0;k<100;k++) {
				sum_sorted += sorted[k];
				sum_FC += FC[k];
			}
			
			System.out.println("n: " + c.size() + " AVG. ORG QUERY TIME: " + (sum_sorted/100.0));
			System.out.println("n: " + c.size() + " AVG.  FC QUERY TIME: " + (sum_FC/100.0));
			
		}
	}
	
	
	public static Coordinate generate_a_point(int coord_min, int coord_max) {
			
			Random rand = new Random();
			
			// x, y: randomly generated coordinate between [coord_min,coord_max]
			
			int x = coord_min + rand.nextInt((coord_max + 1) - coord_min);
			int y = coord_min + rand.nextInt((coord_max + 1) - coord_min);
			
			Coordinate out = new Coordinate(x,y);
			
			return out;
		}
	
	public static ArrayList<Coordinate> generate_point_set(int n){
		
		ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
		
		for (int i=0;i<n;i++) {
			Coordinate pt = generate_a_point(1,M);
			pt.setId(i+1);
			coords.add(pt);
		}
		
		return coords;
	}
	
	public static ArrayList<Coordinate> generate_a_query(int s){
		
		// generate bottom left and top right corners of query 'square' and return in list
		Coordinate bot_left = generate_a_point(1,M - s);
		Coordinate top_right = new Coordinate(bot_left.getX() + s,bot_left.getY() + s);
		
		ArrayList<Coordinate> query_range = new ArrayList<Coordinate>();
		
		query_range.add(bot_left);
		query_range.add(top_right);
		
		return query_range;
		
	}
	
	
	/** Checks RT is valid tree on x
	 * @param root: root of tree to be assessed
	 * @return boolean - True if valid tree
	 */
	public static boolean checkValidRT_X(RangeTreeNode root) {
		
		if (root==null || (root.getLeft() == null && root.getRight() == null)) {
			return true;
		}
		
		if (root.getLeft() != null) {
			if (root.getLeft().getX() >= root.getX()) {
				if (!(root.getLeft().getX() == root.getX() && root.getLeft().getY() < root.getY())) {
					print(root);
					return false;
				}
			}
		}
		
		if (root.getRight() != null) {
			if (root.getRight().getX() <= root.getX()) {
				if (!(root.getRight().getX() == root.getX() && root.getRight().getY() > root.getY())) {
					print(root);
					return false;
				}
			}
		}
		
		return (checkValidRT_X(root.getLeft()) && checkValidRT_X(root.getRight()));
	}
	
	
	/** Print subtree rooted at node
	 * @param node: root of subtree
	 */
	public static void print(RangeTreeNode node) {
		
		
		if (node != null) {
			if (node.getParent() != null) {
				System.out.print("Parent: " + node.getParent().getX() + " " + node.getParent().getY() + " ; ");
			}
		
			System.out.println(node.getX() + " " + node.getY());
	
			print(node.getLeft());
			print(node.getRight());
		} else {
			System.out.println("NULL");
		}
		
	}

}
