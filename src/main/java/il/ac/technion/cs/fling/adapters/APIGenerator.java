package il.ac.technion.cs.fling.adapters;

import java.util.List;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.InterfaceDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.MethodDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.TypeName;
import il.ac.technion.cs.fling.internal.compiler.api.dom.CompilationUnit;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Interface;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;

public abstract class APIGenerator {
  protected final Namer namer;
  protected final String endName;
  protected final String bottomName;
  protected final String topName;

  protected APIGenerator(final Namer namer, final String endName, String bottomName, String topName) {
    this.namer = namer;
    this.endName = endName;
    this.bottomName = bottomName;
    this.topName = topName;
  }

  protected APIGenerator(final Namer namer, final String endName) {
    this(namer, endName, "BOTTOM", "TOP");
  }

  public final String bottomTypeName() {
    return bottomName;
  }

  abstract public String printBotInterface();

  abstract public String printFluentAPI(CompilationUnit fluentAPI);

  final String printInterface(final Interface i) {
    return i.isTop() ? printTopInterface() : i.isBot() ? printBotInterface() : printInterface(i.declaration, i.methods);
  }

  abstract public String printInterface(InterfaceDeclaration declaration, List<Method> methods);

  abstract public String printIntermediateMethod(MethodDeclaration declaration, Type returnType);

  final String printMethod(final Method method) {
    return method.isStartMethod() ? startMethod(method.asStartMethod().declaration, method.asStartMethod().returnType) : //
        method.isTerminationMethod() ? printTerminationMethod() : //
            printIntermediateMethod(method.asIntermediateMethod().declaration,
                method.asIntermediateMethod().returnType);
  }

  abstract public String printTerminationMethod();

  abstract public String printTopInterface();

  public final String printType(final Type type) {
    return type.isTop() ? topTypeName()
        : type.isBot() ? bottomTypeName()
            : type.arguments.isEmpty() ? typeName(type.name) : typeName(type.name, type.arguments);
  }

  abstract public String startMethod(MethodDeclaration declaration, Type returnType);

  final public String topTypeName() {
    return topTypeName();
  }

  abstract public String typeName(TypeName name);

  abstract public String typeName(TypeName name, List<Type> typeArguments);
}