package roth.ori.fling.parser.rll;

import static roth.ori.fling.parser.rll.JSM.ACCEPT;
import static roth.ori.fling.parser.rll.JSM.JAMMED;
import static roth.ori.fling.parser.rll.JSM.UNKNOWN;

import roth.ori.fling.bnf.BNF;
import roth.ori.fling.parser.ll.BNFAnalyzer;
import roth.ori.fling.symbols.SpecialSymbols;
import roth.ori.fling.symbols.GrammarElement;
import roth.ori.fling.symbols.Terminal;
import roth.ori.fling.symbols.Verb;

public class RLLPConcrete {
  protected final BNF bnf;
  protected JSM jsm;
  protected boolean accept;
  protected boolean reject;
  protected boolean initialized;
  protected GrammarElement startSymbol;
  private BNFAnalyzer analyzer;

  public RLLPConcrete(BNF bnf) {
    this(bnf, new BNFAnalyzer(bnf));
  }
  public RLLPConcrete(BNF bnf, BNFAnalyzer analyzer) {
    this.bnf = bnf;
    this.jsm = null;
    this.analyzer = analyzer;
    accept = false;
    reject = false;
    initialized = false;
  }
  void jump(Verb v) {
    jsm = jsm.jump(v);
    if (jsm == JAMMED || jsm == UNKNOWN)
      reject = true;
  }
  private RLLPConcrete consume(Verb verb) {
    if (!initialized) {
      jsm = new JSM(bnf, analyzer,
          bnf.starts.stream().filter(s -> analyzer.firstSetOf(s.asNonTerminal()).contains(verb)).findAny().get(),
          JSM.initialBaseLegalJumps());
      initialized = true;
    }
    assert jsm != null && verb != null;
    if (initialized && jsm.isEmpty()) {
      reject = true;
      return this;
    }
    if (accept)
      throw new RuntimeException("Parser has already accepted");
    if (reject)
      throw new RuntimeException("Parser has already rejected");
    JSM $ = jsm.jump(verb);
    if (verb.equals(SpecialSymbols.$)) {
      if ($ != ACCEPT)
        reject = true;
      else
        accept = true;
      return this;
    }
    assert $ != UNKNOWN && $ != ACCEPT;
    if ($ == JAMMED) {
      reject = true;
      return this;
    }
    jsm = $;
    return this;
  }
  public RLLPConcrete consume(Terminal t) {
    return consume(new Verb(t));
  }
  public RLLPConcrete consume(Verb... ts) {
    for (Verb t : ts)
      consume(t);
    return this;
  }
  public RLLPConcrete consume(Terminal... ts) {
    for (Terminal t : ts)
      try {
        consume(t);
      } catch (@SuppressWarnings("unused") RuntimeException e) {
        assert reject;
        break;
      }
    return this;
  }
  public boolean accepted() {
    if (reject || jsm == null)
      return false;
    if (accept)
      return true;
    return jsm.jump(SpecialSymbols.$) == ACCEPT;
  }
  public boolean rejected() {
    return reject;
  }
}
