package il.ac.technion.cs.fling;

import static il.ac.technion.cs.fling.internal.grammar.sententials.Constants.intermediateVariableName;
import static java.util.Collections.*;
import static java.util.stream.Collectors.toSet;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.antlr.runtime.tree.Tree;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.*;

import il.ac.technion.cs.fling.grammars.api.BNFAPIAST.*;
import il.ac.technion.cs.fling.internal.grammar.sententials.*;
import il.ac.technion.cs.fling.internal.grammar.types.Parameter;
import il.ac.technion.cs.fling.internal.util.Counter;

/** An extended Backus-Naur form specification of formal Language, collection of
 * derivation rules of the form <code>V ::= w X | Y z.</code>, augmented with
 * lots of services which found shelter in this class.
 * 
 * @author Ori Roth */
public class FancyEBNF extends EBNF {
  /** Set of nullable variables and notations */
  public final Set<Symbol> nullables;
  /** Maps variables and notations to their firsts set */
  public final Map<Symbol, Set<Token>> firsts;
  /** Maps variables and notations to their follows set */
  public final Map<Variable, Set<Token>> follows;
  /** Head variables set, containing variables used as API parameters */
  public final Set<Variable> headVariables;
  /** Maps generated variables to the notation originated them. Optional */
  public final Map<Variable, Quantifier> extensionHeadsMapping;
  /** Set of generated variables */
  public final Set<Variable> extensionProducts;

  public FancyEBNF(final Set<Token> Σ, final Set<Variable> Γ, final Set<ERule> R, final Variable startVariable,
      final Set<Variable> headVariables, final Map<Variable, Quantifier> extensionHeadsMapping,
      final Set<Variable> extensionProducts, final boolean addStartSymbolDerivationRules) {
    super(Σ, Γ, startVariable, R);
    if (addStartSymbolDerivationRules) {
      this.Γ.add(Constants.S);
      R.add(new ERule(Constants.S, new ExtendedSententialForm(startVariable)));
    }
    this.headVariables = headVariables;
    this.extensionHeadsMapping = extensionHeadsMapping == null ? Collections.emptyMap() : extensionHeadsMapping;
    this.extensionProducts = extensionProducts == null ? Collections.emptySet() : extensionProducts;
    this.nullables = getNullables();
    this.firsts = getFirsts();
    this.follows = getFollows();
  }

  /** @param symbols sequence of grammar symbols
   * @return whether the sequence is nullable */
  public boolean isNullable(final Symbol... symbols) {
    return isNullable(Arrays.asList(symbols));
  }

  /** @param symbols sequence of grammar symbols
   * @return whether the sequence is nullable */
  public boolean isNullable(final List<Symbol> symbols) {
    return symbols.stream().allMatch(symbol -> nullables.contains(symbol) || //
        symbol.isQuantifier() && symbol.asQuantifier().isNullable(this::isNullable));
  }

  public Set<Token> firsts(final Symbol... symbols) {
    return firsts(Arrays.asList(symbols));
  }

  public Set<Token> firsts(final Collection<Symbol> symbols) {
    final Set<Token> $ = new LinkedHashSet<>();
    for (final Symbol s : symbols) {
      $.addAll(firsts.get(s));
      if (!isNullable(s))
        break;
    }
    return unmodifiableSet($);
  }

  /** Return a possibly smaller BNF including only rules reachable form start
   * symbol */
  private static FancyEBNF reduce(EBNF b) {
    final Set<Variable> Γ = new LinkedHashSet<>(Collections.singleton(b.ε));
    final Set<ERule> R = new LinkedHashSet<>();
    final Set<Token> Σ = new LinkedHashSet<>();

    for (;;)
      for (Variable v : Γ) {
        b.rules(v).forEachOrdered(R::add);
        b.rules(v).flatMap(ERule::tokens).forEachOrdered(Σ::add);
        Set<Variable> vs = b.rules(v).flatMap(ERule::variables).collect(Collectors.toSet());
        if (Γ.containsAll(vs))
          return new FancyEBNF(Σ, Γ, R, b.ε, null, null, null, true);
        Γ.addAll(vs);
      }
  }

  public boolean isOriginalVariable(final Symbol symbol) {
    return symbol.isVariable() && !extensionProducts.contains(symbol);
  }

