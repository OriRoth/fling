/* This file was automatically generated by Fling (c) on Mon Feb 24 19:22:06 IST 2020 */

package il.ac.technion.cs.fling.examples.generated;

import java.util.*;

@SuppressWarnings("all")
public interface RegularExpression {
  public static q0ø_RE$_q0$q0ø<q0$_$_q0$<$>, $> re() {
    α α = new α();
    α.w.add(
        new il.ac.technion.cs.fling.internal.compiler.Assignment(
            il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.re));
    return α;
  }

  interface $ {
    il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Expression $();
  }

  interface q0$_$_q0$<q0$> extends $ {}

  interface q0ø_RE$_q0$q0ø<q0$, q0ø> {
    q0$_Tail$_q0$q0ø<q0$, q0ø> exactly(java.lang.String string);

    q0$_Tail$_q0$q0ø<q0$, q0ø> option(
        il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$ rE);

    q0$_Tail$_q0$q0ø<q0$, q0ø> noneOrMore(
        il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$ rE);

    q0$_Tail$_q0$q0ø<q0$, q0ø> oneOrMore(
        il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$ rE);

    q0$_Tail$_q0$q0ø<q0$, q0ø> either(
        il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$... rEs);

    q0$_Tail$_q0$q0ø<q0$, q0ø> anyChar();

    q0$_Tail$_q0$q0ø<q0$, q0ø> anyDigit();
  }

  interface q0$_Tail$_q0$q0ø<q0$, q0ø> extends $ {
    q0ø_RE$_q0$q0ø<q0$, q0ø> and();

    q0ø_RE$_q0$q0ø<q0$, q0ø> or();
  }

  static class α implements $, q0$_$_q0$, q0ø_RE$_q0$q0ø, q0$_Tail$_q0$q0ø {
    public java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w =
        new java.util.LinkedList();

    public α re() {
      this.w.add(
          new il.ac.technion.cs.fling.internal.compiler.Assignment(
              il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.re, new Object[] {}));
      return this;
    }

    public α exactly(java.lang.String string) {
      this.w.add(
          new il.ac.technion.cs.fling.internal.compiler.Assignment(
              il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.exactly,
              new Object[] {string}));
      return this;
    }

    public α option(il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$ rE) {
      this.w.add(
          new il.ac.technion.cs.fling.internal.compiler.Assignment(
              il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.option,
              new Object[] {
                ((il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.α) rE).$()
              }));
      return this;
    }

    public α noneOrMore(il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$ rE) {
      this.w.add(
          new il.ac.technion.cs.fling.internal.compiler.Assignment(
              il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.noneOrMore,
              new Object[] {
                ((il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.α) rE).$()
              }));
      return this;
    }

    public α oneOrMore(il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$ rE) {
      this.w.add(
          new il.ac.technion.cs.fling.internal.compiler.Assignment(
              il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.oneOrMore,
              new Object[] {
                ((il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.α) rE).$()
              }));
      return this;
    }

    public α either(il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$... rEs) {
      this.w.add(
          new il.ac.technion.cs.fling.internal.compiler.Assignment(
              il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.either,
              new Object[] {
                java.util.Arrays.stream(rEs)
                    .map(
                        il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.α.class
                            ::cast)
                    .map(il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.α::$)
                    .toArray(
                        il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE[]::new)
              }));
      return this;
    }

    public α anyChar() {
      this.w.add(
          new il.ac.technion.cs.fling.internal.compiler.Assignment(
              il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.anyChar,
              new Object[] {}));
      return this;
    }

    public α anyDigit() {
      this.w.add(
          new il.ac.technion.cs.fling.internal.compiler.Assignment(
              il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.anyDigit,
              new Object[] {}));
      return this;
    }

    public α and() {
      this.w.add(
          new il.ac.technion.cs.fling.internal.compiler.Assignment(
              il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.and, new Object[] {}));
      return this;
    }

    public α or() {
      this.w.add(
          new il.ac.technion.cs.fling.internal.compiler.Assignment(
              il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.or, new Object[] {}));
      return this;
    }

    public il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.Expression $() {
      return il.ac.technion.cs.fling.examples.generated.RegularExpressionCompiler.parse_Expression(
          w);
    }
  } /* This file was automatically generated by Fling (c) on Mon Feb 24 19:22:06 IST 2020 */

  @SuppressWarnings("all")
  public interface RE {
    public static q0$_Tail$_q0$q0ø<q0$_$_q0$<$>, $> exactly(java.lang.String string) {
      α α = new α();
      α.w.add(
          new il.ac.technion.cs.fling.internal.compiler.Assignment(
              il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.exactly, string));
      return α;
    }

    public static q0$_Tail$_q0$q0ø<q0$_$_q0$<$>, $> option(
        il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$ rE) {
      α α = new α();
      α.w.add(
          new il.ac.technion.cs.fling.internal.compiler.Assignment(
              il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.option,
              ((il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.α) rE).$()));
      return α;
    }

    public static q0$_Tail$_q0$q0ø<q0$_$_q0$<$>, $> noneOrMore(
        il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$ rE) {
      α α = new α();
      α.w.add(
          new il.ac.technion.cs.fling.internal.compiler.Assignment(
              il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.noneOrMore,
              ((il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.α) rE).$()));
      return α;
    }

