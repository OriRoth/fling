LISTINGS=
LISTINGS+=abc.listing 
LISTINGS+=binary-function-example.listing 
LISTINGS+=binary-function.listing 
LISTINGS+=compiler.listing
LISTINGS+=fluflu.example.listing
LISTINGS+=gamma-example.listing 
LISTINGS+=gamma.listing 
LISTINGS+=id.listing
LISTINGS+=jump-stack-E.listing 
LISTINGS+=jump-stack-currency.listing 
LISTINGS+=jump-stack-example.listing
LISTINGS+=jump-stack-push.listing 
LISTINGS+=jump-stack.listing 
LISTINGS+=mammal.listing 
LISTINGS+=peep.listing 
LISTINGS+=prefix-proof.cases.listing 
LISTINGS+=prefix-proof.configuration.listing 
LISTINGS+=prefix-proof.full.listing 
LISTINGS+=prefix-proof.many.listing 
LISTINGS+=proof.cases.listing 
LISTINGS+=proof.configuration.listing 
LISTINGS+=proof.full.listing 
LISTINGS+=proof.headers.listing 
LISTINGS+=proof.many.listing 
LISTINGS+=spda.listing 
LISTINGS+=sql.bnf.listing
LISTINGS+=sql.enums.listing
LISTINGS+=sql.usage.listing
LISTINGS+=stack-use-cases.listing
LISTINGS+=stack.listing
LISTINGS+=toilette.illegal.listing
LISTINGS+=toilette.legal.listing
LISTINGS+=toilette.types.listing


ESCAPE = ( export PYTHONIOENCODING=UTF-8 ; ../Programs/escape.py)

latex:

include ../Programs/Makefile

SOURCES=$(wildcard *.sty *.tex *.fragment) $(LISTINGS) Makefile

.PHONY:
listings: $(LISTINGS)


ECOOP_CODE_PATH = ../../src/main/java/automaton/ecoop
OOPSLA_CODE_PATH = ../../src/main/java/automaton/oopsla

DOMAIN = $(ECOOP_CODE_PATH)/Domain.java 
DOMAIN_DEMO = $(ECOOP_CODE_PATH)/DomainDemo.java  
JDPDA = $(ECOOP_CODE_PATH)/jDPDA.java
PREFIX_JDPDA = $(ECOOP_CODE_PATH)/prefix_jDPDA.java
TOILETTE = $(OOPSLA_CODE_PATH)/ToiletteSeat.java
FLUFLU = $(OOPSLA_CODE_PATH)/FlufluTest.java
SQL = $(OOPSLA_CODE_PATH)/SQLFrag.java
JS_DEMO = $(ECOOP_CODE_PATH)/JumpStackDemo.java

prefix-proof.full.listing: $(PREFIX_JDPDA)
	awk '/begin{full}/{f=1;c+=1;next}/end{full}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | $(ESCAPE) > $@ 

prefix-proof.configuration.listing: $(PREFIX_JDPDA)
	awk '/begin{configuration}/{f=1;c+=1;next}/end{configuration}/{f=0;next}f{print}' $< | sed 's/^  //' | $(ESCAPE) > $@

prefix-proof.many.listing: $(PREFIX_JDPDA)
	awk '/begin{many}/{f=1;c+=1;next}/end{many}/{f=0;next}f{print}' $< | sed 's/^  //' | $(ESCAPE) > $@ 

prefix-proof.cases.listing: $(PREFIX_JDPDA)
	awk '/begin{cases}/{f=1;c+=1;next}/end{cases}/{f=0;next}f&& $$0!~/begin{/ && $$0!~/end{/{print}' $< | sed 's/^  //' | $(ESCAPE) > $@ 
  
proof.many.listing: $(JDPDA)
	awk '/begin{many}/{f=1;c+=1;next}/end{many}/{f=0;next}f{print}' $< | sed 's/^  //' | $(ESCAPE) > $@ 

proof.headers.listing: $(JDPDA)
	awk '/begin{headers}/{f=1;c+=1;next}/end{headers}/{f=0;next}f{print}' $< | sed 's/^  //' | $(ESCAPE) > $@ 
  
proof.configuration.listing: $(JDPDA)
	awk '/begin{configuration}/{f=1;c+=1;next}/end{configuration}/{f=0;next}f{print}' $< | sed 's/^  //' | $(ESCAPE) > $@ 
  
proof.full.listing: $(JDPDA)
	awk '/begin{full}/{f=1;c+=1;next}/end{full}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | $(ESCAPE) > $@ 

proof.cases.listing: $(JDPDA)
	awk '/begin{cases}/{f=1;c+=1;next}/end{cases}/{f=0;next}f&& $$0!~/begin{/ && $$0!~/end{/{print}' $< | sed 's/^  //' | $(ESCAPE) > $@ 

jump-stack-example.listing: $(JS_DEMO)
	awk '/^.*jump_stack.*$$/,/^  }.*$$/' $< | sed 's/^  //' | $(ESCAPE) > $@ 

