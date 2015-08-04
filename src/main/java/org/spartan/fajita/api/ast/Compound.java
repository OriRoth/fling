package org.spartan.fajita.api.ast;

import java.util.ArrayList;

public abstract class Compound {

	public Object[] params;
	
	public final ArrayList<Compound> children;
	public final String name;
	public Compound parent;

	public Compound(final Compound parent, final String name) {
		this.parent = parent;
		this.name = name;
		children = getChildren();
		params = new Object[]{};
	}

	Compound(final Compound parent, final String name,final Object... params) {
		this.parent = parent;
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
		for(;current.parent!=null;current = current.parent);
		return current;
	}
}