package org.spartan.fajita.api.examples.balancedParenthesis.bnf;

import org.spartan.fajita.api.parser.stack.StateStack;

public class Symbols {
  public interface NT_BALANCED<S extends StateStack<?>> {
    S BALANCED();
  }

  public interface NT_NOT_EMPTY<S extends StateStack<?>> {
    S NOT_EMPTY();
  }

  public interface NT_EMPTY<S extends StateStack<?>> {
    S EMPTY();
  }

  public interface NT_START<S extends StateStack<?>> {
    S START();
  }

  public interface Term_lp<S extends StateStack<?>> {
    S lp();
  }

  public interface Term_rp<S extends StateStack<?>> {
    S rp();
  }

  public interface Term_build<S extends StateStack<?>> {
    S build();
  }

  public interface Term_$<Seal> {
    Seal $();
  }
}
