package fling.internal.compiler.ast;

import fling.internal.compiler.ast.nodes.ASTCompilationUnitNode;
import fling.internal.compiler.ast.nodes.AbstractClassNode;
import fling.internal.compiler.ast.nodes.ClassNode;
import fling.internal.compiler.ast.nodes.ConcreteClassNode;

public interface PolymorphicLanguageASTAdapterBase {
  String printASTClass(ASTCompilationUnitNode compilationUnit);
  String printAbstractClass(AbstractClassNode abstractClass);
  String printConcreteClass(ConcreteClassNode concreteClass);
  default String printClass(ClassNode clazz) {
    return clazz.isAbstract() ? printAbstractClass(clazz.asAbstract()) : printConcreteClass(clazz.asConcrete());
  }
}
