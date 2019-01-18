package roth.ori.fling.parser.ell;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import roth.ori.fling.bnf.DerivationRule;
import roth.ori.fling.bnf.EBNF;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.GrammarElement;
import roth.ori.fling.symbols.Terminal;
import roth.ori.fling.symbols.Verb;
import roth.ori.fling.symbols.extendibles.Extendible;

public class EBNFAnalyzer {
  public final EBNF ebnf;
  public final Map<GrammarElement, Set<List<GrammarElement>>> normalized;
  private final Set<GrammarElement> nullableSymbols;
  public final Map<GrammarElement, Set<Terminal>> baseFirstSets;

  public EBNFAnalyzer(final EBNF ebnf, Map<GrammarElement, Set<List<GrammarElement>>> normalized) {
    this.ebnf = ebnf;
    this.normalized = normalized;
    nullableSymbols = calculateNullableSymbols();
    baseFirstSets = calculateSymbolFirstSet();
  }
  public EBNFAnalyzer(Map<Symbol, Set<List<GrammarElement>>> n, Set<Symbol> start) {
    this(recreateEBNF(n, start), new HashMap<>(n));
  }
  private Set<GrammarElement> calculateNullableSymbols() {
    Set<GrammarElement> nullables = new HashSet<>();
    boolean moreChanges;
    do {
      moreChanges = false;
      for (GrammarElement lhs : normalized.keySet())
        for (List<GrammarElement> clause : normalized.get(lhs))
          if (clause.stream().allMatch(x -> nullables.contains(x) || //
              x.isExtendible() && x.asExtendible().updateNullable(nullables)))
            moreChanges |= nullables.add(lhs);
    } while (moreChanges);
    return nullables;
  }
  private Map<GrammarElement, Set<Terminal>> calculateSymbolFirstSet() {
    Map<GrammarElement, Set<Terminal>> $ = new HashMap<>();
    for (GrammarElement s : normalized.keySet())
      $.put(s, new HashSet<>());
    for (Extendible e : ebnf.extendibles)
      $.put(e, new HashSet<>());
    for (Verb term : ebnf.verbs)
      $.put(term, new HashSet<>(Arrays.asList(term)));
    boolean moreChanges;
    do {
      moreChanges = false;
      for (GrammarElement lhs : normalized.keySet())
        for (List<GrammarElement> clause : normalized.get(lhs))
          for (GrammarElement s : clause) {
            moreChanges |= s.isExtendible() && s.asExtendible().updateFirstSet(nullableSymbols, $);
            moreChanges |= $.get(lhs).addAll($.getOrDefault(s, new HashSet<>()));
            if (!isNullable(s))
              break;
          }
    } while (moreChanges);
    return $;
  }
  public boolean isNullable(final GrammarElement... expression) {
    return Arrays.asList(expression).stream().allMatch(symbol -> nullableSymbols.contains(symbol));
  }
  public Set<Terminal> firstSetOf(final GrammarElement... expression) {
    Set<Terminal> $ = new HashSet<>();
    for (GrammarElement symbol : expression) {
      $.addAll(baseFirstSets.get(symbol));
      if (!isNullable(symbol))
        break;
    }
    return $;
  }
  public Set<Terminal> firstSetOf(final List<GrammarElement> expression) {
    return firstSetOf(expression.toArray(new GrammarElement[] {}));
  }
  public boolean isNullable(List<GrammarElement> expr) {
    return isNullable(expr.toArray(new GrammarElement[] {}));
  }
  public static GrammarElement[] ruleSuffix(DerivationRule rule, int index) {
    return Arrays.copyOfRange(rule.body().toArray(new GrammarElement[] {}), index, rule.size());
  }
  public static RuntimeException reject() {
    return new ELLRecognizerRejection();
  }
  public static RuntimeException reject(String message) {
    return new ELLRecognizerRejection(message);
  }
  private static EBNF recreateEBNF(Map<Symbol, Set<List<GrammarElement>>> n, Set<Symbol> start) {
    Set<Verb> vs = new HashSet<>();
    Set<Symbol> nts = new HashSet<>();
    Set<Extendible> exs = new HashSet<>();
    Set<DerivationRule> rs = new HashSet<>();
    for (Symbol lhs : n.keySet()) {
      nts.add(lhs.asNonTerminal());
      for (List<GrammarElement> clause : n.get(lhs)) {
        rs.add(new DerivationRule(lhs.asNonTerminal(), clause));
        for (GrammarElement s : clause)
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

  public static class ELLRecognizerRejection extends RuntimeException {
    private static final long serialVersionUID = 1383359472626586435L;

    public ELLRecognizerRejection() {
      super(ELLRecognizer.class.getSimpleName() + " rejected");
    }
    public ELLRecognizerRejection(String message) {
      super(ELLRecognizer.class.getSimpleName() + " rejected: " + message);
    }
  }
}
