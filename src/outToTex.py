s = raw_input()
lf = '\\\\'
cases = '\\begin{cases}%s\\end{cases}'
v,e,_ = s.split(']')
v = v[1:]
e = e[2:]
v = v.replace(',',lf)
e = e.replace('), (',')'+lf+'(')

print cases %(v,)
print '\,\,\,\,\,\,\,\,\,\,\,\,\,\,\,\,\,\,\,\,\,\,\,\,\,\,'
print cases %(e,)
