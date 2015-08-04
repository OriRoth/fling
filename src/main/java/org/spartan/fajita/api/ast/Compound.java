package org.spartan.fajita.api.ast;

import java.util.ArrayList;

public abstract class Compound {

	public Object[] params;
	
	public final ArrayList<Compound> children;
	public final String name;
	private Compound parent;

	public Compound(final Compound parent, final String name) {
		setParent(parent);
		this.name = name;
		children = getChildren();
		params = new Object[]{};
	}

	public Compound(final Compound parent, final String name,final Object... params) {
		setParent(parent);
		this.name = name;
		this.params = params;
		children = getChildren();
	}
	
	public abstract ArrayList<Compound> getChildren();

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