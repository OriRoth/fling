package org.spartan.fajita.api.bnf.symbols;

public class Verb implements Terminal, Comparable<Verb> {
  public final Type type;
  private final String name;

  private Verb(String name, Class<?>... type) {
    this(name, new Type(type));
  }
  private Verb(String name, Type t) {
    this.name = name;
    this.type = t;
  }
  public Verb(Terminal terminal, Class<?>... type) {
    this(terminal.name(), type);
  }
  @Override public String toString() {
    return name + type.toString();
  }
  @Override public String name() {
    return name;
  }
  @Override public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
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
  @Override public int hashCode() {
    final int prime = 19;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }
  @Override public int compareTo(Verb v) {
    return toString().compareTo(v.toString());
  }
}
