package il.ac.technion.cs.fling.internal.grammar.rules;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.FieldNode.FieldNodeFragment;
public abstract class Quantifier implements Component {
  public abstract Stream<Symbol> symbols();
  public abstract Variable expand(Linker namer, Consumer<Variable> variableDeclaration,
      Consumer<ERule> ruleDeclaration);
  public abstract List<FieldNodeFragment> getFields(Function<Component, List<FieldNodeFragment>> fieldTypesSolver,
      Function<String, String> nameFromBaseSolver);
  public abstract boolean isNullable(Predicate<Component> nullabilitySolver);
  public abstract Set<Token> getFirsts(Function<List<? extends Component>, Set<Token>> firstsSolver);
  public abstract int fieldCount();
  protected abstract String getVisitingStatement(final Symbol symbol, final //
  BiFunction<Variable, String, String> variableVisitingSolver, //
      final String accessor, final Supplier<String> variableNamesGenerator);
  protected int fieldCount(final Symbol s) {
    assert !s.isTerminal();
    if (s.isToken())
      return s.isParameterized() ? 1 : 0;
    if (s.isVariable())
      return 1;
    if (s.isQuantifier())
      return s.asQuantifier().fieldCount();
    assert false : "unrecognized symbol " + s;
    return 0;
  }
  public abstract static class Sequence extends Quantifier {
    public final List<Symbol> symbols;
    @Override public Stream<Symbol> symbols() {
      return symbols.stream();
    }
    public Sequence(final List<Symbol> symbols) {
      this.symbols = Collections.unmodifiableList(symbols);
      verify();
    }
    public abstract String marker();
    public final String name() {
      return String.format("(%s)%s", symbols.size() == 1 ? symbols.get(0) : //
          symbols().map(Object::toString).collect(Collectors.joining(",")), //
          marker());
    }
    @Override public final String toString() {
      return name();
    }
    @Override public int fieldCount() {
      return symbols().mapToInt(this::fieldCount).sum();
    }
    protected void verify() {
      for (final Symbol s : symbols)
        if (fieldCount(s) > 1)
          throw new RuntimeException("complex nested quantifiers are not supported");
      if (symbols.isEmpty() || fieldCount() == 0)
        throw new RuntimeException("unparameterized quantifiers are not supported");
    }
  }
}
