LISTINGS=
LISTINGS+=proof.cases.listing 
LISTINGS+=proof.many.listing 
LISTINGS+=proof.configuration.listing 
LISTINGS+=proof.full.listing 
LISTINGS+=proof.headers.listing 
LISTINGS+=prefix-proof.configuration.listing 
LISTINGS+=prefix-proof.many.listing 
LISTINGS+=prefix-proof.cases.listing 
LISTINGS+=prefix-proof.full.listing 

latex:

include ../Programs/Makefile

.PHONY: latex view bibtex graphs all bibtex clean 

SOURCES=$(wildcard *.sty *.tex *.fragment) $(LISTINGS) Makefile

TARGET=$(MAIN).pdf 

CODE_PATH = ../../src/main/java/automaton

JDPDA = $(CODE_PATH)/jDPDA.java
PREFIX_JDPDA = $(CODE_PATH)/prefix_jDPDA.java

prefix-proof.full.listing: $(PREFIX_JDPDA)
	awk '/begin{full}/{f=1;c+=1;next}/end{full}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

prefix-proof.configuration.listing: $(PREFIX_JDPDA)
	awk '/begin{configuration}/{f=1;c+=1;next}/end{configuration}/{f=0;next}f{print}' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

prefix-proof.many.listing: $(PREFIX_JDPDA)
	awk '/begin{many}/{f=1;c+=1;next}/end{many}/{f=0;next}f{print}' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

prefix-proof.cases.listing: $(PREFIX_JDPDA)
	awk '/begin{cases}/{f=1;c+=1;next}/end{cases}/{f=0;next}f&& $$0!~/begin{/ && $$0!~/end{/{print}' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 
  
proof.many.listing: $(JDPDA)
	awk '/begin{many}/{f=1;c+=1;next}/end{many}/{f=0;next}f{print}' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

proof.headers.listing: $(JDPDA)
	awk '/begin{headers}/{f=1;c+=1;next}/end{headers}/{f=0;next}f{print}' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 
  
proof.configuration.listing: $(JDPDA)
	awk '/begin{configuration}/{f=1;c+=1;next}/end{configuration}/{f=0;next}f{print}' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 
  
proof.full.listing: $(JDPDA)
	awk '/begin{full}/{f=1;c+=1;next}/end{full}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

proof.cases.listing: $(JDPDA)
	awk '/begin{cases}/{f=1;c+=1;next}/end{cases}/{f=0;next}f&& $$0!~/begin{/ && $$0!~/end{/{print}' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 
