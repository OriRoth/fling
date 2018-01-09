package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.api.Fajita.option;
import static org.spartan.fajita.revision.examples.Exp2.NT.*;
import static org.spartan.fajita.revision.examples.Exp2.Term.*;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class Exp2 extends Grammar {
  public static enum Term implements Terminal {
    a, //
    a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22, a23, a24, a25, a26, a27, a28, a29, a30
  }

  public static enum NT implements NonTerminal {
    S, //
    A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, A26, A27, A28, A29, A30
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(Exp2.class, Term.class, NT.class, "Exp2", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to(A0) //
        .derive(A0).to(a0, option(A1), a0, option(A2), a0) //
        .derive(A1).to(a1, option(A3), a1, option(A4), a1) //
        .derive(A3).to(a3, option(A7), a3, option(A8), a3) //
        .derive(A7).to(a7, option(A15), a7, option(A16), a7) //
        .derive(A15).to(a15) //
        .derive(A16).to(a16) //
        .derive(A8).to(a8, option(A17), a8, option(A18), a8) //
        .derive(A17).to(a17) //
        .derive(A18).to(a18) //
        .derive(A4).to(a4, option(A9), a4, option(A10), a4) //
        .derive(A9).to(a9, option(A19), a9, option(A20), a9) //
        .derive(A19).to(a19) //
        .derive(A20).to(a20) //
        .derive(A10).to(a10, option(A21), a10, option(A22), a10) //
        .derive(A21).to(a21) //
        .derive(A22).to(a22) //
        .derive(A2).to(a2, option(A5), a2, option(A6), a2) //
        .derive(A5).to(a5, option(A11), a5, option(A12), a5) //
        .derive(A11).to(a11, option(A23), a11, option(A24), a11) //
        .derive(A23).to(a23) //
        .derive(A24).to(a24) //
        .derive(A12).to(a12, option(A25), a12, option(A26), a12) //
        .derive(A25).to(a25) //
        .derive(A26).to(a26) //
        .derive(A6).to(a6, option(A13), a6, option(A14), a6) //
        .derive(A13).to(a13, option(A27), a13, option(A28), a13) //
        .derive(A27).to(a27) //
        .derive(A28).to(a28) //
        .derive(A14).to(a14, option(A29), a14, option(A30), a14) //
        .derive(A29).to(a29) //
        .derive(A30).to(a30) //
    ;
  }
  public static void main(String[] args) throws IOException {
    new Exp2().generateGrammarFiles();
  }
}
