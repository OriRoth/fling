package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.Datalog.V.AdditionalClause;
import static il.ac.technion.cs.fling.examples.languages.Datalog.V.Bodyless;
import static il.ac.technion.cs.fling.examples.languages.Datalog.V.Fact;
import static il.ac.technion.cs.fling.examples.languages.Datalog.V.FirstClause;
import static il.ac.technion.cs.fling.examples.languages.Datalog.V.Program;
import static il.ac.technion.cs.fling.examples.languages.Datalog.V.Query;
import static il.ac.technion.cs.fling.examples.languages.Datalog.V.Rule;
import static il.ac.technion.cs.fling.examples.languages.Datalog.V.RuleBody;
import static il.ac.technion.cs.fling.examples.languages.Datalog.V.RuleHead;
import static il.ac.technion.cs.fling.examples.languages.Datalog.V.Statement;
import static il.ac.technion.cs.fling.examples.languages.Datalog.V.Term;
import static il.ac.technion.cs.fling.examples.languages.Datalog.V.WithBody;
import static il.ac.technion.cs.fling.examples.languages.Datalog.Σ.always;
import static il.ac.technion.cs.fling.examples.languages.Datalog.Σ.and;
import static il.ac.technion.cs.fling.examples.languages.Datalog.Σ.fact;
import static il.ac.technion.cs.fling.examples.languages.Datalog.Σ.infer;
import static il.ac.technion.cs.fling.examples.languages.Datalog.Σ.l;
import static il.ac.technion.cs.fling.examples.languages.Datalog.Σ.of;
import static il.ac.technion.cs.fling.examples.languages.Datalog.Σ.query;
import static il.ac.technion.cs.fling.examples.languages.Datalog.Σ.v;
import static il.ac.technion.cs.fling.examples.languages.Datalog.Σ.when;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;
import static il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers.noneOrMore;
import static il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers.oneOrMore;

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

import il.ac.technion.cs.fling.adapters.JavaMediator;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.generated.DatalogAST.Program;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;

/** Fling input specifying the formal Datalog language.
 * 
 * @author Yossi Gil */
public class Datalog implements FluentLanguageAPI<Datalog.Σ, Datalog.V> {
  /** Set of terminals, i.e., method names of generated fluent API. */
  public enum Σ implements Terminal {
    infer, fact, query, of, and, when, always, v, l
  }

  /** Set of non-terminals, i.e., abstract concepts of fluent API; these names
   * will be translated into names of classes of abstract syntax tree that Fling
   * generates, i.e., this AST will have class {@link Program} which will have a
   * list of {@link #Statement}, etc. */
  public enum V implements Variable {
    Program, Statement, Rule, Query, Fact, Bodyless, WithBody, //
    RuleHead, RuleBody, FirstClause, AdditionalClause, Term
  }

  @Override public Class<Σ> Σ() {
    return Σ.class;
  }

  @Override public Class<V> V() {
    return V.class;
  }

  /** Short name of {@link String}.class, used to specify the type of parameters
   * to fluent API methods in grammar specification. */
  private static final Class<String> S = String.class;
  /** Datalog's grammar in Backus-Naur form. */
  public static final il.ac.technion.cs.fling.EBNF bnf = bnf(). //
      start(Program). // This is the start symbol
      derive(Program).to(oneOrMore(Statement)). // Program ::= Statement*
      specialize(Statement).into(Fact, Rule, Query).
      /*
       * Defines the rule Statement ::= Fact |Rule | Query, but also defines that
       * classes {@link Fact}, {@link Rule} and {@link Query} extend class {@link
       * Statement}
       */
      derive(Fact).to(fact.with(S), of.many(S)). // Fact ::= fact(S*) of(S*)
      derive(Query).to(query.with(S), of.many(Term)). //
      specialize(Rule).into(Bodyless, WithBody). //
      derive(Bodyless).to(always.with(S), of.many(Term)). //
      derive(WithBody).to(RuleHead, RuleBody). //
      derive(RuleHead).to(infer.with(S), of.many(Term)). //
      derive(RuleBody).to(FirstClause, noneOrMore(AdditionalClause)). //
      derive(FirstClause).to(when.with(S), of.many(Term)). //
      derive(AdditionalClause).to(and.with(S), of.many(Term)). //
      derive(Term).to(l.with(S)).or(v.with(S)). //
      build();

  @Override public il.ac.technion.cs.fling.EBNF BNF() {
    return bnf;
  }

  /** Prints the Datalog API/AST types/AST run-time compiler to corresponding
   * files. */
  public static void main(final String[] args) throws IOException, FormatterException {
    /*
     * The {@link JavaMediator} responsible for compiling the Java Datalog API/AST
     * types/AST run-time compiler.
     */
    final JavaMediator jm = new JavaMediator(//
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
    final String PATH = "./src/test/java/il/ac/technion/cs/fling/examples/generated/";
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
