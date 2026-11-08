package edu.example.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StatisticsVisitorTest {

    private final FileSystemWalker walker = new FileSystemWalker();

    private StatisticsVisitor statisticsOf(Element root) {
        StatisticsVisitor visitor = new StatisticsVisitor();
        walker.walk(root, visitor);
        return visitor;
    }

    @Test
    void countsFilesDirectoriesAndDepthAcrossNesting() {
        StatisticsVisitor stats = statisticsOf(TestTrees.project());
        assertEquals(7, stats.fileCount());
        assertEquals(5, stats.directoryCount());
        assertEquals(3, stats.maxDepth());
    }

    @Test
    void depthIsMeasuredFromTheChosenRoot() {
        StatisticsVisitor stats = statisticsOf(TestTrees.src());
        assertEquals(3, stats.fileCount());
        assertEquals(3, stats.directoryCount());
        assertEquals(2, stats.maxDepth());
    }

    @Test
    void emptyRootDirectoryIsCountedAtDepthZero() {
        StatisticsVisitor stats = statisticsOf(new DirectoryNode("empty"));
        assertEquals(0, stats.fileCount());
        assertEquals(1, stats.directoryCount());
        assertEquals(0, stats.maxDepth());
    }

    @Test
    void emptyNestedDirectoryStillDeterminesDepth() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode a = new DirectoryNode("a");
        root.add(a);
        a.add(new DirectoryNode("b"));
        StatisticsVisitor stats = statisticsOf(root);
        assertEquals(0, stats.fileCount());
        assertEquals(3, stats.directoryCount());
        assertEquals(2, stats.maxDepth());
    }

    @Test
    void fileAsRootHasDepthZeroAndNoDirectories() {
        StatisticsVisitor stats = statisticsOf(new FileNode("solo.txt", 1));
        assertEquals(1, stats.fileCount());
        assertEquals(0, stats.directoryCount());
        assertEquals(0, stats.maxDepth());
    }

    @Test
    void sameVisitorWalkedTwiceDoesNotMultiplyCounts() {
        StatisticsVisitor stats = new StatisticsVisitor();
        DirectoryNode project = TestTrees.project();
        walker.walk(project, stats);
        walker.walk(project, stats);
        assertEquals(7, stats.fileCount());
        assertEquals(5, stats.directoryCount());
        assertEquals(3, stats.maxDepth());
    }

    @Test
    void shallowerSecondWalkDoesNotKeepOldMaximumDepth() {
        StatisticsVisitor stats = new StatisticsVisitor();
        walker.walk(TestTrees.project(), stats);
        walker.walk(TestTrees.docs(), stats);
        assertEquals(1, stats.maxDepth());
    }

    @Test
    void deeplyNestedChainIsFullyCounted() {
        DirectoryNode root = new DirectoryNode("level0");
        DirectoryNode current = root;
        for (int level = 1; level <= 100; level++) {
            DirectoryNode next = new DirectoryNode("level" + level);
            current.add(next);
            current = next;
        }
        current.add(new FileNode("leaf.txt", 7));

        StatisticsVisitor stats = statisticsOf(root);
        assertEquals(101, stats.directoryCount());
        assertEquals(1, stats.fileCount());
        assertEquals(101, stats.maxDepth());
    }
}
