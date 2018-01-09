package org.spartan.fajita.revision.examples.usage;

import static org.spartan.fajita.revision.junk.Exp2.*;

public class Exp2 {
  public static void main(String[] args) {
    System.out.println(
      a().a().a().a().a().a().a().a().a().a() //
      .a().a().a() //
    );
  }
}
