package org.spartan.fajita.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.rllp.Item;
import org.spartan.fajita.api.rllp.RLLP;
import org.spartan.fajita.api.rllp.generation.NamesCache;
import org.spartan.fajita.api.rllp.generation.RLLPEncoder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

@SuppressWarnings("restriction") public class FajitaEncoder {
  public static String encode(Fajita fajita) {
    NamesCache.fajitaApiName = fajita.getApiName();
    List<RLLPEncoder> encoders = getAllBNFs(fajita).stream() //
        .map(bnf -> new RLLPEncoder(new RLLP(bnf))) //
        .collect(Collectors.toList());
    return TypeSpec.classBuilder(fajita.getApiName()) //
        .addModifiers(Modifier.PUBLIC) //
        .addType(addErrorType()) //
        .addTypes( //
            encoders.stream() //
                .map(encoder -> encoder.encode()) //
                .collect(Collectors.toList())) //
        .addMethods(encoders.stream() //
            .map(encoder -> getStaticMethods(encoder)) //
            .flatMap(methods -> methods.stream()) //
            .collect(Collectors.toList())) //
        .build().toString(); //
  }
  private static Collection<BNF> getAllBNFs(Fajita fajita) {
    BNF main = new BNF(fajita.getVerbs(), fajita.getNonTerminals(), fajita.getRules(), fajita.getStartSymbols());
    List<BNF> $ = new ArrayList<>(Arrays.asList(main));
    for (NonTerminal nt : fajita.getNestedParameters())
      $.add(main.getSubBNF(nt));
    return $;
  }
  private static TypeSpec addErrorType() {
    return TypeSpec.classBuilder(NamesCache.errorTypeName).build();
  }
  private static Collection<MethodSpec> getStaticMethods(RLLPEncoder encoder) {
    return encoder.getRLLP().getStartItems().stream()//
        .map(i -> generateStaticMethods(i, encoder))//
        .flatMap(methods -> methods.stream())//
        .collect(Collectors.toList());//
  }
  private static Collection<MethodSpec> generateStaticMethods(Item i, RLLPEncoder encoder) {
    return encoder.getRLLP().analyzer.firstSetOf(i).stream().map(v -> encoder.methodOf(i, v))//
        .map(m -> MethodSpec.methodBuilder(m.name)//
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC) //
            .addParameters(augmentParameter(m.parameters))//
            .addCode("return null;\n") //
            .returns(ClassName.get(encoder.getRLLP().bnf.getApiName(), m.returnType.toString())).build()) //
        .collect(Collectors.toList());
  }
  private static Iterable<ParameterSpec> augmentParameter(Iterable<ParameterSpec> parameters){
    return parameters;
  }
}
