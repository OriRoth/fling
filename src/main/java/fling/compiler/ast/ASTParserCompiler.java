package fling.compiler.ast;

import fling.grammar.sententials.Variable;

public interface ASTParserCompiler {
  String printParserClass();
  String getParsingMethodName(Variable variable);
}
