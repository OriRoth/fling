package org.spartan.fajita.revision.motivation;

@SuppressWarnings({ "unused", "rawtypes" }) public class BalancedComplicated {
  interface S¢_$<ε> {
    ¢SCS_<ε> o();
    Object $();
  }

  interface ¢SCS_<ε> {
    ¢SCS_c<C¢_o<S¢_$<ε>, ¢SCS_<ε>>, S¢_$<ε>> o();
    S¢_$<ε> c();
  }

  interface C¢_o<ε, o> {
    ε c();
  }

  interface ¢SCS_c<ε, c> {
    ¢SCS_c<C¢_oc<S¢_c<ε, c>, ¢SCS_c<ε, c>, c>, S¢_c<ε, c>> o();
    S¢_c<ε, c> c();
  }

  interface C¢_oc<ε, o, c> {
    ε c();
  }

  interface S¢_c<ε, c> {
    ¢SCS_c<ε, c> o();
    c c();
  }

  interface $ {
    Object $();
  }

  static class $$$ //
      implements S¢_$, ¢SCS_, C¢_o, ¢SCS_c, C¢_oc, S¢_c, $ {
    @Override public $$$ o() {
      return this;
    }
    @Override public $$$ c() {
      return this;
    }
    @Override public Object $() {
      return new Object();
    }
  }

  @SuppressWarnings("unchecked") static ¢SCS_<$> o() {
    return new $$$();
  }
  public static void main(String[] args) {
    o().c().$();
    o().o().c().o().c().c().$();
  }
}
