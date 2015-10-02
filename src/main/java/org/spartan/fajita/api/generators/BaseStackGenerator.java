package org.spartan.fajita.api.generators;

import static org.spartan.fajita.api.generators.GeneratorsUtils.suppressWarningAnnot;
import static org.spartan.fajita.api.generators.GeneratorsUtils.type;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.BASE_STACK;

import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

public class BaseStackGenerator {
  public static TypeSpec generate() {
    return TypeSpec.interfaceBuilder(BASE_STACK.typename) //
        .addTypeVariable(TypeVariableName.get("Tail", type(BASE_STACK))) //
        .addAnnotation(suppressWarningAnnot("rawtypes", "unused"))//
        .build();
  }
}