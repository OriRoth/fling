package il.ac.technion.cs.fling.examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import org.junit.Test;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

import il.ac.technion.cs.fling.adapters.JavaMediator;
import il.ac.technion.cs.fling.examples.automata.AeqB;
import il.ac.technion.cs.fling.examples.automata.AnBn;
import il.ac.technion.cs.fling.examples.automata.ExtendedBalancedParentheses;
import il.ac.technion.cs.fling.examples.automata.LongFall;
import il.ac.technion.cs.fling.examples.languages.ArithmeticExpression;
import il.ac.technion.cs.fling.examples.languages.BNF;
import il.ac.technion.cs.fling.examples.languages.BalancedParentheses;
import il.ac.technion.cs.fling.examples.languages.Datalog;
import il.ac.technion.cs.fling.examples.languages.EBNF;
import il.ac.technion.cs.fling.examples.languages.HTMLTable;
import il.ac.technion.cs.fling.examples.languages.QuantifiersTestLanguage;
import il.ac.technion.cs.fling.examples.languages.RegularExpression;
import il.ac.technion.cs.fling.examples.languages.SimpleArithmetic;
import il.ac.technion.cs.fling.examples.languages.SubFigure;
import il.ac.technion.cs.fling.examples.languages.TableMaker;
import il.ac.technion.cs.fling.examples.languages.TaggedBalancedParentheses;

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
    $.put("TableMaker", new TableMaker().apiClass);
    return $;
  }).get();
  private static final String PATH = "./src/test/java/il/ac/technion/cs/fling/examples/generated/";

  @Test public void compile() throws IOException {
    System.out.println("project path: " + PATH);
    final Path outputFolder = Paths.get(PATH);
    if (!Files.exists(outputFolder)) {
      Files.createDirectory(outputFolder);
      System.out.println("directory " + PATH + " created successfully");
    }
    for (final Entry<String, String> file : files.entrySet()) {
      final String fileName = file.getKey();
      final String content = file.getValue();
      compile(fileName, content);
      System.out.println("file " + fileName + ".java written successfully.");
    }
  }

  private void compile(final String fileName, final String content) throws IOException {
    final Path filePath = Paths.get(PATH + fileName + ".java");
    if (Files.exists(filePath))
      Files.delete(filePath);
    Files.write(filePath, Collections.singleton(FORMAT_OUTPUT ? //
        format(content) : //
        content //
    ), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
  }

  private String format(final String content) {
    try {
      return new Formatter().formatSource(content);
    } catch (final FormatterException e) {
      e.printStackTrace();
      return content;
    }
  }

  public static void main(final String[] args) throws IOException {
    new LoopOverLanguageDefinitions().compile();
  }
}
