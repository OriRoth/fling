package org.spartan.fajita.api.generators.typeArguments;

import static org.spartan.fajita.api.generators.GeneratorsUtils.STACK_TYPE_PARAMETER;
import static org.spartan.fajita.api.generators.GeneratorsUtils.type;
import static org.spartan.fajita.api.generators.GeneratorsUtils.wildcardArray;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.BASE_STATE;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.EMPTY_STACK;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.parser.LRParser;
import org.spartan.fajita.api.parser.State;

import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;

public class StateTypeData {
  final State state;
  private final LRParser parser;
  private final int baseTASize;
  // type arguments for the state
  private final Map<InheritedState, TypeVariableName> typeParameters;
  public static final InheritedState stackTP = new InheritedState(-1, null, null);
  // base state type parameters
  private final Map<Symbol, TypeName> baseStateTAs;

  public StateTypeData(final LRParser parser, final State s, final List<Symbol> baseStateSymbols) {
    this.parser = parser;
    state = s;
    baseTASize = baseStateSymbols.size() + 1;
    typeParameters = calculateFormalParameters();
    baseStateTAs = new HashMap<>();
    if (typeParameters.containsKey(stackTP))
      baseStateTAs.put(SpecialSymbols.$, type(EMPTY_STACK));
    else
      baseStateTAs.put(SpecialSymbols.$, getFormalParameter(stackTP));
  }
  private Map<InheritedState, TypeVariableName> calculateFormalParameters() {
    Map<InheritedState, TypeVariableName> $ = new HashMap<>();
    if (state != parser.getInitialState())
      $.put(stackTP, calculateStackTypeParameter());
    final TypeName baseStateType = ParameterizedTypeName.get(type(BASE_STATE), wildcardArray(baseTASize));
    for (InheritedState i : sortedTypeArguments())
      $.put(i, TypeVariableName.get(i.toString(), baseStateType));
    return $;
  }
  List<InheritedState> sortedTypeArguments() {
    return state.getItems().stream().map(i -> new InheritedState(i.dotIndex, i.rule.lhs, i.lookahead)).distinct().sorted()
        .filter(entry -> entry.depth > 0 && entry.lhs != SpecialSymbols.augmentedStartSymbol && entry.lookahead != SpecialSymbols.$)
        .collect(Collectors.toList());
  }
  @SuppressWarnings("boxing") private TypeVariableName calculateStackTypeParameter() {
    int max_depth = state.getItems().stream().map(item -> item.dotIndex).max((x, y) -> Integer.compare(x, y)).get();
    ParameterizedTypeName innerType = recursiveStackParameterCalculator(max_depth);
    return TypeVariableName.get(STACK_TYPE_PARAMETER, innerType);
  }
  private ParameterizedTypeName recursiveStackParameterCalculator(final int max_depth) {
    if (max_depth == 1)
      return ParameterizedTypeName.get(type(BASE_STATE), wildcardArray(baseTASize));
    TypeName[] stackArgument = Arrays
        .copyOf(new TypeName[] { WildcardTypeName.subtypeOf(recursiveStackParameterCalculator(max_depth - 1)) }, baseTASize);
    Arrays.setAll(stackArgument, i -> i == 0 ? stackArgument[0] : WildcardTypeName.subtypeOf(Object.class));
    return ParameterizedTypeName.get(type(BASE_STATE), stackArgument);
  }
  public void setBaseType(final Symbol symb, final TypeName type) {
    baseStateTAs.put(symb, type);
  }
  public TypeVariableName getFormalParameter(final InheritedState data) {
    return typeParameters.get(data);
  }
  public int getFormalParametersNumber() {
    return typeParameters.size();
  }
  public List<TypeVariableName> getFormalParameters() {
    return typeParameters.values().stream().collect(Collectors.toList());
  }
  public ParameterizedTypeName getBaseType() {
    return ParameterizedTypeName.get(type(BASE_STATE), baseStateTAs.values().toArray(new TypeName[] {}));
  }
}
