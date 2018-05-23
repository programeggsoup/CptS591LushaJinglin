/**
 * ModularityOptimizer
 *
 * @author Ludo Waltman
 * @author Nees Jan van Eck
 * @version 1.3.0, 08/31/15
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ModularityOptimizer

{
    static String qsupplyFileName;
    static String qdemandFileName;
    static String svqFileName;

    public static void main(String[] args) throws IOException
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

        if (args.length == 12)
        {
			//data\pbmc_raw9.txt data\output.txt 1 1 2 10 500 336 1 data\9busQsupply.txt data\9busQdemand.txt data\9busSVQ.txt
            inputFileName = args[0];	//input nodes and edges, we use data\pbmc_raw9.txt
            outputFileName = args[1];	//we use data\output.txt
            modularityFunction = Integer.parseInt(args[2]);	//which function to choose standard(1), alternative(2), we choose 1
            resolution = Double.parseDouble(args[3]);	//we choose 1.0
            algorithm = Integer.parseInt(args[4]);	//which algorithm to choose Louvain(1), Multilevel(2), smart local moving(3), we choose 2
            nRandomStarts = Integer.parseInt(args[5]);	//how many start point we randomly choose, we input 10
            nIterations = Integer.parseInt(args[6]);	//times of loops, we input 10
            randomSeed = Long.parseLong(args[7]);	//use to fix random at each round, we input 336
            printOutput = (Integer.parseInt(args[8]) > 0);	//whether to print output, no(0), yes(1), we choose 1
            qsupplyFileName = args[9];	//Qsupply file name 
            qdemandFileName = args[10];		//Qdemand file name
            svqFileName = args[11];	// SVQ file name

            if (printOutput)
            {
                System.out.println("Modularity Optimizer version 1.3.0 by Ludo Waltman and Nees Jan van Eck");
                System.out.println();
            }
        }
        else
        {
            console = System.console();
            System.out.println("Modularity Optimizer version 1.3.0 by Ludo Waltman and Nees Jan van Eck");
            System.out.println();
            inputFileName = console.readLine("Input file name: ");
            outputFileName = console.readLine("Output file name: ");
            modularityFunction = Integer.parseInt(console.readLine("Modularity function (1 = standard; 2 = alternative): "));
            resolution = Double.parseDouble(console.readLine("Resolution parameter (e.g., 1.0): "));
            algorithm = Integer.parseInt(console.readLine("Algorithm (1 = Louvain; 2 = Louvain with multilevel refinement; 3 = smart local moving): "));
            nRandomStarts = Integer.parseInt(console.readLine("Number of random starts (e.g., 10): "));
            nIterations = Integer.parseInt(console.readLine("Number of iterations (e.g., 10): "));
            randomSeed = Long.parseLong(console.readLine("Random seed (e.g., 0): "));
            printOutput = (Integer.parseInt(console.readLine("Print output (0 = no; 1 = yes): ")) > 0);
            System.out.println();
        }

        if (printOutput)
        {
            System.out.println("Reading input file...");
            System.out.println();
        }

        network = readInputFile(inputFileName, modularityFunction);

        if (printOutput)
        {
            System.out.format("Number of nodes: %d%n", network.getNNodes());
            System.out.format("Number of edges: %d%n", network.getNEdges());
            System.out.println();
            System.out.println("Running " + ((algorithm == 1) ? "Louvain algorithm" : ((algorithm == 2) ? "Louvain algorithm with multilevel refinement" : "smart local moving algorithm")) + "...");
            System.out.println();
        }

		//we use standard modularity Function, thus resolution2 = 1
        resolution2 = ((modularityFunction == 1) ? (resolution / (2 * network.getTotalEdgeWeight() + network.totalEdgeWeightSelfLinks)) : resolution);

        beginTime = System.currentTimeMillis();
        clustering = null;
        maxModularity = Double.NEGATIVE_INFINITY;
        random = new Random(randomSeed);
        for (i = 0; i < nRandomStarts; i++)
        {
            if (printOutput && (nRandomStarts > 1))
                System.out.format("Random start: %d%n", i + 1);

            VOSClusteringTechnique = new VOSClusteringTechnique(network, resolution2);

            j = 0;
            update = true;
            do
            {
                if (printOutput && (nIterations > 1))
                    System.out.format("Iteration: %d%n", j + 1);

                if (algorithm == 1)
                    update = VOSClusteringTechnique.runLouvainAlgorithm(random);
                else if (algorithm == 2)
                    update = VOSClusteringTechnique.runLouvainAlgorithmWithMultilevelRefinement(random);
                else if (algorithm == 3)
                    VOSClusteringTechnique.runSmartLocalMovingAlgorithm(random);
                j++;

                modularity = VOSClusteringTechnique.calcQualityFunction();

                if (printOutput && (nIterations > 1))
                    System.out.format("Modularity: %.4f%n", modularity);
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
            System.out.format("Elapsed time: %f ms%n", Math.round((endTime - beginTime))/10.0);
            System.out.println();
            System.out.println("Writing output file...");
            System.out.println();
        }

        writeOutputFile(outputFileName, clustering);
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

		//get total lines
        nLines = 0;
        while (bufferedReader.readLine() != null)
            nLines++;

        bufferedReader.close();

        bufferedReader = new BufferedReader(new FileReader(fileName));

		//new two arrays, size = number of edges
        node1 = new int[nLines];
        node2 = new int[nLines];
		//edgeWeight1, size = number of edges
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
			//if there're edges weights the data should be like "node1	node2	edgeweight", but we don't have edge weight now, so the whole edge weight will be like 1
            edgeWeight1[j] = (splittedLine.length > 2) ? Double.parseDouble(splittedLine[2]) : 1;
        }
		// node index is from 0 to n-1, the node arrays size should be n
        nNodes = i + 1;
		/** 
		*	node1 = {0,3,4,2,5,6,1,7,3}
		*	node2 = {3,4,5,5,6,7,7,8,8}
		*	edgeweight = {1.0 ×9}
		*/

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
