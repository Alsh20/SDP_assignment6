package edu.example.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DirectoryNode extends Node {

    private final List<Node> children = new ArrayList<>();

    public DirectoryNode(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor visitor) {
        Objects.requireNonNull(visitor, "visitor").visit(this);
    }

    @Override
    public List<Element> children() {
        return List.copyOf(children);
    }

    public void add(Node child) {
        Objects.requireNonNull(child, "child");
        requireUniqueSiblingName(child);
        requireNoParent(child);
        requireNoCycle(child);
        children.add(child);
        child.attachTo(this);
    }

    private void requireUniqueSiblingName(Node child) {
        boolean taken = children.stream().anyMatch(existing -> existing.name().equals(child.name()));
        if (taken) {
            throw new IllegalArgumentException("Sibling name already exists: " + child.name());
        }
    }

    private static void requireNoParent(Node child) {
        if (child.hasParent()) {
            throw new IllegalArgumentException("Element already has a parent: " + child.name());
        }
    }

    private void requireNoCycle(Node child) {
        for (DirectoryNode ancestor = this; ancestor != null; ancestor = ancestor.parent()) {
            if (ancestor == child) {
                throw new IllegalArgumentException("Adding element would create a cycle: " + child.name());
            }
        }
    }
}
