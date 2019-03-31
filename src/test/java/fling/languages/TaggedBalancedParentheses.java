package fling.languages;

import static fling.grammar.BNF.bnf;
import static fling.languages.TaggedBalancedParentheses.V.AB;
import static fling.languages.TaggedBalancedParentheses.V.P;
import static fling.languages.TaggedBalancedParentheses.Σ.a;
import static fling.languages.TaggedBalancedParentheses.Σ.b;
import static fling.languages.TaggedBalancedParentheses.Σ.c;
import static fling.languages.TaggedBalancedParentheses.Σ.ↄ;

import fling.adapters.JavaMediator;
import fling.grammar.BNF;
import fling.grammar.sententials.Terminal;
import fling.grammar.sententials.Variable;

public class TaggedBalancedParentheses {
  public enum Σ implements Terminal {
    c, ↄ, a, b
  }

  public enum V implements Variable {
    P, AB
  }

  public static final BNF bnf = bnf(V.class). //
      start(P). //
      derive(P, c.many(char.class), P, ↄ.with(AB), P). //
      derive(P). //
      derive(AB, a). //
      derive(AB, b). //
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, "fling.generated", "TaggedBalancedParentheses", Σ.class);
  // public static void compilationTest() {
  // __().c('a').ↄ('b').$();
  // __().c('a').ↄ('b').ↄ('c');
  // __().c('a').c('b').c('c').ↄ('d').ↄ('e');
  // __().c('a').c('b').c('c').ↄ('d').ↄ('e').ↄ('f').$();
  // __().c('a').c('b').ↄ('c').ↄ('d').c('e').ↄ('f').$();
  // __().c('a').c('b').ↄ('c').ↄ('d').c('e');
  // __().ↄ('a');
  // }
  // public static void main(String[] args) {
  // P parseTree = __().c('a', 'a').c('b').ↄ('c').c('d').ↄ('e').ↄ('f').$();
  // traverse(parseTree, 0);
  // }
  // private static void traverse(P p, int depth) {
  // if (p instanceof P1)
  // traverse((P1) p, depth);
  // else
  // traverse((P2) p, depth);
  // }
  // private static void traverse(P1 p, int depth) {
  // for (int i = 0; i < depth; ++i)
  // System.out.print('\t');
  // System.out.println(p.c);
  // traverse(p.p, depth + 1);
  // for (int i = 0; i < depth; ++i)
  // System.out.print('\t');
  // System.out.println(p.c2);
  // traverse(p.p2, depth);
  // }
  // @SuppressWarnings("unused") private static void traverse(P2 p, int depth) {
  // // Relax.
  // }
}
