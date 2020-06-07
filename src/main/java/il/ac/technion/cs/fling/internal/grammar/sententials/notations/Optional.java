package il.ac.technion.cs.fling.internal.grammar.sententials.notations;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.*;
import java.util.function.*;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.FieldNode.FieldNodeFragment;
import il.ac.technion.cs.fling.internal.grammar.sententials.*;
import il.ac.technion.cs.fling.internal.grammar.types.ClassParameter;

@JavaCompatibleNotation
public class Optional extends QQ {

	public Optional(Symbol symbol) {
		super(symbol);
	}

	@Override
	public Variable expand(final Namer namer, final Consumer<Variable> variableDeclaration,
			final Consumer<DerivationRule> ruleDeclaration) {
		final Variable head = namer.createQuantificationChild(symbol);
		variableDeclaration.accept(head);
		ruleDeclaration.accept(new DerivationRule(head, asList(//
				new SententialForm(symbol), // 
				new SententialForm())));
		return head;
	}

	@Override
	public List<FieldNodeFragment> getFields(final Function<Symbol, List<FieldNodeFragment>> fieldsSolver,
		 final Function<String, String> nameFromBaseSolver) {
		// TODO manage inner symbol with no fields.
		return fieldsSolver.apply(symbol).stream() //
				.map(innerField -> new FieldNodeFragment( //
						String.format("%s<%s>", //
								List.class.getCanonicalName(), //
								ClassParameter.unPrimitiveType(innerField.parameterType)), //
						innerField.parameterName) {
					@Override
					public String visitingMethod(final BiFunction<Variable, String, String> variableVisitingSolver,
							final String accessor, final Supplier<String> variableNamesGenerator) {
						if (!symbol.isVariable())
							return null;
						final String streamingVariable = variableNamesGenerator.get();
						return String.format("%s.stream().forEach(%s->%s)", //
								accessor, //
								streamingVariable, //
								variableVisitingSolver.apply(symbol.asVariable(), streamingVariable));
					}
				}) //
				.collect(toList());
	}

	@Override
	public boolean isNullable(final Function<Symbol, Boolean> nullabilitySolver) {
		return true;
	}

	@Override
	public Set<Verb> getFirsts(final Function<Symbol, Set<Verb>> firstsSolver) {
		return firstsSolver.apply(symbol);
	}


	@SuppressWarnings("unchecked")
	public static List<List<Object>> abbreviate(final List<Object> rawNode, final int fieldCount) {
		final List<List<Object>> $ = new ArrayList<>();
		for (int i = 0; i < fieldCount; ++i)
			$.add(new ArrayList<>());
		List<Object> currentRawNode = rawNode;
		while (!currentRawNode.isEmpty()) {
			assert currentRawNode.size() == fieldCount + 1;
			final List<Object> rawArguments = currentRawNode.subList(0, fieldCount);
			for (int i = 0; i < fieldCount; ++i)
				$.get(i).add(rawArguments.get(i));
			currentRawNode = (List<Object>) currentRawNode.get(fieldCount);
		}
		return $;
	}

	@Override
	String marker() {
		return "?";
	}

}
