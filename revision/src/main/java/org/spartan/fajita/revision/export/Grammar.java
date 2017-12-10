package org.spartan.fajita.revision.export;

import java.io.IOException;
import java.util.Arrays;

import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.testing.FajitaTestingAST.Example;
import org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleBody;
import org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleKind;
import org.spartan.fajita.revision.export.testing.FajitaTestingAST.MalExample;
import org.spartan.fajita.revision.export.testing.FajitaTestingAST.Test;
import org.spartan.fajita.revision.parser.ell.ELLRecognizer;
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
        b.call, b.with);
  }
  @SuppressWarnings("unused") private static boolean _match(ELLRecognizer ell, Terminal call, Object[] with) {
    // System.out.println(call);
    // System.out.println(Arrays.asList(with));
    return false;
  }
}
