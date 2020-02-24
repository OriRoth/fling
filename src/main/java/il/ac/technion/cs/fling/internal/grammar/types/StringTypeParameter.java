package il.ac.technion.cs.fling.internal.grammar.types;

public interface StringTypeParameter extends TypeParameter {
  String typeName();
  default String parameterTypeName() {
    return typeName();
  }
}
