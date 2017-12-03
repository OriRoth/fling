package org.spartan.fajita.revision.junk;

import java.lang.String;
import java.lang.SuppressWarnings;
import org.spartan.fajita.revision.export.ASTNode;
import org.spartan.fajita.revision.export.FluentAPIRecorder;

@SuppressWarnings("all") public class LITERAL {
  public static LITERAL_1 name(String arg0) {
    $$$ $$$ = new $$$();
    $$$.recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.name, arg0);
    return $$$;
  }

  public interface LITERAL_1 {
    LITERAL_2 terms(String... arg0);
  }

  public interface LITERAL_2 extends ASTNode {
  }

  private interface ParseError {
  }

  private static class $$$ extends FluentAPIRecorder implements LITERAL_1, LITERAL_2 {
    $$$() {
      super(new org.spartan.fajita.revision.examples.Datalog().bnf().bnf()
          .getSubBNF(org.spartan.fajita.revision.examples.Datalog.NT.LITERAL));
    }
    public $$$ terms(String... arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.terms, arg0);
      return this;
    }
  }
}
