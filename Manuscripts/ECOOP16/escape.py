#!/usr/bin/python
import sys
out = u''
normalStyle = [u'\u00a4',u'\u2717',u'\u2713']
while True:
  try: 
    raw = raw_input()  
  except EOFError:
    break
  inp = raw.decode('UTF-8')
  for c in inp:
    try:
      c.decode('ASCII')
      out = out + c.decode('UTF-8')
    except UnicodeError:
      if c in normalStyle:
        out = out + c
      else:
        out = out + u'\xa2$'+c+u'$\xa2'
  out = out +'\n'
print out[:-1]
