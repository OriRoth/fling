package org.spartan.fajita.api.jlr.simulator;

import java.util.EmptyStackException;
import java.util.Map;
import java.util.Stack;

import org.spartan.fajita.api.jlr.JActionTable;
import org.spartan.fajita.api.jlr.JActionTable.Action;
import org.spartan.fajita.api.jlr.JActionTable.Jump;
import org.spartan.fajita.api.jlr.JActionTable.Shift;
import org.spartan.fajita.api.jlr.JState;

public class JLRSimulator {
  
  
  private Boolean result;
  private String inputHandled;
  private int internalState;
  private Stack<Map<Integer, JState>> stack;
  private JActionTable actionTable;

  public JLRSimulator(JActionTable actionTable) {
    this.actionTable = actionTable;
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
  private void act(char c) {
    Action entry = actionTable.get(internalState, null/*c*/);
    if (entry.isAccept())
      accept();
    else if (entry.isError())
      reject();
    else if (entry.isShift())
      shift((Shift)entry);
    else if (entry.isJump())
      jump((Jump)entry);
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
      throw new InternalError("Algorithm fault, jumped to a label not on the stack.",e);
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
  @SuppressWarnings("boxing") public static boolean runJLR(JActionTable automaton, String input) {
    JLRSimulator simulator = new JLRSimulator(automaton);
    for (char c : (input + "$").toCharArray()) {
      simulator.act(c);
      if (simulator.isDone())
        break;
    }
    return simulator.result();
  }
}
