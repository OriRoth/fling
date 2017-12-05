package org.spartan.fajita.revision.symbols.extendibles;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
  protected List<DerivationRule> solvedSymbols = new LinkedList<>();

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
    return solvedSymbols;
  }
  protected abstract void solve();
  @Override public String name() {
    return "#" + getClass().getSimpleName();
  }
  @Override public String toString() {
    return getClass().getSimpleName() + symbols;
  }
  protected NonTerminal nonTerminal() {
    return producer.apply(lhs);
  }
  protected Symbol solve(Symbol s) {
    solvedSymbols.addAll(s.solve(lhs, producer));
    return s.head();
  }
  protected List<Symbol> solve(List<Symbol> ss) {
    return ss.stream().map(x -> solve(x)).collect(Collectors.toList());
  }
  protected void addRule(NonTerminal lhs, List<Symbol> rhs) {
    solvedSymbols.add(new DerivationRule(lhs, rhs));
  }
  protected void addRule(Symbol lhs, List<Symbol> rhs) {
    addRule((NonTerminal) lhs, rhs);
  }
  @Override public List<Symbol> symbols() {
    assert isSolved;
    return new LinkedList<>(symbols);
  }
  @Override public boolean updateNullable(Set<Symbol> knownNullables) {
    if (knownNullables.contains(this))
      return false;
    if (isNullable(knownNullables)) {
      knownNullables.add(this);
      return true;
    }
    return false;
  }
  protected abstract boolean isNullable(Set<Symbol> knownNullables);
  @Override public boolean updateFirstSet(Set<Symbol> nullables, Map<Symbol, Set<Terminal>> knownFirstSets) {
    Set<Terminal> fs = getFirstSet(nullables, knownFirstSets);
    if (fs.isEmpty())
      return false;
    knownFirstSets.putIfAbsent(this, new HashSet<>());
    return knownFirstSets.get(this).addAll(fs);
  }
  protected abstract Set<Terminal> getFirstSet(Set<Symbol> nullables, Map<Symbol, Set<Terminal>> knownFirstSets);
}
