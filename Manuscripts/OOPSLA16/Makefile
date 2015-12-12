MAIN=00
LOCALTEXMF= ../../../yogitex//
LATEX=lualatex -file-line-error

export TEXINPUTS=:$(LOCALTEXMF)
export BIBINPUTS=:$(LOCALTEXMF)

LISTINGS=
LISTINGS+=abc.listing 
LISTINGS+=binary-function-example.listing 
LISTINGS+=binary-function.listing 
LISTINGS+=compiler.listing
LISTINGS+=gamma-example.listing 
LISTINGS+=gamma.listing 
LISTINGS+=id.listing
LISTINGS+=jump-stack-currency.listing 
LISTINGS+=jump-stack-E.listing 
LISTINGS+=jump-stack-example.listing
LISTINGS+=jump-stack.listing 
LISTINGS+=jump-stack-push.listing 
LISTINGS+=mammal.listing 
LISTINGS+=peep.listing 
LISTINGS+=proof.cases.listing 
LISTINGS+=proof.many.listing 
LISTINGS+=proof.configuration.listing 
LISTINGS+=proof.full.listing 
LISTINGS+=proof.headers.listing 
LISTINGS+=prefix-proof.configuration.listing 
LISTINGS+=prefix-proof.many.listing 
LISTINGS+=prefix-proof.cases.listing 
LISTINGS+=spda.listing 
LISTINGS+=stack.listing
LISTINGS+=stack-use-cases.listing

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

DOMAIN = $(CODE_PATH)/Domain.java 
DOMAIN_DEMO = $(CODE_PATH)/DomainDemo.java  
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

jump-stack-example.listing: $(CODE_PATH)/JumpStackDemo.java
	awk '/^.*jump_stack.*$$/,/^  }.*$$/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@ 

compiler.listing: ../Figures/Cons.java
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
		awk '/^.*Peep *<.*$$/,/^  }/' $< | sed 's/^  //' | ( export PYTHONIOENCODING=UTF-8 ; ./escape.py) > $@

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

clean: $(JUNKFILES) 
	- [ -z '$^' ]        || mv -f $^ /tmp
	- [ -z '$(wildcard $(TARGET))' ] || mv -f $(TARGET) /tmp
