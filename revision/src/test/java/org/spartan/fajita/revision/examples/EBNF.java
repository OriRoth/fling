package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.api.Fajita.attribute;
import static org.spartan.fajita.revision.examples.EBNF.NT.*;
import static org.spartan.fajita.revision.examples.EBNF.Term.*;
import static org.spartan.fajita.revision.junk.EBNF.derive;
import static org.spartan.fajita.revision.junk.ClauseTail.*;
import static org.spartan.fajita.revision.junk.Literal.*;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class EBNF extends Grammar {
  public static enum Term implements Terminal {
    derive, to, and, or, orNone, option, noneOrMore, oneOrMore, t, s
  }

  public static enum NT implements NonTerminal {
    EBNF, Rule, ClauseTail, Literal
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(getClass(), Term.class, NT.class, "EBNF", Main.packagePath, Main.projectPath) //
        .start(EBNF) //
        .derive(EBNF).to(Fajita.oneOrMore(Rule)) //
        .derive(Rule).to(attribute(derive, NonTerminal.class), attribute(to, Literal), Fajita.option(ClauseTail)) //
        .derive(ClauseTail).to(Fajita.oneOrMore(Fajita.either(and, or), Literal)) //
        .derive(Literal).to(attribute(t, Terminal.class), ClauseTail) //
        /**/.or(attribute(s, NonTerminal.class), Fajita.option(ClauseTail)) //
        /**/.or(attribute(oneOrMore, Literal), Fajita.option(ClauseTail)) //
        /**/.or(attribute(noneOrMore, Literal), Fajita.option(ClauseTail)) //
        /**/.or(attribute(option, Literal), Fajita.option(ClauseTail)) //
    ;
  }
  public static void main(String[] args) throws IOException {
    new EBNF().generateGrammarFiles();
  }
  public static void testing() {
    // derive(EBNF).to(oneOrMore(s(Rule)));
  }
}
