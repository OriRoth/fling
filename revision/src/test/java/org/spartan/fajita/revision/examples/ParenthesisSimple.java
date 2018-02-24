package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.examples.ParenthesisSimple.NT.S;
import static org.spartan.fajita.revision.examples.ParenthesisSimple.Term.l;
import static org.spartan.fajita.revision.examples.ParenthesisSimple.Term.r;
import static org.spartan.fajita.revision.junk.ParenthesisSimple.l;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class ParenthesisSimple extends Grammar {
  public static enum Term implements Terminal {
    l, r
  }

  public static enum NT implements NonTerminal {
    S
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(ParenthesisSimple.class, Term.class, NT.class, "ParenthesisSimple", Main.packagePath, Main.projectPath)
        .start(S) //
        .derive(S).to(l, S, r, S).orNone();
  }
  public static void main(String[] args) throws IOException {
    new ParenthesisSimple().generateGrammarFiles();
  }
  public static void testing() {
    l().r().$().ω();
    l().l().r().l().r().r().$().ω();
    l().l().r().l().l().l().r().r().r().r().$().ω();
    // l().l().r().$();
    // l().l().r().r().r();
    // l().l().r().l().l().r().r().r().r();
  }
}
