package org.spartan.fajita.api.examples.abcExample.bnf;

import org.spartan.fajita.api.parser.stack.RuntimeStateStack;

public class Symbols {
  public interface NT_A<S extends RuntimeStateStack<?>> {
    S A();
  }

  public interface NT_START<S extends RuntimeStateStack<?>> {
    S START();
  }

  public interface Term_a<S extends RuntimeStateStack<?>> {
    S a();
  }

  public interface Term_b<S extends RuntimeStateStack<?>> {
    S b();
  }

  public interface Term_c<S extends RuntimeStateStack<?>> {
    S c();
  }

  public interface Term_$<Seal> {
    Seal $();
  }
}
