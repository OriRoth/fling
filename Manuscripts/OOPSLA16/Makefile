MAIN=00
LATEX=lualatex -file-line-error


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

.PHONY: latex view bibtex graphs all bibtex clean 

SOURCES=$(wildcard *.sty *.tex *.fragment) $(LISTINGS) Makefile

TARGET=$(MAIN).pdf 

# Target to be used for compiles when references did not change
$(TARGET) again latex: $(SOURCES)
	$(LATEX) -synctex=1 ./$(MAIN).tex

silent: $(wildcard *.sty *.tex *.fragment) $(LISTINGS) Makefile
	$(LATEX) -interaction=batchmode -synctex=1 -halt-on-error -file-line-error ./$(MAIN).tex

again: silent

$(MAIN).blg bibtex:	
	$(LATEX) -interaction=batchmode -draftmode $(MAIN).tex
	bibtex $(MAIN)
	$(LATEX) -interaction=batchmode -draftmode ./$(MAIN).tex

view: $(MAIN).pdf 
	okular --unique $(MAIN).pdf &> /dev/null &

graphs: $(wildcard *.sty *.tex) $(LISTINGS) 
	$(LATEX) -interaction=batchmode -draftmode -shell-escape ./$(MAIN).tex

all:	graphs bibtex latex 

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


warnings:	
	 $(LATEX) -file-line-error -synctex=1 -recorder 00.tex 2>&1 | grep --color=auto -E "Warning|Missing" 

lacheck:
	lacheck *.tex

spelling:
	for f in *.tex ; do if [ $$f != 00.tex ]; then aspell check $$f;fi; done

dup:
	for f in *.tex ; do if [ $$f != 00.tex ]; then dup $$f;fi; done

JUNKFILES=
JUNKFILES+=$(wildcard *~ *.aux )
JUNKFILES+=$(wildcard *.backup )
JUNKFILES+=$(wildcard *.bak )
JUNKFILES+=$(wildcard *.bbl )
JUNKFILES+=$(wildcard *.bcf )
JUNKFILES+=$(wildcard *.blg )
JUNKFILES+=$(wildcard *blx.bib )
JUNKFILES+=$(wildcard [dD]elme* )
JUNKFILES+=$(wildcard DELME* )
JUNKFILES+=$(wildcard *.dvi )
JUNKFILES+=$(wildcard *.fdb_)
JUNKFILES+=$(wildcard *.fls )
JUNKFILES+=$(wildcard *gnuplottex-fig[0-9]*.*)
JUNKFILES+=$(wildcard latexmk )
JUNKFILES+=$(wildcard *.listing )
JUNKFILES+=$(wildcard *.log )
JUNKFILES+=$(wildcard *.o )
JUNKFILES+=$(wildcard *.out )
JUNKFILES+=$(wildcard *.run.xml )
JUNKFILES+=$(wildcard .*swo )
JUNKFILES+=$(wildcard .*swp )
JUNKFILES+=$(wildcard *.synctex.gz )
JUNKFILES+=$(wildcard *.vtc)
JUNKFILES+=$(wildcard *.detexed )
JUNKFILES+=$(wildcard *.class )

clean: $(JUNKFILES) 
	- [ -z '$^' ]        || mv -f $^ /tmp
	- [ -z '$(wildcard $(TARGET))' ] || mv -f $(TARGET) /tmp
