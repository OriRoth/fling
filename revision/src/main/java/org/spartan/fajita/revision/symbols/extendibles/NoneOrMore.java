package org.spartan.fajita.revision.symbols.extendibles;

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

import org.spartan.fajita.revision.parser.ell.Interpretation;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;

public class NoneOrMore extends BaseExtendible {
  public NoneOrMore(List<Symbol> symbols) {
    super(symbols);
  }
  @Override protected void solve() {
    head = nonTerminal();
    solvedSymbols = solve(symbols);
    List<Symbol> rhs = new ArrayList<>(solvedSymbols);
    rhs.add(head);
    addRule(head, rhs);
    addRule(head, new LinkedList<>());
  }
  @Override public boolean isNullable(@SuppressWarnings("unused") Set<Symbol> knownNullables) {
    return true;
  }
  @Override public Set<Terminal> getFirstSet(Set<Symbol> nullables, Map<Symbol, Set<Terminal>> knownFirstSets) {
    Set<Terminal> $ = new HashSet<>();
    for (Symbol s : symbols) {
      if (knownFirstSets.containsKey(s))
        $.addAll(knownFirstSets.get(s));
      if (!nullables.contains(s))
        break;
    }
    return $;
  }
  @Override public List<String> parseTypes(Function<Symbol, List<String>> operation) {
    List<String> $ = new LinkedList<>();
    for (Symbol s : symbols)
      for (String q : operation.apply(s))
        $.add(q + "[]");
    return $;
  }
  // TODO Roth: check whether some of this can be generalized
  @SuppressWarnings({ "rawtypes", "unchecked" }) @Override public List<?> fold(List<?> t) {
    assert isSolved && head != null;
    if (t.isEmpty())
      return new ArrayList<>();
    Object o = t.get(0);
    assert o instanceof Interpretation;
    Interpretation current = (Interpretation) o;
    assert head.equals(current.symbol);
    List $ = new LinkedList<>();
    while (!current.value.isEmpty()) {
      $.addAll(current.value.subList(0, current.value.size() - 1));
      Object l2 = current.value.get(current.value.size() - 1);
      assert l2 instanceof Interpretation;
      current = (Interpretation) l2;
      assert head.equals(current.symbol);
    }
    return $;
  }
  @SuppressWarnings({ "rawtypes", "unchecked" }) @Override public List<Object> conclude(List values,
      BiFunction<Symbol, List, List> solution, Function<Symbol, Class> classSolution) {
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
  @SuppressWarnings("rawtypes") @Override public List<Class> toClasses(Function<Symbol, Class> classSolution) {
    List<Class> $ = new LinkedList<>();
    for (Symbol s : symbols)
      $.addAll(s.toClasses(classSolution).stream().map(c -> Array.newInstance(c, 0).getClass()).collect(Collectors.toList()));
    return $;
  }
}
