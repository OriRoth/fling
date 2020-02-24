package il.ac.technion.cs.fling.examples.generated;

import java.util.*;

@SuppressWarnings("all")
public interface TaggedBalancedParenthesesAST {
  interface P {}

  interface AB {}

  public class P1 implements P {
    public final char[] c;
    public final P p;
    public final AB ↄ;
    public final P p2;

    public P1(char[] c, P p, AB ↄ, P p2) {
      this.c = c;
      this.p = p;
      this.ↄ = ↄ;
      this.p2 = p2;
    }
  }

  public class P2 implements P {
    public P2() {}
  }

  public class AB1 implements AB {
    public AB1() {}
  }

  public class AB2 implements AB {
    public final java.util.List<java.lang.Integer> b;

    public AB2(java.util.List<java.lang.Integer> b) {
      this.b = b;
    }
  }

  public static class Visitor {
    public final void visit(
        il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P p) {
      if (p instanceof il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P1)
        visit((il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P1) p);
      else if (p
          instanceof il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P2)
        visit((il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P2) p);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.AB aB) {
      if (aB instanceof il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.AB1)
        visit((il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.AB1) aB);
      else if (aB
          instanceof il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.AB2)
        visit((il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.AB2) aB);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P1 p1) {
      try {
        this.whileVisiting(p1);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P) p1.p);
      visit((il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P) p1.p2);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P2 p2) {
      try {
        this.whileVisiting(p2);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.AB1 aB1) {
      try {
        this.whileVisiting(aB1);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.AB2 aB2) {
      try {
        this.whileVisiting(aB2);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P1 p1)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P2 p2)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.AB1 aB1)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.AB2 aB2)
        throws java.lang.Exception {}
  }
}

