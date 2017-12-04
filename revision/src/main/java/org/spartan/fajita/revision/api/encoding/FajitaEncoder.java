package org.spartan.fajita.revision.api.encoding;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.ast.encoding.JamoosClassesRenderer;
import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.parser.rll.Item;
import org.spartan.fajita.revision.parser.rll.RLLP;
import org.spartan.fajita.revision.symbols.NonTerminal;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

public class FajitaEncoder {
  Fajita fajita;
  List<RLLPEncoder> encoders;
  private EncoderUtils namer;

  public FajitaEncoder(Fajita fajita) {
    this.fajita = fajita;
    this.namer = new EncoderUtils();
  }
  private Map<String, String> _encode() {
    encoders = getAllBNFs().stream() //
        .map(bnf -> new RLLPEncoder(new RLLP(bnf), namer, fajita.terminals, fajita.provider, bnf.isSubBNF)) //
        .collect(Collectors.toList());
    Collection<TypeSpec> types = new ArrayList<>();
    types.addAll(encoders.stream().map(enc -> enc.encode()).collect(Collectors.toList()));
    HashMap<String, String> files = new HashMap<>();
    for (TypeSpec t : types) {
      String content = JavaFile.builder(fajita.packagePath, t).build().toString();
      files.put(t.name + ".java", content);
    }
    JamoosClassesRenderer jcr = JamoosClassesRenderer.render(fajita.ebnf(), fajita.packagePath);
    files.put(jcr.topClassName + ".java", jcr.topClass);
    return files;
  }
  private Collection<BNF> getAllBNFs() {
    BNF main = fajita.bnf();
    List<BNF> $ = new ArrayList<>(Arrays.asList(main));
    for (NonTerminal nt : main.nestedNonTerminals)
      $.add(main.getSubBNF(nt));
    return $;
  }
  public static Collection<MethodSpec> getStaticMethods(RLLPEncoder encoder) {
    return encoder.rllp.getStartItems().stream()//
        .map(i -> generateStaticMethods(i, encoder))//
        .flatMap(methods -> methods.stream())//
        .collect(Collectors.toList());//
  }
  public static Collection<MethodSpec> generateStaticMethods(Item i, RLLPEncoder encoder) {
    return encoder.rllp.analyzer.firstSetOf(i).stream().map(v -> encoder.methodOf(i, v))//
        .map(m -> {
          return MethodSpec.methodBuilder(m.name)//
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC) //
              .addParameters(m.parameters)//
              .varargs(m.varargs) //
              .addCode(
                  RLLPEncoder.$$$nameEscaped + " " + RLLPEncoder.$$$nameEscaped + " = new " + RLLPEncoder.$$$nameEscaped + "();") //
              .addCode(RLLPEncoder.$$$nameEscaped + ".recordTerminal(" + encoder.getTerminalName(m.name)
                  + getArguments(m.parameters) + ");") //
              .addCode("return " + RLLPEncoder.$$$nameEscaped + ";") //
              .returns(m.returnType).build();
        }).collect(Collectors.toSet());
  }
  public static Map<String, String> encode(Fajita fajita) {
    return new FajitaEncoder(fajita)._encode();
  }
  public static String getArguments(List<ParameterSpec> ps) {
    if (ps.isEmpty())
      return "";
    return "," + String.join(",", ps.stream().map(x -> x.name).collect(toList()));
  }
}