package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.examples.Nested.NT.*;
import static org.spartan.fajita.api.examples.Nested.Term.*;
import static org.spartan.fajita.api.junk.Regex.oneOf;

import java.io.IOException;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.junk.Regex;

public class Nested {
  static enum Term implements Terminal {
    super_wrapped, wrap
  }

  static enum NT implements NonTerminal {
    WRAPPER, START
  }

  private static String apiName = "NestedAPI";

  public static String buildAPI() {
    String api = Fajita.buildBNF(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(START) //
        .derive(START).to(WRAPPER) //
        .derive(WRAPPER).to(wrap, WRAPPER) //
        .or(super_wrapped)
        .go();
    return api;
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(apiName, buildAPI());
  }
  public static void expressionBuilder() {
    // compiles
    oneOf(Regex.range('a').to('z').$()).$();
    // does not compile
  }
}
