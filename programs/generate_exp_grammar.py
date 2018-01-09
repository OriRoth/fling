from sys import argv
from sys import stdout


l = int(argv[1])
n = 2 ** l


def declare():
  for i in range(2 * n - 1):
    stdout.write('a' + str(i) + ', ')
  print ''
  for i in range(2 * n - 1):
    stdout.write('A' + str(i) + ', ')
  print ''


def derive(i, d):
  if d == l:
    print '.derive(A' + str(i) +  ').to(option(a' + str(i) + ')) //'
    return
  i1, i2 = 2 * i + 1, 2 * i + 2
  print '.derive(A' + str(i) + ').to(option(a' + str(i) + '), option(A' + str(i1) + '), option(' + 'A' + str(i2) + ')) //'
  derive(i1, d + 1)
  derive(i2, d + 1)


def use():
  stdout.write('a()')
  for i in range(2 * n - 1):
    stdout.write('.a' + str(i) + '()')
  print ';'


declare()
derive(0, 0)
use()

