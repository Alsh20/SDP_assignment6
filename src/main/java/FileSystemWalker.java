package edu.example.visitor;

import java.util.Objects;

public final class FileSystemWalker {

    public void walk(Element root, Visitor visitor) {
        Objects.requireNonNull(root, "root");
        Objects.requireNonNull(visitor, "visitor");
        visitor.reset();
        traverse(root, visitor, "", 0);
    }

    private void traverse(Element element, Visitor visitor, String path, int depth) {
        visitor.beforeVisit(path, depth);
        element.accept(visitor);
        for (Element child : element.children()) {
            traverse(child, visitor, childPath(path, child.name()), depth + 1);
        }
    }

    private static String childPath(String parentPath, String childName) {
        return parentPath.isEmpty() ? childName : parentPath + "/" + childName;
    }
}
