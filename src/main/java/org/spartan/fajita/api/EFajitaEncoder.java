package org.spartan.fajita.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.JamoosClassesRenderer;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.rllp.ERLLPEncoder;
import org.spartan.fajita.api.rllp.EEncoderUtils;
import org.spartan.fajita.api.rllp.Item;
import org.spartan.fajita.api.rllp.RLLP;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

@SuppressWarnings("restriction") public class EFajitaEncoder {
  EFajita fajita;
  List<ERLLPEncoder> encoders;
  private EEncoderUtils namer;

  public EFajitaEncoder(EFajita fajita) {
    this.fajita = fajita;
    this.namer = new EEncoderUtils();
  }
  private Map<String, String> _encode() {
    encoders = getAllBNFs().stream() //
        .map(bnf -> new ERLLPEncoder(new RLLP(bnf), namer, fajita.terminals, fajita.provider)) //
        .collect(Collectors.toList());
    Collection<TypeSpec> types = new ArrayList<>();
    types.addAll(encoders.stream().map(enc -> enc.encode()).collect(Collectors.toList()));
    HashMap<String, String> files = new HashMap<>();
    for (TypeSpec t : types) {
      String content = JavaFile.builder(fajita.getPackagePath(), t).build().toString();
      files.put(t.name + ".java", content);
    }
    JamoosClassesRenderer jcr = JamoosClassesRenderer.render(fajita.bnf(), fajita.packagePath);
    files.put(jcr.topClassName + ".java", jcr.topClass);
    return files;
  }
  private Collection<BNF> getAllBNFs() {
    BNF main = new BNF(fajita.getVerbs(), fajita.getNonTerminals(), fajita.getRules(), fajita.getStartSymbols(),
        fajita.getApiName());
    List<BNF> $ = new ArrayList<>(Arrays.asList(main));
    for (NonTerminal nt : fajita.getNestedParameters())
      $.add(main.getSubBNF(nt));
    return $;
  }
  public static Collection<MethodSpec> getStaticMethods(ERLLPEncoder encoder) {
    return encoder.rllp.getStartItems().stream()//
        .map(i -> generateStaticMethods(i, encoder))//
        .flatMap(methods -> methods.stream())//
        .collect(Collectors.toList());//
  }
  public static Collection<MethodSpec> generateStaticMethods(Item i, ERLLPEncoder encoder) {
    return encoder.rllp.analyzer.firstSetOf(i).stream().map(v -> encoder.methodOf(i, v))//
        .map(m -> {
          return MethodSpec.methodBuilder(m.name)//
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC) //
              .addParameters(m.parameters)//
              .varargs(m.varargs) //
              .addCode(
                  ERLLPEncoder.$$$nameEscaped + " " + ERLLPEncoder.$$$nameEscaped + " = new " + ERLLPEncoder.$$$nameEscaped + "();") //
              .addCode(ERLLPEncoder.$$$nameEscaped + ".recordTerminal(" + encoder.getTerminalName(m.name) + ");") //
              .addCode("return " + ERLLPEncoder.$$$nameEscaped + ";") //
              .returns(m.returnType).build();
        }).collect(Collectors.toSet());
  }
  public static Map<String, String> encode(EFajita fajita) {
    return new EFajitaEncoder(fajita)._encode();
  }
}