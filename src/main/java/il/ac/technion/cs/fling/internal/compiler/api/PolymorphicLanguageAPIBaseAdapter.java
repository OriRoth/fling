package il.ac.technion.cs.fling.internal.compiler.api;

import java.util.List;

import il.ac.technion.cs.fling.internal.compiler.api.APICompiler.InterfaceDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler.MethodDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler.TypeName;
import il.ac.technion.cs.fling.internal.compiler.api.dom.CompilationUnit;
import il.ac.technion.cs.fling.internal.compiler.api.dom.AbstractMethod;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Interfac;
import il.ac.technion.cs.fling.internal.compiler.api.dom.PolymorphicType;

public interface PolymorphicLanguageAPIBaseAdapter {
  String printFluentAPI(CompilationUnit<TypeName, MethodDeclaration, InterfaceDeclaration> fluentAPI);

  String topTypeName();

  String bottomTypeName();

  String typeName(TypeName name);

  String typeName(TypeName name, List<PolymorphicType<TypeName>> typeArguments);

  default String printType(final PolymorphicType<TypeName> type) {
    return type.isTop() ? topTypeName()
        : type.isBot() ? bottomTypeName()
            : type.typeArguments.isEmpty() ? typeName(type.name) : typeName(type.name, type.typeArguments);
  }

  String startMethod(MethodDeclaration declaration, PolymorphicType<TypeName> returnType);

  String printTerminationMethod();

  String printIntermediateMethod(MethodDeclaration declaration, PolymorphicType<TypeName> returnType);

  default String printMethod(final AbstractMethod<TypeName, MethodDeclaration> method) {
    return method.isStartMethod() ? startMethod(method.asStartMethod().declaration, method.asStartMethod().returnType) : //
        method.isTerminationMethod() ? printTerminationMethod() : //
            printIntermediateMethod(method.asIntermediateMethod().declaration,
                method.asIntermediateMethod().returnType);
  }

  String printTopInterface();

  String printBotInterface();

  String printInterface(InterfaceDeclaration declaration,
      List<AbstractMethod<TypeName, MethodDeclaration>> methods);

  default String printInterface(final Interfac<TypeName, MethodDeclaration, InterfaceDeclaration> interfaze) {
    return interfaze.isTop() ? printTopInterface()
        : interfaze.isBot() ? printBotInterface() : printInterface(interfaze.declaration, interfaze.methods);
  }
}
