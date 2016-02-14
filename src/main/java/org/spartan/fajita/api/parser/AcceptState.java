package org.spartan.fajita.api.parser;

import java.util.HashSet;

import org.spartan.fajita.api.bnf.BNF;

public final class AcceptState extends JState{
  public AcceptState(final BNF bnf,int index) {
    super(new HashSet<>(), bnf, index);
  }
  @Override public String toString() {
    return "accept state";
  }
  @Override public boolean equals(final Object obj) {
    return getClass().equals(obj.getClass());
  }
}
