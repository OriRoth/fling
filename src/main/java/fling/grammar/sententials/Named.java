package fling.grammar.sententials;

public interface Named {
  String name();
  static Named by(String name) {
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
      @Override public boolean equals(Object o) {
        if (this == o)
          return true;
        if (!(o instanceof Named))
          return false;
        return name.equals(((Named) o).name());
      }
    };
  }
}
