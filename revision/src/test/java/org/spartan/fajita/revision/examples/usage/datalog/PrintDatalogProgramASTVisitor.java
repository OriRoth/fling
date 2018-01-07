package org.spartan.fajita.revision.examples.usage.datalog;

import org.spartan.fajita.revision.export.ASTVisitor;
import org.spartan.fajita.revision.junk.DatalogAST;
import org.spartan.fajita.revision.junk.DatalogAST.Program;

public class PrintDatalogProgramASTVisitor {
  @SuppressWarnings("unused") public static void main(String[] args) {
    new ASTVisitor(DatalogAST.class) {
      public boolean visit(Program p) {
        return true;
      }
    }.startVisit(Ancestor.program());
  }
}