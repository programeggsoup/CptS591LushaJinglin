# -*- coding: utf-8 -*-
"""
Created on Sun Mar 25 15:54:51 2018

@author: lusha
"""

import os
import pygrid
from pygrid import *
import igraph
import matplotlib.pyplot as plt
import matplotlib.mlab as mlab 
import pylab as pl
import numpy as np

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

print('number of nodes:', graph123.vcount())
print('number of edges:', graph123.ecount())
print('Whether the graph is weighted:', graph123.is_weighted())
print('Whether the graph is directed:', graph123.is_directed())
print('Whether the graph is connected:', graph123.is_connected())
"""print('Number of strong connected components:', graph123.clusters())"""
print('The Maximum degree:', graph123.maxdegree())
print('Average path length', graph123.average_path_length(directed=False, unconn=False))
print('The diameter:', graph123.diameter(directed=False, unconn=False, weights=None))
print('The local clustering coefficient:', graph123.transitivity_local_undirected(vertices=None, mode="nan", weights=None))
print('The global clustering coefficient:', graph123.transitivity_undirected(mode="nan"))
print('The eigenvectors centrality:')

laplacian = graph123.laplacian()
[w,v] = np.linalg.eig(laplacian)
print('The eigenvalues:', w)
sorted_w = sorted(w)

dist = graph123.degree_distribution()
print('The degree distribution:')
print dist

data = list(dist.bins())

print data

xs, ys = zip(*[(left, count) for left, _, count in 
dist.bins()])
pl.bar(xs, ys)
pl.show()


"""igraph.plot(graph123)"""