package fling.compiler.ast.nodes;

import java.util.List;

import fling.grammar.sententials.Symbol;

public class FieldNode {
  public final Symbol source;
  private List<FieldNodeFragment> inferredFieldFragments;

  public FieldNode(Symbol source) {
    this.source = source;
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

    private FieldNodeFragment(String parameterType, String parameterName) {
      this.parameterType = parameterType;
      this.parameterName = parameterName;
    }
    public static FieldNodeFragment of(String parameterType, String parameterName) {
      return new FieldNodeFragment(parameterType, parameterName);
    }
  }
}
