package org.spartan.fajita.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.rllp.RLLP;
import org.spartan.fajita.api.rllp.generation.RLLPEncoder;

public class Main {
  public static void apiGenerator(BNF bnf) throws IOException {
    final RLLP rllp = new RLLP(bnf);
    String code = RLLPEncoder.generate(rllp);
    String filename = "FluentAPI_" + Math.abs(bnf.hashCode() % 1000);
    try (FileOutputStream fos = new FileOutputStream(
        new File("/home/tomerlevi/fajita/src/main/java/org/spartan/fajita/api/junk/" + filename + ".java"))) {
      fos.write(code.getBytes(), 0, code.getBytes().length);
      fos.close();
      System.out.println(filename + " written successfully");
    }
  }
}
