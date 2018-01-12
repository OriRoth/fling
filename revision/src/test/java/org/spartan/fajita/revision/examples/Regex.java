package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.api.Fajita.attribute;
import static org.spartan.fajita.revision.examples.Regex.NT.Expression;
import static org.spartan.fajita.revision.examples.Regex.NT.RE;
import static org.spartan.fajita.revision.examples.Regex.NT.Tail;
import static org.spartan.fajita.revision.examples.Regex.Term.and;
import static org.spartan.fajita.revision.examples.Regex.Term.anyChar;
import static org.spartan.fajita.revision.examples.Regex.Term.anyDigit;
import static org.spartan.fajita.revision.examples.Regex.Term.either;
import static org.spartan.fajita.revision.examples.Regex.Term.exactly;
import static org.spartan.fajita.revision.examples.Regex.Term.noneOrMore;
import static org.spartan.fajita.revision.examples.Regex.Term.oneOrMore;
import static org.spartan.fajita.revision.examples.Regex.Term.option;
import static org.spartan.fajita.revision.examples.Regex.Term.or;
import static org.spartan.fajita.revision.examples.Regex.Term.re;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class Regex extends Grammar {
  public static enum Term implements Terminal {
    re, exactly, and, or, option, noneOrMore, oneOrMore, either, anyChar, anyDigit
  }

  public static enum NT implements NonTerminal {
    Expression, RE, Tail
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(getClass(), Term.class, NT.class, "Regex", Main.packagePath, Main.projectPath) //
        .start(Expression) //
        .derive(Expression).to(re, RE) //
        .derive(RE).to(attribute(exactly, String.class), Tail) //
        /**/.or(attribute(option, RE), Tail) //
        /**/.or(attribute(noneOrMore, RE), Tail) //
        /**/.or(attribute(oneOrMore, RE), Tail) //
        /**/.or(attribute(either, RE, RE), Tail) //
        /**/.or(anyChar, Tail) //
        /**/.or(anyDigit, Tail) //
        .derive(Tail).to(and, RE).or(or, RE).orNone();
  }
  public static void main(String[] args) throws IOException {
    new Regex().generateGrammarFiles();
  }
}
