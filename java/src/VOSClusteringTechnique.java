/**
 * VOSClusteringTechnique
 *
 * @author Ludo Waltman
 * @author Nees Jan van Eck
 * @version 1.3.1, 11/23/14
 */

import java.util.*;

public class VOSClusteringTechnique
{
    protected Network network;
    protected Clustering clustering;
    protected double resolution;

    public VOSClusteringTechnique(Network network, double resolution)
    {
        this.network = network;
        clustering = new Clustering(network.nNodes);
        clustering.initSingletonClusters();
        this.resolution = resolution;
    }

    public VOSClusteringTechnique(Network network, Clustering clustering, double resolution)
    {
        this.network = network;
        this.clustering = clustering;
        this.resolution = resolution;
    }

    public Network getNetwork()
    {
        return network;
    }

    public Clustering getClustering()
    {
        return clustering;
    }

    public double getResolution()
    {
        return resolution;
    }

    public void setNetwork(Network network)
    {
        this.network = network;
    }

    public void setClustering(Clustering clustering)
    {
        this.clustering = clustering;
    }

    public void setResolution(double resolution)
    {
        this.resolution = resolution;
    }

    public double calcQualityFunction()
    {
        double qualityFunction;
        double[] clusterWeight;
        int i, j, k;

        qualityFunction = 0;

        for (i = 0; i < network.nNodes; i++)
        {
            j = clustering.cluster[i];
            for (k = network.firstNeighborIndex[i]; k < network.firstNeighborIndex[i + 1]; k++)
                if (clustering.cluster[network.neighbor[k]] == j)
                    qualityFunction += network.edgeWeight[k];
        }
        qualityFunction += network.totalEdgeWeightSelfLinks;

        clusterWeight = new double[clustering.nClusters];
        for (i = 0; i < network.nNodes; i++)
            clusterWeight[clustering.cluster[i]] += network.nodeWeight[i];
        for (i = 0; i < clustering.nClusters; i++)
            qualityFunction -= clusterWeight[i] * clusterWeight[i] * resolution;

        qualityFunction /= 2 * network.getTotalEdgeWeight() + network.totalEdgeWeightSelfLinks;

        int max = 0;
        for (i = 0; i < network.nNodes; i++){
            if(clustering.cluster[i]>max)
                max = clustering.cluster[i];
        }
        max++;
        List<Double> sensitivity = new ArrayList<>();
        List<Double> Qbalance = new ArrayList<>();
        //int[] Qsupply = {100,100,100,0,0,0,0,0,0};
        //int[] Qdemand = {0,20,30,20,10,20,10,20,30};
        //int[] Qsupply = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,400,300,300,250,167,300,240,250,300,300};
        //double[] Qdemand = {44.2,0,2.4,184,0,0,84,176.6,-66.6,0,0,88,0,0,153,32.3,0,30,0,103,115,0,84.6,-92.2,47.2,17,75.5,27.6,26.9,0,4.6,0,0,0,0,0,0,0,250};
        int[] Qsupply = {15,0,0,300,0,50,0,300,0,200,0,120,0,0,30,0,0,50,24,0,0,0,0,300,140,1000,300,0,0,0,300,42,0,24,0,24,0,0,0,300,0,300,0,0,0,100,0,0,210,0,0,0,0,300,23,15,0,0,180,0,300,20,0,0,200,200,0,0,300,32,0,100,100,9,0,23,70,0,0,280,0,0,0,0,23,0,1000,0,300,300,100,9,0,0,0,0,0,0,100,155,0,0,40,23,23,0,200,0,0,23,1000,1000,200,0,0,1000,0,0};
        double[] Qdemand = {27,9,10,12,0,22,2,0,0,0,23,10,16,1,30,10,3,34,25,3,8,5,3,0,0,0,13,7,4,0,27,23,9,26,9,17,0,0,11,23,10,23,7,8,22,10,0,11,30,4,8,5,11,32,22,18,3,3,113,3,0,14,0,0,0,18,7,0,0,20,0,0,0,27,11,36,28,26,32,26,0,27,10,7,15,10,0,10,0,42,0,10,7,16,31,15,9,8,0,18,15,3,16,25,26,16,12,1,3,30,0,13,0,3,7,0,8,15};
        int[] Qs = new int[max];
        int[] Qd = new int[max];
        /*double[][] svq = {{0.10159104,-0.03955934,-0.03957958,0.05039104,0.01451888,-0.03306847,-0.0375356,-0.0326149,0.01585694}
                ,{-0.03955934,0.10146739,-0.02425666,-0.03315934,-0.03471069,-0.01774555,0.01465622,0.04591183,-0.01260385}
                ,{-0.03957958,-0.02425666,0.1026502,-0.03317958,-0.01101329,0.05056131,0.00623672,-0.01731221,-0.0341069}
                ,{0.05039104,-0.03315934,-0.03317958,0.05679104,0.02091888,-0.02666847,-0.0311356,-0.0262149,0.02225694}
                ,{0.01451888,-0.03471069,-0.01101329,0.02091888,0.06730891,-0.00450218,-0.02280459,-0.02776625,-0.00194966}
                ,{-0.03306847,-0.01774555,0.05056131,-0.02666847,-0.00450218,0.05707242,0.01274783,-0.0108011,-0.02759578}
                ,{-0.0375356,0.01465622,0.00623672,-0.0311356,-0.02280459,0.01274783,0.05576563,0.02160066,-0.01953127}
                ,{-0.0326149,0.04591183,-0.01731221,-0.0262149,-0.02776625,-0.0108011,0.02160066,0.05285627,-0.0056594}
                ,{0.01585694,-0.01260385,-0.0341069,0.02225694,-0.00194966,-0.02759578,-0.01953127,-0.0056594,0.06333297}};*/

        double[] evq = new double[max];
        int[] com_number = new int[max];

        for (i = 0; i < network.nNodes; i++){
            j = clustering.cluster[i];
            Qs[j] += Qsupply[i];
            Qd[j] += Qdemand[i];
            for(int ii = i+1; ii < network.nNodes; ii++){
                int jj = clustering.cluster[ii];
                if(j == jj){
                    evq[j] += ModularityOptimizer.svq[i][ii];
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
        allmod = allmod/Qs.length;

        return qualityFunction-allmod;
    }

    public boolean runLocalMovingAlgorithm()
    {
        return runLocalMovingAlgorithm(new Random());
    }

    public boolean runLocalMovingAlgorithm(Random random)
    {
        boolean update;
        double maxQualityFunction, qualityFunction;
        double[] clusterWeight, edgeWeightPerCluster;
        int bestCluster, i, j, k, l, nNeighboringClusters, nStableNodes, nUnusedClusters;
        int[] neighboringCluster, newCluster, nNodesPerCluster, nodePermutation, unusedCluster;

        if (network.nNodes == 1)
            return false;

        update = false;

        clusterWeight = new double[network.nNodes];
        nNodesPerCluster = new int[network.nNodes];
        for (i = 0; i < network.nNodes; i++)
        {
            clusterWeight[clustering.cluster[i]] += network.nodeWeight[i];
            nNodesPerCluster[clustering.cluster[i]]++;
        }

        nUnusedClusters = 0;
        unusedCluster = new int[network.nNodes];
        for (i = 0; i < network.nNodes; i++)
            if (nNodesPerCluster[i] == 0)
            {
                unusedCluster[nUnusedClusters] = i;
                nUnusedClusters++;
            }

        nodePermutation = Arrays2.generateRandomPermutation(network.nNodes, random);

        edgeWeightPerCluster = new double[network.nNodes];
        neighboringCluster = new int[network.nNodes - 1];
        nStableNodes = 0;
        i = 0;
        do
        {
            j = nodePermutation[i];

            nNeighboringClusters = 0;
            for (k = network.firstNeighborIndex[j]; k < network.firstNeighborIndex[j + 1]; k++)
            {
                l = clustering.cluster[network.neighbor[k]];
                if (edgeWeightPerCluster[l] == 0)
                {
                    neighboringCluster[nNeighboringClusters] = l;
                    nNeighboringClusters++;
                }
                edgeWeightPerCluster[l] += network.edgeWeight[k];
            }

            clusterWeight[clustering.cluster[j]] -= network.nodeWeight[j];
            nNodesPerCluster[clustering.cluster[j]]--;
            if (nNodesPerCluster[clustering.cluster[j]] == 0)
            {
                unusedCluster[nUnusedClusters] = clustering.cluster[j];
                nUnusedClusters++;
            }

            bestCluster = -1;
            maxQualityFunction = 0;
            for (k = 0; k < nNeighboringClusters; k++)
            {
                l = neighboringCluster[k];
                qualityFunction = edgeWeightPerCluster[l] - network.nodeWeight[j] * clusterWeight[l] * resolution;
                if ((qualityFunction > maxQualityFunction) || ((qualityFunction == maxQualityFunction) && (l < bestCluster)))
                {
                    bestCluster = l;
                    maxQualityFunction = qualityFunction;
                }
                edgeWeightPerCluster[l] = 0;
            }
            if (maxQualityFunction == 0)
            {
                bestCluster = unusedCluster[nUnusedClusters - 1];
                nUnusedClusters--;
            }

            clusterWeight[bestCluster] += network.nodeWeight[j];
            nNodesPerCluster[bestCluster]++;
            if (bestCluster == clustering.cluster[j])
                nStableNodes++;
            else
            {
                clustering.cluster[j] = bestCluster;
                nStableNodes = 1;
                update = true;
            }

            i = (i < network.nNodes - 1) ? (i + 1) : 0;
        }
        while (nStableNodes < network.nNodes);

        newCluster = new int[network.nNodes];
        clustering.nClusters = 0;
        for (i = 0; i < network.nNodes; i++)
            if (nNodesPerCluster[i] > 0)
            {
                newCluster[i] = clustering.nClusters;
                clustering.nClusters++;
            }
        for (i = 0; i < network.nNodes; i++)
            clustering.cluster[i] = newCluster[clustering.cluster[i]];

        return update;
    }

    public boolean runLouvainAlgorithm()
    {
        return runLouvainAlgorithm(new Random());
    }

    public boolean runLouvainAlgorithm(Random random)
    {
        boolean update, update2;
        VOSClusteringTechnique VOSClusteringTechnique;

        if (network.nNodes == 1)
            return false;

        update = runLocalMovingAlgorithm(random);

        if (clustering.nClusters < network.nNodes)
        {
            VOSClusteringTechnique = new VOSClusteringTechnique(network.createReducedNetwork(clustering), resolution);

            update2 = VOSClusteringTechnique.runLouvainAlgorithm(random);

            if (update2)
            {
                update = true;

                clustering.mergeClusters(VOSClusteringTechnique.clustering);
            }
        }

        return update;
    }

    public boolean runIteratedLouvainAlgorithm(int maxNIterations)
    {
        return runIteratedLouvainAlgorithm(maxNIterations, new Random());
    }

    public boolean runIteratedLouvainAlgorithm(int maxNIterations, Random random)
    {
        boolean update;
        int i;

        i = 0;
        do
        {
            update = runLouvainAlgorithm(random);
            i++;
        }
        while ((i < maxNIterations) && update);
        return ((i > 1) || update);
    }

    public boolean runLouvainAlgorithmWithMultilevelRefinement()
    {
        return runLouvainAlgorithmWithMultilevelRefinement(new Random());
    }

    public boolean runLouvainAlgorithmWithMultilevelRefinement(Random random)
    {
        boolean update, update2;
        VOSClusteringTechnique VOSClusteringTechnique;

        if (network.nNodes == 1)
            return false;

        update = runLocalMovingAlgorithm(random);

        if (clustering.nClusters < network.nNodes)
        {
            VOSClusteringTechnique = new VOSClusteringTechnique(network.createReducedNetwork(clustering), resolution);

            update2 = VOSClusteringTechnique.runLouvainAlgorithmWithMultilevelRefinement(random);

            if (update2)
            {
                update = true;

                clustering.mergeClusters(VOSClusteringTechnique.clustering);

                runLocalMovingAlgorithm(random);
            }
        }

        return update;
    }

    public boolean runIteratedLouvainAlgorithmWithMultilevelRefinement(int maxNIterations)
    {
        return runIteratedLouvainAlgorithmWithMultilevelRefinement(maxNIterations, new Random());
    }

    public boolean runIteratedLouvainAlgorithmWithMultilevelRefinement(int maxNIterations, Random random)
    {
        boolean update;
        int i;

        i = 0;
        do
        {
            update = runLouvainAlgorithmWithMultilevelRefinement(random);
            i++;
        }
        while ((i < maxNIterations) && update);
        return ((i > 1) || update);
    }

    public boolean runSmartLocalMovingAlgorithm()
    {
        return runSmartLocalMovingAlgorithm(new Random());
    }

    public boolean runSmartLocalMovingAlgorithm(Random random)
    {
        boolean update;
        int i, j, k;
        int[] nNodesPerClusterReducedNetwork;
        int[][] nodePerCluster;
        Network[] subnetwork;
        VOSClusteringTechnique VOSClusteringTechnique;

        if (network.nNodes == 1)
            return false;

        update = runLocalMovingAlgorithm(random);

        if (clustering.nClusters < network.nNodes)
        {
            subnetwork = network.createSubnetworks(clustering);

            nodePerCluster = clustering.getNodesPerCluster();

            clustering.nClusters = 0;
            nNodesPerClusterReducedNetwork = new int[subnetwork.length];
            for (i = 0; i < subnetwork.length; i++)
            {
                VOSClusteringTechnique = new VOSClusteringTechnique(subnetwork[i], resolution);

                VOSClusteringTechnique.runLocalMovingAlgorithm(random);

                for (j = 0; j < subnetwork[i].nNodes; j++)
                    clustering.cluster[nodePerCluster[i][j]] = clustering.nClusters + VOSClusteringTechnique.clustering.cluster[j];
                clustering.nClusters += VOSClusteringTechnique.clustering.nClusters;
                nNodesPerClusterReducedNetwork[i] = VOSClusteringTechnique.clustering.nClusters;
            }

            VOSClusteringTechnique = new VOSClusteringTechnique(network.createReducedNetwork(clustering), resolution);

            i = 0;
            for (j = 0; j < nNodesPerClusterReducedNetwork.length; j++)
                for (k = 0; k < nNodesPerClusterReducedNetwork[j]; k++)
                {
                    VOSClusteringTechnique.clustering.cluster[i] = j;
                    i++;
                }
            VOSClusteringTechnique.clustering.nClusters = nNodesPerClusterReducedNetwork.length;

            update |= VOSClusteringTechnique.runSmartLocalMovingAlgorithm(random);

            clustering.mergeClusters(VOSClusteringTechnique.clustering);
        }

        return update;
    }

    public boolean runIteratedSmartLocalMovingAlgorithm(int nIterations)
    {
        return runIteratedSmartLocalMovingAlgorithm(nIterations, new Random());
    }

    public boolean runIteratedSmartLocalMovingAlgorithm(int nIterations, Random random)
    {
        boolean update;
        int i;

        update = false;
        for (i = 0; i < nIterations; i++)
            update |= runSmartLocalMovingAlgorithm(random);
        return update;
    }

    public int removeCluster(int cluster)
    {
        double maxQualityFunction, qualityFunction;
        double[] clusterWeight, totalEdgeWeightPerCluster;
        int i, j;

        clusterWeight = new double[clustering.nClusters];
        totalEdgeWeightPerCluster = new double[clustering.nClusters];
        for (i = 0; i < network.nNodes; i++)
        {
            clusterWeight[clustering.cluster[i]] += network.nodeWeight[i];
            if (clustering.cluster[i] == cluster)
                for (j = network.firstNeighborIndex[i]; j < network.firstNeighborIndex[i + 1]; j++)
                    totalEdgeWeightPerCluster[clustering.cluster[network.neighbor[j]]] += network.edgeWeight[j];
        }

        i = -1;
        maxQualityFunction = 0;
        for (j = 0; j < clustering.nClusters; j++)
            if ((j != cluster) && (clusterWeight[j] > 0))
            {
                qualityFunction = totalEdgeWeightPerCluster[j] / clusterWeight[j];
                if (qualityFunction > maxQualityFunction)
                {
                    i = j;
                    maxQualityFunction = qualityFunction;
                }
            }

        if (i >= 0)
        {
            for (j = 0; j < network.nNodes; j++)
                if (clustering.cluster[j] == cluster)
                    clustering.cluster[j] = i;
            if (cluster == clustering.nClusters - 1)
                clustering.nClusters = Arrays2.calcMaximum(clustering.cluster) + 1;
        }

        return i;
    }

    public void removeSmallClusters(int minNNodesPerCluster)
    {
        int i, j, k;
        int[] nNodesPerCluster;
        VOSClusteringTechnique VOSClusteringTechnique;

        VOSClusteringTechnique = new VOSClusteringTechnique(network.createReducedNetwork(clustering), resolution);

        nNodesPerCluster = clustering.getNNodesPerCluster();

        do
        {
            i = -1;
            j = minNNodesPerCluster;
            for (k = 0; k < VOSClusteringTechnique.clustering.nClusters; k++)
                if ((nNodesPerCluster[k] > 0) && (nNodesPerCluster[k] < j))
                {
                    i = k;
                    j = nNodesPerCluster[k];
                }

            if (i >= 0)
            {
                j = VOSClusteringTechnique.removeCluster(i);
                if (j >= 0)
                    nNodesPerCluster[j] += nNodesPerCluster[i];
                nNodesPerCluster[i] = 0;
            }
        }
        while (i >= 0);

        clustering.mergeClusters(VOSClusteringTechnique.clustering);
    }
}
