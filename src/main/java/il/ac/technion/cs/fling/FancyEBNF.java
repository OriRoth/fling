package il.ac.technion.cs.fling;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import il.ac.technion.cs.fling.internal.grammar.rules.Body;
import il.ac.technion.cs.fling.internal.grammar.rules.Component;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.ERule;
import il.ac.technion.cs.fling.internal.grammar.rules.Quantifier;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
import il.ac.technion.cs.fling.internal.grammar.types.Parameter;
/** An extended Backus-Naur form specification of formal Language, collection of
 * derivation rules of the form <code>v ::= w X | Y z.</code>, augmented with
 * lots of services which found shelter in this class.
 *
 * @author Ori Roth */
public class FancyEBNF extends EBNF.Decorator {
  /** Set of nullable variables and notations */
  public final Set<Component> nullables;
  /** Maps variables and notations to their firsts set */
  public final Map<Component, Set<Token>> firsts;
  /** Maps variables and notations to their follows set */
  public final Map<Variable, Set<Token>> follows;
  /** Head variables set, containing variables used as API parameters */
  public final Set<Variable> headVariables;
  /** Maps generated variables to the notation originated them. Optional */
  public final Map<Variable, Quantifier> extensionHeadsMapping;
  /** Set of generated variables */
  public final Set<Variable> extensionProducts;
  public FancyEBNF(final EBNF ebnf, final Set<Variable> headVariables,
      final Map<Variable, Quantifier> extensionHeadsMapping, final Set<Variable> extensionProducts,
      final boolean addStartSymbolDerivationRules) {
    super(ebnf);
    if (addStartSymbolDerivationRules) {
      Γ.add(Constants.S);
      R.add(new ERule(Constants.S, new Body(ebnf.ε)));
    }
    this.headVariables = headVariables;
    this.extensionHeadsMapping = extensionHeadsMapping == null ? Collections.emptyMap() : extensionHeadsMapping;
    this.extensionProducts = extensionProducts == null ? Collections.emptySet() : extensionProducts;
    nullables = getNullables();
    firsts = getFirsts();
    follows = getFollows();
  }
  /** @param symbols sequence of grammar symbols
   * @return whether the sequence is nullable */
  public boolean isNullable(final Component... symbols) {
    return isNullable(Arrays.asList(symbols));
  }
  /** @param symbols sequence of grammar symbols
   * @return whether the sequence is nullable */
  public boolean isNullable(final List<Component> symbols) {
    return symbols.stream().allMatch(symbol -> nullables.contains(symbol) || //
        symbol.isQuantifier() && symbol.asQuantifier().isNullable(this::isNullable));
  }
  public Set<Token> firsts(final Component... symbols) {
    return firsts(Arrays.asList(symbols));
  }
  public <T extends Collection<? extends Component>> Set<Token> firsts(final T symbols) {
    final Set<Token> $ = new LinkedHashSet<>();
    for (final Component s : symbols) {
      $.addAll(!s.isQuantifier() ? firsts.get(s) : s.asQuantifier().getFirsts(this::firsts));
      if (!isNullable(s))
        break;
    }
    return unmodifiableSet($);
  }
  /** Return a possibly smaller BNF including only rules reachable form start
   * symbol */
  private static FancyEBNF reduce(final EBNF b) {
    final Set<Variable> Γ = new LinkedHashSet<>();
    final Set<ERule> R = new LinkedHashSet<>();
    final Set<Token> Σ = new LinkedHashSet<>();
    final Set<Variable> newVariables = new LinkedHashSet<>();
    newVariables.add(b.ε);
    while (!newVariables.isEmpty()) {
      Γ.addAll(newVariables);
      final Set<Variable> currentVariables = new LinkedHashSet<>();
      currentVariables.addAll(newVariables);
      newVariables.clear();
      for (final Variable v : currentVariables) {
        b.rules(v).forEachOrdered(R::add);
        b.rules(v).flatMap(ERule::tokens).forEachOrdered(Σ::add);
        b.rules(v).flatMap(ERule::variables) //
            .filter(_v -> !Γ.contains(_v)).forEach(newVariables::add);
      }
    }
    return new FancyEBNF(new EBNF(Σ, Γ, b.ε, R), null, null, null, true);
  }
  public boolean isOriginalVariable(final Component symbol) {
    return symbol.isVariable() && !extensionProducts.contains(symbol);
  }
  private Set<Component> getNullables() {
    final Set<Component> $ = new LinkedHashSet<>();
    while ($.addAll(Γ.stream() //
        .filter(v -> bodies(v).anyMatch(sf -> sf.stream().allMatch(symbol -> isNullable(symbol, $)))) //
        .collect(toSet())))
      ;
    return $;
  }
  private boolean isNullable(final Component symbol, final Set<Component> knownNullables) {
    if (symbol.isToken())
      return false;
    if (symbol.isVariable())
      return knownNullables.contains(symbol);
    if (symbol.isQuantifier())
      return symbol.asQuantifier().isNullable(s -> isNullable(s, knownNullables));
    throw new RuntimeException("problem while analyzing BNF");
  }
  private Map<Component, Set<Token>> getFirsts() {
    final Map<Component, Set<Token>> $ = new LinkedHashMap<>();
    Σ.forEach(σ -> $.put(σ, singleton(σ)));
    Γ.forEach(v -> $.put(v, new LinkedHashSet<>()));
    for (boolean changed = true; changed;) {
      changed = false;
      for (final Variable v : Γ)
        for (final Body sf : bodiesList(v))
          for (final Component symbol : sf) {
            final Set<Token> set = $.get(v);
            if (symbol.isQuantifier())
              changed |= set.addAll(symbol.asQuantifier().getFirsts(ss -> { //
                final Set<Token> firsts = new LinkedHashSet<>();
                for (final Component s : ss) {
                  firsts.addAll($.get(s));
                  if (!isNullable(symbol))
                    break;
                }
                return firsts;
              }));
            else {
              final Set<Token> c = $.get(symbol);
              assert c != null : this + ":\n" + symbol;
              changed |= set.addAll(c);
            }
            if (!isNullable(symbol))
              break;
          }
    }
    Γ.forEach(v -> $.put(v, unmodifiableSet($.get(v))));
    return unmodifiableMap($);
  }
  private Map<Variable, Set<Token>> getFollows() {
    final Map<Variable, Set<Token>> $ = new LinkedHashMap<>();
    Γ.forEach(v -> $.put(v, new LinkedHashSet<>()));
    $.get(Constants.S).add(Constants.$$);
    for (boolean changed = true; changed;) {
      changed = false;
      for (final Variable v : Γ)
        for (final Body sf : bodiesList(v))
          for (int i = 0; i < sf.size(); ++i) {
            if (!sf.get(i).isVariable())
              continue;
            final Variable current = sf.get(i).asVariable();
            final List<Component> rest = sf.subList(i + 1, sf.size());
            changed |= $.get(current).addAll(firsts(rest));
            if (isNullable(rest))
              changed |= $.get(current).addAll($.get(v));
          }
    }
    Γ.forEach(s -> $.put(s, unmodifiableSet($.get(s))));
    return unmodifiableMap($);
  }
  public static FancyEBNF from(final EBNF source) {
    final Set<Variable> heads = new LinkedHashSet<>();
    source.Σ.forEach(t -> t.parameters() //
        .map(Parameter::declaredHeadVariables).forEach(heads::addAll));
    return new FancyEBNF(source, heads, null, null, true);
  }
  public FancyEBNF reduce() {
    return reduce(this);
  }
}
