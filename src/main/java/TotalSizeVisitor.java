package edu.example.visitor;

public final class TotalSizeVisitor implements Visitor {

    private long totalBytes;

    @Override
    public void visit(FileNode file) {
        totalBytes = Math.addExact(totalBytes, file.size());
    }

    @Override
    public void visit(DirectoryNode directory) {
    }

    public long totalBytes() {
        return totalBytes;
    }

    @Override
    public void reset() {
        totalBytes = 0;
    }
}
