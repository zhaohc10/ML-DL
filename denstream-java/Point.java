package zhc.swim.denstream;

import java.io.BufferedWriter;
import java.io.IOException;

public class Point {

	public int id;
	public long startTime; // each point should have timestamp
	public int dim;
	public double[] vec; // vector for the point
	public int mcid; // belong to which micro cluster

	public double maxDis;
	public double minDis;
	public boolean visited;

	public Point(int id, long startTime, double[] vec) {
		this.id = id;
		this.startTime = startTime;
		this.dim = vec.length;
		this.vec = vec;
		visited = false;
		mcid = -1; // init with -1, don't belong to any micro cluster firstly
	}

	public double getDisTo(Point p) {
		double dis = 0;
		double temp = 0;
		double[] pvec = p.vec;
		for (int i = 0; i < dim; i++) {
			temp = pvec[i] - vec[i];
			dis += temp * temp;
		}
		return Math.sqrt(dis);
	}

	public void print(BufferedWriter bw) {

		try {
			for (double var : vec) {
				bw.write(var + " ");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
