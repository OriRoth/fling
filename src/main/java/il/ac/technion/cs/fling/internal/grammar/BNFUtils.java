package il.ac.technion.cs.fling.internal.grammar;
import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import il.ac.technion.cs.fling.EBNF;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.grammar.rules.Body;
import il.ac.technion.cs.fling.internal.grammar.rules.Component;
import il.ac.technion.cs.fling.internal.grammar.rules.ERule;
import il.ac.technion.cs.fling.internal.grammar.rules.Quantifier;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
import il.ac.technion.cs.fling.namers.NaiveLinker;
public class BNFUtils {
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
  static FancyEBNF normalize(final FancyEBNF bnf, final Linker namer) {
    final Set<Variable> V = new LinkedHashSet<>(bnf.Γ);
    final Set<ERule> R = new LinkedHashSet<>();
    for (final Variable v : bnf.Γ) {
      final List<Body> rhs = bnf.bodiesList(v);
      assert rhs.size() > 0 : v + " in: " + bnf;
      if (rhs.size() == 1) {
        // Sequence (or redundant alteration).
        R.add(new ERule(v, rhs));
        continue;
      }
      final List<Variable> alteration = new ArrayList<>();
      for (final Body sf : rhs)
        if (sf.size() == 1 && sf.stream().allMatch(bnf::isOriginalVariable))
          // Ready alteration variable.
          alteration.add(sf.get(0).asVariable());
        else {
          // Create a suitable child variable.
          final Variable a = namer.fresh(v);
          V.add(a);
          R.add(new ERule(a, Collections.singletonList(sf)));
          alteration.add(a);
        }
      R.add(new ERule(v, alteration.stream().map(Body::new).collect(toList())));
    }
    return new FancyEBNF(new EBNF(bnf.Σ, V, bnf.ε, R), bnf.headVariables, bnf.extensionHeadsMapping,
        bnf.extensionProducts, false);
  }
  static FancyEBNF normalize(final FancyEBNF bnf) {
    Linker namer = new NaiveLinker(null);
    final Set<Variable> V = new LinkedHashSet<>(bnf.Γ);
    final Set<ERule> R = new LinkedHashSet<>();
    for (final Variable v : bnf.Γ) {
      final List<Body> rhs = bnf.bodiesList(v);
      assert rhs.size() > 0 : v + " in: " + bnf;
      if (rhs.size() == 1) {
        // Sequence (or redundant alteration).
        R.add(new ERule(v, rhs));
        continue;
      }
      final List<Variable> alteration = new ArrayList<>();
      for (final Body sf : rhs)
        if (sf.size() == 1 && sf.stream().allMatch(bnf::isOriginalVariable))
          // Ready alteration variable.
          alteration.add(sf.get(0).asVariable());
        else {
          // Create a suitable child variable.
          final Variable a = namer.fresh(v);
          V.add(a);
          R.add(new ERule(a, Collections.singletonList(sf)));
          alteration.add(a);
        }
      R.add(new ERule(v, alteration.stream().map(Body::new).collect(toList())));
    }
    return new FancyEBNF(new EBNF(bnf.Σ, V, bnf.ε, R), bnf.headVariables, bnf.extensionHeadsMapping,
        bnf.extensionProducts, false);
  }
  static FancyEBNF getBNF(final FancyEBNF ebnf) {
    Linker namer = new NaiveLinker(null);
    final Set<Variable> Γ = new LinkedHashSet<>(ebnf.Γ);
    final Set<ERule> R = new LinkedHashSet<>();
    final Map<Variable, Quantifier> extensionHeadsMapping = new LinkedHashMap<>();
    final Set<Variable> extensionProducts = new LinkedHashSet<>();
    for (final ERule r : ebnf.R) {
      final List<Body> rhs = new ArrayList<>();
      for (final Body b : r.bodiesList()) {
        final List<Component> cs = new ArrayList<>();
        for (final Component c : b) {
          if (!c.isQuantifier()) {
            cs.add(c);
            continue;
          }
          final Quantifier q = c.asQuantifier();
          final Variable head = q.expand(namer, extensionProducts::add, R::add);
          extensionHeadsMapping.put(head, q);
          cs.add(head);
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
