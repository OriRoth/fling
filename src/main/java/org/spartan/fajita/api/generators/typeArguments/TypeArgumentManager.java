package org.spartan.fajita.api.generators.typeArguments;

import static org.spartan.fajita.api.generators.GeneratorsUtils.STACK_TYPE_PARAMETER;
import static org.spartan.fajita.api.generators.GeneratorsUtils.merge;
import static org.spartan.fajita.api.generators.GeneratorsUtils.type;
import static org.spartan.fajita.api.generators.GeneratorsUtils.wildcardArray;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.BASE_STACK;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.BASE_STATE;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.ERROR_STATE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
  final Map<State, StateTypeData> statesTypeData;
  private final DirectedGraph<ContextedState, DefaultEdge> dependencies;
  private Map<ContextedState, TypeName> instantiations;

  public TypeArgumentManager(final LRParser parser) {
    this.parser = parser;
    bnf = parser.bnf;
    symbols = initializeSymbolIndexes();
    baseTAList = initializeBaseTypeArgumentList();
    statesTypeData = new HashMap<>();
    parser.getStates().forEach(s -> statesTypeData.put(s, new StateTypeData(s, symbols)));
    dependencies = generateDependenciesGraph();
    calculateInstantiations();
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
    Collections.sort($, symbolComparator());
    return $;
  }
  public static Comparator<Symbol> symbolComparator() {
    return new Comparator<Symbol>() {
      @Override public int compare(Symbol symb1, Symbol symb2) {
        return symb1.isTerminal() && symb2.isNonTerminal() ? -1
            : symb1.isNonTerminal() && symb2.isTerminal() ? 1 : symb1.name().compareTo(symb2.name());
      }
    };
  }
  public TypeVariableName getType(final int index) {
    return baseTAList.get(index);
  }
  public TypeVariableName getType(final Symbol s) {
    return getType(symbols.indexOf(s) + 1);
  }
  public int baseStateArgumentNumber() {
    return baseTAList.size();
  }
  public List<TypeVariableName> stateTypeArguments(final State s) {
    return statesTypeData.get(s).getFormalParameters();
  }
  private void calculateInstantiations() {
    instantiations = new HashMap<>();
    TopologicalOrderIterator<ContextedState, DefaultEdge> iter = new TopologicalOrderIterator<>(dependencies);
    iter.forEachRemaining(s -> {
      if (s.transitions.length == 1)
        InstantiateSingleTransition(s);
      else if (s.transitions.length == 2)
        InstantiateDoubleTransition(s);
      else
        throw new IllegalStateException("unexpected transition length :" + s.transitions.length);
    });
    System.out.println(instantiations);
  }
  private void InstantiateSingleTransition(final ContextedState s) {
    assert (s.transitions.length == 1);
    if (!s.isInherited()) {
      StateTypeData q$B_Data = statesTypeData.get(s.context);
      StateTypeData q_X$B_Data = statesTypeData.get(s.state);
      Collection<InheritedParameter> q$BParams = q$B_Data.getInheritedParameters();
      for (InheritedParameter A_i_a : q_X$B_Data.getInheritedParameters()) {
        NonTerminal A = A_i_a.lhs;
        Terminal a = A_i_a.lookahead;
        int i = A_i_a.depth;
        InheritedParameter q$B_Param = new InheritedParameter(i - 1, A, a);
        TypeName instantiatedType;
        if (q$BParams.contains(q$B_Param))// Inherited TA
          instantiatedType = q$B_Data.getFormalParameter(q$B_Param);
        else // Synthesized from Q^{B}
          instantiatedType = findInstance(s.context, A, a);
        assert (instantiatedType != null);
        s.instantiate(A_i_a, instantiatedType);
      }
    }
    instantiations.put(s, s.getInstantiation());
  }
  private void InstantiateDoubleTransition(final ContextedState s) {
    assert (s.transitions.length == 2);
    if (!s.isInherited()) {
      StateTypeData q$B_Data = statesTypeData.get(s.context);
      StateTypeData q_X$B_Data = statesTypeData.get(s.context.goTo(s.transitions[0]));
      StateTypeData q_XY$B_Data = statesTypeData.get(s.state);
      Collection<InheritedParameter> q_X$B_Params = q_X$B_Data.getInheritedParameters();
      Collection<InheritedParameter> q$B_Params = q$B_Data.getInheritedParameters();
      for (InheritedParameter A_i_a : q_XY$B_Data.getInheritedParameters()) {
        NonTerminal A = A_i_a.lhs;
        Terminal a = A_i_a.lookahead;
        int i = A_i_a.depth;
        InheritedParameter q_X$B_Param = new InheritedParameter(i - 1, A, a);
        InheritedParameter q$B_Param = new InheritedParameter(i - 2, A, a);
        TypeName instantiatedType;
        if (q$B_Params.contains(q$B_Param))// Inherited TA
          instantiatedType = q$B_Data.getFormalParameter(q$B_Param);
        else if (q_X$B_Params.contains(q_X$B_Param)) // Synthesized from Q^{B}
          instantiatedType = findInstance(q$B_Data.state, A, a);
        else {
          ContextedState q_Aa$X = find(q_X$B_Data.state, A, a);
          if (q_Aa$X.isInherited()) // Synthesized from Q^{B}_{X} , abstract
            instantiatedType = findInstance(q$B_Data.state, ((InheritedContexedState) q_Aa$X).lhs,
                ((InheritedContexedState) q_Aa$X).lookahead);
          else // Synthesized from Q^{B}_{X} , abstract
            instantiatedType = findInstance(q_X$B_Data.state, A, a);
        }
        assert (instantiatedType != null);
        s.instantiate(A_i_a, instantiatedType);
      }
    }
    instantiations.put(s, s.getInstantiation());
  }
  TypeName findInstance(State context, Symbol... transitions) {
    return instantiations.get(new ContextedState(context, transitions));
  }
  private DirectedGraph<ContextedState, DefaultEdge> generateDependenciesGraph() {
    final DefaultDirectedGraph<ContextedState, DefaultEdge> $ = new DefaultDirectedGraph<>(DefaultEdge.class);
    parser.getStates().forEach(state -> symbols.forEach(symb -> {
      if (state.isLegalTransition(symb))
        $.addVertex(new ContextedState(state.goTo(symb), state, symb));
      else if (state.isLegalReduce(symb)) {
        Item item = state.getItems().stream() //
            .filter(i -> i.readyToReduce() && i.lookahead.equals(symb)).findAny().get();
        $.addVertex(new InheritedContexedState(state, item.rule.lhs, item.dotIndex, (Terminal) symb, symb));
      }
    }));
    for (State q$B : parser.getStates())
      addStateDependencies(q$B, $);
    System.out.println($);
    // TODO: handle cycles case.
    if ($.vertexSet().size() > 0 && new CycleDetector<>($).detectCycles())
      throw new IllegalArgumentException("Cycles are not handled yet, the graph:" + $);
    return $;
  }
  private void addStateDependencies(final State q$B, final DefaultDirectedGraph<ContextedState, DefaultEdge> g) {
    Set<Item> items = q$B.getItems().stream()
        .filter(i -> i.dotIndex == 0 && i.lookahead != SpecialSymbols.$ && i.rule.lhs != SpecialSymbols.augmentedStartSymbol)
        .collect(Collectors.toSet());
    for (Item i : items) {
      ContextedState q_Ab$B = find(q$B, i.rule.lhs, i.lookahead);
      List<Symbol> rhs = i.rule.getChildren();
      // By rule (A-> ε. , b)
      if (rhs.size() == 0)// epsilon rule
        rhs.add(i.lookahead);
      // By rule [A -> .Xα , b]
      Symbol X = rhs.get(0);
      ContextedState q_X$B = new ContextedState(q$B.goTo(X), q$B, X);
      addEdge(g, q_Ab$B, q_X$B);
      // By rule [ A -> Cd , b]
      if (rhs.size() == 2 && rhs.get(0).isNonTerminal() && rhs.get(1).isTerminal()) {
        NonTerminal C = (NonTerminal) rhs.get(0);
        Terminal d = (Terminal) rhs.get(1);
        ContextedState q_Cd$B = find(q$B, C, d);
        if (!q_Cd$B.isInherited())
          addEdge(g, q_Ab$B, q_Cd$B);
      }
    }
    // By rules on two transition contexts
    new HashSet<>(g.vertexSet()).stream().filter(v -> {
      return v.transitions.length == 2 && v.context.equals(q$B) && !v.isInherited();
    }).forEach(q_Ab$B -> {
      NonTerminal A = (NonTerminal) q_Ab$B.transitions[0];
      Terminal b = (Terminal) q_Ab$B.transitions[1];
      ContextedState q_A$B = new ContextedState(q$B.goTo(A), q$B, A);
      addEdge(g, q_A$B, q_Ab$B);
      ContextedState q_b$A = find(q$B.goTo(A), b);
      // if (q_b$A.isInherited())
      addEdge(g, q_b$A, q_Ab$B);
      // /**
      // * else ;// the state is known in a higher place. maybe Q$b know about
      // it.
      // */
    });
  }
  private static void addEdge(final DirectedGraph<ContextedState, DefaultEdge> g, final ContextedState src,
      final ContextedState dst) {
    g.addVertex(src);
    g.addVertex(dst);
    g.addEdge(src, dst);
  }
  private ContextedState find(final State q$B, final NonTerminal A, final Terminal b) {
    Action action = parser.actionTable(q$B.goTo(A), b);
    if (action.isShift())
      return new ContextedState(((Shift) action).state, q$B, A, b);
    // reduce
    DerivationRule rule = ((Reduce) action).item.rule;
    List<Symbol> alpha = rule.getChildren();
    NonTerminal C = rule.lhs;
    if (alpha.size() > 1)
      return new InheritedContexedState(q$B, C, alpha.size() - 1, b, A, b);
    if (alpha.size() == 1) // alpha = A
      return find(q$B, C, b);
    return find(q$B.goTo(A), C, b);
  }
  private ContextedState find(final State q$B, final Terminal b) {
    Action action = parser.actionTable(q$B, b);
    if (action.isShift())
      return new ContextedState(((Shift) action).state, q$B, b);
    // reduce
    DerivationRule rule = ((Reduce) action).item.rule;
    List<Symbol> alpha = rule.getChildren();
    NonTerminal C = rule.lhs;
    if (alpha.size() > 0)
      return new InheritedContexedState(q$B, C, alpha.size() - 1, b, b);
    return find(q$B, C, b);
  }
  public ParameterizedTypeName getInstantiatedState(State s) {
    StateTypeData typeData = statesTypeData.get(s);
    for (Symbol symb : symbols) {
      if (!s.isLegalTransition(symb) && !s.isLegalReduce(symb))
        typeData.setBaseType(symb, type(ERROR_STATE));
      else
        typeData.setBaseType(symb, findInstance(s, symb));
    }
    return typeData.getBaseType();
  }

  private class ContextedState {
    public final State context;
    public final State state;
    public final Symbol[] transitions;
    private final Map<InheritedParameter, TypeName> instantiation;

    public ContextedState(final State actual, final State context, final Symbol... transitions) {
      state = actual;
      this.context = context;
      this.transitions = transitions;
      instantiation = new HashMap<>();
      instantiateStack();
    }
    public boolean isInherited() {
      return false;
    }
    public ContextedState(final State context, final Symbol... transitions) {
      state = null;
      this.context = context;
      this.transitions = transitions;
      instantiation = new HashMap<>();
    }
    private void instantiateStack() {
      TypeName type = anonymizeType(context, type(STACK_TYPE_PARAMETER));
      if (transitions.length == 2) {
        type = anonymizeType(context.goTo(transitions[0]), type);
      }
      instantiate(StateTypeData.stackTP, type);
    }
    private TypeName anonymizeType(State s, TypeName stackType) {
      if (s.isInitial())
        return type(s.name);
      List<TypeVariableName> paramList = statesTypeData.get(s).getFormalParameters();
      TypeName[] params = merge(new TypeName[] { stackType }, wildcardArray(paramList.size() - 1));
      return ParameterizedTypeName.get(type(s.name), params);
    }
    @Override public String toString() {
      return getDescription(Integer.toString(state.index));
    }
    protected String getDescription(String stateDesc) {
      StringBuilder sb = new StringBuilder("Q^{" + context.index + "}_{");
      for (Symbol symb : transitions)
        sb.append(symb.name());
      sb.append("}\\left(" + stateDesc + "\\right)");
      return sb.toString();
    }
    @Override public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((context == null) ? 0 : context.hashCode());
      result = prime * result + Arrays.hashCode(transitions);
      return result;
    }
    @Override public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      ContextedState other = (ContextedState) obj;
      if (context == null) {
        if (other.context != null)
          return false;
      } else if (!context.equals(other.context))
        return false;
      if (!Arrays.equals(transitions, other.transitions))
        return false;
      return true;
    }
    public void instantiate(InheritedParameter param, TypeName type) {
      instantiation.put(param, type);
    }
    public TypeName getInstantiation() {
      StateTypeData data = statesTypeData.get(state);
      TypeName[] typeArguments = new TypeName[data.getFormalParameters().size()];
      assert (typeArguments.length == instantiation.size());
      typeArguments[0] = instantiation.get(StateTypeData.stackTP);
      int index = 1;
      for (InheritedParameter paramName : data.getInheritedParameters()) {
        typeArguments[index++] = instantiation.get(paramName);
      }
      return ParameterizedTypeName.get(type(state.name), typeArguments);
    }
  }

  private class InheritedContexedState extends ContextedState {
    NonTerminal lhs;
    int depth;
    Terminal lookahead;

    public InheritedContexedState(final State context, final NonTerminal lhs, final int depth, final Terminal lookahead,
        final Symbol... transitions) {
      super(context, transitions);
      this.lhs = lhs;
      this.depth = depth;
      this.lookahead = lookahead;
    }
    @Override public boolean isInherited() {
      return true;
    }
    @Override public String toString() {
      return getDescription(getInstantiation().toString());
    }
    @SuppressWarnings("unused") @Override public void instantiate(InheritedParameter param, TypeName type) {
      throw new IllegalStateException("can't instantiate");
    }
    @Override public TypeName getInstantiation() {
      return type(new InheritedParameter(depth, lhs, lookahead).toString());
    }
  }
}
