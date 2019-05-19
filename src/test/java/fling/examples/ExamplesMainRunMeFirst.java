package fling.examples;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

import org.junit.Test;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

import fling.examples.automata.*;
import fling.examples.languages.*;

@SuppressWarnings("static-method") public class ExamplesMainRunMeFirst {
  private static final boolean FORMAT_OUTPUT = true;
  private static final Map<String, String> files = ((Supplier<Map<String, String>>) () -> {
    final Map<String, String> $ = new LinkedHashMap<>();
    $.put("ExtendedBalancedParentheses", ExtendedBalancedParentheses.fluentAPI);
    $.put("LongFall", LongFall.JavaFluentAPI);
    $.put("AnBn", AnBn.JavaFluentAPI);
    $.put("AeqB", AeqB.JavaFluentAPI);
    $.put("BalancedParentheses", BalancedParentheses.jm.apiClass);
    $.put("BalancedParenthesesAST", BalancedParentheses.jm.astClass);
    $.put("BalancedParenthesesCompiler", BalancedParentheses.jm.astCompilerClass);
    $.put("TaggedBalancedParentheses", TaggedBalancedParentheses.jm.apiClass);
    $.put("TaggedBalancedParenthesesAST", TaggedBalancedParentheses.jm.astClass);
    $.put("TaggedBalancedParenthesesCompiler", TaggedBalancedParentheses.jm.astCompilerClass);
    $.put("Datalog", Datalog.jm.apiClass);
    $.put("DatalogAST", Datalog.jm.astClass);
    $.put("DatalogCompiler", Datalog.jm.astCompilerClass);
    $.put("SubFigure", SubFigure.jm.apiClass);
    $.put("SubFigureAST", SubFigure.jm.astClass);
    $.put("SubFigureCompiler", SubFigure.jm.astCompilerClass);
    $.put("ArithmeticExpression", ArithmeticExpression.jm.apiClass);
    $.put("ArithmeticExpressionAST", ArithmeticExpression.jm.astClass);
    $.put("ArithmeticExpressionCompiler", ArithmeticExpression.jm.astCompilerClass);
    $.put("BNFAPI", BNF.jm.apiClass);
    $.put("BNFAPIAST", BNF.jm.astClass);
    $.put("BNFAPICompiler", BNF.jm.astCompilerClass);
    $.put("RegularExpression", RegularExpression.jm.apiClass);
    $.put("RegularExpressionAST", RegularExpression.jm.astClass);
    $.put("RegularExpressionCompiler", RegularExpression.jm.astCompilerClass);
    $.put("HTMLTable", HTMLTable.jm.apiClass);
    $.put("HTMLTableAST", HTMLTable.jm.astClass);
    $.put("HTMLTableCompiler", HTMLTable.jm.astCompilerClass);
    return $;
  }).get();
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
