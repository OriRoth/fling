package fling;

import static java.util.Collections.*;
import static java.util.stream.Collectors.toSet;

import java.util.*;
import java.util.stream.Collectors;

import fling.grammars.api.BNFAPIAST.*;
import fling.internal.grammar.sententials.*;
import fling.internal.grammar.types.TypeParameter;

/**
 * Backus-Naur form. Language specification, collection of derivation rules of
 * the form <code>V ::= w X | Y z.</code> Computes grammar's nullables set and
 * firsts/follows mappings.
 * 
 * @author Ori Roth
 */
public class BNF {
  /**
   * Derivation rules collection.
   */
  public final Set<DerivationRule> R;
  /**
   * Set of nullable variables and notations.
   */
  public final Set<Symbol> nullables;
  /**
   * Maps variables and notations to their firsts set.
   */
  public final Map<Symbol, Set<Verb>> firsts;
  /**
   * Maps variables and notations to their follows set.
   */
  public final Map<Variable, Set<Verb>> follows;
  /**
   * Verbs collection.
   */
  public final Set<Verb> Σ;
  /**
   * Variables collection.
   */
  public final Set<Variable> V;
  /**
   * Start variable.
   */
  public final Variable startVariable;
  /**
   * Head variables set, containing variables used as API parameters.
   */
  public final Set<Variable> headVariables;
  /**
   * Maps generated variables to the notation originated them. Optional.
   */
  public final Map<Variable, Notation> extensionHeadsMapping;
  /**
   * Set of generated variables.
   */
  public final Set<Variable> extensionProducts;

