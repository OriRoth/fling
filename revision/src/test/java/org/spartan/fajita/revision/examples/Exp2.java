package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.api.Fajita.option;
import static org.spartan.fajita.revision.api.Fajita.oneOrMore;
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
        .derive(S).to(oneOrMore(a, A0)) //
        .derive(A0).to(option(a0), option(A1), option(A2)) //
        .derive(A1).to(option(a1), option(A3), option(A4)) //
        .derive(A3).to(option(a3), option(A7), option(A8)) //
        .derive(A7).to(option(a7), option(A15), option(A16)) //
        .derive(A15).to(option(a15)) //
        .derive(A16).to(option(a16)) //
        .derive(A8).to(option(a8), option(A17), option(A18)) //
        .derive(A17).to(option(a17)) //
        .derive(A18).to(option(a18)) //
        .derive(A4).to(option(a4), option(A9), option(A10)) //
        .derive(A9).to(option(a9), option(A19), option(A20)) //
        .derive(A19).to(option(a19)) //
        .derive(A20).to(option(a20)) //
        .derive(A10).to(option(a10), option(A21), option(A22)) //
        .derive(A21).to(option(a21)) //
        .derive(A22).to(option(a22)) //
        .derive(A2).to(option(a2), option(A5), option(A6)) //
        .derive(A5).to(option(a5), option(A11), option(A12)) //
        .derive(A11).to(option(a11), option(A23), option(A24)) //
        .derive(A23).to(option(a23)) //
        .derive(A24).to(option(a24)) //
        .derive(A12).to(option(a12), option(A25), option(A26)) //
        .derive(A25).to(option(a25)) //
        .derive(A26).to(option(a26)) //
        .derive(A6).to(option(a6), option(A13), option(A14)) //
        .derive(A13).to(option(a13), option(A27), option(A28)) //
        .derive(A27).to(option(a27)) //
        .derive(A28).to(option(a28)) //
        .derive(A14).to(option(a14), option(A29), option(A30)) //
        .derive(A29).to(option(a29)) //
        .derive(A30).to(option(a30)) //
    ;
  }
  public static void main(String[] args) throws IOException {
    new Exp2().generateGrammarFiles();
  }
}
