package fling.compiler.ast;

import fling.compiler.ast.nodes.AbstractClassNode;
import fling.compiler.ast.nodes.ClassNode;
import fling.compiler.ast.nodes.ConcreteClassNode;

public interface PolymorphicLanguageASTAdapterBase extends PolymorphicLanguageASTAdapter {
  String printAbstractClass(AbstractClassNode abstractClass);
  String printConcreteClass(ConcreteClassNode concreteClass);
  default String printClass(ClassNode clazz) {
    return clazz.isAbstract() ? printAbstractClass(clazz.asAbstract()) : printConcreteClass(clazz.asConcrete());
  }
}
