package il.ac.technion.cs.fling.examples.generated;

import java.util.*;

@SuppressWarnings("all")
public interface BNFAPICompiler {
  public static il.ac.technion.cs.fling.examples.generated.BNFAPIAST.PlainBNF parse_PlainBNF(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    _a = w.remove(0);
    il.ac.technion.cs.fling.Variable variable =
        (il.ac.technion.cs.fling.Variable) _a.arguments.get(0);
    _b =
        il.ac.technion.cs.fling.internal.grammar.sententials.notations.NoneOrMore.abbreviate(
            parse__Rule2(w), 1);
    java.util.List<il.ac.technion.cs.fling.examples.generated.BNFAPIAST.Rule> rule =
        (java.util.List<il.ac.technion.cs.fling.examples.generated.BNFAPIAST.Rule>) _b.get(0);
    return new il.ac.technion.cs.fling.examples.generated.BNFAPIAST.PlainBNF(variable, rule);
  }

  public static il.ac.technion.cs.fling.examples.generated.BNFAPIAST.Rule parse_Rule(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.BNF.Σ.derive)) return parse_Rule1(w);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.BNF.Σ.specialize)) return parse_Rule2(w);
    return null;
  }

  public static il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleBody parse_RuleBody(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.BNF.Σ.to)) return parse_RuleBody1(w);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.BNF.Σ.toEpsilon))
      return parse_RuleBody2(w);
    return null;
  }

  public static il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleTail parse_RuleTail(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.BNF.Σ.or)) return parse_RuleTail1(w);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.BNF.Σ.orNone)) return parse_RuleTail2(w);
    return null;
  }

  public static il.ac.technion.cs.fling.examples.generated.BNFAPIAST.Rule1 parse_Rule1(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.Variable variable =
        (il.ac.technion.cs.fling.Variable) _a.arguments.get(0);
    il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleBody ruleBody = parse_RuleBody(w);
    return new il.ac.technion.cs.fling.examples.generated.BNFAPIAST.Rule1(variable, ruleBody);
  }

  public static il.ac.technion.cs.fling.examples.generated.BNFAPIAST.Rule2 parse_Rule2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.Variable variable =
        (il.ac.technion.cs.fling.Variable) _a.arguments.get(0);
    _a = w.remove(0);
    il.ac.technion.cs.fling.Variable[] variables =
        (il.ac.technion.cs.fling.Variable[]) _a.arguments.get(0);
    return new il.ac.technion.cs.fling.examples.generated.BNFAPIAST.Rule2(variable, variables);
  }

  public static il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleBody1 parse_RuleBody1(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.Symbol[] symbols =
        (il.ac.technion.cs.fling.Symbol[]) _a.arguments.get(0);
    _b =
        il.ac.technion.cs.fling.internal.grammar.sententials.notations.NoneOrMore.abbreviate(
            parse__RuleTail2(w), 1);
    java.util.List<il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleTail> ruleTail =
        (java.util.List<il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleTail>) _b.get(0);
    return new il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleBody1(symbols, ruleTail);
  }

  public static il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleBody2 parse_RuleBody2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    return new il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleBody2();
  }

  public static il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleTail1 parse_RuleTail1(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.Symbol[] symbols =
        (il.ac.technion.cs.fling.Symbol[]) _a.arguments.get(0);
    return new il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleTail1(symbols);
  }

  public static il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleTail2 parse_RuleTail2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    return new il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleTail2();
  }

  public static java.util.List<java.lang.Object> parse__Rule2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<java.lang.Object> _b;
    if (w.isEmpty()) return java.util.Collections.emptyList();
    _a = w.get(0);
    if (!(il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ,
        il.ac.technion.cs.fling.examples.languages.BNF.Σ.derive,
        il.ac.technion.cs.fling.examples.languages.BNF.Σ.specialize)))
      return java.util.Collections.emptyList();
    il.ac.technion.cs.fling.examples.generated.BNFAPIAST.Rule rule = parse_Rule(w);
    java.util.List<java.lang.Object> _c = parse__Rule2(w);
    return java.util.Arrays.asList(rule, _c);
  }

  public static java.util.List<java.lang.Object> parse__RuleTail2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<java.lang.Object> _b;
    if (w.isEmpty()) return java.util.Collections.emptyList();
    _a = w.get(0);
    if (!(il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ,
        il.ac.technion.cs.fling.examples.languages.BNF.Σ.or,
        il.ac.technion.cs.fling.examples.languages.BNF.Σ.orNone)))
      return java.util.Collections.emptyList();
    il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleTail ruleTail = parse_RuleTail(w);
    java.util.List<java.lang.Object> _c = parse__RuleTail2(w);
    return java.util.Arrays.asList(ruleTail, _c);
  }
}

