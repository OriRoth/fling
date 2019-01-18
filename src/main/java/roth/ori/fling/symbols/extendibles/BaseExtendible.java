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
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.GrammarElement;
import roth.ori.fling.symbols.Terminal;

@SuppressWarnings("hiding") public abstract class BaseExtendible implements Extendible {
  protected Symbol head;
  protected List<GrammarElement> symbols;
  protected List<GrammarElement> solvedSymbols;
  protected boolean isSolved = false;
  private Symbol lhs;
  private Function<Symbol, Symbol> producer;
  private List<DerivationRule> solvedRules = new LinkedList<>();
  private List<DerivationRule> rawSolution;

  public BaseExtendible(List<GrammarElement> symbols) {
    this.symbols = symbols;
  }
  @Override public void fixSymbols(Function<List<GrammarElement>, List<GrammarElement>> fix) {
    assert !isSolved;
    symbols = fix.apply(symbols);
  }
  @Override public Symbol head() {
    assert isSolved;
    return head;
  }
  @Override public List<DerivationRule> solve(Symbol lhs, Function<Symbol, Symbol> producer) {
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
  protected Symbol nonTerminal() {
    return producer.apply(lhs);
  }
  protected GrammarElement solve(GrammarElement s) {
    solvedRules.addAll(s.solve(lhs, producer));
    return s.head();
  }
  protected List<GrammarElement> solve(List<GrammarElement> ss) {
    return ss.stream().map(x -> solve(x)).collect(Collectors.toList());
  }
  protected void addRule(Symbol lhs, List<GrammarElement> rhs) {
    solvedRules.add(new DerivationRule(lhs, rhs));
  }
  protected void addRule(GrammarElement lhs, List<GrammarElement> rhs) {
    addRule((Symbol) lhs, rhs);
  }
  @Override public List<GrammarElement> symbols() {
    return new LinkedList<>(symbols);
  }
  @Override public boolean updateNullable(Set<GrammarElement> knownNullables) {
    if (knownNullables.contains(this))
      return false;
    if (isNullable(knownNullables)) {
      knownNullables.add(this);
      return true;
    }
    return false;
  }
  protected abstract boolean isNullable(Set<GrammarElement> knownNullables);
  @Override public boolean updateFirstSet(Set<GrammarElement> nullables, Map<GrammarElement, Set<Terminal>> knownFirstSets) {
    Set<Terminal> fs = getFirstSet(nullables, knownFirstSets);
    if (fs.isEmpty())
      return false;
    knownFirstSets.putIfAbsent(this, new HashSet<>());
    return knownFirstSets.get(this).addAll(fs);
  }
  protected abstract Set<Terminal> getFirstSet(Set<GrammarElement> nullables, Map<GrammarElement, Set<Terminal>> knownFirstSets);
  @Override public List<DerivationRule> rawSolution() {
    assert isSolved;
    if (rawSolution != null)
      return rawSolution;
    rawSolution = new LinkedList<>();
    Map<GrammarElement, Extendible> heads = computeHeads();
    Set<Symbol> toConclude = new HashSet<>(), seen = new HashSet<>();
    toConclude.add(head);
    do {
      Set<Symbol> current = toConclude;
      seen.addAll(toConclude);
      toConclude = new HashSet<>();
      for (DerivationRule r : solvedRules)
        if (current.contains(r.head)) {
          List<GrammarElement> rhs = new LinkedList<>();
          for (GrammarElement s : r.body()) {
            if (heads.containsKey(s)) {
              rhs.add(heads.get(s));
              continue;
            }
            if (s.isNonTerminal() && !seen.contains(s))
              toConclude.add(s.asNonTerminal());
            rhs.add(s);
          }
          rawSolution.add(new DerivationRule(r.head, rhs));
        }
    } while (!toConclude.isEmpty());
    return rawSolution;
  }
  private Map<GrammarElement, Extendible> computeHeads() {
    Map<GrammarElement, Extendible> $ = new HashMap<>();
    for (GrammarElement s : symbols)
      if (s.isExtendible())
        $.put(s.head(), s.asExtendible());
    return $;
  }
}
