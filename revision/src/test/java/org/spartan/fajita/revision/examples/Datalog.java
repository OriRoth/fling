package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.api.Fajita.attribute;
import static org.spartan.fajita.revision.examples.Datalog.NT.BODY;
import static org.spartan.fajita.revision.examples.Datalog.NT.LITERAL;
import static org.spartan.fajita.revision.examples.Datalog.NT.RULE;
import static org.spartan.fajita.revision.examples.Datalog.NT.S;
import static org.spartan.fajita.revision.examples.Datalog.Term.body;
import static org.spartan.fajita.revision.examples.Datalog.Term.fact;
import static org.spartan.fajita.revision.examples.Datalog.Term.head;
import static org.spartan.fajita.revision.examples.Datalog.Term.literal;
import static org.spartan.fajita.revision.examples.Datalog.Term.name;
import static org.spartan.fajita.revision.examples.Datalog.Term.terms;
import static org.spartan.fajita.revision.junk.Datalog.fact;
import static org.spartan.fajita.revision.junk.LITERAL.name;
import static org.spartan.fajita.revision.api.Fajita.oneOrMore;
import static org.spartan.fajita.revision.junk.BODY1.literal;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.types.VarArgs;

// TODO Roth add OR
public class Datalog extends Grammar {
  public static enum Term implements Terminal {
    head, body, fact, literal, name, terms
  }

  public static enum NT implements NonTerminal {
    S, RULE, LITERAL, BODY
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(getClass(), Term.class, NT.class, "Datalog", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to(oneOrMore(RULE)) //
        .derive(RULE) //
        /**/.to(attribute(fact, LITERAL)) //
        /**/.or(attribute(head, LITERAL)).and(BODY) //
        .derive(BODY).to(attribute(body, oneOrMore(attribute(literal, LITERAL))))
        //
        .derive(LITERAL).to(attribute(name, String.class), attribute(terms, new VarArgs(String.class)));
  }
  /**
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    // System.out.println(bnf().go().toString(ASCII));
    // new Datalog().generateGrammarFiles();
    test();
  }
  static void test() {
    System.out.println(fact(name("parent").terms("john", "bob")) //
        .fact(name("parent").terms("bob", "donald")) //
        .head(name("ancestor").terms("A", "B")).body( //
            literal(name("parent").terms("A", "B"))) //
        .head(name("ancestor").terms("A", "B")).body( //
            literal(name("parent").terms("A", "C")) //
                .literal(name("ancestor").terms("C", "B"))));
  }
}
