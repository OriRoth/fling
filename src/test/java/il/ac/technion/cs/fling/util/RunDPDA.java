package il.ac.technion.cs.fling.util;

import static il.ac.technion.cs.fling.automata.Alphabet.ε;

import java.util.*;

import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

public class RunDPDA {
  public static <Q, Σ, Γ> boolean run(DPDA<Q, Σ, Γ> dpda, @SuppressWarnings("unchecked") Σ... _w) {
    Queue<Σ> w = new LinkedList<>();
    Collections.addAll(w, _w);
    Word<Γ> stack = dpda.γ0;
    Q q = dpda.q0;
    for (var σ = w.poll(); σ != null; σ = w.poll()) {
      var δ = dpda.δ(q, σ, stack.top());
      if (δ == null)
        return false;
      q = δ.q$;
      stack = stack.pop().push(δ.getΑ());
      for (δ = dpda.δ(q, ε(), stack.top()); δ != null; δ = dpda.δ(q, ε(), stack.top())) {
        q = δ.q$;
        stack = stack.pop().push(δ.getΑ());
      }
    }
    return dpda.F.contains(q);
  }
}
