package edu.example.visitor;

import java.util.List;

public interface Element {

    String name();

    void accept(Visitor visitor);

    default List<Element> children() {
        return List.of();
    }
}
