package fling.internal.compiler.ast;

import fling.internal.compiler.ast.nodes.*;

public interface PolymorphicLanguageASTAdapterBase {
  String printASTClass(ASTCompilationUnitNode compilationUnit);
  String printAbstractClass(AbstractClassNode abstractClass);
  String printConcreteClass(ConcreteClassNode concreteClass);
  default String printClass(final ClassNode clazz) {
    return clazz.isAbstract() ? printAbstractClass(clazz.asAbstract()) : printConcreteClass(clazz.asConcrete());
  }
}
