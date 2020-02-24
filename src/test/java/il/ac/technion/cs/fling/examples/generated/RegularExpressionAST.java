package il.ac.technion.cs.fling.examples.generated;

import java.util.*;

@SuppressWarnings("all")
public interface RegularExpressionAST {
  public class Expression {
    public final RE rE;

    public Expression(RE rE) {
      this.rE = rE;
    }
  }

  interface RE {}

  interface Tail {}

  public class RE1 implements RE {
    public final java.lang.String exactly;
    public final Tail tail;

    public RE1(java.lang.String exactly, Tail tail) {
      this.exactly = exactly;
      this.tail = tail;
    }
  }

  public class RE2 implements RE {
    public final RE option;
    public final Tail tail;

    public RE2(RE option, Tail tail) {
      this.option = option;
      this.tail = tail;
    }
  }

  public class RE3 implements RE {
    public final RE noneOrMore;
    public final Tail tail;

    public RE3(RE noneOrMore, Tail tail) {
      this.noneOrMore = noneOrMore;
      this.tail = tail;
    }
  }

  public class RE4 implements RE {
    public final RE oneOrMore;
    public final Tail tail;

    public RE4(RE oneOrMore, Tail tail) {
      this.oneOrMore = oneOrMore;
      this.tail = tail;
    }
  }

  public class RE5 implements RE {
    public final RE[] either;
    public final Tail tail;

    public RE5(RE[] either, Tail tail) {
      this.either = either;
      this.tail = tail;
    }
  }

  public class RE6 implements RE {
    public final Tail tail;

    public RE6(Tail tail) {
      this.tail = tail;
    }
  }

  public class RE7 implements RE {
    public final Tail tail;

    public RE7(Tail tail) {
      this.tail = tail;
    }
  }

  public class Tail1 implements Tail {
    public final RE rE;

    public Tail1(RE rE) {
      this.rE = rE;
    }
  }

  public class Tail2 implements Tail {
    public final RE rE;

    public Tail2(RE rE) {
      this.rE = rE;
    }
  }

  public class Tail3 implements Tail {
    public Tail3() {}
  }

  public static class Visitor {
    public final void visit(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Expression expression) {
      try {
        this.whileVisiting(expression);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE) expression.rE);
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE rE) {
      if (rE instanceof il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE1)
        visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE1) rE);
      else if (rE instanceof il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE2)
        visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE2) rE);
      else if (rE instanceof il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE3)
        visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE3) rE);
      else if (rE instanceof il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE4)
        visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE4) rE);
      else if (rE instanceof il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE5)
        visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE5) rE);
      else if (rE instanceof il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE6)
        visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE6) rE);
      else if (rE instanceof il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE7)
        visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE7) rE);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail tail) {
      if (tail instanceof il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail1)
        visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail1) tail);
      else if (tail
          instanceof il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail2)
        visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail2) tail);
      else if (tail
          instanceof il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail3)
        visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail3) tail);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE1 rE1) {
      try {
        this.whileVisiting(rE1);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail) rE1.tail);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE2 rE2) {
      try {
        this.whileVisiting(rE2);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail) rE2.tail);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE3 rE3) {
      try {
        this.whileVisiting(rE3);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail) rE3.tail);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE4 rE4) {
      try {
        this.whileVisiting(rE4);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail) rE4.tail);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE5 rE5) {
      try {
        this.whileVisiting(rE5);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail) rE5.tail);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE6 rE6) {
      try {
        this.whileVisiting(rE6);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail) rE6.tail);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE7 rE7) {
      try {
        this.whileVisiting(rE7);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail) rE7.tail);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail1 tail1) {
      try {
        this.whileVisiting(tail1);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE) tail1.rE);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail2 tail2) {
      try {
        this.whileVisiting(tail2);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE) tail2.rE);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail3 tail3) {
      try {
        this.whileVisiting(tail3);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Expression expression)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE1 rE1)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE2 rE2)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE3 rE3)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE4 rE4)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE5 rE5)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE6 rE6)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE7 rE7)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail1 tail1)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail2 tail2)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Tail3 tail3)
        throws java.lang.Exception {}
  }
}

