package il.ac.technion.cs.fling.internal.compiler.ast;

import il.ac.technion.cs.fling.internal.compiler.ast.nodes.*;

public interface PolymorphicLanguageASTAdapterBase {
  String printASTClass(ASTCompilationUnitNode compilationUnit);

  String printAbstractClass(AbstractClassNode abstractClass);

  String printConcreteClass(ConcreteClassNode concreteClass);

  default String printClass(final ClassNode clazz) {
    return clazz.isAbstract() ? printAbstractClass(clazz.asAbstract()) : printConcreteClass(clazz.asConcrete());
  }
}
