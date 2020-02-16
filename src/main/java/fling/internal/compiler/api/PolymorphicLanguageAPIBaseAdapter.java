package fling.internal.compiler.api;

import java.util.*;

import fling.internal.compiler.api.APICompiler.MethodDeclaration;
import fling.internal.compiler.api.nodes.*;

public interface PolymorphicLanguageAPIBaseAdapter {
  String printFluentAPI(
      APICompilationUnitNode<APICompiler.TypeName, APICompiler.MethodDeclaration, APICompiler.InterfaceDeclaration> fluentAPI);
  String printTopType();
  String printBotType();
  String printIntermediateType(APICompiler.TypeName name);
  String printIntermediateType(APICompiler.TypeName name, List<PolymorphicTypeNode<APICompiler.TypeName>> typeArguments);
  default String printType(final PolymorphicTypeNode<APICompiler.TypeName> type) {
    return type.isTop() ? printTopType()
        : type.isBot() ? printBotType()
            : type.typeArguments.isEmpty() ? printIntermediateType(type.name)
                : printIntermediateType(type.name, type.typeArguments);
  }
  String printStartMethod(MethodDeclaration declaration, PolymorphicTypeNode<APICompiler.TypeName> returnType);
  String printTerminationMethod();
  String printIntermediateMethod(APICompiler.MethodDeclaration declaration, PolymorphicTypeNode<APICompiler.TypeName> returnType);
  default String printMethod(final AbstractMethodNode<APICompiler.TypeName, APICompiler.MethodDeclaration> method) {
    return method.isStartMethod() ? printStartMethod(method.asStartMethod().declaration, method.asStartMethod().returnType) : //
        method.isTerminationMethod() ? printTerminationMethod() : //
            printIntermediateMethod(method.asIntermediateMethod().declaration, method.asIntermediateMethod().returnType);
  }
  String printTopInterface();
  String printBotInterface();
  String printInterface(APICompiler.InterfaceDeclaration declaration,
      List<AbstractMethodNode<APICompiler.TypeName, APICompiler.MethodDeclaration>> methods);
  default String printInterface(
      final InterfaceNode<APICompiler.TypeName, APICompiler.MethodDeclaration, APICompiler.InterfaceDeclaration> interfaze) {
    return interfaze.isTop() ? printTopInterface()
        : interfaze.isBot() ? printBotInterface() : printInterface(interfaze.declaration, interfaze.methods);
  }
}
