package il.ac.technion.cs.fling.internal.grammar.rules;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.*;
import java.util.function.*;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.FieldNode.FieldNodeFragment;
import il.ac.technion.cs.fling.internal.grammar.sententials.quantifiers.JavaCompatibleQuantifier;
import il.ac.technion.cs.fling.internal.grammar.types.ClassParameter;

// TODO support nested notations (?).
@JavaCompatibleQuantifier public class NoneOrMore extends Quantifier.Single {
  public NoneOrMore(Symbol symbol) {
    super(symbol);
  }

  @Override public Variable expand(final Namer namer, final Consumer<Variable> variableDeclaration,
      final Consumer<ERule> ruleDeclaration) {
    final Variable head = namer.createQuantificationChild(symbol);
    variableDeclaration.accept(head);
    ruleDeclaration.accept(new ERule(head, asList(//
        new Body(symbol, head), //
        new Body())));
    return head;
  }

  @Override public List<FieldNodeFragment> getFields(final Function<Component, List<FieldNodeFragment>> fieldsSolver,
      @SuppressWarnings("unused") final Function<String, String> nameFromBaseSolver) {
    // TODO manage inner symbol with no fields.
    return fieldsSolver.apply(symbol).stream() //
        .map(innerField -> new FieldNodeFragment( //
            String.format("%s<%s>", //
                List.class.getCanonicalName(), //
                ClassParameter.unPrimitiveType(innerField.parameterType)), //
            innerField.parameterName) {
          @Override public String visitingMethod(final BiFunction<Variable, String, String> variableVisitingSolver,
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

  @SuppressWarnings("unused") @Override public boolean isNullable(final Function<Component, Boolean> nullabilitySolver) {
    return true;
  }

  @Override public Set<Token> getFirsts(final Function<Component, Set<Token>> firstsSolver) {
    return firstsSolver.apply(symbol);
  }

  @SuppressWarnings("unchecked") public static List<List<Object>> abbreviate(final List<Object> rawNode,
      final int fieldCount) {
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

  @Override public String marker() {
    return "*";
  }
}