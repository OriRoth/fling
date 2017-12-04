package org.spartan.fajita.revision.symbols;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import org.spartan.fajita.revision.bnf.DerivationRule;
import org.spartan.fajita.revision.symbols.extendibles.Extendible;

public interface Symbol {
  public String name();
  public default boolean isVerb() {
    return Verb.class.isAssignableFrom(getClass());
  }
  public default boolean isNonTerminal() {
    return NonTerminal.class.isAssignableFrom(getClass());
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
  public default NonTerminal asNonTerminal() {
    return (NonTerminal) this;
  }
  public default Terminal asTerminal() {
    return (Terminal) this;
  }
  public default Extendible asExtendible() {
    return (Extendible) this;
  }
  // NOTE applicable only after solve
  default Symbol head() {
    return this;
  }
  @SuppressWarnings("unused") default List<DerivationRule> solve(NonTerminal lhs, Function<NonTerminal, NonTerminal> producer) {
    return new LinkedList<>();
  }
}