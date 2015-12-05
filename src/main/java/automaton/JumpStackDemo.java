package automaton;

import automaton.Domain.JS;
import automaton.Domain.JS.E;
import automaton.Domain.JS.P;
import automaton.Domain.JS.¤;
import automaton.Domain.Γʹ;
import automaton.Domain.Γʹ.Γ.γ1;
import automaton.Domain.Γʹ.Γ.γ2;

enum a {
  ;
  public static void use_cases_of_jump_stack() {
    // Create a stack a with five items in it:
    E empty = JS.empty;
    P<γ1,E,E,¤> γ1 = empty.γ1();
    P<γ1,P<γ1,P<γ1,E,E,¤>,P<γ1,E,E,¤>,¤>,P<γ1,E,E,¤>,¤> γ12 = γ1.γ1();
    P<γ2,P<γ2,P<γ1,P<γ1,P<γ1,E,E,¤>,P<γ1,E,E,¤>,¤>,P<γ1,E,E,¤>,¤>,P<γ1,E,E,¤>,P<γ1,P<γ1,P<γ1,E,E,¤>,P<γ1,E,E,¤>,¤>,P<γ1,E,E,¤>,¤>>,P<γ1,E,E,¤>,P<γ1,P<γ1,P<γ1,E,E,¤>,P<γ1,E,E,¤>,¤>,P<γ1,E,E,¤>,¤>> γ2 = γ12.γ2();
    P<γ1, E, P<γ1, E, P<γ2, E, P<γ1, E, E, ¤>, P<γ1, E, P<γ1, E, E, ¤>, ¤>>, P<γ1, E, P<γ1, E, E, ¤>, ¤>>, P<γ1, E, P<γ1, E, E, ¤>, ¤>> _1 = JS.empty.γ1().γ1().γ2().γ1().γ1();
    P<γ1, P<γ2, P<γ1, P<γ1, E>>>> _2 = _1.pop(); // ✓ Pop one item
    P<γ2, P<γ1, P<γ1, E>>> _3 = _2.pop(); // ✓ Pop another item
    P<γ1, P<γ1, E>> _4 = _3.pop(); // ✓ Pop yet another item
    P<γ1, E> _5 = _4.pop(); // ✓ Pop penultimate item
    γ1 _6 = _5.top(); // ✓ Examine last item
    E _7 = _5.pop(); // ✓ Pop last item
    JS.¤ _8 = _7.pop(); // ✗ Cannot pop from an empty stack
    Γʹ.¤ _9 = _7.top(); // ✗ empty stack has no top element
  } 
}
