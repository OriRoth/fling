package org.spartan.fajita.api.examples.hamcrest;

import static org.spartan.fajita.api.examples.ASTViewer.*;
import static org.spartan.fajita.api.examples.hamcrest.Hamcrest.NT.*;
import static org.spartan.fajita.api.examples.hamcrest.Hamcrest.Term.*;
import static org.spartan.fajita.api.examples.hamcrest.Hamcrest.Term.anything;
import static org.spartan.fajita.api.examples.hamcrest.Hamcrest.Term.assertThat;
import static org.spartan.fajita.api.examples.hamcrest.Hamcrest.Term.instance_of;
import static org.spartan.fajita.api.examples.hamcrest.Hamcrest.Term.not;
import static org.spartan.fajita.api.examples.hamcrest.HamcrestBuilder.*;
import static org.spartan.fajita.api.examples.hamcrest.HamcrestBuilder.anything;
import static org.spartan.fajita.api.examples.hamcrest.HamcrestBuilder.assertThat;
import static org.spartan.fajita.api.examples.hamcrest.HamcrestBuilder.instance_of;
import static org.spartan.fajita.api.examples.hamcrest.HamcrestBuilder.not;

import org.spartan.fajita.api.ast.Compound;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.parser.LRParser;

@SuppressWarnings("unused") public class Hamcrest {
  @SuppressWarnings("unchecked") public static void expressionBuilder() {
    // top down
    Compound e0 = assertThat("A", anything());
    /* for some reason i don't get type safety as expected */
    Compound e1 = assertThat("A", instance_of(String.class));
    Compound e2 = assertThat(new Integer(123), equal_to("S"));
    Compound e3 = assertThat("A", not().anything());
    Compound e4 = assertThat("A", not().not().not().equals_to("V"));
    // bottom up
    Compound e5 = assertThat(e1, not(equal_to("S")));
    Compound e6 = assertThat("A", not(not(equal_to("S"))));
    Compound e7 = assertThat("ASDAD", not().any_of(not().anything(), equal_to("AS")));
    showASTs(e0, e1, e2, e3, e4, e5, e6, e7);
  }

  enum Term implements Terminal {
    assertThat, instance_of, anything, not, equals_to, any_of, value, type;
    @Override public Type type() {
      return new Type();
    }
  }

  static enum NT implements NonTerminal {
    ASSERT, MATCHER
    // , MATCHERS;
  }

  public static void buildBNF() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("Hamcrest") //
        .setStartSymbols(ASSERT) //
        .endConfig() //
        .derive(ASSERT).to(assertThat).and(value).and(MATCHER) //
        .derive(MATCHER).to(instance_of).and(type) //
        /*       */.or().to(anything) //
        /*       */.or().to(equals_to).and(value) //
        /*       */.or().to(not).and(MATCHER) //
        // /* */.or().to(any_of).and(MATCHERS) //
        // .derive(MATCHERS).to(MATCHER)//
        // /* */.or().to(MATCHER).and(MATCHERS) //
        .finish();
    System.out.println(bnf);
    LRParser parser = new LRParser(bnf);
    System.out.println(parser);
  }
}
