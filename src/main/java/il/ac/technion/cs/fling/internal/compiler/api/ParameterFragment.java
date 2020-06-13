package il.ac.technion.cs.fling.internal.compiler.api;

/** Parameter declaration inferred from token. Single token may define multiple
 * parameters.
 *
 * @author Ori Roth */
public class ParameterFragment {
  /** Parameter type name. */
  public final String parameterType;
  /** Parameter variable name. */
  public final String parameterName;

  private ParameterFragment(final String parameterType, final String parameterName) {
    this.parameterType = parameterType;
    this.parameterName = parameterName;
  }

  public static ParameterFragment of(final String parameterType, final String parameterName) {
    return new ParameterFragment(parameterType, parameterName);
  }

  public String parameterType() {
    return parameterType();
  }

  public String parameterName() {
    return parameterName;
  }
}