# -*- coding: utf-8 -*-
"""
Created on Sat Apr 14 20:47:53 2018

@author: Jinglin
"""

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


'''print graph123'''

'''print graph123.edge_betweenness(directed=False)'''

'''implementLouvain = graph123.community_multilevel()

print (implementLouvain)
outputModularity = graph123.modularity(implementLouvain)
print("Modularity Optimal Value",outputModularity)'''

'''print graph123.clusters().membership'''

old_modularity = old_modularity = graph123.modularity(graph123.clusters().membership)
new_modularity = 1.0

while (new_modularity - old_modularity) > 0:
    
    old_modularity = graph123.modularity(graph123.clusters().membership)

    edge_betweeness = graph123.edge_betweenness(directed=False)

    largest_edge = max(edge_betweeness)

    index_largest_edge = edge_betweeness.index(max(edge_betweeness))

    '''print index_largest_edge'''

    '''print largest_edge'''

    graph123.delete_edges(index_largest_edge)

    '''print graph123.ecount()'''
    
    new_clusters = graph123.clusters()
    
    new_modularity = graph123.modularity(graph123.clusters().membership)

    '''print new_clusters'''

    print new_modularity


print graph123.clusters()