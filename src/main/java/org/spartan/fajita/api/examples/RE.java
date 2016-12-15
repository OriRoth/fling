package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.examples.RE.NT.*;
import static org.spartan.fajita.api.examples.RE.Term.*;

import java.io.IOException;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class RE {
  static enum Term implements Terminal {
    range, to, oneOf, chars
  }

  static enum NT implements NonTerminal {
    RANGE, ONE_OF, START
  }

  private static String apiName = "Regex";

  public static String buildAPI() {
    String api = Fajita.buildBNF(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(START) //
        .derive(START).to(ONE_OF) //
        .derive(ONE_OF).to(oneOf, RANGE) //
        .derive(RANGE).to(range, char.class).and(to, char.class) //
        .go();
    return api;
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(apiName, buildAPI());
  }
  public static void expressionBuilder() {
    // compiles
    // does not compile
  }
}
