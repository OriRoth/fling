package org.spartan.fajita.api.generators.typeArguments;

import static org.spartan.fajita.api.generators.GeneratorsUtils.*;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis;
import org.spartan.fajita.api.parser.LRParser;
import org.spartan.fajita.api.parser.State;

import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
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
    $.remove(Terminal.$);
    $.remove(bnf.getAugmentedStartSymbol());
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
  public static void main(final String[] args) {
    LRParser parser = BalancedParenthesis.buildBNF();
    TypeArgumentManager tam = new TypeArgumentManager(parser);
    final List<TypeSpec> $ = new ArrayList<>();
    parser.getStates()
        .forEach(s -> $.add(TypeSpec.classBuilder("Q" + s.stateIndex).addTypeVariables(tam.stateTypeArguments(s)).build()));
    for (TypeSpec typeSpec : $)
      if (!typeSpec.name.equals("Q0"))
        System.out.println(typeSpec);
    // parser.states.forEach(s ->
    // System.out.println(tam.statesTypeData.get(s).dependencies));
  }
}
