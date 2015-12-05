import os
import timeit
import sys

code = 'package automaton; \n\
  public class KillCompiler {  \n\
  static class A<Recurse_1,Recurse_2>{ \n\
    A<A<Recurse_1,Recurse_2>,A<Recurse_1,Recurse_2>> d() { return null; } \n\
  } \n\
  \n\
  public static void main(String[] args) { \n\
  A<?,?> s =new A<String,String>()%s; \n\
   \n\
  } \n\
}'

def n_invocations(n): 
  return code %('.d()'*n,)

runtimes = []
for i in xrange(0,int(sys.argv[1])):
  f = open('KillCompiler.java','w')
  f.write(n_invocations(i))
  f.flush()
  f.close()
  current = [timeit.timeit(r'os.system("javac KillCompiler.java")','import os',number=1)]
  print '%i,%f,' %(i,current[0])
  runtimes += current
print runtimes
