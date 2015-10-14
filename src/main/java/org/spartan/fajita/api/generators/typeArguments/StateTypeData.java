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
import org.spartan.fajita.api.parser.ActionTable.Error;
import org.spartan.fajita.api.parser.Item;
import org.spartan.fajita.api.parser.LRParser;
import org.spartan.fajita.api.parser.State;

import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;

public class StateTypeData {
  private final State s;
  private final String name;
  private final LRParser parser;
  private final List<Symbol> baseStateSymbols;
  private final int baseStateArgumentNumber;
  // type arguments for the state
  private final Map<InheritedState, TypeVariableName> typeArguments;
  private static final InheritedState stackTypeArgument = new InheritedState(-1, null, null);
  // base state type parameters
  private final Map<Integer, TypeName> baseStateTypes;
  public final DirectedGraph<State, DefaultEdge> dependencies;

  @SuppressWarnings("boxing") public StateTypeData(final LRParser parser, final State s, final List<Symbol> baseStateSymbols) {
    this.parser = parser;
    this.s = s;
    name = "Q" + s.stateIndex;
    this.baseStateSymbols = baseStateSymbols;
    baseStateArgumentNumber = baseStateSymbols.size() + 1;
    typeArguments = calculateStateTypeArguments();
    baseStateTypes = new HashMap<>();
    dependencies = generateDependenciesGraph();
    if (typeArguments.containsKey(stackTypeArgument))
      baseStateTypes.put(0, type(EMPTY_STACK));
    else
      baseStateTypes.put(0, typeArguments.get(stackTypeArgument));
  }
  private DirectedGraph<State, DefaultEdge> generateDependenciesGraph() {
    DefaultDirectedGraph<State, DefaultEdge> $ = new DefaultDirectedGraph<>(DefaultEdge.class);
    for (Symbol lookahead : s.allLegalLookaheads()) {
      if (lookahead == Terminal.$)
        continue;
      State neighbor = parser.getState(s, lookahead);
      $.addVertex(neighbor);
      for (Symbol lookahead2 : neighbor.allLegalLookaheads()) {
        if (lookahead2 == Terminal.$)
          continue;
        State second_neighbor = parser.getState(neighbor, lookahead2);
        $.addVertex(second_neighbor);
        // $.addEdge(neighbor, second_neighbor);
      }
    }
    for (Item i : s.items) {
      if (i.dotIndex > 0 || i.lookahead == Terminal.$ || i.rule.lhs.equals(parser.bnf.getAugmentedStartSymbol()))
        continue;
      State src = parser.getState(parser.getState(s, i.rule.lhs), i.lookahead);
      State dst = parser.getState(s, i.rule.getChildren().get(0));
      $.addEdge(src, dst);
    }
    // TODO: handle cycles case.
    if ($.vertexSet().size() > 0 && new CycleDetector<>($).detectCycles())
      throw new IllegalArgumentException("Cycles are not handled yet, found on Q" + s.stateIndex + ":" + $);
    return $;
  }
  void calculateBaseStateType(final Symbol symb, final StateTypeData nextState) {
    assert(s.goTo(symb).intValue() == nextState.s.stateIndex);
    TypeName[] parameterizedNextState = new TypeName[nextState.typeArguments.size()];
    boolean shiftAction = symb.isNonTerminal() || parser.actionTable(s, ((Terminal) symb)).getClass() == Error.class;
    // stack parameter
    parameterizedNextState[0] = ParameterizedTypeName.get(type(name),
        merge(new TypeName[] { typeArguments.get(stackTypeArgument) }, wildcardArray(typeArguments.size() - 1)));
    // inherited types (named states that current state inherited from parent
    // and passes to child)
    List<InheritedState> localTypeArguments = sortedTypeArguments();
    for (InheritedState nextStateTypeArgument : nextState.sortedTypeArguments()) {
      int contains = localTypeArguments
          .indexOf(new InheritedState(nextStateTypeArgument.depth - 1, nextStateTypeArgument.lhs, nextStateTypeArgument.lookahead));
      if (contains > 0) {
        // inherited types
      } else {
        // generated types
      }
    }
    // generated types (named states that current state knows, and inherits to
    // child)
    Integer BaseTypeindex = new Integer(baseStateSymbols.indexOf(symb) + 1);
    baseStateTypes.put(BaseTypeindex, ParameterizedTypeName.get(type(nextState.name), parameterizedNextState));
  }
  private Map<InheritedState, TypeVariableName> calculateStateTypeArguments() {
    Map<InheritedState, TypeVariableName> $ = new HashMap<>();
    if (s.stateIndex != 0)
      $.put(stackTypeArgument, calculateStackTypeParameter());
    final TypeName baseStateType = ParameterizedTypeName.get(type(BASE_STATE), wildcardArray(baseStateArgumentNumber));
    for (InheritedState i : sortedTypeArguments())
      $.put(i, TypeVariableName.get(i.toString(), baseStateType));
    return $;
  }
  private List<InheritedState> sortedTypeArguments() {
    return s.items.stream().map(i -> new InheritedState(i.dotIndex, i.rule.lhs, i.lookahead)).distinct().sorted()
        .filter(entry -> entry.depth > 0 && entry.lhs != s.bnf.getAugmentedStartSymbol() && entry.lookahead != Terminal.$)
        .collect(Collectors.toList());
  }
  @SuppressWarnings("boxing") private TypeVariableName calculateStackTypeParameter() {
    int max_depth = s.items.stream().map(item -> item.dotIndex).max((x, y) -> Integer.compare(x, y)).get();
    ParameterizedTypeName innerType = recursiveStackParameterCalculator(max_depth);
    return TypeVariableName.get(STACK_TYPE_PARAMETER, innerType);
  }
  private ParameterizedTypeName recursiveStackParameterCalculator(final int max_depth) {
    if (max_depth == 1)
      return ParameterizedTypeName.get(type(BASE_STATE), wildcardArray(baseStateArgumentNumber));
    TypeName[] stackArgument = Arrays.copyOf(
        new TypeName[] { WildcardTypeName.subtypeOf(recursiveStackParameterCalculator(max_depth - 1)) }, baseStateArgumentNumber);
    Arrays.setAll(stackArgument, i -> i == 0 ? stackArgument[0] : WildcardTypeName.subtypeOf(Object.class));
    return ParameterizedTypeName.get(type(BASE_STATE), stackArgument);
  }
  public ParameterizedTypeName getParameterizedBaseType() {
    return ParameterizedTypeName.get(type(BASE_STATE), baseStateTypes.values().toArray(new TypeName[] {}));
  }
  public List<TypeVariableName> getTypeArguments() {
    return typeArguments.values().stream().collect(Collectors.toList());
  }
}
