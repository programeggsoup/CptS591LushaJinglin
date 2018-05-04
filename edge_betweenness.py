# -*- coding: utf-8 -*-
"""
Created on Sat Mar 31 19:55:16 2018

@author: Jinglin
"""

import os
import pygrid
from pygrid import *
import igraph
import matplotlib.pyplot as plt
import numpy as np
from sklearn import cluster, datasets, mixture

if __name__ == '__main__':
    path = os.getcwd()
    gl = gridlabObject(filename="123IEEE_python.glm", workingDirectory=path, header_file=os.path.join("IEEE_123_TestFeeder", "123nodeglmheader.txt"))
    hardFiles = ['123nodeglmconductors.txt', '123nodeglmlinespacing.txt', '123nodeglmlineconfig.txt', '123nodeglmlines.txt', '123nodeglmloads.txt', '123nodeglmnodes.txt', '123nodeglmtransformer.txt', '123nodeglmcapacitors.txt', '123nodeglmregulators.txt']
    for filename in hardFiles:
        filename = os.path.join("IEEE_123_TestFeeder", filename)
        newObjs = gl.read_glm_file(filename)
        print(filename)
        for objType in newObjs.keys():
            if objType not in gl.objects.keys():
                gl.objects[objType] = {}
            for obj in newObjs[objType].keys():
                gl.objects[objType][obj] = newObjs[objType][obj]
    for typeObj in gl.objects.keys():
        allobjects = sorted(gl.objects[typeObj].keys())
        for obj in gl.objects[typeObj].keys():
            if obj == "node350":
                pass #we're skipping this node for some reason
            if obj == "node610":
                gl.objects[typeObj][obj]["nominal_voltage"] = "277.1"
            outline = gl.object_string(gl.objects[typeObj][obj], typeObj)
            with open(gl.outFileName, 'a') as outFile:
                outFile.write(outline + "\n")
                
graph123=igraph.Graph() 
ob=gl.objects
overheadline=ob['overhead_line']
graph123.vs['name']=[]
for k,v in overheadline.items():
    linefrom=v['from']
    lineto=v['to']
    vs=graph123.vs
    if linefrom not in vs['name']:
        graph123.add_vertex(linefrom)
    if lineto not in vs['name']:
        graph123.add_vertex(lineto)
    graph123.add_edge(linefrom,lineto)
    
undergroundline=ob['underground_line']
for k,v in undergroundline.items():
    linefrom=v['from']
    lineto=v['to']
    vs=graph123.vs
    if linefrom not in vs['name']:
        graph123.add_vertex(linefrom)
    if lineto not in vs['name']:
        graph123.add_vertex(lineto)
    graph123.add_edge(linefrom,lineto)

regulators=ob['regulator']
for k,v in regulators.items():
    regulatorfrom=v['from']
    regulatorto=v['to']
    vs=graph123.vs
    if regulatorfrom not in vs['name']:
        graph123.add_vertex(regulatorfrom)
    if regulatorto not in vs['name']:
        graph123.add_vertex(regulatorto)
    graph123.add_edge(regulatorfrom,regulatorto)
    
transformer=ob['transformer']
for k,v in transformer.items():
    transformerfrom=v['from']
    transformerto=v['to']
    vs=graph123.vs
    if transformerfrom not in vs['name']:
        graph123.add_vertex(transformerfrom)
    if transformerto not in vs['name']:
        graph123.add_vertex(transformerto)
    graph123.add_edge(transformerfrom,transformerto)
    
"""print(graph123)"""
'''print('number of edges:', graph123.ecount())'''
"""print('number of nodes:', graph123.vcount())
print('number of edges:', graph123.ecount())
print('Whether the graph is weighted:', graph123.is_weighted())
print('Whether the graph is directed:', graph123.is_directed())
print('Whether the graph is connected:', graph123.is_connected())"""
"""print('Number of strong connected components:', graph123.clusters())"""
"""print('The Maximum degree:', graph123.maxdegree())
print('Average path length', graph123.average_path_length(directed=False, unconn=False))
print('The diameter:', graph123.diameter(directed=False, unconn=False, weights=None))
print('The local clustering coefficient:', graph123.transitivity_local_undirected(vertices=None, mode="nan", weights=None))
print('The global clustering coefficient:', graph123.transitivity_undirected(mode="nan"))
print('The eigenvectors centrality:')

ev = graph123.evcent(directed=False, scale=True, weights=None, return_eigenvalue=True)
print('The eigenvalues:', ev[1])
print(sorted(ev[0]))

dist = graph123.degree_distribution()
print('The degree distribution:', dist)"""
"""matplotlib inline"""
"""dist = np.random.normal(size = 1000)
plt.hist(dist, normed=True, bins=30)
plt.ylabel('Probability');"""


ed = graph123.community_edge_betweenness(directed=False, weights=None)

print(ed)
print('format: ',ed.format())
print('summary: ',ed.summary())
print('clustering:')
print(ed.as_clustering())
print('optimal count:', ed.optimal_count)
print graph123.modularity(ed.as_clustering())

'''
membership = ed.as_clustering().membership'''

'''import csv
from itertools import izip

writer = csv.writer(open("output.csv", "wb"))
for name, membership in izip(graph123.vs["name"], membership):
    writer.writerow([name, membership])'''

"""MEJ Newman and M Girvan: Finding and evaluating community structure in networks. Phys Rev E 69 026113, 2004."""
"""print(graph123.modularity())"""

"""igraph.plot(graph123)"""



'''print('Using Louvain algorithm')
lv = graph123.community_multilevel(return_levels=True)
print(lv[0])
print(lv[1])
print(lv[2])

print lv'''

'''print("Plot")
i = graph123.community_infomap()
colors = ["#E41A1C", "#377EB8", "#4DAF4A", "#984EA3", "#FF7F00","#E41A1C", "#377EB8", "#4DAF4A", "#984EA3", "#FF7F00"
          ,"#E22A1C", "#366EB8", "#4D354A", "#984223", "#FCCF00","#E4571C", "#3222B8", "#4DA111", "#123EA3", "#FF7231"
          ,"#E22444", "#3234B8", "#4D994A", "#9E4013", "#FEEE00","#E45DDD", "#3E62B8", "#5D90A1", "#12BBA3", "#F15331"
          ]
graph123.vs['color'] = [None]
for clid, cluster in enumerate(i):
    for member in cluster:
        graph123.vs[member]['color'] = colors[clid]
graph123.vs['frame_width'] = 0
igraph.plot(graph123)'''

'''print('Returns a graph where each cluster is contracted into a single vertex.: ')
print(lv.cluster_graph())
print('crossing: ',lv.crossing())
print('Recalculates the stored modularity value.')
print(lv.recalculate_modularity())
print('optimal count:', lv.optimal_count)
print(lv._plot_item)'''

'''print('Label Propagation')
lp = graph123.community_label_propagation();
print(lp)'''

'''print('Spectral Clustering')'''
'''sc = graph123.sp'''
'''print(lp)''' 