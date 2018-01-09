package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.examples.Exp2.NT.*;
import static org.spartan.fajita.revision.examples.Exp2.Term.a;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class Exp2 extends Grammar {
  public static enum Term implements Terminal {
    a
  }

  public static enum NT implements NonTerminal {
    A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(Exp2.class, Term.class, NT.class, "Exp2", Main.packagePath, Main.projectPath) //
        .start(A0) //
        .derive(A0).to(A1, A2) //
        .derive(A1).to(A3, A4) //
        .derive(A3).to(A7, A8) //
        .derive(A7).to(a) //
        .derive(A8).to(a) //
        .derive(A4).to(A9, A10) //
        .derive(A9).to(a) //
        .derive(A10).to(a) //
        .derive(A2).to(A5, A6) //
        .derive(A5).to(A11, A12) //
        .derive(A11).to(a) //
        .derive(A12).to(a) //
        .derive(A6).to(A13, A14) //
        .derive(A13).to(a) //
        .derive(A14).to(a) //
    ;
  }
  public static void main(String[] args) throws IOException {
    new Exp2().generateGrammarFiles();
  }
}
