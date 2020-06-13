package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.SimpleArithmetic.V.E;
import static il.ac.technion.cs.fling.examples.languages.SimpleArithmetic.V.E_;
import static il.ac.technion.cs.fling.examples.languages.SimpleArithmetic.V.F;
import static il.ac.technion.cs.fling.examples.languages.SimpleArithmetic.V.T;
import static il.ac.technion.cs.fling.examples.languages.SimpleArithmetic.V.T_;
import static il.ac.technion.cs.fling.examples.languages.SimpleArithmetic.Σ.begin;
import static il.ac.technion.cs.fling.examples.languages.SimpleArithmetic.Σ.end;
import static il.ac.technion.cs.fling.examples.languages.SimpleArithmetic.Σ.i;
import static il.ac.technion.cs.fling.examples.languages.SimpleArithmetic.Σ.mult;
import static il.ac.technion.cs.fling.examples.languages.SimpleArithmetic.Σ.plus;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;
import static java.util.Collections.singleton;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

import il.ac.technion.cs.fling.adapters.JavaMediator;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.languages.SimpleArithmetic.V;
import il.ac.technion.cs.fling.examples.languages.SimpleArithmetic.Σ;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;

public class SimpleArithmetic implements FluentLanguageAPI<Σ, V> {
  // Terminal symbols:
  public enum Σ implements Terminal {
    plus, mult, begin, end, i
  }

  // Non-terminal symbols:
  public enum V implements Variable {
    E, E_, T, T_, F
  }

  @Override public Class<Σ> Σ() {
    return Σ.class;
  }

  @Override public Class<V> V() {
    return V.class;
  }

  @Override public il.ac.technion.cs.fling.EBNF BNF() {
    // @formatter:off
    return bnf(). // Start defining BNF
        start(E). // Declare the start symbol
        derive(E).to(T, E_). // E ::= T E'
        derive(E_).to(plus, T, E_).orNone(). // E' ::= + T E' | ε
        derive(T).to(F, T_). // T ::= F T'
        derive(T_).to(mult, F, T_).orNone(). // T' ::= * F T' | ε
        derive(F).to(begin, E, end).or(i.with(Integer.class)). // F ::= (E) | int
        build(); // Yield BNF;
    // @formatter:on
  }

  public static void main(final String[] args) //
      throws IOException, FormatterException {
    // @formatter:off
    final JavaMediator jm = new JavaMediator(new SimpleArithmetic().BNF(), // Grammar definition
        "il.ac.technion.cs.fling.examples.generated", // Output package name
        "SimpleArithmetic", // Output base class name
        Σ.class // Class of terminal symbols
    );
    // @formatter:on
    writeToFile("SimpleArithmetic", jm.apiClass);
    writeToFile("SimpleArithmeticAST", jm.astClass);
    writeToFile("SimpleArithmeticCompiler", jm.astCompilerClass);
  }

  private static void writeToFile(final String fileName, final String fileContent) //
      throws IOException, FormatterException {
    final String path = "./src/test/java/il/ac/technion/cs/fling/examples/generated/";
    final Formatter formatter = new Formatter();
    final Path filePath = Paths.get(path + fileName + ".java");
    Files.write(filePath, singleton(formatter.formatSource(fileContent)), //
        StandardOpenOption.CREATE, StandardOpenOption.WRITE);
  }
}
