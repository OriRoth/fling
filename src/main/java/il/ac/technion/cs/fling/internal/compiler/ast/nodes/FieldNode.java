package il.ac.technion.cs.fling.internal.compiler.ast.nodes;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import il.ac.technion.cs.fling.internal.grammar.rules.Component;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
public class FieldNode {
  public final Component source;
  private List<FieldNodeFragment> inferredFieldFragments;
  public FieldNode(final Component source) {
    this.source = source;
  }
  public Component source() {
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
    @SuppressWarnings({ "static-method", "unused" }) public String visitingStatement(
            final BiFunction<? super Variable, ? super String, String> variableVisitingSolver, final String accessor,
            final Supplier<String> variableNamesGenerator) {
      return null;
    }
  }
}
