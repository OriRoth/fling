package org.spartan.fajita.api.ll;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Verb;

public class PredictionTable {
  public class NotLLGrammar extends RuntimeException {
    private static final long serialVersionUID = -6362446023081095384L;

    public NotLLGrammar(String message) {
      super(message);
    }
  }

  private final HashMap<Symbol, Map<Verb, Push>> table;

  public PredictionTable(Collection<NonTerminal> nonterminals) {
    table = new HashMap<>();
    for (Symbol nt : nonterminals)
      table.put(nt, new HashMap<>());
  }
  void set(NonTerminal nt, Verb v, Push a) {
    Push result = table.get(nt).put(v, a);
    if (result != null)
      throw new NotLLGrammar("nonterminal " + nt + " has two rules with intersecting First set : <" + result + "> , <" + a + ">");
  }
  boolean isError(NonTerminal nt, Verb v) {
    return !table.get(nt).containsKey(v);
  }
  Push get(NonTerminal nt, Verb v) {
    if (isError(nt, v))
      throw new IllegalStateException("M[" + nt + "," + v + "] not a push operaion!");
    return table.get(nt).get(v);
  }

  public static class Push {
    public final List<Symbol> string;

    public Push(List<Symbol> string) {
      this.string = string;
    }
    public Push() {
      string = new ArrayList<>();
    }
    @Override public String toString() {
      return string.toString();
    }
  }
}
