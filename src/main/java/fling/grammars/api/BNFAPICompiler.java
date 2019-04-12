package fling.grammars.api;

import static fling.grammars.api.BNFAPI.Σ.*;

import fling.Variable;
import fling.grammars.api.BNFAPIAST.*;

@SuppressWarnings("all") public interface BNFAPICompiler {
  public static PlainBNF parse_PlainBNF(java.util.List<fling.internal.compiler.Assignment> w) {
    fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    _a = w.remove(0);
    Variable variable = (Variable) _a.arguments.get(0);
    _b = fling.internal.grammar.sententials.notations.NoneOrMore.abbreviate(parse__Rule2(w), 1);
    java.util.List<Rule> rule = (java.util.List<Rule>) _b.get(0);
    return new PlainBNF(variable, rule);
  }
  public static Rule parse_Rule(java.util.List<fling.internal.compiler.Assignment> w) {
    fling.internal.compiler.Assignment _a = w.get(0);
    if (fling.internal.util.Collections.included(_a.σ, derive))
      return parse_Rule1(w);
    if (fling.internal.util.Collections.included(_a.σ, specialize))
      return parse_Rule2(w);
    return null;
  }
  public static RuleBody parse_RuleBody(java.util.List<fling.internal.compiler.Assignment> w) {
    fling.internal.compiler.Assignment _a = w.get(0);
    if (fling.internal.util.Collections.included(_a.σ, to))
      return parse_RuleBody1(w);
    if (fling.internal.util.Collections.included(_a.σ, toEpsilon))
      return parse_RuleBody2(w);
    return null;
  }
  public static RuleTail parse_RuleTail(java.util.List<fling.internal.compiler.Assignment> w) {
    fling.internal.compiler.Assignment _a = w.get(0);
    if (fling.internal.util.Collections.included(_a.σ, or))
      return parse_RuleTail1(w);
    if (fling.internal.util.Collections.included(_a.σ, orNone))
      return parse_RuleTail2(w);
    return null;
  }
  public static Derivation parse_Rule1(java.util.List<fling.internal.compiler.Assignment> w) {
    fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    Variable variable = (Variable) _a.arguments.get(0);
    RuleBody ruleBody = parse_RuleBody(w);
    return new Derivation(variable, ruleBody);
  }
  public static Specialization parse_Rule2(java.util.List<fling.internal.compiler.Assignment> w) {
    fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    Variable variable = (Variable) _a.arguments.get(0);
    _a = w.remove(0);
    Variable[] variables = (Variable[]) _a.arguments.get(0);
    return new Specialization(variable, variables);
  }
  public static ConcreteDerivation parse_RuleBody1(java.util.List<fling.internal.compiler.Assignment> w) {
    fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    fling.internal.grammar.sententials.Symbol[] symbols = (fling.internal.grammar.sententials.Symbol[]) _a.arguments.get(0);
    _b = fling.internal.grammar.sententials.notations.NoneOrMore.abbreviate(parse__RuleTail2(w), 1);
    java.util.List<RuleTail> ruleTail = (java.util.List<RuleTail>) _b.get(0);
    return new ConcreteDerivation(symbols, ruleTail);
  }
  public static EpsilonDerivation parse_RuleBody2(java.util.List<fling.internal.compiler.Assignment> w) {
    fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    return new EpsilonDerivation();
  }
  public static ConcreteDerivationTail parse_RuleTail1(java.util.List<fling.internal.compiler.Assignment> w) {
    fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    fling.internal.grammar.sententials.Symbol[] symbols = (fling.internal.grammar.sententials.Symbol[]) _a.arguments.get(0);
    return new ConcreteDerivationTail(symbols);
  }
  public static EpsilonDerivationTail parse_RuleTail2(java.util.List<fling.internal.compiler.Assignment> w) {
    fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    return new EpsilonDerivationTail();
  }
  public static java.util.List<Object> parse__Rule2(java.util.List<fling.internal.compiler.Assignment> w) {
    fling.internal.compiler.Assignment _a;
    java.util.List<Object> _b;
    if (w.isEmpty())
      return java.util.Collections.emptyList();
    _a = w.get(0);
    if (!(fling.internal.util.Collections.included(_a.σ, derive, specialize)))
      return java.util.Collections.emptyList();
    Rule rule = parse_Rule(w);
    java.util.List<Object> _c = parse__Rule2(w);
    return java.util.Arrays.asList(rule, _c);
  }
  public static java.util.List<Object> parse__RuleTail2(java.util.List<fling.internal.compiler.Assignment> w) {
    fling.internal.compiler.Assignment _a;
    java.util.List<Object> _b;
    if (w.isEmpty())
      return java.util.Collections.emptyList();
    _a = w.get(0);
    if (!(fling.internal.util.Collections.included(_a.σ, or, orNone)))
      return java.util.Collections.emptyList();
    RuleTail ruleTail = parse_RuleTail(w);
    java.util.List<Object> _c = parse__RuleTail2(w);
    return java.util.Arrays.asList(ruleTail, _c);
  }
}
