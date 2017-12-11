package org.spartan.fajita.revision.export;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.testing.FajitaTestingAST.Example;
import org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleBody;
import org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleBodyNext;
import org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleKind;
import org.spartan.fajita.revision.export.testing.FajitaTestingAST.MalExample;
import org.spartan.fajita.revision.export.testing.FajitaTestingAST.Test;
import org.spartan.fajita.revision.parser.ell.EBNFAnalyzer.ELLRecognizerRejection;
import org.spartan.fajita.revision.parser.ell.ELLRecognizer;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public abstract class Grammar {
  public abstract FajitaBNF bnf();
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
    List<Object> $ = Arrays.stream(args).map(a -> !(a instanceof NonTerminal) ? a : ASTNode.dummy).collect(Collectors.toList());
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
