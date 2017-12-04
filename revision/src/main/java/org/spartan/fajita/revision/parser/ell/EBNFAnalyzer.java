package org.spartan.fajita.revision.parser.ell;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spartan.fajita.revision.bnf.DerivationRule;
import org.spartan.fajita.revision.bnf.EBNF;
import org.spartan.fajita.revision.parser.rll.Item;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.extendibles.Extendible;

public class EBNFAnalyzer {
  public final EBNF ebnf;
  private final Set<Symbol> nullableSymbols;
  private final Map<Symbol, Set<Terminal>> baseFirstSets;

  public EBNFAnalyzer(final EBNF ebnf) {
    this.ebnf = ebnf;
    nullableSymbols = calculateNullableSymbols();
    baseFirstSets = calculateSymbolFirstSet();
  }
  private Set<Symbol> calculateNullableSymbols() {
    Set<Symbol> nullables = new HashSet<>();
    boolean moreChanges;
    do {
      moreChanges = false;
      for (DerivationRule rule : ebnf.rules())
        if (rule.getRHS().stream().allMatch(x -> nullables.contains(x) || //
            x.isExtendible() && x.asExtendible().updateNullable(nullables)))
          moreChanges |= nullables.add(rule.lhs);
    } while (moreChanges);
    return nullables;
  }
  private Map<Symbol, Set<Terminal>> calculateSymbolFirstSet() {
    Map<Symbol, Set<Terminal>> $ = new HashMap<>();
    for (NonTerminal nt : ebnf.nonTerminals)
      $.put(nt, new HashSet<>());
    for (Extendible e : ebnf.extendibles)
      $.put(e, new HashSet<>());
    for (Verb term : ebnf.verbs)
      $.put(term, new HashSet<>(Arrays.asList(term)));
    boolean moreChanges;
    do {
      moreChanges = false;
      for (DerivationRule r : ebnf.rules())
        for (Symbol s : r.getRHS()) {
          moreChanges |= s.isExtendible() && s.asExtendible().updateFirstSet(nullableSymbols, $);
          moreChanges |= $.get(r.lhs).addAll($.getOrDefault(s, new HashSet<>()));
          if (!isNullable(s))
            break;
        }
    } while (moreChanges);
    return $;
  }
  public boolean isNullable(final Symbol... expression) {
    return Arrays.asList(expression).stream().allMatch(symbol -> nullableSymbols.contains(symbol));
  }
  public boolean isSuffixNullable(final Item i) {
    return isNullable(i.rule.getRHS().subList(i.dotIndex, i.rule.size()));
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
  public Set<Terminal> firstSetOf(Item i) {
    return firstSetOf(i.rule.getRHS().subList(i.dotIndex, i.rule.size()));
  }
  public boolean isNullable(List<Symbol> expr) {
    return isNullable(expr.toArray(new Symbol[] {}));
  }
  public static Symbol[] ruleSuffix(DerivationRule rule, int index) {
    return Arrays.copyOfRange(rule.getRHS().toArray(new Symbol[] {}), index, rule.size());
  }
}
