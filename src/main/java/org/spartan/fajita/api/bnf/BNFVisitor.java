package org.spartan.fajita.api.bnf;

import java.util.List;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

@SuppressWarnings("unused") public abstract class BNFVisitor {
  final void visit(BNF bnf) {
    for (DerivationRule r : bnf.getRules()) {
      visitLHS(r.lhs);
      List<Symbol> rhs = r.getRHS();
      if (rhs.isEmpty())
        visitEpsilon();
      else
        for (Symbol s : rhs)
          visit(s);
      visitRHS(rhs);
      visit(r);
    }
  }
  void visit(NonTerminal nt) {
    //
  }
  void visit(Terminal t) {
    //
  }
  private final void visit(Symbol s) {
    if (s.isNonTerminal())
      visit((NonTerminal) s);
    else
      visit((Terminal) s);
  }
  void visitEpsilon() {
    //
  }
  void visitLHS(NonTerminal nt) {
    //
  }
  void visitRHS(List<Symbol> rhs) {
    //
  }
  void visit(DerivationRule rule) {
    //
  }
}
