package org.spartan.fajita.revision.junk;

import java.lang.SuppressWarnings;
import org.spartan.fajita.revision.export.ASTNode;
import org.spartan.fajita.revision.export.FluentAPIRecorder;

@SuppressWarnings("all")
public class LITERALS {
  public static LITERALS_1 literal(ASTNode arg0) {
    $$$ $$$ = new $$$();$$$.recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.literal,arg0);return $$$;}

  public interface LITERALS_1 extends ASTNode {
    LITERALS_1 literal(ASTNode arg0);
  }

  private interface ParseError {
  }

  private static class $$$ extends FluentAPIRecorder implements LITERALS_1 {
    $$$() {
      super(new org.spartan.fajita.revision.examples.Datalog().bnf().bnf().getSubBNF(org.spartan.fajita.revision.examples.Datalog.NT.LITERALS));}

    public $$$ literal(ASTNode arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.literal,arg0);return this;}
  }
}
