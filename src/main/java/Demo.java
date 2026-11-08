package edu.example.visitor;

public final class Demo {

    private Demo() {
    }

    public static void main(String[] args) {
        DirectoryNode docs = new DirectoryNode("docs");
        DirectoryNode workspace = buildWorkspace(docs);
        FileSystemWalker walker = new FileSystemWalker();

        TotalSizeVisitor size = new TotalSizeVisitor();
        walker.walk(workspace, size);
        System.out.println("Total bytes: " + size.totalBytes());

        SearchVisitor search = new SearchVisitor(name -> name.endsWith(".txt"), 50);
        walker.walk(workspace, search);
        System.out.println("Search (*.txt, size >= 50): " + search.matches());

        StatisticsVisitor stats = new StatisticsVisitor();
        walker.walk(workspace, stats);
        System.out.println("Statistics: files=" + stats.fileCount()
                + ", directories=" + stats.directoryCount()
                + ", maxDepth=" + stats.maxDepth());

        walker.walk(workspace, size);
        System.out.println("Total bytes, same visitor reused: " + size.totalBytes());

        TotalSizeVisitor docsSize = new TotalSizeVisitor();
        walker.walk(docs, docsSize);
        System.out.println("Total bytes of docs subtree: " + docsSize.totalBytes());

        printRejectedChanges(workspace);
    }

    private static DirectoryNode buildWorkspace(DirectoryNode docs) {
        DirectoryNode workspace = new DirectoryNode("workspace");
        DirectoryNode src = new DirectoryNode("src");
        DirectoryNode archive = new DirectoryNode("archive");

        workspace.add(new FileNode("notes.txt", 80));
        workspace.add(docs);
        docs.add(new FileNode("design.md", 240));
        docs.add(new FileNode("todo.txt", 20));
        workspace.add(src);
        src.add(new FileNode("App.java", 400));
        src.add(archive);
        archive.add(new FileNode("old.txt", 90));
        src.add(new DirectoryNode("empty"));
        return workspace;
    }

    private static void printRejectedChanges(DirectoryNode workspace) {
        tryAndReport(() -> workspace.add(new FileNode("notes.txt", 1)));
        tryAndReport(() -> new FileNode("broken.bin", -5));
        tryAndReport(() -> workspace.add(workspace));
    }

    private static void tryAndReport(Runnable invalidChange) {
        try {
            invalidChange.run();
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
}
