MAIN=00
LOCALTEXMF= ../../../yogitex//
LATEX=lualatex -file-line-error

export TEXINPUTS=:$(LOCALTEXMF)
export BIBINPUTS=:$(LOCALTEXMF)

LISTINGS=
LISTINGS+=abc.listing 
LISTINGS+=binary-function-example.listing 
LISTINGS+=binary-function.listing 
LISTINGS+=gamma-example.listing 
LISTINGS+=gamma.listing 
LISTINGS+=id.listing
LISTINGS+=jump-stack.listing 
LISTINGS+=jump-stack-currency.listing 
LISTINGS+=jump-stack-E.listing 
LISTINGS+=jump-stack-push.listing 
LISTINGS+=mammal.listing 
LISTINGS+=peep.listing 
LISTINGS+=spda.listing 
LISTINGS+=stack.listing
LISTINGS+=stack-use-cases.listing
LISTINGS+=jump-stack-example.listing
LISTINGS+=compiler.listing

.PHONY: latex view bibtex graphs all bibtex clean 

# Target to be used for compiles when references did not change
$(MAIN).pdf latex: $(wildcard *.sty *.tex) $(LISTINGS) Makefile
	$(LATEX) -synctex=1 ./$(MAIN).tex

$(MAIN).blg bibtex:	
	$(LATEX) -interaction=nonstopmode -draftmode $(MAIN).tex
	bibtex $(MAIN)
	$(LATEX) -interaction=nonstopmode -draftmode ./$(MAIN).tex

view: $(MAIN).pdf 
	okular --unique $(MAIN).pdf &> /dev/null &

graphs: $(wildcard *.sty *.tex) $(LISTINGS) 
	$(LATEX) -shell-escape ./$(MAIN).tex

all:	graphs bibtex latex 

DOMAIN = ../../src/main/java/automaton/Domain.java 
DOMAIN_DEMO = ../../src/main/java/automaton/DomainDemo.java  
COMPILER = ../Figures/KillCompiler.java
  
jump-stack-example.listing: $(DOMAIN_DEMO)
	awk '/^.*jump_stack.*$$/,/^  }.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

compiler.listing: $(COMPILER)
	awk '/^.*interface.*$$/,/^  }.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

jump-stack.listing: $(DOMAIN)
	awk '/^.*interface *JS.*$$/,/^  }.*$$/' $< | sed 's/^  //' \
		| grep -v Override  \
    | awk 'BEGIN{RS="{[\n\t ]+}\n"; ORS="{ … }\n"}{print}' \
		| head -n -1 \
	  | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

jump-stack-currency.listing: $(DOMAIN)
	awk '/^.*interface *JS.*$$/,/^  }.*$$/' $<  \
	| awk '/^.*interface *¤ .*$$/,/^    }.*$$/' $< | sed 's/^    //' \
	| ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

jump-stack-E.listing: $(DOMAIN)
	awk '/^.*interface *JS.*$$/,/^  }.*$$/' $<  \
	| awk '/^.*interface *E .*$$/,/^    }.*$$/' | sed 's/^    //' \
	| ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

jump-stack-push.listing: $(DOMAIN)
	awk '/^.*private *interface *P.*$$/,/^  }.*$$/' $< | sed 's/^  //' \
	| sed -e 's/@Override //' \
	| ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 
	

stack.listing: $(DOMAIN)
	awk '/^.*Stack *<Rest.*>> *{ *$$/,/^  }.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

stack-use-cases.listing: $(DOMAIN_DEMO)
	awk '/^.* use.*of_stack().*$$/,/^  }.*$$/' $< | sed 's/^  //' |  ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

gamma.listing: $(DOMAIN)
	awk '/^.*class *Γʹ *{ *$$/,/^  }.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

gamma-example.listing: $(DOMAIN_DEMO)
	awk '/^.*function_g.*$$/,/^  }.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

binary-function.listing: $(DOMAIN)
	awk '/^.*static abstract class f {.*$$/,/^  }.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 
	awk '/^.*static abstract class R {.*$$/,/^  }.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) >> $@ 

mammal.listing: $(DOMAIN_DEMO)
	awk '/^.*Mammals.*$$/,/^.*extends Heap.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

binary-function-example.listing:  $(DOMAIN_DEMO)
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
JUNKFILES+=$(wildcard $(MAIN).pdf )
JUNKFILES+=$(wildcard *.o )
JUNKFILES+=$(wildcard *.out )
JUNKFILES+=$(wildcard *.run.xml )
JUNKFILES+=$(wildcard .*swo )
JUNKFILES+=$(wildcard .*swp )
JUNKFILES+=$(wildcard *.synctex.gz )
JUNKFILES+=$(wildcard *.vtc)

clean: $(JUNKFILES) 
	- [ -z '$^' ] || mv -f $^ /tmp

