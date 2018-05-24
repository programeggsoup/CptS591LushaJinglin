# -*- coding: utf-8 -*-
"""
Created on Tue May 15 10:47:30 2018

@author: Jinglin
"""

import jpype 
jvmPath = jpype.getDefaultJVMPath() 
classpath = "C:\\Users\\user\\OneDrive\\Documents\\eclipse-workspace\\CptS591LushaJinglin\\java\\out\\production\\multilevel"
jvmArg = "-Djava.class.path=" + classpath 
if not jpype.isJVMStarted():
    jpype.startJVM(jvmPath,jvmArg)
#jpype.java.lang.System.out.println("hello world!") 
#jpype.shutdownJVM()
ModularityOptimizer = jpype.JClass("ModularityOptimizer3")     
m = ModularityOptimizer()   
try:   
    print m.test("C:\\Users\\user\\OneDrive\\Documents\\eclipse-workspace\\CptS591LushaJinglin\\python\\networkInfo.txt","C:\\Users\\user\\OneDrive\\Documents\\eclipse-workspace\\CptS591LushaJinglin\\python\\output.txt","1","1","2","10000","10","336","1","C:\\Users\\user\\OneDrive\\Documents\\eclipse-workspace\\CptS591LushaJinglin\\python\\Qsupply.txt","C:\\Users\\user\\OneDrive\\Documents\\eclipse-workspace\\CptS591LushaJinglin\\python\\Qdemand.txt","C:\\Users\\user\\OneDrive\\Documents\\eclipse-workspace\\CptS591LushaJinglin\\python\\SVQ.txt")
except jpype.JavaException, ex:
    print ex.javaClass(), ex.message()
    print ex.stacktrace() 

jpype.shutdownJVM()