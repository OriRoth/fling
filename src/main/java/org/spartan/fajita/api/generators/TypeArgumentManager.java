package org.spartan.fajita.api.generators;

import static org.spartan.fajita.api.generators.GeneratorsUtils.*;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.BASE_STACK;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.BASE_STATE;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.parser.LRParser;

import com.squareup.javapoet.TypeVariableName;

public class TypeArgumentManager {
  private final List<TypeVariableName> baseStateTypeArguments;
  public final List<Symbol> symbols;
  private final LRParser parser;
  private final BNF bnf;

  public TypeArgumentManager(final LRParser parser) {
    this.parser = parser;
    bnf = parser.bnf;
    symbols = initializeSymbolIndexes();
    baseStateTypeArguments = initializeTypes();
  }
  private ArrayList<TypeVariableName> initializeTypes() {
    ArrayList<TypeVariableName> $ = new ArrayList<>();
    // Stack type parameter
    $.add(TypeVariableName.get(STACK_TYPE_PARAMETER, parameterizeWithWildcard(BASE_STACK.typename)));
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
    return baseStateTypeArguments.get(index);
  }
  public TypeVariableName getType(final Symbol s) {
    return baseStateTypeArguments.get(symbols.indexOf(s) + 1);
  }
  public int baseStateArgumentNumber() {
    return baseStateTypeArguments.size();
  }
}
