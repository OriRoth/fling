import sys
out = u''
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
      out = out + u'\xa2$'+c+u'$\xa2'
  out = out +'\n'
print out[:-1]