  public BNF(final Set<Verb> Σ, final Set<? extends Variable> V, final Set<DerivationRule> R, final Variable startVariable,
      final Set<Variable> headVariables, final Map<Variable, Notation> extensionHeadsMapping, final Set<Variable> extensionProducts,
      final boolean addStartSymbolDerivationRules) {
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
    this.extensionHeadsMapping = extensionHeadsMapping == null ? Collections.emptyMap() : extensionHeadsMapping;
    this.extensionProducts = extensionProducts == null ? Collections.emptySet() : extensionProducts;
    this.nullables = getNullables();
    this.firsts = getFirsts();
    this.follows = getFollows();
  }
  /**
   * @return all grammar symbols.
   */
  public Set<Symbol> symbols() {
    final Set<Symbol> $ = new LinkedHashSet<>();
    $.addAll(Σ);
    $.addAll(V);
    return unmodifiableSet($);
  }
  /**
   * @param v a variable
   * @return the right hand side of its derivation rule
   */
  public List<SententialForm> rhs(final Variable v) {
    return R.stream().filter(r -> r.lhs.equals(v)).findFirst().map(DerivationRule::rhs).orElse(null);
  }
  /**
   * @param symbols sequence of grammar symbols
   * @return whether the sequence is nullable
   */
  public boolean isNullable(final Symbol... symbols) {
    return isNullable(Arrays.asList(symbols));
  }
  /**
   * @param symbols sequence of grammar symbols
   * @return whether the sequence is nullable
   */
  public boolean isNullable(final List<Symbol> symbols) {
    return symbols.stream().allMatch(symbol -> nullables.contains(symbol) || //
        symbol.isNotation() && symbol.asNotation().isNullable(this::isNullable));
  }
  public Set<Verb> firsts(final Symbol... symbols) {
    return firsts(Arrays.asList(symbols));
  }
  public Set<Verb> firsts(final Collection<Symbol> symbols) {
    final Set<Verb> $ = new LinkedHashSet<>();
    for (final Symbol s : symbols) {
      $.addAll(firsts.get(s));
      if (!isNullable(s))
        break;
    }
    return unmodifiableSet($);
  }
  public BNF reachableSubBNF() {
    final Set<DerivationRule> subR = new LinkedHashSet<>();
    final Set<Verb> subΣ = new LinkedHashSet<>();
    final Set<Variable> subV = new LinkedHashSet<>();
    Set<Variable> newSubV = new LinkedHashSet<>();
    newSubV.add(startVariable);
    int previousCount = -1;
    while (previousCount < subV.size()) {
      previousCount = subV.size();
      final Set<Variable> newestSubV = new LinkedHashSet<>();
      for (final DerivationRule rule : R) {
        if (!newSubV.contains(rule.lhs))
          continue;
        subR.add(rule);
        for (final SententialForm sf : rule.rhs)
          for (final Symbol symbol : sf)
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
  public boolean isOriginalVariable(final Symbol symbol) {
    return symbol.isVariable() && !extensionProducts.contains(symbol);
  }
  private Set<Symbol> getNullables() {
    final Set<Symbol> $ = new LinkedHashSet<>();
    for (; $.addAll(V.stream() //
        .filter(v -> rhs(v).stream() //
            .anyMatch(sf -> sf.stream().allMatch(symbol -> isNullable(symbol, $)))) //
        .collect(toSet()));)
      ;
    return $;
  }
  private boolean isNullable(final Symbol symbol, final Set<Symbol> knownNullables) {
    if (symbol.isVerb())
      return false;
    if (symbol.isVariable())
      return knownNullables.contains(symbol);
    if (symbol.isNotation())
      return symbol.asNotation().isNullable(s -> isNullable(s, knownNullables));
    throw new RuntimeException("problem while analyzing BNF");
  }
  private Map<Symbol, Set<Verb>> getFirsts() {
    final Map<Symbol, Set<Verb>> $ = new LinkedHashMap<>();
    Σ.forEach(σ -> $.put(σ, singleton(σ)));
    V.forEach(v -> $.put(v, new LinkedHashSet<>()));
    for (boolean changed = true; changed;) {
      changed = false;
      for (final Variable v : V)
        for (final SententialForm sf : rhs(v))
          for (final Symbol symbol : sf) {
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
    final Map<Variable, Set<Verb>> $ = new LinkedHashMap<>();
    V.forEach(v -> $.put(v, new LinkedHashSet<>()));
    $.get(Constants.S).add(Constants.$$);
    for (boolean changed = true; changed;) {
      changed = false;
      for (final Variable v : V)
        for (final SententialForm sf : rhs(v))
          for (int i = 0; i < sf.size(); ++i) {
            if (!sf.get(i).isVariable())
              continue;
            final Variable current = sf.get(i).asVariable();
            final List<Symbol> rest = sf.subList(i, sf.size());
            changed |= $.get(current).addAll(firsts(rest));
            if (isNullable(rest))
              changed |= $.get(v).addAll($.get(current));
          }
    }
    V.forEach(s -> $.put(s, unmodifiableSet($.get(s))));
    return unmodifiableMap($);
  }
  public static BNF toBNF(final PlainBNF specification) {
    final Builder $ = new Builder();
    $.start(specification.start);
    for (final Rule rule : specification.rule)
      if (rule instanceof Derivation) {
        // Derivation rule.
        final Derivation derivation = (Derivation) rule;
        Variable lhs = derivation.derive;
        if (derivation.ruleBody instanceof ConcreteDerivation) {
          // Concrete derivation rule.
          ConcreteDerivation concrete = (ConcreteDerivation) derivation.ruleBody;
          $.derive(lhs).to((concrete).to);
          for (RuleTail tail : concrete.ruleTail)
            if (tail instanceof ConcreteDerivationTail)
              // Concrete tail.
              $.derive(lhs).to(((ConcreteDerivationTail) tail).or);
            else
              // Epsilon tail.
              $.derive(lhs).toEpsilon();
        } else
          // Epsilon derivation rule.
          $.derive(lhs).toEpsilon();
      } else {
        // Specialization rule.
        final Specialization specializationRule = (Specialization) rule;
        $.specialize(specializationRule.specialize).into(specializationRule.into);
      }
    return $.build();
  }

  @Deprecated private static class Builder {
    private final Set<Verb> Σ;
    private final Set<Variable> V;
    private final Set<DerivationRule> R;
    private Variable start;
    private final Set<Variable> heads;

    public Builder() {
      this.Σ = new LinkedHashSet<>();
      this.V = new LinkedHashSet<>();
      this.R = new LinkedHashSet<>();
      this.heads = new LinkedHashSet<>();
    }
    public Derive derive(final Variable lhs) {
      processSymbol(lhs);
      return new Derive(lhs);
    }
    public Specialize specialize(final Variable lhs) {
      return new Specialize(lhs);
    }
    void processSymbol(final Symbol symbol) {
      assert !symbol.isTerminal();
      if (symbol.isVerb()) {
        Σ.add(symbol.asVerb());
        symbol.asVerb().parameters.stream() //
            .map(TypeParameter::declaredHeadVariables) //
            .forEach(heads::addAll);
      } else if (symbol.isNotation())
        symbol.asNotation().abbreviatedSymbols().forEach(this::processSymbol);
      else if (symbol.isVariable()) {
        final Variable variable = symbol.asVariable();
        if (!V.contains(variable)) {
          V.add(variable);
          R.add(new DerivationRule(variable, new ArrayList<>()));
        }
      }
    }
    public final Builder start(final Variable startVariable) {
      start = startVariable;
      return this;
    }
    public BNF build() {
      assert start != null : "declare a start variable";
      return new BNF(Σ, V, R, start, heads, null, null, true);
    }
    List<SententialForm> rhs(final Variable v) {
      return R.stream().filter(r -> r.lhs.equals(v)).findFirst().map(DerivationRule::rhs).orElse(null);
    }

    public class Derive {
      private final Variable lhs;

      public Derive(final Variable lhs) {
        this.lhs = lhs;
      }
      public Builder to(final Symbol... sententialForm) {
        final SententialForm processedSententialForm = new SententialForm(Arrays.stream(sententialForm) //
            .map(symbol -> {
              return !symbol.isTerminal() ? symbol : new Verb(symbol.asTerminal());
            }) //
            .collect(Collectors.toList()));
        processedSententialForm.forEach(Builder.this::processSymbol);
        rhs(lhs).add(processedSententialForm);
        return Builder.this;
      }
      public Builder toEpsilon() {
        final SententialForm processedSententialForm = new SententialForm();
        rhs(lhs).add(processedSententialForm);
        return Builder.this;
      }
    }

    public class Specialize {
      private final Variable lhs;

      public Specialize(final Variable lhs) {
        this.lhs = lhs;
      }
      public Builder into(final Variable... variables) {
        for (final Variable variable : variables) {
          final SententialForm processedSententialForm = new SententialForm(variable);
          processedSententialForm.forEach(Builder.this::processSymbol);
          rhs(lhs).add(processedSententialForm);
        }
        return Builder.this;
      }
    }
  }
}
