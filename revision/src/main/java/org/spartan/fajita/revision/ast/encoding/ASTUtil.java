package org.spartan.fajita.revision.ast.encoding;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import org.spartan.fajita.revision.bnf.EBNF;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.util.DAG;

public class ASTUtil {
  public static Map<NonTerminal, Set<List<Symbol>>> normalize(EBNF ebnf, DAG<NonTerminal> inheritance,
      Function<NonTerminal, NonTerminal> producer) {
    return sortRules(ebnf.normalizedForm(producer), inheritance);
  }
  private static Map<NonTerminal, Set<List<Symbol>>> sortRules(Map<NonTerminal, Set<List<Symbol>>> orig,
      DAG<NonTerminal> inheritance) {
    clearEmptyRules(orig);
    clearAugSRules(orig);
    inheritance.clear();
    for (Entry<NonTerminal, Set<List<Symbol>>> e : orig.entrySet())
      if (isInheritanceRule(e.getValue()))
        for (List<Symbol> rhs : e.getValue())
          for (Symbol s : rhs)
            if (s.isNonTerminal()) {
              inheritance.initialize((NonTerminal) s);
              inheritance.add((NonTerminal) s, e.getKey());
            }
    Map<NonTerminal, Set<List<Symbol>>> $ = new LinkedHashMap<>(), remain = new HashMap<>(orig);
    orig.keySet().stream().filter(x -> !inheritance.containsKey(x)).forEach(x -> {
      $.put(x, orig.get(x));
      remain.remove(x);
    });
    while (!remain.isEmpty()) {
      remain.entrySet().stream()
          .filter(
              e -> e.getValue().stream().allMatch(c -> c.stream().allMatch(s -> (!(s instanceof NonTerminal) || $.containsKey(s)))))
          .forEach(e -> $.put(e.getKey(), e.getValue()));
      $.keySet().forEach(x -> remain.remove(x));
    }
    return $;
  }
  private static void clearEmptyRules(Map<NonTerminal, Set<List<Symbol>>> rs) {
    List<Symbol> tbr = new LinkedList<>();
    rs.keySet().stream().forEach(k -> //
    rs.get(k).stream().forEach(c -> //
    c.stream().filter(l -> //
    l.isVerb() && ((Verb) l).type.length == 0).forEach(e -> tbr.add(e))));
    rs.values().stream().forEach(r -> r.stream().forEach(c -> c.removeAll(tbr)));
  }
  private static void clearAugSRules(Map<NonTerminal, Set<List<Symbol>>> rs) {
    rs.remove(SpecialSymbols.augmentedStartSymbol);
  }
  public static boolean isInheritanceRule(Set<List<Symbol>> rhs) {
    List<Symbol> x;
    return rhs.size() > 1 || rhs.size() == 1 && (x = rhs.stream().findFirst().get()).size() == 1 && x.get(0).isNonTerminal();
  }
  public static String capital(String s) {
    if (s == null)
      throw new IllegalArgumentException("Should not capitalize null String");
    if (s.length() == 0)
      return s;
    return s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
  }
}
