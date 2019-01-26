package fling.compiler.api;

import java.util.List;

import fling.compiler.ast.AbstractMethodNode;
import fling.compiler.ast.FluentAPINode;
import fling.compiler.ast.InterfaceNode;
import fling.compiler.ast.PolymorphicTypeNode;
import fling.sententials.Named;
import fling.sententials.Terminal;

public interface APIPolymorphicLanguageAdapter<Q extends Named, Σ extends Terminal, Γ extends Named> {
  String printTopType();
  String printBotType();
  String printIntermediateType(APICompiler<Q, Σ, Γ>.TypeName name);
  String printIntermediateType(APICompiler<Q, Σ, Γ>.TypeName name,
      List<PolymorphicTypeNode<APICompiler<Q, Σ, Γ>.TypeName>> typeArguments);
  default String printType(PolymorphicTypeNode<APICompiler<Q, Σ, Γ>.TypeName> type) {
    return type.isTop() ? printTopType()
        : type.isBot() ? printBotType()
            : type.typeArguments.isEmpty() ? printIntermediateType(type.name)
                : printIntermediateType(type.name, type.typeArguments);
  }
  String printStartMethod(PolymorphicTypeNode<APICompiler<Q, Σ, Γ>.TypeName> returnType);
  String printTerminationMethod();
  String printIntermediateMethod(APICompiler<Q, Σ, Γ>.MethodDeclaration declaration,
      PolymorphicTypeNode<APICompiler<Q, Σ, Γ>.TypeName> returnType);
  default String printMethod(AbstractMethodNode<APICompiler<Q, Σ, Γ>.TypeName, APICompiler<Q, Σ, Γ>.MethodDeclaration> method) {
    return method.isStartMethod() ? printStartMethod(method.asStartMethod().returnType) : //
        method.isTerminationMethod() ? printTerminationMethod() : //
            printIntermediateMethod(method.asIntermediateMethod().declaration, method.asIntermediateMethod().returnType);
  }
  String printTopInterface();
  String printBotInterface();
  String printInterface(APICompiler<Q, Σ, Γ>.InterfaceDeclaration declaration,
      List<AbstractMethodNode<APICompiler<Q, Σ, Γ>.TypeName, APICompiler<Q, Σ, Γ>.MethodDeclaration>> methods);
  default String printInterface(
      InterfaceNode<APICompiler<Q, Σ, Γ>.TypeName, APICompiler<Q, Σ, Γ>.MethodDeclaration, APICompiler<Q, Σ, Γ>.InterfaceDeclaration> interfaze) {
    return interfaze.isTop() ? printTopInterface()
        : interfaze.isBot() ? printBotInterface() : printInterface(interfaze.declaration, interfaze.methods);
  }
  String printFluentAPI(
      FluentAPINode<APICompiler<Q, Σ, Γ>.TypeName, APICompiler<Q, Σ, Γ>.MethodDeclaration, APICompiler<Q, Σ, Γ>.InterfaceDeclaration> fluentAPI);
}
