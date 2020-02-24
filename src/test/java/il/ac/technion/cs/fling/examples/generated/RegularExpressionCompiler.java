package il.ac.technion.cs.fling.examples.generated;

import java.util.*;

@SuppressWarnings("all")
public interface RegularExpressionCompiler {
  public static il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Expression
      parse_Expression(java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE rE = parse_RE(w);
    return new il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Expression(rE);
  }

  public static il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE parse_RE(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.exactly))
      return parse_RE1(w);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.option))
      return parse_RE2(w);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.noneOrMore))
      return parse_RE3(w);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.oneOrMore))
      return parse_RE4(w);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.either))
      return parse_RE5(w);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.anyChar))
      return parse_RE6(w);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.anyDigit))
      return parse_RE7(w);
    return null;
  }

  public static il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail parse_Tail(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    if (w.isEmpty()) return parse_Tail3(w);
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.and))
      return parse_Tail1(w);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.or))
      return parse_Tail2(w);
    return parse_Tail3(w);
  }

  public static il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE1 parse_RE1(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    java.lang.String string = (java.lang.String) _a.arguments.get(0);
    il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail tail = parse_Tail(w);
    return new il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE1(string, tail);
  }

  public static il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE2 parse_RE2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE rE =
        (il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE) _a.arguments.get(0);
    il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail tail = parse_Tail(w);
    return new il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE2(rE, tail);
  }

  public static il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE3 parse_RE3(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE rE =
        (il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE) _a.arguments.get(0);
    il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail tail = parse_Tail(w);
    return new il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE3(rE, tail);
  }

  public static il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE4 parse_RE4(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE rE =
        (il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE) _a.arguments.get(0);
    il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail tail = parse_Tail(w);
    return new il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE4(rE, tail);
  }

  public static il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE5 parse_RE5(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE[] rEs =
        (il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE[]) _a.arguments.get(0);
    il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail tail = parse_Tail(w);
    return new il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE5(rEs, tail);
  }

  public static il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE6 parse_RE6(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail tail = parse_Tail(w);
    return new il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE6(tail);
  }

  public static il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE7 parse_RE7(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail tail = parse_Tail(w);
    return new il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE7(tail);
  }

  public static il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail1 parse_Tail1(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE rE = parse_RE(w);
    return new il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail1(rE);
  }

  public static il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail2 parse_Tail2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE rE = parse_RE(w);
    return new il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail2(rE);
  }

  public static il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail3 parse_Tail3(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    return new il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail3();
  }
}

