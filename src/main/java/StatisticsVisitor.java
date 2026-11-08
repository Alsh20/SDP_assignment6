package edu.example.visitor;

public final class StatisticsVisitor implements Visitor {

    private long fileCount;
    private long directoryCount;
    private int maxDepth;
    private int currentDepth;

    @Override
    public void beforeVisit(String relativePath, int depth) {
        currentDepth = depth;
    }

    @Override
    public void visit(FileNode file) {
        fileCount++;
        trackDepth();
    }

    @Override
    public void visit(DirectoryNode directory) {
        directoryCount++;
        trackDepth();
    }

    public long fileCount() {
        return fileCount;
    }

    public long directoryCount() {
        return directoryCount;
    }

    public int maxDepth() {
        return maxDepth;
    }

    @Override
    public void reset() {
        fileCount = 0;
        directoryCount = 0;
        maxDepth = 0;
        currentDepth = 0;
    }

    private void trackDepth() {
        maxDepth = Math.max(maxDepth, currentDepth);
    }
}
