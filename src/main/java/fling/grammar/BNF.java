package fling.grammar;

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fling.grammar.sententials.Constants;
import fling.grammar.sententials.DerivationRule;
import fling.grammar.sententials.Notation;
import fling.grammar.sententials.SententialForm;
import fling.grammar.sententials.Symbol;
import fling.grammar.sententials.Variable;
import fling.grammar.sententials.Verb;
import fling.grammar.types.TypeParameter;

public class BNF {
  public final Set<DerivationRule> R;
  public final Set<Symbol> nullables;
  public final Map<Symbol, Set<Verb>> firsts;
  public final Map<Variable, Set<Verb>> follows;
  public final Set<Verb> Σ;
  public final Set<Variable> V;
  public final Variable startVariable;
  public final Set<Variable> headVariables;
  public final Map<Variable, Notation> extensionHeadsMapping;
  public final Set<Variable> extensionProducts;

  public BNF(Set<Verb> Σ, Set<? extends Variable> V, Set<DerivationRule> R, Variable startVariable, Set<Variable> headVariables,
      Map<Variable, Notation> extensionHeadsMapping, Set<Variable> extensionProducts, boolean addStartSymbolDerivationRules) {
    this.Σ = Σ;
    Σ.add(Constants.$$);
    this.V = new LinkedHashSet<>(V);
    this.R = R;
    if (addStartSymbolDerivationRules) {
      this.V.add(Constants.S);
      R.add(new DerivationRule(Constants.S, new ArrayList<>()));
      rhs(Constants.S).add(new SententialForm(startVariable));
    }
    this.headVariables = headVariables;
    this.startVariable = startVariable;
    this.extensionHeadsMapping = extensionHeadsMapping;
    this.extensionProducts = extensionProducts;
    this.nullables = getNullables();
    this.firsts = getFirsts();
    this.follows = getFollows();
  }
  public static <V extends Enum<V> & Variable> Builder<V> bnf(Class<V> V) {
    return new Builder<>(V);
  }
  public Set<Symbol> symbols() {
    Set<Symbol> $ = new LinkedHashSet<>();
    $.addAll(Σ);
    $.addAll(V);
    return unmodifiableSet($);
  }
  public List<SententialForm> rhs(Variable v) {
    return R.stream().filter(r -> r.lhs.equals(v)).findFirst().map(DerivationRule::rhs).orElse(null);
  }
  public boolean isNullable(Symbol... symbols) {
    return isNullable(Arrays.asList(symbols));
  }
  public boolean isNullable(List<Symbol> symbols) {
    return symbols.stream().allMatch(symbol -> nullables.contains(symbol) || //
        symbol.isNotation() && symbol.asNotation().isNullable(this::isNullable));
  }
  public Set<Verb> firsts(Symbol... symbols) {
    return firsts(Arrays.asList(symbols));
  }
  public Set<Verb> firsts(Collection<Symbol> symbols) {
    Set<Verb> $ = new LinkedHashSet<>();
    for (Symbol s : symbols) {
      $.addAll(firsts.get(s));
      if (!isNullable(s))
        break;
    }
    return unmodifiableSet($);
  }
  public BNF reachableSubBNF() {
    Set<DerivationRule> subR = new LinkedHashSet<>();
    Set<Verb> subΣ = new LinkedHashSet<>();
    Set<Variable> subV = new LinkedHashSet<>();
    Set<Variable> newSubV = new LinkedHashSet<>();
    newSubV.add(startVariable);
    int previousCount = -1;
    while (previousCount < subV.size()) {
      previousCount = subV.size();
      Set<Variable> newestSubV = new LinkedHashSet<>();
      for (DerivationRule rule : R) {
        if (!newSubV.contains(rule.lhs))
          continue;
        subR.add(rule);
        for (SententialForm sf : rule.rhs)
          for (Symbol symbol : sf)
            if (symbol.isVerb())
              subΣ.add(symbol.asVerb());
            else if (symbol.isVariable())
              newestSubV.add(symbol.asVariable());
            else
              throw new RuntimeException("problem while analyzing BNF");
      }
      subV.addAll(newSubV);
      newSubV = newestSubV;
    }
    return new BNF(subΣ, subV, subR, startVariable, null, null, null, true);
  }
  private Set<Symbol> getNullables() {
    Set<Symbol> $ = new LinkedHashSet<>();
    for (; $.addAll(V.stream() //
        .filter(v -> rhs(v).stream() //
            .anyMatch(sf -> sf.stream().allMatch(symbol -> isNullable(symbol, $)))) //
        .collect(toSet()));)
      ;
    return $;
  }
  private boolean isNullable(Symbol symbol, Set<Symbol> knownNullables) {
    if (symbol.isVerb())
      return false;
    if (symbol.isVariable())
      return knownNullables.contains(symbol);
    if (symbol.isNotation())
      return symbol.asNotation().isNullable(s -> isNullable(s, knownNullables));
    throw new RuntimeException("problem while analyzing BNF");
  }
  private Map<Symbol, Set<Verb>> getFirsts() {
    Map<Symbol, Set<Verb>> $ = new LinkedHashMap<>();
    Σ.forEach(σ -> $.put(σ, singleton(σ)));
    V.forEach(v -> $.put(v, new LinkedHashSet<>()));
    for (boolean changed = true; changed;) {
      changed = false;
      for (Variable v : V)
        for (SententialForm sf : rhs(v))
          for (Symbol symbol : sf) {
            changed |= $.get(v).addAll(!symbol.isNotation() ? $.get(symbol) : //
                symbol.asNotation().getFirsts($::get));
            if (!isNullable(symbol))
              break;
          }
    }
    V.forEach(v -> $.put(v, unmodifiableSet($.get(v))));
    return unmodifiableMap($);
  }
  private Map<Variable, Set<Verb>> getFollows() {
    Map<Variable, Set<Verb>> $ = new LinkedHashMap<>();
    V.forEach(v -> $.put(v, new LinkedHashSet<>()));
    $.get(Constants.S).add(Constants.$$);
    for (boolean changed = true; changed;) {
      changed = false;
      for (Variable v : V)
        for (SententialForm sf : rhs(v))
          for (int i = 0; i < sf.size(); ++i) {
            if (!sf.get(i).isVariable())
              continue;
            Variable current = sf.get(i).asVariable();
            List<Symbol> rest = sf.subList(i, sf.size());
            changed |= $.get(current).addAll(firsts(rest));
            if (isNullable(rest))
              changed |= $.get(v).addAll($.get(current));
          }
    }
    V.forEach(s -> $.put(s, unmodifiableSet($.get(s))));
    return unmodifiableMap($);
  }

