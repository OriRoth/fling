package org.spartan.fajita.api.examples.balancedParenthesis;

import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.NT.B;
import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.Term.lp;
import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.Term.rp;

import java.io.IOException;

import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class BalancedParenthesis {
  public static void expressionBuilder() {
    // new Q0().lp().rp().lp().rp().lp().lp().rp().rp().$();
    // new Q0().lp().lp().lp().rp().rp().rp().lp().rp().lp().rp().$();
  }

  static enum Term implements Terminal {
    lp, rp, build;
  }

  static enum NT implements NonTerminal {
    B;
  }

  /**
   * This example does not work because <B> is endable and also not endable.
   **/
  public static BNF buildBNF() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .start(B) //
        .derive(B).to(lp).and(B).and(rp).and(B) //
        /*        */.orNone().go();
    return bnf;
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(buildBNF());
  }
}
