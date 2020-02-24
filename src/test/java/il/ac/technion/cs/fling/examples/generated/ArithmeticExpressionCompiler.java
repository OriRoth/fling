package il.ac.technion.cs.fling.examples.generated;

import java.util.*;

@SuppressWarnings("all")
public interface ArithmeticExpressionCompiler {
  public static il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.E parse_E(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.T t = parse_T(w);
    il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.E_ e_ = parse_E_(w);
    return new il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.E(t, e_);
  }

  public static il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.T parse_T(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.F f = parse_F(w);
    il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.T_ t_ = parse_T_(w);
    return new il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.T(f, t_);
  }

  public static il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.E_ parse_E_(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    if (w.isEmpty()) return parse_E_3(w);
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ.plus))
      return parse_E_1(w);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ.minus))
      return parse_E_2(w);
    return parse_E_3(w);
  }

  public static il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.F parse_F(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ.begin))
      return parse_F1(w);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ.v))
      return parse_F2(w);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ.n))
      return parse_F3(w);
    return null;
  }

  public static il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.T_ parse_T_(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    if (w.isEmpty()) return parse_T_3(w);
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ.times))
      return parse_T_1(w);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ.divide))
      return parse_T_2(w);
    return parse_T_3(w);
  }

  public static il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.E_1 parse_E_1(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.T t = parse_T(w);
    il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.E_ e_ = parse_E_(w);
    return new il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.E_1(t, e_);
  }

  public static il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.E_2 parse_E_2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.T t = parse_T(w);
    il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.E_ e_ = parse_E_(w);
    return new il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.E_2(t, e_);
  }

  public static il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.E_3 parse_E_3(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    return new il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.E_3();
  }

  public static il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.F1 parse_F1(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.E e = parse_E(w);
    _a = w.remove(0);
    return new il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.F1(e);
  }

  public static il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.F2 parse_F2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    java.lang.String string = (java.lang.String) _a.arguments.get(0);
    return new il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.F2(string);
  }

  public static il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.F3 parse_F3(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    double d = (double) _a.arguments.get(0);
    return new il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.F3(d);
  }

  public static il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.T_1 parse_T_1(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.F f = parse_F(w);
    il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.T_ t_ = parse_T_(w);
    return new il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.T_1(f, t_);
  }

  public static il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.T_2 parse_T_2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.F f = parse_F(w);
    il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.T_ t_ = parse_T_(w);
    return new il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.T_2(f, t_);
  }

  public static il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.T_3 parse_T_3(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    return new il.ac.technion.cs.fling.examples.generated.ArithmeticExpressionAST.T_3();
  }
}

