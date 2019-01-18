package fling.compiler.ast;

import java.util.Collections;
import java.util.List;

public class PolymorphicTypeNode<T> {
  public final T name;
  public final List<PolymorphicTypeNode<T>> typeArguments;

  public PolymorphicTypeNode(T name, List<PolymorphicTypeNode<T>> typeArguments) {
    this.name = name;
    this.typeArguments = Collections.unmodifiableList(typeArguments);
  }
  public PolymorphicTypeNode(T name) {
    this.name = name;
    this.typeArguments = Collections.emptyList();
  }
  public boolean isLeaf() {
    return typeArguments.isEmpty();
  }
}
