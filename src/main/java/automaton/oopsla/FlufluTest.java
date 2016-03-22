package automaton.oopsla;

import com.javax0.fluflu.Fluentize;
import com.javax0.fluflu.Transition;
public class FlufluTest {
  // begin{full}
  @Fluentize(className = "L", startState = "Q0"
      , startMethod = "a")
  public abstract class ToBeFluentized {
    @Transition(from = "Q0", to = "Q0") public void a() {
    }
  }
  // end{full}
}
