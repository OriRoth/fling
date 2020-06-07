package il.ac.technion.cs.fling.internal.grammar.sententials;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.FieldNode.FieldNodeFragment;

public interface Quantifier extends Symbol {
	default Stream<Symbol> symbols() {
		return Stream.of(symbol());
	}

	Symbol symbol();

	Collection<Symbol> abbreviatedSymbols();

	Variable expand(Namer namer, Consumer<Variable> variableDeclaration, Consumer<DerivationRule> ruleDeclaration);

	List<FieldNodeFragment> getFields(Function<Symbol, List<FieldNodeFragment>> fieldTypesSolver,
			Function<String, String> nameFromBaseSolver);

	boolean isNullable(Function<Symbol, Boolean> nullabilitySolver);

	Set<Verb> getFirsts(Function<Symbol, Set<Verb>> firstsSolver);
}
