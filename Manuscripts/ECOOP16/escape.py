#!/usr/bin/python
import sys
while True:
    escape = u'\xa2'
    try: 
        raw = raw_input()
    except EOFError:
        break
    if not raw: break
    inp = raw.decode('UTF-8')
    for c in inp:
      try:
        c.decode('ASCII')
        sys.stdout.write("%c" %c)
      except UnicodeError:
        sys.stdout.write('%c' %escape)
        sys.stdout.write('$')
        sys.stdout.write('%c' %c) 
        sys.stdout.write('$')
        sys.stdout.write('%c' %escape)
    sys.stdout.write("\n")
