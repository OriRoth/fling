package il.ac.technion.cs.fling.examples.generated;

import java.util.*;

@SuppressWarnings("all")
public interface SubFigureAST {
  interface Figure {}

  interface Orientation {}

  public class Figure1 implements Figure {
    public final java.lang.String load;

    public Figure1(java.lang.String load) {
      this.load = load;
    }
  }

  public class Figure2 implements Figure {
    public final Orientation orientation;
    public final java.util.List<Figure> figure;

    public Figure2(Orientation orientation, java.util.List<Figure> figure) {
      this.orientation = orientation;
      this.figure = figure;
    }
  }

  public class Orientation1 implements Orientation {
    public Orientation1() {}
  }

  public class Orientation2 implements Orientation {
    public Orientation2() {}
  }

  public static class Visitor {
    public final void visit(il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure figure) {
      if (figure instanceof il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure1)
        visit((il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure1) figure);
      else if (figure instanceof il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure2)
        visit((il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure2) figure);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.SubFigureAST.Orientation orientation) {
      if (orientation
          instanceof il.ac.technion.cs.fling.examples.generated.SubFigureAST.Orientation1)
        visit((il.ac.technion.cs.fling.examples.generated.SubFigureAST.Orientation1) orientation);
      else if (orientation
          instanceof il.ac.technion.cs.fling.examples.generated.SubFigureAST.Orientation2)
        visit((il.ac.technion.cs.fling.examples.generated.SubFigureAST.Orientation2) orientation);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure1 figure1) {
      try {
        this.whileVisiting(figure1);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure2 figure2) {
      try {
        this.whileVisiting(figure2);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit(
          (il.ac.technion.cs.fling.examples.generated.SubFigureAST.Orientation)
              figure2.orientation);
      figure2
          .figure
          .stream()
          .forEach(
              _x_ -> visit((il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure) _x_));
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.SubFigureAST.Orientation1 orientation1) {
      try {
        this.whileVisiting(orientation1);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.SubFigureAST.Orientation2 orientation2) {
      try {
        this.whileVisiting(orientation2);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure1 figure1)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.SubFigureAST.Figure2 figure2)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.SubFigureAST.Orientation1 orientation1)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.SubFigureAST.Orientation2 orientation2)
        throws java.lang.Exception {}
  }
}

