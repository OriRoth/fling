package org.spartan.fajita.revision.symbols.extendibles;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.spartan.fajita.revision.bnf.DerivationRule;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;

public class OneOrMore extends BaseExtendible {
  public OneOrMore(List<Symbol> symbols) {
    super(symbols);
  }
  @Override protected List<DerivationRule> solve() {
    head = nonTerminal();
    NonTerminal head2 = nonTerminal();
    symbols = solve(symbols);
    List<Symbol> rhs1 = new ArrayList<>(symbols);
    rhs1.add(head2);
    addRule(head, rhs1);
    List<Symbol> rhs2 = new ArrayList<>(symbols);
    rhs2.add(head2);
    addRule(head2, rhs2);
    addRule(head2, new LinkedList<>());
    return null;
  }
  @Override protected boolean nullable() {
    // TODO Auto-generated method stub
    return false;
  }
  @Override protected List<Terminal> firsts() {
    // TODO Auto-generated method stub
    return null;
  }
  public static OneOrMore oneOrMore(Symbol s, Symbol... ss) {
    return new OneOrMore(merge(s, ss));
  }
}
