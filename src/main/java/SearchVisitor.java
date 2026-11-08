package edu.example.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class SearchVisitor implements Visitor {

    private final Predicate<String> filenameCriterion;
    private final long minimumSize;
    private final List<String> matches = new ArrayList<>();
    private String currentPath = "";

    public SearchVisitor(Predicate<String> filenameCriterion, long minimumSize) {
        this.filenameCriterion = Objects.requireNonNull(filenameCriterion, "filenameCriterion");
        if (minimumSize < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative");
        }
        this.minimumSize = minimumSize;
    }

    @Override
    public void beforeVisit(String relativePath, int depth) {
        currentPath = relativePath;
    }

    @Override
    public void visit(FileNode file) {
        if (isMatch(file)) {
            matches.add(reportedPath(file));
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
    }

    public List<String> matches() {
        return matches.stream().sorted().toList();
    }

    @Override
    public void reset() {
        matches.clear();
        currentPath = "";
    }

    private boolean isMatch(FileNode file) {
        return file.size() >= minimumSize && filenameCriterion.test(file.name());
    }

    private String reportedPath(FileNode file) {
        return currentPath.isEmpty() ? file.name() : currentPath;
    }
}
