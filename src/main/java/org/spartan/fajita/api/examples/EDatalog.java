package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.EFajita.attribute;
import static org.spartan.fajita.api.EFajita.oneOrMore;
import static org.spartan.fajita.api.examples.EDatalog.NT.BODY;
import static org.spartan.fajita.api.examples.EDatalog.NT.LITERAL;
import static org.spartan.fajita.api.examples.EDatalog.NT.RULE;
import static org.spartan.fajita.api.examples.EDatalog.NT.S;
import static org.spartan.fajita.api.examples.EDatalog.Term.body;
import static org.spartan.fajita.api.examples.EDatalog.Term.fact;
import static org.spartan.fajita.api.examples.EDatalog.Term.head;
import static org.spartan.fajita.api.examples.EDatalog.Term.literal;
import static org.spartan.fajita.api.examples.EDatalog.Term.name;
import static org.spartan.fajita.api.examples.EDatalog.Term.terms;
import static org.spartan.fajita.api.junk.Body1.literal;
import static org.spartan.fajita.api.junk.Datalog.fact;
import static org.spartan.fajita.api.junk.Literal.name;

import java.io.IOException;

import org.spartan.fajita.api.EFajita;
import org.spartan.fajita.api.EFajita.FajitaBNF;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.type.VarArgs;
import org.spartan.fajita.api.export.Grammar;

// TODO Roth add OR
public class EDatalog extends Grammar {
  public static enum Term implements Terminal {
    head, body, fact, literal, name, terms
  }

  public static enum NT implements NonTerminal {
    S, RULE, LITERAL, BODY
  }

  @Override public FajitaBNF bnf() {
    return EFajita.build(getClass(), Term.class, NT.class) //
        .setApiName("Datalog") //
        .start(S) //
        .derive(S).to(oneOrMore(RULE)) //
        .derive(RULE) //
        /**/.to(attribute(fact, LITERAL)) //
        /**/.or(attribute(head, LITERAL)).and(BODY) //
        .derive(BODY).to(attribute(body, oneOrMore(attribute(literal, LITERAL)))) //
        .derive(LITERAL).to(attribute(name, String.class), attribute(terms, new VarArgs(String.class)));
  }
  /**
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    // System.out.println(bnf().go().toString(ASCII));
    // new
    // EDatalog().generateGrammarFiles(org.spartan.fajita.api.Main.packagePath);
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
