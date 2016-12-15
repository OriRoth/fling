package org.spartan.fajita.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
  public static void apiGenerator(String apiName, String code) throws IOException {
    String filename = apiName + ".java";
    try (FileOutputStream fos = new FileOutputStream(
        new File("/home/tomerlevi/fajita/src/main/java/org/spartan/fajita/api/junk/" + filename))) {
      fos.write(code.getBytes(), 0, code.getBytes().length);
      fos.close();
      System.out.println(filename + " written successfully");
    }
  }
}
