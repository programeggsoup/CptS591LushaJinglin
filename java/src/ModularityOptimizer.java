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

public class ModularityOptimizer
{

    public String test(String inputfilename,String outputfilename,String modularityfunction,String res,String alg,String nrandom,String niter,String seed,String output,String qsupply,String qdemand,String svqinfo) throws IOException
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
        VOSClusteringTechnique VOSClusteringTechnique;
        String qsupplyFileName;
        String qdemandFileName;
        String svqFileName;

        inputFileName = inputfilename;
        outputFileName = outputfilename;
        modularityFunction = Integer.parseInt(modularityfunction);
        resolution = Double.parseDouble(res);
        algorithm = Integer.parseInt(alg);
        nRandomStarts = Integer.parseInt(nrandom);
        nIterations = Integer.parseInt(niter);
        randomSeed = Long.parseLong(seed);
        printOutput = (Integer.parseInt(output) > 0);
        
        /* reactive power information input */
        qsupplyFileName = qsupply;
        qdemandFileName = qdemand;
        svqFileName = svqinfo;

        System.out.println("Modularity Optimizer version 1.3.0 by Ludo Waltman and Nees Jan van Eck");

        if (printOutput)
        {
            System.out.println("Reading input file...");
        }

        network = readInputFile(inputFileName, modularityFunction);

        // read power information file
        PowerInformation powerInfo = new PowerInformation();
        powerInfo.readPowerFile(qsupplyFileName, qdemandFileName, svqFileName, network);

        // write solution into file
        BufferedWriter bufferedWriter;
        bufferedWriter = new BufferedWriter(new FileWriter(outputFileName));

        if (printOutput)
        {
            bufferedWriter.write("Number of nodes: " + String.format("%d",network.getNNodes()) + "\r\n");
            bufferedWriter.write("Number of edges: " + String.format("%d", network.getNEdges()) + "\r\n");
            bufferedWriter.write("Running " + ((algorithm == 1) ? "Louvain algorithm" : ((algorithm == 2) ? "Louvain algorithm with multilevel refinement" : "smart local moving algorithm")) + "...\r\n");
            bufferedWriter.newLine();
        }

        resolution2 = ((modularityFunction == 1) ? (resolution / (2 * network.getTotalEdgeWeight() + network.totalEdgeWeightSelfLinks)) : resolution);

        beginTime = System.currentTimeMillis();
        clustering = null;
        maxModularity = Double.NEGATIVE_INFINITY;
        random = new Random(randomSeed);
        for (i = 0; i < nRandomStarts; i++)
        {
            if (printOutput && (nRandomStarts > 1))
                bufferedWriter.write("Random start: " + String.format("%d", i+1) + "\r\n");

            VOSClusteringTechnique = new VOSClusteringTechnique(network, resolution2);

            j = 0;
            update = true;
            do
            {
                if (printOutput && (nIterations > 1))
                    bufferedWriter.write("Iteration: " + String.format("%d", j+1) + "\r\n");

                if (algorithm == 1)
                    update = VOSClusteringTechnique.runLouvainAlgorithm(random);
                else if (algorithm == 2)
                    update = VOSClusteringTechnique.runLouvainAlgorithmWithMultilevelRefinement(random);
                else if (algorithm == 3)
                    VOSClusteringTechnique.runSmartLocalMovingAlgorithm(random);
                j++;

                modularity = VOSClusteringTechnique.calcQualityFunction();

                if (printOutput && (nIterations > 1))
                    bufferedWriter.write("Modularity: "+ String.format(" %.8f", modularity) + "\r\n");
            }
            while ((j < nIterations) && update);

            if (modularity > maxModularity)
            {
                clustering = VOSClusteringTechnique.getClustering();
                maxModularity = modularity;
            }

            if (printOutput && (nRandomStarts > 1))
            {
                if (nIterations == 1)
                    bufferedWriter.write("Modularity: "+ String.format(" %.8f", modularity) + "\r\n");
                bufferedWriter.newLine();
            }
        }
        endTime = System.currentTimeMillis();

        if (printOutput)
        {
            if (nRandomStarts == 1)
            {
                if (nIterations > 1)
                    bufferedWriter.newLine();
                bufferedWriter.write("Modularity: "+ String.format(" %.8f", maxModularity) + "\r\n");
            }
            else
                bufferedWriter.write("Maximum modularity in " + String.format("%d", nRandomStarts) + " random starts: " + String.format("%.8f", maxModularity) + "\r\n");
            bufferedWriter.write("Elapsed time: " +  String.format("%.8f", Math.round((endTime - beginTime))/(nRandomStarts * 1.0)) +"ms\r\n");
            bufferedWriter.write("Number of Clusters: " + String.format("%d", clustering.getNClusters()) + "\r\n");

            for (i = 0; i < clustering.nClusters; i++)
            {
                bufferedWriter.write(Arrays.toString(clustering.getNodesPerCluster()[i]));
                bufferedWriter.newLine();
            }
        }
        bufferedWriter.close();

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

}
