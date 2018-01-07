import os
import timeit
import sys

if len(sys.argv)<2:
  raise Exception('at least 1 parameter required')
output = sys.argv[1]

out = open(output,'w+')

for name, command in  zip(*[iter(sys.argv[2:])]*2):
  out.write('x,y')
  current = [timeit.timeit(r'os.system("%s")' %(command,),'import os',number=1)]
  out.write(',\n%s,%f' %(name,current[0]))

out.close()

