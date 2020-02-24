package il.ac.technion.cs.fling.examples.generated;

@SuppressWarnings("all")
public interface SimpleArithmeticAST {
  public class E {
    public final T t;
    public final E_ e_;

    public E(T t, E_ e_) {
      this.t = t;
      this.e_ = e_;
    }
  }

  public class T {
    public final F f;
    public final T_ t_;

    public T(F f, T_ t_) {
      this.f = f;
      this.t_ = t_;
    }
  }

  interface E_ {}

  interface F {}

  interface T_ {}

  public class E_1 implements E_ {
    public final T t;
    public final E_ e_;

    public E_1(T t, E_ e_) {
      this.t = t;
      this.e_ = e_;
    }
  }

  public class E_2 implements E_ {
    public E_2() {}
  }

  public class F1 implements F {
    public final E e;

    public F1(E e) {
      this.e = e;
    }
  }

  public class F2 implements F {
    public final java.lang.Integer i;

    public F2(java.lang.Integer i) {
      this.i = i;
    }
  }

  public class T_1 implements T_ {
    public final F f;
    public final T_ t_;

    public T_1(F f, T_ t_) {
      this.f = f;
      this.t_ = t_;
    }
  }

  public class T_2 implements T_ {
    public T_2() {}
  }

  public static class Visitor {
    public final void visit(il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E e) {
      try {
        this.whileVisiting(e);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T) e.t);
      visit((il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E_) e.e_);
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T t) {
      try {
        this.whileVisiting(t);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.F) t.f);
      visit((il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T_) t.t_);
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E_ e_) {
      if (e_ instanceof il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E_1)
        visit((il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E_1) e_);
      else if (e_ instanceof il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E_2)
        visit((il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E_2) e_);
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.F f) {
      if (f instanceof il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.F1)
        visit((il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.F1) f);
      else if (f instanceof il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.F2)
        visit((il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.F2) f);
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T_ t_) {
      if (t_ instanceof il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T_1)
        visit((il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T_1) t_);
      else if (t_ instanceof il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T_2)
        visit((il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T_2) t_);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E_1 e_1) {
      try {
        this.whileVisiting(e_1);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T) e_1.t);
      visit((il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E_) e_1.e_);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E_2 e_2) {
      try {
        this.whileVisiting(e_2);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.F1 f1) {
      try {
        this.whileVisiting(f1);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E) f1.e);
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.F2 f2) {
      try {
        this.whileVisiting(f2);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T_1 t_1) {
      try {
        this.whileVisiting(t_1);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.F) t_1.f);
      visit((il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T_) t_1.t_);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T_2 t_2) {
      try {
        this.whileVisiting(t_2);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E e)
        throws java.lang.Exception {}

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T t)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E_1 e_1)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.E_2 e_2)
        throws java.lang.Exception {}

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.F1 f1)
        throws java.lang.Exception {}

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.F2 f2)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T_1 t_1)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.T_2 t_2)
        throws java.lang.Exception {}
  }
}

