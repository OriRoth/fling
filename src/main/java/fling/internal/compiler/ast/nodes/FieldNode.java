package fling.internal.compiler.ast.nodes;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import fling.internal.grammar.sententials.Symbol;
import fling.internal.grammar.sententials.Variable;

public class FieldNode {
  public final Symbol source;
  private List<FieldNodeFragment> inferredFieldFragments;

  public FieldNode(Symbol source) {
    this.source = source;
  }
  public Symbol source() {
    return source;
  }
  public List<FieldNodeFragment> getInferredFieldFragments() {
    if (inferredFieldFragments == null)
      throw new IllegalStateException("field type(s) and name(s) not decided");
    return inferredFieldFragments;
  }
  public void setInferredFieldFragments(List<FieldNodeFragment> inferredFieldFragments) {
    this.inferredFieldFragments = inferredFieldFragments;
  }

  public static class FieldNodeFragment {
    public final String parameterType;
    public final String parameterName;

    public FieldNodeFragment(String parameterType, String parameterName) {
      this.parameterType = parameterType;
      this.parameterName = parameterName;
    }
    public static FieldNodeFragment of(String parameterType, String parameterName) {
      return new FieldNodeFragment(parameterType, parameterName);
    }
    @SuppressWarnings({ "static-method", "unused" }) public String visitingMethod(
        BiFunction<Variable, String, String> variableVisitingSolver, String accessor, Supplier<String> variableNamesGenerator) {
      return null;
    }
  }
}
