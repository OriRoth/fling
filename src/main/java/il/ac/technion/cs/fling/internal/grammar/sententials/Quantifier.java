package il.ac.technion.cs.fling.internal.grammar.sententials;

import static java.util.Arrays.asList;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.FieldNode.FieldNodeFragment;

public abstract class Quantifier implements GeneralizedSymbol {
  public abstract Stream<GeneralizedSymbol> symbols();
  public abstract Collection<GeneralizedSymbol> abbreviatedSymbols();
  public abstract Variable expand(Namer namer, Consumer<Variable> variableDeclaration,
      Consumer<DerivationRule> ruleDeclaration);
  public abstract List<FieldNodeFragment> getFields(Function<GeneralizedSymbol, List<FieldNodeFragment>> fieldTypesSolver,
      Function<String, String> nameFromBaseSolver);
  public abstract boolean isNullable(Function<GeneralizedSymbol, Boolean> nullabilitySolver);
  public abstract Set<Verb> getFirsts(Function<GeneralizedSymbol, Set<Verb>> firstsSolver);
  public static abstract class Single extends Quantifier {
    @Override public Stream<GeneralizedSymbol> symbols() {
      return Stream.of(symbol);
    }
    public final GeneralizedSymbol symbol;
    public Single(final GeneralizedSymbol symbol) {
      Objects.requireNonNull(symbol);
      assert !symbol.isQuantifier() : "nested notations are not supported";
      this.symbol = symbol;
    }
    public final GeneralizedSymbol symbol() {
      return symbol;
    }
    @Override public final String name() {
      return symbol.name() + marker();
    }
    public final String toString() {
      return symbol + marker();
    }
    public abstract String marker();
    public final Collection<GeneralizedSymbol> abbreviatedSymbols() {
      return asList(symbol);
    }
  }
}
