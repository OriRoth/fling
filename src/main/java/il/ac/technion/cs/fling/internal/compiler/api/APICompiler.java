package il.ac.technion.cs.fling.internal.compiler.api;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method.Chained;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.api.dom.SkeletonType;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;
import il.ac.technion.cs.fling.internal.compiler.api.dom.TypeName;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
/** Encodes deterministic pushdown automaton ({@link DPDA}) as type declarations
 * constituting proper fluent API of the automaton's language. The automaton is
 * compiled into an AST which can be translated to polymorphic, object-oriented
 * language.
 *
 * @author Ori Roth */
public abstract class APICompiler {
  /** Inducing automaton. */
  public final DPDA<Named, Token, Named> dpda;
  /** Compiled types. */
  protected final Map<TypeName, Type> types = new LinkedHashMap<>();
  /** Mapping of terminals to type variable nodes. */
  protected final Map<Named, SkeletonType> typeVariables = new LinkedHashMap<>();
  public APICompiler(final DPDA<Named, Token, Named> dpda) {
    this.dpda = dpda;
    dpda.Q().forEach(q -> typeVariables.put(q, SkeletonType.of(new TypeName(q))));
  }
  /** Compile fluent API. The object's state after calling this method is
   * undefined.
   *
   * @return compiled API */
  public Model compileFluentAPI() {
    return new Model(compileStartMethods(), types(), extraMethods());
  }
  /** Compile API concrete implementation.
   *
   * @return concrete implementation */
  protected abstract List<Chained> extraMethods();
  /** Compile API static start methods.
   *
   * @return compiled methods */
  protected abstract List<Method.Start> compileStartMethods();
  /** Compile API types.
   *
   * @return compiled types */
  protected abstract List<Type> types();
}