compiler.listing: ../Figures/Cons.java
	awk '/^.*interface.*$$/,/^  }.*$$/' $< | sed 's/^  //' | $(ESCAPE) > $@ 

jump-stack.listing: $(DOMAIN)
	awk '/^.*interface *JS.*$$/,/^  }.*$$/' $< | sed 's/^  //' \
		| grep -v Override  \
    | awk 'BEGIN{RS="{[\n\t ]+}\n"; ORS="{ … }\n"}{print}' \
		| head -n -1 \
	  | $(ESCAPE) > $@ 

jump-stack-currency.listing: $(DOMAIN)
	awk '/^.*interface *JS.*$$/,/^  }.*$$/' $<  \
	| awk '/^.*interface *¤ .*$$/,/^    }.*$$/' $< | sed 's/^    //' \
	| $(ESCAPE) > $@ 

jump-stack-E.listing: $(DOMAIN)
	awk '/^.*interface *JS.*$$/,/^  }.*$$/' $<  \
	| awk '/^.*interface *E .*$$/,/^    }.*$$/' | sed 's/^    //' \
	| $(ESCAPE) > $@ 

jump-stack-push.listing: $(DOMAIN)
	awk '/^.*private *interface *P.*$$/,/^  }.*$$/' $< | sed 's/^  //' \
	| sed -e 's/@Override //' \
	| $(ESCAPE) > $@ 
	

stack.listing: $(DOMAIN)
	awk '/^.*Stack *<Rest.*>> *{ *$$/,/^  }.*$$/' $< | sed 's/^  //' | $(ESCAPE) > $@ 

stack-use-cases.listing: $(DOMAIN_DEMO)
	awk '/^.* use.*of_stack().*$$/,/^  }.*$$/' $< | sed 's/^  //' |  $(ESCAPE) > $@ 

gamma.listing: $(DOMAIN)
	awk '/^.*class *Γʹ *{ *$$/,/^  }.*$$/' $< | sed 's/^  //' | $(ESCAPE) > $@ 

gamma-example.listing: $(DOMAIN_DEMO)
	awk '/^.*function_g.*$$/,/^  }.*$$/' $< | sed 's/^  //' | $(ESCAPE) > $@ 

binary-function.listing: $(DOMAIN)
	awk '/^.*static abstract class f {.*$$/,/^  }.*$$/' $< | sed 's/^  //' | $(ESCAPE) > $@ 
	awk '/^.*static abstract class R {.*$$/,/^  }.*$$/' $< | sed 's/^  //' | $(ESCAPE) >> $@ 

mammal.listing: $(DOMAIN_DEMO)
	awk '/^.*Mammals.*$$/,/^.*extends Heap.*$$/' $< | sed 's/^  //' | $(ESCAPE) > $@ 

binary-function-example.listing:  $(DOMAIN_DEMO)
	awk '/^.*function_f.*$$/,/^  }.*$$/' $< | sed 's/^  //' | $(ESCAPE) > $@ 

spda.listing: $(DOMAIN)
		awk '/^.*public static abstract class Q<S\s.*$$/,/^  }.*$$/' $< | sed 's/^  //' | $(ESCAPE) > $@ 

id.listing: $(DOMAIN)
		awk '/^.*interface ID.*$$/,/^.*class C/' $< | sed 's/^  //' | $(ESCAPE) > $@ 

abc.listing: $(DOMAIN_DEMO)
		awk '/^.*interface ID.*$$/,/^.*class C/' $< | sed 's/^  //' | $(ESCAPE) > $@ 

peep.listing:  $(DOMAIN_DEMO)
		awk '/^.*Peep *<.*$$/,/^  }/' $< | sed 's/^  //' | $(ESCAPE) > $@


toilette.types.listing: $(TOILETTE)
	awk '/begin{types}/{f=1;c+=1;next}/end{types}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | $(ESCAPE) > $@ 

toilette.legal.listing: $(TOILETTE)
	awk '/begin{legal}/{f=1;c+=1;next}/end{legal}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | $(ESCAPE) > $@ 

toilette.illegal.listing: $(TOILETTE)
	awk '/begin{illegal}/{f=1;c+=1;next}/end{illegal}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | $(ESCAPE) > $@ 

fluflu.example.listing: $(FLUFLU)
	awk '/begin{full}/{f=1;c+=1;next}/end{full}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | $(ESCAPE) > $@ 

sql.bnf.listing: $(SQL)
	awk '/begin{BNF}/{f=1;c+=1;next}/end{BNF}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | $(ESCAPE) > $@ 

sql.enums.listing: $(SQL)
	awk '/begin{enums}/{f=1;c+=1;next}/end{enums}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | $(ESCAPE) > $@ 

sql.usage.listing: $(SQL)
	awk '/begin{usage}/{f=1;c+=1;next}/end{usage}/{f=0;next}f&& ($$0!~/begin{/) && ($$0!~/end{/){print}' $< | $(ESCAPE) > $@ 
