package org.spartan.fajita.revision.junk;

import java.lang.SuppressWarnings;
import org.spartan.fajita.revision.export.ASTNode;
import org.spartan.fajita.revision.export.FluentAPIRecorder;

@SuppressWarnings("all")
public class Test1 {
  public static S_1 a(ASTNode arg0, ASTNode arg1) {
    $$$ $$$ = new $$$();$$$.recordTerminal(org.spartan.fajita.revision.examples.Test1.Term.a,arg0,arg1);return $$$;}

  public interface S_1 extends ASTNode {
  }

  public interface A_1 {
    A_2 b(int arg0);
  }

  public interface A_2 {
  }

  private interface ParseError {
  }

  private static class $$$ extends FluentAPIRecorder implements S_1, A_1, A_2 {
    $$$() {
      super(new org.spartan.fajita.revision.examples.Test1().bnf().ebnf());}

    public $$$ b(int arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.Test1.Term.b,arg0);return this;}
  }
}
