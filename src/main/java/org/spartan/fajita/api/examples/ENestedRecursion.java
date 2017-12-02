package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.examples.ENestedRecursion.Term.*;
import static org.spartan.fajita.api.examples.ENestedRecursion.NT.*;
import static org.spartan.fajita.api.junk.Ebalancedparent.*;

import java.io.IOException;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.EFajita;
import org.spartan.fajita.api.EFajita.FajitaBNF;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.rllp.RLLP;

public class ENestedRecursion {
  private static final String apiName = "EBalancedParent";

  static enum Term implements Terminal {
    a, b;
  }

  static enum NT implements NonTerminal {
    A, B, S
  }

  public static FajitaBNF fajita() {
    return EFajita.build(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(S) //
        .derive(S).to(A, S, B) //
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
