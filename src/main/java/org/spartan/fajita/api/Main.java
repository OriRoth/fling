package org.spartan.fajita.api;

import static org.spartan.fajita.api.Main.NT.S;
import static org.spartan.fajita.api.Main.Term.a;
import static org.spartan.fajita.api.Main.Term.b;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.rllp.RLLP;
import org.spartan.fajita.api.rllp.generation.RLLPEncoder;

public class Main {
  public static void main(final String[] args) throws IOException {
    apiGenerator(testBNF());
  }
  static BNF testBNF() {
    return new BNFBuilder(Term.class, NT.class)//
        .start(S) //
        .derive(S).to(a).and(a).and(S)//
        .or(b) //
        .go();
  }
  public static void apiGenerator(BNF bnf) throws IOException {
    final RLLP rllp = new RLLP(bnf);
    String code = RLLPEncoder.generate(rllp);
    String filename = "FluentAPI_" + bnf.hashCode() % 1000;
    try (FileOutputStream fos = new FileOutputStream(
        new File("/home/tomerlevi/fajita/src/main/java/org/spartan/fajita/api/junk/" + filename + ".java"))) {
      fos.write(code.getBytes(), 0, code.getBytes().length);
      fos.close();
      System.out.println(filename+" written successfully");
    }
  }

  static enum Term implements Terminal {
    a, b
  }

  static enum NT implements NonTerminal {
    S
  }
}
