package fling.compiler.ast;

import fling.compiler.ast.nodes.ASTCompilationUnitNode;
import fling.compiler.ast.nodes.AbstractClassNode;
import fling.compiler.ast.nodes.ClassNode;
import fling.compiler.ast.nodes.ConcreteClassNode;

public interface PolymorphicLanguageASTAdapter {
  String printASTClass(ASTCompilationUnitNode compilationUnit);
  String printAbstractClass(AbstractClassNode abstractClass);
  String printConcreteClass(ConcreteClassNode concreteClass);
  default String printClass(ClassNode clazz) {
    return clazz.isAbstract() ? printAbstractClass(clazz.asAbstract()) : printConcreteClass(clazz.asConcrete());
  }
}
