package edu.example.visitor;

public abstract class Node implements Element {

    private final String name;
    private DirectoryNode parent;

    Node(String name) {
        this.name = Names.requireValid(name);
    }

    @Override
    public final String name() {
        return name;
    }

    boolean hasParent() {
        return parent != null;
    }

    DirectoryNode parent() {
        return parent;
    }

    void attachTo(DirectoryNode newParent) {
        this.parent = newParent;
    }
}
