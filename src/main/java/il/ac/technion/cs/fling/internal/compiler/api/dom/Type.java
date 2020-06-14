package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.Collections;
import java.util.List;

import il.ac.technion.cs.fling.internal.compiler.api.TypeName;

public class Type {
  public final TypeName name;
  public final List<Type> arguments;

  public Type(final TypeName name) {
    this(name, Collections.emptyList());
  }

  public String render() {
    return null;
  }

  private Type() {
    this(null);
  }

  public static final Type TOP = new Type();
  public static final Type BOTTOM = new Type();

  public static Type top() {
    return TOP;
  }

  public static Type bot() {
    return BOTTOM;
  }

  public Type(final TypeName name, final List<Type> arguments) {
    this.name = name;
    this.arguments = Collections.unmodifiableList(arguments);
  }

  public boolean isBot() {
    return this == BOTTOM;
  }

  public boolean isTop() {
    return this == TOP;
  }
}
