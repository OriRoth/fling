package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.GeneralizedSymbol.*;

import static il.ac.technion.cs.fling.examples.languages.Java.V.*;
import static il.ac.technion.cs.fling.examples.languages.Java.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.BNF;
import il.ac.technion.cs.fling.adapters.JavaMediator;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.generated.DatalogAST.Program;
import il.ac.technion.cs.fling.examples.languages.Java.*;

/**
 * Fling input specifying the formal Datalog language.
 * 
 * @author Yossi Gil
 */
public class Java implements FluentLanguageAPI<Σ, V> {
  /** Set of terminals, i.e., method names of generated fluent API. */
  public enum Σ implements Terminal {
	  name,number,variable, method
  }

  /**
   * Set of non-terminals, i.e., abstract concepts of fluent API; these names
   * will be translated into names of classes of abstract syntax tree that Fling
   * generates, i.e., this AST will have class {@link Program} which will have a
   * list of {@link Statement}, etc.
   */
  public enum V implements Variable {
    Program, Statement, Expression, InfixExpression, PrefixExpression, 
    Declaration, Header, Member, Field, Constructor, Method, Initializer
  }

  @Override public Class<Σ> Σ() {
    return Σ.class;
  }
  @Override public Class<V> V() {
    return V.class;
  }

  /**
   * Datalog's grammar in Backus-Naur form.
   */
  public static final BNF bnf = bnf(). //
      start(Program). // This is the start symbol
      derive(Program).to(oneOrMore(Declaration)). //
      derive(Declaration).to(Header, oneOrMore(Member)). //
      specialize(Member).into(Field, Constructor, Method, Initializer).//
      build();

  @Override public il.ac.technion.cs.fling.BNF BNF() {
    return bnf;
  }
  /**
   * Prints the Datalog API/AST types/AST run-time compiler to corresponding
   * files.
   */
  public static void main(String[] args) throws IOException, FormatterException {
    /* The {@link JavaMediator} responsible for compiling the Java Datalog
     * API/AST types/AST run-time compiler. */
    JavaMediator jm = new JavaMediator(//
        bnf, // use this BNF as language specification
        // Name of package in which output will reside
        "il.ac.technion.cs.fling.examples.generated",
        // Name of generated class
        "Datalog", Σ.class //
    );
    Map<String, String> files = ((Supplier<Map<String, String>>) () -> {
      final Map<String, String> $ = new LinkedHashMap<>();
      $.put("Datalog", jm.apiClass);
      $.put("DatalogAST", jm.astClass);
      $.put("DatalogCompiler", jm.astCompilerClass);
      return $;
    }).get();
    String PATH = "./src/test/java/il/ac/technion/cs/fling/examples/generated/";
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
      Files.write(filePath, Collections.singleton(formatter.formatSource(file.getValue())), StandardOpenOption.CREATE,
          StandardOpenOption.WRITE);
      System.out.println("file " + file.getKey() + ".java written successfully.");
    }
  }
}
