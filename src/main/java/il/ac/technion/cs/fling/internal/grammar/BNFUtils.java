package il.ac.technion.cs.fling.internal.grammar;
import java.util.*;
import static java.util.stream.Collectors.toList;
import il.ac.technion.cs.fling.EBNF;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
import il.ac.technion.cs.fling.namers.VariableGenerator;
public enum BNFUtils {
  ;

  static FancyEBNF reduce(final FancyEBNF bnf, final Variable v) {
    final Set<Token> Σ = new LinkedHashSet<>();
    final Set<Variable> V = new LinkedHashSet<>();
    V.add(v);
    final Set<ERule> rs = new LinkedHashSet<>();
    for (boolean more = true; more;) {
      more = false;
      for (final ERule r : bnf.R)
        if (!rs.contains(r) && V.contains(r.variable)) {
          more = true;
          rs.add(r);
          r.variables().forEachOrdered(V::add);
          r.tokens().forEachOrdered(Σ::add);
        }
    }
    return new FancyEBNF(new EBNF(Σ, V, v, rs), null, null, null, true);
  }
  /** Return a possibly smaller BNF including only rules reachable form start
   * symbol */
  public static FancyEBNF reduce(final EBNF b) {
    final Set<Variable> Γ = new LinkedHashSet<>();
    final Set<ERule> R = new LinkedHashSet<>();
    final Set<Token> Σ = new LinkedHashSet<>();
    final Set<Variable> newVariables = new LinkedHashSet<>();
    newVariables.add(b.ε);
    while (!newVariables.isEmpty()) {
      Γ.addAll(newVariables);
      final Iterable<Variable> currentVariables = new LinkedHashSet<>(newVariables);
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
  static FancyEBNF normalize(final FancyEBNF bnf) {
    final VariableGenerator g = new VariableGenerator();
    final Set<Variable> V = new LinkedHashSet<>(bnf.Γ);
    final Set<ERule> R = new LinkedHashSet<>();
    for (final Variable v : bnf.Γ) {
      final List<Body> rhs = bnf.bodiesList(v);
      assert !rhs.isEmpty() : v + " in: " + bnf;
      if (rhs.size() == 1) {
        // Sequence (or redundant alteration).
        R.add(new ERule(v, rhs));
        continue;
      }
      final Collection<Variable> alteration = new ArrayList<>();
      for (final Body sf : rhs)
        if (sf.size() == 1 && sf.stream().allMatch(bnf::isOriginalVariable))
          // Ready alteration variable.
          alteration.add(sf.get(0).asVariable());
        else {
          // Create a suitable child variable.
          final Variable a = g.fresh(v);
          V.add(a);
          R.add(new ERule(a, Collections.singletonList(sf)));
          alteration.add(a);
        }
      R.add(new ERule(v, alteration.stream().map(Body::new).collect(toList())));
    }
    return new FancyEBNF(new EBNF(bnf.Σ, V, bnf.ε, R), bnf.headVariables, bnf.extensionHeadsMapping,
        bnf.extensionProducts, false);
  }
  static FancyEBNF expandQuantifiers(final FancyEBNF ebnf) {
    final VariableGenerator g = new VariableGenerator();
    final Set<Variable> Γ = new LinkedHashSet<>(ebnf.Γ);
    final Set<ERule> R = new LinkedHashSet<>();
    final Map<Variable, Quantifier> extensionHeadsMapping = new LinkedHashMap<>();
    final Set<Variable> extensionProducts = new LinkedHashSet<>();
    for (final ERule r : ebnf.R) {
      final List<Body> rhs = new ArrayList<>();
      for (final Body b : r.bodiesList()) {
        final List<Component> cs = new ArrayList<>();
        for (final Component c : b) {
          if (c instanceof Quantifier q) {
            final Variable v = q.expand(g, extensionProducts::add, R::add);
            extensionHeadsMapping.put(v, q);
            cs.add(v);
          } else
            cs.add(c);
        }
        rhs.add(new Body(cs));
      }
      R.add(new ERule(r.variable, rhs));
    }
    Γ.addAll(extensionProducts);
    return new FancyEBNF(new EBNF(ebnf.Σ, Γ, ebnf.ε, R), ebnf.headVariables, extensionHeadsMapping, extensionProducts,
        false);
  }
}
