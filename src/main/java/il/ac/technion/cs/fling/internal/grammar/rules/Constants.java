package il.ac.technion.cs.fling.internal.grammar.rules;
public interface Constants {
  Token $$ = new Token(Terminal.$);
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
