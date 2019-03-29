package fling.grammar.sententials;

public interface Constants {
  public static final Terminal $ = new Terminal() {
    @Override public String name() {
      return "$";
    }
    @Override public String toString() {
      return "$";
    }
  };
  public static final Verb $$ = new Verb($);
  public static final Variable S = new Variable() {
    @Override public String name() {
      return "S";
    }
    @Override public String toString() {
      return "S";
    }
  };
}
