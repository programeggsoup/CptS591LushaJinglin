import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PowerInformation {
    static double[] Qsupply;
    static double[] Qdemand;
    static double[][] SVQ;
	
	public void readPowerFile (String qsupplyFileName, String qdemandFileName, String svqFileName, Network network) {
		/* read in the qsupply, qdemand and SVQ */
        /* qsupply */
        try {
			BufferedReader bufferedReader;
			bufferedReader = new BufferedReader(new FileReader(qsupplyFileName)); /* "data.txt" */
			Qsupply = new double[network.nNodes];
			String line;
			int ii = 0;
			//String[] tokensQs = bufferedReader.readLine().split("\n");
			while ((line = bufferedReader.readLine()) != null){
			    Qsupply[ii] = Double.parseDouble(line);
			    ii++;
			}
			bufferedReader.close();

			/* qdemand */
			bufferedReader = new BufferedReader(new FileReader(qdemandFileName));
			Qdemand = new double[network.nNodes];
			ii = 0;
			//String[] tokensQd = bufferedReader.readLine().split("\n");
			while ((line = bufferedReader.readLine()) != null){
				Qdemand[ii] = Double.parseDouble(line);
				ii++;
			}
			bufferedReader.close();

			/* SVQ */
			bufferedReader = new BufferedReader(new FileReader(svqFileName));
			SVQ = new double[network.nNodes][network.nNodes];
			int count = 0;
			while ((line = bufferedReader.readLine()) != null){
			    String[] tokens = line.split("\t");
			    double[] svqi = new double[network.nNodes];
			    for(ii = 0; ii < network.nNodes; ii++){
			        svqi[ii] = Double.parseDouble(tokens[ii]);
			    }
			    SVQ[count] = svqi;
			    count++;
			}
			bufferedReader.close();
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/***
	 * 
	 * @param clustering
	 * @param network
	 * @return modified part modularity
	 */
	public double getModifiedPart(Clustering clustering, Network network) {
	    List<Double> Qbalance = new ArrayList<>();		
	    List<Double> sensitivity = new ArrayList<>();
	    int[][] nodesPerCluster = clustering.getNodesPerCluster();
	    int i,j,k;
	    
	    // for every cluster, calculate Qbalance and sensitivity
	    for (i=0; i<clustering.nClusters; i++) {
	    	// calculate Qbalance in one cluster
	    	// nodes in this cluster
	    	int[] nodesPerC = nodesPerCluster[i];
	    	int Qs = 0; // sum of Qsupply
	    	int Qd = 0; // sum of Qdemand
	    	for (j=0; j<nodesPerC.length; j++) {
	    		Qs += Qsupply[nodesPerC[j]];
	    		Qd += Qdemand[nodesPerC[j]];
	    	}
	    	if (( Qs > Qd) || (Qd == 0) ) {
	    		Qbalance.add((double) 0);
	    	}
	    	else {
	    		Qbalance.add((double) (1-Math.abs(Qs/Qd)));
	    	}
	    	
	    	double totalSVQ = 0;
	    	int count = 0;
	    	// calculate sensitivity in one cluster
	    	for (j=0; j< nodesPerC.length; j++) {
	    		for(k=j+1; k<nodesPerC.length; k++) {
	    			totalSVQ = totalSVQ + SVQ[nodesPerC[j]][nodesPerC[k]];
	    			count++;
	    		}
	    	}
	    	double averageSVQ = totalSVQ/count;
	    	sensitivity.add(averageSVQ);
	    	
	    }
	    
	    double sumInfo = 0;
	    for (i=0; i<clustering.nClusters; i++) {
	    	sumInfo = sumInfo + Qbalance.get(i) + sensitivity.get(i);
	    }
	    
	    double modifiedPart = sumInfo/clustering.nClusters;
		return modifiedPart;
		
		
	    /* modify the modularity */	    
	    /*
	    List<Double> sensitivity = new ArrayList<>();
	    List<Double> Qbalance = new ArrayList<>();
	    int[] Qs = new int[clustering.nClusters];
	    int[] Qd = new int[clustering.nClusters];
	    double[] evq = new double[clustering.nClusters];
	    int[] com_number = new int[clustering.nClusters];
	    int i, j;
	    

	    
	    for (i = 0; i < network.nNodes; i++){
	        j = clustering.cluster[i];
	        Qs[j] += Qsupply[i];
	        Qd[j] += Qdemand[i];
	        for(int ii = i+1; ii < network.nNodes; ii++){
	            int jj = clustering.cluster[ii];
	            if(j == jj){
	                evq[j] += SVQ[i][ii];
	                com_number[j] ++;
	            }
	        }
	    }
	    double allmod = 0.0;
	    for(int jj = 0; jj < Qs.length; jj++){
	        if(Qs[jj]>Qd[jj]||Qd[jj]==0){
	            Qbalance.add(0.0);
	        }
	        else{
	            Qbalance.add(1.0-Math.abs((double)Qs[jj]/(double)Qd[jj]));
	        }
	        sensitivity.add(evq[jj]/com_number[jj]);
	        allmod += (Qbalance.get(jj) + sensitivity.get(jj));
	    }
	    return allmod = allmod/Qs.length;
	    
	    */
	}

}
