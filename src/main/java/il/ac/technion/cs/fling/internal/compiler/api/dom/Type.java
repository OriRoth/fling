package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.List;

import il.ac.technion.cs.fling.adapters.APIGenerator;

/** @author Yossi Gil
 * @since 2020- */
public class Type {
  private static final Type TOP = new Type() {
    @Override public String render(final APIGenerator g) {
      return g.renderTypeTop();
    }
  };
  private static final Type BOTTOM = new Type() {
    @Override public String render(final APIGenerator g) {
      return g.renderTypeBottom();
    }
  };
  public final TypeSignature signature;
  public final List<Method> methods;

  public Type(final TypeSignature signature, final List<Method> methods) {
    this.signature = signature;
    this.methods = methods;
  }

  private Type() {
    signature = null;
    methods = null;
  }

  public List<Method> methods() {
    return methods;
  }

  public static Type top() {
    return TOP;
  }

  public static Type bot() {
    return BOTTOM;
  }

  public boolean isTop() {
    return this == TOP;
  }

  public boolean isBot() {
    return this == BOTTOM;
  }

  public String render(final APIGenerator g) {
    return g.render(signature, methods);
  }
}
