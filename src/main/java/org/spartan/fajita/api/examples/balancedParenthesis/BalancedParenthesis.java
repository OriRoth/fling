package org.spartan.fajita.api.examples.balancedParenthesis;

import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.NT.B;
import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.Term.lp;
import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.Term.rp;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class BalancedParenthesis {
  public static void expressionBuilder() {
//    new Q0().lp().rp().lp().rp().lp().lp().rp().rp().$();
//    new Q0().lp().lp().lp().rp().rp().rp().lp().rp().lp().rp().$();
  }

  static enum Term implements Terminal {
    lp, rp, build;
  }

  static enum NT implements NonTerminal {
    B;
  }

  public static BNF buildBNF() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .start(B) //
        .derive(B).to(B).and(lp).and(B).and(rp) //
        /*        */.orNone()
        .go();
    return bnf;
  }
}
