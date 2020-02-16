package fling.internal.compiler.ast.nodes;

import java.util.*;
import java.util.function.*;

import fling.*;

public class FieldNode {
  public final Symbol source;
  private List<FieldNodeFragment> inferredFieldFragments;

  public FieldNode(final Symbol source) {
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
  public void setInferredFieldFragments(final List<FieldNodeFragment> inferredFieldFragments) {
    this.inferredFieldFragments = inferredFieldFragments;
  }

  public static class FieldNodeFragment {
    public final String parameterType;
    public final String parameterName;

    public FieldNodeFragment(final String parameterType, final String parameterName) {
      this.parameterType = parameterType;
      this.parameterName = parameterName;
    }
    public static FieldNodeFragment of(final String parameterType, final String parameterName) {
      return new FieldNodeFragment(parameterType, parameterName);
    }
    @SuppressWarnings({ "static-method", "unused" }) public String visitingMethod(
        final BiFunction<Variable, String, String> variableVisitingSolver, final String accessor,
        final Supplier<String> variableNamesGenerator) {
      return null;
    }
  }
}
