package il.ac.technion.cs.fling.examples.generated;

@SuppressWarnings("all")
public interface SubFigureCompiler {
  public static il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure parse_Figure(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Is.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.load)) return parse_Figure1(w);
    if (il.ac.technion.cs.fling.internal.util.Is.included(
        _a.σ,
        il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.row,
        il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.column)) return parse_Figure2(w);
    return null;
  }

  public static il.ac.technion.cs.fling.examples.generated.SubFigureAST.Orientation
      parse_Orientation(java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Is.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.row))
      return parse_Orientation1(w);
    if (il.ac.technion.cs.fling.internal.util.Is.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.column))
      return parse_Orientation2(w);
    return null;
  }

  public static il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure1 parse_Figure1(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    java.lang.String string = (java.lang.String) _a.arguments.get(0);
    return new il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure1(string);
  }

  public static il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure2 parse_Figure2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    il.ac.technion.cs.fling.examples.generated.SubFigureAST.Orientation orientation =
        parse_Orientation(w);
    _b =
        il.ac.technion.cs.fling.internal.grammar.sententials.notations.OneOrMore.abbreviate(
            parse__Figure3(w), 1);
    java.util.List<il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure> figure =
        (java.util.List<il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure>) _b.get(0);
    _a = w.remove(0);
    return new il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure2(orientation, figure);
  }

  public static il.ac.technion.cs.fling.examples.generated.SubFigureAST.Orientation1
      parse_Orientation1(java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    return new il.ac.technion.cs.fling.examples.generated.SubFigureAST.Orientation1();
  }

  public static il.ac.technion.cs.fling.examples.generated.SubFigureAST.Orientation2
      parse_Orientation2(java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    return new il.ac.technion.cs.fling.examples.generated.SubFigureAST.Orientation2();
  }

  public static java.util.List<java.lang.Object> parse__Figure3(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<java.lang.Object> _b;
    il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure figure = parse_Figure(w);
    java.util.List<java.lang.Object> _c = parse__Figure4(w);
    return java.util.Arrays.asList(figure, _c);
  }

  public static java.util.List<java.lang.Object> parse__Figure4(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<java.lang.Object> _b;
    if (w.isEmpty()) return java.util.Collections.emptyList();
    _a = w.get(0);
    if (!(il.ac.technion.cs.fling.internal.util.Is.included(
        _a.σ,
        il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.load,
        il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.row,
        il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.column)))
      return java.util.Collections.emptyList();
    il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure figure = parse_Figure(w);
    java.util.List<java.lang.Object> _c = parse__Figure4(w);
    return java.util.Arrays.asList(figure, _c);
  }
}

