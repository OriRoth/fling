LISTINGS=
LISTINGS+=toilette.types.listing
LISTINGS+=toilette.legal.listing
LISTINGS+=toilette.illegal.listing
LISTINGS+=fluflu.example.listing
LISTINGS+=sql.bnf.listing
LISTINGS+=sql.terminals.listing
LISTINGS+=sql.nonterminals.listing

latex:

include ../Programs/Makefile

SOURCES=$(wildcard *.sty *.tex *.fragment) $(LISTINGS) Makefile

.PHONY:
listings: $(LISTINGS)


CODE_PATH = ../../src/main/java/automaton/oopsla

TOILETTE = $(CODE_PATH)/ToiletteSeat.java
FLUFLU = $(CODE_PATH)/FlufluTest.java
SQL = $(CODE_PATH)/SQLFrag.java

toilette.types.listing: $(TOILETTE)
	awk '/begin{types}/{f=1;c+=1;next}/end{types}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

toilette.legal.listing: $(TOILETTE)
	awk '/begin{legal}/{f=1;c+=1;next}/end{legal}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

toilette.illegal.listing: $(TOILETTE)
	awk '/begin{illegal}/{f=1;c+=1;next}/end{illegal}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

fluflu.example.listing: $(FLUFLU)
	awk '/begin{full}/{f=1;c+=1;next}/end{full}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

sql.bnf.listing: $(SQL)
	awk '/begin{BNF}/{f=1;c+=1;next}/end{BNF}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

sql.terminals.listing: $(SQL)
	awk '/begin{terminals}/{f=1;c+=1;next}/end{terminals}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

sql.nonterminals.listing: $(SQL)
	awk '/begin{nonterminals}/{f=1;c+=1;next}/end{nonterminals}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

