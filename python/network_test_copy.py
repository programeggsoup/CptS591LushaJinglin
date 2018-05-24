# -*- coding: utf-8 -*-
"""
Created on Tue May 15 10:47:30 2018

@author: Jinglin
"""

import jpype 
jvmPath = jpype.getDefaultJVMPath() 
classpath = "E:\\5.Karen\\CptS591LushaJinglin\\java\\out\\production\\multilevel"
jvmArg = "-Djava.class.path=" + classpath 
if not jpype.isJVMStarted():
    jpype.startJVM(jvmPath,jvmArg)
#jpype.java.lang.System.out.println("hello world!") 
#jpype.shutdownJVM()
ModularityOptimizer = jpype.JClass("ModularityOptimizer3")     
m = ModularityOptimizer()   
try:   
    print m.test("E:\\5.Karen\\CptS591LushaJinglin\\data\\networkInfo.txt","E:\\5.Karen\\CptS591LushaJinglin\\data\\output.txt","1","1","2","10000","10","336","1","E:\\5.Karen\\CptS591LushaJinglin\\data\\Qsupply.txt","E:\\5.Karen\\CptS591LushaJinglin\\data\\Qdemand.txt","E:\\5.Karen\\CptS591LushaJinglin\\data\\SVQ.txt")
except jpype.JavaException, ex:
    print ex.javaClass(), ex.message()
    print ex.stacktrace() 

jpype.shutdownJVM()