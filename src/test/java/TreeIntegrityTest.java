package edu.example.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class TreeIntegrityTest {

    @Test
    void negativeFileSizeIsRejectedAndZeroIsAllowed() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad", -1));
        assertEquals(0, new FileNode("empty", 0).size());
    }

    @Test
    void invalidNamesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode(null, 1));
        assertThrows(IllegalArgumentException.class, () -> new FileNode(" ", 1));
        assertThrows(IllegalArgumentException.class, () -> new DirectoryNode("a/b"));
        assertThrows(IllegalArgumentException.class, () -> new DirectoryNode(".."));
    }

    @Test
    void duplicateSiblingNameIsRejectedEvenAcrossElementTypes() {
        DirectoryNode dir = new DirectoryNode("dir");
        dir.add(new FileNode("same", 1));
        assertThrows(IllegalArgumentException.class, () -> dir.add(new FileNode("same", 2)));
        assertThrows(IllegalArgumentException.class, () -> dir.add(new DirectoryNode("same")));
        assertEquals(1, dir.children().size());
    }

    @Test
    void sameNameInDifferentDirectoriesIsAllowed() {
        DirectoryNode a = new DirectoryNode("a");
        DirectoryNode b = new DirectoryNode("b");
        a.add(new FileNode("same", 1));
        b.add(new FileNode("same", 1));
        assertEquals(1, a.children().size());
        assertEquals(1, b.children().size());
    }

    @Test
    void elementWithAParentCannotBeAddedElsewhere() {
        DirectoryNode a = new DirectoryNode("a");
        DirectoryNode b = new DirectoryNode("b");
        FileNode shared = new FileNode("shared", 1);
        DirectoryNode movable = new DirectoryNode("movable");
        a.add(shared);
        a.add(movable);

        assertThrows(IllegalArgumentException.class, () -> b.add(shared));
        assertThrows(IllegalArgumentException.class, () -> b.add(movable));
        assertThrows(IllegalArgumentException.class, () -> a.add(shared));
        assertEquals(0, b.children().size());
    }

    @Test
    void directoryCannotContainItself() {
        DirectoryNode dir = new DirectoryNode("dir");
        assertThrows(IllegalArgumentException.class, () -> dir.add(dir));
        assertEquals(0, dir.children().size());
    }

    @Test
    void indirectCyclesAreRejected() {
        DirectoryNode a = new DirectoryNode("a");
        DirectoryNode b = new DirectoryNode("b");
        DirectoryNode c = new DirectoryNode("c");
        a.add(b);
        b.add(c);

        assertThrows(IllegalArgumentException.class, () -> c.add(a));
        assertThrows(IllegalArgumentException.class, () -> b.add(a));
        assertEquals(0, c.children().size());
    }

    @Test
    void rejectedAddLeavesChildFreeToBeAddedElsewhere() {
        DirectoryNode a = new DirectoryNode("a");
        DirectoryNode b = new DirectoryNode("b");
        a.add(new FileNode("f", 1));
        FileNode duplicate = new FileNode("f", 2);

        assertThrows(IllegalArgumentException.class, () -> a.add(duplicate));
        b.add(duplicate);

        assertEquals(List.of(duplicate), b.children());
    }

    @Test
    void nullChildIsRejected() {
        assertThrows(NullPointerException.class, () -> new DirectoryNode("d").add(null));
    }

    @Test
    void deepValidNestingIsSupported() {
        DirectoryNode root = new DirectoryNode("level0");
        DirectoryNode current = root;
        for (int level = 1; level <= 100; level++) {
            DirectoryNode next = new DirectoryNode("level" + level);
            current.add(next);
            current = next;
        }
        FileNode leaf = new FileNode("leaf.txt", 7);
        current.add(leaf);

        Element cursor = root;
        int steps = 0;
        while (!cursor.children().isEmpty()) {
            assertEquals(1, cursor.children().size());
            cursor = cursor.children().get(0);
            steps++;
        }
        assertEquals(101, steps);
        assertSame(leaf, cursor);
    }

    @Test
    void childrenSnapshotCannotModifyTheDirectory() {
        DirectoryNode dir = new DirectoryNode("d");
        dir.add(new FileNode("x", 1));
        List<Element> snapshot = dir.children();

        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        dir.add(new FileNode("y", 1));
        assertEquals(1, snapshot.size());
        assertEquals(2, dir.children().size());
    }
}
