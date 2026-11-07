package edu.example.visitor;

public interface Visitor {

    void visit(FileNode file);

    void visit(DirectoryNode directory);

    default void beforeVisit(String relativePath, int depth) {
    }

    default void reset() {
    }
}
