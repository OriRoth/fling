package fling.internal.grammar.sententials;

import java.util.*;
import java.util.function.*;

import fling.internal.compiler.Namer;
import fling.internal.compiler.ast.nodes.FieldNode.FieldNodeFragment;
import fling.internal.grammar.sententials.notations.*;

public interface Notation extends Symbol {
  Collection<Symbol> abbreviatedSymbols();
  Variable extend(Namer namer, Consumer<Variable> variableDeclaration, Consumer<DerivationRule> ruleDeclaration);
  List<FieldNodeFragment> getFields(Function<Symbol, List<FieldNodeFragment>> fieldTypesSolver,
      Function<String, String> nameFromBaseSolver);
  boolean isNullable(Function<Symbol, Boolean> nullabilitySolver);
  Set<Verb> getFirsts(Function<Symbol, Set<Verb>> firstsSolver);
  static OneOrMore oneOrMore(final Symbol symbol) {
    return new OneOrMore(!symbol.isTerminal() ? symbol : new Verb(symbol.asTerminal()));
  }
  static NoneOrMore noneOrMore(final Symbol symbol) {
    return new NoneOrMore(!symbol.isTerminal() ? symbol : new Verb(symbol.asTerminal()));
  }
}
