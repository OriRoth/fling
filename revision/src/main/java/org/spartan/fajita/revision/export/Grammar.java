package org.spartan.fajita.revision.export;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.FajitaTestingAST.Example;
import org.spartan.fajita.revision.export.FajitaTestingAST.ExampleKind;
import org.spartan.fajita.revision.export.FajitaTestingAST.MalExample;
import org.spartan.fajita.revision.export.FajitaTestingAST.Test;

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
  private static boolean _match(Object[] xs) {
    for (Object o : xs)
      System.out.println(o);
    return true;
  }
}
