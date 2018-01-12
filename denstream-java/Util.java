package zhc.swim.denstream;

public class Util {

	// fading function f(t) = 2^(-lambda * deltaT)
		// lambda: higher the value of λ, the lower importance of the historical data compared to more recent data
	// -------------- how to define micro cluster --------------
		// CF1: cluster feature 1 , the weighted linear sum
		// CF2: cluster feature 1 , the weighted squared sum
	// -------------- how generate pmc and omc  --------------
	//	Micro-Clusters are classi ed based on weight w
		//	Potential core-micro-clusters (p-micro-clusters ), with w ≥ β · μ
		//	Outlier micro-clusters (o-micro-clusters ), with w < β · μ
	// -------------- how merge point to  pmc or omc or new omc --------------
		//	For time interval delta_t, if no points are merged into MC, then the weight will decrease:
			// MC = (2^(−α·δt) · CF1, 2^(−α·δt) · CF2, 2^(−α·δt) · w,tc).
		//	If a point p is merged, MC = (CF1 +p,CF2 +p2,w +1,t ).
		// *********** try to merge point **********
		// if the new point merge to the MC, check it's density (here check it's updated weighted if less than threshold)
		// if density is ok, just using updated cluster feature calculated above and update the weight of the cluster
		// if can't merge, just keep the point's nearest MC no change (no change for cluster vector and weight)
		// then try to merge it to omc, if can merged to omc, then check if omc can transfer to pmd
		// if can't merge to omc as well, then generate a new omc based on this points

	// -------------- how maintain or pruned MC --------------
		//	For each existing p-micro-cluster cp, if no new point is merged into it, the weight of cp will decay gradually.
		// it should be deleted and its memory space released for new p-micro-clusters.
		// Thus, we need to check the weight of each p-micro-cluster periodically.
			//Pruning strategy is performed every Tp time steps,
			//All p-micro-clusters with weight w < β · μ are pruned

		// Because the minimal time span for a p- micro-cluster fading into an outlier is t_p,
		// so here just check all the pmc and omc  when p.id % t_p ==0,

	// ------------- how safely pruning omc ----------------
		//	It is natural to check each o-micro-cluster every Tp time periods.
		//	At these time points, we compare the weight of each o-micro-cluster with its lower limit of weight (denoted as ξ).
		//	If the weight of an o-micro- cluster is below its lower limit of weight,
		//	that means the o-micro-cluster may not grow into a p-micro-cluster from current aspect.
	    //  lower limit of weight is defined:

	// ---------------- how merge some pmc to a bigger pmc ----------------
		//When a clustering request arrives, a variant of DBSCAN algorithm is applied
		//on the set of on-line maintained p- micro-clusters to get the final result of clustering.

		// here, the threshold using  2 * Util.epsilon

		//DBSCAN based online component generates  final clusters on demand using p-micro-clusters as virtual points

		// this step is try to find any possible that some potential micro clusters can merge to on bigger micro cluster
		// and then add the final merged cluster to the results
		// using BFS algorithm to implement this part,
	// ---------------- how out put the result  ----------------
		// output file , every 1000 points ingest in, create a new file,
		// each line stands for one cluster, each item in the line stands for micro cluster
		// the file will output all the cluster (build by micro cluster)


	public static double a;  // base var using for decay function
	public static double lambda;  // coefficient using for decay function
	public static double epsilon;  // maximum radius (threshold)

	public static double decayFun(long curt, long lastT){
//		System.out.println(curt);
//		System.out.println(lastT);
//		System.out.println(Math.pow(a, -lambda*(curt-lastT)));
		return Math.pow(a, -lambda*(curt-lastT));
	}

}
