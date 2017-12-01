package org.spartan.fajita.api.bnf;

import org.spartan.fajita.api.EFajita;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;

public class EVerb extends Verb {
  public ENonTerminal ent;

  public EVerb(Terminal terminal, ENonTerminal ent) {
    super(terminal); // TODO Roth: initialize with an error type (?)
    this.ent = ent;
  }
  public Verb bind(EFajita builder, NonTerminal lhs) {
    return new Verb(name(), (NonTerminal) builder.solve(lhs, ent)); // TODO Roth: always a non terminal?
  }
}
