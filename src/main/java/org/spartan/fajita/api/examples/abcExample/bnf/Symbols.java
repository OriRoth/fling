package org.spartan.fajita.api.examples.abcExample.bnf;

import org.spartan.fajita.api.parser.stack.StateStack;

public class Symbols {
  public interface NT_A<S extends StateStack<?>> {
    S A();
  }

  public interface NT_START<S extends StateStack<?>> {
    S START();
  }

  public interface Term_a<S extends StateStack<?>> {
    S a();
  }

  public interface Term_b<S extends StateStack<?>> {
    S b();
  }

  public interface Term_c<S extends StateStack<?>> {
    S c();
  }

  public interface Term_$<Seal> {
    Seal $();
  }
}
