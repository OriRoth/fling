package org.spartan.fajita.revision.motivation;

public class FluentMath {
  interface NonNegative {
  }

  interface Zero extends NonNegative {
    Positive<Zero> inc();
  }

  interface Positive<MinusOne extends NonNegative> extends NonNegative {
    Positive<Positive<MinusOne>> inc();
    MinusOne dec();
  }

  static <T extends NonNegative> T initialize() {
    return null;
  }
  public static void main(String[] args) {
    Positive<Positive<Positive<Zero>>> three = //
        FluentMath.<Positive<Positive<Positive<Zero>>>> initialize();
    three.dec().dec().dec();
  }
}