    public static q0$_Tail$_q0$q0ø<q0$_$_q0$<$>, $> oneOrMore(
        il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$ rE) {
      α α = new α();
      α.w.add(
          new il.ac.technion.cs.fling.internal.compiler.Assignment(
              il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.oneOrMore,
              ((il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.α) rE).$()));
      return α;
    }

    public static q0$_Tail$_q0$q0ø<q0$_$_q0$<$>, $> either(
        il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$... rEs) {
      α α = new α();
      α.w.add(
          new il.ac.technion.cs.fling.internal.compiler.Assignment(
              il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.either,
              java.util.Arrays.stream(rEs)
                  .map(
                      il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.α.class::cast)
                  .map(il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.α::$)
                  .toArray(
                      il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE[]::new)));
      return α;
    }

    public static q0$_Tail$_q0$q0ø<q0$_$_q0$<$>, $> anyChar() {
      α α = new α();
      α.w.add(
          new il.ac.technion.cs.fling.internal.compiler.Assignment(
              il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.anyChar));
      return α;
    }

    public static q0$_Tail$_q0$q0ø<q0$_$_q0$<$>, $> anyDigit() {
      α α = new α();
      α.w.add(
          new il.ac.technion.cs.fling.internal.compiler.Assignment(
              il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.anyDigit));
      return α;
    }

    interface $ {}

    interface q0$_$_q0$<q0$> extends $ {}

    interface q0$_Tail$_q0$q0ø<q0$, q0ø> extends $ {
      q0ø_RE$_q0$q0ø<q0$, q0ø> and();

      q0ø_RE$_q0$q0ø<q0$, q0ø> or();
    }

    interface q0ø_RE$_q0$q0ø<q0$, q0ø> {
      q0$_Tail$_q0$q0ø<q0$, q0ø> exactly(java.lang.String string);

      q0$_Tail$_q0$q0ø<q0$, q0ø> option(
          il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$ rE);

      q0$_Tail$_q0$q0ø<q0$, q0ø> noneOrMore(
          il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$ rE);

      q0$_Tail$_q0$q0ø<q0$, q0ø> oneOrMore(
          il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$ rE);

      q0$_Tail$_q0$q0ø<q0$, q0ø> either(
          il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$... rEs);

      q0$_Tail$_q0$q0ø<q0$, q0ø> anyChar();

      q0$_Tail$_q0$q0ø<q0$, q0ø> anyDigit();
    }

    static class α implements $, q0$_$_q0$, q0$_Tail$_q0$q0ø, q0ø_RE$_q0$q0ø {
      public java.util.List<il.ac.technion.cs.fling.internal.compiler.Assignment> w =
          new java.util.LinkedList();

      public α exactly(java.lang.String string) {
        this.w.add(
            new il.ac.technion.cs.fling.internal.compiler.Assignment(
                il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.exactly,
                new Object[] {string}));
        return this;
      }

      public α option(il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$ rE) {
        this.w.add(
            new il.ac.technion.cs.fling.internal.compiler.Assignment(
                il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.option,
                new Object[] {
                  ((il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.α) rE).$()
                }));
        return this;
      }

      public α noneOrMore(il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$ rE) {
        this.w.add(
            new il.ac.technion.cs.fling.internal.compiler.Assignment(
                il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.noneOrMore,
                new Object[] {
                  ((il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.α) rE).$()
                }));
        return this;
      }

      public α oneOrMore(il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$ rE) {
        this.w.add(
            new il.ac.technion.cs.fling.internal.compiler.Assignment(
                il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.oneOrMore,
                new Object[] {
                  ((il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.α) rE).$()
                }));
        return this;
      }

      public α either(il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.$... rEs) {
        this.w.add(
            new il.ac.technion.cs.fling.internal.compiler.Assignment(
                il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.either,
                new Object[] {
                  java.util.Arrays.stream(rEs)
                      .map(
                          il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.α.class
                              ::cast)
                      .map(il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.α::$)
                      .toArray(
                          il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE[]::new)
                }));
        return this;
      }

      public α anyChar() {
        this.w.add(
            new il.ac.technion.cs.fling.internal.compiler.Assignment(
                il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.anyChar,
                new Object[] {}));
        return this;
      }

      public α anyDigit() {
        this.w.add(
            new il.ac.technion.cs.fling.internal.compiler.Assignment(
                il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.anyDigit,
                new Object[] {}));
        return this;
      }

      public α and() {
        this.w.add(
            new il.ac.technion.cs.fling.internal.compiler.Assignment(
                il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.and,
                new Object[] {}));
        return this;
      }

      public α or() {
        this.w.add(
            new il.ac.technion.cs.fling.internal.compiler.Assignment(
                il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.or,
                new Object[] {}));
        return this;
      }

      public il.ac.technion.cs.fling.examples.generated.RegularExpressionAST.RE $() {
        return il.ac.technion.cs.fling.examples.generated.RegularExpressionCompiler.parse_RE(w);
      }
    }
  }
}

