package org.spartan.fajita.api.ll;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

public class GeneratorStrings {
  public static final String stackClass = "Stack";
  public static final String tailParameter = "Tail";
  public static final String containerClass= "Container";
  
  public static ClassName type(final String classname) {
    return ClassName.get("", classname);
  }
  
  public static MethodSpec.Builder basicMethod(String name){
    return MethodSpec.methodBuilder(name).addCode("return null;\n");
  }
}
