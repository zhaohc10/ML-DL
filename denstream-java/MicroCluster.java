package zhc.swim.denstream;

public class MicroCluster {
	int id;
	private double weight;
	double[] center;
	double[] CF1;
	double[] CF2;
	long lt;
	long to; // create time
	boolean visited;
	int dim;

	// only call this when generate the new MC, so just use one point to build the new MC
	// no matter it is pmc or omc
	// update the cluster feature vector and label the creation time stamp
	public MicroCluster(int id, Point p) {
		this.id = id;
		p.mcid = id;
		this.dim = p.dim;
		double[] pvec = p.vec;
		CF1 = new double[dim];
		CF2 = new double[dim];
		center = new double[dim];
		for (int i = 0; i < dim; i++) {
			CF1[i] = pvec[i];
			CF2[i] = pvec[i] * pvec[i];
		}
		weight = 1;
//		to = p.startTime;
		System.arraycopy(CF1, 0, center, 0, dim);
		lt = p.startTime;
	}

	// how insert new point to the existed MC ??
	public boolean insert(Point p) {
		double decayFactor = Util.decayFun(p.startTime, lt);
		double[] CCF1 = new double[CF1.length]; // already feed the value in the construct method
		System.arraycopy(CF1, 0, CCF1, 0, CF1.length);
		double[] CCF2 = new double[CF2.length];
		// copy parameter: src var, src pos, des var, des pos, copy length
		System.arraycopy(CF2, 0, CCF2, 0, CF2.length);
		double[] vec = p.vec;
		// every iterate no matter the new points can merge to the micro,
		// the cluster feature vector will have to apply decay once;
		//
		for (int i = 0; i < dim; i++) {
			CCF1[i] *= decayFactor;
			CCF1[i] += vec[i];
			CCF2[i] *= decayFactor;
			CCF2[i] += vec[i] * vec[i];
		}
		// if the new point merge to the MC, check it's density (here check it's updated weighted if less than threshold)
		// if density is ok, just using updated cluster feature calculated above and update the weight of the cluster
		// if can't merge, just keep the point's nearest MC no change (no change for cluster vector and weight)

		if (getRadius(CCF1, CCF2, weight * decayFactor + 1) < Util.epsilon) {
			CF1 = CCF1;
			CF2 = CCF2;
			weight *= decayFactor;
			weight++;
			for (int i = 0; i < dim; i++) {
				center[i] = CF1[i] / weight;
			}
			lt = p.startTime;
			return true;
		} else {
			return false;
		}
	}
	
	boolean insert2(Point p){
		double decayFactor = Util.decayFun(p.startTime, lt);
		double[] pvec = p.vec;
		double dis = 0;
		for (int i = 0; i < dim; i++) {
			dis += Math.pow(center[i] - pvec[i], 2);
		}
		dis = Math.sqrt(dis);
		if (dis < Util.epsilon) {
			weight *= decayFactor;
			weight++;
			return true;
		} else {
			return false;
		}
	}

	// check if the distance between point to center less than threshold epsilon
	// mean time, the weight of this mc instance update if new point merge into it
	public boolean near(Point p) {
		double[] pvec = p.vec;
		double dis = 0;
		for (int i = 0; i < dim; i++) {
			dis += Math.pow(center[i] - pvec[i], 2);
		}
		dis = Math.sqrt(dis);
		if (dis < Util.epsilon) {
			weight++; //update the weight if new point merge in
			return true;
		} else {
			return false;
		}
	}

	public double getRadius(double[] LS, double SS[], double weight) {
		double radius = 0;
		for (int i = 0; i < dim; i++) {
			radius += (SS[i] - (Math.pow(LS[i], 2) / weight));
		}
		return Math.sqrt(radius / weight);
	}

	public double getDisTo(Point p) {
		double dis = 0;
		double temp = 0;
		double[] pvec = p.vec;
		for (int i = 0; i < dim; i++) {
			temp = pvec[i] - center[i];
			dis += temp * temp;
		}
		return Math.sqrt(dis);
	}

	public double getWeight(long time) {
		return weight * Util.decayFun(time, lt);
	}
	
	public double getWeight(){ // no time because at the cold start time
		return weight;
	}

	public double getDisTo(MicroCluster cur) {
		double dis = 0;
		double temp = 0;
		double[] pvec = cur.center;
		for (int i = 0; i < dim; i++) {
			temp = pvec[i] - center[i];
			dis += temp * temp;
		}
		return Math.sqrt(dis);
	}

//	 public static void main(String[] args) {
//		 double[] a = { 53, 6, 15, 12 };
//		 double[] b = { 15, 32, 9, 21 };
//		 double[] c = { 28, 6, 27, 14 };
//		 double[] center = new double[4];
//		 double[] LS = new double[4];
//		 double[] SS = new double[4];
//		 double val1 = 0;
//		 double val2 = 0;
//		 for (int i = 0; i < 4; i++) {
//		 LS[i] = a[i] + b[i] + c[i];
//		 SS[i] = Math.pow(a[i], 2) + Math.pow(b[i], 2) + Math.pow(c[i], 2);
//		 center[i] = LS[i] / 3;
//		 val1 += SS[i] / 3 - Math.pow(LS[i] / 3, 2);
//		 val2 += Math.pow(a[i] - center[i], 2)
//		 	+ Math.pow(b[i] - center[i], 2)
//		 	+ Math.pow(c[i] - center[i], 2);
//		 }
//		 System.out.println(Math.sqrt(val1));
//		 System.out.println(Math.sqrt(val2 / 3));
//
//	 }

}
