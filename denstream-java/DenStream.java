package zhc.swim.denstream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class DenStream {

	double epsilon; // used for define radius threshold
	double beta; // the micro cluster weight threshold  minw = beta * mu
	double mu; // the micro cluster weight threshold  minw = beta * mu
	double minw;  // the micro cluster weight threshold  minw = beta * mu
	double lambda;  // used for decay function, a ^(-lambda * deltaT)
	double a; // used for decay function, a ^(-lambda * deltaT)
	long t_p; // period time for pruning outlier micro cluster
	boolean isInitial = false;
	List<Point> initBuffer;
	int initSize; //
	Set<MicroCluster> p_micro_cluster;
	Set<MicroCluster> o_micro_cluster;
	int mcid; // micro cluster id
	List<Cluster> results; // the merged micro cluster add to the results list

	public DenStream(double epsilon, double beta, double mu, double lambda,
			double a, int initSize) {
		this.epsilon = epsilon;
		this.beta = beta;
		this.mu = mu;
		this.lambda = lambda;
		this.a = a;
		minw = beta * mu; // the micro cluster weight threshold
		t_p = (long) Math.floor((1 / lambda)
				* (Math.log((minw) / (minw - 1)) / Math.log(Util.a)));
		initBuffer = new ArrayList<Point>();
		this.initSize = initSize;
		p_micro_cluster = new HashSet<MicroCluster>();
		o_micro_cluster = new HashSet<MicroCluster>();
		mcid = 0;
		System.out.println("minw=" + minw);
		System.out.println("t_p=" + t_p);
	}

	public void process(Point p) {
		if (!isInitial) {
			initBuffer.add(p);
			if (initBuffer.size() > initSize) {
				// generate MC and check if the points can merge to the MC,
				// update the MC's weight and change on the point's label
				init();
				isInitial = true;
			}
		} else {

			merge(p);
			// Because the minimal time span for a p- micro-cluster fading into an outlier is t_p,
			// so here just check all the pmc and omc  when p.id % t_p ==0,
			if (p.id % t_p == 0 && p.id != 0) {
				Iterator<MicroCluster> itr = p_micro_cluster.iterator();
				while (itr.hasNext()) {
					MicroCluster mc = itr.next();
					if (mc.getWeight(p.startTime) < minw) {
						mc.to = p.startTime;
						itr.remove();
					}
				}
				Iterator<MicroCluster> itr2 = o_micro_cluster.iterator();
				while (itr2.hasNext()) {
					MicroCluster mc = itr2.next();
					if (mc.getWeight(p.startTime) < getWeight(p.startTime,
							mc.to)) {
						itr2.remove();
					}
				}
			}
			generateCluster(p.startTime);
		}
	}

	// this step is try to find any possible that some potential micro clusters can merge to on bigger micro cluster
	// and then add the final merged cluster to the results
	// using BFS algorithm to implement this part,
	public void generateCluster(long time) {
		// first step is make all pmc to false status and try traversal them
		Iterator<MicroCluster> itr = p_micro_cluster.iterator();
		while (itr.hasNext()) {
			itr.next().visited = false;
		}
		// micro cluster merged to cluster also need a threshold
		double thres = 2 * Util.epsilon;
		int Cid = 0;
		results = new ArrayList<Cluster>(); // create the instance
		Iterator<MicroCluster> itr2 = p_micro_cluster.iterator();
		// traversal all the pmc
		while (itr2.hasNext()) {
			MicroCluster mc = itr2.next();
			// double check the micro cluster are pmc but not omc
			if (!mc.visited && mc.getWeight(time) > mu) {
				mc.visited = true;
				// not every micro cluster will generate the cluster because some micro cluster will merged and be set to visited
				Cluster C = new Cluster(Cid);
				results.add(C);
				Cid++;
				C.add(mc);
				// linkedlist is best for draw the relationship of micro cluster
				Queue<MicroCluster> queue = new LinkedList<MicroCluster>();
				queue.add(mc);
				while (!queue.isEmpty()) {
					// define one seed for the cluster want to merged
					// then find all the micro cluster close enough to this seed or seed's child <Quue>
					MicroCluster seed = queue.poll();
					Iterator<MicroCluster> itr3 = p_micro_cluster.iterator();
					while (itr3.hasNext()) {
						MicroCluster cur = itr3.next();
						if (!cur.visited && cur != seed
								&& seed.getDisTo(cur) < thres) {
							if (cur.getWeight(time) > mu) {
								queue.add(cur);
								cur.visited = true;
								C.add(cur);
							} else {
								cur.visited = true;
								C.add(cur);
							}
						}// end if
					}// end while(query)
				}// end while(queue)
			}
		}
	}

	private double getWeight(long startTime, long to) {
		return (Math.pow(a, -(Util.lambda * (startTime - to + t_p))) - 1)
				/ (Math.pow(a, -(Util.lambda * (t_p))) - 1);
	}

	private void init() {
		int size = initBuffer.size();
		for (int i = 0; i < size; i++) {
			Point p = initBuffer.get(i);
			if (!p.visited) {
				p.visited = true;
				MicroCluster mc = new MicroCluster(mcid++, p);
				List<Point> NN = nearestNeighbor(mc); // return the point list can merge to MC (distance < threshold)
				// if MC's weight < threshold, it maybe a OMC, so just reset the assigned points status
				if (mc.getWeight() <= minw) {
					int nsize = NN.size();
					for (int j = 0; j < nsize; j++) {
						NN.get(j).visited = false;
						NN.get(j).mcid = -1;
					}
					p.visited = false;
					p.mcid = -1;
					mcid--;
				} else {
					p_micro_cluster.add(mc);
				}
			}
		}
	}

	// assign all the point in initBuffer to the mc if distance less than threshold
	// each point labeled as visited and belong or not belong to the MC
	// mean time, the mc's weight update if new point merge into it
	private List<Point> nearestNeighbor(MicroCluster mc) {
		List<Point> list = new ArrayList<Point>(); // just save point which can merge to the MC
		int size = initBuffer.size();
		for (int i = 0; i < size; i++) {
			Point p = initBuffer.get(i);
			if (!p.visited) {
				if (mc.near(p)) {
					p.visited = true;
					p.mcid = mc.id;
					list.add(p);
				}
			}
		}
		return list;
	}

	// try to merge the point to nearest pmc,else try merge to omc, else generate a new omc
	public void merge(Point p) {

		boolean merged = false;
		// first, try to merge to nearest pmc, if can't merge, no change for the pmc and try to merge to omc
		if (p_micro_cluster.size() > 0) {
			MicroCluster nn = nearestNeighbor(p, p_micro_cluster);
			if (nn != null && nn.insert(p)) {
				p.mcid = nn.id;
				merged = true;
				System.out.println("merge into p_micro");
			}
		}
		if (!merged && o_micro_cluster.size() > 0) {
			MicroCluster nn = nearestNeighbor(p, o_micro_cluster);
			if (nn != null && nn.insert(p)) {
				p.mcid = nn.id;
				merged = true;
				System.out.println("merge into o_micro");
				if (nn.getWeight(p.startTime) > minw) {
					p_micro_cluster.add(nn);
					o_micro_cluster.remove(nn);
					System.out.println("o_micro change into p_micro");
				}
			}
		}
		if (!merged) {
			MicroCluster mc = new MicroCluster(mcid++, p);
			o_micro_cluster.add(mc);
			System.out.println("new o_micro generate");
		}
	}

	// find the nearest MC to the new point and return the closest mc instance
	private MicroCluster nearestNeighbor(Point p, Set<MicroCluster> set) {
		double dis = 0;
		double minDis = Double.POSITIVE_INFINITY;
		MicroCluster result = null;
		Iterator<MicroCluster> itr = set.iterator();
		while (itr.hasNext()) {
			MicroCluster mc = itr.next();
			dis = mc.getDisTo(p);
			if (minDis > dis) {
				minDis = dis;
				result = mc;
			}
		}
		return result;
	}

	public void test(boolean isMerged, boolean isMergedP, long time) {
		if (isMergedP && !isMergedP) {// merge by o_micro
			for (MicroCluster mc : o_micro_cluster) {
				if (mc.getWeight(time) > minw) {
					System.out.println("the o_micro is larger than min weight");
					System.exit(0);
				}
			}
		}
		if (!isMerged) {
			// System.out.println("new mc");
		}
	}
}
