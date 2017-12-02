package org.spartan.fajita.api.export;

import java.util.Arrays;

import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;

public class RuntimeVerb extends Verb {
  Terminal t;
  Object[] args;

  public RuntimeVerb(Terminal t, Object... args) {
    super(t);
    this.t = t;
    this.args = args;
  }
  // TODO Roth: set proper hash with respect to Verb
  @Override public int hashCode() {
    return 0;
  }
  @Override public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null)
      return false;
    if (obj instanceof RuntimeVerb)
      return t.equals(((RuntimeVerb) obj).t) && Arrays.deepEquals(args, ((RuntimeVerb) obj).args);
    if (!(obj instanceof Verb))
      return false;
    Verb v = (Verb) obj;
    if (!t.equals(v.terminal))
      return false;
    return v.type.accepts(args);
  }
  @Override public String name() {
    return "#";
  }
}
