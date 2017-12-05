package org.spartan.fajita.revision.parser.ell;

import static org.spartan.fajita.revision.parser.ell.EBNFAnalyzer.reject;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.spartan.fajita.revision.bnf.EBNF;
import org.spartan.fajita.revision.export.RuntimeVerb;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.extendibles.Extendible;

public class ELLRecognizer {
  private final Map<Symbol, Set<List<Symbol>>> n;
  private final EBNFAnalyzer a;
  private ELLStack stack;

  public ELLRecognizer(final EBNF ebnf) {
    n = ebnf.regularFormWithExtendibles();
    a = new EBNFAnalyzer(ebnf);
    stack = new ELLStack(ebnf.isSubEBNF ? ebnf.subHead : SpecialSymbols.augmentedStartSymbol);
  }
  public void consume(RuntimeVerb input) {
    stack = stack.match(input);
  }
  public Object ast() {
    return Interpretation.of(stack.current, stack.interpretations);
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
      else if (a.firstSetOf(ss).contains(input))
        return ss;
    }
    if (hasEmptyRule)
      return new LinkedList<>();
    throw reject("cannot match " + nt + " with " + input);
  }
  private List<Symbol> getPush(Extendible e, Verb input) {
    return getPush(n.get(e).stream().findFirst().get().get(0), input);
  }

  @SuppressWarnings("synthetic-access") public class ELLStack {
    public ELLStack parent;
    public Symbol current;
    public List<Interpretation> interpretations;
    public Stack<ELLStack> children;

    public ELLStack(Symbol current) {
      this.current = current;
      this.interpretations = new LinkedList<>();
      assert n.containsKey(current) : reject();
    }
    public ELLStack(Symbol current, ELLStack parent) {
      this.current = current;
      this.interpretations = new LinkedList<>();
      assert n.containsKey(current) : reject();
      this.parent = parent;
    }
    public ELLStack match(RuntimeVerb input) {
      System.out.println(generateChildren(input));
      System.out.println(children);
      if (generateChildren(input) && _match(input))
        return this;
      parent.interpretations.add(Interpretation.of(current, interpretations));
      return parent.match(input);
    }
    private boolean generateChildren(RuntimeVerb input) {
      if (children != null)
        return true;
      children = new Stack<>();
      boolean hasEmptyRule = false;
      for (List<Symbol> clause : n.get(current)) {
        if (clause.isEmpty())
          hasEmptyRule = true;
        if (a.firstSetOf(clause).contains(input)) {
          for (int i = clause.size() - 1; i >= 0; --i)
            children.push(new ELLStack(clause.get(i), this));
          return true;
        }
      }
      return hasEmptyRule;
    }
    private boolean _match(RuntimeVerb input) {
      if (children.isEmpty())
        return false;
      ELLStack c = children.peek();
      if (c._match(input))
        return true;
      if (!a.isNullable(c.current))
        throw reject();
      interpretations.add(Interpretation.of(c.current, null));
      children.pop();
      return _match(input);
    }
    @Override public String toString() {
      return current.toString() + (children == null ? "[?]" : children);
    }
  }

  public static class Interpretation extends AbstractMap.SimpleEntry<Symbol, Object> {
    private static final long serialVersionUID = -1984822822971661087L;

    public Interpretation(Symbol symbol, Object value) {
      super(symbol, value);
    }
    @Override public String toString() {
      return getKey() + "=" + getValue();
    }
    public static Interpretation of(Symbol symbol, Object value) {
      return new Interpretation(symbol, value);
    }
  }
}
