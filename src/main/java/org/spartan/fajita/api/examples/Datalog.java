package org.spartan.fajita.api.examples;

import java.io.IOException;
import java.util.Map;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.Fajita.Deriver;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.type.VarArgs;

import static org.spartan.fajita.api.examples.Datalog.Term.*;
import static org.spartan.fajita.api.examples.Datalog.NT.*;
 import static org.spartan.fajita.api.junk.Datalog.*;
 import static org.spartan.fajita.api.junk.Literal.*;
 import static org.spartan.fajita.api.junk.Literals.*;

// TODO Roth add OR
public class Datalog {
  private static final String apiName = "Datalog";

  static enum Term implements Terminal {
    head, body, fact, literal, name, terms
  }

  static enum NT implements NonTerminal {
    RULES, RULE, LITERAL, BODY, LITERALS, LITERALS_OR_NONE
  }

  public static Deriver BNF() {
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
    return BNF().go(Main.packagePath);
  }
  public static void main(String[] args) throws IOException {
    System.out.println(BNF().toString());
    Main.apiGenerator(buildBNF());
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
