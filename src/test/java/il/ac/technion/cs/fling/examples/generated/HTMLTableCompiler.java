package il.ac.technion.cs.fling.examples.generated;

@SuppressWarnings("all")
public interface HTMLTableCompiler {
  public static il.ac.technion.cs.fling.examples.generated.HTMLTableAST.HTML parse_HTML(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    java.lang.String string = (java.lang.String) _a.arguments.get(0);
    il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Table table = parse_Table(w);
    return new il.ac.technion.cs.fling.examples.generated.HTMLTableAST.HTML(string, table);
  }

  public static il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Table parse_Table(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    java.lang.String[] strings = (java.lang.String[]) _a.arguments.get(0);
    il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Header header = parse_Header(w);
    _b =
        il.ac.technion.cs.fling.internal.grammar.sententials.notations.NoneOrMore.abbreviate(
            parse__Row2(w), 1);
    java.util.List<il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Row> row =
        (java.util.List<il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Row>) _b.get(0);
    _a = w.remove(0);
    return new il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Table(strings, header, row);
  }

  public static il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Header parse_Header(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Tr tr = parse_Tr(w);
    _b =
        il.ac.technion.cs.fling.internal.grammar.sententials.notations.NoneOrMore.abbreviate(
            parse__Th2(w), 1);
    java.util.List<il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Th> th =
        (java.util.List<il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Th>) _b.get(0);
    _a = w.remove(0);
    return new il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Header(tr, th);
  }

  public static il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Row parse_Row(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Tr tr = parse_Tr(w);
    _b =
        il.ac.technion.cs.fling.internal.grammar.sententials.notations.NoneOrMore.abbreviate(
            parse__Td2(w), 1);
    java.util.List<il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Td> td =
        (java.util.List<il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Td>) _b.get(0);
    _a = w.remove(0);
    return new il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Row(tr, td);
  }

  public static il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Tr parse_Tr(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    java.lang.String[] strings = (java.lang.String[]) _a.arguments.get(0);
    return new il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Tr(strings);
  }

  public static il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Th parse_Th(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    java.lang.String[] strings = (java.lang.String[]) _a.arguments.get(0);
    _a = w.remove(0);
    java.lang.String string = (java.lang.String) _a.arguments.get(0);
    _a = w.remove(0);
    return new il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Th(strings, string);
  }

  public static il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Td parse_Td(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    java.lang.String[] strings = (java.lang.String[]) _a.arguments.get(0);
    il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Cell cell = parse_Cell(w);
    _a = w.remove(0);
    return new il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Td(strings, cell);
  }

  public static il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Cell parse_Cell(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a = w.get(0);
    if (il.ac.technion.cs.fling.internal.util.Is.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.HTMLTable.Σ.¢)) return parse_Cell1(w);
    if (il.ac.technion.cs.fling.internal.util.Is.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.HTMLTable.Σ.table)) return parse_Table(w);
    return null;
  }

  public static il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Cell1 parse_Cell1(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<?> _b;
    _a = w.remove(0);
    java.lang.String string = (java.lang.String) _a.arguments.get(0);
    return new il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Cell1(string);
  }

  public static java.util.List<java.lang.Object> parse__Row2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<java.lang.Object> _b;
    if (w.isEmpty()) return java.util.Collections.emptyList();
    _a = w.get(0);
    if (!(il.ac.technion.cs.fling.internal.util.Is.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.HTMLTable.Σ.tr)))
      return java.util.Collections.emptyList();
    il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Row row = parse_Row(w);
    java.util.List<java.lang.Object> _c = parse__Row2(w);
    return java.util.Arrays.asList(row, _c);
  }

  public static java.util.List<java.lang.Object> parse__Th2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<java.lang.Object> _b;
    if (w.isEmpty()) return java.util.Collections.emptyList();
    _a = w.get(0);
    if (!(il.ac.technion.cs.fling.internal.util.Is.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.HTMLTable.Σ.th)))
      return java.util.Collections.emptyList();
    il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Th th = parse_Th(w);
    java.util.List<java.lang.Object> _c = parse__Th2(w);
    return java.util.Arrays.asList(th, _c);
  }

  public static java.util.List<java.lang.Object> parse__Td2(
      java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w) {
    il.ac.technion.cs.fling.internal.compiler.Assignment _a;
    java.util.List<java.lang.Object> _b;
    if (w.isEmpty()) return java.util.Collections.emptyList();
    _a = w.get(0);
    if (!(il.ac.technion.cs.fling.internal.util.Is.included(
        _a.σ, il.ac.technion.cs.fling.examples.languages.HTMLTable.Σ.td)))
      return java.util.Collections.emptyList();
    il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Td td = parse_Td(w);
    java.util.List<java.lang.Object> _c = parse__Td2(w);
    return java.util.Arrays.asList(td, _c);
  }
}

