import sys
inp = raw_input().decode('UTF-8')
for c in inp:
  try:
    c.decode('ASCII')
    sys.stdout.write("%c" %c)
  except UnicodeError:
    sys.stdout.write(u'\xa2$%c$\xa2' %c)
