package il.ac.technion.cs.fling.examples.generated;

import java.util.*;

@SuppressWarnings("all")
public interface BalancedParenthesesAST {
  interface P {}

  public class P1 implements P {
    public final P p;
    public final P p2;

    public P1(P p, P p2) {
      this.p = p;
      this.p2 = p2;
    }
  }

  public class P2 implements P {
    public P2() {}
  }

  public static class Visitor {
    public final void visit(il.ac.technion.cs.fling.examples.generated.BalancedParenthesesAST.P p) {
      if (p instanceof il.ac.technion.cs.fling.examples.generated.BalancedParenthesesAST.P1)
        visit((il.ac.technion.cs.fling.examples.generated.BalancedParenthesesAST.P1) p);
      else if (p instanceof il.ac.technion.cs.fling.examples.generated.BalancedParenthesesAST.P2)
        visit((il.ac.technion.cs.fling.examples.generated.BalancedParenthesesAST.P2) p);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.BalancedParenthesesAST.P1 p1) {
      try {
        this.whileVisiting(p1);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.BalancedParenthesesAST.P) p1.p);
      visit((il.ac.technion.cs.fling.examples.generated.BalancedParenthesesAST.P) p1.p2);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.BalancedParenthesesAST.P2 p2) {
      try {
        this.whileVisiting(p2);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.BalancedParenthesesAST.P1 p1)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.BalancedParenthesesAST.P2 p2)
        throws java.lang.Exception {}
  }
}

