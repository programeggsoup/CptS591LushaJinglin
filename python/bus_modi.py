# -*- coding: utf-8 -*-
"""
Created on Tue May 15 10:47:30 2018

@author: Jinglin
"""

import csv
import igraph
import numpy
import modification
import time
import sys

graph9bus=igraph.Graph() 
    
with open(str(sys.argv[1]) + 'busnode.csv','rb') as csvfileNode:
    csvreaderNode=csv.reader(csvfileNode)
    mycsvNode=list(csvreaderNode)
    for row in mycsvNode:
        graph9bus.add_vertex(name=row[0])
        
nodeNumber=graph9bus.vcount()

Bmatrix=numpy.zeros((nodeNumber,nodeNumber))
SVQ=numpy.zeros((nodeNumber,nodeNumber))
with open(str(sys.argv[1]) + 'busbranch.csv','rb') as csvfileBranch:
    csvreaderBranch=csv.reader(csvfileBranch)
    mycsvBranch=list(csvreaderBranch)
    for row in mycsvBranch:
        B=(1/complex(float(row[2]),float(row[3]))).imag
        graph9bus.add_edge(row[0],row[1])
        Bmatrix[int(row[0])-1,int(row[1])-1]=B
        Bmatrix[int(row[1])-1,int(row[0])-1]=B

#print Bmatrix
for i in range(nodeNumber):
    for j in range(nodeNumber):
        if j!=i:
            Bmatrix[i,i]=Bmatrix[i,i]-Bmatrix[i,j]
        
#print 'Bmtarix',Bmatrix     
SVQ=numpy.linalg.pinv(Bmatrix)
#print SVQ

with open(str(sys.argv[1]) + 'busQ.csv','rb') as csvfileQ:
    csvreaderQ=csv.reader(csvfileQ)
    mycsvQ=list(csvreaderQ)
    for row in mycsvQ:
        #print row[1]
        graph9bus.vs.select(int(row[0])-1)["Qsupply"]=float(row[1])
        graph9bus.vs.select(int(row[0])-1)["Qdemand"]=float(row[2])

# using Louvain
start_time = time.time()
clusters=graph9bus.community_multilevel()
mod=graph9bus.modularity(clusters)

#louvain.louvain(graph9bus,SVQ)
newmod=modification.modification(mod,graph9bus,SVQ,clusters)

end_time = time.time()
print 'Degree distribution: ',graph9bus.degree_distribution()
print 'Running time: ',(end_time - start_time)
print 'New mod: ',newmod
