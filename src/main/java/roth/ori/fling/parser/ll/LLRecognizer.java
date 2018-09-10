package roth.ori.fling.parser.ll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import roth.ori.fling.bnf.BNF;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.SpecialSymbols;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;
import roth.ori.fling.symbols.Verb;

public class LLRecognizer {
  private final BNF bnf;
  private final BNFAnalyzer analyzer;
  private final Map<NonTerminal, Map<Verb, List<Symbol>>> actionTable;
  private final Stack<Symbol> stack;
  private boolean rejected;
  private boolean initialized;

  public LLRecognizer(final BNF bnf) {
    this.bnf = bnf;
    this.analyzer = new BNFAnalyzer(bnf);
    this.actionTable = createActionTable();
    this.stack = new Stack<>();
    this.rejected = this.initialized = false;
  }
  public boolean consume(Verb... input) {
    return consume(Arrays.asList(input));
  }
  public boolean consume(Terminal... input) {
    return consume(new ArrayList<>(Arrays.asList(input).stream().map(t -> new Verb(t)).collect(Collectors.toList())));
  }
  public boolean consume(Verb v) {
    if (rejected)
      return false;
    if (!initialized) {
      stack.push(bnf.startSymbols.stream().filter(s -> !isError(s.asNonTerminal(), v)).findAny().get());
      initialized = true;
    }
    Symbol top = stack.pop();
    if (v.equals(SpecialSymbols.$))
      return top.equals(SpecialSymbols.$);
    if (top.isVerb())
      return top.equals(v);
    if (isError(top.asNonTerminal(), v))
      return analyzer.isNullable(top) && consume(v);
    List<Symbol> toPush = getPush((NonTerminal) top, v);
    for (Symbol x : toPush)
      stack.push(x);
    return true;
  }
  public boolean consume(List<Verb> input) {
    return input.stream().allMatch(i -> consume(i));
  }
  public List<Symbol> getPush(NonTerminal nt, Verb v) {
    return actionTable.get(nt).get(v);
  }
  public boolean isError(NonTerminal nt, Verb v) {
    return !actionTable.get(nt).containsKey(v);
  }
  private Map<NonTerminal, Map<Verb, List<Symbol>>> createActionTable() {
    Map<NonTerminal, Map<Verb, List<Symbol>>> $ = new HashMap<>();
    for (NonTerminal nt : bnf.nonTerminals) {
      Map<Verb, List<Symbol>> innerMap = new HashMap<>();
      for (Verb v : bnf.verbs) {
        List<Symbol> closure = analyzer.llClosure(nt, v);
        if (closure != null)
          innerMap.put(v, closure);
      }
      $.put(nt, innerMap);
    }
    return $;
  }
}
