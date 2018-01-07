package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.examples.EBNF.NT.AndOr;
import static org.spartan.fajita.revision.examples.EBNF.NT.ClauseTail;
import static org.spartan.fajita.revision.examples.EBNF.NT.EBNF;
import static org.spartan.fajita.revision.examples.EBNF.NT.Literal;
import static org.spartan.fajita.revision.examples.EBNF.NT.Rule;
import static org.spartan.fajita.revision.examples.EBNF.NT.To;
import static org.spartan.fajita.revision.examples.EBNF.Term.and;
import static org.spartan.fajita.revision.examples.EBNF.Term.attribute;
import static org.spartan.fajita.revision.examples.EBNF.Term.derive;
import static org.spartan.fajita.revision.examples.EBNF.Term.either;
import static org.spartan.fajita.revision.examples.EBNF.Term.noneOrMore;
import static org.spartan.fajita.revision.examples.EBNF.Term.oneOrMore;
import static org.spartan.fajita.revision.examples.EBNF.Term.option;
import static org.spartan.fajita.revision.examples.EBNF.Term.or;
import static org.spartan.fajita.revision.examples.EBNF.Term.to;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.types.VarArgs;

public class EBNF extends Grammar {
  public static enum Term implements Terminal {
    derive, to, and, or, orNone, option, noneOrMore, oneOrMore, either, attribute
  }

  public static enum NT implements NonTerminal {
    EBNF, Rule, ClauseTail, Literal, To, AndOr
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(getClass(), Term.class, NT.class, "EBNF", Main.packagePath, Main.projectPath) //
        .start(EBNF) //
        .derive(EBNF).to(Fajita.oneOrMore(Rule)) //
        .derive(Rule).to(Fajita.attribute(derive, String.class), To, Fajita.option(ClauseTail)) //
        .derive(To).to(Fajita.attribute(to, Literal)) //
        /**/.or(Fajita.attribute(to, String.class)) //
        .derive(ClauseTail).to(Fajita.noneOrMore(AndOr)) //
        .derive(AndOr).to(Fajita.either(Fajita.attribute(and, Literal), Fajita.attribute(or, Literal))) //
        /**/.or(Fajita.either(Fajita.attribute(and, String.class), Fajita.attribute(or, String.class))) //
        .derive(Literal).to(Fajita.attribute(oneOrMore, Literal), Fajita.option(ClauseTail)) //
        /**/.or(Fajita.attribute(oneOrMore, String.class), Fajita.option(ClauseTail)) //
        /**/.or(Fajita.attribute(noneOrMore, Literal), Fajita.option(ClauseTail)) //
        /**/.or(Fajita.attribute(noneOrMore, String.class), Fajita.option(ClauseTail)) //
        /**/.or(Fajita.attribute(option, Literal), Fajita.option(ClauseTail)) //
        /**/.or(Fajita.attribute(option, String.class), Fajita.option(ClauseTail)) //
        /**/.or(Fajita.attribute(either, Literal, Literal), Fajita.option(ClauseTail)) //
        /**/.or(Fajita.attribute(either, Literal, String.class), Fajita.option(ClauseTail)) //
        /**/.or(Fajita.attribute(either, String.class, Literal), Fajita.option(ClauseTail)) //
        /**/.or(Fajita.attribute(either, String.class, String.class), Fajita.option(ClauseTail)) //
        /**/.or(Fajita.attribute(attribute, String.class, new VarArgs(Object.class)), Fajita.option(ClauseTail)) //
    ;
  }
  public static void main(String[] args) throws IOException {
    new EBNF().generateGrammarFiles();
  }
}
