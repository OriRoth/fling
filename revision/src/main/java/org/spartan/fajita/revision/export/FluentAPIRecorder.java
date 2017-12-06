package org.spartan.fajita.revision.export;

import org.spartan.fajita.revision.ast.encoding.JamoosClassesRenderer;
import org.spartan.fajita.revision.bnf.EBNF;
import org.spartan.fajita.revision.parser.ell.ELLRecognizer;
import org.spartan.fajita.revision.parser.ell.Interpretation;
import org.spartan.fajita.revision.symbols.Terminal;

public class FluentAPIRecorder {
  public final ELLRecognizer ell;
  public final JamoosClassesRenderer jamoos;
  private String packagePath;

  public FluentAPIRecorder(EBNF ebnf, String packagePath) {
    this.ell = new ELLRecognizer(ebnf);
    this.jamoos = new JamoosClassesRenderer(ebnf, packagePath);
    this.packagePath = packagePath;
  }
  public void recordTerminal(Terminal t, Object... args) {
    ell.consume(new RuntimeVerb(t, args));
  }
  @Override public String toString() {
    return conclude().toString();
  }
  public String toString(int ident) {
    return conclude().toString(ident);
  }
  public void fold() {
    conclude();
  }
  public Interpretation conclude() {
    return ell.ast();
  }
  public <S> S ast(String astClassName) {
    return new ASTBuilder(conclude(), jamoos, packagePath + "." + astClassName).build();
  }
}
