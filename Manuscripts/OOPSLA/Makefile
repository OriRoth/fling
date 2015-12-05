MAIN=00
LOCALTEXMF= ../../../yogitex//
LATEX=lualatex -file-line-error

export TEXINPUTS=:$(LOCALTEXMF)
export BIBINPUTS=:$(LOCALTEXMF)

LISTINGS=
LISTINGS+=abc.listing 
LISTINGS+=binary-function-example.listing 
LISTINGS+=binary-function.listing 
LISTINGS+=peep.listing 
LISTINGS+=gamma-example.listing 
LISTINGS+=gamma.listing 
LISTINGS+=id.listing
LISTINGS+=mammal.listing 
LISTINGS+=spda.listing 
LISTINGS+=stack.listing
LISTINGS+=stack-use-cases.listing

# Target to be used for compiles when references did not change
latex: $(wildcard *.sty *.tex) $(LISTINGS) 
	$(LATEX) -synctex=1 ./$(MAIN).tex

$(MAIN).pdf: latex $(wildcard *.sty *.tex) 	
	$(LATEX) -synctex=1 ./$(MAIN).tex

DOMAIN=../../src/main/java/automaton/Domain.java 
DOMAIN_DEMO=../../src/main/java/automaton/DomainDemo.java  

stack.listing: $(DOMAIN)
	awk '/^.*Stack *<Rest.*>> *{ *$$/,/^  }.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

stack-use-cases.listing: $(DOMAIN_DEMO)
	awk '/^.* use.*stack().*$$/,/^  }.*$$/' $< | sed 's/^  //' |  ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

gamma.listing: $(DOMAIN)
	awk '/^.*class *Γʹ *{ *$$/,/^  }.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

gamma-example.listing: $(DOMAIN_DEMO)
	awk '/^.*function_g.*$$/,/^  }.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

binary-function.listing: $(DOMAIN)
	awk '/^.*static abstract class f {.*$$/,/^  }.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 
	awk '/^.*static abstract class R {.*$$/,/^  }.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) >> $@ 

mammal.listing: $(DOMAIN_DEMO)
	awk '/^.*Mammals.*$$/,/^.*extends Heap.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

binary-function-example.listing:  $(DOMAIN_DEM)
	awk '/^.*function_f.*$$/,/^  }.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

spda.listing: $(DOMAIN)
		awk '/^.*public static abstract class Q<S\s.*$$/,/^  }.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

id.listing: $(DOMAIN)
		awk '/^.*interface ID.*$$/,/^.*class C/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

abc.listing: $(DOMAIN_DEMO)
		awk '/^.*interface ID.*$$/,/^.*class C/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

peep.listing:  $(DOMAIN_DEMO)
		awk '/^.*Peep *<.*$$/,/^ *peep.P.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@


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

JUNKFILES=$(wildcard [dD]elme* DELME* *~ *.aux *.listing *.fls *.bbl *.blg *.bcf *.fdb_latexmk *.synctex.gz *.run.xml *blx.bib *.log *.dvi *.out *.o $(MAIN).pdf *.listing .*swp .*swo *.bak *.backup *.vtc)

clean: $(JUNKFILES) 
	- [ -z '$^' ] || mv -f $^ /tmp