  public static class Builder<V extends Enum<V> & Variable> {
    private final Set<Verb> Σ;
    private final Class<V> V;
    private final Set<DerivationRule> R;
    private V start;
    private final Set<Variable> heads;

    public Builder(Class<V> V) {
      this.Σ = new LinkedHashSet<>();
      this.V = V;
      this.R = new LinkedHashSet<>();
      for (V v : EnumSet.allOf(V))
        R.add(new DerivationRule(v, new ArrayList<>()));
      this.heads = new LinkedHashSet<>();
    }
    public Builder<V> derive(Variable lhs, Symbol... sententialForm) {
      SententialForm processedSententialForm = new SententialForm(Arrays.stream(sententialForm) //
          .map(symbol -> {
            return !symbol.isTerminal() ? symbol : new Verb(symbol.asTerminal());
          }) //
          .collect(Collectors.toList()));
      processedSententialForm.forEach(this::processSymbol);
      rhs(lhs).add(processedSententialForm);
      return this;
    }
    private void processSymbol(Symbol symbol) {
      assert !symbol.isTerminal();
      if (symbol.isVerb()) {
        Σ.add(symbol.asVerb());
        symbol.asVerb().parameters.stream() //
            .map(TypeParameter::declaredHeadVariables) //
            .forEach(heads::addAll);
      } else if (symbol.isNotation())
        symbol.asNotation().abbreviatedSymbols().forEach(this::processSymbol);
    }
    public final Builder<V> start(V startVariable) {
      start = startVariable;
      return this;
    }
    public BNF build() {
      assert start != null : "declare a start variable";
      return new BNF(Σ, EnumSet.allOf(V), R, start, heads, null, null, true);
    }
    private List<SententialForm> rhs(Variable v) {
      return R.stream().filter(r -> r.lhs.equals(v)).findFirst().map(DerivationRule::rhs).orElse(null);
    }
  }
}
