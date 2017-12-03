package org.spartan.fajita.revision.export;

import java.util.Arrays;

import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;

public class RuntimeVerb extends Verb {
  Terminal t;
  Object[] args;

  public RuntimeVerb(Terminal t, Object... args) {
    super(t);
    this.t = t;
    this.args = args;
  }
  public Object[] values() {
    return args;
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
    return v.accepts(args);
  }
  @Override public String name() {
    return t.name();
  }
  @Override public String toString() {
    return name() + Arrays.deepToString(args);
  }
}
