package org.spartan.fajita.api.bnf.symbols;

import org.spartan.fajita.api.bnf.symbols.type.ClassesType;
import org.spartan.fajita.api.bnf.symbols.type.NestedType;
import org.spartan.fajita.api.bnf.symbols.type.ParameterType;
import org.spartan.fajita.api.export.RuntimeVerb;

public class Verb implements Terminal, Comparable<Verb> {
  public final Terminal terminal;
  public final ParameterType type;
  private final String name;

  public Verb(Terminal terminal, ParameterType t) {
    this.terminal = terminal;
    this.name = terminal.name();
    this.type = t;
  }
  public Verb(Terminal terminal, Class<?>... type) {
    this(terminal, new ClassesType(type));
  }
  public Verb(Terminal terminal, NonTerminal nested) {
    this(terminal, new NestedType(nested));
  }
  @Override public String toString() {
    return name + "(" + type.toString() + ")";
  }
  @Override public String name() {
    return name;
  }
  @Override public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (obj instanceof RuntimeVerb)
      return obj.equals(this);
    if (!(obj instanceof Verb))
      return false;
    Verb other = (Verb) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    return true;
  }
  // TODO Roth: set proper hash with respect to RuntimeVerb
  @Override public int hashCode() {
    // final int prime = 19;
    // int result = 1;
    // result = prime * result + ((name == null) ? 0 : name.hashCode());
    // result = prime * result + ((type == null) ? 0 : type.hashCode());
    // return result;
    return 0;
  }
  @Override public int compareTo(Verb v) {
    return toString().compareTo(v.toString());
  }
}
