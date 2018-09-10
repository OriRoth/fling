package roth.ori.fling.parser.ell;

import static roth.ori.fling.parser.ell.EBNFAnalyzer.reject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import roth.ori.fling.bnf.EBNF;
import roth.ori.fling.export.RuntimeVerb;
import roth.ori.fling.symbols.SpecialSymbols;
import roth.ori.fling.symbols.Symbol;

public class ELLRecognizer {
  private final Map<Symbol, Set<List<Symbol>>> n;
  public final EBNFAnalyzer analyzer;
  private ELLStack stack;
  private static final String PP_IDENT = "--";

  public ELLRecognizer(final EBNF ebnf) {
    n = ebnf.regularFormWithExtendibles(ebnf.afterSolution());
    analyzer = new EBNFAnalyzer(ebnf, n);
    stack = new ELLStack(ebnf.isSubEBNF ? ebnf.subHead : SpecialSymbols.augmentedStartSymbol);
  }
  public void consume(RuntimeVerb input) {
    stack = stack.match(input);
  }
  public Interpretation ast() {
    stack = stack.consume$();
    Interpretation $ = Interpretation.of(stack.current, stack.interpretations);
    $.foldExtendibles();
    return $;
  }
  @Override public String toString() {
    return ast().toString();
  }
  public String toString(int ident) {
    return ast().toString(ident);
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
      assert current.isVerb() || n.containsKey(current) : reject();
      this.parent = parent;
    }
    public ELLStack match(RuntimeVerb input) {
      if (generateChildren(input) && _match(input))
        return this;
      if (parent == null)
        throw reject();
      parent.interpretations.add(Interpretation.of(current, interpretations));
      parent.children.pop();
      return parent.match(input);
    }
    private boolean generateChildren(RuntimeVerb input) {
      if (children != null)
        return true;
      if (current.isVerb()) {
        if (current.equals(input))
          return true;
        throw reject();
      }
      children = new Stack<>();
      for (List<Symbol> clause : n.get(current)) {
        if (analyzer.firstSetOf(clause).contains(input)) {
          for (int i = clause.size() - 1; i >= 0; --i)
            children.push(new ELLStack(clause.get(i), this));
          return true;
        }
      }
      if (!analyzer.isNullable(current))
        return false;
      for (List<Symbol> clause : n.get(current)) {
        if (analyzer.isNullable(clause)) {
          for (int i = clause.size() - 1; i >= 0; --i)
            children.push(new ELLStack(clause.get(i), this));
          return true;
        }
      }
      assert false : "Should not reach here";
      return false;
    }
    private boolean _match(RuntimeVerb input) {
      if (current.isVerb()) {
        if (!current.equals(input))
          throw reject();
        List<Object> args = new LinkedList<>();
        Collections.addAll(args, input.args);
        parent.interpretations.add(Interpretation.of(current, args));
        parent.children.pop();
        return true;
      }
      if (children.isEmpty())
        return false;
      ELLStack c = children.peek();
      if (!c.generateChildren(input))
        throw reject();
      if (c._match(input)) {
        if (children.isEmpty() && parent != null) {
          parent.interpretations.add(Interpretation.of(current, interpretations));
          parent.children.pop();
        }
        return true;
      }
      if (!c.children.isEmpty() && !analyzer.isNullable(c.current))
        throw reject();
      interpretations.add(Interpretation.of(c.current, c.interpretations));
      children.pop();
      return _match(input);
    }
    public ELLStack consume$() {
      if (current.isVerb())
        throw reject("folded on terminal");
      if (children == null) {
        if (!analyzer.isNullable(current))
          throw reject("folded on non nullable");
        children = new Stack<>();
        for (List<Symbol> clause : n.get(current)) {
          if (analyzer.isNullable(clause)) {
            for (int i = clause.size() - 1; i >= 0; --i)
              children.push(new ELLStack(clause.get(i), this));
            break;
          }
        }
      }
      while (!children.isEmpty())
        children.peek().consume$();
      if (parent == null)
        return this;
      parent.interpretations.add(Interpretation.of(current, interpretations));
      parent.children.pop();
      return parent;
    }
    @Override public String toString() {
      return current.toString() + (children == null ? "[?]" : children);
    }
  }

  // TODO Roth: add interpretation to pp
  @SuppressWarnings("unused") private static String pp(ELLStack stack) {
    return pp(stack, 0);
  }
  private static String pp(ELLStack stack, int ident) {
    StringBuilder $ = new StringBuilder();
    for (int i = 0; i < ident; ++i)
      $.append(PP_IDENT);
    $.append(stack.current).append("\n");
    if (stack.children != null)
      for (ELLStack c : stack.children)
        $.append(pp(c, ident + 1));
    return $.toString();
  }
}
