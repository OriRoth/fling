package org.spartan.fajita.revision.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

public class Main {
  public static final String packagePath = "org.spartan.fajita.revision.junk";
  public static final String projectPath = "/home/ori/Desktop/git/fajita/revision/src/test/java/";

  public static void apiGenerator(Map<String, String> files) throws IOException {
    String filePath = projectPath + packagePath.replace('.', '/') + '/';
    for (Entry<String, String> f : files.entrySet()) {
      final String fname = f.getKey();
      byte[] fileContent = f.getValue().getBytes();
      File ff = new File(filePath + fname);
      ff.getParentFile().mkdirs();
      try (FileOutputStream fos = new FileOutputStream(ff)) {
        fos.write(fileContent, 0, fileContent.length);
        System.out.println(fname + " written successfully");
      }
    }
  }
}
