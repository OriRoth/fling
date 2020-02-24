package il.ac.technion.cs.fling.examples.generated;

import java.util.*;

@SuppressWarnings("all")
public interface BalancedParenthesesCompiler {
  public static il.ac.technion.cs.fling.examples.generated.BalancedParenthesesAST.P parse_P(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    if (w.isEmpty()) return parse_P2(w);
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Collections.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.BalancedParentheses.Σ.c))
      return parse_P1(w);
    return parse_P2(w);
  }

  public static il.ac.technion.cs.fling.examples.generated.BalancedParenthesesAST.P1 parse_P1(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.BalancedParenthesesAST.P p = parse_P(w);
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.BalancedParenthesesAST.P p2 = parse_P(w);
    return new il.ac.technion.cs.fling.examples.generated.BalancedParenthesesAST.P1(p, p2);
  }

  public static il.ac.technion.cs.fling.examples.generated.BalancedParenthesesAST.P2 parse_P2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    return new il.ac.technion.cs.fling.examples.generated.BalancedParenthesesAST.P2();
  }
}

