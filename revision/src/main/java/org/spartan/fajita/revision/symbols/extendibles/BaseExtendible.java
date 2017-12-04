package org.spartan.fajita.revision.symbols.extendibles;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.spartan.fajita.revision.bnf.DerivationRule;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;

@SuppressWarnings("hiding") public abstract class BaseExtendible implements Extendible {
  protected NonTerminal head;
  protected List<Symbol> symbols;
  protected boolean isSolved = false;
  private NonTerminal lhs;
  private Function<NonTerminal, NonTerminal> producer;
  protected List<DerivationRule> $ = new LinkedList<>();

  public BaseExtendible(List<Symbol> symbols) {
    this.symbols = symbols;
  }
  @Override public Symbol head() {
    assert isSolved;
    return head;
  }
  @Override public List<DerivationRule> solve(NonTerminal lhs, Function<NonTerminal, NonTerminal> producer) {
    assert !isSolved;
    isSolved = true;
    this.lhs = lhs;
    this.producer = producer;
    solve();
    return $;
  }
  protected abstract void solve();
  @Override public String name() {
    return "#" + getClass().getSimpleName();
  }
  @Override public String toString() {
    return getClass().getSimpleName() + symbols;
  }
  @Override public boolean isNullable() {
    assert isSolved;
    return nullable();
  }
  protected abstract boolean nullable();
  @Override public List<Terminal> firstSet() {
    assert isSolved;
    return firsts();
  }
  protected abstract List<Terminal> firsts();
  protected NonTerminal nonTerminal() {
    return producer.apply(lhs);
  }
  protected Symbol solve(Symbol s) {
    $.addAll(s.solve(lhs, producer));
    return s.head();
  }
  protected List<Symbol> solve(List<Symbol> ss) {
    return ss.stream().map(x -> solve(x)).collect(Collectors.toList());
  }
  protected void addRule(NonTerminal lhs, List<Symbol> rhs) {
    $.add(new DerivationRule(lhs, rhs));
  }
  protected void addRule(Symbol lhs, List<Symbol> rhs) {
    addRule((NonTerminal) lhs, rhs);
  }
}
