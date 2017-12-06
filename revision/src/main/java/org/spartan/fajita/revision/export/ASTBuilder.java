package org.spartan.fajita.revision.export;

import java.util.List;

import org.spartan.fajita.revision.parser.ell.Interpretation;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.extendibles.Extendible;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("rawtypes") public class ASTBuilder {
  private Interpretation current;
  private final String astPath;

  public ASTBuilder(Interpretation conclusion, String astPath) {
    this.current = conclusion;
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
    throw problem();
  }
  private Object build(Interpretation i) {
    Symbol s = i.symbol;
    if (s.isExtendible())
      return build(s.asExtendible(), i.value);
    throw problem();
  }
  private Object build(Object o, List values) {
    if (o instanceof Symbol)
      return build((Symbol) o, values);
    throw problem();
  }
  private Object build(Symbol s, List values) {
    if (s.isNonTerminal())
      return build((NonTerminal) s, values);
    System.out.println(s.getClass());
    throw problem();
  }
  private Object build(NonTerminal s, List values) {
    // System.out.println(s);
    // System.out.println(clazz(s));
    // System.out.println(values);
    throw problem();
  }
  private Object build(Extendible e, List values) {
    return e.conclude(values, this::build);
  }
  @SuppressWarnings("unchecked") private Object instance(Class<?> c, List value) {
    List arguments = build(value);
    try {
      return c.getConstructors()[0].newInstance(arguments.toArray(new Object[arguments.size()]));
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
        | SecurityException e) {
      throw new RuntimeException(e);
    }
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
  private static RuntimeException problem() {
    return new RuntimeException("encountered a problem creating the ast");
  }
}
