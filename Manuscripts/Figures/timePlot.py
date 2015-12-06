import os
import timeit
import sys

code = 'package automaton; \n\
  public class $ {  \n\
  static interface Cons<Car,Cdr>{ \n\
    Cons<Cons<Car,Cdr>,Cons<Car,Cdr>> d(); \n\
  } \n\
  \n\
  public static void main(String[] args) { \n\
  ((Cons<?,?>)null)%s; \n\
   \n\
  } \n\
}'

def n_invocations(n): 
  return code %('.d()'*n,)

if len(sys.argv)==1:
  raise Exception('argv is empty. First parameter must be the maximal sequence length')
runtimes = []
output = 'kill.csv'

out = open(output,'w')
out.write('x,y')
for i in xrange(1,1+int(sys.argv[1])):
  f = open('$.java','w')
  f.write(n_invocations(i))
  f.flush()
  f.close()
  current = [timeit.timeit(r'os.system("javac $.java")','import os',number=1)]
  out.write(',\n%i,%f' %(i,current[0]))
  runtimes += current
print 'output printed to file %s' %(output,)
print runtimes
