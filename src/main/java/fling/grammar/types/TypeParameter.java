package fling.grammar.types;

public interface TypeParameter {
  String typeName();
  default String parameterTypeName() {
    return typeName();
  }
  String baseParameterName();
}
