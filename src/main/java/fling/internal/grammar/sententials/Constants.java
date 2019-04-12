package fling.internal.grammar.sententials;

public interface Constants {
  Terminal $ = new Terminal() {
    @Override public String name() {
      return "$";
    }
    @Override public String toString() {
      return "$";
    }
  };
  Verb $$ = new Verb($);
  Variable S = new Variable() {
    @Override public String name() {
      return "S";
    }
    @Override public String toString() {
      return "S";
    }
  };
}
