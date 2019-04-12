package fling.internal.grammar.sententials;

import java.util.*;
import java.util.function.*;

import fling.*;
import fling.internal.compiler.Namer;
import fling.internal.compiler.ast.nodes.FieldNode.FieldNodeFragment;

public interface Notation extends Symbol {
  Collection<Symbol> abbreviatedSymbols();
  Variable extend(Namer namer, Consumer<Variable> variableDeclaration, Consumer<DerivationRule> ruleDeclaration);
  List<FieldNodeFragment> getFields(Function<Symbol, List<FieldNodeFragment>> fieldTypesSolver,
      Function<String, String> nameFromBaseSolver);
  boolean isNullable(Function<Symbol, Boolean> nullabilitySolver);
  Set<Verb> getFirsts(Function<Symbol, Set<Verb>> firstsSolver);
}
