package org.spartan.fajita.revision.parser.ell;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spartan.fajita.revision.bnf.DerivationRule;
import org.spartan.fajita.revision.bnf.EBNF;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.extendibles.Extendible;

public class EBNFAnalyzer {
  public final EBNF ebnf;
  public final Map<Symbol, Set<List<Symbol>>> normalized;
  private final Set<Symbol> nullableSymbols;
  private final Map<Symbol, Set<Terminal>> baseFirstSets;

  public EBNFAnalyzer(final EBNF ebnf, Map<Symbol, Set<List<Symbol>>> normalized) {
    this.ebnf = ebnf;
    this.normalized = normalized;
    nullableSymbols = calculateNullableSymbols();
    baseFirstSets = calculateSymbolFirstSet();
  }
  public EBNFAnalyzer(Map<NonTerminal, Set<List<Symbol>>> n, Set<NonTerminal> start) {
    this(recreateEBNF(n, start), new HashMap<>(n));
  }
  private Set<Symbol> calculateNullableSymbols() {
    Set<Symbol> nullables = new HashSet<>();
    boolean moreChanges;
    do {
      moreChanges = false;
      for (Symbol lhs : normalized.keySet())
        for (List<Symbol> clause : normalized.get(lhs))
          if (clause.stream().allMatch(x -> nullables.contains(x) || //
              x.isExtendible() && x.asExtendible().updateNullable(nullables)))
            moreChanges |= nullables.add(lhs);
    } while (moreChanges);
    return nullables;
  }
  private Map<Symbol, Set<Terminal>> calculateSymbolFirstSet() {
    Map<Symbol, Set<Terminal>> $ = new HashMap<>();
    for (Symbol s : normalized.keySet())
      $.put(s, new HashSet<>());
    for (Extendible e : ebnf.extendibles)
      $.put(e, new HashSet<>());
    for (Verb term : ebnf.verbs)
      $.put(term, new HashSet<>(Arrays.asList(term)));
    boolean moreChanges;
    do {
      moreChanges = false;
      for (Symbol lhs : normalized.keySet())
        for (List<Symbol> clause : normalized.get(lhs))
          for (Symbol s : clause) {
            moreChanges |= s.isExtendible() && s.asExtendible().updateFirstSet(nullableSymbols, $);
            moreChanges |= $.get(lhs).addAll($.getOrDefault(s, new HashSet<>()));
            if (!isNullable(s))
              break;
          }
    } while (moreChanges);
    return $;
  }
  public boolean isNullable(final Symbol... expression) {
    return Arrays.asList(expression).stream().allMatch(symbol -> nullableSymbols.contains(symbol));
  }
  public Set<Terminal> firstSetOf(final Symbol... expression) {
    Set<Terminal> $ = new HashSet<>();
    for (Symbol symbol : expression) {
      $.addAll(baseFirstSets.get(symbol));
      if (!isNullable(symbol))
        break;
    }
    return $;
  }
  public Set<Terminal> firstSetOf(final List<Symbol> expression) {
    return firstSetOf(expression.toArray(new Symbol[] {}));
  }
  public boolean isNullable(List<Symbol> expr) {
    return isNullable(expr.toArray(new Symbol[] {}));
  }
  public static Symbol[] ruleSuffix(DerivationRule rule, int index) {
    return Arrays.copyOfRange(rule.getRHS().toArray(new Symbol[] {}), index, rule.size());
  }
  public static RuntimeException reject() {
    return new RuntimeException(ELLRecognizer.class.getSimpleName() + " rejected");
  }
  public static RuntimeException reject(String message) {
    return new RuntimeException(ELLRecognizer.class.getSimpleName() + " rejected: " + message);
  }
  private static EBNF recreateEBNF(Map<NonTerminal, Set<List<Symbol>>> n, Set<NonTerminal> start) {
    Set<Verb> vs = new HashSet<>();
    Set<NonTerminal> nts = new HashSet<>();
    Set<Extendible> exs = new HashSet<>();
    Set<DerivationRule> rs = new HashSet<>();
    for (NonTerminal lhs : n.keySet()) {
      nts.add(lhs.asNonTerminal());
      for (List<Symbol> clause : n.get(lhs)) {
        rs.add(new DerivationRule(lhs.asNonTerminal(), clause));
        for (Symbol s : clause)
          if (s.isVerb())
            vs.add(s.asVerb());
          else if (s.isExtendible())
            exs.add(s.asExtendible());
          else
            nts.add(s.asNonTerminal());
      }
    }
    return new EBNF(vs, nts, exs, rs, start, "#");
  }
}
