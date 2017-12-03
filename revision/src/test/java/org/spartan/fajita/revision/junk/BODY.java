package org.spartan.fajita.revision.junk;

import java.lang.SuppressWarnings;
import org.spartan.fajita.revision.export.ASTNode;
import org.spartan.fajita.revision.export.FluentAPIRecorder;

@SuppressWarnings("all")
public class BODY {
  public static BODY_1 body() {
    $$$ $$$ = new $$$();$$$.recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.body);return $$$;}

  public interface BODY_1 {
    BODY_2 literal(ASTNode arg0);
  }

  public interface BODY_2 extends ASTNode {
    LITERALS_1 literal(ASTNode arg0);
  }

  public interface LITERALS_1 extends ASTNode {
    LITERALS_1 literal(ASTNode arg0);
  }

  private interface ParseError {
  }

  private static class $$$ extends FluentAPIRecorder implements BODY_1, BODY_2, LITERALS_1 {
    $$$() {
      super(new org.spartan.fajita.revision.examples.Datalog().bnf().bnf().getSubBNF(org.spartan.fajita.revision.examples.Datalog.NT.BODY));}

    public $$$ literal(ASTNode arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.literal,arg0);return this;}
  }
}
