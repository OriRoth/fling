package org.spartan.fajita.api.generators.typeArguments;

import static org.spartan.fajita.api.generators.GeneratorsUtils.*;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.BASE_STATE;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.EMPTY_STACK;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
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
  // type arguments for the state
  private final Map<SimpleEntry<Integer, NonTerminal>, TypeVariableName> typeArguments;
  private static final SimpleEntry<Integer, NonTerminal> stackTypeArgument = new SimpleEntry<>(new Integer(-1), null);
  // base state type parameters
  private final Map<Integer, TypeName> baseStateTypes;

  @SuppressWarnings("boxing") public StateTypeData(final LRParser parser, final State s, final List<Symbol> baseStateSymbols) {
    this.parser = parser;
    this.s = s;
    name = "Q" + s.stateIndex;
    this.baseStateSymbols = baseStateSymbols;
    typeArguments = calculateStateTypeArguments();
    baseStateTypes = new HashMap<>();
    if (typeArguments.containsKey(stackTypeArgument))
      baseStateTypes.put(0, type(EMPTY_STACK));
    else
      baseStateTypes.put(0, typeArguments.get(stackTypeArgument));
  }
  @SuppressWarnings("boxing") void calculateBaseStateType(final Symbol symb, final StateTypeData nextState) {
    assert(s.goTo(symb).intValue() == nextState.s.stateIndex);
    TypeName[] parameterizedNextState = new TypeName[nextState.typeArguments.size()];
    parameterizedNextState[0] = ParameterizedTypeName.get(type(nextState.name),
        merge(new TypeName[] { typeArguments.get(stackTypeArgument) }, wildcardArray(nextState.typeArguments.size() - 1)));
    // inherited types (named states that current state inherited from parent
    // and passes to child)
    List<SimpleEntry<Integer, NonTerminal>> localTypeArguments = sortedTypeArguments();
    for (SimpleEntry<Integer, NonTerminal> entry : nextState.sortedTypeArguments()) {
      int contains = localTypeArguments.indexOf(new SimpleEntry<>(entry.getKey() - 1, entry.getValue()));
      if (contains > 0) {
        // inherited types
      } else {
        // generated types
      }
    }
    // generated types (named states that current state knows, and inherits to
    // child)
  }
  private Map<SimpleEntry<Integer, NonTerminal>, TypeVariableName> calculateStateTypeArguments() {
    Map<SimpleEntry<Integer, NonTerminal>, TypeVariableName> $ = new HashMap<>();
    if (s.items.stream().anyMatch(i -> i.rule.lhs.equals(s.bnf.getAugmentedStartSymbol())))
      return $;
    $.put(stackTypeArgument, calculateStackTypeParameter());
    for (SimpleEntry<Integer, NonTerminal> i : sortedTypeArguments())
      $.put(i, TypeVariableName.get(i.getValue().name() + i.getKey(),
          ParameterizedTypeName.get(type(BASE_STATE), wildcardArray(baseStateSymbols.size()))));
    return $;
  }
  @SuppressWarnings("boxing") private List<SimpleEntry<Integer, NonTerminal>> sortedTypeArguments() {
    return s.items.stream().map(i -> new AbstractMap.SimpleEntry<>(i.dotIndex, i.rule.lhs)).distinct().sorted((x, y) -> {
      return (x.getKey() != y.getKey()) ? Integer.compare(x.getKey(), y.getKey())
          : x.getValue().name().compareTo(y.getValue().name());
    }).filter(entry -> entry.getKey().intValue() > 0).collect(Collectors.toList());
  }
  @SuppressWarnings("boxing") private TypeVariableName calculateStackTypeParameter() {
    int max_depth = s.items.stream().map(item -> item.dotIndex).max((x, y) -> Integer.compare(x, y)).get();
    ParameterizedTypeName innerType = recursiveStackParameterCalculator(max_depth);
    return TypeVariableName.get(STACK_TYPE_PARAMETER, innerType);
  }
  private ParameterizedTypeName recursiveStackParameterCalculator(final int max_depth) {
    if (max_depth == 1)
      return ParameterizedTypeName.get(type(BASE_STATE), wildcardArray(baseStateSymbols.size()));
    TypeName[] stackArgument = Arrays.copyOf(
        new TypeName[] { WildcardTypeName.subtypeOf(recursiveStackParameterCalculator(max_depth - 1)) }, baseStateSymbols.size());
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
