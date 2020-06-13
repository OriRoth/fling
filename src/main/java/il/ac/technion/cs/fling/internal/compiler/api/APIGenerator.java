package il.ac.technion.cs.fling.internal.compiler.api;

import java.util.List;

import il.ac.technion.cs.fling.internal.compiler.api.dom.CompilationUnit;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Interface;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;

public interface APIGenerator {
  String printFluentAPI(CompilationUnit fluentAPI);

  String topTypeName();

  String bottomTypeName();

  String typeName(TypeName name);

  String typeName(TypeName name, List<Type> typeArguments);

  default String printType(final Type type) {
    return type.isTop() ? topTypeName()
        : type.isBot() ? bottomTypeName()
            : type.arguments.isEmpty() ? typeName(type.name) : typeName(type.name, type.arguments);
  }

  String startMethod(MethodDeclaration declaration, Type returnType);

  String printTerminationMethod();

  String printIntermediateMethod(MethodDeclaration declaration, Type returnType);

  default String printMethod(final Method method) {
    return method.isStartMethod() ? startMethod(method.asStartMethod().declaration, method.asStartMethod().returnType) : //
        method.isTerminationMethod() ? printTerminationMethod() : //
            printIntermediateMethod(method.asIntermediateMethod().declaration,
                method.asIntermediateMethod().returnType);
  }

  String printTopInterface();

  String printBotInterface();

  String printInterface(InterfaceDeclaration declaration, List<Method> methods);

  default String printInterface(final Interface interfaze) {
    return interfaze.isTop() ? printTopInterface()
        : interfaze.isBot() ? printBotInterface() : printInterface(interfaze.declaration, interfaze.methods);
  }
}
