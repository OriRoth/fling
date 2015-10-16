package org.spartan.fajita.api.generators.typeArguments;

import static org.spartan.fajita.api.generators.GeneratorsUtils.*;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.parser.Item;
import org.spartan.fajita.api.parser.LRParser;
import org.spartan.fajita.api.parser.State;

import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;

public class StateTypeData {
  final State state;
  private final LRParser parser;
  private final int baseTypeArgumentNumber;
  // type arguments for the state
  private final Map<InheritedState, TypeVariableName> typeArguments;
  public static final InheritedState stackTypeArgument = new InheritedState(-1, null, null);
  // base state type parameters
  private final Map<Integer, TypeName> baseStateTypes;
  public final DirectedGraph<State, DefaultEdge> dependencies;

  @SuppressWarnings("boxing") public StateTypeData(final LRParser parser, final State s, final List<Symbol> baseStateSymbols) {
    this.parser = parser;
    state = s;
    baseTypeArgumentNumber = baseStateSymbols.size() + 1;
    typeArguments = calculateStateTypeArguments();
    baseStateTypes = new HashMap<>();
    dependencies = generateDependenciesGraph();
    if (typeArguments.containsKey(stackTypeArgument))
      baseStateTypes.put(0, type(EMPTY_STACK));
    else
      baseStateTypes.put(0, getTypeArgument(stackTypeArgument));
  }
  private DirectedGraph<State, DefaultEdge> generateDependenciesGraph() {
    DefaultDirectedGraph<State, DefaultEdge> $ = new DefaultDirectedGraph<>(DefaultEdge.class);
    for (Symbol lookahead : state.allLegalLookaheads()) {
      if (lookahead == Terminal.$)
        continue;
      State neighbor = state.goTo(lookahead);
      $.addVertex(neighbor);
      for (Symbol lookahead2 : neighbor.allLegalLookaheads()) {
        if (lookahead2 == Terminal.$)
          continue;
        State second_neighbor = neighbor.goTo(lookahead2);
        $.addVertex(second_neighbor);
        // $.addEdge(neighbor, second_neighbor);
      }
    }
    for (Item i : state.getItems()) {
      if (i.dotIndex > 0 || i.lookahead == Terminal.$ || i.rule.lhs.equals(parser.bnf.getAugmentedStartSymbol()))
        continue;
      // By rule (1)
      State src = state.goTo(i.rule.lhs).goTo(i.lookahead);
      List<Symbol> ruleRHS = i.rule.getChildren();
      State dst = state.goTo(ruleRHS.get(0));
      $.addEdge(src, dst);
      // By rule (2)
      if (ruleRHS.size() == 2 && ruleRHS.get(0).isNonTerminal() && ruleRHS.get(1).isTerminal()) {
        State dst2 = state.goTo(ruleRHS.get(0)).goTo(ruleRHS.get(1));
        $.addEdge(src, dst2);
      }
    }
    // TODO: handle cycles case.
    if ($.vertexSet().size() > 0 && new CycleDetector<>($).detectCycles())
      throw new IllegalArgumentException("Cycles are not handled yet, found on " + state.name + ":" + $);
    return $;
  }
  private Map<InheritedState, TypeVariableName> calculateStateTypeArguments() {
    Map<InheritedState, TypeVariableName> $ = new HashMap<>();
    if (state != parser.getInitialState())
      $.put(stackTypeArgument, calculateStackTypeParameter());
    final TypeName baseStateType = ParameterizedTypeName.get(type(BASE_STATE), wildcardArray(baseTypeArgumentNumber));
    for (InheritedState i : sortedTypeArguments())
      $.put(i, TypeVariableName.get(i.toString(), baseStateType));
    return $;
  }
  List<InheritedState> sortedTypeArguments() {
    return state.getItems().stream().map(i -> new InheritedState(i.dotIndex, i.rule.lhs, i.lookahead)).distinct().sorted()
        .filter(entry -> entry.depth > 0 && entry.lhs != parser.bnf.getAugmentedStartSymbol() && entry.lookahead != Terminal.$)
        .collect(Collectors.toList());
  }
  @SuppressWarnings("boxing") private TypeVariableName calculateStackTypeParameter() {
    int max_depth = state.getItems().stream().map(item -> item.dotIndex).max((x, y) -> Integer.compare(x, y)).get();
    ParameterizedTypeName innerType = recursiveStackParameterCalculator(max_depth);
    return TypeVariableName.get(STACK_TYPE_PARAMETER, innerType);
  }
  private ParameterizedTypeName recursiveStackParameterCalculator(final int max_depth) {
    if (max_depth == 1)
      return ParameterizedTypeName.get(type(BASE_STATE), wildcardArray(baseTypeArgumentNumber));
    TypeName[] stackArgument = Arrays.copyOf(
        new TypeName[] { WildcardTypeName.subtypeOf(recursiveStackParameterCalculator(max_depth - 1)) }, baseTypeArgumentNumber);
    Arrays.setAll(stackArgument, i -> i == 0 ? stackArgument[0] : WildcardTypeName.subtypeOf(Object.class));
    return ParameterizedTypeName.get(type(BASE_STATE), stackArgument);
  }
  public ParameterizedTypeName getParameterizedBaseType() {
    return ParameterizedTypeName.get(type(BASE_STATE), baseStateTypes.values().toArray(new TypeName[] {}));
  }
  public void setBaseType(final int index, final TypeName type) {
    baseStateTypes.put(new Integer(index), type);
  }
  public TypeVariableName getTypeArgument(final InheritedState data) {
    return typeArguments.get(data);
  }
  public int getTypeArgumentNumber() {
    return typeArguments.size();
  }
  public List<TypeVariableName> getTypeArguments() {
    return typeArguments.values().stream().collect(Collectors.toList());
  }
}
