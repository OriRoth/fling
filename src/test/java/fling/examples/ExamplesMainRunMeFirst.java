package fling.examples;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.Map.Entry;

import org.junit.Test;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

import fling.examples.automata.*;
// import fling.examples.automata.AnBn;
// import fling.examples.automata.ExtendedBalancedParentheses;
// import fling.examples.automata.LongFall;
// import fling.examples.languages.Arithmetic;
import fling.examples.languages.*;

@SuppressWarnings("static-method") public class ExamplesMainRunMeFirst {
  private static final boolean FORMAT_OUTPUT = true;
  private static final Map<String, String> files = new LinkedHashMap<>();
  static {
    files.put("ExtendedBalancedParentheses", ExtendedBalancedParentheses.fluentAPI);
    files.put("LongFall", LongFall.JavaFluentAPI);
    files.put("AnBn", AnBn.JavaFluentAPI);
    files.put("AeqB", AeqB.JavaFluentAPI);
    files.put("BalancedParentheses", BalancedParentheses.jm.apiClass);
    files.put("BalancedParenthesesAST", BalancedParentheses.jm.astClass);
    files.put("BalancedParenthesesCompiler", BalancedParentheses.jm.astCompilerClass);
    files.put("TaggedBalancedParentheses", TaggedBalancedParentheses.jm.apiClass);
    files.put("TaggedBalancedParenthesesAST", TaggedBalancedParentheses.jm.astClass);
    files.put("TaggedBalancedParenthesesCompiler", TaggedBalancedParentheses.jm.astCompilerClass);
    files.put("Datalog", Datalog.jm.apiClass);
    files.put("DatalogAST", Datalog.jm.astClass);
    files.put("DatalogCompiler", Datalog.jm.astCompilerClass);
    files.put("SubFigure", SubFigure.jm.apiClass);
    files.put("SubFigureAST", SubFigure.jm.astClass);
    files.put("SubFigureCompiler", SubFigure.jm.astCompilerClass);
    files.put("Arithmetic", Arithmetic.jm.apiClass);
    files.put("ArithmeticAST", Arithmetic.jm.astClass);
    files.put("ArithmeticCompiler", Arithmetic.jm.astCompilerClass);
    files.put("BNFAPI", BNF.jm.apiClass);
    files.put("BNFAPIAST", BNF.jm.astClass);
    files.put("BNFAPICompiler", BNF.jm.astCompilerClass);
  }
  private static final String PATH = "./src/test/java/fling/examples/generated/";

  @Test public void compile() throws IOException, FormatterException {
    System.out.println("project path: " + PATH);
    final Path outputFolder = Paths.get(PATH);
    if (!Files.exists(outputFolder)) {
      Files.createDirectory(outputFolder);
      System.out.println("directory " + PATH + " created successfully");
    }
    final Formatter formatter = new Formatter();
    for (final Entry<String, String> file : files.entrySet()) {
      final Path filePath = Paths.get(PATH + file.getKey() + ".java");
      if (Files.exists(filePath))
        Files.delete(filePath);
      Files.write(filePath, Collections.singleton(FORMAT_OUTPUT ? //
          formatter.formatSource(file.getValue()) : //
          file.getValue() //
      ), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
      System.out.println("file " + file.getKey() + ".java written successfully.");
    }
  }
  public static void main(final String[] args) throws IOException, FormatterException {
    new ExamplesMainRunMeFirst().compile();
  }
}
