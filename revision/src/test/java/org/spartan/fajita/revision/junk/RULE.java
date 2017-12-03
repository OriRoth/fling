package org.spartan.fajita.revision.junk;

import java.lang.SuppressWarnings;
import org.spartan.fajita.revision.export.ASTNode;
import org.spartan.fajita.revision.export.FluentAPIRecorder;

@SuppressWarnings("all")
public class RULE {
  public static RULE_1_n1 head(ASTNode arg0) {
    $$$ $$$ = new $$$();$$$.recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.head,arg0);return $$$;}

  public static RULE_1 fact(ASTNode arg0) {
    $$$ $$$ = new $$$();$$$.recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.fact,arg0);return $$$;}

  public interface RULE_1 extends ASTNode {
  }

  public interface RULE_1_n1 {
    BODY_1 body();
  }

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

  private static class $$$ extends FluentAPIRecorder implements RULE_1, RULE_1_n1, BODY_1, BODY_2, LITERALS_1 {
    $$$() {
      super(new org.spartan.fajita.revision.examples.Datalog().bnf().bnf().getSubBNF(org.spartan.fajita.revision.examples.Datalog.NT.RULE));}

    public $$$ body() {
      recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.body);return this;}

    public $$$ literal(ASTNode arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.literal,arg0);return this;}
  }
}
