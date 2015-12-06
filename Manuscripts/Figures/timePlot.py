import os
import timeit
import sys

code = 'package automaton; \n\
  public class _ {  \n\
  static interface Cons<Car,Cdr>{ \n\
    Cons<Cons<Car,Cdr>,Cons<Car,Cdr>> d(); \n\
  } \n\
  \n\
  public static void main(String[] args) { \n\
  Cons<?,?> s =new Cons<String,String>(){ \n\
	public Cons<Cons<String,String>,Cons<String,String>>d() {return null;}\n\
}%s; \n\
   \n\
  } \n\
}'

def n_invocations(n): 
  return code %('.d()'*n,)

runtimes = []
for i in xrange(0,int(sys.argv[1])):
  f = open('_.java','w')
  f.write(n_invocations(i))
  f.flush()
  f.close()
  current = [timeit.timeit(r'os.system("javac _.java")','import os',number=1)]
  print '%i,%f,' %(i,current[0])
  runtimes += current
print runtimes
