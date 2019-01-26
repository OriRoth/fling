package fling.compiler.api;

import java.util.Collections;
import java.util.List;

public class InterfaceNode<T, D, N> {
  @SuppressWarnings("rawtypes") /* private */ static final InterfaceNode TOP = new InterfaceNode();
  @SuppressWarnings("rawtypes") /* private */ static final InterfaceNode BOT = new InterfaceNode();
  public final N declaration;
  public final List<AbstractMethodNode<T, D>> methods;

  public InterfaceNode(N name, List<AbstractMethodNode<T, D>> methods) {
    this.declaration = name;
    this.methods = Collections.unmodifiableList(methods);
  }
  private InterfaceNode() {
    this.declaration = null;
    this.methods = null;
  }
  @SuppressWarnings("unchecked") public static <T, D, N> InterfaceNode<T, D, N> top() {
    return TOP;
  }
  @SuppressWarnings("unchecked") public static <T, D, N> InterfaceNode<T, D, N> bot() {
    return BOT;
  }
  public boolean isTop() {
    return this == TOP;
  }
  public boolean isBot() {
    return this == BOT;
  }
}
