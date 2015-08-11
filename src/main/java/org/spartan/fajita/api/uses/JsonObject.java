package org.spartan.fajita.api.uses;

import static org.spartan.fajita.api.uses.JsonObject.NT.*;
import static org.spartan.fajita.api.uses.JsonObject.Term.*;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class JsonObject {
    public static void expressionBuilder() {

	// showASTs();
    }

    public static enum Term implements Terminal {
	startObject, endObject, name(String.class), //
	toInt(int.class), toBool(boolean.class), toDouble(double.class), //
	toString(String.class), toNull, toObject, toArray, //
	startArray, endArray, //
	addInt(int.class), addBool(boolean.class), addDouble(double.class), //
	addString(String.class), addObject, addArray;

	private final Class<?> type;

	private Term(final Class<?> type) {
	    this.type = type;
	}

	private Term() {
	    type = Void.class;
	}

	@Override
	public Class<?> type() {
	    return type;
	}

    }

    public static enum NT implements NonTerminal {
	START, OBJECT, NEXT, NEXT_ADD, LAST_ADD, ADD, //
	TO_TYPE, TO_INT, TO_BOOL, TO_DOUBLE, //
	TO_STRING, TO_NULL, TO_OBJECT, TO_ARRAY, //
	ARRAY, ELEMENTS, LAST_ELEMENT, NEXT_ELEMENT, //
	ELEMENT, INT_ELEMENT, BOOL_ELEMENT, DOUBLE_ELEMENT, //
	STRING_ELEMENT, OBJECT_ELEMENT, ARRAY_ELEMENT;
    }

    /**
     * We would like to have overloaded functions, e.g.
     * "to()" </br>
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
	BNF<Term, NT> b = new BNFBuilder<>(Term.class, NT.class) //
		.setApiName("Json Object Builder") //
		.derive(START).to(OBJECT) //
		.derive(OBJECT).to(startObject).and(NEXT).and(endObject) //
		.derive(NEXT).toOneOf(NEXT_ADD).or(LAST_ADD)//
		.derive(NEXT_ADD).to(ADD).and(NEXT) //
		.derive(LAST_ADD).to(ADD) //
		.derive(ADD).to(name).and(TO_TYPE)//
		.derive(TO_TYPE).toOneOf(TO_INT).or(TO_BOOL).or(TO_DOUBLE).or(TO_STRING).or(TO_NULL).or(TO_ARRAY)
		.or(TO_OBJECT)//
		.derive(TO_INT).to(toInt)//
		.derive(TO_BOOL).to(toBool) //
		.derive(TO_DOUBLE).to(toDouble) //
		.derive(TO_STRING).to(toString) //
		.derive(TO_NULL).to(toNull) //
		.derive(TO_OBJECT).to(toObject).and(OBJECT) //
		.derive(TO_ARRAY).to(toArray).and(ARRAY) //
		.derive(ARRAY).to(startArray).and(ELEMENTS).and(endArray) //
		.derive(ELEMENTS).toOneOf(LAST_ELEMENT).or(NEXT_ELEMENT) //
		.derive(LAST_ELEMENT).to(ELEMENT) //
		.derive(NEXT_ELEMENT).to(ELEMENT).and(ELEMENTS) //
		.derive(ELEMENT).toOneOf(INT_ELEMENT).or(BOOL_ELEMENT).or(STRING_ELEMENT).or(DOUBLE_ELEMENT)
		.or(ARRAY_ELEMENT).or(OBJECT_ELEMENT) //
		.derive(INT_ELEMENT).to(addInt) //
		.derive(BOOL_ELEMENT).to(addBool) //
		.derive(STRING_ELEMENT).to(addString) //
		.derive(DOUBLE_ELEMENT).to(addDouble) //
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
