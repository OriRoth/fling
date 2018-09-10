package roth.ori.fling.export;

import roth.ori.fling.ast.encoding.JamoosClassesRenderer;
import roth.ori.fling.bnf.EBNF;
import roth.ori.fling.parser.ell.ELLRecognizer;
import roth.ori.fling.parser.ell.Interpretation;
import roth.ori.fling.symbols.Terminal;

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
    return new ASTBuilder(conclude(), jamoos, packagePath + "." + astClassName, ell.analyzer).build();
  }
}
