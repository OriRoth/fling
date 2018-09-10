package roth.ori.fling.examples.usage.datalog;

import roth.ori.fling.export.ASTVisitor;
import roth.ori.fling.junk.DatalogAST;
import roth.ori.fling.junk.DatalogAST.Program;

public class PrintDatalogProgramASTVisitor {
  @SuppressWarnings("unused") public static void main(String[] args) {
    new ASTVisitor(DatalogAST.class) {
      public boolean visit(Program p) {
        return true;
      }
    }.startVisit(Ancestor.program());
  }
}
