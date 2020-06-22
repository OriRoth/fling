package il.ac.technion.cs.fling.internal.grammar.rules;
public interface Constants {
  Terminal $ = new Terminal() {
    @Override public String name() {
      return "$";
    }
    @Override public String toString() {
      return "$";
    }
  };
  Token $$ = new Token($);
  Variable S = new Variable() {
    @Override public String name() {
      return "S";
    }
    @Override public String toString() {
      return "S";
    }
  };
  String intermediateVariableName = "_$_";
}
