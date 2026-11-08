package edu.example.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TotalSizeVisitorTest {

    private final FileSystemWalker walker = new FileSystemWalker();

    private long totalOf(Element root) {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        walker.walk(root, visitor);
        return visitor.totalBytes();
    }

    @Test
    void sumsFilesAcrossAllNestingLevels() {
        assertEquals(1169, totalOf(TestTrees.project()));
    }

    @Test
    void subtreeTotalsOnlyCountThatSubtree() {
        assertEquals(500, totalOf(TestTrees.src()));
        assertEquals(549, totalOf(TestTrees.docs()));
    }

    @Test
    void emptyDirectoryAndDirectoriesOnlyTreeTotalZero() {
        DirectoryNode root = new DirectoryNode("root");
        assertEquals(0, totalOf(root));
        root.add(new DirectoryNode("a"));
        root.add(new DirectoryNode("b"));
        assertEquals(0, totalOf(root));
    }

    @Test
    void fileAsRootCountsOnce() {
        assertEquals(10, totalOf(new FileNode("solo.txt", 10)));
    }

    @Test
    void sameVisitorWalkedTwiceDoesNotAccumulate() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        DirectoryNode project = TestTrees.project();
        walker.walk(project, visitor);
        walker.walk(project, visitor);
        assertEquals(1169, visitor.totalBytes());
    }

    @Test
    void secondWalkOfAnotherTreeReflectsOnlyThatTree() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        walker.walk(TestTrees.project(), visitor);
        walker.walk(TestTrees.docs(), visitor);
        assertEquals(549, visitor.totalBytes());
    }
}
