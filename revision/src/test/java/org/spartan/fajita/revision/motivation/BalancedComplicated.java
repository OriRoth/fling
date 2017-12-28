package org.spartan.fajita.revision.motivation;

public class BalancedComplicated {
  interface S¢_$<ε> {
    ¢SCS_<ε> o();
    Object $();
  }

  interface ¢SCS_<ε> {
    C¢SCS_o<S¢_$<ε>, ¢SCS_<ε>> o();
    S¢_$<ε> c();
  }

  interface C¢SCS_o<ε, o> {
    C¢SCS_o<S¢_c<C¢_o<ε, o>, ε>, C¢SCS_o<ε, o>> o();
    S¢_c<C¢_o<ε, o>, ε> c();
  }

  interface S¢_c<ε, c> {
    ¢SCS_c<ε, c> o();
    c c();
  }

  @SuppressWarnings("unused") interface C¢_o<ε, o> {
    ε c();
  }

  interface ¢SCS_c<ε, c> {
    C¢SCS_oc<S¢_c<ε, c>, ¢SCS_c<ε, c>, c> o();
    S¢_c<ε, c> c();
  }

  interface C¢SCS_oc<ε, o, c> {
    C¢SCS_oc<S¢_c<C¢_oc<ε, o, c>, ε>, C¢SCS_oc<ε, o, c>, S¢_c<C¢_oc<ε, o, c>, ε>> o();
    S¢_c<C¢_oc<ε, o, c>, ε> c();
  }

  @SuppressWarnings("unused") interface C¢_oc<ε, o, c> {
    ε c();
  }

  interface $ {
    Object $();
  }

  @SuppressWarnings("rawtypes") static class $$$ //
      implements S¢_$, ¢SCS_, C¢SCS_o, S¢_c, C¢_o, ¢SCS_c, C¢SCS_oc, C¢_oc, $ {
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
