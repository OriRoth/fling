package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.examples.NestedRecursion.NT.*;
import static org.spartan.fajita.api.examples.NestedRecursion.Term.a;
import static org.spartan.fajita.api.examples.NestedRecursion.Term.b;

import java.io.IOException;
import java.util.Map;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class NestedRecursion {
  private static final String apiName = "BalancedParent";

  static enum Term implements Terminal {
    a, b;
  }

  static enum NT implements NonTerminal {
    A, B, S
  }

  public static Map<String, String> buildApi() {
    return Fajita.buildBNF(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(S) //
        .derive(S).to(A).and(S).and(B) //
        /*    */.orNone()//
        .derive(A).to(a) //
        .derive(B).to(b) //
        .go(Main.packagePath);
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(buildApi());
  }
  void test() {
//    a().a().a().b().b().$();
//    a().b().$();
//    a().a().a().a().b().b().b().b().$();
  }
}
