package il.ac.technion.cs.fling.internal.grammar.rules;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.FieldNode.FieldNodeFragment;
import il.ac.technion.cs.fling.internal.grammar.sententials.quantifiers.JavaCompatibleQuantifier;
import il.ac.technion.cs.fling.internal.grammar.types.ClassParameter;

// TODO support nested notations (?).
@JavaCompatibleQuantifier public class OneOrMore extends Quantifier.Single {
  public OneOrMore(final Symbol symbol) {
    super(symbol);
  }

  @Override public Variable expand(final Namer namer, final Consumer<Variable> variableDeclaration,
      final Consumer<ERule> ruleDeclaration) {
    final Variable head = namer.createQuantificationChild(symbol);
    final Variable tail = namer.createQuantificationChild(symbol);
    variableDeclaration.accept(head);
    variableDeclaration.accept(tail);
    ruleDeclaration.accept(new ERule(head, asList( //
        new Body(symbol, tail))));
    ruleDeclaration.accept(new ERule(tail, asList(//
        new Body(symbol, tail), //
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

  @Override public boolean isNullable(final Function<Component, Boolean> nullabilitySolver) {
    return nullabilitySolver.apply(symbol);
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
    return "+";
  }
}
