package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.examples.NestedRecursion.Term.*;
import static org.spartan.fajita.api.examples.NestedRecursion.NT.*;
import static org.spartan.fajita.api.junk.Balancedparent.*;

import java.io.IOException;
import org.spartan.fajita.api.Main;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.Fajita.Deriver;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.rllp.RLLP;

public class NestedRecursion {
  private static final String apiName = "BalancedParent";

  static enum Term implements Terminal {
    a, b;
  }

  static enum NT implements NonTerminal {
    A, B, S
  }

  public static Deriver fajita() {
    return Fajita.buildBNF(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(S) //
        .derive(S).to(A).and(S).and(B) //
        /**/.orNone()//
        .derive(A).to(a) //
        .derive(B).to(b);
  }
  public static void main(String[] args) throws IOException {
    RLLP rllp = new RLLP(fajita().go());
    System.out.println(rllp);
    Main.apiGenerator(fajita().go(Main.packagePath));
  }
  static void test() {
    a().b();
    a().a().b().b();
    a().a().a().a().b().b().b().b();
  }
}
