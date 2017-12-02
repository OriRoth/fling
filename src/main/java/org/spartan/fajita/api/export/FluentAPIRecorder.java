package org.spartan.fajita.api.export;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.ll.LLRecognizer;

public class FluentAPIRecorder {
  private final LLRecognizer ll;

  public FluentAPIRecorder(BNF bnf) {
    this.ll = new LLRecognizer(bnf);
  }
  public void recordTerminal(Terminal t, Object... args) {
    ll.consume(new RuntimeVerb(t, args));
    assert !ll.rejected() : "RLLP has rejected...";
  }
  @Override public String toString() {
    return ll.ast().toString();
  }
}
