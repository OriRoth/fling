package roth.ori.fling.export;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import roth.ori.fling.ast.encoding.JamoosClassesRenderer;
import roth.ori.fling.parser.ell.EBNFAnalyzer;
import roth.ori.fling.parser.ell.Interpretation;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.SpecialSymbols;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;
import roth.ori.fling.symbols.Verb;
import roth.ori.fling.symbols.extendibles.Extendible;

@SuppressWarnings("rawtypes") public class ASTBuilder {
  private Interpretation current;
  private final JamoosClassesRenderer jamoos;
  private final String astPath;
  private final EBNFAnalyzer analyzer;

  public ASTBuilder(Interpretation conclusion, JamoosClassesRenderer jamoos, String astPath, EBNFAnalyzer analyzer) {
    this.current = conclusion;
    this.jamoos = jamoos;
    this.astPath = astPath;
    this.analyzer = analyzer;
  }
  @SuppressWarnings("unchecked") public <S> S build() {
    assert current.symbol.equals(SpecialSymbols.augmentedStartSymbol);
    Interpretation s = (Interpretation) current.value.get(0);
    return (S) instance(clazz(s), s.value);
  }
  @SuppressWarnings("unchecked") private List buildAll(List value) {
    return (List) value.stream().map(this::build).collect(toList());
  }
  private List build(Object o) {
    if (o instanceof Interpretation)
      return build((Interpretation) o);
    return Collections.singletonList(o);
  }
  private List build(Interpretation i) {
    return build(i.symbol, i.value);
  }
  private List build(Symbol s, List values) {
    if (SpecialSymbols.augmentedStartSymbol.equals(s))
      return buildAugS(values);
    if (s.isNonTerminal())
      return build(s.asNonTerminal(), values);
    if (s.isExtendible())
      return build(s.asExtendible(), values);
    if (s.isVerb())
      return build(s.asVerb(), values);
    throw problem();
  }
  private List buildAugS(List values) {
    assert values.size() == 1 && values.get(0) instanceof Interpretation;
    return build((Interpretation) values.get(0));
  }
  private List build(NonTerminal nt, List values) {
    if (nt == null)
      throw problem();
    if (jamoos.isAbstractNonTerminal(nt)) {
      if (values.isEmpty())
        return build(jamoos.solveAbstractNonTerminal(nt, null, analyzer), new ArrayList<>());
      assert values.size() == 1 && values.get(0) instanceof Interpretation;
      Interpretation i = (Interpretation) values.get(0);
      return build(jamoos.solveAbstractNonTerminal(nt, nextTerminal(values), analyzer), i.value);
    }
    return Collections.singletonList(instance(clazz(nt), values));
  }
  private List build(Extendible e, List values) {
    return e.conclude(values, this::build, this::clazz);
  }
  private List build(Verb v, List values) {
    return v.conclude(values, this::build);
  }
  @SuppressWarnings("unchecked") private Object instance(Class<?> c, List values) {
    List ba = (List) buildAll(values).stream().filter(x -> x != null).collect(toList()), arguments = new LinkedList();
    for (Object o : ba)
      arguments.addAll((List) o);
    try {
      Constructor<?> ctor = c.getConstructors()[0];
      return instance(ctor, arguments.toArray(new Object[arguments.size()]));
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
        | SecurityException e) {
      throw new RuntimeException(e);
    }
  }
  private static Object instance(Constructor<?> ctor, Object[] arguments)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    return ctor.newInstance(ctor.getParameterTypes().length > 0 ? arguments : new Object[0]);
  }
  private Class<?> clazz(String s) {
    try {
      return Class.forName(astPath + "$" + s);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
  private Class<?> clazz(Symbol s) {
    return clazz(s.name());
  }
  private Class<?> clazz(Interpretation i) {
    return clazz(i.symbol);
  }
  private static Terminal nextTerminal(List values) {
    return values.isEmpty() ? null : nextTerminal(values.get(0));
  }
  private static Terminal nextTerminal(Object o) {
    if (o instanceof Interpretation)
      return nextTerminal((Interpretation) o);
    throw problem();
  }
  private static Terminal nextTerminal(Interpretation i) {
    return i.symbol.isTerminal() ? i.symbol.asTerminal() : nextTerminal(i.value);
  }
  private static RuntimeException problem() {
    return new RuntimeException("encountered a problem creating the ast");
  }
}
