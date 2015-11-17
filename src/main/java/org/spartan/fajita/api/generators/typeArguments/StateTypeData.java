package org.spartan.fajita.api.generators.typeArguments;

import static org.spartan.fajita.api.generators.GeneratorsUtils.STACK_TYPE_PARAMETER;
import static org.spartan.fajita.api.generators.GeneratorsUtils.type;
import static org.spartan.fajita.api.generators.GeneratorsUtils.wildcardArray;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.BASE_STATE;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.EMPTY_STACK;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.parser.State;

import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;

public class StateTypeData {
  final State state;
  private final int baseTASize;
  // type arguments for the state
  private final LinkedHashMap<InheritedParameter, TypeVariableName> typeParameters;
  static final InheritedParameter stackTP = new InheritedParameter(-1, null, null);
  // base state type parameters
  private final Map<Symbol, TypeName> baseTAs;

  public StateTypeData(final State s, final List<Symbol> baseStateSymbols) {
    state = s;
    baseTASize = baseStateSymbols.size() + 1;
    typeParameters = setFormalParametersTypes();
    baseTAs = new HashMap<>();
    if (typeParameters.containsKey(stackTP))
      baseTAs.put(SpecialSymbols.$, type(EMPTY_STACK));
    else
      baseTAs.put(SpecialSymbols.$, getFormalParameter(stackTP));
  }
  private LinkedHashMap<InheritedParameter, TypeVariableName> setFormalParametersTypes() {
    LinkedHashMap<InheritedParameter, TypeVariableName> $ = new LinkedHashMap<>();
    List<InheritedParameter> formalParametersList = state.getItems().stream()
        .map(i1 -> new InheritedParameter(i1.dotIndex, i1.rule.lhs, i1.lookahead)).distinct().sorted()
        .filter(entry -> entry.depth > 0 && entry.lhs != SpecialSymbols.augmentedStartSymbol && entry.lookahead != SpecialSymbols.$)
        .collect(Collectors.toList());
    if (!state.isInitial())
      $.put(stackTP, calculateStackTypeParameter());
    final TypeName baseStateType = ParameterizedTypeName.get(type(BASE_STATE), wildcardArray(baseTASize));
    for (InheritedParameter i : formalParametersList)
      $.put(i, TypeVariableName.get(i.toString(), baseStateType));
    return $;
  }
  List<InheritedParameter> getInheritedParameters() {
    ArrayList<InheritedParameter> arrayList = new ArrayList<>(typeParameters.keySet());
    arrayList.remove(stackTP);
    return arrayList; 
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
  void setBaseType(final Symbol symb, final TypeName type) {
    baseTAs.put(symb, type);
  }
  TypeVariableName getFormalParameter(final InheritedParameter data) {
    return typeParameters.get(data);
  }
  List<TypeVariableName> getFormalParameters() {
    return typeParameters.values().stream().collect(Collectors.toList());
  }
  ParameterizedTypeName getBaseType() {
    return ParameterizedTypeName.get(type(BASE_STATE), baseTAs.values().toArray(new TypeName[] {}));
  }
}
