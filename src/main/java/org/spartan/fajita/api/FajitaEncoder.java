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
import org.spartan.fajita.api.rllp.Namer;
import org.spartan.fajita.api.rllp.RLLP;
import org.spartan.fajita.api.rllp.RLLPEncoder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

@SuppressWarnings("restriction") public class FajitaEncoder {
  Fajita fajita;
  List<RLLPEncoder> encoders;

  private FajitaEncoder(Fajita fajita) {
    this.fajita = fajita;
  }
  private String _encode() {
    encoders = getAllBNFs().stream() //
        .map(bnf -> new RLLPEncoder(new RLLP(bnf))) //
        .collect(Collectors.toList());
    TypeSpec mainType = TypeSpec.classBuilder(fajita.getApiName()) //
        .addModifiers(Modifier.PUBLIC) //
        .addType(addErrorType()) //
        .addMethods(encoders.stream() //
            .map(encoder -> getStaticMethods(encoder)) //
            .flatMap(methods -> methods.stream()) //
            .collect(Collectors.toList())) //
        .addTypes( //
            encoders.stream() //
                .map(encoder -> encoder.encode()) //
                .collect(Collectors.toList())) //
        .build(); //
    return JavaFile.builder(fajita.getPackagePath(), mainType).build().toString();
  }
  private Collection<BNF> getAllBNFs() {
    BNF main = new BNF(fajita.getVerbs(), fajita.getNonTerminals(), fajita.getRules(), fajita.getStartSymbols());
    List<BNF> $ = new ArrayList<>(Arrays.asList(main));
    for (NonTerminal nt : fajita.getNestedParameters())
      $.add(main.getSubBNF(nt));
    return $;
  }
  private static TypeSpec addErrorType() {
    return TypeSpec.classBuilder(Namer.errorTypeName).build();
  }
  private Collection<MethodSpec> getStaticMethods(RLLPEncoder encoder) {
    return encoder.rllp.getStartItems().stream()//
        .map(i -> generateStaticMethods(i, encoder))//
        .flatMap(methods -> methods.stream())//
        .collect(Collectors.toList());//
  }
  private Collection<MethodSpec> generateStaticMethods(Item i, RLLPEncoder encoder) {
    return encoder.rllp.analyzer.firstSetOf(i).stream().map(v -> encoder.methodOf(i, v))//
        .map(m -> {
          return MethodSpec.methodBuilder(m.name)//
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC) //
              .addParameters(m.parameters)//
              .addCode("return null;\n") //
              .returns(augmentFullClassPath(encoder.rllp.bnf.getApiName(), m.returnType))//
              .build();
        }) //
        .collect(Collectors.toSet());
  }
  private TypeName augmentFullClassPath(String bnfName, TypeName methodReturnType) {
    if (!(methodReturnType instanceof ParameterizedTypeName))
      return getFullClassName(bnfName, methodReturnType.toString());
    return augmentFullClassPath(bnfName, (ParameterizedTypeName) methodReturnType);
  }
  private TypeName augmentFullClassPath(String bnfName, ParameterizedTypeName parameterized) {
    ClassName classname = getFullClassName(bnfName, parameterized.rawType.simpleName());
    TypeName[] augmentedParams = parameterized.typeArguments.stream().map(typeArg -> augmentFullClassPath(bnfName, typeArg))//
        .collect(Collectors.toList()).toArray(new TypeName[] {});
    return ParameterizedTypeName.get(classname, augmentedParams);
  }
  private static ClassName getFullClassName(String nested, String classname) {
    return ClassName.get("", nested, classname);
  }
  public static String encode(Fajita fajita) {
    return new FajitaEncoder(fajita)._encode();
  }
}