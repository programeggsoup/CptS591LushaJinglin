# -*- coding: utf-8 -*-
"""
Created on Sun Apr 15 15:34:30 2018

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
    

'''copy a new graph without edge'''
new_graph = igraph.Graph()
new_graph.add_vertices(graph123.vs['name'])
print new_graph

'''Paper2 begin, initial modified modularity'''
modularity_0 = new_graph.modularity(new_graph.clusters().membership)
print modularity_0
print new_graph.clusters()

'''repeat select an edge(e)'''
edge_seq = igraph.EdgeSeq(graph123)
delta_modularity = []
for i in edge_seq:
    new_graph.add_edges([i.tuple]);
    new_modularity = new_graph.modularity(new_graph.clusters().membership)
    delta_modularity.append(new_modularity - modularity_0)
    new_graph.delete_edges([i.tuple])
    '''print new_modularity'''

'''print max(delta_modularity)
print edge_seq[delta_modularity.index(max(delta_modularity))]'''
'''print  list(old_edges)'''



