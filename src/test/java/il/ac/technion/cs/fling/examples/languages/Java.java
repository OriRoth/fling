package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.Java.V.Constructor;
import static il.ac.technion.cs.fling.examples.languages.Java.V.Declaration;
import static il.ac.technion.cs.fling.examples.languages.Java.V.Field;
import static il.ac.technion.cs.fling.examples.languages.Java.V.Header;
import static il.ac.technion.cs.fling.examples.languages.Java.V.Initializer;
import static il.ac.technion.cs.fling.examples.languages.Java.V.Member;
import static il.ac.technion.cs.fling.examples.languages.Java.V.Method;
import static il.ac.technion.cs.fling.examples.languages.Java.V.Program;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

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

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

import il.ac.technion.cs.fling.adapters.JavaMediator;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.generated.DatalogAST.Program;
import il.ac.technion.cs.fling.examples.languages.Java.V;
import il.ac.technion.cs.fling.examples.languages.Java.Σ;
import il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;

/** Fling input specifying the formal Datalog language.
 * 
 * @author Yossi Gil */
public class Java implements FluentLanguageAPI<Σ, V> {
  /** Set of terminals, i.e., method names of generated fluent API. */
  public enum Σ implements Terminal {
    name, number, variable, method
  }

  /** Set of non-terminals, i.e., abstract concepts of fluent API; these names
   * will be translated into names of classes of abstract syntax tree that Fling
   * generates, i.e., this AST will have class {@link Program} which will have a
   * list of {@link #Statement}, etc. */
  public enum V implements Variable {
    Program, Statement, Expression, InfixExpression, PrefixExpression, Declaration, Header, Member, Field, Constructor,
    Method, Initializer
  }

  @Override public Class<Σ> Σ() {
    return Σ.class;
  }

  @Override public Class<V> V() {
    return V.class;
  }

  /** Datalog's grammar in Backus-Naur form. */
  public static final il.ac.technion.cs.fling.EBNF bnf = bnf(). //
      start(Program). // This is the start symbol
      derive(Program).to(Quantifiers.oneOrMore(Declaration)). //
      derive(Declaration).to(Header, Quantifiers.oneOrMore(Member)). //
      specialize(Member).into(Field, Constructor, Method, Initializer).//
      build();

  @Override public il.ac.technion.cs.fling.EBNF BNF() {
    return bnf;
  }

  /** Prints the Datalog API/AST types/AST run-time compiler to corresponding
   * files. */
  public static void main(String[] args) throws IOException, FormatterException {
    /*
     * The {@link JavaMediator} responsible for compiling the Java Datalog API/AST
     * types/AST run-time compiler.
     */
    JavaMediator jm = new JavaMediator(//
        bnf, // use this BNF as language specification
        // Name of package in which output will reside
        "il.ac.technion.cs.fling.examples.generated",
        // Name of generated class
        "Datalog", Σ.class //
    );
    final Map<String, String> $ = new LinkedHashMap<>();
    $.put("Datalog", jm.apiClass);
    $.put("DatalogAST", jm.astClass);
    $.put("DatalogCompiler", jm.astCompilerClass);
    String PATH = "./src/test/java/il/ac/technion/cs/fling/examples/generated/";
    System.out.println("project path: " + PATH);
    final Path outputFolder = Paths.get(PATH);
    if (!Files.exists(outputFolder)) {
      Files.createDirectory(outputFolder);
      System.out.println("directory " + PATH + " created successfully");
    }
    final Formatter formatter = new Formatter();
    for (final Entry<String, String> file : $.entrySet()) {
      final Path filePath = Paths.get(PATH + file.getKey() + ".java");
      if (Files.exists(filePath))
        Files.delete(filePath);
      Files.write(filePath, Collections.singleton(formatter.formatSource(file.getValue())), StandardOpenOption.CREATE,
          StandardOpenOption.WRITE);
      System.out.println("file " + file.getKey() + ".java written successfully.");
    }
  }
}
