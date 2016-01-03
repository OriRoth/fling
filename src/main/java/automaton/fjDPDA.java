package automaton;

/** JLR parser for the grammar :
 * S -> B
 * B -> Ab | Ac
 * A -> aA | a
 * 
 * that generates the language : L = a^+(b|c)
 * 
 * δ( q₀ , a ) = q₀ ❴ b→q₂ q₃ , c→q₂ q₄ ❵ q₅
 * δ(q₅,a) = q₅ q₅
 * δ(q₅,b) = jump(b)
 * δ(q₅,c) = jump(c)
 * δ(q₃,$) = jump($)
 * δ(q₄,$) = jump($)
 */
class fjDPDA {
  static class A {
    static class L {
      /* empty */ }

    public interface E { /* Empty stack configuration */ }

    interface ¤ { /* Error configuration. */ }

    interface Cq0<Rest, JRb, JRc> extends q0a_Push_fbc_q5<Rest, JRb, JRc, Cq0<Rest, JRb, JRc>> {
      // inherited a()
    }

    interface Cq3<Rest, JRb, JRc> {
      L $();
    }

    interface Cq4<Rest, JRb, JRc> {
      L $();
    }

    interface Cq5<Rest, JRb, JRc> extends //
        q5a_Push_q5q5<Rest, JRb, JRc> {
      // inherited a()
      JRb b();
      JRc c();
    }

    //@formatter:off
    interface q5a_Push_q5q5<Rest, JRb, JRc> {
      Cq5<
        Cq5<Rest, JRb, JRc>,
        JRb,
        JRc
      > a();
    }
    //@formatter:on

    //@formatter:off
    interface q0a_Push_fbc_q5<Rest, JRb, JRc,Me> {
      Cq5<
        Me,
        Cq3<Me,JRb,JRc>,
        Cq4<Me,JRb,JRc>
      > a();
    }
    //@formatter:on

    static Cq0<E, ¤, ¤> build = null;

    static void accepts() {
      A.build.a().b().$();
      A.build.a().c().$();
      A.build.a().a().a().b().$();
      A.build.a().a().a().c().$();
    }
    static void rejects() {
      A.build.b();
      A.build.$();
      A.build.a().b().b();
      A.build.a().c().a();
    }
  }
}