package il.ac.technion.cs.fling;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import il.ac.technion.cs.fling.internal.grammar.rules.Body;
import il.ac.technion.cs.fling.internal.grammar.rules.Component;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.ERule;
import il.ac.technion.cs.fling.internal.grammar.rules.Quantifier;
import il.ac.technion.cs.fling.internal.grammar.rules.Symbol;
import il.ac.technion.cs.fling.internal.grammar.rules.TempComponent;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;

/** An extended Backus-Naur form specification of a formal Language, represented
 * as a set of {@link #R} of extended derivation rules {@link ERule}.
 * 
 * @author Yossi Gil */
public class EBNF {
  @Override public String toString() {
    return "<Σ=" + Σ + ", Γ=" + Γ + ", ε=" + ε + ", R=" + R + ">";
  }

  /** Tokens' vocabulary */
  public final Set<Token> Σ;
  /** Variables' vocabulary */
  public final Set<Variable> Γ;
  /** Start variable */
  public final Variable ε;
  /** Derivation rules collection */
  public final Set<ERule> R;

  public EBNF(final Set<Token> Σ, final Set<Variable> Γ, final Variable ε, final Set<ERule> R) {
    this.Σ = Σ;
    this.Γ = Γ;
    this.ε = ε;
    this.R = R;
    Σ.add(Constants.$$);
    verify();
  }

  public abstract static class Decorator extends EBNF {
    public Decorator(final EBNF ebnf) {
      super(ebnf.Σ, ebnf.Γ, ebnf.ε, ebnf.R);
    }
  }

  void verify() {
    assert R.size() > 0;
    assert Γ.contains(ε);
  }

  /** @return all rules in this instance */
  public Stream<ERule> rules() {
    return R.stream();
  }

  /** @return all rules defining a variable */
  public Stream<ERule> rules(final Variable v) {
    return R.stream().filter(r -> r.of(v));
  }

  /** @return stream of all grammar symbols */
  public Stream<Symbol> symbols() {
    return Stream.concat(Σ.stream(), Γ.stream());
  }

  /** @param v a variable
   * @return stream of right hand sides of all its derivation rule */
  public final Stream<Body> bodies(final Variable v) {
    return rules(v).flatMap(ERule::bodies);
  }

  /** @param v a variable
   * @return a list of the right hand side of all its derivation rule */
  public final List<Body> bodiesList(final Variable v) {
    return rules(v).flatMap(ERule::bodies).collect(Collectors.toList());
  }

  static class Builder {
    private final Set<Token> Σ = new LinkedHashSet<>();
    private final Set<Variable> V = new LinkedHashSet<>();
    private final Set<ERule> R = new LinkedHashSet<>();
    private Variable start;

    public Derive derive(final Variable variable) {
      add(variable);
      return new Derive(variable);
    }

    public Specialize specialize(final Variable variable) {
      add(variable);
      return new Specialize(variable);
    }

    public final Builder start(final Variable v) {
      add(v);
      start = v;
      return this;
    }

    Quantifier add(final Quantifier q) {
      q.symbols().forEach(this::add);
      return q;
    }

    Variable add(final Variable v) {
      V.add(v);
      return v;
    }

    Token add(final Token t) {
      Σ.add(t);
      return t;
    }

    Component add(final Component s) {
      assert !s.isTerminal();
      if (s instanceof Token)
        return add((Token) s);
      if (s instanceof Quantifier)
        return add((Quantifier) s);
      if (s instanceof Variable)
        return add((Variable) s);
      assert false : s + ":" + this;
      return s;
    }

    public EBNF build() {
      assert start != null : "declare a start variable";
      return new EBNF(Σ, V, start, R);
    }

    public class Derive {
      private final Variable variable;
      private Body form;

      public Derive(final Variable variable) {
        this.variable = variable;
      }

      public Builder to(final TempComponent... cs) {
        final List<Component> normalize = normalize(cs);
        return to(normalize);
      }

      private Builder to(final List<Component> cs) {
        for (final Component c : cs)
          add(c);
        R.add(new ERule(variable, new Body(cs)));
        return Builder.this;
      }

      private List<Component> normalize(final TempComponent... cs) {
        final List<Component> $ = new ArrayList<>();
        for (final TempComponent c : cs)
          $.add(c.normalize());
        return $;
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
        final List<Body> forms = new ArrayList<>();
        for (final Variable v : vs) {
          forms.add(new Body(v));
          V.add(v);
        }
        R.add(new ERule(variable, forms));
        return Builder.this;
      }
    }
  }
}
