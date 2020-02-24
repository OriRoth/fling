package il.ac.technion.cs.fling.examples.generated;

import java.util.*;

@SuppressWarnings("all")
public interface DatalogCompiler {
  public static il.ac.technion.cs.fling.examples.generated.DatalogAST.Program parse_Program(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _b =
        il.ac.technion.cs.fling.internal.grammar.sententials.notations.OneOrMore.abbreviate(
            parse__Statement3(w), 1);
    java.util.List<il.ac.technion.cs.fling.examples.generated.DatalogAST.Statement> statement =
        (java.util.List<il.ac.technion.cs.fling.examples.generated.DatalogAST.Statement>) _b.get(0);
    return new il.ac.technion.cs.fling.examples.generated.DatalogAST.Program(statement);
  }

  public static il.ac.technion.cs.fling.examples.generated.DatalogAST.Statement parse_Statement(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.Datalog.Σ.fact)) return parse_Fact(w);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ,
        il.ac.technion.cs.fling.examples.languages.Datalog.Σ.always,
        il.ac.technion.cs.fling.examples.languages.Datalog.Σ.infer)) return parse_Rule(w);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.Datalog.Σ.query)) return parse_Query(w);
    return null;
  }

  public static il.ac.technion.cs.fling.examples.generated.DatalogAST.Fact parse_Fact(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    java.lang.String string = (java.lang.String) _a.arguments.get(0);
    _a = w.remove(0);
    java.lang.String[] strings = (java.lang.String[]) _a.arguments.get(0);
    return new il.ac.technion.cs.fling.examples.generated.DatalogAST.Fact(string, strings);
  }

  public static il.ac.technion.cs.fling.examples.generated.DatalogAST.Rule parse_Rule(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.Datalog.Σ.always))
      return parse_Bodyless(w);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.Datalog.Σ.infer)) return parse_WithBody(w);
    return null;
  }

  public static il.ac.technion.cs.fling.examples.generated.DatalogAST.Query parse_Query(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    java.lang.String string = (java.lang.String) _a.arguments.get(0);
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.DatalogAST.Term[] terms =
        (il.ac.technion.cs.fling.examples.generated.DatalogAST.Term[]) _a.arguments.get(0);
    return new il.ac.technion.cs.fling.examples.generated.DatalogAST.Query(string, terms);
  }

  public static il.ac.technion.cs.fling.examples.generated.DatalogAST.Bodyless parse_Bodyless(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    java.lang.String string = (java.lang.String) _a.arguments.get(0);
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.DatalogAST.Term[] terms =
        (il.ac.technion.cs.fling.examples.generated.DatalogAST.Term[]) _a.arguments.get(0);
    return new il.ac.technion.cs.fling.examples.generated.DatalogAST.Bodyless(string, terms);
  }

  public static il.ac.technion.cs.fling.examples.generated.DatalogAST.WithBody parse_WithBody(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    il.ac.technion.cs.fling.examples.generated.DatalogAST.RuleHead ruleHead = parse_RuleHead(w);
    il.ac.technion.cs.fling.examples.generated.DatalogAST.RuleBody ruleBody = parse_RuleBody(w);
    return new il.ac.technion.cs.fling.examples.generated.DatalogAST.WithBody(ruleHead, ruleBody);
  }

  public static il.ac.technion.cs.fling.examples.generated.DatalogAST.RuleHead parse_RuleHead(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    java.lang.String string = (java.lang.String) _a.arguments.get(0);
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.DatalogAST.Term[] terms =
        (il.ac.technion.cs.fling.examples.generated.DatalogAST.Term[]) _a.arguments.get(0);
    return new il.ac.technion.cs.fling.examples.generated.DatalogAST.RuleHead(string, terms);
  }

  public static il.ac.technion.cs.fling.examples.generated.DatalogAST.RuleBody parse_RuleBody(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    il.ac.technion.cs.fling.examples.generated.DatalogAST.FirstClause firstClause =
        parse_FirstClause(w);
    _b =
        il.ac.technion.cs.fling.internal.grammar.sententials.notations.NoneOrMore.abbreviate(
            parse__AdditionalClause2(w), 1);
    java.util.List<il.ac.technion.cs.fling.examples.generated.DatalogAST.AdditionalClause>
        additionalClause =
            (java.util.List<il.ac.technion.cs.fling.examples.generated.DatalogAST.AdditionalClause>)
                _b.get(0);
    return new il.ac.technion.cs.fling.examples.generated.DatalogAST.RuleBody(
        firstClause, additionalClause);
  }

  public static il.ac.technion.cs.fling.examples.generated.DatalogAST.FirstClause parse_FirstClause(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    java.lang.String string = (java.lang.String) _a.arguments.get(0);
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.DatalogAST.Term[] terms =
        (il.ac.technion.cs.fling.examples.generated.DatalogAST.Term[]) _a.arguments.get(0);
    return new il.ac.technion.cs.fling.examples.generated.DatalogAST.FirstClause(string, terms);
  }

  public static il.ac.technion.cs.fling.examples.generated.DatalogAST.AdditionalClause
      parse_AdditionalClause(
          java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    java.lang.String string = (java.lang.String) _a.arguments.get(0);
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.DatalogAST.Term[] terms =
        (il.ac.technion.cs.fling.examples.generated.DatalogAST.Term[]) _a.arguments.get(0);
    return new il.ac.technion.cs.fling.examples.generated.DatalogAST.AdditionalClause(
        string, terms);
  }

  public static il.ac.technion.cs.fling.examples.generated.DatalogAST.Term parse_Term(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.Datalog.Σ.l)) return parse_Term1(w);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.Datalog.Σ.v)) return parse_Term2(w);
    return null;
  }

  public static il.ac.technion.cs.fling.examples.generated.DatalogAST.Term1 parse_Term1(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    java.lang.String string = (java.lang.String) _a.arguments.get(0);
    return new il.ac.technion.cs.fling.examples.generated.DatalogAST.Term1(string);
  }

  public static il.ac.technion.cs.fling.examples.generated.DatalogAST.Term2 parse_Term2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    java.lang.String string = (java.lang.String) _a.arguments.get(0);
    return new il.ac.technion.cs.fling.examples.generated.DatalogAST.Term2(string);
  }

  public static java.util.List<java.lang.Object> parse__Statement3(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<java.lang.Object> _b;
    il.ac.technion.cs.fling.examples.generated.DatalogAST.Statement statement = parse_Statement(w);
    java.util.List<java.lang.Object> _c = parse__Statement4(w);
    return java.util.Arrays.asList(statement, _c);
  }

  public static java.util.List<java.lang.Object> parse__Statement4(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<java.lang.Object> _b;
    if (w.isEmpty()) return java.util.Collections.emptyList();
    _a = w.get(0);
    if (!(il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ,
        il.ac.technion.cs.fling.examples.languages.Datalog.Σ.fact,
        il.ac.technion.cs.fling.examples.languages.Datalog.Σ.query,
        il.ac.technion.cs.fling.examples.languages.Datalog.Σ.always,
        il.ac.technion.cs.fling.examples.languages.Datalog.Σ.infer)))
      return java.util.Collections.emptyList();
    il.ac.technion.cs.fling.examples.generated.DatalogAST.Statement statement = parse_Statement(w);
    java.util.List<java.lang.Object> _c = parse__Statement4(w);
    return java.util.Arrays.asList(statement, _c);
  }

  public static java.util.List<java.lang.Object> parse__AdditionalClause2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<java.lang.Object> _b;
    if (w.isEmpty()) return java.util.Collections.emptyList();
    _a = w.get(0);
    if (!(il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.Datalog.Σ.and)))
      return java.util.Collections.emptyList();
    il.ac.technion.cs.fling.examples.generated.DatalogAST.AdditionalClause additionalClause =
        parse_AdditionalClause(w);
    java.util.List<java.lang.Object> _c = parse__AdditionalClause2(w);
    return java.util.Arrays.asList(additionalClause, _c);
  }
}

