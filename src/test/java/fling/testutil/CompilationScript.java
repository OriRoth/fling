package fling.testutil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

import fling.languages.AnBn;
import fling.languages.BalancedParentheses;
import fling.languages.ExtendedBalancedParentheses;
import fling.languages.LongFall;
import fling.languages.TaggedBalancedParentheses;

public class CompilationScript {
  private static final Map<String, String> files = new LinkedHashMap<>();
  static {
//    files.put("ExtendedBalancedParentheses", ExtendedBalancedParentheses.fluentAPI);
//    files.put("LongFall", LongFall.JavaFluentAPI);
//    files.put("AnBn", AnBn.JavaFluentAPI);
//    files.put("BalancedParentheses", BalancedParentheses.jm.apiClass);
//    files.put("BalancedParenthesesAST", BalancedParentheses.jm.astClass);
//    files.put("BalancedParenthesesCompiler", BalancedParentheses.jm.astCompilerClass);
    files.put("TaggedBalancedParentheses", TaggedBalancedParentheses.jm.apiClass);
    files.put("TaggedBalancedParenthesesAST", TaggedBalancedParentheses.jm.astClass);
    files.put("TaggedBalancedParenthesesCompiler", TaggedBalancedParentheses.jm.astCompilerClass);
  }
  private static final String PATH = "./src/test/java/fling/generated/";

  public static void main(String[] args) throws IOException, FormatterException {
    System.out.println("project path: " + PATH);
    Path outputFolder = Paths.get(PATH);
    if (!Files.exists(outputFolder)) {
      Files.createDirectory(outputFolder);
      System.out.println("directory " + PATH + " created successfully");
    }
    Formatter formatter = new Formatter();
    for (Entry<String, String> file : files.entrySet()) {
      Path filePath = Paths.get(PATH + file.getKey() + ".java");
      if (Files.exists(filePath))
        Files.delete(filePath);
      Files.write(filePath, Collections.singleton(formatter.formatSource(file.getValue())), StandardOpenOption.CREATE,
          StandardOpenOption.WRITE);
      System.out.println("file " + file.getKey() + ".java written successfully.");
    }
  }
}
