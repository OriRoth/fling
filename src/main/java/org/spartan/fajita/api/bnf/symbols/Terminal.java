package org.spartan.fajita.api.bnf.symbols;

public interface Terminal extends Symbol {
    public default Class<?> type(){
	return Void.class;
    }

    @Override
    public default String toString2() {
	if (type() == Void.class)
	    return name() + "()";
	else
	    return name() + "(" + type().getSimpleName() + ")";
    }
}