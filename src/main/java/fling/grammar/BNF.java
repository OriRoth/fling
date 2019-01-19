package fling.grammar;

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fling.sententials.Constants;
import fling.sententials.DerivationRule;
import fling.sententials.SententialForm;
import fling.sententials.Symbol;
import fling.sententials.Terminal;
import fling.sententials.Variable;

public class BNF {
  public final Set<DerivationRule> R;
  public final Set<Symbol> nullables;
  public final Map<Symbol, Set<Terminal>> firsts;
  public final Map<Variable, Set<Terminal>> follows;
  public final Set<Terminal> Σ;
  public final Set<Variable> V;

  public <Σ extends Enum<Σ> & Terminal, V extends Enum<V> & Variable> //
  BNF(Class<Σ> Σ, Class<V> V, Set<DerivationRule> R, Set<Variable> startVariables) {
    this(new LinkedHashSet<>(EnumSet.allOf(Σ)), new LinkedHashSet<>(EnumSet.allOf(V)), R, startVariables);
  }
  public BNF(Set<Terminal> Σ, Set<Variable> V, Set<DerivationRule> R, Set<Variable> startVariables) {
    this.Σ = Σ;
    Σ.add(Constants.$);
    this.V = V;
    V.add(Constants.S);
    this.R = R;
    for (Variable v : startVariables)
      rhs(Constants.S).add(new SententialForm(v));
    this.nullables = getNullables();
    this.firsts = getFirsts();
    this.follows = getFollows();
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
    return isNullable(symbols);
  }
  public boolean isNullable(List<Symbol> symbols) {
    return symbols.stream().allMatch(nullables::contains);
  }
  public Set<Terminal> firsts(Symbol... symbols) {
    return firsts(Arrays.asList(symbols));
  }
  public Set<Terminal> firsts(Collection<Symbol> symbols) {
    Set<Terminal> $ = new LinkedHashSet<>();
    for (Symbol s : symbols) {
      $.addAll(firsts.get(s));
      if (!isNullable(s))
        break;
    }
    return unmodifiableSet($);
  }
  private Set<Symbol> getNullables() {
    return V.stream().filter(v -> rhs(v).stream().anyMatch(List::isEmpty)).collect(toSet());
  }
  private Map<Symbol, Set<Terminal>> getFirsts() {
    Map<Symbol, Set<Terminal>> $ = new LinkedHashMap<>();
    Σ.forEach(σ -> $.put(σ, singleton(σ)));
    V.forEach(v -> $.put(v, new LinkedHashSet<>()));
    for (boolean changed = true; changed;) {
      changed = false;
      for (Variable v : V)
        for (SententialForm sf : rhs(v))
          for (Symbol s : sf) {
            changed |= $.get(v).addAll($.get(s));
            if (!isNullable(s))
              break;
          }
    }
    V.forEach(v -> $.put(v, unmodifiableSet($.get(v))));
    return unmodifiableMap($);
  }
  private Map<Variable, Set<Terminal>> getFollows() {
    Map<Variable, Set<Terminal>> $ = new LinkedHashMap<>();
    V.forEach(v -> $.put(v, new LinkedHashSet<>()));
    $.get(Constants.S).add(Constants.$);
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

  public static class Builder<Σ extends Enum<Σ> & Terminal, V extends Enum<V> & Variable> {
    private final Class<Σ> Σ;
    private final Class<V> V;
    private final Set<DerivationRule> R;
    private final Set<Variable> starts;

    public Builder(Class<Σ> Σ, Class<V> V) {
      this.Σ = Σ;
      this.V = V;
      this.R = new LinkedHashSet<>();
      for (V v : EnumSet.allOf(V))
        R.add(new DerivationRule(v, new ArrayList<>()));
      this.starts = new LinkedHashSet<>();
    }
    public Builder<Σ, V> derive(Variable lhs, Symbol... sententialForm) {
      rhs(lhs).add(new SententialForm(sententialForm));
      return this;
    }
    @SafeVarargs public final Builder<Σ, V> start(V... startVariables) {
      Collections.addAll(starts, startVariables);
      return this;
    }
    public BNF build() {
      return new BNF(Σ, V, R, starts);
    }
    private List<SententialForm> rhs(Variable v) {
      return R.stream().filter(r -> r.lhs.equals(v)).findFirst().map(DerivationRule::rhs).orElse(null);
    }
  }
}
