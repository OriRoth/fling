package org.spartan.fajita.api.examples;

import java.io.IOException;
import java.util.Map;

import static org.spartan.fajita.api.examples.Even.Term.*;
import static org.spartan.fajita.api.examples.Even.NT.*;
import static org.spartan.fajita.api.junk.Even.*;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class Even {
  private static final String apiName = "Even";

  static enum Term implements Terminal {
    s, m
  }

  static enum NT implements NonTerminal {
    S
  }

  public static Map<String, String> buildBNF() {
    return Fajita.buildBNF(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(S) //
        .derive(S).to(s).and(S).and(s).or(m) //
        .go(Main.packagePath);
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(buildBNF());
  }
  static void test() {
    m().s(); // Should NOT compile
  }
}
