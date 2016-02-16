package org.spartan.fajita.api.jlr.simulator;

import java.util.Collection;
import java.util.EmptyStackException;
import java.util.Map;
import java.util.Stack;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.jlr.JActionTable;
import org.spartan.fajita.api.jlr.JActionTable.Action;
import org.spartan.fajita.api.jlr.JActionTable.Jump;
import org.spartan.fajita.api.jlr.JActionTable.Shift;
import org.spartan.fajita.api.jlr.JLRRecognizer;
import org.spartan.fajita.api.jlr.JState;

/**
 * The simulator works only on BNFs whos alphabet is a subset of {"a","b",...,"z" ,"A",...,"Z"}
 * @author stlevy
 *
 */
public class JLRSimulator {
  private final Stack<Map<Integer, JState>> stack;
  private final JActionTable actionTable;
  private final Collection<Verb> verbs;
  private Boolean result;
  private String inputHandled;
  private int internalState;

  public JLRSimulator(JActionTable actionTable, Collection<Verb> verbs) {
    this.actionTable = actionTable;
    this.verbs = verbs;
    result = null;
    inputHandled = "";
    internalState = 0;
    stack = new Stack<>();
  }
  private Boolean result() {
    return result;
  }
  private boolean isDone() {
    return result == null;
  }
  private Verb verbOf(final char c) {
    return verbs.stream().filter(verb -> verb.name().equals(String.valueOf(c))).findFirst().get();
  }
  private void act(char c) {
    Action entry = actionTable.get(internalState, verbOf(c));
    if (entry.isAccept())
      accept();
    else if (entry.isError())
      reject();
    else if (entry.isShift())
      shift((Shift) entry);
    else if (entry.isJump())
      jump((Jump) entry);
    else
      throw new InternalError("Something went wrong, unknown action.");
  }
  private void shift(Shift shiftAction) {
    stack.push(shiftAction.jumpSet);
    internalState = shiftAction.nextState.index;
  }
  @SuppressWarnings("boxing") private void jump(Jump jumpAction) {
    int dst = jumpAction.label;
    Map<Integer, JState> top = stack.pop();
    try {
      for (; !top.containsKey(dst); top = stack.pop())
        /**/ ;
    } catch (EmptyStackException e) {
      throw new InternalError("Algorithm fault, jumped to a label not on the stack.", e);
    }
    internalState = top.get(dst).index;
  }
  private void accept() {
    System.out.println("Accepted on '" + inputHandled + "'");
    result = Boolean.TRUE;
  }
  private void reject() {
    System.out.println("Rejected on '" + inputHandled + "'");
    result = Boolean.FALSE;
  }
  @SuppressWarnings("boxing") public static boolean runJLR(BNF bnf, String input) {
    JLRRecognizer jlr = new JLRRecognizer(bnf);
    System.out.println(jlr);
    JLRSimulator simulator = new JLRSimulator(jlr.getActionTable(),bnf.getVerbs());
    for (char c : (input + "$").toCharArray()) {
      simulator.act(c);
      if (simulator.isDone())
        break;
    }
    return simulator.result();
  }
}
