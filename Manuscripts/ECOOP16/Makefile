MAIN=00
LOCALTEXMF= ../../../yogitex//
LATEX=lualatex -file-line-error

export TEXINPUTS=:$(LOCALTEXMF)
export BIBINPUTS=:$(LOCALTEXMF)

# Target to be used for compiles when references did not change
latex: $(wildcard *.sty *.tex) stack.listing gamma.listing gamma-example.listing	
	$(LATEX) -synctex=1 ./$(MAIN).tex

$(MAIN).pdf: latex $(wildcard *.sty *.tex) 	
	$(LATEX) -synctex=1 ./$(MAIN).tex

stack.listing: ../../src/main/java/automaton/Domain.java  
	awk '/^.*Stack *<Tail.*>> *{ *$$/,/^  }.*$$/' $< | sed 's/^  //' | sed 's/return null;/.../g' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

gamma.listing: ../../src/main/java/automaton/Domain.java  
	awk '/^.*class *Γʹ *{ *$$/,/^  }.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

gamma-example.listing: ../../src/main/java/automaton/Domain.java  
	awk '/^.*demo_of_g.*$$/,/^  }.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 


warnings:	
	- $(LATEX) -file-line-error -synctex=1 -recorder 00.tex 2>&1 | grep --color=auto -E "Warning|Missing" 
	- lacheck *.tex
	- for f in *.tex ; do if [ $$f != 00.tex ]; then aspell check $$f;fi; done


full:	graphs bibtex final

view: $(MAIN).pdf 
	okular $(MAIN).pdf &> /dev/null &

graphs: 

$(MAIN).blg bibtex:	
	$(LATEX) -interaction=nonstopmode -draftmode $(MAIN).tex
	bibtex $(MAIN)
	$(LATEX) -interaction=nonstopmode -draftmode ./$(MAIN).tex

all:	graphs bibtex $(MAIN).pdf

.PHONY: clean graphs bibtex latex

JUNKFILES=$(wildcard [dD]elme* DELME* *~ *.aux *.listing *.fls *.bbl *.blg *.bcf *.fdb_latexmk *.synctex.gz *.run.xml *blx.bib *.log *.dvi *.out *.o $(MAIN).pdf *.listing)

clean: $(JUNKFILES) 
	- [ -z '$^' ] || mv -f $^ /tmp

