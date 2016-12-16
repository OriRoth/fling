package org.spartan.fajita.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
  private static final String packagePath = "org.spartan.fajita.api.junk";

  public static void apiGenerator(String apiName, String _code) throws IOException {
    String filename = apiName + ".java";
    String packageStatement = "package " + packagePath + ";";
    String code = packageStatement + "\n" + _code;
    String filePath = "/home/tomerlevi/fajita/src/main/java/" + packagePath.replace('.', '/') + '/';
    try (FileOutputStream fos = new FileOutputStream(new File(filePath + filename))) {
      fos.write(code.getBytes(), 0, code.getBytes().length);
      System.out.println(filename + " written successfully");
    }
  }
}
