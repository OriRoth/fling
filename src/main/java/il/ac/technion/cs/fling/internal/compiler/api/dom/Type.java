package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.Collections;
import java.util.List;

import il.ac.technion.cs.fling.adapters.APIGenerator;

/** @author Yossi Gil
 * @since 2020- */
public class Type {
  private static final Type TOP = new Type() {
    @Override public String render(final APIGenerator g) {
      return g.renderInterfaceTop();
    }
  };
  private static final Type BOTTOM = new Type() {
    @Override public String render(final APIGenerator g) {
      return g.renderInterfaceBottom();
    }
  };
  public final InterfaceDeclaration declaration;
  public final List<Method> methods;

  public Type(final InterfaceDeclaration declaration, final List<Method> methods) {
    this.declaration = declaration;
    this.methods = Collections.unmodifiableList(methods);
  }

  private Type() {
    declaration = null;
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
    return g.render(declaration, methods);
  }
}
