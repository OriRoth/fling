package il.ac.technion.cs.fling.examples.generated;

@SuppressWarnings("all")
public interface SimpleArithmeticCompiler {
  public static il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E parse_E(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T t = parse_T(w);
    il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E_ e_ = parse_E_(w);
    return new il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E(t, e_);
  }

  public static il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T parse_T(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.F f = parse_F(w);
    il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T_ t_ = parse_T_(w);
    return new il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T(f, t_);
  }

  public static il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E_ parse_E_(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    if (w.isEmpty()) return parse_E_2(w);
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Is.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.SimpleArithmetic.Σ.plus))
      return parse_E_1(w);
    return parse_E_2(w);
  }

  public static il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.F parse_F(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Is.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.SimpleArithmetic.Σ.begin))
      return parse_F1(w);
    if (il.ac.technion.cs.fling.internal.util.Is.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.SimpleArithmetic.Σ.i)) return parse_F2(w);
    return null;
  }

  public static il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T_ parse_T_(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    if (w.isEmpty()) return parse_T_2(w);
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Is.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.SimpleArithmetic.Σ.mult))
      return parse_T_1(w);
    return parse_T_2(w);
  }

  public static il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E_1 parse_E_1(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T t = parse_T(w);
    il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E_ e_ = parse_E_(w);
    return new il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E_1(t, e_);
  }

  public static il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E_2 parse_E_2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    return new il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E_2();
  }

  public static il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.F1 parse_F1(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E e = parse_E(w);
    _a = w.remove(0);
    return new il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.F1(e);
  }

  public static il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.F2 parse_F2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    java.lang.Integer integer = (java.lang.Integer) _a.arguments.get(0);
    return new il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.F2(integer);
  }

  public static il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T_1 parse_T_1(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.F f = parse_F(w);
    il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T_ t_ = parse_T_(w);
    return new il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T_1(f, t_);
  }

  public static il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T_2 parse_T_2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    return new il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T_2();
  }
}

