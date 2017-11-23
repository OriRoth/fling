package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.bnf.BNFRenderer.builtin.*;
import static org.spartan.fajita.api.examples.TwoParentsBNF.NT.A;
import static org.spartan.fajita.api.examples.TwoParentsBNF.NT.B;
import static org.spartan.fajita.api.examples.TwoParentsBNF.NT.C;
import static org.spartan.fajita.api.examples.TwoParentsBNF.NT.D;
import static org.spartan.fajita.api.examples.TwoParentsBNF.NT.S;
import static org.spartan.fajita.api.examples.TwoParentsBNF.Term.a;
import static org.spartan.fajita.api.examples.TwoParentsBNF.Term.b;

import java.util.Map;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.Fajita.Deriver;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

// TODO Roth add OR
public class TwoParentsBNF {
  private static final String apiName = "TwoParentsBNF";

  static enum Term implements Terminal {
    a, b
  }

  static enum NT implements NonTerminal {
    S, A, B, C, D
  }

  public static Deriver bnf() {
    return Fajita.buildBNF(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(S) //
        .derive(S).to(A).and(B) //
        .derive(A).to(C).or(D) //
        .derive(B).to(C).or(D) //
        .derive(C).to(a) //
        .derive(D).to(a).and(b);
    // return Fajita.buildBNF(Term.class, NT.class) //
    // .setApiName(apiName) //
    // .start(S) //
    // .derive(S).to(A).or(B) //
    // .derive(A).to(a) //
    // .derive(B).to(a).and(b);
  }
  public static Map<String, String> buildBNF() {
    return bnf().go(Main.packagePath);
  }
  public static void main(String[] args) {
    System.out.println(bnf().go().toString(JAMOOS_INTERFACES));
    // Main.apiGenerator(buildBNF());
  }
}
