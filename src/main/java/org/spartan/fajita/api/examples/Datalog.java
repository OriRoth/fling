package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.bnf.BNFRenderer.builtin.*;
import static org.spartan.fajita.api.examples.Datalog.NT.BODY;
import static org.spartan.fajita.api.examples.Datalog.NT.LITERAL;
import static org.spartan.fajita.api.examples.Datalog.NT.LITERALS;
import static org.spartan.fajita.api.examples.Datalog.NT.LITERALS_OR_NONE;
import static org.spartan.fajita.api.examples.Datalog.NT.RULE;
import static org.spartan.fajita.api.examples.Datalog.NT.RULES;
import static org.spartan.fajita.api.examples.Datalog.Term.body;
import static org.spartan.fajita.api.examples.Datalog.Term.fact;
import static org.spartan.fajita.api.examples.Datalog.Term.head;
import static org.spartan.fajita.api.examples.Datalog.Term.literal;
import static org.spartan.fajita.api.examples.Datalog.Term.name;
import static org.spartan.fajita.api.examples.Datalog.Term.terms;
import static org.spartan.fajita.api.junk.Datalog2.fact;
import static org.spartan.fajita.api.junk.Literal2.name;
import static org.spartan.fajita.api.junk.Literals2.literal;

import java.util.Map;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.Fajita.Deriver;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.type.VarArgs;

// TODO Roth add OR
public class Datalog {
  private static final String apiName = "Datalog";

  static enum Term implements Terminal {
    head, body, fact, literal, name, terms
  }

  static enum NT implements NonTerminal {
    RULES, RULE, LITERAL, BODY, LITERALS, LITERALS_OR_NONE
  }

  public static Deriver bnf() {
    return Fajita.buildBNF(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(RULES) //
        .derive(RULES).to(RULE).and(RULES).orNone() //
        .derive(RULE) //
        /**/.to(fact, LITERAL) //
        /**/.or(head, LITERAL).and(BODY) //
        .derive(BODY).to(body, LITERALS) //
        .derive(LITERALS).to(literal, LITERAL).and(LITERALS_OR_NONE) //
        .derive(LITERALS_OR_NONE).to(literal, LITERAL).and(LITERALS_OR_NONE).orNone() //
        .derive(LITERAL).to(name, String.class).and(terms, new VarArgs(String.class));
  }
  public static Map<String, String> buildBNF() {
    return bnf().go(Main.packagePath);
  }
  public static void main(String[] args) {
    System.out.println(bnf().go().toString(ASCII));
    // System.out.println(bnf().go().toString(JAMOOS_CLASSES));
    // Main.apiGenerator(buildBNF());
    test();
  }
  static void test() {
    fact(name("parent").terms("john", "bob")) //
        .fact(name("parent").terms("bob", "donald")) //
        .head(name("ancestor").terms("A", "B")).body( //
            literal(name("parent").terms("A", "B"))) //
        .head(name("ancestor").terms("A", "B")).body( //
            literal(name("parent").terms("A", "C")) //
                .literal(name("ancestor").terms("C", "B")));
  }
}
