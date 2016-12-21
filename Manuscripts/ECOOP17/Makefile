LISTINGS=
LISTINGS+=toilette.types.listing
LISTINGS+=toilette.legal.listing
LISTINGS+=toilette.illegal.listing
LISTINGS+=fluflu.example.listing
LISTINGS+=sql.bnf.listing
LISTINGS+=sql.enums.listing
LISTINGS+=sql.legal.listing
LISTINGS+=sql.illegal.listing

ESCAPE = ../Programs/escape.py

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
	awk '/begin{types}/{f=1;c+=1;next}/end{types}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | ( export PYTHONIOENCODING=UTF-8 ; $(ESCAPE)) > $@ 

toilette.legal.listing: $(TOILETTE)
	awk '/begin{legal}/{f=1;c+=1;next}/end{legal}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | ( export PYTHONIOENCODING=UTF-8 ; $(ESCAPE)) > $@ 

toilette.illegal.listing: $(TOILETTE)
	awk '/begin{illegal}/{f=1;c+=1;next}/end{illegal}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | ( export PYTHONIOENCODING=UTF-8 ; $(ESCAPE)) > $@ 

fluflu.example.listing: $(FLUFLU)
	awk '/begin{full}/{f=1;c+=1;next}/end{full}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | ( export PYTHONIOENCODING=UTF-8 ; $(ESCAPE)) > $@ 

sql.bnf.listing: $(SQL)
	awk '/begin{BNF}/{f=1;c+=1;next}/end{BNF}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | ( export PYTHONIOENCODING=UTF-8 ; $(ESCAPE)) > $@ 

sql.enums.listing: $(SQL)
	awk '/begin{enums}/{f=1;c+=1;next}/end{enums}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | ( export PYTHONIOENCODING=UTF-8 ; $(ESCAPE)) > $@ 

sql.legal.listing: $(SQL)
	awk '/begin{legal}/{f=1;c+=1;next}/end{legal}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | ( export PYTHONIOENCODING=UTF-8 ; $(ESCAPE)) > $@ 

sql.illegal.listing: $(SQL)
	awk '/begin{illegal}/{f=1;c+=1;next}/end{illegal}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | ( export PYTHONIOENCODING=UTF-8 ; $(ESCAPE)) > $@ 
