from sys import argv
from sys import stdout


l = int(argv[1])
n = 2 ** l
p = int(argv[2])


def declare():
  for i in range(n):
    stdout.write('A' + str(i) + ', ')
  print ''


def derive():
  for i in range(n - 1):
    print '.derive(A' + str(i) + ').to(a' + ((', option(A' + str(i + 1) + '), a') * p) + ') //'
  print '.derive(A' + str(n - 1) + ').to(a) //'


declare()
derive()

