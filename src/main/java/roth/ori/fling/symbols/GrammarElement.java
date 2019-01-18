package roth.ori.fling.symbols;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import roth.ori.fling.bnf.DerivationRule;
import roth.ori.fling.symbols.extendibles.Extendible;

public interface GrammarElement {
  public String name();
  public default boolean isVerb() {
    return Verb.class.isAssignableFrom(getClass());
  }
  public default boolean isNonTerminal() {
    return Symbol.class.isAssignableFrom(getClass());
  }
  public default boolean isTerminal() {
    return Terminal.class.isAssignableFrom(getClass());
  }
  public default boolean isExtendible() {
    return Extendible.class.isAssignableFrom(getClass());
  }
  public default Verb asVerb() {
    return (Verb) this;
  }
  public default Symbol asNonTerminal() {
    return (Symbol) this;
  }
  public default Terminal asTerminal() {
    return (Terminal) this;
  }
  public default Extendible asExtendible() {
    return (Extendible) this;
  }
  // NOTE applicable only after solve
  default GrammarElement head() {
    return this;
  }
  @SuppressWarnings("unused") default List<DerivationRule> solve(Symbol lhs, Function<Symbol, Symbol> producer) {
    return new LinkedList<>();
  }
  @SuppressWarnings({ "rawtypes", "unused" }) default List<Class> toClasses(Function<GrammarElement, Class> classSolution) {
    throw new UnsupportedOperationException();
  }
}