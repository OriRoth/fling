package org.spartan.fajita.revision.parser.ell;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.bnf.DerivationRule;
import org.spartan.fajita.revision.bnf.EBNF;
import org.spartan.fajita.revision.export.FluentAPIRecorder;
import org.spartan.fajita.revision.export.RuntimeVerb;
import org.spartan.fajita.revision.parser.ll.BNFAnalyzer;
import org.spartan.fajita.revision.parser.rll.Item;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.extendibles.Extendible;

public class ELLRecognizer {
  private final EBNF ebnf;
  private final Map<Symbol, Set<List<Symbol>>> n;
  private final EBNFAnalyzer a;
  private Stack<ERuntimeItem> stack = new Stack<>();
  private boolean initialized = false;

  public ELLRecognizer(final EBNF ebnf) {
    this.ebnf = ebnf;
    this.n = ebnf.regularFormWithExtendibles();
    this.a = new EBNFAnalyzer(ebnf);
    for (Symbol s : n.keySet())
      System.out.println(s + " ::= " + n.get(s));
  }
  public void consume(RuntimeVerb input) {
    if (!initialized) {
      stack.push(ERuntimeItem.of(ebnf.startSymbols.stream().filter(x -> a.firstSetOf(x).contains(input)).findAny().get()));
      initialized = true;
    }
    ERuntimeItem topItem = stack.peek();
    Symbol top = topItem.afterDot();
    if (top.isVerb()) {
      Verb v = (Verb) top;
      if (input.equals(v)) {
        topItem.advance(input.args);
        return;
      }
      throw reject();
    }
    List<Symbol> toPush = getPush(top, input);
    mtop.toConsume = toPush.size();
    for (Symbol x : toPush) {
      stack.push(x);
      if (x.isNonTerminal())
        match.push(nonTerminal(x.asNonTerminal()));
    }
  }
  public Object ast() {
    return null;
  }
  private List<Symbol> getPush(Symbol s, Verb input) {
    if (s.isNonTerminal())
      return getPush(s.asNonTerminal(), input);
    if (s.isExtendible())
      return getPush(s.asExtendible(), input);
    throw reject("should not reach here");
  }
  private List<Symbol> getPush(NonTerminal nt, Verb input) {
    assert n.containsKey(nt) : reject("non terminal " + nt + " not in EBNF");
    boolean hasEmptyRule = false;
    for (List<Symbol> ss : n.get(nt)) {
      if (ss.isEmpty())
        hasEmptyRule = true;
      else if (a.firstSetOf(ss.get(0)).contains(input))
        return ss;
    }
    if (hasEmptyRule)
      return new LinkedList<>();
    throw reject("cannot match " + nt + " with " + input);
  }
  private List<Symbol> getPush(Extendible nt, Verb input) {
  }
  private static RuntimeException reject() {
    return new RuntimeException(ELLRecognizer.class.getSimpleName() + " rejected");
  }
  private static RuntimeException reject(String message) {
    return new RuntimeException(ELLRecognizer.class.getSimpleName() + " rejected: " + message);
  }
}
