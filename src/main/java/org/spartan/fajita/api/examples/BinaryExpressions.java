package org.spartan.fajita.api.examples;

import java.util.ArrayList;

import org.spartan.fajita.api.ast.Atomic;
import org.spartan.fajita.api.ast.Compound;
import org.spartan.fajita.api.ast.InheritedNonterminal;

public class BinaryExpressions {

	// this is also Start NT
	public static class Expression extends InheritedNonterminal {

		public Expression(final Compound parent) {
			super(parent, "EXPRESSION");
		}

		public Expression(final Compound parent, final String name) {
			super(parent, name);
		}

		public Expression() {
			super(null, "Expression");
		}

		// adding methods for all terminals in FIRST(S) for "flat" compilation

		public Not not() {
			return new Not(this);
		}

		public Literal bool(final boolean b) {
			return new Literal(this, b);
		}

		// this is part of the bottom up syntax
		public Compound or(final InheritedNonterminal e1, final InheritedNonterminal e2) {
			Or or = new Or(e1, e2);
			deriveTo(or);
			return this;
		}

		public Compound and(final InheritedNonterminal e1, final InheritedNonterminal e2) {
			And and = new And(e1, e2);
			deriveTo(and);
			return this;
		}
	}

	public static class Or extends InheritedNonterminal {

		// for top-down
		public Or(final Expression parent) {
			super(parent, "OR");
			parent.deriveTo(this);
		}

		// for bottom-up
		public Or(final InheritedNonterminal e1, final InheritedNonterminal e2) {
			super(null, "OR");
			((Expression) children.get(0)).deriveTo(e1);
			((Expression) children.get(2)).deriveTo(e2);
		}

		@Override
		public ArrayList<Compound> getChildren() {
			ArrayList<Compound> $ = new ArrayList<>();
			$.add(new Expression(this));
			$.add(new Atomic(this, "or"));
			$.add(new Expression(this));
			return $;
		}
	}

	public static class And extends InheritedNonterminal {

		// for top down
		public And(final Expression parent) {
			super(parent, "AND");
			parent.deriveTo(this);
		}

		// for bottom up
		public And(final InheritedNonterminal e1, final InheritedNonterminal e2) {
			super(null, "AND");
			((Expression) children.get(0)).deriveTo(e1);
			((Expression) children.get(2)).deriveTo(e2);
		}

		@Override
		public ArrayList<Compound> getChildren() {
			ArrayList<Compound> $ = new ArrayList<>();
			$.add(new Expression(this));
			$.add(new Atomic(this, "and"));
			$.add(new Expression(this));
			return $;
		}
	}

	// whoever inherits from Expression need to get the parent Expression in
	// constructor and call deriveTo(this)

	public static class Not extends InheritedNonterminal {
		public Not(final Expression parent) {
			super(parent, "NOT");
			parent.deriveTo(this);
		}

		@Override
		public ArrayList<Compound> getChildren() {
			ArrayList<Compound> $ = new ArrayList<>();
			$.add(new Atomic(this, "not", params));
			$.add(new Expression(this));
			return $;
		}

		public Literal bool(final boolean b) {
			return new Literal((Expression) children.get(1), b);
		}
	}

	public static class Literal extends InheritedNonterminal {

		private boolean value;

		// for top-down
		public Literal(final Expression parent, final boolean b) {
			super(parent, "LITERAL");
			value = b;
			children.get(0).params = new Object[]{b};
			parent.deriveTo(this);
		}

		// for bottom-up
		public Literal(final boolean b) {
			super(null, "LITERAL");
			children.get(0).params = new Object[]{b};
		}

		@Override
		public ArrayList<Compound> getChildren() {
			ArrayList<Compound> $ = new ArrayList<>();
			$.add(new Atomic(this, "boolean"));
			return $;
		}

		// adding methods for all terminals in FIRST(S) for "flat" compilation

		public Expression and() {
			// now we know Expression -> AND
			// and not Expression -> LITERAL
			// fix the parent expression
			parent.children.clear();
			And and = new And((Expression) parent);
			new Literal((Expression) and.children.get(0), value);
			// now the tree is fixed
			return (Expression) and.children.get(2);
		}

		public Expression or() {
			// now we know Expression -> OR
			// and not Expression -> LITERAL
			// fix the parent expression
			parent.children.clear();
			Or or = new Or((Expression) parent);
			new Literal((Expression) or.children.get(0), value);
			// now the tree is fixed
			return (Expression) or.children.get(2);
		}
	}

	public static Literal bool(final boolean b) {
		return new Literal(b);
	}

	public static Or or(final InheritedNonterminal e1, final InheritedNonterminal e2) {
		return new Or(e1, e2);
	}

	public static And and(final InheritedNonterminal e1, final InheritedNonterminal e2) {
		return new And(e1, e2);
	}
}
