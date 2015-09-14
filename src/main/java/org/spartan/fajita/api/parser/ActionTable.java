package org.spartan.fajita.api.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class ActionTable {
  public static class Action {
    //
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
    public final int stateIndex;

    public Shift(final int stateIndex) {
      this.stateIndex = stateIndex;
    }
    @Override public String toString() {
      return "s" + stateIndex;
    }
    @Override public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + stateIndex;
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
      if (stateIndex != other.stateIndex)
        return false;
      return true;
    }
  }

  public static class Reduce extends Action {
    public Reduce() {
    }
    @Override public String toString() {
      return "red";
    }
  }

  public class ReduceReduceConflictException extends RuntimeException {
    private static final long serialVersionUID = -2979864485863027282L;
    private final State state;
    private final Symbol lookahead;

    public ReduceReduceConflictException(final State state, final Symbol lookahead) {
      this.lookahead = lookahead;
      this.state = state;
    }
    @Override public String getMessage() {
      return "R/R Conflict on state : " + state + " lookahead " + lookahead.toString();
    }
  }

  public class ShiftReduceConflictException extends RuntimeException {
    private static final long serialVersionUID = -2979864485863027282L;
    private final State state;
    private final Symbol lookahead;

    public ShiftReduceConflictException(final State state, final Symbol lookahead) {
      this.state = state;
      this.lookahead = lookahead;
    }
    @Override public String getMessage() {
      return "S/R conflict on state " + state + " lookahead " + lookahead.toString();
    }
  }

  private final Map<Terminal, Action>[] table;

  @SuppressWarnings("unchecked") public ActionTable(final List<State> states) {
    table = new HashMap[states.size()];
    for (int i = 0; i < states.size(); i++)
      table[i] = new HashMap<>();
  }
  void set(final State state, final Terminal lookahead, final Action act) {
    checkForConflicts(state, lookahead, act);
    table[state.stateIndex].put(lookahead, act);
  }
  private void checkForConflicts(final State state, final Symbol lookahead, final Action act) {
    Action previous = table[state.stateIndex].get(lookahead);
    if (previous == null || previous.equals(act))
      return;
    if (previous.getClass() == Reduce.class && act.getClass() == Reduce.class)
      throw new ReduceReduceConflictException(state, lookahead);
    throw new ShiftReduceConflictException(state, lookahead);
  }
  public Action get(final int stateIndex, final Terminal lookahead) {
    Action $ = table[stateIndex].get(lookahead);
    if ($ == null)
      return new Error();
    return $;
  }
  @Override public String toString() {
    String $ = "action table:\n   |";
    List<Terminal> terminals = Arrays.asList(table).stream().flatMap(map -> map.keySet().stream()).distinct()
        .collect(Collectors.toList());
    for (int i = 0; i < terminals.size(); i++)
      $ += String.format("%5.5s|", terminals.get(i).name());
    $ += "\n";
    for (int i = 0; i < table.length; i++) {
      $ += String.format("%-3d|", new Integer(i));
      for (int j = 0; j < terminals.size(); j++)
        $ += String.format("%5.5s|", get(i, terminals.get(j)).toString());
      $ += "\n";
    }
    return $;
  }
}
