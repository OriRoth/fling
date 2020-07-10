package il.ac.technion.cs.fling.namers;
import java.util.*;
import il.ac.technion.cs.fling.internal.grammar.rules.Component;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
public class VariableGenerator {
  private final Map<Variable, Integer> astChildrenCounter = new HashMap<>();
  private final Map<Component, Integer> notationsChildrenCounter = new HashMap<>();
  /** Create new variable subject to given symbol in notation's context.
   *
   * @param inner parent symbol
   * @return child variable */
  public Variable fresh(final Variable v) {
    return Variable.byName(v.name() + astChildrenCounter.merge(v, Integer.valueOf(1), Integer::sum));
  }
  /** Create new variable subject to given symbol in notation's context.
   *
   * @param inner parent symbol
   * @return child variable */
  public Variable fresh(final List<? extends Component> symbols) {
    return Variable.byName(freshName(firstVariable(symbols)));
  }
  private static Variable firstVariable(final List<? extends Component> cs) {
    for (Component c : cs)
      if (c instanceof Variable v)
        return v;
    return Variable.byName("$$");
  }
  private String freshName(Variable v) {
    return "_" + v.name() + notationsChildrenCounter.merge(v, Integer.valueOf(1), Integer::sum);
  }
}