  private Set<Symbol> getNullables() {
    final Set<Symbol> $ = new LinkedHashSet<>();
    while ($.addAll(Γ.stream() //
        .filter(v -> forms(v).anyMatch(sf -> sf.stream().allMatch(symbol -> isNullable(symbol, $)))) //
        .collect(toSet())))
      ;
    return $;
  }

  private boolean isNullable(final Symbol symbol, final Set<Symbol> knownNullables) {
    if (symbol.isToken())
      return false;
    if (symbol.isVariable())
      return knownNullables.contains(symbol);
    if (symbol.isQuantifier())
      return symbol.asQuantifier().isNullable(s -> isNullable(s, knownNullables));
    throw new RuntimeException("problem while analyzing BNF");
  }

  private Map<Symbol, Set<Token>> getFirsts() {
    final Map<Symbol, Set<Token>> $ = new LinkedHashMap<>();
    Σ.forEach(σ -> $.put(σ, singleton(σ)));
    Γ.forEach(v -> $.put(v, new LinkedHashSet<>()));
    for (boolean changed = true; changed;) {
      changed = false;
      for (final Variable v : Γ)
        for (final ExtendedSententialForm sf : formsList(v))
          for (final Symbol symbol : sf) {
            changed |= $.get(v).addAll(!symbol.isQuantifier() ? $.get(symbol) : //
                symbol.asQuantifier().getFirsts($::get));
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
        for (final ExtendedSententialForm sf : formsList(v))
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
    Γ.forEach(s -> $.put(s, unmodifiableSet($.get(s))));
    return unmodifiableMap($);
  }

  public static FancyEBNF toBNF(final PlainBNF specification) {
    final Builder $ = new Builder();
    $.start(specification.start);
    for (final Rule rule : specification.rule)
      if (rule instanceof Derivation) {
        // Derivation rule.
        final Derivation derivation = (Derivation) rule;
        Variable variable = derivation.derive;
        if (derivation.ruleBody instanceof ConcreteDerivation) {
          // Concrete derivation rule.
          ConcreteDerivation concrete = (ConcreteDerivation) derivation.ruleBody;
          $.derive(variable).to((concrete).to);
          for (RuleTail tail : concrete.ruleTail)
            if (tail instanceof ConcreteDerivationTail)
              // Concrete tail.
              $.derive(variable).to(((ConcreteDerivationTail) tail).or);
            else
              // Epsilon tail.
              $.derive(variable).toEpsilon();
        } else
          // Epsilon derivation rule.
          $.derive(variable).toEpsilon();
      } else {
        // Specialization rule.
        final Specialization specializationRule = (Specialization) rule;
        $.specialize(specializationRule.specialize).into(specializationRule.into);
      }
    try {
      return $.build();
    } catch (Exception e) {
      throw new RuntimeException(
          "problem while analyzing BNF, make sure the grammar adheres its class description (LL/LR/etc)", e);
    }
  }

  private static class Builder {
    private final Set<Token> Σ = new LinkedHashSet<>();
    private final Set<Variable> V = new LinkedHashSet<>();
    private final Set<ERule> R = new LinkedHashSet<>();
    private final Set<Variable> heads = new LinkedHashSet<>();
    private Variable start;

    private Stream<ExtendedSententialForm> rhs(final Variable v) {
      return R.stream().filter(r -> r.of(v)).flatMap(ERule::forms);
    }

    public Derive derive(final Variable variable) {
      V.add(variable);
      return new Derive(variable);
    }

    public Specialize specialize(final Variable variable) {
      V.add(variable);
      return new Specialize(variable);
    }

    void processSymbol(final Symbol symbol) {
      assert !symbol.isTerminal();
      if (symbol.isToken()) {
        Σ.add(symbol.asToken());
        symbol.asToken().parameters() //
            .map(Parameter::declaredHeadVariables) //
            .forEach(heads::addAll);
      } else if (symbol.isQuantifier())
        symbol.asQuantifier().abbreviatedSymbols().forEach(this::processSymbol);
      else if (symbol.isVariable()) {
        final Variable variable = symbol.asVariable();
        if (!V.contains(variable)) {
          V.add(variable);
          R.add(new ERule(variable, new ArrayList<>()));
        }
      }
    }

    public final Builder start(final Variable v) {
      V.add(v);
      start = v;
      return this;
    }

    Symbol add(Symbol s) {
      return s;
    }

    public FancyEBNF build() {
      assert start != null : "declare a start variable";
      return new FancyEBNF(Σ, V, R, start, heads, null, null, true);
    }

    public class Derive {
      private final Variable variable;
      private ExtendedSententialForm form;

      public Derive(final Variable variable) {
        this.variable = variable;
      }

      public Builder to(final Symbol... ss) {
        for (int i = 0; i < ss.length; ++i)
          ss[i] = add(ss[i].normalize());
        R.add(new ERule(variable, new ExtendedSententialForm(ss)));
        return Builder.this;
      }

      public Builder toEpsilon() {
        R.add(new ERule(variable));
        return Builder.this;
      }
    }

    public class Specialize {
      private final Variable variable;

      public Specialize(final Variable variable) {
        this.variable = variable;
      }

      public Builder into(final Variable... vs) {
        List<ExtendedSententialForm> forms = new ArrayList<>();
        for (final Variable v : vs) {
          forms.add(new ExtendedSententialForm(v));
          V.add(v);
        }
        R.add(new ERule(variable, forms));
        return Builder.this;
      }
    }

  }

  public static FancyEBNF fromANTLR(Grammar grammar) {
    assert grammar.ast.getChildCount() == 2 : "ANTLR grammar is not simplified";
    Builder $ = new Builder();
    boolean initialized = false;
    Tree rules = grammar.ast.getChild(1);
    Counter counter = new Counter();
    for (int i = 0; i < rules.getChildCount(); ++i) {
      Tree rule = rules.getChild(i);
      assert rule.getChildCount() == 2;
      String variableName = rule.getChild(0).getText();
      Variable variable = Variable.byName(variableName);
      if (!initialized) {
        // Assume first ANTLR variable is start variable.
        $.start(variable);
        initialized = true;
      }
      Optional<Symbol> rhs = extractANTLRSentential($, rule.getChild(1), counter);
      if (rhs.isPresent())
        $.derive(variable).to(rhs.get());
      else
        $.derive(variable).toEpsilon();
    }
    return $.build();
  }

  private static Optional<Symbol> extractANTLRSentential(Builder $, Object element, Counter nameCounter) {
    if (element instanceof List) {
      List<?> elements = (List<?>) element;
      if (elements.isEmpty())
        return Optional.empty();
      if (elements.size() == 1)
        return extractANTLRSentential($, elements.get(0), nameCounter);
      Variable top = Variable.byName(intermediateVariableName + nameCounter.getAndInc());
      List<Symbol> items = new ArrayList<>();
      for (Object item : elements)
        extractANTLRSentential($, item, nameCounter).ifPresent(items::add);
      $.derive(top).to(items.toArray(new Symbol[items.size()]));
      return Optional.of(top);
    }
    if (element instanceof AltAST)
      return extractANTLRSentential($, ((AltAST) element).getChildren(), nameCounter);
    if (element instanceof BlockAST) {
      BlockAST block = (BlockAST) element;
      if (block.getChildCount() <= 1)
        return extractANTLRSentential($, block.getChildren(), nameCounter);
      Variable top = Variable.byName(intermediateVariableName + nameCounter.getAndInc());
      List<Symbol> items = new ArrayList<>();
      for (Object item : block.getChildren())
        extractANTLRSentential($, item, nameCounter).ifPresent(items::add);
      items.forEach(symbol -> $.derive(top).to(symbol));
      return Optional.of(top);
    }
    if (element instanceof RuleRefAST)
      return Optional.of(Variable.byName(element.toString()));
    if (element instanceof StarBlockAST) {
      Optional<Symbol> inner = extractANTLRSentential($, ((StarBlockAST) element).getChildren(), nameCounter);
      return inner.map(Symbol::noneOrMore);
    }
    if (element instanceof PlusBlockAST) {
      Optional<Symbol> inner = extractANTLRSentential($, ((PlusBlockAST) element).getChildren(), nameCounter);
      return inner.map(Symbol::oneOrMore);
    }
    if (element instanceof TerminalAST) {
      String name = ((TerminalAST) element).getText();
      name = name.substring(1, name.length() - 1);
      // Assume simple terminal.
      return Optional.of(Terminal.byName(name));
    }
    throw new RuntimeException(String.format("Grammar element %s no supported", element.getClass().getSimpleName()));
  }

  public FancyEBNF reduce() {
    return reduce(this);
  }
}
