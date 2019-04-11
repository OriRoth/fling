package fling.internal.compiler.ast;

import fling.internal.grammar.sententials.Variable;

public interface ASTParserCompiler {
  String printParserClass();
  String getParsingMethodName(Variable variable);
}
