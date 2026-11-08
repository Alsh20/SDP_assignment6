package edu.example.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

class SearchVisitorTest {

    private final FileSystemWalker walker = new FileSystemWalker();

    private List<String> search(Element root, Predicate<String> criterion, long minimumSize) {
        SearchVisitor visitor = new SearchVisitor(criterion, minimumSize);
        walker.walk(root, visitor);
        return visitor.matches();
    }

    @Test
    void nameAndSizeConstraintsAreCombined() {
        assertEquals(List.of("docs/guide.txt", "src/util/notes.txt"),
                search(TestTrees.project(), name -> name.endsWith(".txt"), 50));
    }

    @Test
    void thresholdIsInclusive() {
        List<String> atBoundary = search(TestTrees.project(), name -> name.equals("notes.txt"), 50);
        List<String> aboveBoundary = search(TestTrees.project(), name -> name.equals("notes.txt"), 51);
        assertEquals(List.of("src/util/notes.txt"), atBoundary);
        assertTrue(aboveBoundary.isEmpty());
    }

    @Test
    void sizeConstraintAloneFiltersSmallFiles() {
        assertEquals(List.of("docs/guide.txt", "src/Main.java", "src/util/Helper.java"),
                search(TestTrees.project(), name -> true, 150));
    }

    @Test
    void nameConstraintAloneIgnoresSize() {
        assertEquals(List.of("docs/draft.txt", "docs/guide.txt", "src/util/notes.txt"),
                search(TestTrees.project(), name -> name.endsWith(".txt"), 0));
    }

    @Test
    void pathsAreLexicographicallySortedNotInInsertionOrder() {
        assertEquals(List.of("README.md", "build.log", "docs/draft.txt", "docs/guide.txt",
                        "src/Main.java", "src/util/Helper.java", "src/util/notes.txt"),
                search(TestTrees.project(), name -> true, 0));
    }

    @Test
    void pathsAreRelativeToTheChosenRoot() {
        assertEquals(List.of("Main.java", "util/Helper.java"),
                search(TestTrees.src(), name -> name.endsWith(".java"), 0));
    }

    @Test
    void fileAsRootIsReportedByItsName() {
        assertEquals(List.of("solo.txt"), search(new FileNode("solo.txt", 10), name -> true, 10));
    }

    @Test
    void noMatchAndEmptyStructuresGiveEmptyResult() {
        assertTrue(search(TestTrees.project(), name -> false, 0).isEmpty());
        assertTrue(search(TestTrees.project(), name -> true, 1_000_000).isEmpty());
        assertTrue(search(new DirectoryNode("empty"), name -> true, 0).isEmpty());
    }

    @Test
    void sameVisitorWalkedTwiceDoesNotDuplicateResults() {
        SearchVisitor visitor = new SearchVisitor(name -> name.endsWith(".txt"), 50);
        DirectoryNode project = TestTrees.project();
        walker.walk(project, visitor);
        walker.walk(project, visitor);
        assertEquals(List.of("docs/guide.txt", "src/util/notes.txt"), visitor.matches());
    }

    @Test
    void returnedListIsUnmodifiable() {
        SearchVisitor visitor = new SearchVisitor(name -> true, 0);
        walker.walk(TestTrees.project(), visitor);
        List<String> result = visitor.matches();
        assertThrows(UnsupportedOperationException.class, () -> result.add("x"));
    }

    @Test
    void invalidConfigurationIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new SearchVisitor(name -> true, -1));
        assertThrows(NullPointerException.class, () -> new SearchVisitor(null, 0));
    }
}
