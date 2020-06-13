package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.Collections;
import java.util.List;

import il.ac.technion.cs.fling.internal.compiler.api.InterfaceDeclaration;

public class Interface {
  private static final Interface TOP = new Interface();
  private static final Interface BOT = new Interface();
  public final InterfaceDeclaration declaration;
  public final List<Method> methods;

  public Interface(final InterfaceDeclaration declaration, final List<Method> methods) {
    this.declaration = declaration;
    this.methods = Collections.unmodifiableList(methods);
  }

  private Interface() {
    declaration = null;
    methods = null;
  }

  public List<Method> methods() {
    return methods;
  }

  public static Interface top() {
    return TOP;
  }

  public static Interface bot() {
    return BOT;
  }

  public boolean isTop() {
    return this == TOP;
  }

  public boolean isBot() {
    return this == BOT;
  }
}
