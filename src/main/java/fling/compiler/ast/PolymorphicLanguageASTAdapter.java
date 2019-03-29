package fling.compiler.ast;

import fling.compiler.ast.nodes.ASTCompilationUnitNode;

public interface PolymorphicLanguageASTAdapter {
  String printASTClass(ASTCompilationUnitNode compilationUnit);
  default String printASTParser() {
    throw new UnsupportedOperationException();
  }
}
