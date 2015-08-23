package org.spartan.fajita.api.parser.stack;

public interface IStack<Head, Tail extends IStack<?, ?>> {

    public Head peek();
}