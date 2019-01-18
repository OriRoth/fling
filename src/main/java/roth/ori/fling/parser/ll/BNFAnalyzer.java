package roth.ori.fling.parser.ll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import roth.ori.fling.bnf.BNF;
import roth.ori.fling.bnf.DerivationRule;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.GrammarElement;
import roth.ori.fling.symbols.Verb;

public class BNFAnalyzer {
  public final BNF bnf;
  private final Collection<NonTerminal> nullableSymbols;
  private final Map<GrammarElement, Collection<Verb>> baseFirstSets;
  private final Map<NonTerminal, Map<Verb, List<GrammarElement>>> llClosure;

  public BNFAnalyzer(final BNF bnf) {
    this.bnf = bnf;
    nullableSymbols = calculateNullableSymbols();
    baseFirstSets = calculateSymbolFirstSet();
    llClosure = new HashMap<>();
    for (NonTerminal nt : bnf.nonTerminals)
      for (Verb v : bnf.verbs)
        llClosure(nt, v);
  }
  private Collection<NonTerminal> calculateNullableSymbols() {
    Set<NonTerminal> nullables = new HashSet<>();
    boolean moreChanges;
    do {
      moreChanges = false;
      for (DerivationRule rule : bnf.rules())
        if (rule.getRHS().stream().allMatch(child -> nullables.contains(child)))
          moreChanges |= nullables.add(rule.lhs);
    } while (moreChanges);
    return nullables;
  }
  private Map<GrammarElement, Collection<Verb>> calculateSymbolFirstSet() {
    Map<GrammarElement, Collection<Verb>> $ = new HashMap<>();
    for (NonTerminal nt : bnf.nonTerminals)
      $.put(nt, new LinkedHashSet<>());
    for (Verb term : bnf.verbs)
      $.put(term, new LinkedHashSet<>(Arrays.asList(term)));
    boolean moreChanges;
    do {
      moreChanges = false;
      for (DerivationRule dRule : bnf.rules())
        for (GrammarElement symbol : dRule.getRHS()) {
          moreChanges |= $.get(dRule.lhs).addAll($.getOrDefault(symbol, new TreeSet<>()));
          if (!isNullable(symbol))
            break;
        }
    } while (moreChanges);
    return $;
  }
  public boolean isNullable(final GrammarElement... expression) {
    return Arrays.asList(expression).stream().allMatch(symbol -> nullableSymbols.contains(symbol));
  }
  public Collection<Verb> firstSetOf(final GrammarElement... expression) {
    List<Verb> $ = new ArrayList<>();
    for (GrammarElement symbol : expression) {
      $.addAll(baseFirstSets.get(symbol));
      if (!isNullable(symbol))
        break;
    }
    return $;
  }
  public Collection<Verb> firstSetOf(final List<GrammarElement> expression) {
    return firstSetOf(expression.toArray(new GrammarElement[expression.size()]));
  }
  // NOTE This is the "consolidation" algorithm
  public List<GrammarElement> llClosure(final NonTerminal nt, final Verb v) {
    if (llClosure.containsKey(nt) && llClosure.get(nt).containsKey(v))
      return llClosure.get(nt).get(v);
    llClosure.putIfAbsent(nt, new HashMap<>());
    if (bnf.getRulesOf(nt).stream().noneMatch(d -> firstSetOf(d.getRHS()).contains(v))) {
      llClosure.get(nt).put(v, null);
      return null;
    }
    Stack<GrammarElement> $ = new Stack<>();
    $.add(nt);
    outer: for (;;) {
      if ($.isEmpty()) {
        llClosure.get(nt).put(v, $);
        return $;
      }
      GrammarElement current = $.pop();
      if (current.isVerb()) {
        assert current.equals(v);
        llClosure.get(nt).put(v, $);
        return $;
      }
      assert current.isNonTerminal();
      for (DerivationRule r : bnf.getRulesOf(current.asNonTerminal()))
        if (firstSetOf(r.getRHS()).contains(v)) {
          List<GrammarElement> a = new ArrayList<>(r.getRHS());
          Collections.reverse(a);
          for (GrammarElement s : a)
            $.push(s);
          continue outer;
        }
      assert isNullable(current);
    }
  }
  public boolean isNullable(List<GrammarElement> expr) {
    return isNullable(expr.toArray(new GrammarElement[] {}));
  }
  public static GrammarElement[] ruleSuffix(DerivationRule rule, int index) {
    return Arrays.copyOfRange(rule.getRHS().toArray(new GrammarElement[] {}), index, rule.size());
  }
}
