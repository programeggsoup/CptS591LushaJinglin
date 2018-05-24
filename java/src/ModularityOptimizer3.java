/**
 * ModularityOptimizer
 *
 * @author Ludo Waltman
 * @author Nees Jan van Eck
 * @version 1.3.0, 08/31/15
 */

import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class ModularityOptimizer3
{
    static double[] Qsupply;
    static double[] Qdemand;
    static double[][] svq;

    public String test(String aa,String ab,String ac,String ad,String ae,String af,String ag,String ah,String ai,String aj,String ak,String al) throws IOException
    {
        boolean printOutput, update;
        Clustering clustering;
        Console console;
        double modularity, maxModularity, resolution, resolution2;
        int algorithm, i, j, modularityFunction, nIterations, nRandomStarts;
        long beginTime, endTime, randomSeed;
        Network network;
        Random random;
        String inputFileName, outputFileName;
        VOSClusteringTechnique3 VOSClusteringTechnique3;
        String qsupplyFileName;
        String qdemandFileName;
        String svqFileName;

        inputFileName = aa;
        outputFileName = ab;
        modularityFunction = Integer.parseInt(ac);
        resolution = Double.parseDouble(ad);
        algorithm = Integer.parseInt(ae);
        nRandomStarts = Integer.parseInt(af);
        nIterations = Integer.parseInt(ag);
        randomSeed = Long.parseLong(ah);
        printOutput = (Integer.parseInt(ai) > 0);
        // reactive power information input
        qsupplyFileName = aj;
        qdemandFileName = ak;
        svqFileName = al;

        System.out.println("Modularity Optimizer version 1.3.0 by Ludo Waltman and Nees Jan van Eck");

        if (printOutput)
        {
            System.out.println("Reading input file...");
            System.out.println();
        }

        network = readInputFile(inputFileName, modularityFunction);

        /* read in the svq, qsupply and qdemand */
        /* qsupply */
        BufferedReader bufferedReader;
        bufferedReader = new BufferedReader(new FileReader(qsupplyFileName)); /* "data/data.txt" */
        Qsupply = new double[network.nNodes];
        String[] tokensQs = bufferedReader.readLine().split("\n");
        for (int ii = 0; ii < tokensQs.length; ii++){
            Qsupply[ii] = Double.parseDouble(tokensQs[ii]);
        }
        bufferedReader.close();

        /* qdemand */
        bufferedReader = new BufferedReader(new FileReader(qdemandFileName));
        Qdemand = new double[network.nNodes];
        String[] tokensQd = bufferedReader.readLine().split("\n");
        for (int ii = 0; ii < tokensQd.length; ii++){
            Qdemand[ii] = Double.parseDouble(tokensQs[ii]);
        }
        bufferedReader.close();

        /* svq */
        String line;
        bufferedReader = new BufferedReader(new FileReader(svqFileName));
        svq = new double[network.nNodes][network.nNodes];
        int count = 0;
        while ((line = bufferedReader.readLine()) != null){
            String[] tokens = line.split("\t");
            double[] svqi = new double[network.nNodes];
            for(int ii = 0; ii < network.nNodes; ii++){
                svqi[ii] = Double.parseDouble(tokens[ii]);
            }
            svq[count] = svqi;
        }
        bufferedReader.close();

        if (printOutput)
        {
            System.out.format("Number of nodes: %d%n", network.getNNodes());
            System.out.format("Number of edges: %d%n", network.getNEdges());
            System.out.println();
            System.out.println("Running " + ((algorithm == 1) ? "Louvain algorithm" : ((algorithm == 2) ? "Louvain algorithm with multilevel refinement" : "smart local moving algorithm")) + "...");
            System.out.println();
        }

        resolution2 = ((modularityFunction == 1) ? (resolution / (2 * network.getTotalEdgeWeight() + network.totalEdgeWeightSelfLinks)) : resolution);

        beginTime = System.currentTimeMillis();
        clustering = null;
        maxModularity = Double.NEGATIVE_INFINITY;
        random = new Random(randomSeed);
        for (i = 0; i < nRandomStarts; i++)
        {
            if (printOutput && (nRandomStarts > 1))
                System.out.format("Random start: %d%n", i + 1);

            VOSClusteringTechnique3 = new VOSClusteringTechnique3(network, resolution2);

            j = 0;
            update = true;
            do
            {
                if (printOutput && (nIterations > 1))
                    System.out.format("Iteration: %d%n", j + 1);

                if (algorithm == 1)
                    update = VOSClusteringTechnique3.runLouvainAlgorithm(random);
                else if (algorithm == 2)
                    update = VOSClusteringTechnique3.runLouvainAlgorithmWithMultilevelRefinement(random);
                else if (algorithm == 3)
                    VOSClusteringTechnique3.runSmartLocalMovingAlgorithm(random);
                j++;

                modularity = VOSClusteringTechnique3.calcQualityFunction();

                if (printOutput && (nIterations > 1))
                    System.out.format("Modularity: %.4f%n", modularity);
            }
            while ((j < nIterations) && update);

            if (modularity > maxModularity)
            {
                clustering = VOSClusteringTechnique3.getClustering();
                maxModularity = modularity;
            }

            if (printOutput && (nRandomStarts > 1))
            {
                if (nIterations == 1)
                    System.out.format("Modularity: %.4f%n", modularity);
                System.out.println();
            }
        }
        endTime = System.currentTimeMillis();

        if (printOutput)
        {
            if (nRandomStarts == 1)
            {
                if (nIterations > 1)
                    System.out.println();
                System.out.format("Modularity: %.4f%n", maxModularity);
            }
            else
                System.out.format("Maximum modularity in %d random starts: %.4f%n", nRandomStarts, maxModularity);
            System.out.format("Number of communities: %d%n", clustering.getNClusters());
            System.out.format("Elapsed time: %f ms%n", Math.round((endTime - beginTime))/(nRandomStarts * 1.0));
            System.out.println();
            //System.out.println("Writing output file...");
            System.out.println();
        }

        //writeOutputFile(outputFileName, clustering);
        return "End of the line";
    }

    private static Network readInputFile(String fileName, int modularityFunction) throws IOException
    {
        BufferedReader bufferedReader;
        double[] edgeWeight1, edgeWeight2, nodeWeight;
        int i, j, nEdges, nLines, nNodes;
        int[] firstNeighborIndex, neighbor, nNeighbors, node1, node2;
        Network network;
        String[] splittedLine;

        bufferedReader = new BufferedReader(new FileReader(fileName));

        nLines = 0;
        while (bufferedReader.readLine() != null)
            nLines++;

        bufferedReader.close();

        bufferedReader = new BufferedReader(new FileReader(fileName));

        node1 = new int[nLines];
        node2 = new int[nLines];
        edgeWeight1 = new double[nLines];
        i = -1;
        for (j = 0; j < nLines; j++)
        {
            splittedLine = bufferedReader.readLine().split("\t");
            node1[j] = Integer.parseInt(splittedLine[0]);
            if (node1[j] > i)
                i = node1[j];
            node2[j] = Integer.parseInt(splittedLine[1]);
            if (node2[j] > i)
                i = node2[j];
            edgeWeight1[j] = (splittedLine.length > 2) ? Double.parseDouble(splittedLine[2]) : 1;
        }
        nNodes = i + 1;

        bufferedReader.close();

        nNeighbors = new int[nNodes];
        for (i = 0; i < nLines; i++)
            if (node1[i] < node2[i])
            {
                nNeighbors[node1[i]]++;
                nNeighbors[node2[i]]++;
            }

        firstNeighborIndex = new int[nNodes + 1];
        nEdges = 0;
        for (i = 0; i < nNodes; i++)
        {
            firstNeighborIndex[i] = nEdges;
            nEdges += nNeighbors[i];
        }
        firstNeighborIndex[nNodes] = nEdges;

        neighbor = new int[nEdges];
        edgeWeight2 = new double[nEdges];
        Arrays.fill(nNeighbors, 0);
        for (i = 0; i < nLines; i++)
            if (node1[i] < node2[i])
            {
                j = firstNeighborIndex[node1[i]] + nNeighbors[node1[i]];
                neighbor[j] = node2[i];
                edgeWeight2[j] = edgeWeight1[i];
                nNeighbors[node1[i]]++;
                j = firstNeighborIndex[node2[i]] + nNeighbors[node2[i]];
                neighbor[j] = node1[i];
                edgeWeight2[j] = edgeWeight1[i];
                nNeighbors[node2[i]]++;
            }

        if (modularityFunction == 1)
            network = new Network(nNodes, firstNeighborIndex, neighbor, edgeWeight2);
        else
        {
            nodeWeight = new double[nNodes];
            Arrays.fill(nodeWeight, 1);
            network = new Network(nNodes, nodeWeight, firstNeighborIndex, neighbor, edgeWeight2);
        }

        return network;
    }

    private static void writeOutputFile(String fileName, Clustering clustering) throws IOException
    {
        BufferedWriter bufferedWriter;
        int i, nNodes;

        nNodes = clustering.getNNodes();

        clustering.orderClustersByNNodes();

        bufferedWriter = new BufferedWriter(new FileWriter(fileName));

        for (i = 0; i < nNodes; i++)
        {
            bufferedWriter.write(Integer.toString(clustering.getCluster(i)));
            bufferedWriter.newLine();
        }

        bufferedWriter.close();
    }
}
