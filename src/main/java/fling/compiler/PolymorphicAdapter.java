package fling.compiler;

import fling.compiler.ast.FluentAPINode;
import fling.compiler.ast.InterfaceNode;
import fling.compiler.ast.MethodNode;
import fling.compiler.ast.PolymorphicTypeNode;

public interface PolymorphicAdapter<Q, Σ, Γ> {
  String printType(PolymorphicTypeNode<Compiler<Q, Σ, Γ>.TypeName> type);
  String printMethod(MethodNode<Compiler<Q, Σ, Γ>.TypeName, Compiler<Q, Σ, Γ>.MethodDeclaration> method);
  String printInterface(
      InterfaceNode<Compiler<Q, Σ, Γ>.TypeName, Compiler<Q, Σ, Γ>.MethodDeclaration, Compiler<Q, Σ, Γ>.InterfaceDeclaration> interfaze);
  String printFluentAPI(String name,
      FluentAPINode<Compiler<Q, Σ, Γ>.TypeName, Compiler<Q, Σ, Γ>.MethodDeclaration, Compiler<Q, Σ, Γ>.InterfaceDeclaration> fluentAPI);
}
