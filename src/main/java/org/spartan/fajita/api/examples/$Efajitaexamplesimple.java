package org.spartan.fajita.api.examples;

class $Efajitaexamplesimple {
  class S {
    EitherTextA e1;
  }

  class EitherTextA {
    private String text1;

    boolean isText() {
      return text1 == null;
    }
    String getText() {
      return text1;
    }

    private S a1;

    boolean isA() {
      return a1 == null;
    }
    S getA() {
      return a1;
    }
  }
}