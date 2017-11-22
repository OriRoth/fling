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
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.rllp.EncoderUtils;
import org.spartan.fajita.api.rllp.Item;
import org.spartan.fajita.api.rllp.RLLP;
import org.spartan.fajita.api.rllp.RLLPEncoder;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

public class FajitaEncoder {
  Fajita fajita;
  List<RLLPEncoder> encoders;
  private EncoderUtils namer;

  private FajitaEncoder(Fajita fajita) {
    this.fajita = fajita;
    this.namer = new EncoderUtils(fajita);
  }
  private Map<String, String> _encode() {
    encoders = getAllBNFs().stream() //
        .map(bnf -> new RLLPEncoder(new RLLP(bnf), namer)) //
        .collect(Collectors.toList());
    Collection<TypeSpec> types = new ArrayList<>();
    types.add(getUtilitiesType());
    types.addAll(encoders.stream().map(enc -> enc.encode()).collect(Collectors.toList()));
    HashMap<String, String> files = new HashMap<>();
    for (TypeSpec t : types) {
      String content = JavaFile.builder(fajita.getPackagePath(), t).build().toString();
      files.put(t.name + ".java", content);
    }
    return files;
  }
  private TypeSpec getUtilitiesType() {
    String comment = "To use this fluent API you should import:\n";
    for (RLLPEncoder e : encoders) {
      comment += "<li>";
      if (e.getApiName().equals(fajita.getApiName()))
        comment += "import static " + fajita.getPackagePath() + "." + e.getApiName() + ".*;";
      else
        comment += "import " + fajita.getPackagePath() + "." + e.getApiName() + ";";
      comment += "</li>\n";
    }
    return TypeSpec.classBuilder(EncoderUtils.utilClass)//
        .addModifiers(Modifier.PUBLIC)//
        .addType(getASTType())//
        .addJavadoc(comment)//
        .build();
  }
  private Collection<BNF> getAllBNFs() {
    BNF main = new BNF(fajita.getVerbs(), fajita.getNonTerminals(), fajita.getRules(), fajita.getStartSymbols(),
        fajita.getApiName());
    List<BNF> $ = new ArrayList<>(Arrays.asList(main));
    for (NonTerminal nt : fajita.getNestedParameters())
      $.add(main.getSubBNF(nt));
    return $;
  }
  private TypeSpec getASTType() {
    return TypeSpec.interfaceBuilder(namer.returnTypeOf$())//
        .addModifiers(Modifier.PUBLIC) //
        .build();
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
              .addCode("return null;\n") //
              .returns(m.returnType).build();
        }).collect(Collectors.toSet());
  }
  public static Map<String, String> encode(Fajita fajita) {
    return new FajitaEncoder(fajita)._encode();
  }
}