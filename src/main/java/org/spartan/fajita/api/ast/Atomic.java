package org.spartan.fajita.api.ast;

import java.util.ArrayList;

public abstract class Atomic extends Compound {

	public Atomic(final Compound parent) {
		super(parent);
	}

	@Override
	public ArrayList<Compound> getChildren() {
		return new ArrayList<>();
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}
