package il.ac.technion.cs.fling.internal.grammar.rules;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.*;
import java.util.function.*;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.FieldNode.FieldNodeFragment;
import il.ac.technion.cs.fling.internal.grammar.sententials.quantifiers.JavaCompatibleQuantifier;
import il.ac.technion.cs.fling.internal.grammar.types.ClassParameter;

@JavaCompatibleQuantifier public class Opt extends Quantifier.Single {
  public Opt(Symbol symbol) {
    super(symbol);
  }

  @Override public Variable expand(final Namer namer, final Consumer<Variable> variableDeclaration,
      final Consumer<ERule> ruleDeclaration) {
    final Variable head = namer.createQuantificationChild(symbol);
    variableDeclaration.accept(head);
    ruleDeclaration.accept(new ERule(head, asList(//
        new Body(symbol), //
        new Body())));
    return head;
  }

  @Override public List<FieldNodeFragment> getFields(final Function<Component, List<FieldNodeFragment>> fieldsSolver,
      @SuppressWarnings("unused") final Function<String, String> nameFromBaseSolver) {
    // TODO manage inner symbol with no fields.
    return fieldsSolver.apply(symbol).stream() //
        .map(innerField -> new FieldNodeFragment( //
            String.format("%s<%s>", //
                java.util.Optional.class.getCanonicalName(), //
                ClassParameter.unPrimitiveType(innerField.parameterType)), //
            innerField.parameterName) {
          @Override public String visitingMethod(final BiFunction<Variable, String, String> variableVisitingSolver,
              final String accessor, final Supplier<String> variableNamesGenerator) {
            if (!symbol.isVariable())
              return null;
            final String streamingVariable = variableNamesGenerator.get();
            return String.format("%s.ifPresent(%s->%s)", //
                accessor, //
                streamingVariable, //
                variableVisitingSolver.apply(symbol.asVariable(), streamingVariable));
          }
        }) //
        .collect(toList());
  }

  @Override public boolean isNullable(@SuppressWarnings("unused") final Function<Component, Boolean> nullabilitySolver) {
    return true;
  }

  @Override public Set<Token> getFirsts(final Function<Component, Set<Token>> firstsSolver) {
    return firstsSolver.apply(symbol);
  }

  public static List<java.util.Optional<Object>> abbreviate(final List<Object> rawNode, final int fieldCount) {
    // TODO support many fields
    assert fieldCount == 1;
    return asList(rawNode.isEmpty() ? java.util.Optional.empty() : java.util.Optional.of(rawNode.get(0)));
  }

  @Override public String marker() {
    return "?";
  }
}
