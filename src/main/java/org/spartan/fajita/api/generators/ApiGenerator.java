package org.spartan.fajita.api.generators;

import static org.spartan.fajita.api.generators.GeneratorsUtils.REDUCES_FIELD;
import static org.spartan.fajita.api.generators.GeneratorsUtils.REDUCES_TYPE;
import static org.spartan.fajita.api.generators.GeneratorsUtils.STACK_FIELD;
import static org.spartan.fajita.api.generators.GeneratorsUtils.STACK_TYPE_PARAMETER;
import static org.spartan.fajita.api.generators.GeneratorsUtils.commaSeperatedArgs;
import static org.spartan.fajita.api.generators.GeneratorsUtils.generateBaseState;
import static org.spartan.fajita.api.generators.GeneratorsUtils.generateEmptyStack;
import static org.spartan.fajita.api.generators.GeneratorsUtils.generateErrorState;
import static org.spartan.fajita.api.generators.GeneratorsUtils.generateIStack;
import static org.spartan.fajita.api.generators.GeneratorsUtils.generateParseErrorException;
import static org.spartan.fajita.api.generators.GeneratorsUtils.type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.examples.ASTViewer;
import org.spartan.fajita.api.generators.GeneratorsUtils.Classname;
import org.spartan.fajita.api.generators.typeArguments.TypeArgumentManager;
import org.spartan.fajita.api.parser.old.LRParser;
import org.spartan.fajita.api.parser.old.State;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

public class ApiGenerator {
  private final LRParser parser;
  private final TypeArgumentManager tam;
  @SuppressWarnings("unused") private BNF bnf;

  private ApiGenerator(BNF bnf) {
    this.bnf = bnf;
    parser = new LRParser(bnf);
    tam = new TypeArgumentManager(parser);
  }
  private JavaFile generate() {
    Builder states = TypeSpec.classBuilder(BNF.getApiName());
    states.addModifiers(Modifier.PUBLIC);
    states.addType(generateIStack());
    states.addType(generateEmptyStack());
    states.addType(generateErrorState(tam));
    states.addType(generateParseErrorException());
    states.addType(generateBaseState(tam));
    final List<TypeSpec> types = new ArrayList<>();
    parser.getStates().forEach(s -> {
      types.add(generateClass(tam, s, s.name));
    });
    states.addTypes(types);
    return JavaFile.builder("", states.build()).build();
  }
  private static TypeSpec generateClass(final TypeArgumentManager tam, final State s, final String name) {
    Builder $ = TypeSpec.classBuilder(name) //
        .addModifiers(Modifier.STATIC, Modifier.PUBLIC) //
        .addTypeVariables(tam.getFormalParameters(s)) //
        .superclass(tam.getInstantiatedBaseState(s));
    // adds reduces
    $.addFields(serializeReducingRules(s));
    // adds constructor
    if (s.index == 0)
      $.addMethod(MethodSpec.constructorBuilder()//
          .addStatement("super(new EmptyStack(),new $T<>())", ArrayList.class)//
          .addModifiers(Modifier.PUBLIC).build());
    else
      $.addMethod(MethodSpec.constructorBuilder()//
          .addParameter(type(STACK_TYPE_PARAMETER), STACK_FIELD)//
          .addParameter(REDUCES_TYPE, REDUCES_FIELD)//
          .addStatement("super($N,$N)", STACK_FIELD, REDUCES_FIELD).build());
    // adds methods
    ParameterizedTypeName baseState = tam.getInstantiatedBaseState(s);
    for (int i = 0; i < tam.symbols.size(); i++) {
      Symbol symb = tam.symbols.get(i);
      TypeName type = baseState.typeArguments.get(i + 1);
      if (type.toString().equals(Classname.ERROR_STATE.typename))
        continue;
      $.addMethod(getMethodSpec(symb, type, s));
    }
    if (s.getItems().stream().anyMatch(i -> i.readyToReduce() && i.lookahead.equals(SpecialSymbols.$)))
      $.addMethod(getMethodSpec(SpecialSymbols.$, TypeName.get(Object.class), s));
    return $.build();
  }
  private static Iterable<FieldSpec> serializeReducingRules(State s) {
    final List<DerivationRule> reduces = s.getItems().stream() //
        .filter(i -> i.readyToReduce() && !i.rule.lhs.equals(SpecialSymbols.augmentedStartSymbol))//
        .map(i -> i.rule).distinct()
        .collect(Collectors.toList());
    final List<FieldSpec> $ = new ArrayList<>();
    for (DerivationRule rule : reduces)
      $.add(FieldSpec.builder(String.class, "rule" + rule.getIndex(), Modifier.FINAL, Modifier.PRIVATE)
          .initializer("$S", rule.serialize()).build());
    return $;
  }
  @SuppressWarnings("boxing") private static MethodSpec getMethodSpec(Symbol symb, TypeName type, State s) {
    final boolean is$ = symb.equals(SpecialSymbols.$);
    com.squareup.javapoet.MethodSpec.Builder $ = MethodSpec.methodBuilder(symb.name()).addAnnotation(Override.class)
        .addModifiers(symb.isVerb() || is$ ? Modifier.PUBLIC : Modifier.PROTECTED).returns(type);
    if (symb.isVerb())
      for (int i = 0; i < ((Verb) symb).type.classes.size(); i++)
        $.addParameter(((Verb) symb).type.classes.get(i), "arg" + i);
    if (s.isLegalTransition(symb)) {
      if (is$) {
        $.addStatement("$T.showASTs($T.generateAST($N))", ASTViewer.class, Main.class, REDUCES_FIELD);
        $.addStatement("System.out.println(\"finished\")");
        $.addStatement("return new Object()");
      } else
        $.addStatement("return new " + (type.getClass().equals(ParameterizedTypeName.class)
            ? ((ParameterizedTypeName) type).rawType + "<>" : type.toString()) + "(this,$N)", REDUCES_FIELD);
    } else { // reduce
      DerivationRule reduce = s.getItems().stream().filter(item -> item.readyToReduce() && item.isLegalReduce((Terminal) symb))
          .findAny().get().rule;
      $.addStatement("$N.add($T.deserialize(rule$L))", REDUCES_FIELD, DerivationRule.class, reduce.getIndex());
      String pops = "pop()";
      for (int i = 1; i < reduce.getChildren().size(); i++)
        pops += ".pop()";
      String cast = is$ ? "" : "(" + type + ")";
      if (!is$)
        $.addAnnotation(GeneratorsUtils.suppressWarningAnnot("unchecked"));
      $.addStatement("return " + cast + pops + ".$N().$N($L)", reduce.lhs.name(), symb.name(),
          commaSeperatedArgs(((Verb) symb).type.classes.size()));
    }
    return $.build();
  }
  public static JavaFile generate(BNF bnf) {
    return new ApiGenerator(bnf).generate();
  }
}
