package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.Collections;
import java.util.List;

class Type {

}

class MononorphicType extends Type {

}

public class PolymorphicType<T> {
  public final T name;
  public final List<PolymorphicType<T>> typeArguments;
  @SuppressWarnings("rawtypes") private static final PolymorphicType TOP = new PolymorphicType<>();
  @SuppressWarnings("rawtypes") private static final PolymorphicType BOT = new PolymorphicType<>();

  public PolymorphicType(final T name, final List<PolymorphicType<T>> typeArguments) {
    this.name = name;
    this.typeArguments = Collections.unmodifiableList(typeArguments);
  }

  public PolymorphicType(final T name) {
    this.name = name;
    typeArguments = Collections.emptyList();
  }

  private PolymorphicType() {
    name = null;
    typeArguments = null;
  }

  @SuppressWarnings("unchecked") public static <T> PolymorphicType<T> top() {
    return TOP;
  }

  @SuppressWarnings("unchecked") public static <T> PolymorphicType<T> bot() {
    return BOT;
  }

  public boolean isTop() {
    return this == TOP;
  }

  public boolean isBot() {
    return this == BOT;
  }
}
