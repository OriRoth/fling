package org.spartan.fajita.revision.export;

import org.spartan.fajita.revision.bnf.EBNF;
import org.spartan.fajita.revision.parser.ell.ELLRecognizer;
import org.spartan.fajita.revision.parser.ell.Interpretation;
import org.spartan.fajita.revision.symbols.Terminal;

public class FluentAPIRecorder {
  public final ELLRecognizer ell;

  public FluentAPIRecorder(EBNF ebnf) {
    this.ell = new ELLRecognizer(ebnf);
  }
  public void recordTerminal(Terminal t, Object... args) {
    ell.consume(new RuntimeVerb(t, args));
  }
  @Override public String toString() {
    return ast().toString();
  }
  public String toString(int ident) {
    return ast().toString(ident);
  }
  public void fold() {
    ast();
  }
  public Interpretation ast() {
    return ell.ast();
  }
}
