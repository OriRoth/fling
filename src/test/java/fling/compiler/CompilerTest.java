package fling.compiler;

import static fling.automata.DPDA.dpda;
import static fling.compiler.CompilerTest.Q.q0;
import static fling.compiler.CompilerTest.Q.q1;
import static fling.compiler.CompilerTest.Q.q2;
import static fling.compiler.CompilerTest.Γ.γ0;
import static fling.compiler.CompilerTest.Γ.γ1;
import static fling.compiler.CompilerTest.Σ.c;
import static fling.compiler.CompilerTest.Σ.Ↄ;
import static fling.compiler.CompilerTest.Σ.ↄ;
import static fling.sententials.Alphabet.ε;
import static generated.BalancedParentheses.__;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

import fling.automata.DPDA;

public class CompilerTest {
  private static final String PATH = "./src/test/java/generated/";

  enum Q {
    q0, q1, q2
  }

  enum Σ {
    c, ↄ, Ↄ
  }

  enum Γ {
    γ0, γ1
  }

  public static DPDA<Q, Σ, Γ> dpda = dpda(Q.class, Σ.class, Γ.class) //
      .δ(q0, c, γ0, q1, γ0, γ1) //
      .δ(q1, c, γ1, q1, γ1, γ1) //
      .δ(q1, ↄ, γ1, q1) //
      .δ(q1, ε(), γ0, q0, γ0) //
      .δ(q1, Ↄ, γ1, q2) //
      .δ(q2, ε(), γ1, q2) //
      .δ(q2, ε(), γ0, q0, γ0) //
      .q0(q0) //
      .F(q0) //
      .γ0(γ0) //
      .go();

  public static void main(String[] args) throws IOException {
    Path outputFolder = Paths.get(PATH);
    if (!Files.exists(outputFolder)) {
      Files.createDirectory(outputFolder);
    }
    Path filePath = Paths.get(PATH + "BalancedParentheses.java");
    if (Files.exists(filePath))
      Files.delete(filePath);
    Files.write(filePath, Collections.singleton(new JavaAdapter<Q, Σ, Γ>("generated", "BalancedParentheses", "__", "$") //
        .printFluentAPI(new Compiler<>(dpda).compileFluentAPI())), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    System.out.println("File BalancedParentheses.java written successfully.");
  }
  public static void compilationTest() {
    __().c().ↄ().$();
    __().c().ↄ().ↄ();
    __().c().c().c().ↄ().ↄ();
    __().c().c().c().ↄ().ↄ().ↄ().$();
    __().c().c().c().ↄ().Ↄ().c().ↄ().$();
    __().c().c().c().ↄ().Ↄ().c();
    __().c().c().c().ↄ().Ↄ().c().Ↄ().$();
  }
}
