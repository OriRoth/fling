package il.ac.technion.cs.fling.internal.compiler.api.dom;
import java.util.*;
import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
/** Encodes deterministic pushdown automaton ({@link DPDA}) as type declarations
 * constituting proper fluent API of the automaton's language. The automaton is
 * compiled into an AST which can be translated to polymorphic, object-oriented
 * language.
 *
 * @author Ori Roth */
public abstract class DPDAToModel {
  /** Inducing automaton. */
  final DPDA<Named, Token, Named> dpda;
  /** Compiled types. */
  final Map<Type.Name, Type> types = new LinkedHashMap<>();
  void add(final Type t) {
    types.put(t.name, t);
  }
  {
    add(Type.named(Type.Name.BOTTOM));
    add(Type.named(Type.Name.TOP));
  }
  /** Mapping of terminals to type variable nodes. */
  final Map<Named, Type.Grounded> typeVariables = new LinkedHashMap<>();
  protected DPDAToModel(final DPDA<Named, Token, Named> dpda) {
    this.dpda = dpda;
    dpda.Q().forEach(q -> typeVariables.put(q, Type.Grounded.of(Type.Name.q(q))));
  }
  /** Compile fluent API. The object's state after calling this method is
   * undefined.
   *
   * @return compiled API */
  public Model go() {
    return new Model(startMethods(), types());
  }
  /** Compile API static start methods.
   *
   * @return compiled methods */
  protected abstract List<Method> startMethods();
  /** Compile API types.
   *
   * @return compiled types */
  private List<Type> types() {
    return new ArrayList<>(types.values());
  }
}