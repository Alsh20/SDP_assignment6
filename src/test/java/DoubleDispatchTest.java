package edu.example.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;

class DoubleDispatchTest {

    private static final class RecordingVisitor implements Visitor {
        final List<String> calls = new ArrayList<>();

        @Override
        public void visit(FileNode file) {
            calls.add("file:" + file.name());
        }

        @Override
        public void visit(DirectoryNode directory) {
            calls.add("dir:" + directory.name());
        }
    }

    @Test
    void fileAcceptsIntoFileOverload() {
        RecordingVisitor visitor = new RecordingVisitor();
        new FileNode("x.txt", 1).accept(visitor);
        assertEquals(List.of("file:x.txt"), visitor.calls);
    }

    @Test
    void directoryAcceptsIntoDirectoryOverload() {
        RecordingVisitor visitor = new RecordingVisitor();
        new DirectoryNode("d").accept(visitor);
        assertEquals(List.of("dir:d"), visitor.calls);
    }

    @Test
    void walkerVisitsParentBeforeChildrenInInsertionOrder() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode inner = new DirectoryNode("inner");
        root.add(inner);
        inner.add(new FileNode("deep.txt", 1));
        root.add(new FileNode("top.txt", 2));

        RecordingVisitor visitor = new RecordingVisitor();
        new FileSystemWalker().walk(root, visitor);

        assertEquals(List.of("dir:root", "dir:inner", "file:deep.txt", "file:top.txt"), visitor.calls);
    }

    @Test
    void walkerVisitsEveryReachableNodeExactlyOnce() {
        RecordingVisitor visitor = new RecordingVisitor();
        new FileSystemWalker().walk(TestTrees.project(), visitor);

        assertEquals(12, visitor.calls.size());
        assertEquals(12, new HashSet<>(visitor.calls).size());
    }
}
