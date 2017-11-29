package org.spartan.fajita.api.export;

import java.util.List;

import org.spartan.fajita.api.bnf.symbols.Terminal;

public class FluentAPIRecorder {
  private List<Terminal> methodCalls = an.empty.list();

  public void recordTerminal(Terminal t) {
    methodCalls.add(t);
    System.out.println(methodCalls);
  }
}
