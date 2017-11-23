package org.spartan.fajita.api.examples;

class $Twoparentsbnf {
  interface S {
    A a();
    B b();
  }

  interface augS {
    S s();
  }

  interface A {
    /* C c();D d(); */}

  interface B {
    /* C c();D d(); */}

  interface C extends A, B {
    Void a();
  }

  interface D extends A, B {
    Void a();
    Void b();
  }
}
