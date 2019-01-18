package roth.ori.fling.symbols.extendibles;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import roth.ori.fling.parser.ell.Interpretation;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.GrammarElement;
import roth.ori.fling.symbols.Terminal;

public class OneOrMore extends BaseExtendible {
  private Symbol head2;

  public OneOrMore(List<GrammarElement> symbols) {
    super(symbols);
  }
  @Override protected void solve() {
    head = nonTerminal();
    head2 = nonTerminal();
    solvedSymbols = solve(symbols);
    List<GrammarElement> rhs1 = new ArrayList<>(solvedSymbols);
    rhs1.add(head2);
    addRule(head, rhs1);
    List<GrammarElement> rhs2 = new ArrayList<>(solvedSymbols);
    rhs2.add(head2);
    addRule(head2, rhs2);
    addRule(head2, new LinkedList<>());
  }
  @Override public boolean isNullable(Set<GrammarElement> knownNullables) {
    return symbols.stream().allMatch(x -> knownNullables.contains(x));
  }
  @Override public Set<Terminal> getFirstSet(Set<GrammarElement> nullables, Map<GrammarElement, Set<Terminal>> knownFirstSets) {
    Set<Terminal> $ = new HashSet<>();
    for (GrammarElement s : symbols) {
      if (knownFirstSets.containsKey(s))
        $.addAll(knownFirstSets.get(s));
      if (!nullables.contains(s))
        break;
    }
    return $;
  }
  @Override public List<String> parseTypes(Function<GrammarElement, List<String>> operation,
      @SuppressWarnings("unused") Function<GrammarElement, List<String>> forgivingOperation) {
    List<String> $ = new LinkedList<>();
    for (GrammarElement s : symbols)
      for (String q : operation.apply(s))
        $.add(q + "[]");
    return $;
  }
  // TODO Roth: check whether some of this can be generalized
  @SuppressWarnings({ "rawtypes", "unchecked" }) @Override public List<?> fold(List<?> t) {
    assert isSolved && head != null && head2 != null && t.size() == 1;
    Object o = t.get(0);
    assert o instanceof Interpretation;
    Interpretation current = (Interpretation) o;
    assert head.equals(current.symbol) && current.value.size() > 0;
    List $ = new LinkedList<>();
    $.addAll(current.value.subList(0, current.value.size() - 1));
    Object l = current.value.get(current.value.size() - 1);
    assert l instanceof Interpretation;
    Interpretation li = (Interpretation) l;
    assert head2.equals(li.symbol);
    while (!li.value.isEmpty()) {
      $.addAll(li.value.subList(0, li.value.size() - 1));
      Object l2 = li.value.get(li.value.size() - 1);
      assert l2 instanceof Interpretation;
      li = (Interpretation) l2;
      assert head2.equals(li.symbol);
    }
    return $;
  }
  @SuppressWarnings({ "rawtypes", "unchecked" }) @Override public List<Object> conclude(List values,
      BiFunction<GrammarElement, List, List> solution, Function<GrammarElement, Class> classSolution) {
    List<List> solved = new LinkedList<>();
    int currentSymbol = 0;
    for (Object o : values) {
      if (solved.size() < currentSymbol + 1)
        solved.add(new LinkedList<>());
      Interpretation i = (Interpretation) o;
      assert i.symbol.equals(symbols.get(currentSymbol));
      solved.get(currentSymbol).add(solution.apply(i.symbol, i.value));
      ++currentSymbol;
      if (currentSymbol == symbols.size())
        currentSymbol = 0;
    }
    List<List> processed = processTokens(solved);
    List<Class> processedClasses = toClasses(classSolution);
    List<Object> $ = new LinkedList<>();
    for (int i = 0; i < processedClasses.size(); ++i) {
      if (processed.size() <= i)
        processed.add(new ArrayList<>());
      $.add(Array.newInstance(processedClasses.get(i).getComponentType(), processed.get(i).size()));
      for (int j = 0; j < processed.get(i).size(); ++j)
        ((Object[]) $.get(i))[j] = processed.get(i).get(j);
    }
    return $;
  }
  @SuppressWarnings({ "rawtypes", "unchecked" }) private static List<List> processTokens(List<List> solved) {
    List<List> $ = new LinkedList<>();
    for (List l : solved) {
      List<List> n = new LinkedList();
      for (Object o : l) {
        assert o instanceof List;
        List q = (List) o;
        for (int i = 0; i < q.size(); ++i) {
          if (n.size() < i + 1)
            n.add(new LinkedList<>());
          n.get(i).add(q.get(i));
        }
      }
      $.addAll(n);
    }
    return $;
  }
  @SuppressWarnings("rawtypes") @Override public List<Class> toClasses(Function<GrammarElement, Class> classSolution) {
    List<Class> $ = new LinkedList<>();
    for (GrammarElement s : symbols)
      $.addAll(s.toClasses(classSolution).stream().map(c -> Array.newInstance(c, 0).getClass()).collect(Collectors.toList()));
    return $;
  }
}
