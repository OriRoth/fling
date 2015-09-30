package org.spartan.fajita.api.generators;

import static org.spartan.fajita.api.generators.GeneratorsUtils.BASE_STACK_CLASSNAME;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

public class BaseStackGenerator {
  public static TypeSpec generate() {
    return TypeSpec.interfaceBuilder(BASE_STACK_CLASSNAME)
        .addTypeVariable(TypeVariableName.get("Tail", ClassName.get("", BASE_STACK_CLASSNAME)))
        .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "{\"rawtypes\",\"unused\"}").build())
        .build();
  }
}