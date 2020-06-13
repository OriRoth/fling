package il.ac.technion.cs.fling.internal.compiler.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.internal.compiler.api.dom.CompilationUnit;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Interface;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;
import il.ac.technion.cs.fling.internal.compiler.api.dom.TypeBody;
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
  protected final Map<TypeName, Interface> types = new LinkedHashMap<>();
  /** Mapping of terminals to type variable nodes. */
  protected final Map<Named, Type> typeVariables = new LinkedHashMap<>();

  public APICompiler(final DPDA<Named, Token, Named> dpda) {
    this.dpda = dpda;
    dpda.Q().forEach(q -> typeVariables.put(q, new Type(new TypeName(q))));
  }

  /** Compile fluent API. The object's state after calling this method is
   * undefined.
   *
   * @return compiled API */
  public CompilationUnit compileFluentAPI() {
    return new CompilationUnit(compileStartMethods(), compileInterfaces(), complieConcreteImplementation());
  }

  /** Compile API concrete implementation.
   *
   * @return concrete implementation */
  protected abstract TypeBody<TypeName, MethodDeclaration> complieConcreteImplementation();

  /** Compile API static start methods.
   *
   * @return compiled methods */
  protected abstract List<Method> compileStartMethods();

  /** Compile API types.
   *
   * @return compiled types */
  protected abstract List<Interface> compileInterfaces();
}
