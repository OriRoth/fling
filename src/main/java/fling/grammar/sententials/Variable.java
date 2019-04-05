package fling.grammar.sententials;

public interface Variable extends Symbol {
  public static Variable byName(String name) {
    return new Variable() {
      @Override public String name() {
        return name;
      }
      @Override public String toString() {
        return name;
      }
      @Override public int hashCode() {
        return name.hashCode();
      }
      @Override public boolean equals(Object obj) {
        if (this == obj)
          return true;
        if (!(obj instanceof Variable))
          return false;
        Variable other = (Variable) obj;
        return name().equals(other.name());
      }
    };
  }
}
