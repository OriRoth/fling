package fling.grammars.api;

import java.util.*;

@SuppressWarnings("all") public interface BNFCompiler {
  static BNFAST.Specification parse_Specification(final List<fling.internal.compiler.Assignment> w) {
    fling.internal.compiler.Assignment _a;
    List<?> _b;
    _a = w.remove(0);
    _a = w.remove(0);
    final fling.internal.grammar.sententials.Variable variable = (fling.internal.grammar.sententials.Variable) _a.arguments.get(0);
    _b = fling.internal.grammar.sententials.notations.NoneOrMore.abbreviate(parse__Rule2(w), 1);
    final List<BNFAST.Rule> rule = (List<BNFAST.Rule>) _b.get(0);
    return new BNFAST.Specification(variable, rule);
  }
  static BNFAST.Rule parse_Rule(final List<fling.internal.compiler.Assignment> w) {
    final fling.internal.compiler.Assignment _a = w.get(0);
    if (fling.internal.util.Collections.included(_a.σ, BNFAPI.Σ.derive))
      return parse_Rule1(w);
    if (fling.internal.util.Collections.included(_a.σ, BNFAPI.Σ.specialize))
      return parse_Rule2(w);
    return null;
  }
  static BNFAST.DerivationTarget parse_DerivationTarget(final List<fling.internal.compiler.Assignment> w) {
    final fling.internal.compiler.Assignment _a = w.get(0);
    if (fling.internal.util.Collections.included(_a.σ, BNFAPI.Σ.to))
      return parse_DerivationTarget1(w);
    if (fling.internal.util.Collections.included(_a.σ, BNFAPI.Σ.toEpsilon))
      return parse_DerivationTarget2(w);
    return null;
  }
  static BNFAST.DerivationRule parse_Rule1(final List<fling.internal.compiler.Assignment> w) {
    fling.internal.compiler.Assignment _a;
    _a = w.remove(0);
    final fling.internal.grammar.sententials.Variable variable = (fling.internal.grammar.sententials.Variable) _a.arguments.get(0);
    final BNFAST.DerivationTarget derivationTarget = parse_DerivationTarget(w);
    return new BNFAST.DerivationRule(variable, derivationTarget);
  }
  static BNFAST.SpecializationRule parse_Rule2(final List<fling.internal.compiler.Assignment> w) {
    fling.internal.compiler.Assignment _a;
    _a = w.remove(0);
    final fling.internal.grammar.sententials.Variable variable = (fling.internal.grammar.sententials.Variable) _a.arguments.get(0);
    _a = w.remove(0);
    final fling.internal.grammar.sententials.Variable[] variables = (fling.internal.grammar.sententials.Variable[]) _a.arguments
        .get(0);
    return new BNFAST.SpecializationRule(variable, variables);
  }
  static BNFAST.ConcreteDerivation parse_DerivationTarget1(final List<fling.internal.compiler.Assignment> w) {
    fling.internal.compiler.Assignment _a;
    _a = w.remove(0);
    final fling.internal.grammar.sententials.Symbol[] symbols = (fling.internal.grammar.sententials.Symbol[]) _a.arguments.get(0);
    return new BNFAST.ConcreteDerivation(symbols);
  }
  static BNFAST.EpsilonDerivation parse_DerivationTarget2(final List<fling.internal.compiler.Assignment> w) {
    w.remove(0);
    return new BNFAST.EpsilonDerivation();
  }
  static List<Object> parse__Rule2(final List<fling.internal.compiler.Assignment> w) {
    fling.internal.compiler.Assignment _a;
    if (w.isEmpty())
      return Collections.emptyList();
    _a = w.get(0);
    if (!fling.internal.util.Collections.included(_a.σ, BNFAPI.Σ.derive, BNFAPI.Σ.specialize))
      return Collections.emptyList();
    final BNFAST.Rule rule = parse_Rule(w);
    return Arrays.asList(rule, parse__Rule2(w));
  }
}
