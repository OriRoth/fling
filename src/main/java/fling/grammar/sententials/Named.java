package fling.grammar.sententials;

public interface Named {
  String name();
  public static Named by(String name) {
    return new Named() {
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
        if (!(obj instanceof Named))
          return false;
        return name.equals(((Named) obj).name());
      }
    };
  }
}
