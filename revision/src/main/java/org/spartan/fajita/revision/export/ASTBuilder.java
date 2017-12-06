package org.spartan.fajita.revision.export;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.spartan.fajita.revision.ast.encoding.JamoosClassesRenderer;
import org.spartan.fajita.revision.parser.ell.Interpretation;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.extendibles.Extendible;

@SuppressWarnings("rawtypes") public class ASTBuilder {
  private Interpretation current;
  private final JamoosClassesRenderer jamoos;
  private final String astPath;

  public ASTBuilder(Interpretation conclusion, JamoosClassesRenderer jamoos, String astPath) {
    this.current = conclusion;
    this.jamoos = jamoos;
    this.astPath = astPath;
  }
  @SuppressWarnings("unchecked") public <S> S build() {
    assert current.symbol.equals(SpecialSymbols.augmentedStartSymbol);
    Interpretation s = (Interpretation) current.value.get(0);
    return (S) instance(clazz(s), s.value);
  }
  @SuppressWarnings("unchecked") private List build(List value) {
    return (List) value.stream().map(this::build).collect(toList());
  }
  private Object build(Object o) {
    if (o instanceof Interpretation)
      return build((Interpretation) o);
    return o;
  }
  private Object build(Interpretation i) {
    return build(i.symbol, i.value);
  }
  private Object build(Object o, List values) {
    if (o instanceof Symbol)
      return build((Symbol) o, values);
    throw problem();
  }
  private Object build(Symbol s, List values) {
    if (s.isNonTerminal())
      return build(s.asNonTerminal(), values);
    if (s.isExtendible())
      return build(s.asExtendible(), values);
    if (s.isVerb())
      return build(s.asVerb(), values);
    throw problem();
  }
  private Object build(NonTerminal nt, List values) {
    if (nt == null)
      throw problem();
    if (jamoos.isAbstractNonTerminal(nt))
      return build(jamoos.solveAbstractNonTerminal(nt, nextTerminal(values)), values);
    return instance(clazz(nt), values);
  }
  private Object build(Extendible e, List values) {
    return e.conclude(values, this::build);
  }
  private Object build(Verb v, List values) {
    List solved = v.conclude(values, this::build);
    return solved.get(0);
  }
  @SuppressWarnings("unchecked") private Object instance(Class<?> c, List values) {
    List arguments = build(values);
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
    return ctor.newInstance(arguments);
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
