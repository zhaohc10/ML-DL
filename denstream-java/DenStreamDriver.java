package zhc.swim.denstream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class DenStreamDriver {

	public static void main(String[] args) throws IOException {
		double epsilon = 0; // the radius threshold
		double beta = 0; // used for define weight threshold, beta * mu
		double mu = 0; // used for define weight threshold, beta * mu
		double lambda = 0; // used for decay function, a ^(-lambda * deltaT)
		double a = 0; // used for decay function, a ^(-lambda * deltaT)
		String input = null; // data file path
		String output = null; // result fold path
		String pointToMC = null;// generated from output
		String MCToCluster = null;// generated from output
		int dim = 0; // point dimension
		int initSize = 0; // cold start size

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-e")) {
				epsilon = Double.parseDouble(args[++i]);
			}
			if (args[i].equals("-b")) {
				beta = Double.parseDouble(args[++i]);
			}
			if (args[i].equals("-m")) {
				mu = Double.parseDouble(args[++i]);
			}
			if (args[i].equals("-l")) {
				lambda = Double.parseDouble(args[++i]);
			}
			if (args[i].equals("-a")) {
				a = Double.parseDouble(args[++i]);
			}
			if (args[i].equals("-in")) {
				input = args[++i];
			}
			if (args[i].equals("-out")) {
				output = args[++i];
			}
		}
		input = "src/main/resources/kddcup99/training.csv";
		output = "src/main/resources/kddcup99";
		epsilon = 0.4;
		dim = 32; // 32 total columns and last one is label

		beta = 0.6;
		mu = 1.6667;
		lambda = 0.00288; // decay function parameter
		a = 2; // decay function parameter
		pointToMC = output + "/pointToMC.txt";
		MCToCluster = output + "/mcToCluster";
		initSize = 100;  // how many point for cold start

		Util.a = a;
		Util.epsilon = epsilon;
		Util.lambda = lambda;
		System.out.println("epsilon=" + Util.epsilon);

		if (epsilon == 0 || beta == 0 || mu == 0 || lambda == 0 || a == 0
				|| input == null || dim == 0 || initSize == 0) {
			System.err.println("Error parameter!");
			System.exit(0);
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(pointToMC));
		System.out.println(pointToMC);
		DenStream denstream = new DenStream(epsilon, beta, mu, lambda, a,
				initSize);
		BufferedReader br = new BufferedReader(new FileReader(input));
		String line = null;

		long start = System.currentTimeMillis();
		long time = start;
		long end = 0;
		long time2 = 0;

		int id = 0;
		boolean outInit = false; // flag when cold start part is warmed
		while ((line = br.readLine()) != null) {// while data stream is active
			StringTokenizer st = new StringTokenizer(line,","); //split with ,
//			id = Integer.parseInt(st.nextToken());
			double[] vec = new double[dim];
			for (int i = 0; i < dim; i++) {
				vec[i] = Double.parseDouble(st.nextToken());
			}
			time2 = System.currentTimeMillis() - start;
			Point p = new Point(id, id, vec);
			id++;

			denstream.process(p);
            // set outInit to true when finished following items:
                // 1. check denStream initial is done and then write point's updated status to file in the initBuffer
                // 2. set the MC's start time to 0 as the baseline for which generated at the init phase
			if (outInit) {
				bw.write(p.id + " " + p.mcid + "\n");
			} else if (denstream.isInitial) {
				System.out.println("+++++++++++++++" + p.id);
				for (Point curp : denstream.initBuffer) {
					bw.write(curp.id + " " + curp.mcid + "\n");
				}
				for (MicroCluster mc : denstream.p_micro_cluster) {
					mc.lt = 0;
					mc.to = 0;
				}
				outInit = true;
			}

			if (p.id % 200 == 0 && p.id != 0) {
			    // output file , every 1000 points ingest in, create a new file,
                // each line stands for one cluster, each item in the line stands for micro cluster
                // the file will output all the cluster (build by micro cluster)
				BufferedWriter bw2 = new BufferedWriter(new FileWriter(
						MCToCluster + (id / 1000) + ".txt"));
				for (Cluster C : denstream.results) {
					for (MicroCluster mc : C.list) {
						bw2.write(mc.id + " ");
					}
					bw2.write("\n"); // ADDED FOR EACH CLUSTER
				}
				bw2.close();
				System.out.print("ID/1000 :" + id / 1000 + " --> ");
				end = System.currentTimeMillis();
				if(end-time > 25000){
					System.out.println("time out");
					br.close();
					bw.close();
					System.exit(0);
				}
				System.out.println("period time for this iter: " + ((end - time)/25.0));
				time = end;
			}
		}
		end = System.currentTimeMillis();
		System.out.println("Total start to end duration:  " + (end - start));
		br.close();
		bw.close();
		System.out.println("DenStream");
	}
}
