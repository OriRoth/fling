package il.ac.technion.cs.fling.adapters;

import java.util.List;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.MethodDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.dom.CompilationUnit;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Interface;
import il.ac.technion.cs.fling.internal.compiler.api.dom.InterfaceDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;
import il.ac.technion.cs.fling.internal.compiler.api.dom.TypeName;

public abstract class APIGenerator {
  public final String bottomName;
  protected final String endName;
  protected final Namer namer;
  public final String topName;

  protected APIGenerator(final Namer namer) {
    this(namer, "$");
  }

  protected APIGenerator(final Namer namer, final String endName) {
    this(namer, endName, "BOTTOM", "TOP");
  }

  protected APIGenerator(final Namer namer, final String endName, final String bottomName, final String topName) {
    this.namer = namer;
    this.endName = endName;
    this.bottomName = bottomName;
    this.topName = topName;
  }

  abstract public String renderInterface(InterfaceDeclaration declaration, List<Method> methods);

  abstract public String printIntermediateMethod(MethodDeclaration declaration, Type returnType);

  abstract public String renderCompilationUnit(CompilationUnit fluentAPI);

  final String renderInterface(final Interface i) {
    return i.render(this);
  }

  abstract public String renderInterfaceBottom();

  abstract public String renderInterfaceTop();

  final String renderMethod(final Method m) {
    return m.render(this);
  }

  abstract public String renderMethod(MethodDeclaration declaration, Type returnType);

  abstract public String renderTerminationMethod();

  public final String renderType(final Type t) {
    return t.render(this);
  }

  abstract public String renderTypeMonomorphic(TypeName name);

  abstract public String renderTypePolymorphic(TypeName name, List<Type> typeArguments);
}