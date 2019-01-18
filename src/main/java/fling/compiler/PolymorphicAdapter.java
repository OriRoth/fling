package fling.compiler;

import java.util.List;

import fling.compiler.ast.FluentAPINode;
import fling.compiler.ast.InterfaceNode;
import fling.compiler.ast.MethodNode;
import fling.compiler.ast.PolymorphicTypeNode;

public interface PolymorphicAdapter<Q, Σ, Γ> {
  String printTopType();
  String printBotType();
  String printIntermediateType(Compiler<Q, Σ, Γ>.TypeName name);
  String printIntermediateType(Compiler<Q, Σ, Γ>.TypeName name,
      List<PolymorphicTypeNode<Compiler<Q, Σ, Γ>.TypeName>> typeArguments);
  default String printType(PolymorphicTypeNode<Compiler<Q, Σ, Γ>.TypeName> type) {
    return type.isTop() ? printTopType()
        : type.isBot() ? printBotType()
            : type.typeArguments.isEmpty() ? printIntermediateType(type.name)
                : printIntermediateType(type.name, type.typeArguments);
  }
  String printStartMethod(PolymorphicTypeNode<Compiler<Q, Σ, Γ>.TypeName> returnType);
  String printTerminationMethod();
  String printIntermediateMethod(Compiler<Q, Σ, Γ>.MethodDeclaration declaration,
      PolymorphicTypeNode<Compiler<Q, Σ, Γ>.TypeName> returnType);
  default String printMethod(MethodNode<Compiler<Q, Σ, Γ>.TypeName, Compiler<Q, Σ, Γ>.MethodDeclaration> method) {
    return method.isStartMethod() ? printStartMethod(method.asStartMethod().returnType) : //
        method.isTerminationMethod() ? printTerminationMethod() : //
            printIntermediateMethod(method.asIntermediateMethod().declaration, method.asIntermediateMethod().returnType);
  }
  String printTopInterface();
  String printBotInterface();
  String printInterface(Compiler<Q, Σ, Γ>.InterfaceDeclaration declaration,
      List<MethodNode<Compiler<Q, Σ, Γ>.TypeName, Compiler<Q, Σ, Γ>.MethodDeclaration>> methods);
  default String printInterface(
      InterfaceNode<Compiler<Q, Σ, Γ>.TypeName, Compiler<Q, Σ, Γ>.MethodDeclaration, Compiler<Q, Σ, Γ>.InterfaceDeclaration> interfaze) {
    return interfaze.isTop() ? printTopInterface()
        : interfaze.isBot() ? printBotInterface() : printInterface(interfaze.declaration, interfaze.methods);
  }
  String printFluentAPI(
      FluentAPINode<Compiler<Q, Σ, Γ>.TypeName, Compiler<Q, Σ, Γ>.MethodDeclaration, Compiler<Q, Σ, Γ>.InterfaceDeclaration> fluentAPI);
}
