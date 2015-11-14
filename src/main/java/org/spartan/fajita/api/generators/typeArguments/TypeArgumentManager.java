package org.spartan.fajita.api.generators.typeArguments;

import static org.spartan.fajita.api.generators.GeneratorsUtils.*;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.parser.ActionTable.Action;
import org.spartan.fajita.api.parser.ActionTable.Reduce;
import org.spartan.fajita.api.parser.ActionTable.Shift;
import org.spartan.fajita.api.parser.Item;
import org.spartan.fajita.api.parser.LRParser;
import org.spartan.fajita.api.parser.State;

import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

public class TypeArgumentManager {
  /**
   * The type arguments in BaseState.
   */
  private final List<TypeVariableName> baseTAList;
  public final List<Symbol> symbols;
  private final LRParser parser;
  private final BNF bnf;
  // TODO: change to private
  public final Map<State, StateTypeData> statesTypeData;
  public final DirectedGraph<ContextedState, DefaultEdge> dependencies;

  public TypeArgumentManager(final LRParser parser) {
    this.parser = parser;
    bnf = parser.bnf;
    symbols = initializeSymbolIndexes();
    baseTAList = initializeBaseTypeArgumentList();
    dependencies = generateDependenciesGraph();
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
    return baseTAList.get(index);
  }
  public TypeVariableName getType(final Symbol s) {
    return baseTAList.get(symbols.indexOf(s) + 1);
  }
  public int baseStateArgumentNumber() {
    return baseTAList.size();
  }
  public List<TypeVariableName> stateTypeArguments(final State s) {
    return statesTypeData.get(s).getFormalParameters();
  }
  private void calculateBaseTypes(final State state) {
    StateTypeData typeDatum = statesTypeData.get(state);
    Map<State, TypeName> contexedInstantiations = new HashMap<>();
    TopologicalOrderIterator<ContextedState, DefaultEdge> iter = new TopologicalOrderIterator<>(typeDatum.dependencies);
    iter.forEachRemaining(s -> calculateBaseType(s, contexedInstantiations));
  }
  private void calculateBaseType(final ContextedState s, final Map<State, TypeName> contexedInstantiations) {
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
    TypeName[] $ = new TypeName[nextStateData.getFormalParametersNumber()];
    // stack parameter
    $[0] = ParameterizedTypeName.get(type(stateData.state.name),
        merge(new TypeName[] { stateData.getFormalParameter(StateTypeData.stackTP) },
            wildcardArray(stateData.getFormalParametersNumber() - 1)));
    int index = 1;
    List<InheritedState> localTypeArguments = stateData.sortedTypeArguments();
    for (InheritedState inheritedData : nextStateData.sortedTypeArguments()) {
      InheritedState corresponding = new InheritedState(inheritedData.depth - 1, inheritedData.lhs, inheritedData.lookahead);
      if (localTypeArguments.contains(corresponding))
        // inherited types
        $[index++] = stateData.getFormalParameter(corresponding);
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
  private DirectedGraph<ContextedState, DefaultEdge> generateDependenciesGraph() {
    final DefaultDirectedGraph<ContextedState, DefaultEdge> $ = new DefaultDirectedGraph<>(DefaultEdge.class);
    for (State q_B : parser.getStates())
      addStateDependencies(q_B, $);
    System.out.println($);
    // TODO: handle cycles case.
    if ($.vertexSet().size() > 0 && new CycleDetector<>($).detectCycles())
      throw new IllegalArgumentException("Cycles are not handled yet, found on " + q_B.name + ":" + $);
    return $;
  }
  private void addStateDependencies(final State q_B, final DefaultDirectedGraph<ContextedState, DefaultEdge> g) {
    Set<Item> items = q_B.getItems().stream()
        .filter(i -> i.dotIndex == 0 && i.lookahead != SpecialSymbols.$ && i.rule.lhs != SpecialSymbols.augmentedStartSymbol)
        .collect(Collectors.toSet());
    for (Item i : items) {
      // By rule (1)
      Optional<ContextedState> isNotInherited = find(q_B, i.rule.lhs, i.lookahead);
      if (!isNotInherited.isPresent()) // inherited TA
        continue;
      ContextedState q_Ab$B = isNotInherited.get();
      List<Symbol> rhs = i.rule.getChildren();
      /************************************
       * if (rhs.size() == 0) ;// epsilon rule , what to do?
       * rhs.add(i.lookahead);
       ***********************************/
      Symbol X = rhs.get(0);
      ContextedState q_X$B = new ContextedState(q_B.goTo(X), q_B, X);
      addEdge(g, q_Ab$B, q_X$B);
      // By rule (2)
      if (rhs.size() == 2 && rhs.get(0).isNonTerminal() && rhs.get(1).isTerminal()) {
        NonTerminal C = (NonTerminal) rhs.get(0);
        Terminal d = (Terminal) rhs.get(1);
        ContextedState q_Cd$B = find(q_B, C, d).get();
        addEdge(g, q_Ab$B, q_Cd$B);
      }
    }
    // By rules (3) and (4)
    new HashSet<>(g.vertexSet()).stream().filter(v -> v.transitions.length == 2 && v.context.equals(q_B)).forEach(q_Ab$B -> {
      NonTerminal A = (NonTerminal) q_Ab$B.transitions[0];
      Terminal b = (Terminal) q_Ab$B.transitions[1];
      ContextedState q_A$B = new ContextedState(q_B.goTo(A), q_B, A);
      addEdge(g, q_A$B, q_Ab$B);
      Optional<ContextedState> q_b$A = find(q_B.goTo(A), b);
      if (q_b$A.isPresent())
        addEdge(g, q_b$A.get(), q_Ab$B);
      /**
       * else ;// the state is known in a higher place. maybe Q_b know about it.
       */
    });
  }
  private static void addEdge(final DirectedGraph<ContextedState, DefaultEdge> g, final ContextedState src,
      final ContextedState dst) {
    g.addVertex(src);
    g.addVertex(dst);
    g.addEdge(src, dst);
  }
  private Optional<ContextedState> find(final State q_B, final NonTerminal A, final Terminal b) {
    Action action = parser.actionTable(q_B.goTo(A), b);
    if (action.isShift())
      return Optional.of(new ContextedState(((Shift) action).state, q_B, A, b));
    // reduce
    DerivationRule rule = ((Reduce) action).item.rule;
    List<Symbol> alpha = rule.getChildren();
    NonTerminal C = rule.lhs;
    if (alpha.size() > 1)
      return Optional.empty();
    if (alpha.size() == 1) // alpha = A
      return find(q_B, C, b);
    return find(q_B.goTo(A), C, b);
  }
  private Optional<ContextedState> find(final State q_B, final Terminal b) {
    Action action = parser.actionTable(q_B, b);
    if (action.isShift())
      return Optional.of(new ContextedState(((Shift) action).state, q_B, b));
    // reduce
    DerivationRule rule = ((Reduce) action).item.rule;
    List<Symbol> alpha = rule.getChildren();
    NonTerminal C = rule.lhs;
    if (alpha.size() > 0)
      return Optional.empty();
    return find(q_B, C, b);
  }

  private static class ContextedState {
    public final State context;
    public final State state;
    public final Symbol[] transitions;

    public ContextedState(final State actual, final State context, final Symbol... transitions) {
      state = actual;
      this.context = context;
      this.transitions = transitions;
    }
    @Override public String toString() {
      StringBuilder sb = new StringBuilder("Q^{" + context.index + "}_{");
      for (Symbol symb : transitions)
        sb.append(symb.name());
      sb.append("}\\left(" + state.index + "\\right)");
      return sb.toString();
    }
    @Override public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((context == null) ? 0 : context.hashCode());
      result = prime * result + ((state == null) ? 0 : state.hashCode());
      result = prime * result + Arrays.hashCode(transitions);
      return result;
    }
    @Override public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      ContextedState other = (ContextedState) obj;
      if (context == null) {
        if (other.context != null)
          return false;
      } else if (!context.equals(other.context))
        return false;
      if (state == null) {
        if (other.state != null)
          return false;
      } else if (!state.equals(other.state))
        return false;
      if (!Arrays.equals(transitions, other.transitions))
        return false;
      return true;
    }
  }
}
