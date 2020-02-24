package il.ac.technion.cs.fling.examples.generated;

@SuppressWarnings("all")
public interface TaggedBalancedParenthesesCompiler {
  public static il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P parse_P(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    if (w.isEmpty()) return parse_P2(w);
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Is.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.TaggedBalancedParentheses.Σ.c))
      return parse_P1(w);
    return parse_P2(w);
  }

  public static il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.AB parse_AB(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Is.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.TaggedBalancedParentheses.Σ.a))
      return parse_AB1(w);
    if (il.ac.technion.cs.fling.internal.util.Is.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.TaggedBalancedParentheses.Σ.b))
      return parse_AB2(w);
    return null;
  }

  public static il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P1 parse_P1(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    char[] chars = (char[]) _a.arguments.get(0);
    il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P p = parse_P(w);
    _a = w.remove(0);
    il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.AB aB =
        (il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.AB)
            _a.arguments.get(0);
    il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P p2 = parse_P(w);
    return new il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P1(
        chars, p, aB, p2);
  }

  public static il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P2 parse_P2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    return new il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P2();
  }

  public static il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.AB1
      parse_AB1(java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    return new il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.AB1();
  }

  public static il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.AB2
      parse_AB2(java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _b =
        il.ac.technion.cs.fling.internal.grammar.sententials.notations.OneOrMore.abbreviate(
            parse__b3(w), 1);
    java.util.List<java.lang.Integer> i = (java.util.List<java.lang.Integer>) _b.get(0);
    return new il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.AB2(i);
  }

  public static java.util.List<java.lang.Object> parse__b3(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<java.lang.Object> _b;
    _a = w.remove(0);
    int i = (int) _a.arguments.get(0);
    java.util.List<java.lang.Object> _c = parse__b4(w);
    return java.util.Arrays.asList(i, _c);
  }

  public static java.util.List<java.lang.Object> parse__b4(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<java.lang.Object> _b;
    if (w.isEmpty()) return java.util.Collections.emptyList();
    _a = w.get(0);
    if (!(il.ac.technion.cs.fling.internal.util.Is.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.TaggedBalancedParentheses.Σ.b)))
      return java.util.Collections.emptyList();
    _a = w.remove(0);
    int i = (int) _a.arguments.get(0);
    java.util.List<java.lang.Object> _c = parse__b4(w);
    return java.util.Arrays.asList(i, _c);
  }
}

