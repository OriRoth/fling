package org.spartan.fajita.revision.junk;

import java.lang.SuppressWarnings;
import org.spartan.fajita.revision.export.ASTNode;
import org.spartan.fajita.revision.export.FluentAPIRecorder;

@SuppressWarnings("all")
public class BODY1 {
  public static BODY1_1 literal(ASTNode arg0) {
    $$$ $$$ = new $$$();$$$.recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.literal,arg0);return $$$;}

  public interface BODY1_1 extends ASTNode {
    BODY2_1 literal(ASTNode arg0);
  }

  public interface BODY2_1 extends ASTNode {
    BODY2_1 literal(ASTNode arg0);
  }

  private interface ParseError {
  }

  private static class $$$ extends FluentAPIRecorder implements BODY1_1, BODY2_1 {
    $$$() {
      super(new org.spartan.fajita.revision.examples.Datalog().bnf().ebnf().getSubBNF(org.spartan.fajita.revision.symbols.NonTerminal.of("BODY1")));}

    public $$$ literal(ASTNode arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.literal,arg0);return this;}
  }
}
