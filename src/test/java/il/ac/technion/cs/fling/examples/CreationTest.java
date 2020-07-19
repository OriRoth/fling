package il.ac.technion.cs.fling.examples;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.adapters.JavaMediator;
import il.ac.technion.cs.fling.examples.languages.*;
@SuppressWarnings("static-method") class CreationTest {
  @Test public void checkDatalog() {
    final var x = new Datalog();
    new JavaMediator(x.BNF(), "", x.name(), x.Σ());
  }
  @Test public void checkBNF() {
    final var x = new BNF();
    new JavaMediator(x.BNF(), "", x.name(), x.Σ());
  }
  @Test public void checkBalancedParentheses() {
    final var x = new BalancedParentheses();
    new JavaMediator(x.BNF(), "", x.name(), x.Σ());
  }
}
