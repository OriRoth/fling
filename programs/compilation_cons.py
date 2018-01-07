import os
import timeit
import sys

code = 'public class $ {  \n\
  static interface Cons<Car,Cdr>{ \n\
    Cons<Cons<Car,Cdr>,Cons<Car,Cdr>> d(); \n\
  } \n\
  \n\
  public static void main(String[] args) { \n\
  System.out.println(((Cons<?,?>)null)%s); \n\
   \n\
  } \n\
}'

def n_invocations(n): 
  return code %('.d()'*n,)

if len(sys.argv)!=4:
  raise Exception('3 parameters required')
runtimes = []
output = sys.argv[3]

out = open(output,'w+')
out.write('x,y')
for i in xrange(1,1+int(sys.argv[1])):
  f = open('junk/$.java','w')
  f.write(n_invocations(i))
  f.close()
  current = [timeit.timeit(r'os.system("%s")' %(sys.argv[2],),'import os',number=1)]
  out.write(',\n%i,%f' %(i,current[0]))
  runtimes += current

out.close()

# print 'output printed to file %s' %(output,)
# print runtimes

