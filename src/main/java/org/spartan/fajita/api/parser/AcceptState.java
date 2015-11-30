package org.spartan.fajita.api.parser;

import java.util.HashSet;

import org.spartan.fajita.api.bnf.BNF;

public final class AcceptState extends State {
  protected AcceptState(final BNF bnf) {
    super(new HashSet<>(), bnf, -1);
  }
  @Override public String toString() {
    return "accept state";
  }
  @Override public boolean equals(final Object obj) {
    return getClass().equals(obj.getClass());
  }
}
