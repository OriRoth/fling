package org.spartan.fajita.api.ll;

import static org.spartan.fajita.api.ll.GeneratorStrings.basicMethod;
import static org.spartan.fajita.api.ll.GeneratorStrings.containerClass;
import static org.spartan.fajita.api.ll.GeneratorStrings.tailParameter;
import static org.spartan.fajita.api.ll.GeneratorStrings.type;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.jgrapht.util.ModifiableInteger;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Verb;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import com.squareup.javapoet.TypeVariableName;

public class LLRecognizerGenerator {
  public static JavaFile generate(BNF bnf) {
    LLRecognizer recognizer = new LLRecognizer(bnf);
    List<TypeSpec> configurationTypes = new ArrayList<>();
    // Generate verb types
    for (Verb v : bnf.getVerbs())
      configurationTypes.add(generateVerbConfiguration(v));
    for (NonTerminal nt : bnf.getNonTerminals())
      if (!nt.equals(SpecialSymbols.augmentedStartSymbol))
        configurationTypes.add(generateNTConfiguration(recognizer, nt));
    TypeSpec.Builder container = TypeSpec.classBuilder(containerClass).addTypes(configurationTypes);
    ParameterizedTypeName startType = ParameterizedTypeName.get(type("C_"+bnf.getStartSymbols().get(0).name()),type("C_$"));
    container.addField(FieldSpec.builder(startType, "start",Modifier.PUBLIC,Modifier.STATIC).build());
    return JavaFile.builder("", container.build()).build();
  }
  private static TypeSpec generateNTConfiguration(LLRecognizer recognizer, NonTerminal nt) {
    TypeSpec.Builder $ = TypeSpec.classBuilder("C_" + nt.name());
    $.addTypeVariable(TypeVariableName.get(tailParameter));
    for (Verb v : recognizer.bnf.getVerbs()) {
      if (recognizer.isError(nt, v))
        continue;
      MethodSpec.Builder method = basicMethod(v.name())
          .returns(pushToStack(TypeVariableName.get(tailParameter), recognizer.getPush(nt, v)));
      $.addMethod(method.build());
    }
    return $.build();
  }
  private static TypeSpec generateVerbConfiguration(final Verb v) {
    if (v.equals(SpecialSymbols.$))
      return generate$Configuration();
    TypeSpec.Builder $ = TypeSpec.classBuilder("C_" + v.name());
    // type parameters
    $.addTypeVariable(
        // TypeVariableName.get(tailParameter,
        // ParameterizedTypeName.get(type(stackClass),
        // WildcardTypeName.subtypeOf(Object.class)))
        TypeVariableName.get(tailParameter));
    // The matching method
    MethodSpec.Builder matcher = basicMethod(v.name()).returns(type(tailParameter));
    $.addMethod(matcher.build());
    return $.build();
  }
  private static TypeSpec generate$Configuration() {
    TypeSpec.Builder $ = TypeSpec.classBuilder("C_$");
    // The matching method
    MethodSpec.Builder matcher = MethodSpec.methodBuilder("$").addCode("return;\n");
    $.addMethod(matcher.build());
    return $.build();
  }
  private static TypeName pushToStack(TypeName stack, List<Symbol> toPush) {
    TypeName current = stack;
    for (Symbol s : toPush) {
      current = ParameterizedTypeName.get(type("C_" + s.name()), current);
    }
    return current;
  }
}
