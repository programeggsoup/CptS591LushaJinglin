# -*- coding: utf-8 -*-
"""
Created on Wed Apr 18 20:56:01 2018

@author: Jinglin
"""

import csv
import igraph
import numpy
import matplotlib.pyplot as plt
import pylab as pl
import louvain
import time


graph339bus=igraph.Graph() 
    
with open('39busnode.csv','rb') as csvfileNode:
    csvreaderNode=csv.reader(csvfileNode)
    mycsvNode=list(csvreaderNode)
    for row in mycsvNode:
        graph339bus.add_vertex(name=row[0])
        
nodeNumber=graph339bus.vcount()

SVQ=numpy.zeros((nodeNumber,nodeNumber))
with open('39busbranch.csv','rb') as csvfileBranch:
    csvreaderBranch=csv.reader(csvfileBranch)
    mycsvBranch=list(csvreaderBranch)
    for row in mycsvBranch:
        B=(1/complex(float(row[2]),float(row[3]))).imag
        graph339bus.add_edge(row[0],row[1])
        SVQ[int(row[0])-1,int(row[1])-1]=-B
        SVQ[int(row[1])-1,int(row[0])-1]=-B
        
with open('39busQ.csv','rb') as csvfileQ:
    csvreaderQ=csv.reader(csvfileQ)
    mycsvQ=list(csvreaderQ)
    for row in mycsvQ:
        #print row[1]
        graph339bus.vs.select(int(row[0])-1)["Qsupply"]=row[1]
        graph339bus.vs.select(int(row[0])-1)["Qdemand"]=row[2]
        

# using Louvain
start_time = time.time()
clusters=graph339bus.community_multilevel()
mod=graph339bus.modularity(clusters)
end_time = time.time()
print 'Using Lovain:',clusters
print mod
print (end_time - start_time)


# using Betweeness
start_time = time.time()
vd=graph339bus.community_edge_betweenness(clusters=None, directed=True, weights=None)
mod=graph339bus.modularity(vd.as_clustering())
end_time = time.time()
print 'Using Betweeness:',vd.as_clustering()
print mod
print (end_time - start_time)

# using Label propagation
start_time = time.time()
clusters=graph339bus.community_label_propagation(weights=None, initial=None, fixed=None)
mod=graph339bus.modularity(clusters)
end_time = time.time()
print 'Using Label propagation',clusters
print mod
print (end_time - start_time)

#louvain.louvain(graph339bus,SVQ)
    
"""print(graph339bus)"""

print('number of nodes:', graph339bus.vcount())
print('number of edges:', graph339bus.ecount())
print('Whether the graph is weighted:', graph339bus.is_weighted())
print('Whether the graph is directed:', graph339bus.is_directed())
print('Whether the graph is connected:', graph339bus.is_connected())
"""print('Number of strong connected components:', graph339bus.clusters())"""
print('The Maximum degree:', graph339bus.maxdegree())
print('Average path length', graph339bus.average_path_length(directed=False, unconn=False))
print('The diameter:', graph339bus.diameter(directed=False, unconn=False, weights=None))
print('The local clustering coefficient:', graph339bus.transitivity_local_undirected(vertices=None, mode="nan", weights=None))
print('The global clustering coefficient:', graph339bus.transitivity_undirected(mode="nan"))
print('The eigenvectors centrality:')


laplacian = graph339bus.laplacian()
[w,v] = numpy.linalg.eig(laplacian)
sorted_w = sorted(w)
print('The eigenvalues:', sorted_w)

dist = graph339bus.degree_distribution()
print('The degree distribution:')
print dist



data = list(dist.bins())

print data

xs, ys = zip(*[(left, count) for left, _, count in 
dist.bins()])
pl.bar(xs, ys)
pl.show()