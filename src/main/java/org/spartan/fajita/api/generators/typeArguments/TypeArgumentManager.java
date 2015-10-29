package org.spartan.fajita.api.generators.typeArguments;

import static org.spartan.fajita.api.generators.GeneratorsUtils.*;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.parser.LRParser;
import org.spartan.fajita.api.parser.State;

import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

public class TypeArgumentManager {
  private final List<TypeVariableName> baseTypeArgumentsList;
  public final List<Symbol> symbols;
  private final LRParser parser;
  private final BNF bnf;
  // TODO: change to private
  public final Map<State, StateTypeData> statesTypeData;

  public TypeArgumentManager(final LRParser parser) {
    this.parser = parser;
    bnf = parser.bnf;
    symbols = initializeSymbolIndexes();
    baseTypeArgumentsList = initializeBaseTypeArgumentList();
    statesTypeData = new HashMap<>();
    parser.getStates().forEach(s -> statesTypeData.put(s, new StateTypeData(parser, s, symbols)));
  }
  private ArrayList<TypeVariableName> initializeBaseTypeArgumentList() {
    ArrayList<TypeVariableName> $ = new ArrayList<>();
    // Stack type parameter
    $.add(TypeVariableName.get(STACK_TYPE_PARAMETER, ParameterizedTypeName.get(type(BASE_STACK), wildcardArray(1))));
    // symbol type parameters
    for (Symbol s : symbols)
      $.add(TypeVariableName.get(s.name(), type(BASE_STATE)));
    return $;
  }
  private List<Symbol> initializeSymbolIndexes() {
    List<Symbol> $ = new LinkedList<>();
    $.addAll(bnf.getTerminals());
    $.addAll(bnf.getNonTerminals());
    $.remove(SpecialSymbols.$);
    $.remove(SpecialSymbols.augmentedStartSymbol);
    return $;
  }
  public TypeVariableName getType(final int index) {
    return baseTypeArgumentsList.get(index);
  }
  public TypeVariableName getType(final Symbol s) {
    return baseTypeArgumentsList.get(symbols.indexOf(s) + 1);
  }
  public int baseStateArgumentNumber() {
    return baseTypeArgumentsList.size();
  }
  public List<TypeVariableName> stateTypeArguments(final State s) {
    return statesTypeData.get(s).getTypeArguments();
  }
  private void calculateBaseTypes(final State state) {
    StateTypeData typeDatum = statesTypeData.get(state);
    Map<State, TypeName> relativeInstantiations = new HashMap<>();
    TopologicalOrderIterator<State, DefaultEdge> iter = new TopologicalOrderIterator<>(typeDatum.dependencies);
    while (iter.hasNext()) {
      State next = iter.next();
      Optional<Symbol> transition = state.allLegalLookaheads().stream().filter(s -> state.goTo(s) == next).findAny();
      if (transition.isPresent())
        calculateBaseType(state, transition.get());
      else {
        // this state has distance 2 from `state` and has no base type argument
      }
    }
  }
  private void calculateBaseType(final State s, final Symbol symb) {
    StateTypeData stateData = statesTypeData.get(s);
    StateTypeData nextStateData = statesTypeData.get(s.goTo(symb));
    TypeName $ = null;
    if (symb.isNonTerminal() || parser.actionTable(s, ((Terminal) symb)).isShift()) {
      TypeName parameters[] = calculateShiftAction(stateData, nextStateData);
      $ = ParameterizedTypeName.get(type(nextStateData.state.name), parameters);
    } else if (parser.actionTable(s, (Terminal) symb).isReduce()) {
      TypeName parameters[] = calculateReduceAction(stateData, nextStateData);
      $ = ParameterizedTypeName.get(type(nextStateData.state.name), parameters);
    } else if (parser.actionTable(s, (Terminal) symb).isError())
      $ = type(ERROR_STATE);
    stateData.setBaseType(symbols.indexOf(symb) + 1, $);
  }
  private TypeName[] calculateReduceAction(final StateTypeData stateData, final StateTypeData nextStateData) {
    // TODO Auto-generated method stub
    return null;
  }
  private TypeName[] calculateShiftAction(final StateTypeData stateData, final StateTypeData nextStateData) {
    TypeName[] $ = new TypeName[nextStateData.getTypeArgumentNumber()];
    // stack parameter
    $[0] = ParameterizedTypeName.get(type(stateData.state.name),
        merge(new TypeName[] { stateData.getTypeArgument(StateTypeData.stackTypeArgument) },
            wildcardArray(stateData.getTypeArgumentNumber() - 1)));
    int index = 1;
    List<InheritedState> localTypeArguments = stateData.sortedTypeArguments();
    for (InheritedState inheritedData : nextStateData.sortedTypeArguments()) {
      InheritedState corresponding = new InheritedState(inheritedData.depth - 1, inheritedData.lhs, inheritedData.lookahead);
      if (localTypeArguments.contains(corresponding))
        // inherited types
        $[index++] = stateData.getTypeArgument(corresponding);
      else {
        // generated types
        assert(inheritedData.depth == 1);
        State afterReduce = stateData.state.goTo(inheritedData.lhs).goTo(inheritedData.lookahead);
        TypeName[] afterReduceParameters = calculateDoubleShiftType();
        $[index++] = ParameterizedTypeName.get(type(afterReduce.name), afterReduceParameters);
      }
    }
    return $;
  }
  private TypeName[] calculateDoubleShiftAction(final Symbol symb1, final Symbol symb2, final StateTypeData shift1,
      final StateTypeData shift2) {
    // move all of the calculation methods to the scope of the TAM, all of the
    // type data are required.
    // maybe the symbols are redundant in all those calculation methods.
    //
    return null;
  }
}
