# -*- coding: utf-8 -*-
"""
Created on Sun Mar 25 15:54:51 2018

@author: lusha
"""

import os
import pygrid
from pygrid import *
import igraph
import numpy

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
    
'''
laplacian=graph123.laplacian()
[w,v]=numpy.linalg.eig(laplacian)
sorted_w=sorted(w)
print(sorted_w)
'''

# our algorithm

g=graph123.as_undirected()
mod=g.modularity(g.clusters().membership)
edgeset=igraph.EdgeSeq(g)
iteration=0

while True:
    deltamod=[]
    
    edge_list = []
    for i in edgeset:
        edge_list.append(i.tuple)
    
    for i in edge_list:
        g.delete_edges([i])
        newmod=g.modularity(g.clusters().membership)
        deltamod.append(newmod-mod)
        g.add_edges([i])
    
    maxdeltamod=max(deltamod)
    maxdeltamodindex=deltamod.index(maxdeltamod)
    print maxdeltamod
    if maxdeltamod > 0:
        edgeToDelete=edgeset[maxdeltamodindex].tuple
        g.delete_edges([edgeToDelete])
        mod=g.modularity(g.clusters().membership)
        edgeset=igraph.EdgeSeq(g)
        iteration=iteration+1
    else:
        break
    
print(g.clusters())
print(iteration)
  


