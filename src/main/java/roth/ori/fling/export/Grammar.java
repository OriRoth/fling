package roth.ori.fling.export;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import roth.ori.fling.api.Fling;
import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Fling.SetSymbols;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.testing.FlingTestingAST.Example;
import roth.ori.fling.export.testing.FlingTestingAST.ExampleBody;
import roth.ori.fling.export.testing.FlingTestingAST.ExampleBodyNext;
import roth.ori.fling.export.testing.FlingTestingAST.ExampleKind;
import roth.ori.fling.export.testing.FlingTestingAST.MalExample;
import roth.ori.fling.export.testing.FlingTestingAST.Test;
import roth.ori.fling.parser.ell.EBNFAnalyzer.ELLRecognizerRejection;
import roth.ori.fling.parser.ell.ELLRecognizer;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;

public abstract class Grammar {
  public <Term extends Enum<Term> & Terminal, NT extends Enum<NT> & Symbol> SetSymbols buildFlingBNF(
      final Class<Term> terminalEnum, final Class<NT> nonterminalEnum, String apiName, String packagePath, String projectPath) {
    return Fling.build(getClass(), terminalEnum, nonterminalEnum, apiName, packagePath, projectPath);
  }
  public abstract FlingBNF bnf();
  @SuppressWarnings("static-method") public Test examples() {
    return new Test(new ExampleKind[] {});
  }
  public void generateGrammarFiles() throws IOException {
    Main.apiGenerator(bnf().go());
  }
  public void test() {
    _test(examples());
  }
  private void _test(Test examples) {
    for (ExampleKind e : examples.test1)
      _test(e);
  }
  private void _test(ExampleKind e) {
    if (e instanceof Example)
      _test((Example) e);
    else
      _test((MalExample) e);
  }
  private void _test(Example e) {
    assert _match(e.example) : "Example fail in " + getClass();
  }
  private void _test(MalExample e) {
    assert !_match(e.malexample) : "Malexample fail in " + getClass();
  }
  private boolean _match(ExampleBody b) {
    return _match(new ELLRecognizer(!b.examplebody1.isPresent() ? bnf().ebnf() : bnf().ebnf().makeSubBNF(b.examplebody1.get())),
        b.call, b.with, b.examplebody2);
  }
  private static boolean _match(ELLRecognizer ell, Terminal t, Object[] args, ExampleBodyNext[] next) {
    List<Object> $ = Arrays.stream(args).map(a -> !(a instanceof Symbol) ? a : ASTNode.dummy).collect(Collectors.toList());
    try {
      ell.consume(new RuntimeVerb(t, $.toArray(new Object[$.size()])));
      return _match(ell, next);
    } catch (@SuppressWarnings("unused") ELLRecognizerRejection e) {
      return false;
    }
  }
  private static boolean _match(ELLRecognizer ell, ExampleBodyNext[] next) {
    return next.length == 0 || _match(ell, next[0].then, next[0].with, Arrays.copyOfRange(next, 1, next.length));
  }
}
