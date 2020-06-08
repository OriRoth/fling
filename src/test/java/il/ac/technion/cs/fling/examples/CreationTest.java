package il.ac.technion.cs.fling.examples;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import il.ac.technion.cs.fling.adapters.JavaMediator;
import il.ac.technion.cs.fling.examples.languages.*;
import il.ac.technion.cs.fling.examples.languages.BalancedParentheses;

class CreationTest {
  @Test public void checkDatalog() {
    Datalog x = new Datalog();
    new JavaMediator(x.BNF(), "", x.name(), x.Σ());
  }

  @Test public void checkBNF() {
    BNF x = new BNF();
    new JavaMediator(x.BNF(), "", x.name(), x.Σ());
  }

  @Test public void checkBalancedParentheses() {
    BalancedParentheses x = new BalancedParentheses();
    new JavaMediator(x.BNF(), "", x.name(), x.Σ());
  }

}
