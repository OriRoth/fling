package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.examples.BalancedParenthesis.NT.B;

import static org.spartan.fajita.api.examples.BalancedParenthesis.NT.S;
import static org.spartan.fajita.api.junk.Balanced.*;
import static org.spartan.fajita.api.examples.BalancedParenthesis.Term.*;

import java.io.IOException;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class BalancedParenthesis {
  private static final String apiName = "Balanced";

  static enum Term implements Terminal {
    lp, rp, build;
  }

  static enum NT implements NonTerminal {
    B, S;
  }

  /**
   * This example does not work because <B> is endable and also not endable.
   **/
  public static String buildBNF() {
    return Fajita.buildBNF(Term.class, NT.class) //
        .setApiName(apiName).start(S) //
        .derive(S).to(B).and(build) //
        .derive(B).to(lp).and(B).and(rp).and(B) //
        /*        */.orNone()//
        .go(Main.packagePath);
  }
  public static void compiles() {
    // Test trivial
    build().$();
    // Test casual
    lp().rp().build().$();
    // Test Deep
    /* */lp().lp().lp().lp().lp() //
        .rp().rp().rp().rp().rp() //
        .build().$();
    // Test Long
    lp().rp().lp().lp().rp().rp().build().$();
    // Test long and deep
    lp().lp().lp().lp().lp().rp().rp().rp().rp().lp().lp().rp().rp().rp().build().$();
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(apiName, buildBNF());
  }
}
