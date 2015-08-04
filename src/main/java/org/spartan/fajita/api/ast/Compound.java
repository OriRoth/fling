package org.spartan.fajita.api.ast;

import java.util.ArrayList;

public abstract class Compound {

	public Object[] params;
	
	public final ArrayList<Compound> children;
	public final String name;
	private Compound parent;

	public Compound(final Compound parent) {
		this(parent,new Object[]{});
	}

	public Compound(final Compound parent,final Object... params) {
		setParent(parent);
		name = getName();
		children = getChildren();
		this.params = params;
	}
	
	public abstract ArrayList<Compound> getChildren();
	public abstract String getName();
	
	@Override
	public String toString() {
		return name.toString() +" : " + this.getClass().getSimpleName();
	}
	
	public Compound getRoot() {
		Compound current = this;
		for(;current.getParent()!=null;current = current.getParent());
		return current;
	}

	public Compound getParent() {
		return parent;
	}

	protected void setParent(final Compound parent) {
		this.parent = parent;
	}
}