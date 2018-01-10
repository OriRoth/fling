import os
import timeit
import sys
import shutil

code = 'package org.spartan.fajita.revision.examples.usage;\n\
import static org.spartan.fajita.revision.junk.Exp2.a;\n\
public class Exp2 {\n\
  public static void main(String[] args) {\n\
    System.out.println(\n\
      a()%s //\n\
    );\n\
  }\n\
}'

def n_invocations(n): 
  return code %('.a()'*(n-1),)

outdir = 'out/graphs/testcases/Exp2/'
shutil.rmtree(outdir)
os.mkdir(outdir)

for name, command in zip(*[iter(sys.argv[2:])]*2):
  output = outdir + name + '.csv'
  out = open(output,'w+')
  out.write('x,y')
  for i in xrange(1,1+int(sys.argv[1])):
    f = open('src/test/java/org/spartan/fajita/revision/examples/usage/Exp2.java','w+')
    f.write(n_invocations(i))
    f.close()
    current = [timeit.timeit(r'os.system("%s")' %(command,),'import os',number=1)]
    out.write(',\n%i,%f' %(i,current[0]))
    os.remove('src/test/java/org/spartan/fajita/revision/examples/usage/Exp2.java')
  out.close()

