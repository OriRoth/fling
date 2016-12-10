package org.spartan.fajita.api.examples.json;

import static org.spartan.fajita.api.examples.json.JsonObject.NT.*;
import static org.spartan.fajita.api.examples.json.JsonObject.Term.*;

import java.io.IOException;

import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class JsonObject {
  public static void expressionBuilder() {
    // showASTs();
  }

  static enum Term implements Terminal {
    startObject, endObject, name, //
    to, toNull, toObject, toArray, //
    startArray, endArray, //
    add, addObject, addArray;
  }

  static enum NT implements NonTerminal {
    START, OBJECT, NEXT, ADD, //
    TO_TYPE, TO, TO_NULL, TO_OBJECT, TO_ARRAY, //
    ARRAY, ELEMENTS, //
    ELEMENT_TYPE, ELEMENT, OBJECT_ELEMENT, ARRAY_ELEMENT;
  }

  /**
   * We would like to have overloaded functions, e.g. "to()" </br>
   * we would like to do :
   * <li><code>name("foo").to("bar")</code></li>
   * <li><code>name("foo").to(5)</code></li>
   * <li><code>name("foo").to(true)</code></li> instead of
   * <li><code>name("foo").toString("bar")</code></li>
   * <li><code>name("foo").toInt(5)</code></li>
   * <li><code>name("foo").toBool(true)</code></li> for that we need a syntax to
   * desribe overloading in the Enums. </br>
   * for example we can add an optional constructor with (group : integer)
   * parameter, where all Terms with the same group will be mapped to the name
   * of the smallest ordinal but with the declared class
   * 
   * @return
   */
  public static BNF buildBNF() {
    BNF b = new BNFBuilder(Term.class, NT.class) //
        .start(START) //
        .derive(START).to(OBJECT) //
        .derive(OBJECT).to(startObject).and(NEXT).and(endObject) //
        .derive(NEXT).to(ADD).and(NEXT).orNone()//
        .derive(ADD).to(name).and(TO_TYPE)//
        .derive(TO_TYPE).to(TO).or(TO_NULL).or(TO_ARRAY).or(TO_OBJECT)//
        .derive(TO).to(to)//
        .derive(TO_NULL).to(toNull) //
        .derive(TO_OBJECT).to(toObject).and(OBJECT) //
        .derive(TO_ARRAY).to(toArray).and(ARRAY) //
        .derive(ARRAY).to(startArray).and(ELEMENTS).and(endArray) //
        .derive(ELEMENTS).to(ELEMENT_TYPE).and(ELEMENTS).orNone() //
        .derive(ELEMENT_TYPE).to(ELEMENT).or(ARRAY_ELEMENT).or(OBJECT_ELEMENT) //
        .derive(ELEMENT).to(add) //
        .derive(OBJECT_ELEMENT).to(addObject).and(OBJECT) //
        .derive(ARRAY_ELEMENT).to(addArray).and(ARRAY) //
        .go();
    return b;
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(buildBNF());
  }
}
