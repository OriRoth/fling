package sandbox;

import sandbox.Qʹ.Q;
import sandbox.Qʹ.¤;
import sandbox.Qʹ.Q.q1;
import sandbox.Qʹ.Q.q2;
import sandbox.Γ.γ1;
import sandbox.Γ.γ2;

public class Automaton {
}

abstract class Qʹ {
  static final class ¤ extends Qʹ {
  }

  static abstract class Q<CurrentStack extends Stack, Top1 extends Γ, Top2 extends Γ> extends Qʹ {
    abstract Qʹ σ1();
    abstract Qʹ σ2();

    static abstract class q1<CurrentStack extends Stack, Top1 extends Γ, Top2 extends Γ> extends Q<CurrentStack, Top1, Top2> {
    }

    static final class q1_1_1<CurrentStack extends Stack> extends q1<CurrentStack, γ1, γ1> {
      @Override q2 σ1() {
        return null;
      }
      ¤ σ2() {
        return null;
      }
    }

    static class q1_1_2<CurrentStack extends Stack> extends q1<CurrentStack, γ1, γ2> {      Q σ1() {
      return null;
    }
    ¤ σ2() {
      return null;
    }
    }

    static class q1_2_1<CurrentStack extends Stack> extends q1<CurrentStack, γ2, γ1> {      Q σ1() {
      return null;
    }
    ¤ σ2() {
      return null;
    }
    }

    static class q1_2_2<CurrentStack extends Stack> extends q1<CurrentStack, γ2, γ2> {      Q σ1() {
      return null;
    }
    ¤ σ2() {
      return null;
    }
    }

    static abstract class q2<CurrentStack extends Stack, Top1 extends Γ, Top2 extends Γ> extends Q<CurrentStack, Top1, Top2> {
    }

    static class q2_1_1<CurrentStack extends Stack> extends q2<CurrentStack, γ1, γ1> {      Q σ1() {
      return null;
    }
    ¤ σ2() {
      return null;
    }
    }

    static class q2_1_2<CurrentStack extends Stack> extends q2<CurrentStack, γ1, γ2> {      Q σ1() {
      return null;
    }
    ¤ σ2() {
      return null;
    }
    }

    static class q2_2_1<CurrentStack extends Stack> extends q2<CurrentStack, γ2, γ1> {      Q σ1() {
      return null;
    }
    ¤ σ2() {
      return null;
    }
    }

    static class q2_2_2<CurrentStack extends Stack> extends q2<CurrentStack, γ2, γ2> {      Q σ1() {
      return null;
    }
    ¤ σ2() {
      return null;
    }
    }
  }
