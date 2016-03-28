package org.spartan.fajita.api.bnf.symbols;

public class Verb implements Terminal {
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
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }
//  @Override public String serialize() {
//    return "%" + name() + "%" + "*" + type.serialize() + "*";
//  }
//  public static Verb deserialize(String data) {
//    Type t = Type.deserialize(data.substring(data.indexOf("*") + 1, data.lastIndexOf("*")));
//    String name = data.substring(data.indexOf("%") + 1, data.lastIndexOf("%"));
//    return new Verb(name, t);
//  }
}
