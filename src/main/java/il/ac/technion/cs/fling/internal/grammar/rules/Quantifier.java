package il.ac.technion.cs.fling.internal.grammar.rules;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.FieldNode.FieldNodeFragment;

public abstract class Quantifier implements Component {
  public abstract Stream<Symbol> symbols();

  public abstract Variable expand(Namer namer, Consumer<Variable> variableDeclaration, Consumer<ERule> ruleDeclaration);

  public abstract List<FieldNodeFragment> getFields(Function<Component, List<FieldNodeFragment>> fieldTypesSolver,
      Function<String, String> nameFromBaseSolver);

  public abstract boolean isNullable(Function<Component, Boolean> nullabilitySolver);

  public abstract Set<Token> getFirsts(Function<Component, Set<Token>> firstsSolver);

  public static abstract class Single extends Quantifier {
    @Override public Stream<Symbol> symbols() {
      return Stream.of(symbol);
    }

    public final Symbol symbol;

    public Single(final Symbol symbol) {
      Objects.requireNonNull(symbol);
      this.symbol = symbol;
    }

    public final Symbol symbol() {
      return symbol;
    }

    @Override public final String name() {
      return symbol.name() + marker();
    }

    @Override public final String toString() {
      return symbol + marker();
    }

    public abstract String marker();

  }
}
