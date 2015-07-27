MAIN=00
LOCALTEXMF= ../../../yogitex/
LATEX=xelatex

export TEXINPUTS=:$(LOCALTEXMF)
export BIBINPUTS=:$(LOCALTEXMF)

# Target to be used for compiles when references did not change
final:	
	$(LATEX) -synctex=1 -file-line-error ./$(MAIN).tex

full:	graphs bibtex final

view: 
	okular $(MAIN).pdf &> /dev/null &

graphs: 

bibtex:	graphs
	$(LATEX) -interaction=nonstopmode -draftmode $(MAIN).tex
	bibtex $(MAIN)
	$(LATEX) -interaction=nonstopmode -draftmode ./$(MAIN).tex

all:	graphs bibtex latex

.PHONY: clean 

JUNKFILES=$(wildcard [dD]elme* DELME* *~ *.aux *.listing *.fls *.bbl *.blg *.bcf *.fdb_latexmk *.synctex.gz *.run.xml *blx.bib *.log *.dvi *.out *.o $(MAIN).pdf)  

clean: 
	mv -f $(JUNKFILES) /tmp
