package org.spartan.fajita.api.examples.balancedParenthesis.states;

import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.NT_BALANCED;
import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.NT_EMPTY;
import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.NT_NOT_EMPTY;
import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.Term_build;
import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.Term_lp;
import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.Term_rp;
import org.spartan.fajita.api.parser.stack.StateStack;

public class Q7<Tail extends StateStack<? extends StateStack<BOTTOM>>, BOTTOM extends StateStack<?> & NT_NOT_EMPTY<AFTER_NOT_EMPTY>, AFTER_NOT_EMPTY extends StateStack<?>>
    extends StateStack<Tail>
    implements Term_rp<Q5<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, Q8<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, BOTTOM, AFTER_NOT_EMPTY>>>,
    Term_build<Q5<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, Q8<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, BOTTOM, AFTER_NOT_EMPTY>>>,
    Term_lp<Q4<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, Q3<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, Q8<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, BOTTOM, AFTER_NOT_EMPTY>>>>,
    NT_NOT_EMPTY<Q3<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, Q8<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, BOTTOM, AFTER_NOT_EMPTY>>>,
    NT_BALANCED<Q8<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, BOTTOM, AFTER_NOT_EMPTY>>,
    NT_EMPTY<Q5<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, Q8<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, BOTTOM, AFTER_NOT_EMPTY>>> {
  public Q7(final Tail t) {
    super(t);
  }
  @Override public Q5<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, Q8<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, BOTTOM, AFTER_NOT_EMPTY>> EMPTY() {
    return new Q5<>(this);
  }
  @Override public Q8<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, BOTTOM, AFTER_NOT_EMPTY> BALANCED() {
    return new Q8<>(this);
  }
  @Override public Q3<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, Q8<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, BOTTOM, AFTER_NOT_EMPTY>> NOT_EMPTY() {
    return new Q3<>(this);
  }
  @Override public Q4<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, Q3<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, Q8<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, BOTTOM, AFTER_NOT_EMPTY>>> lp() {
    return new Q4<>(this);
  }
  @Override public Q5<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, Q8<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, BOTTOM, AFTER_NOT_EMPTY>> build() {
    return EMPTY();
  }
  @Override public Q5<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, Q8<Q7<Tail, BOTTOM, AFTER_NOT_EMPTY>, BOTTOM, AFTER_NOT_EMPTY>> rp() {
    return EMPTY();
  }
}
