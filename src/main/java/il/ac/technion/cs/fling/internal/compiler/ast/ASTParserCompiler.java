package il.ac.technion.cs.fling.internal.compiler.ast;

import il.ac.technion.cs.fling.Variable;

public interface ASTParserCompiler {
  /** @return run-time parser program */
  String printParserClass();

  /** @param variable grammar variable
   * @return name of its parsing method */
  String getParsingMethodName(Variable variable);
}
