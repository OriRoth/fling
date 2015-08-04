package org.spartan.fajita.api.examples;

import java.util.ArrayList;

import org.spartan.fajita.api.ast.Atomic;
import org.spartan.fajita.api.ast.Compound;
import org.spartan.fajita.api.ast.InheritedNonterminal;

public class BinaryExpressions {

	// 1)for each NT that has an inheritence rule we need an empty interface
	public interface Expression {
	}
	// 2)and and empty abstract class for compound
	public static abstract class CompoundExpression extends Compound implements Expression {

		CompoundExpression(final Expr parent, final String name) {
			this(parent, name,new Object[]{});
		}

		CompoundExpression(final Expr parent, final String name, final Object... params) {
			super(parent, name, params);
			parent.deriveTo(this);
		}
		
		@Override
		public Expr getParent() {
			return (Expr) super.getParent();
		}
	}

	// 3) and a node for that NT . it will always be the parent of the derived nodes
	public static class Expr extends InheritedNonterminal implements Expression {

		public Expr(final Compound parent) {
			super(parent, "EXPRESSION");
		}

		public Expr(final Compound parent, final String name) {
			super(parent, name);
		}

		public Expr() {
			super(null, "Expr");
		}

		// adding methods for all terminals in FIRST(S) for "flat" compilation

		public Not not() {
			return new Not(this);
		}

		public Literal bool(final boolean b) {
			return new Literal(this, b);
		}

		// this is part of the bottom up syntax
		public Compound or(final CompoundExpression e1, final CompoundExpression e2) {
			new Or(this, e1, e2);
			return this;
		}

		public Compound and(final CompoundExpression e1, final CompoundExpression e2) {
			new And(this, e1, e2);
			return this;
		}
	}

	public static class Or extends CompoundExpression{

		// for top-down
		public Or(final Expr parent) {
			super(parent, "OR");
		}

		// for bottom-up
		public Or(final Expr parent, final CompoundExpression e1, final CompoundExpression e2) {
			super(parent, "OR");
			((Expr) children.get(0)).deriveTo(e1);
			((Expr) children.get(2)).deriveTo(e2);
		}

		@Override
		public ArrayList<Compound> getChildren() {
			ArrayList<Compound> $ = new ArrayList<>();
			$.add(new Expr(this));
			$.add(new Atomic(this, "or"));
			$.add(new Expr(this));
			return $;
		}
	}

	public static class And extends CompoundExpression {

		// for top down
		public And(final Expr parent) {
			super(parent, "AND");
			parent.deriveTo(this);
		}

		// for bottom up
		public And(final Expr parent, final CompoundExpression e1, final CompoundExpression e2) {
			super(parent, "AND");
			((Expr) children.get(0)).deriveTo(e1);
			((Expr) children.get(2)).deriveTo(e2);
		}

		@Override
		public ArrayList<Compound> getChildren() {
			ArrayList<Compound> $ = new ArrayList<>();
			$.add(new Expr(this));
			$.add(new Atomic(this, "and"));
			$.add(new Expr(this));
			return $;
		}
	}

	// whoever inherits from Expr need to get the parent Expr in
	// constructor and call deriveTo(this)

	public static class Not extends CompoundExpression {
		//for top down
		public Not(final Expr parent) {
			super(parent, "NOT");
			parent.deriveTo(this);
		}

		// for bottom up
		public Not(final Expr parent,final CompoundExpression e) {
			super(parent, "NOT");
			((Expr) children.get(1)).deriveTo(e);
		}

		@Override
		public ArrayList<Compound> getChildren() {
			ArrayList<Compound> $ = new ArrayList<>();
			$.add(new Atomic(this, "not", params));
			$.add(new Expr(this));
			return $;
		}

		public Literal bool(final boolean b) {
			return new Literal((Expr) children.get(1), b);
		}
	}

	public static class Literal extends CompoundExpression {

		public Literal(final Expr parent, final boolean b) {
			super(parent, "LITERAL");
			children.get(0).params = new Object[] { b };
			parent.deriveTo(this);
		}

		@Override
		public ArrayList<Compound> getChildren() {
			ArrayList<Compound> $ = new ArrayList<>();
			$.add(new Atomic(this, "boolean"));
			return $;
		}

		// adding methods for all terminals in FIRST(S) for "flat" compilation

		public Expr and() {
			if (getParent() == null)
				setParent(new Expr());
			// Expr->AND and not Expr->LITERAL. fix the parent Expr
			And and = new And(getParent());
			((Expr) and.children.get(0)).deriveTo(this);
			return (Expr) and.children.get(2);
		}

		public Expr or() {
			if (getParent() == null)
				setParent(new Expr());
			// Expr->OR and not Expr->LITERAL. fix the parent Expr
			Or or = new Or(getParent());
			((Expr) or.children.get(0)).deriveTo(this);
			return (Expr) or.children.get(2);
		}
	}

	public static Literal bool(final boolean b) {
		return new Literal(new Expr(), b);
	}

	public static Or or(final CompoundExpression e1, final CompoundExpression e2) {
		return new Or(new Expr(), e1, e2);
	}

	public static And and(final CompoundExpression e1, final CompoundExpression e2) {
		return new And(new Expr(), e1, e2);
	}

	public static Not not(final CompoundExpression e) {
		return new Not(new Expr(),e);
	}
}
