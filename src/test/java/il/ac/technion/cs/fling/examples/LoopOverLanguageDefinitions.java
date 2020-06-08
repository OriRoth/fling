package il.ac.technion.cs.fling.examples;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

import org.junit.Test;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

import il.ac.technion.cs.fling.adapters.JavaMediator;
import il.ac.technion.cs.fling.examples.automata.*;
import il.ac.technion.cs.fling.examples.languages.*;

@SuppressWarnings("static-method") public class LoopOverLanguageDefinitions {

  private static final boolean FORMAT_OUTPUT = true;
  @SuppressWarnings("rawtypes") private static final FluentLanguageAPI[] BNFAPIs = { //
      new BalancedParentheses(), //
      new TaggedBalancedParentheses(), //
      new Datalog(), //
      new SubFigure(), //
      new ArithmeticExpression(), //
      new BNF(), //
      new EBNF(), //
      new RegularExpression(), //
      new HTMLTable(), //
      new SimpleArithmetic(), //
      new QuantifiersTestLanguage(), //
  };
  private static final Map<String, String> files = ((Supplier<Map<String, String>>) () -> {
    final Map<String, String> $ = new LinkedHashMap<>();
    $.put("ExtendedBalancedParentheses", ExtendedBalancedParentheses.fluentAPI);
    $.put("LongFall", LongFall.JavaFluentAPI);
    $.put("AnBn", AnBn.JavaFluentAPI);
    $.put("AeqB", AeqB.JavaFluentAPI);
    for (FluentLanguageAPI<?, ?> api : BNFAPIs) {
      JavaMediator mediator = new JavaMediator(api.BNF(), //
          "il.ac.technion.cs.fling.examples.generated", api.name(), api.Σ());
      $.put(api.name(), mediator.apiClass);
      $.put(api.name() + "AST", mediator.astClass);
      $.put(api.name() + "Compiler", mediator.astCompilerClass);
    }
    $.put("TableMaker", TableMaker.apiClass);
    return $;
  }).get();
  private static final String PATH = "./src/test/java/il/ac/technion/cs/fling/examples/generated/";

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
    new LoopOverLanguageDefinitions().compile();
  }
}
