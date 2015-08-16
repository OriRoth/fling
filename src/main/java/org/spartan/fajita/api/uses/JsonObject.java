package org.spartan.fajita.api.uses;

import static org.spartan.fajita.api.uses.JsonObject.NT.*;
import static org.spartan.fajita.api.uses.JsonObject.Term.*;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;

public class JsonObject {
    public static void expressionBuilder() {

	// showASTs();
    }

    static enum Term implements Terminal {
	startObject, endObject, name(String.class), //
	to(int.class), toNull, toObject, toArray, //
	startArray, endArray, //
	add(int.class), addObject, addArray;

	private final Type type;

	private Term(final Class<?> cls1, final Class<?>... type) {
	    this.type = new Type(cls1, type);
	}

	private Term() {
	    type = new Type();
	}

	@Override
	public Type type() {
	    return type;
	}

    }

    static enum NT implements NonTerminal {
	START, OBJECT, NEXT, NEXT_ADD, LAST_ADD, ADD, //
	TO_TYPE, TO, TO_NULL, TO_OBJECT, TO_ARRAY, //
	ARRAY, ELEMENTS, LAST_ELEMENT, NEXT_ELEMENT, //
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
     * <li><code>name("foo").toBool(true)</code></li>
     * 
     * for that we need a syntax to desribe overloading in the Enums. </br>
     * for example we can add an optional constructor with (group : integer)
     * parameter, where all Terms with the same group will be mapped to the name
     * of the smallest ordinal but with the declared class
     */
    public static void buildBNF() {
	BNF b = new BNFBuilder(Term.class, NT.class) //
		.startConfig() //
		.setApiNameTo("Json Object Builder") //
		.setStartSymbols(START) //
		.overload(to).with(boolean.class)//
		.overload(to).with(double.class) //
		.overload(to).with(String.class) //
		.overload(add).with(boolean.class)//
		.overload(add).with(double.class) //
		.overload(add).with(String.class) //
		.endConfig() //
		.derive(START).to(OBJECT) //
		.derive(OBJECT).to(startObject).and(NEXT).and(endObject) //
		.derive(NEXT).to(NEXT_ADD).or(LAST_ADD)//
		.derive(NEXT_ADD).to(ADD).and(NEXT) //
		.derive(LAST_ADD).to(ADD) //
		.derive(ADD).to(name).and(TO_TYPE)//
		.derive(TO_TYPE).to(TO).or(TO_NULL).or(TO_ARRAY).or(TO_OBJECT)//
		.derive(TO).to(to)//
		.derive(TO_NULL).to(toNull) //
		.derive(TO_OBJECT).to(toObject).and(OBJECT) //
		.derive(TO_ARRAY).to(toArray).and(ARRAY) //
		.derive(ARRAY).to(startArray).and(ELEMENTS).and(endArray) //
		.derive(ELEMENTS).to(LAST_ELEMENT).or(NEXT_ELEMENT) //
		.derive(LAST_ELEMENT).to(ELEMENT_TYPE) //
		.derive(NEXT_ELEMENT).to(ELEMENT_TYPE).and(ELEMENTS) //
		.derive(ELEMENT_TYPE).to(ELEMENT).or(ARRAY_ELEMENT).or(OBJECT_ELEMENT) //
		.derive(ELEMENT).to(add) //
		.derive(OBJECT_ELEMENT).to(addObject).and(OBJECT) //
		.derive(ARRAY_ELEMENT).to(addArray).and(ARRAY) //
		.finish();

	System.out.println(b);
    }

    public static void main(final String[] args) {
	buildBNF();
	expressionBuilder();
    }
}
