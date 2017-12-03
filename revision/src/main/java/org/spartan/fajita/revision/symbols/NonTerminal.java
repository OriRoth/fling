package org.spartan.fajita.revision.symbols;

public interface NonTerminal extends Symbol {
  default String name(String packagePath) {
    return packagePath + "." + name();
  }
  default String name(String packagePath, String containingClass) {
    return packagePath + "." + containingClass + "." + name();
  }
  public default String toString2() {
    return "<" + name() + ">";
  }
  public static NonTerminal of(String name) {
    return new NonTerminal() {
      @Override public String name() {
        return name;
      }
      @Override public boolean equals(Object obj) {
        return obj instanceof NonTerminal && name.equals(((NonTerminal) obj).name());
      }
      @Override public int hashCode() {
        return name.hashCode();
      }
    };
  }
}