package il.ac.technion.cs.fling.internal.grammar.rules;
import static java.util.Arrays.asList;
import java.util.*;
import java.util.function.*;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.FieldNode.FieldNodeFragment;
import il.ac.technion.cs.fling.internal.grammar.sententials.quantifiers.JavaCompatibleQuantifier;
import il.ac.technion.cs.fling.internal.grammar.types.ClassParameter;
import il.ac.technion.cs.fling.namers.VariableGenerator;
@JavaCompatibleQuantifier public class Opt extends Quantifier.Sequence {
  public Opt(final List<Symbol> symbols) {
    super(symbols);
  }
  @Override public Variable expand(final VariableGenerator g, final Consumer<? super Variable> variableDeclaration,
      final Consumer<? super ERule> ruleDeclaration) {
    final List<Component> expandedSymbols = new ArrayList<>();
    //
    for (final Symbol s : symbols)
      expandedSymbols.add(s.isQuantifier() ? s.asQuantifier().expand(g, variableDeclaration, ruleDeclaration) : s);
    final var head = g.fresh(symbols);
    variableDeclaration.accept(head);
    ruleDeclaration.accept(new ERule(head, asList(new Body(expandedSymbols), new Body())));
    return head;
  }
  @Override protected String getVisitingStatement(final Symbol symbol,
                                                  final BiFunction<? super Variable, ? super String, String> variableVisitingSolver, final String accessor,
                                                  final Supplier<String> variableNamesGenerator) {
    if (!symbol.isVariable() && !symbol.isQuantifier())
      return null;
    final var streamingVariable = variableNamesGenerator.get();
    final var action = symbol.isVariable() ? //
        variableVisitingSolver.apply(symbol.asVariable(), streamingVariable) : //
        String.format("{%s}", symbol.asQuantifier().symbols() //
            .map(s -> s.asQuantifier().getVisitingStatement(s, variableVisitingSolver, streamingVariable,
                variableNamesGenerator)));
    return String.format("{%s.ifPresent(%s->%s);}", //
        accessor, //
        streamingVariable, //
        action);
  }
  @Override public List<FieldNodeFragment> getFields(final Function<Component, ? extends List<FieldNodeFragment>> fieldsSolver,
      @SuppressWarnings("unused") final Function<String, String> nameFromBaseSolver) {
    final List<FieldNodeFragment> $ = new ArrayList<>();
    for (final Symbol symbol : symbols)
      for (final FieldNodeFragment rawField : fieldsSolver.apply(symbol))
        $.add(new FieldNodeFragment( //
            String.format("%s<%s>", //
                Optional.class.getCanonicalName(), //
                ClassParameter.unPrimitiveType(rawField.parameterType)), //
            rawField.parameterName) {
          @Override public String visitingStatement(
                  final BiFunction<? super Variable, ? super String, String> variableVisitingSolver, final String accessor,
                  final Supplier<String> variableNamesGenerator) {
            return getVisitingStatement(symbol, variableVisitingSolver, accessor, variableNamesGenerator);
          }
        });
    return $;
  }
  @Override public boolean isNullable(@SuppressWarnings("unused") final Predicate<? super Component> nullabilitySolver) {
    return true;
  }
  @Override public Set<Token> getFirsts(final Function<List<? extends Component>, ? extends Set<Token>> firstsSolver) {
    return firstsSolver.apply(symbols);
  }
  public static List<Optional<Object>> abbreviate(final List<Object> rawNode, final int fieldCount) {
    final List<Optional<Object>> $ = new ArrayList<>();
    if (rawNode.isEmpty()) {
      for (var i = 0; i < fieldCount; ++i)
        $.add(Optional.empty());
      return $;
    }
    assert rawNode.size() == fieldCount;
    for (var i = 0; i < fieldCount; ++i)
      $.add(Optional.of(rawNode.get(i)));
    return $;
  }
  @Override public String marker() {
    return "?";
  }
}
