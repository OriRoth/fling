package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.examples.Datalog.Term.*;
import static org.spartan.fajita.revision.examples.Datalog.NT.*;
import static org.spartan.fajita.revision.api.Fajita.attribute;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;

import java.io.IOException;

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
    S, RULE, RULES, LITERAL, LITERALS, BODY
  }

  @Override public FajitaBNF bnf() {
    // return Fajita.build(getClass(), Term.class, NT.class, Main.packagePath,
    // Main.projectPath) //
    // .setApiName("Datalog") //
    // .start(S) //
    // .derive(S).to(oneOrMore(RULE)) //
    // .derive(RULE) //
    // /**/.to(attribute(fact, LITERAL)) //
    // /**/.or(attribute(head, LITERAL)).and(BODY) //
    // .derive(BODY).to(attribute(body, oneOrMore(attribute(literal, LITERAL))))
    // //
    // .derive(LITERAL).to(attribute(name, String.class), attribute(terms, new
    // VarArgs(String.class)));
    return Fajita.build(getClass(), Term.class, NT.class, "Datalog", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to(RULES) //
        .derive(RULES).to(RULE, RULES).or(RULE) //
        .derive(RULE) //
        /**/.to(attribute(fact, LITERAL)) //
        /**/.or(attribute(head, LITERAL)).and(BODY) //
        .derive(BODY).to(attribute(body, LITERALS)) //
        .derive(LITERALS).to(attribute(literal, LITERAL), LITERALS).or(attribute(literal, LITERAL)) //
        .derive(LITERAL).to(attribute(name, String.class), attribute(terms, new VarArgs(String.class)));
  }
  /**
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    // System.out.println(bnf().go().toString(ASCII));
    new Datalog().generateGrammarFiles();
    // test();
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
