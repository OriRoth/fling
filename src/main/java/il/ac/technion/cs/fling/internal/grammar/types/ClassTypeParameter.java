package il.ac.technion.cs.fling.internal.grammar.types;
public interface ClassTypeParameter extends Parameter {
  String typeName();
  default String parameterTypeName() {
    return typeName();
  }
}
