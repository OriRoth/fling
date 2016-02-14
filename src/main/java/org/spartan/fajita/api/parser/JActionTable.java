package org.spartan.fajita.api.parser;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.parser.stack.JItem;

public class JActionTable {
  public abstract static class Action {
    public boolean isShift() {
      return getClass() == Shift.class;
    }
    public boolean isJump() {
      return getClass() == Jump.class;
    }
    public boolean isError() {
      return getClass() == Error.class;
    }
    public boolean isAccept() {
      return getClass() == Accept.class;
    }
  }

  public static class Error extends Action {
    @Override public String toString() {
      return "";
    }
  }

  public static class Accept extends Action {
    @Override public String toString() {
      return "acc";
    }
    @Override public boolean equals(final Object obj) {
      return obj != null && (obj.getClass() == Accept.class);
    }
    @Override public int hashCode() {
      return getClass().hashCode();
    }
  }

  public static class Shift extends Action {
    public final JState state;

    public Shift(final JState state) {
      this.state = state;
    }
    @Override public String toString() {
      return "s" + state.index;
    }
    @Override public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + state.getItems().hashCode();
      return result;
    }
    @Override public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Shift other = (Shift) obj;
      if (state != other.state)
        return false;
      return true;
    }
  }

  public static class Jump extends Action {
    public int address;
    public Jump(JItem item) {
      this.address = item.label;
    }
    @Override public String toString() {
      return "j"+address;
    }
  }

  public class ReduceReduceConflictException extends RuntimeException {
    private static final long serialVersionUID = -2979864485863027282L;
    private final JState state;
    private final Symbol lookahead;

    public ReduceReduceConflictException(final JState state, final Symbol lookahead) {
      this.lookahead = lookahead;
      this.state = state;
    }
    @Override public String getMessage() {
      return "R/R Conflict on state : " + state + " lookahead " + lookahead.toString();
    }
  }

  public class ShiftReduceConflictException extends RuntimeException {
    private static final long serialVersionUID = -2979864485863027282L;
    private final JState state;
    private final Symbol lookahead;

    public ShiftReduceConflictException(final JState state, final Symbol lookahead) {
      this.state = state;
      this.lookahead = lookahead;
    }
    @Override public String getMessage() {
      return "S/R conflict on state " + state + " lookahead " + lookahead.toString();
    }
  }

  private final Map<Verb, Action>[] table;

  @SuppressWarnings("unchecked") public JActionTable(final List<JState> states) {
    table = new HashMap[states.size()];
    for (int i = 0; i < states.size(); i++)
      table[i] = new HashMap<>();
  }
  void set(final JState state, final Verb lookahead, final Action act) {
    checkForConflicts(state, lookahead, act);
    table[state.index].put(lookahead, act);
  }
  private void checkForConflicts(final JState state, final Symbol lookahead, final Action act) {
    Action previous = table[state.index].get(lookahead);
    if (previous == null || previous.equals(act))
      return;
    if (previous.getClass() == Jump.class && act.getClass() == Jump.class)
      throw new ReduceReduceConflictException(state, lookahead);
    throw new ShiftReduceConflictException(state, lookahead);
  }
  public Action get(final int stateIndex, final Verb lookahead) {
    Action $ = table[stateIndex].get(lookahead);
    if ($ == null)
      return new Error();
    return $;
  }
  @Override public String toString() {
    String $ = "action table:\n   |";
    List<Verb> verbs = Arrays.asList(table).stream().flatMap(map -> map.keySet().stream()).distinct()
        .collect(Collectors.toList());
    for (int i = 0; i < verbs.size(); i++)
      $ += String.format("%5.5s|", verbs.get(i).name());
    $ += "\n";
    for (int i = 0; i < table.length; i++) {
      $ += String.format("%-3d|", new Integer(i));
      for (int j = 0; j < verbs.size(); j++)
        $ += String.format("%5.5s|", get(i, verbs.get(j)).toString());
      $ += "\n";
    }
    return $;
  }
}
