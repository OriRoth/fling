package org.spartan.fajita.revision.junk;

import java.lang.SuppressWarnings;
import org.spartan.fajita.revision.export.ASTNode;
import org.spartan.fajita.revision.export.FluentAPIRecorder;

@SuppressWarnings("all")
public class A {
  public static A_1 b(int arg0) {
    $$$ $$$ = new $$$();$$$.recordTerminal(org.spartan.fajita.revision.examples.Test1.Term.b,arg0);return $$$;}

  public interface A_1 {
    A_2 b(int arg0);
  }

  public interface A_2 extends ASTNode {
  }

  private interface ParseError {
  }

  private static class $$$ extends FluentAPIRecorder implements A_1, A_2 {
    $$$() {
      super(new org.spartan.fajita.revision.examples.Test1().bnf().ebnf().getSubBNF(org.spartan.fajita.revision.examples.Test1.NT.A));}

    public $$$ b(int arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.Test1.Term.b,arg0);return this;}
  }
}
