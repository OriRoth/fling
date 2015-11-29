package org.spartan.fajita.api.bnf;

import static org.junit.Assert.*;
import static org.spartan.fajita.api.bnf.TestUtils.expectedSet;

import java.util.ArrayList;
import java.util.Set;

import org.junit.Test;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;

@SuppressWarnings("static-method") //
public class OverloadTest {
  private enum Term implements Terminal {
    t1;
    private final Type type;

    private Term() {
      type = Type.VOID;
    }
    @Override public Type type() {
      return type;
    }
  }

  private enum NT implements NonTerminal {
    S, A;
  }

  @Test public void testSingleOverload() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("TEST") //
        .start(NT.S) //
        .overload(Term.t1).with(String.class).endConfig() //
        .derive(NT.S).to(NT.A) //
        .derive(NT.A).to(Term.t1) //
        .finish();
    assertEquals(expectedSet(Type.VOID, new Type(String.class)), bnf.getOverloadsOf(Term.t1));
  }
  @Test public void testMultiParamsOverload() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("TEST") //
        .start(NT.S) //
        .overload(Term.t1).with(Term.class, String.class, int.class).endConfig() //
        .derive(NT.S).to(NT.A) //
        .derive(NT.A).to(Term.t1) //
        .finish();
    assertTrue(bnf.getOverloadsOf(Term.t1).contains(new Type(Term.class, String.class, int.class)));
  }
  @Test public void testMultipleOverloads() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("TEST") //
        .start(NT.S) //
        .overload(Term.t1).with(String.class, int.class, Type.class)//
        .overload(Term.t1).with(String.class, int.class) //
        .overload(Term.t1).with(Integer.class, Object.class) //
        .endConfig() //
        .derive(NT.S).to(NT.A) //
        .derive(NT.A).to(Term.t1) //
        .finish();
    Set<Type> expected = expectedSet(Type.VOID, new Type(String.class, int.class, Type.class), new Type(String.class, int.class),
        new Type(Integer.class, Object.class));
    assertEquals(expected, bnf.getOverloadsOf(Term.t1));
  }
  @Test(expected = IllegalStateException.class) public void testSameOverloadException() {
    new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("TEST") //
        .start(NT.S) //
        .overload(Term.t1).with(Void.class)//
        .endConfig() //
        .derive(NT.S).to(NT.A) //
        .derive(NT.A).to(Term.t1) //
        .finish();
    fail("Should have thrown exception");
  }
  @SuppressWarnings("rawtypes") @Test(expected = IllegalStateException.class) public void testSameGenericsOverloadException() {
    Class<? extends ArrayList> clss1 = new ArrayList<String>().getClass();
    Class<? extends ArrayList> clss2 = new ArrayList<Integer>().getClass();
    new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("TEST") //
        .start(NT.S) //
        .overload(Term.t1).with(clss1) //
        .overload(Term.t1).with(clss2) //
        .endConfig() //
        .derive(NT.S).to(NT.A) //
        .derive(NT.A).to(Term.t1) //
        .finish();
    fail("Should have thrown exception");
  }
}
