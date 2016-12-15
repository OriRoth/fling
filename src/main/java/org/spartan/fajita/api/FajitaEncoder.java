package org.spartan.fajita.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.rllp.RLLP;
import org.spartan.fajita.api.rllp.generation.RLLPEncoder;

import com.squareup.javapoet.TypeSpec;

@SuppressWarnings("restriction") public class FajitaEncoder {
  public static String encode(Fajita fajita) {
    List<RLLPEncoder> encoders = getAllBNFs(fajita).stream()//
        .map(bnf -> new RLLPEncoder(new RLLP(bnf)))//
        .collect(Collectors.toList());
    return TypeSpec.classBuilder(fajita.getApiName())//
        .addModifiers(Modifier.PUBLIC) //
        .addTypes(//
            encoders.stream()//
                .map(encoder -> encoder.encode())//
                .collect(Collectors.toList()))//
        .addMethods(encoders.stream()//
            .map(encoder -> encoder.getStaticMethods()) //
            .flatMap(methods -> methods.stream())//
            .collect(Collectors.toList()))//
        .build().toString();//
  }
  private static Collection<BNF> getAllBNFs(Fajita fajita) {
    BNF main = new BNF(fajita.getVerbs(), fajita.getNonTerminals(), fajita.getRules(), fajita.getStartSymbols(),
        "__Main__" + fajita.getApiName());
    List<BNF> $ = new ArrayList<>(Arrays.asList(main));
    for (NonTerminal nt : fajita.getNestedParameters())
      $.add(main.getSubBNF(nt));
    return $;
  }
}
