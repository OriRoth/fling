package il.ac.technion.cs.fling.internal.grammar.sententials;

import static java.util.Arrays.asList;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.FieldNode.FieldNodeFragment;

public abstract class Quantifier implements Symbol {
  public abstract Stream<Symbol> symbols();

  public abstract Collection<Symbol> abbreviatedSymbols();

  public abstract Variable expand(Namer namer, Consumer<Variable> variableDeclaration,
      Consumer<ERule> ruleDeclaration);

  public abstract List<FieldNodeFragment> getFields(Function<Symbol, List<FieldNodeFragment>> fieldTypesSolver,
      Function<String, String> nameFromBaseSolver);

  public abstract boolean isNullable(Function<Symbol, Boolean> nullabilitySolver);

  public abstract Set<Token> getFirsts(Function<Symbol, Set<Token>> firstsSolver);

  public static abstract class Single extends Quantifier {
    @Override public Stream<Symbol> symbols() {
      return Stream.of(symbol);
    }

    public final Symbol symbol;

    public Single(final Symbol symbol) {
      Objects.requireNonNull(symbol);
      assert !symbol.isQuantifier() : "nested quantifiers are not supported";
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

    @Override public final Collection<Symbol> abbreviatedSymbols() {
      return asList(symbol);
    }
  }
}
