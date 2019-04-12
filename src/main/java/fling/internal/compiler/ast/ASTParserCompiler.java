package fling.internal.compiler.ast;

import fling.Variable;

public interface ASTParserCompiler {
  String printParserClass();
  String getParsingMethodName(Variable variable);
}
