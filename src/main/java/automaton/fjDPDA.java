package automaton;

/** JLR parser for the grammar :
 * S -> B
 * B -> Ab | Ac
 * A -> aA | a
 * 
 * that generates the language : L = a^+(b|c)
 */
@SuppressWarnings({ "rawtypes", "unused" }) class fjDPDA {
  static class A {
    static class L {
      /* empty */ }

    public interface E {
      /* Empty stack configuration */ }

    interface ¤ {
      /* Error configuration. */ }

    interface Cq0<Rest, JRa, JRb, JRc> extends q0a_Push_fbc_q5<Rest, JRa, JRb, JRc, Cq0<Rest, JRa, JRb, JRc>> {
      // inherited a()
    }

    interface Cq3<Rest, JRa, JRb, JRc> {
      L $();
    }

    interface Cq4<Rest, JRa, JRb, JRc> {
      L $();
    }

    interface Cq5<Rest, JRa, JRb, JRc> extends //
        q5a_Push_q5q5<Rest, JRa, JRb, JRc> {
      // inherited a()
      JRb b();
      JRc c();
    }

    //@formatter:off
    interface q5a_Push_q5q5<Rest, JRa, JRb, JRc> {
      Cq5<
        Cq5<Rest, JRa, JRb, JRc>,
        JRa,
        JRb,
        JRc
      > a();
    }
    //@formatter:on

    //@formatter:off
    interface q0a_Push_fbc_q5<Rest, JRa, JRb, JRc,Me> {
      Cq5<
        Me,
        JRa,
        Cq3<Me,JRa,JRb,JRc>,
        Cq4<Me,JRa,JRb,JRc>
      > a();
    }
    //@formatter:on

    static Cq0<E, ¤, ¤, ¤> build = null;

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