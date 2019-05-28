package fling.examples.languages;

import static fling.examples.languages.SimpleArithmetic.V.*;
import static fling.examples.languages.SimpleArithmetic.Σ.*;
import static fling.grammars.api.BNFAPI.bnf;

import java.io.IOException;
import java.nio.file.*;
import static java.util.Collections.singleton;

import com.google.googlejavaformat.java.*;

import fling.*;
import fling.BNF;
import fling.adapters.JavaMediator;

public class SimpleArithmetic {
  // Terminal symbols:
  public enum Σ implements Terminal {
    plus, mult, begin, end, i
  }

  // Non-terminal symbols:
  public enum V implements Variable {
    E, E_, T, T_, F
  }

  public static final BNF bnf = bnf(). // Start defining BNF
      start(E). // Declare the start symbol
      derive(E).to(T, E_). // E ::= T E'
      derive(E_).to(plus, T, E_).orNone(). // E' ::= + T E' | ε
      derive(T).to(F, T_). // T ::= F T'
      derive(T_).to(mult, F, T_).orNone(). // T' ::= * F T' | ε
      derive(F).to(begin, E, end).or(i.with(Integer.class)). // F ::= (E) | int
      build(); // Yield BNF

  public static void main(String[] args) //
      throws IOException, FormatterException {
    JavaMediator jm = new JavaMediator(bnf, // Grammar definition
        "fling.examples.generated", // Output package name
        "SimpleArithmetic", // Output base class name
        Σ.class // Class of terminal symbols
    );
    writeToFile("SimpleArithmetic", jm.apiClass);
    writeToFile("SimpleArithmeticAST", jm.astClass);
    writeToFile("SimpleArithmeticCompiler", jm.astCompilerClass);
  }
  private static void writeToFile(String fileName, String fileContent) //
      throws IOException, FormatterException {
    String path = "./src/test/java/fling/examples/generated/";
    final Formatter formatter = new Formatter();
    final Path filePath = Paths.get(path + fileName + ".java");
    Files.write(filePath, singleton(formatter.formatSource(fileContent)), //
        StandardOpenOption.CREATE, StandardOpenOption.WRITE);
  }
}
