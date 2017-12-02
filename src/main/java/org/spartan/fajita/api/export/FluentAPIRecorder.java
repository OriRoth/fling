package org.spartan.fajita.api.export;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.rllp.RLLPConcrete;

public class FluentAPIRecorder {
  private final RLLPConcrete rllp;

  public FluentAPIRecorder(BNF bnf) {
    this.rllp = new RLLPConcrete(bnf);
  }
  public void recordTerminal(Terminal t) {
    rllp.consume(t);
    assert !rllp.rejected() : "RLLP has rejected...";
  }
}
