package roth.ori.fling.symbols.extendibles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import roth.ori.fling.bnf.DerivationRule;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;

@SuppressWarnings("hiding") public abstract class BaseExtendible implements Extendible {
  protected NonTerminal head;
  protected List<Symbol> symbols;
  protected List<Symbol> solvedSymbols;
  protected boolean isSolved = false;
  private NonTerminal lhs;
  private Function<NonTerminal, NonTerminal> producer;
  private List<DerivationRule> solvedRules = new LinkedList<>();
  private List<DerivationRule> rawSolution;

  public BaseExtendible(List<Symbol> symbols) {
    this.symbols = symbols;
  }
  @Override public void fixSymbols(Function<List<Symbol>, List<Symbol>> fix) {
    assert !isSolved;
    symbols = fix.apply(symbols);
  }
  @Override public NonTerminal head() {
    assert isSolved;
    return head;
  }
  @Override public List<DerivationRule> solve(NonTerminal lhs, Function<NonTerminal, NonTerminal> producer) {
    if (isSolved)
      return solvedRules;
    isSolved = true;
    this.lhs = lhs;
    this.producer = producer;
    solve();
    return solvedRules;
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
    solvedRules.addAll(s.solve(lhs, producer));
    return s.head();
  }
  protected List<Symbol> solve(List<Symbol> ss) {
    return ss.stream().map(x -> solve(x)).collect(Collectors.toList());
  }
  protected void addRule(NonTerminal lhs, List<Symbol> rhs) {
    solvedRules.add(new DerivationRule(lhs, rhs));
  }
  protected void addRule(Symbol lhs, List<Symbol> rhs) {
    addRule((NonTerminal) lhs, rhs);
  }
  @Override public List<Symbol> symbols() {
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
  @Override public List<DerivationRule> rawSolution() {
    assert isSolved;
    if (rawSolution != null)
      return rawSolution;
    rawSolution = new LinkedList<>();
    Map<Symbol, Extendible> heads = computeHeads();
    Set<NonTerminal> toConclude = new HashSet<>(), seen = new HashSet<>();
    toConclude.add(head);
    do {
      Set<NonTerminal> current = toConclude;
      seen.addAll(toConclude);
      toConclude = new HashSet<>();
      for (DerivationRule r : solvedRules)
        if (current.contains(r.lhs)) {
          List<Symbol> rhs = new LinkedList<>();
          for (Symbol s : r.getRHS()) {
            if (heads.containsKey(s)) {
              rhs.add(heads.get(s));
              continue;
            }
            if (s.isNonTerminal() && !seen.contains(s))
              toConclude.add(s.asNonTerminal());
            rhs.add(s);
          }
          rawSolution.add(new DerivationRule(r.lhs, rhs));
        }
    } while (!toConclude.isEmpty());
    return rawSolution;
  }
  private Map<Symbol, Extendible> computeHeads() {
    Map<Symbol, Extendible> $ = new HashMap<>();
    for (Symbol s : symbols)
      if (s.isExtendible())
        $.put(s.head(), s.asExtendible());
    return $;
  }
}
