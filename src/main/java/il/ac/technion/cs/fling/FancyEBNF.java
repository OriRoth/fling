package il.ac.technion.cs.fling;
import java.util.*;
import static java.util.stream.Collectors.toSet;
import static java.util.Collections.*;
import il.ac.technion.cs.fling.internal.grammar.BNFUtils;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
import il.ac.technion.cs.fling.internal.grammar.types.Parameter;
/** An extended Backus-Naur form specification of formal Language, collection of
 * derivation rules of the form {@code v ::= w X | Y z.}, augmented with lots of
 * services which found shelter in this class.
 *
 * @author Ori Roth */
public class FancyEBNF extends EBNF.Decorator {
  /** Set of nullable variables and notations */
  private final Set<Component> nullables;
  /** Maps variables and notations to their firsts set */
  private final Map<Component, Set<Token>> firsts;
  /** Maps variables and notations to their follows set */
  private final Map<Variable, Set<Token>> follows;
  /** Head variables set, containing variables used as API parameters */
  public final Set<Variable> headVariables;
  /** Maps generated variables to the notation originated them. Optional */
  public final Map<Variable, Quantifier> extensionHeadsMapping;
  /** Set of generated variables */
  public final Set<Variable> extensionProducts;
  /*
   * It's almost always a mistake to add a boolean parameter to a public method
   * (part of an API) if that method is not a setter. When reading code using such
   * a method, it can be difficult to decipher what the boolean stands for without
   * looking at the source or documentation. This problem is also known as the
   * boolean trap. The boolean parameter can often be profitably replaced with an
   * enum
   */
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
  private boolean isNullable(final Collection<? extends Component> symbols) {
    return symbols.stream().allMatch(symbol -> nullables.contains(symbol) || //
        symbol.isQuantifier() && symbol.asQuantifier().isNullable(this::isNullable));
  }
  public Set<Token> firsts(final Component... symbols) {
    return firsts(Arrays.asList(symbols));
  }
  public Set<Token> firsts(final Iterable<? extends Component> symbols) {
    final Set<Token> $ = new LinkedHashSet<>();
    for (final Component s : symbols) {
      $.addAll(firsts.get(s));
      if (!isNullable(s))
        break;
    }
    return unmodifiableSet($);
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
    for (var changed = true; changed;) {
      changed = false;
      for (final Variable v : Γ)
        for (final Body sf : bodiesList(v))
          for (final Component symbol : sf) {
            final var set = $.get(v);
            if (symbol.isQuantifier())
              changed |= set.addAll(symbol.asQuantifier().getFirsts(ss -> { //
                final Set<Token> firsts = new LinkedHashSet<>();
                for (final Component s : ss) {
                  firsts.addAll($.get(s));
                  if (!symbol.asQuantifier().isNullable(this::isNullable))
                    break;
                }
                return firsts;
              }));
            else {
              final var c = $.get(symbol);
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
    for (var changed = true; changed;) {
      changed = false;
      for (final Variable v : Γ)
        for (final Body sf : bodiesList(v))
          for (var i = 0; i < sf.size(); ++i) {
            if (!sf.get(i).isVariable())
              continue;
            final var current = sf.get(i).asVariable();
            final List<Component> rest = sf.subList(i, sf.size());
            changed |= $.get(current).addAll(firsts(rest));
            if (isNullable(rest))
              changed |= $.get(v).addAll($.get(current));
          }
    }
    Γ.forEach(s -> $.put(s, unmodifiableSet($.get(s))));
    return unmodifiableMap($);
  }
  public static FancyEBNF from(final EBNF b) {
    final Set<Variable> heads = new LinkedHashSet<>();
    b.Σ.forEach(t -> t.parameters() //
        .map(Parameter::declaredHeadVariables).forEach(heads::addAll));
    return new FancyEBNF(b, heads, null, null, true);
  }
  /** @return clone, eliminating variables never used */
  public FancyEBNF clean() {
    return BNFUtils.reduce(this);
  }
}
