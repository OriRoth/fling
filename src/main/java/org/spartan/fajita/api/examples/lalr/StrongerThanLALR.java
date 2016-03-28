package org.spartan.fajita.api.examples.lalr;

import static org.spartan.fajita.api.examples.lalr.StrongerThanLALR.NT.E;
import static org.spartan.fajita.api.examples.lalr.StrongerThanLALR.NT.F;
import static org.spartan.fajita.api.examples.lalr.StrongerThanLALR.NT.S;
import static org.spartan.fajita.api.examples.lalr.StrongerThanLALR.Term.a;
import static org.spartan.fajita.api.examples.lalr.StrongerThanLALR.Term.b;
import static org.spartan.fajita.api.examples.lalr.StrongerThanLALR.Term.c;
import static org.spartan.fajita.api.examples.lalr.StrongerThanLALR.Term.d;
import static org.spartan.fajita.api.examples.lalr.StrongerThanLALR.Term.e;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
public class StrongerThanLALR {
  public static void expressionBuilder() {
//    new Q0().a().e().c().$();
//    new Q0().a().e().d().$();
//    new Q0().b().e().c().$();
//    new Q0().b().e().d().$();
  }

  static enum Term implements Terminal {
    a, b, c, d,e;
  }

  static enum NT implements NonTerminal {
    S,E,F;
  }

  public static BNF buildBNF() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .start(S) //
        .derive(S).to(a).and(E).and(c) //
        /* */.or(a).and(F).and(d) //
        /* */.or(b).and(F).and(c) //
        /* */.or(b).and(E).and(d) //
        .derive(F).to(e) //
        .derive(E).to(e) //
        .go();
    return bnf;
  }
}
