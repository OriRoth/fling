package fling.sententials;

public interface Constants {
  public static final Terminal $ = new Terminal() {
    @Override public String toString() {
      return "$";
    }
  };
  public static final Variable S = new Variable() {
    @Override public String toString() {
      return "S";
    }
  };
}
